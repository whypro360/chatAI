<template>
  <div class="home" :class="{ 'dark': isDark }">
    <div class="container">
      <h1 class="title">IMRI</h1>
      <div class="cards-grid">
        <!-- 第一行主卡片 -->
        <router-link 
          :to="aiApps[0].route"
          class="card main-card"
        >
          <div class="card-content">
            <component :is="aiApps[0].icon" class="icon" />
            <h2>{{ aiApps[0].title }}</h2>
            <p>{{ aiApps[0].description }}</p>
          </div>
        </router-link>

        <!-- 第二行子卡片 -->
        <div class="sub-cards">
          <router-link 
            v-for="app in aiApps.slice(1)"
            :key="app.id"
            :to="app.route"
            class="card"
          >
            <div class="card-content">
              <component :is="app.icon" class="icon" :class="app.iconClass" />
              <h2>{{ app.title }}</h2>
              <p>{{ app.description }}</p>
            </div>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useDark } from '@vueuse/core'
import { 
  ChatBubbleLeftRightIcon,
  HeartIcon,
  UserGroupIcon,
  DocumentTextIcon
} from '@heroicons/vue/24/outline'

const isDark = useDark()

const aiApps = ref([
  {
    id: 1,
    title: 'AI 聊天',
    description: '多模态对话机器人，支持图片等',
    route: '/ai-chat',
    icon: ChatBubbleLeftRightIcon
  },
  {
    id: 2,
    title: '双代同堂',
    description: '一个帮助你解决代沟的助手',
    route: '/game',
    icon: HeartIcon,
    iconClass: 'heart-icon'
  },
  {
    id: 3,
    title: '智能客服',
    description: '24小时在线的智能咨询师',
    route: '/customer-service',
    icon: UserGroupIcon
  },
  {
    id: 4,
    title: 'ChatPDF',
    description: '打造你的个人知识库，与知识库自由对话',
    route: '/chat-pdf',
    icon: DocumentTextIcon
  }
])
</script>

<style scoped lang="scss">
.home {
  min-height: 100vh;
  padding: 2rem;
  background: var(--bg-color);
  transition: background-color 0.3s;

  .container {
    max-width: 1600px;
    margin: 0 auto;
    padding: 0 2rem;
  }

  .title {
    text-align: center;
    font-size: 2.5rem;
    margin-bottom: 3rem;
    background: linear-gradient(45deg, #007CF0, #00DFD8);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    animation: fadeIn 1s ease-out;
  }

  .cards-grid {
    display: flex;
    flex-direction: column;
    gap: 2rem;
    width: 100%;
  }

  .main-card {
    width: 100%;
    max-width: calc(3 * 320px + 2 * 2rem); // (3卡片宽度 + 2间隔)
    margin: 0 auto;
  }

  .sub-cards {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 2rem;
    width: 100%;
    max-width: calc(3 * 320px + 2 * 2rem);
    margin: 0 auto;
  }

  .card {
    position: relative;
    background: rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    padding: 2rem;
    text-decoration: none;
    color: inherit;
    transition: all 0.3s ease;
    border: 1px solid rgba(255, 255, 255, 0.1);
    overflow: hidden;

    .dark & {
      background: rgba(255, 255, 255, 0.05);
      border: 1px solid rgba(255, 255, 255, 0.05);
    }

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
      
      .dark & {
        box-shadow: 0 10px 20px rgba(0, 0, 0, 0.3);
      }
    }

    .card-content {
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
    }

    .icon {
      width: 48px;
      height: 48px;
      margin-bottom: 1rem;
      color: #007CF0;

      &.heart-icon {
        color: #ff4d4f;
        animation: pulse 1.5s ease-in-out infinite;
      }
    }

    h2 {
      font-size: 1.5rem;
      margin-bottom: 0.5rem;
    }

    p {
      color: #666;
      font-size: 1rem;

      .dark & {
        color: #999;
      }
    }
  }

  &.dark {
    background: #1a1a1a;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}

@media (max-width: 1200px) {
  .sub-cards {
    grid-template-columns: repeat(2, 1fr);
    max-width: calc(2 * 320px + 1 * 2rem);
  }
  
  .main-card {
    max-width: calc(2 * 320px + 1 * 2rem);
  }
}

@media (max-width: 768px) {
  .home {
    padding: 1rem;
    
    .container {
      padding: 0 1rem;
    }
    
    .title {
      font-size: 2rem;
    }

    .sub-cards {
      grid-template-columns: 1fr;
      max-width: 320px;
    }
    
    .main-card {
      max-width: 320px;
    }
  }
}
</style>