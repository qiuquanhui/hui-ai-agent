package com.hui.huiaiagent.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.stereotype.Component;

@Component
public class QueryRewriter {

    //定义查询重写转化器对象
    private final QueryTransformer queryTransformer;

    public QueryRewriter(ChatModel dashscopeChatModel) {
        ChatClient.Builder builder = ChatClient.builder(dashscopeChatModel);
        // 默认提示词是英文的且没要求保持语言，模型会顺着英文输出；这里换成中文提示词
        // 注意：模板里必须保留 {query} 和 {target} 两个占位符，否则 builder 校验会直接报错
        PromptTemplate chinesePromptTemplate = new PromptTemplate("""
                你是一个查询改写助手。请把下面的用户查询改写成更适合在{target}中检索的形式：
                去掉与检索无关的闲聊内容，把它整理成一个独立、完整的问题。
                必须使用中文输出，禁止翻译成英文。

                用户查询：{query}

                改写后的查询：
                """);
        // 创建查询重写转换器
        queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(builder)
                .promptTemplate(chinesePromptTemplate)
                .targetSearchSystem("向量数据库")
                .build();
    }

    public String doQueryRewrite(String prompt) {
        Query query = new Query(prompt);
        // 执行查询重写
        Query transformedQuery = queryTransformer.transform(query);
        // 输出重写后的查询
        return transformedQuery.text();
    }
}