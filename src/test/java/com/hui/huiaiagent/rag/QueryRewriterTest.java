package com.hui.huiaiagent.rag;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class QueryRewriterTest {

    @Resource
    private QueryRewriter queryRewriter;

    @Test
    void doQueryRewrite() {
        String rewritePrompt = queryRewriter.doQueryRewrite("我已经结婚了，但是结婚之后日常被家务、琐事缠身，夫妻经常因为家务分工吵架，怎么办，顺便给我提供一个课程");
        log.info("rewritePrompt:{}",rewritePrompt);
    }
}