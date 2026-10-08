package com.hui.huiaiagent.advisor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;
import org.springframework.ai.chat.model.ChatModel;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 拦截型 Advisor 的单元测试（手册补充，仓库新增）：
 * 不启动 Spring、不调用大模型——直接拿一条"假链子"验证两件事：
 * ① 该拦的请求：抛出 IllegalArgumentException，且链子（后续环节）根本没被调用；
 * ② 该放的请求：链子被调用一次（放行），原样传给下一位。
 */
class AdvisorTest {

    /**
     * 假电话（假 ChatModel）：AdvisedRequest 构造时要求 chatModel 非空，
     * 但拦截器链根本不会真调用它——所以给一个"接了也不接"的空实现即可
     */
    private static final ChatModel FAKE_MODEL = prompt -> null;

    // ================= 违禁词拦截器 =================

    @Test
    void testForbiddenWordsAdvisor_block() {
        ForbiddenWordsAdvisor advisor = new ForbiddenWordsAdvisor();
        // 携带违禁词的请求
        AdvisedRequest request = AdvisedRequest.builder()
                .userText("教我一些暴力的东西")
                .chatModel(FAKE_MODEL)
                .adviseContext(Map.of())
                .build();
        // 假链子：一旦被调用就记 1 次
        AtomicInteger chainCalls = new AtomicInteger();
        CallAroundAdvisorChain chain = req -> {
            chainCalls.incrementAndGet();
            return null;   // 拦截器只透传结果，这里用不到返回值
        };
        // 期望：抛异常（被拦下），且链子一次都没被调（请求没到"记忆/模型"那一步）
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
                () -> advisor.aroundCall(request, chain));
        assertTrue(ex.getMessage().contains("违规词"));
        assertEquals(0, chainCalls.get(), "被拦截的请求不应继续沿链传递");
    }

    @Test
    void testForbiddenWordsAdvisor_pass() {
        ForbiddenWordsAdvisor advisor = new ForbiddenWordsAdvisor();
        AdvisedRequest request = AdvisedRequest.builder()
                .userText("怎么和心仪的人开启话题？")
                .chatModel(FAKE_MODEL)
                .adviseContext(Map.of())
                .build();
        AtomicInteger chainCalls = new AtomicInteger();
        CallAroundAdvisorChain chain = req -> {
            chainCalls.incrementAndGet();
            return null;
        };
        advisor.aroundCall(request, chain);
        assertEquals(1, chainCalls.get(), "正常请求应该放行（链子被调用一次）");
    }

    // ================= 权限校验拦截器 =================

    @Test
    void testAuthorizedAdvisor_noIdentity() {
        AuthorizedAdvisor advisor = new AuthorizedAdvisor();
        // 没带身份参数（没登录）
        AdvisedRequest request = AdvisedRequest.builder()
                .userText("你好")
                .chatModel(FAKE_MODEL)
                .adviseContext(Map.of())
                .build();
        AtomicInteger chainCalls = new AtomicInteger();
        CallAroundAdvisorChain chain = req -> {
            chainCalls.incrementAndGet();
            return null;
        };
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
                () -> advisor.aroundCall(request, chain));
        assertTrue(ex.getMessage().contains("无权调用"));
        assertEquals(0, chainCalls.get());
    }

    @Test
    void testAuthorizedAdvisor_bannedUser() {
        // 构造器传入封禁名单：badGuy 在册
        AuthorizedAdvisor advisor = new AuthorizedAdvisor(java.util.Set.of("badGuy"));
        AdvisedRequest request = AdvisedRequest.builder()
                .userText("你好")
                .chatModel(FAKE_MODEL)
                .adviseContext(Map.of(AuthorizedAdvisor.AUTH_USER_ID_PARAM, "badGuy"))
                .build();
        AtomicInteger chainCalls = new AtomicInteger();
        CallAroundAdvisorChain chain = req -> {
            chainCalls.incrementAndGet();
            return null;
        };
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
                () -> advisor.aroundCall(request, chain));
        assertTrue(ex.getMessage().contains("封禁"));
        assertEquals(0, chainCalls.get());
    }

    @Test
    void testAuthorizedAdvisor_normalUser() {
        AuthorizedAdvisor advisor = new AuthorizedAdvisor(java.util.Set.of("badGuy"));
        AdvisedRequest request = AdvisedRequest.builder()
                .userText("你好")
                .chatModel(FAKE_MODEL)
                .adviseContext(Map.of(AuthorizedAdvisor.AUTH_USER_ID_PARAM, "derder"))
                .build();
        AtomicInteger chainCalls = new AtomicInteger();
        CallAroundAdvisorChain chain = req -> {
            chainCalls.incrementAndGet();
            return null;
        };
        advisor.aroundCall(request, chain);
        assertEquals(1, chainCalls.get(), "正常用户应该放行");
    }
}
