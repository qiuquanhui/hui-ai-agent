package com.hui.huiaiagent.demo.rag;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 查询扩展器 Demo（和 QueryRewriter 同属"查询变换"家族，思路反着来）：
 * 重写器是把一个口语问题"整理成一个规范问题"；扩展器是把一个问题
 * "扩写成 3 个不同角度的问法"，各查一遍再合并——提高召回率
 */
@Component
public class MultiQueryExpanderDemo {

    // 备一个 ChatClient builder（待会交给扩展器用）
    private final ChatClient.Builder chatClientBuilder;

    // 构造器里备好（启动造一次，全局共用）
    public MultiQueryExpanderDemo(ChatModel dashscopeChatModel) {
        this.chatClientBuilder = ChatClient.builder(dashscopeChatModel);
    }

    // 进一个问法，出多个角度的问法（例："如何挽回感情" → "复合方法"/"分手后怎么复合"/"感情修复技巧"）
    public List<Query> expand(String query) {
        MultiQueryExpander queryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(chatClientBuilder)   // 用哪个大模型来扩写
                .numberOfQueries(3)                     // 扩成 3 个（含原问题）
                .build();
        // 执行扩展：把原话装进 Query 对象 → 扩写出多个 Query → 返回列表
        List<Query> queries = queryExpander.expand(new Query(query));
        return queries;
    }
}