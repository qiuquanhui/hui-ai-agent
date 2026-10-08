package com.hui.huiaiagent.rag;

import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

/**
 * 自定义 RAG Advisor 的工厂：造"自选套餐"——检索器、增强器分开自己配
 * （区别于全家桶 QuestionAnswerAdvisor：它的检索规则、拼 prompt 规则都是定死的）
 */
public class LoveAppRagCustomAdvisorFactory {

    /**
     * 造一个"只翻指定抽屉"的 RAG Advisor
     *
     * @param vectorStore 卡片柜
     * @param status      只翻哪个抽屉（单身 / 恋爱 / 已婚——入库时贴的标签）
     */
    public static Advisor createLoveAppRagCustomAdvisor(VectorStore vectorStore, String status) {

        // 第 3 步：过滤条件——相当于 SQL 的 WHERE metadata.status = '已婚'
        // （入库时贴的 status 标签，在这里闭环：只翻这一个抽屉，别的抽屉再像也不翻）
        Filter.Expression expression = new FilterExpressionBuilder()
                .eq("status", status)
                .build();

        // 第 4 步：检索器——定义"怎么翻柜子"的完整规则
        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)          // 去哪个柜子翻
                .filterExpression(expression)      // 只翻带这个标签的抽屉
                .similarityThreshold(0.5)          // 相似度门槛：低于 0.5 的卡片宁可不带（带错的更糟）
                .topK(3)                           // 最多带 3 张（带多了 prompt 冗长、费钱）
                .build();

        // 第 5 步：组装成 Advisor——零件① 翻柜子规则 + 零件② 抄题规则（空结果兜底）
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .queryAugmenter(LoveAppContextualQueryAugmenterFactory.createInstance())
                .build();
    }

//    LoveApp 的 ChatClient 对象应用这个 Advisor：
//    chatClient.advisors(
//            LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(
//    loveAppVectorStore, "已婚"
//            )
//            )
}