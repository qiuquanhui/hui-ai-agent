package com.hui.huiaiagent.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

// 备用刀具①：按 token 数硬切卡片——文档没有 "---" 分隔线时的保底切分手段
// （主线用的是 MarkdownDocumentReader 按分隔线切；这个类默认注释不启用）
@Component
class MyTokenTextSplitter {

    // 全默认参数切分：默认每段约 200 token
    public List<Document> splitDocuments(List<Document> documents) {
        TokenTextSplitter splitter = new TokenTextSplitter();
        return splitter.apply(documents);
    }

    // 自定义参数切分（五个参数从左到右）：
    // 200 = 每段目标 token 数（太长检索不准、太短意思破碎，200 是常用平衡点）
    // 100 = 段内最少字符数（太短的碎块并进上一段）
    // 10  = 最短可嵌入长度（比这还短的段不值得送去算向量）
    // 5000 = 最多切多少段（防失控保险丝）
    // true = 保留切分处的分隔符（尽量不破坏语句）
    public List<Document> splitCustomized(List<Document> documents) {
        TokenTextSplitter splitter = new TokenTextSplitter(200, 100, 10, 5000, true);
        return splitter.apply(documents);
    }
}