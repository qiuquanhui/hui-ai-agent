<script setup>
const apps = [
  {
    to: '/love',
    icon: '💖',
    name: 'AI 恋爱大师',
    desc: '你的专属恋爱智慧助手，解答情感困惑、提供恋爱建议，支持多轮对话记忆',
    tag: '恋爱顾问'
  },
  {
    to: '/manus',
    icon: '🚀',
    name: 'AI 超级智能体',
    desc: '全能 Manus 智能体，自动规划任务并调用工具，一步步完成复杂请求',
    tag: '任务执行'
  }
]
</script>

<template>
  <div class="home">
    <div class="home-content">
      <header class="home-header">
        <div class="logo">🤖</div>
        <h1>Hui AI 应用中心</h1>
        <p>基于 Spring AI 打造的智能应用集合，选择一个应用开始体验</p>
      </header>

      <div class="app-grid">
        <router-link
          v-for="app in apps"
          :key="app.to"
          :to="app.to"
          class="app-card"
          :class="app.to.slice(1)"
        >
          <span class="card-tag">{{ app.tag }}</span>
          <div class="card-icon">{{ app.icon }}</div>
          <h2 class="card-name">{{ app.name }}</h2>
          <p class="card-desc">{{ app.desc }}</p>
          <span class="card-arrow">开始聊天 →</span>
        </router-link>
      </div>

      <footer class="home-footer">
        <p>Vue 3 + Spring AI · 流式对话体验</p>
      </footer>
    </div>
  </div>
</template>

<style scoped>
.home {
  min-height: 100vh;
  background:
    radial-gradient(ellipse 60% 40% at 15% 10%, rgba(244, 63, 94, 0.12), transparent),
    radial-gradient(ellipse 60% 45% at 85% 15%, rgba(99, 102, 241, 0.14), transparent),
    radial-gradient(ellipse 50% 40% at 50% 100%, rgba(139, 92, 246, 0.1), transparent),
    #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.home-content {
  width: 100%;
  max-width: 880px;
}

.home-header {
  text-align: center;
  margin-bottom: 48px;
}

.logo {
  font-size: 56px;
  margin-bottom: 16px;
  animation: float 3.5s ease-in-out infinite;
}

.home-header h1 {
  font-size: 32px;
  font-weight: 700;
  color: #111827;
  letter-spacing: 1px;
  margin-bottom: 12px;
}

.home-header p {
  font-size: 15px;
  color: #6b7280;
}

.app-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.app-card {
  position: relative;
  display: flex;
  flex-direction: column;
  padding: 30px 28px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 20px;
  text-decoration: none;
  transition:
    transform 0.25s ease,
    box-shadow 0.25s ease,
    border-color 0.25s ease;
  overflow: hidden;
}

.app-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.1);
}

/* 顶部渐变饰条 */
.app-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
}

.app-card.love::before {
  background: linear-gradient(135deg, #f43f5e, #fb7185);
}

.app-card.manus::before {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
}

.app-card.love:hover {
  border-color: rgba(244, 63, 94, 0.35);
}

.app-card.manus:hover {
  border-color: rgba(99, 102, 241, 0.35);
}

.card-tag {
  position: absolute;
  top: 18px;
  right: 18px;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 999px;
  color: #6b7280;
  background: #f3f4f6;
}

.app-card.love .card-tag {
  color: #e11d48;
  background: #ffe4e6;
}

.app-card.manus .card-tag {
  color: #4f46e5;
  background: #e0e7ff;
}

.card-icon {
  font-size: 44px;
  margin-bottom: 16px;
  transition: transform 0.25s ease;
}

.app-card:hover .card-icon {
  transform: scale(1.15);
}

.card-name {
  font-size: 20px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 10px;
}

.card-desc {
  font-size: 14px;
  line-height: 1.7;
  color: #6b7280;
  margin-bottom: 20px;
  flex: 1;
}

.card-arrow {
  font-size: 14px;
  font-weight: 500;
  transition: letter-spacing 0.2s ease;
}

.app-card.love .card-arrow {
  color: #e11d48;
}

.app-card.manus .card-arrow {
  color: #4f46e5;
}

.app-card:hover .card-arrow {
  letter-spacing: 1px;
}

.home-footer {
  margin-top: 48px;
  text-align: center;
  font-size: 13px;
  color: #9ca3af;
  user-select: none;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-8px);
  }
}
</style>
