package com.hui.huiaiagent.advisor;

import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

import java.util.Set;

/**
 * 自定义违规词校验 Advisor
 * 在请求到达大模型之前检查用户输入，包含违规词则直接抛出异常拦截，本次不会调用 AI
 */
public class ForbiddenWordsAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    /**
     * 违规词列表（演示用；实际项目可改为从数据库读取，或接入内容安全审核接口）
     */
    private static final Set<String> FORBIDDEN_WORDS = Set.of("暴力", "色情", "赌博");

    /**
     * 前置校验：不通过直接抛异常，请求不会继续沿着 Advisor 链传给大模型
     */
    private AdvisedRequest check(AdvisedRequest advisedRequest) {
        String userText = advisedRequest.userText();
        Assert.hasText(userText, "user text cannot be null");
        // 只要命中任意一个违规词，就拦截
        boolean hasForbiddenWord = FORBIDDEN_WORDS.stream().anyMatch(userText::contains);
        Assert.isTrue(!hasForbiddenWord, "发送的内容中包含违规词，请重新发送！");
        return advisedRequest;
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        // 先校验，校验通过才放行到下一个 Advisor（最终到达大模型）
        return chain.nextAroundCall(this.check(advisedRequest));
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        return chain.nextAroundStream(this.check(advisedRequest));
    }

    @Override
    public int getOrder() {
        // order 越小越先执行。排在权限校验（Integer.MIN_VALUE + 100）之后、
        // 官方记忆 Advisor（Integer.MIN_VALUE + 1000）之前，保证违规消息不会被先存进聊天记忆
        return Integer.MIN_VALUE + 200;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
