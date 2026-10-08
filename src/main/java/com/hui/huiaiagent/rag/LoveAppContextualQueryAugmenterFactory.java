package com.hui.huiaiagent.rag;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

/**
 * 创建上下文查询增强器的工厂：负责"空结果兜底"
 * 默认行为下，就算检索一张卡片都没找到，prompt 仍是"请回答：xxx"——
 * 没有参考资料，AI 就开始自由发挥（幻觉回来了）；这个工厂把这种行为关掉，
 * 检索为空时改输出一套固定话术——开卷考试没翻到书，就老实说不会
 */
public class LoveAppContextualQueryAugmenterFactory {
    public static ContextualQueryAugmenter createInstance() {
        // "没翻到书"时改用的固定话术（三引号内是真正要发给大模型的文字，不能写注释）
        PromptTemplate emptyContextPromptTemplate = new PromptTemplate("""
                你应该输出下面的内容：
                抱歉，我只能回答恋爱相关的问题，别的没办法帮到您哦，
                有问题可以联系编程导航客服 https://codefather.cn
                """);

        return ContextualQueryAugmenter.builder()
                // ① 关掉"没资料也硬答"的默认行为
                .allowEmptyContext(false)
                // ② 检索结果为空时，改用上面这套固定话术
                .emptyContextPromptTemplate(emptyContextPromptTemplate)
                .build();
    }
}
