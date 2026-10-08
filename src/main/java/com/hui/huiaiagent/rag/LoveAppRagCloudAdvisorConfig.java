package com.hui.huiaiagent.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/*
*   云知识库向量存储配置（生产级支线①）
*   把"读文档 → 切卡片 → 入柜"整套杂活外包给阿里云百炼：
*   在控制台建好数据索引并导入文档后，本地只留一个云端检索器
*/
@Configuration
@Slf4j
class LoveAppRagCloudAdvisorConfig {

    //读取Key（从 application-local.yml 拿灵积钥匙）
    @Value("${spring.ai.dashscope.api-key}")
    private String dashScopeApiKey;

    @Bean
    public Advisor loveAppRagCloudAdvisor() {
        // 根据key创建API（访问云端知识库的"门禁卡"）
        DashScopeApi dashScopeApi = new DashScopeApi(dashScopeApiKey);
        final String KNOWLEDGE_INDEX = "恋爱大师";   // 云端数据索引名（需在百炼控制台提前建好）
        // 云端检索器：去百炼的"恋爱大师"索引里找相关文档（切分/向量化全在云端完成）
        DocumentRetriever documentRetriever = new DashScopeDocumentRetriever(dashScopeApi,
                DashScopeDocumentRetrieverOptions.builder()
                        .withIndexName(KNOWLEDGE_INDEX)
                        .build());
        // 组装成 Advisor——LoveApp 注入它，doChatWithRag2 用的就是这位
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .build();
    }
}