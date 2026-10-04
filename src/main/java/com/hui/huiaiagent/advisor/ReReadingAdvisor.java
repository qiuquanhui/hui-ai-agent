package com.hui.huiaiagent.advisor;


import org.springframework.ai.chat.client.advisor.api.*;   // Advisor 相关接口（AdvisedRequest、两条链等）
import reactor.core.publisher.Flux;                        // 流式响应的"传送带"（StreamAroundAdvisor 需要）

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义 Re2 Advisor（Re-Reading，"再读一遍"）
 * 原理：把用户的问题在提示词里重复两遍，让模型"读题读两遍"，
 * 在数学、逻辑类复杂问题上能明显提升正确率（Re-Read 提示技巧）
 */
public class ReReadingAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    /**
     * 前置处理：改造请求——把"读一遍的问题"变成"读两遍的问题"
     */
    private AdvisedRequest before(AdvisedRequest advisedRequest) {

        // ① 复制一份原有的"用户参数表"。为什么不直接改原表？
        //    请求对象在这条 Advisor 链上是共享的，原地修改会污染后面老师看到的数据
        Map<String, Object> advisedUserParams = new HashMap<>(advisedRequest.userParams());

        // ② 把用户的原话存进参数表，变量名叫 re2_input_query（第 ③ 步的模板要引用它）
        advisedUserParams.put("re2_input_query", advisedRequest.userText());

        // ③ 以原请求为"底稿"，改造出一份新请求
        return AdvisedRequest.from(advisedRequest)
                // 把用户消息替换成"两遍模板"：
                // {re2_input_query} 是占位符——真正发送前，框架会用参数表里的值把它替换掉，
                // 两处占位符填的是同一句话，于是问题被"读了两遍"
                .userText("""
                        {re2_input_query}
                        Read the question again: {re2_input_query}
                        """)
                // ④ 挂上第 ①② 步准备的参数表（re2_input_query 的值就在里面）
                .userParams(advisedUserParams)
                // ⑤ 造出"加料版"新请求返回——原请求没动，链上下一位收到的已是读两遍的版本
                .build();
    }

    // 同步调用（.call()）的门卫流程：先 before 改造，再放行给下一位老师
    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        return chain.nextAroundCall(this.before(advisedRequest));
    }

    // 流式调用（.stream()）的门卫流程：同样的改造、同样的放行（两个接口都要实现，见踩坑）
    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        return chain.nextAroundStream(this.before(advisedRequest));
    }

    // 排队序号：数字越小越先执行。本顾问不拦人也不依赖别人，排 0（较靠后）即可
    @Override
    public int getOrder() {
        return 0;
    }

    // 名字：取类的简单名，方便在日志里认出是它
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}