package com.hui.huiaiagent.rag;

//文档向量化：开一个"卡片柜"（向量库），程序启动时把卡片全部翻译成坐标存进去

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
 * 启动时执行一次："考前把书搬进考场"；之后问答只管翻柜子，不再重复翻译（省钱）
 */
@Configuration
public class LoveAppVectorStoreConfig {

    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;        // 备用刀具①：token 切分器（本主线未启用）

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;   // 做卡片工人：读 md → 切卡片 → 贴标签

    @Resource
    private MyKeywordEnricher myKeywordEnricher;           // 备用刀具②：关键词增强器（本主线未启用）

    @Bean
    // 参数 dashscopeEmbeddingModel：Spring 自动递进来的"翻译官"（阶段 2 starter 自动装配）
    VectorStore loveAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        // 开一个卡片柜（存在内存里），并指定入柜/查询时用哪位"翻译官"
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
                .build();
        // 让工人开工：读 markdown → 切卡片 → 贴标签，拿到全部卡片
        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
        // 全部入柜：每张卡片都会被"翻译官"转成 1536 维坐标存好（真调 Embedding 接口）
        simpleVectorStore.add(documents);
        // 需要则开启注释：自主切分（文档没有 "---" 分隔线时换这条路线）
//        List<Document> splitDocuments = myTokenTextSplitter.splitCustomized(documents);
//        simpleVectorStore.add(splitDocuments);
        // 需要时开启注释：自动补充关键词元信息（每张卡多花一次 AI 调用，换一路检索线索）
      //  List<Document> enrichedDocuments = myKeywordEnricher.enrichDocuments(documents);
        //simpleVectorStore.add(enrichedDocuments);
        return simpleVectorStore;                          // 柜子交给 Spring，哪里要用哪里注入
    }
}