package com.hui.huiaiagent.advisor;

import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

import java.util.Set;

/**
 * 自定义权限校验 Advisor
 * 在请求到达大模型之前校验调用者身份，无权限则直接抛出异常拦截，本次不会调用 AI
 *
 * 使用方式：调用时通过 Advisor 参数传入用户身份，例如
 * .advisors(spec -> spec.param(AuthorizedAdvisor.AUTH_USER_ID_PARAM, "用户id"))
 * 参数会进入 adviseContext，本 Advisor 从中读取并校验
 */
public class AuthorizedAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    /**
     * 用户身份参数名（调用方传参时使用的 key）
     */
    public static final String AUTH_USER_ID_PARAM = "authUserId";

    /**
     * 封禁用户黑名单（演示用；实际项目可改为查询数据库，或对接真实的登录 / 权限系统）
     */
    private final Set<String> bannedUserIds;

    public AuthorizedAdvisor() {
        this(Set.of());
    }

    public AuthorizedAdvisor(Set<String> bannedUserIds) {
        this.bannedUserIds = bannedUserIds;
    }

    /**
     * 前置校验：不通过直接抛异常，请求不会继续沿着 Advisor 链传给大模型
     */
    private AdvisedRequest check(AdvisedRequest advisedRequest) {
        // 从 adviseContext 中取出调用方传来的用户身份
        Object userId = advisedRequest.adviseContext().get(AUTH_USER_ID_PARAM);
        // 1. 没传用户身份，视为未登录，直接拒绝
        Assert.notNull(userId, "无权调用 AI：未获取到用户身份，请先登录");
        // 2. 用户在封禁黑名单中，直接拒绝
        Assert.isTrue(!bannedUserIds.contains(String.valueOf(userId)), "无权调用 AI：该用户已被封禁");
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
        // order 越小越先执行。官方记忆 Advisor 默认是 Integer.MIN_VALUE + 1000（几乎最先执行），
        // 权限校验必须排在记忆之前（否则未授权的消息会先被存进聊天记忆），所以取更小的值
        return Integer.MIN_VALUE + 100;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
