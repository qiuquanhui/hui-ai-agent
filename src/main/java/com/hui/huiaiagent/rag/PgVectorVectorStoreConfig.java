package com.hui.huiaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;


import java.util.List;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

// 支线②：pgvector 生产级卡片柜（默认注释关闭——需要 PostgreSQL 才打开）
// 和内存柜 SimpleVectorStore 的区别：真正的数据库柜，重启不丢、多人共享、可扩展
//@Configuration
public class PgVectorVectorStoreConfig {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;   // 做卡片工人（入库时用它读文档）

    @Bean
    public VectorStore pgVectorVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel dashscopeEmbeddingModel) {
        // jdbcTemplate 连 PostgreSQL（配置来自数据源）；dashscopeEmbeddingModel 还是那位"翻译官"
        VectorStore vectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
                .dimensions(1536)                    // 向量维度：和灵积 Embedding 输出对齐
                .distanceType(COSINE_DISTANCE)       // 距离算法：余弦相似度（算"意思像不像"）
                .indexType(HNSW)                     // 索引算法：高维快速近邻搜索
                .initializeSchema(true)              // 自动建表建索引（首次启动免手写 DDL）
                .schemaName("public")                // Optional: defaults to "public"
                .vectorTableName("vector_store")     // Optional: defaults to "vector_store"
                .maxDocumentBatchSize(10000)         // Optional: defaults to 10000
                .build();
        // 加载文档，分批添加（DashScope Embedding API 限制单次最多 25 条，一次性全塞会报 The input texts limit 25）
//       已加入一次，需要文档时再继续加入
//        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
//        int batchSize = 10;                          // 每批 10 张，稳在 25 的限制之下
//        for (int i = 0; i < documents.size(); i += batchSize) {
//            int end = Math.min(i + batchSize, documents.size());
//            vectorStore.add(documents.subList(i, end));
//        }
        return vectorStore;
    }
}
