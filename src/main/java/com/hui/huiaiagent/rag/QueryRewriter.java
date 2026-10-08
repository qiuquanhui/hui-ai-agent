package com.hui.huiaiagent.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.stereotype.Component;

/**
 * 查询重写器：检索前的"问题加工车间"——进一句口语，出一句规范问题
 * （例："我老婆天天跟我吵家务咋整啊" → "已婚夫妻家务分工矛盾如何解决"）
 * 本质是两次模型调用：先本类改写（小调用），再走正常 RAG 检索 + 答题
 */
@Component
public class QueryRewriter {

    // 改写引擎：Spring AI 官方的查询变换器（我们只负责换中文"岗位说明书"）
    private final QueryTransformer queryTransformer;

    // 构造器里装配一次，全局共用（启动时造好，之后每次改写不再重复建设）
    public QueryRewriter(ChatModel dashscopeChatModel) {
        // 先备一个 ChatClient 的 builder（待会交给改写引擎用）
        ChatClient.Builder builder = ChatClient.builder(dashscopeChatModel);
        // 默认提示词是英文的且没要求保持语言，模型会顺着英文输出——可我们的卡片是中文，
        // 拿英文问题对中文卡片比坐标方向全偏，所以换成中文提示词
        // 注意：模板里必须保留 {query} 和 {target} 两个占位符，否则 builder 校验会直接报错
        // （{query} 由框架运行时填用户原话；{target} 由下面的 targetSearchSystem 填入）
        PromptTemplate chinesePromptTemplate = new PromptTemplate("""
                你是一个查询改写助手。请把下面的用户查询改写成更适合在{target}中检索的形式：
                去掉与检索无关的闲聊内容，把它整理成一个独立、完整的问题。
                必须使用中文输出，禁止翻译成英文。

                用户查询：{query}

                改写后的查询：
                """);
        // 创建查询重写转换器：谁改写 + 按哪份说明书改 + 改写是为了往哪查
        queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(builder)              // 用哪个大模型来改写
                .promptTemplate(chinesePromptTemplate)   // 按哪份"岗位说明书"改
                .targetSearchSystem("向量数据库")         // 这个值会填进 {target} 占位符
                .build();
    }

    // 对外唯一入口：进一句口语，出一句规范问题
    public String doQueryRewrite(String prompt) {
        Query query = new Query(prompt);                  // 把用户原话装进框架认识的"查询"对象
        // 执行查询重写（内部是一次大模型调用：填模板 → 发给模型 → 收回改写结果）
        Query transformedQuery = queryTransformer.transform(query);
        // 输出重写后的查询（从结果对象里取出文本）
        return transformedQuery.text();
    }
}