<script setup>
import ChatRoom from '@/components/ChatRoom.vue'
import { chatWithManus } from '@/api/ai'

/**
 * 发送函数：SSE 流式调用超级智能体接口
 * 该接口无需 chatId，此处忽略即可
 */
function send(message, chatId, handlers) {
  return chatWithManus(message, handlers)
}
</script>

<template>
  <ChatRoom
    theme="manus"
    title="AI 超级智能体"
    subtitle="自动规划并调用工具，完成复杂任务"
    ai-avatar="🚀"
    user-avatar="🧑‍💻"
    :use-chat-id="false"
    placeholder="描述一个任务，例如：帮我搜索今天的新闻并生成一份摘要"
    welcome-title="你好，我是超级智能体 🚀"
    welcome-text="我会自动拆解任务、调用工具逐步执行，并在下方实时展示每一步的过程与结果，复杂任务请耐心等待～"
    :send-function="send"
  />
</template>
