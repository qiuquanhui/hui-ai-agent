package com.hui.huiaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;                        // 一张"知识卡片"（正文 + 标签）
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;   // Spring AI 的"切卡机"
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;  // 切卡规则
import org.springframework.core.io.Resource;                            // 一个文件的抽象
import org.springframework.core.io.support.ResourcePatternResolver;     // Spring 的"文件探测器"（按通配符找文件）
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//文档读取："读 md 文件 → 切知识卡片 → 贴标签"的做卡片工人
@Component
@Slf4j
class LoveAppDocumentLoader {

    private final ResourcePatternResolver resourcePatternResolver;

    // 构造器注入"文件探测器"（Spring 自带的零件，能按通配符搜 classpath）
    LoveAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    //读取markdown 文件的代码：把 document 目录下所有 md 变成卡片列表 List<Document>
    public List<Document> loadMarkdowns() {
        List<Document> allDocuments = new ArrayList<>();      // 收集全部卡片的总列表
        try {
            // 通配地址："resources/document 目录下所有 md 文件"——以后加知识扔文件进目录即可，代码不用改
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");
            for (Resource resource : resources) {             // 逐个文件处理
                String fileName = resource.getFilename();     // 如"已婚状态常见恋爱问题与回答.md"
                // 获取文件的状态名:单身，已婚，恋爱
                // 截文件名前两个字符当标签——所以文件起名必须守约定，前两字=有效状态
                String status = fileName.substring(0,2);
                // 配置"切卡机"：怎么切、贴什么标签
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)   // 遇到 "---" 分隔线就切一张新卡片
                        .withIncludeCodeBlock(false)              // 代码块不单独成卡（本项目文档没有代码）
                        .withIncludeBlockquote(false)             // 引用块不单独成卡
                        .withAdditionalMetadata("filename", fileName)  // 贴"文件名"标签（出问题时溯源用）
                        .withAdditionalMetadata("status",status)  // 贴"状态"标签（检索时只翻指定抽屉用）★
                        .build();
                // 造出切卡机，喂给它"这份文件 + 切卡规则"
                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                allDocuments.addAll(reader.get());            // get() 一调用就完成切卡，收集进总列表
            }
        } catch (IOException e) {
            log.error("Markdown 文档加载失败", e);
        }
        return allDocuments;                                  // 全部文件的全部卡片
    }
}