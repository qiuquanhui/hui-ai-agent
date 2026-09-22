package com.hui.huiaiagent.rag;

//文档向量化

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


/**
 * 恋爱大师向量数据库配置（初始化基于内存的向量数据库 Bean）
 */
@Configuration
public class LoveAppVectorStoreConfig {

    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;
    
    @Bean
    VectorStore loveAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
                .build();
        // 加载文档
        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
        // 需要则开启注释：自主切分
//        List<Document> splitDocuments = myTokenTextSplitter.splitCustomized(documents);
//        simpleVectorStore.add(splitDocuments);
           simpleVectorStore.add(documents);

        return simpleVectorStore;
    }
}