package com.hui.huiaiagent.demo.rag;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.rag.Query;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
class MultiQueryExpanderDemoTest {

    @Resource
    private MultiQueryExpanderDemo multiQueryExpanderDemo;

    @Test
    void expand() {
        List<Query> queries = multiQueryExpanderDemo.expand("啥是程序员derder啊啊啊啊啊啊？！请回答我哈哈哈哈");
        log.info("查询扩展器 queries : {}",queries);
        Assertions.assertNotNull(queries);
    }
}
