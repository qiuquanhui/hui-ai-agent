-- ============================================
-- AI 对话记忆表：每聊一句，就往这张表插一行
-- ============================================
CREATE DATABASE IF NOT EXISTS hui_ai_agent DEFAULT CHARSET utf8mb4;

USE hui_ai_agent;

CREATE TABLE IF NOT EXISTS chat_memory (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键，天然记录消息的先后顺序',
    conversation_id VARCHAR(64)     NOT NULL COMMENT '会话 id（对应代码里的 chatId）',
    role            VARCHAR(20)     NOT NULL COMMENT '谁说的：USER=用户 / ASSISTANT=AI / SYSTEM=系统设定',
    content         TEXT            NOT NULL COMMENT '消息原文',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
    PRIMARY KEY (id),
    KEY idx_conversation (conversation_id, id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'AI 对话记忆表';
