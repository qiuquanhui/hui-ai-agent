package com.hui.huiaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.KeywordMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;

// 备用刀具②：让 AI 给每张卡片再补几个"关键词"标签——检索时多一路线索
// （代价：每张卡都要多花一次 AI 调用；默认注释不启用）
@Component
class MyKeywordEnricher {

    @Resource
    private ChatModel dashscopeChatModel;   // 用哪个模型来提炼关键词（阶段 2 自动装配的电话）

    List<Document> enrichDocuments(List<Document> documents) {
        // 参数 2 = 每张卡片提炼 2 个关键词，写进卡片的 metadata（标签区）
        KeywordMetadataEnricher enricher = new KeywordMetadataEnricher(this.dashscopeChatModel, 2);
        return enricher.apply(documents);
    }
}