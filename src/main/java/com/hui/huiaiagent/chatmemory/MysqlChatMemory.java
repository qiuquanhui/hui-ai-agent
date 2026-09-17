package com.hui.huiaiagent.chatmemory;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 基于 MySQL 持久化的对话记忆
 * 和 FileBasedChatMemory 实现同一个 ChatMemory 接口，只是把"一个会话一个文件"
 * 换成"一条消息一行记录"，可以随时在两种实现之间切换
 */
@Component
public class MysqlChatMemory implements ChatMemory {

    private final JdbcTemplate jdbcTemplate;

    // 由 Spring 注入 JdbcTemplate（连接信息来自 application-local.yml 的 spring.datasource）
    public MysqlChatMemory(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 记住新消息：往表里插行，一行一条消息
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        for (Message message : messages) {
            String content = message.getText();
            // 工具调用消息没有纯文本形态，本项目对话用不到，直接跳过
            if (content == null || content.isEmpty()) {
                continue;
            }
            jdbcTemplate.update(
                    "INSERT INTO chat_memory (conversation_id, role, content) VALUES (?, ?, ?)",
                    conversationId, message.getMessageType().name(), content);
        }
    }

    /**
     * 回忆最近 lastN 条消息：按 id 倒序取最近 N 条，再反转成正常的时间顺序
     */
    @Override
    public List<Message> get(String conversationId, int lastN) {
        List<Message> recentMessages = jdbcTemplate.query(
                "SELECT role, content FROM chat_memory WHERE conversation_id = ? ORDER BY id DESC LIMIT ?",
                (rs, rowNum) -> toMessage(rs.getString("role"), rs.getString("content")),
                conversationId, lastN);
        // 倒序取的是"最新在前"，翻回"最旧在前"，AI 读起来才是正常聊天顺序
        Collections.reverse(recentMessages);
        return recentMessages;
    }

    /**
     * 忘记某个会话：删除该会话的全部记录
     */
    @Override
    public void clear(String conversationId) {
        jdbcTemplate.update("DELETE FROM chat_memory WHERE conversation_id = ?", conversationId);
    }

    /**
     * 把数据库里的 (role, content) 还原成 Spring AI 的 Message 对象
     */
    private Message toMessage(String role, String content) {
        return switch (role) {
            case "USER" -> new UserMessage(content);
            case "ASSISTANT" -> new AssistantMessage(content);
            case "SYSTEM" -> new SystemMessage(content);
            // 理论上不会走到这，兜底当成用户消息处理
            default -> new UserMessage(content);
        };
    }
}
