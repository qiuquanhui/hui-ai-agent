<script setup>
import ChatRoom from '@/components/ChatRoom.vue'
import { chatWithLoveAppSse } from '@/api/ai'

/**
 * 发送函数：SSE 流式调用恋爱大师接口
 * chatId 由 ChatRoom 生成并传入，后端以此隔离会话记忆
 */
function send(message, chatId, handlers) {
  return chatWithLoveAppSse(message, chatId, handlers)
}
</script>

<template>
  <ChatRoom
    theme="love"
    title="AI 恋爱大师"
    subtitle="你的专属恋爱智慧助手"
    ai-avatar="💖"
    user-avatar="😊"
    :use-chat-id="true"
    placeholder="说说你的情感困惑，例如：如何和心仪的人开启话题？"
    welcome-title="你好，我是恋爱大师 💖"
    welcome-text="无论是暗恋纠结、约会准备，还是相处中的小摩擦，都可以跟我说说，我来帮你出出主意～"
    :send-function="send"
  />
</template>
