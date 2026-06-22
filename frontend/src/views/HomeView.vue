<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { getPing } from '@/api/ping'
import { useAuthStore } from '@/stores/auth'

interface TypeItem {
  key: string
  name: string
  total: number
  color: string
}

interface PermItem {
  key: string
  name: string
  total: number
  totalLabel: string
  breakdown: Record<string, number>
}

const TYPE_DATA: TypeItem[] = [
  { key: 'staff', name: '教职工', total: 8956, color: '#387EF5' },
  { key: 'student', name: '学生', total: 67428, color: '#0FB17B' },
  { key: 'service', name: '校内服务人员', total: 12384, color: '#F6980D' },
  { key: 'external', name: '校外临时人员', total: 5515, color: '#A251F6' },
]

const PERMS: PermItem[] = [
  {
    key: 'gate',
    name: '进校权限',
    total: 92847,
    totalLabel: '已授权',
    breakdown: { staff: 8956, student: 67428, service: 12384, external: 4079 },
  },
  {
    key: 'net',
    name: '校园网',
    total: 76340,
    totalLabel: '已开通',
    breakdown: { staff: 8956, student: 67384, service: 0, external: 0 },
  },
  {
    key: 'card',
    name: '校园卡',
    total: 88150,
    totalLabel: '已绑定',
    breakdown: { staff: 8956, student: 67428, service: 9220, external: 2546 },
  },
  {
    key: 'library',
    name: '图书馆',
    total: 80204,
    totalLabel: '可借阅',
    breakdown: { staff: 8956, student: 67428, service: 1820, external: 2000 },
  },
]

const auth = useAuthStore()

const NAV_CARDS_ALL = [
  { mod: 'm1', title: '人员基础身份', desc: '人员进档 · 一人一ID · 多源头采集', to: '/identity/basic' },
  { mod: 'm2', title: '人员分类身份', desc: '树形分类 · 标准属性 · 实例挂载', to: '/identity/classification' },
  { mod: 'm3', title: '人员岗位身份', desc: '广义岗位 · 一人多身份', to: '/identity/position' },
  { mod: 'm7', title: '自定义标签身份', desc: '自定义群组 · 灵活标注 · 便捷查询', to: '/identity/tags' },
  { mod: 'm4', title: '组织机构体系', desc: '院系树 · 强绑定身份', to: '/identity/org' },
  { mod: 'm5', title: '身份权限管理', desc: '权限项 · 类型授权 · 状态联动', to: '/identity/permission' },
  { mod: 'm6', title: '系统服务', desc: '数据查询 · 字典 · API · 配置', to: '/services/query/identity' },
]

const NAV_CARDS = computed(() =>
  NAV_CARDS_ALL.filter((card) => auth.canAccessPath(card.to)),
)

const TOTAL = TYPE_DATA.reduce((sum, item) => sum + item.total, 0)
const selectedPermIdx = ref(0)
const tsNow = ref('—')
const apiStatus = ref<'checking' | 'connected' | 'disconnected'>('checking')
const apiPingText = ref('')
let timer: ReturnType<typeof setInterval> | undefined

const selectedPerm = computed(() => PERMS[selectedPermIdx.value]!)

const grantedPct = computed(() => (selectedPerm.value.total / TOTAL) * 100)

const piePaths = computed(() => {
  const granted = selectedPerm.value.total
  const grantedPctVal = (granted / TOTAL) * 100
  const cx = 100
  const cy = 100
  const rOuter = 80
  const rInner = 52
  const grantedAngle = (granted / TOTAL) * Math.PI * 2
  const gap = 0.012
  const sa1 = -Math.PI / 2 + gap
  const ea1 = -Math.PI / 2 + grantedAngle - gap
  const sa2 = -Math.PI / 2 + grantedAngle + gap
  const ea2 = -Math.PI / 2 + Math.PI * 2 - gap

  function arc(sa: number, ea: number, color: string) {
    const x1o = cx + rOuter * Math.cos(sa)
    const y1o = cy + rOuter * Math.sin(sa)
    const x2o = cx + rOuter * Math.cos(ea)
    const y2o = cy + rOuter * Math.sin(ea)
    const x1i = cx + rInner * Math.cos(ea)
    const y1i = cy + rInner * Math.sin(ea)
    const x2i = cx + rInner * Math.cos(sa)
    const y2i = cy + rInner * Math.sin(sa)
    const largeArc = ea - sa > Math.PI ? 1 : 0
    const path = `M ${x1o} ${y1o} A ${rOuter} ${rOuter} 0 ${largeArc} 1 ${x2o} ${y2o} L ${x1i} ${y1i} A ${rInner} ${rInner} 0 ${largeArc} 0 ${x2i} ${y2i} Z`
    return { d: path, fill: color }
  }

  if (grantedPctVal > 0 && grantedPctVal < 100) {
    return [
      arc(sa1, ea1, 'var(--color-primary)'),
      arc(sa2, ea2, '#E5E6EB'),
    ]
  }
  if (grantedPctVal >= 100) {
    return [{ d: '', fill: 'full-primary' }]
  }
  return [{ d: '', fill: 'full-gray' }]
})

function pctOf(total: number) {
  return ((total / TOTAL) * 100).toFixed(1)
}

function formatNum(n: number) {
  return n.toLocaleString()
}

function updateTs() {
  const now = new Date()
  const pad = (n: number) => String(n).padStart(2, '0')
  tsNow.value = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}`
}

function selectPerm(idx: number) {
  selectedPermIdx.value = idx
}

async function checkApiConnection() {
  apiStatus.value = 'checking'
  try {
    const pong = await getPing()
    apiPingText.value = pong
    apiStatus.value = 'connected'
  } catch {
    apiStatus.value = 'disconnected'
    apiPingText.value = ''
  }
}

onMounted(() => {
  updateTs()
  timer = setInterval(updateTs, 60_000)
  void checkApiConnection()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<template>
  <div class="home">
    <div class="breadcrumb">
      身份数据平台 <span class="sep">/</span><span class="last">平台总览</span>
    </div>

    <div class="page-head">
      <div>
        <h1 class="page-title">高校综合身份数据平台</h1>
        <p class="page-desc">解决高校各类人员的数据身份管理、权限管理、综合查询等核心业务</p>
        <div class="api-badge" :class="apiStatus">
          <span v-if="apiStatus === 'checking'">API 检测中…</span>
          <span v-else-if="apiStatus === 'connected'">API 已连通 · {{ apiPingText }}</span>
          <span v-else>API 未连通（请确认后端 dev 已启动）</span>
        </div>
      </div>
      <div class="page-stats">
        <div class="stat-item">
          <div class="stat-num">{{ formatNum(TOTAL) }}</div>
          <div class="stat-label">在档人数</div>
        </div>
        <div class="stat-item">
          <div class="stat-num">7</div>
          <div class="stat-label">数据接入源</div>
        </div>
        <div class="stat-item">
          <div class="stat-num">99.6%</div>
          <div class="stat-label">数据可用率</div>
        </div>
      </div>
    </div>

    <div class="section-title">功能导航</div>
    <section class="nav-grid">
      <RouterLink
        v-for="card in NAV_CARDS"
        :key="card.mod"
        :to="card.to"
        class="nav-card"
      >
        <span class="nav-card-arrow">→</span>
        <div class="nav-card-icon">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <circle cx="12" cy="8" r="4" />
            <path d="M4 21c0-4 4-7 8-7s8 3 8 7" />
          </svg>
        </div>
        <div>
          <div class="nav-card-title">{{ card.title }}</div>
          <div class="nav-card-desc">{{ card.desc }}</div>
        </div>
      </RouterLink>
    </section>

    <div class="section-title">综合数据视图</div>
    <section class="data-grid">
      <div class="data-card">
        <div class="card-head">
          <div>
            <div class="card-title">全校有效身份人员总览</div>
            <div class="card-sub">按人员类型划分</div>
          </div>
          <span class="card-badge live">实时</span>
        </div>

        <div class="total-block">
          <div class="total-num">{{ formatNum(TOTAL) }}</div>
          <div class="total-label">在档有效身份人员</div>
          <div class="total-meta">截至 {{ tsNow }}</div>
        </div>

        <div class="type-list">
          <div v-for="item in TYPE_DATA" :key="item.key" class="type-row">
            <span class="type-dot" :style="{ background: item.color }" />
            <span class="type-name">{{ item.name }}</span>
            <span class="type-meta">
              <span class="type-num">{{ formatNum(item.total) }}</span>
              <span class="type-pct">{{ pctOf(item.total) }}%</span>
            </span>
          </div>
        </div>
      </div>

      <div class="data-card">
        <div class="card-head">
          <div>
            <div class="card-title">学校公共权限使用情况</div>
            <div class="card-sub">所有有效身份人员的授权比例分布</div>
          </div>
          <span class="card-badge">截至 {{ tsNow }}</span>
        </div>

        <p class="pie-desc">
          <strong>{{ selectedPerm.name }}</strong>
          · 已授权 <span class="num-hl">{{ formatNum(selectedPerm.total) }}</span> 人 /
          未授权
          <span class="num-hl muted">{{ formatNum(TOTAL - selectedPerm.total) }}</span> 人
        </p>

        <div class="pie-layout">
          <div class="pie-wrap">
            <svg width="220" height="220" viewBox="0 0 200 200" aria-hidden="true">
              <template v-if="piePaths[0]?.fill === 'full-primary'">
                <circle cx="100" cy="100" r="80" fill="#2563EB" />
                <circle cx="100" cy="100" r="52" fill="#FFF" />
              </template>
              <template v-else-if="piePaths[0]?.fill === 'full-gray'">
                <circle cx="100" cy="100" r="80" fill="#E5E6EB" />
                <circle cx="100" cy="100" r="52" fill="#FFF" />
              </template>
              <path
                v-for="(seg, i) in piePaths.filter((p) => p.d)"
                :key="i"
                :d="seg.d"
                :fill="seg.fill"
                class="pie-slice"
              />
            </svg>
            <div class="pie-center">
              <div class="pie-center-num">{{ grantedPct.toFixed(1) }}%</div>
              <div class="pie-center-label">{{ selectedPerm.name }}已授权</div>
            </div>
          </div>

          <div class="pie-legend">
            <div
              v-for="(perm, i) in PERMS"
              :key="perm.key"
              class="legend-item"
              :class="{ active: i === selectedPermIdx }"
              @click="selectPerm(i)"
            >
              <span
                class="legend-swatch"
                :style="{
                  background: i === selectedPermIdx ? 'var(--color-primary)' : 'var(--text-disabled)',
                }"
              />
              <span class="legend-text">{{ perm.name }}</span>
              <span><span class="legend-num">{{ pctOf(perm.total) }}%</span></span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="perm-grid">
      <div
        v-for="(perm, i) in PERMS"
        :key="perm.key"
        class="perm-card"
        :class="{ active: i === selectedPermIdx }"
        @click="selectPerm(i)"
      >
        <div class="perm-card-head">
          <div class="perm-card-title">{{ perm.name }}</div>
          <div class="perm-card-icon">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <rect x="3" y="11" width="18" height="11" rx="2" />
              <path d="M7 11V7a5 5 0 0 1 10 0v4" />
            </svg>
          </div>
        </div>
        <div class="perm-total">
          <span class="perm-total-num">{{ formatNum(perm.total) }}</span>
          <span class="perm-total-label">{{ perm.totalLabel }}</span>
        </div>
        <div class="perm-coverage">覆盖率 {{ pctOf(perm.total) }}%</div>
        <div class="perm-detail">
          <div v-for="t in TYPE_DATA" :key="t.key" class="perm-row">
            <span class="perm-row-dot" :style="{ background: t.color }" />
            <span class="perm-row-name">{{ t.name }}</span>
            <span class="perm-row-num">{{ formatNum(perm.breakdown[t.key] ?? 0) }}</span>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.breadcrumb {
  font-size: 14px;
  color: var(--text-disabled);
  margin-bottom: 16px;
}

.breadcrumb .sep {
  margin: 0 8px;
}

.breadcrumb .last {
  color: var(--text-primary);
}

.page-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border);
  gap: 24px;
  flex-wrap: wrap;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  line-height: 28px;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.page-desc {
  font-size: 14px;
  color: var(--text-secondary);
}

.api-badge {
  margin-top: 12px;
  display: inline-flex;
  align-items: center;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 4px;
  border: 1px solid var(--border);
}

.api-badge.checking {
  color: var(--text-disabled);
  background: var(--bg-secondary);
}

.api-badge.connected {
  color: var(--color-success);
  background: rgba(0, 180, 42, 0.08);
  border-color: rgba(0, 180, 42, 0.2);
}

.api-badge.disconnected {
  color: var(--color-warning);
  background: rgba(255, 125, 0, 0.08);
  border-color: rgba(255, 125, 0, 0.2);
}

.page-stats {
  display: flex;
  gap: 32px;
}

.stat-item {
  text-align: right;
}

.stat-num {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 28px;
  font-family: 'DIN Alternate', 'Roboto', inherit;
}

.stat-label {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 2px;
}

.section-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-primary);
  line-height: 24px;
  margin-bottom: 16px;
  padding-left: 12px;
  position: relative;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 4px;
  width: 4px;
  height: 16px;
  background: var(--color-primary);
  border-radius: 2px;
}

.nav-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 16px;
  margin-bottom: 32px;
}

@media (max-width: 1400px) {
  .nav-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 900px) {
  .nav-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 600px) {
  .nav-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

.nav-card {
  background: var(--bg-card);
  border-radius: 8px;
  box-shadow: var(--shadow-1);
  padding: 16px;
  cursor: pointer;
  transition: all 200ms;
  border: 1px solid transparent;
  display: flex;
  flex-direction: column;
  gap: 12px;
  position: relative;
  text-decoration: none;
  color: inherit;
}

.nav-card:hover {
  box-shadow: var(--shadow-2);
  border-color: var(--color-primary);
  transform: translateY(-2px);
}

.nav-card-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: rgba(37, 99, 235, 0.08);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 200ms;
}

.nav-card:hover .nav-card-icon {
  background: var(--color-primary);
}

.nav-card-icon svg {
  width: 22px;
  height: 22px;
  stroke: var(--color-primary);
  fill: none;
  stroke-width: 1.8;
  transition: stroke 200ms;
}

.nav-card:hover .nav-card-icon svg {
  stroke: white;
}

.nav-card-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-primary);
  line-height: 24px;
}

.nav-card-desc {
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 16px;
}

.nav-card-arrow {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 24px;
  height: 24px;
  border-radius: 4px;
  color: var(--text-disabled);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 200ms;
}

.nav-card:hover .nav-card-arrow {
  color: var(--color-primary);
  transform: translateX(2px);
}

.data-grid {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 24px;
  margin-bottom: 24px;
}

@media (max-width: 1200px) {
  .data-grid {
    grid-template-columns: 1fr;
  }
}

.data-card {
  background: var(--bg-card);
  border-radius: 8px;
  box-shadow: var(--shadow-1);
  padding: 16px;
}

.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-primary);
  line-height: 24px;
  padding-left: 12px;
  position: relative;
}

.card-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 4px;
  width: 4px;
  height: 16px;
  background: var(--color-primary);
  border-radius: 2px;
}

.card-sub {
  font-size: 12px;
  color: var(--text-secondary);
  margin-left: 12px;
  margin-top: 4px;
}

.card-badge {
  font-size: 12px;
  color: var(--text-secondary);
  padding: 2px 8px;
  background: var(--bg-secondary);
  border-radius: 4px;
  white-space: nowrap;
}

.card-badge.live {
  background: rgba(0, 180, 42, 0.1);
  color: var(--color-success);
}

.card-badge.live::before {
  content: '';
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--color-success);
  margin-right: 6px;
  vertical-align: middle;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.4;
  }
}

.total-block {
  text-align: center;
  padding: 24px 0;
  border-bottom: 1px solid var(--border);
  margin-bottom: 16px;
}

.total-num {
  font-size: 28px;
  font-weight: 600;
  color: var(--color-primary);
  line-height: 36px;
  font-family: 'DIN Alternate', 'Roboto', inherit;
}

.total-label {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 8px;
}

.total-meta {
  font-size: 12px;
  color: var(--text-disabled);
  margin-top: 4px;
}

.type-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.type-row {
  display: grid;
  grid-template-columns: 12px 1fr auto;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background 200ms;
}

.type-row:hover {
  background: var(--bg-secondary);
}

.type-dot {
  width: 10px;
  height: 10px;
  border-radius: 2px;
}

.type-name {
  font-size: 14px;
  color: var(--text-primary);
}

.type-meta {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.type-num {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  font-family: 'DIN Alternate', 'Roboto', inherit;
}

.type-pct {
  font-size: 12px;
  color: var(--text-disabled);
}

.pie-desc {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 16px;
}

.pie-desc strong {
  color: var(--text-primary);
  font-weight: 500;
}

.num-hl {
  color: var(--color-primary);
  font-weight: 500;
  font-family: 'DIN Alternate', 'Roboto', inherit;
}

.num-hl.muted {
  color: var(--text-disabled);
}

.pie-layout {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: 24px;
  align-items: center;
  padding: 8px 0;
}

@media (max-width: 700px) {
  .pie-layout {
    grid-template-columns: 1fr;
  }
}

.pie-wrap {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pie-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  pointer-events: none;
}

.pie-center-num {
  font-size: 28px;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 36px;
  font-family: 'DIN Alternate', 'Roboto', inherit;
}

.pie-center-label {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 4px;
}

.pie-slice {
  transition: opacity 200ms;
  cursor: pointer;
}

.pie-slice:hover {
  opacity: 0.8;
}

.pie-legend {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.legend-item {
  display: grid;
  grid-template-columns: 12px 1fr auto;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 200ms;
  border: 1px solid transparent;
}

.legend-item:hover {
  background: var(--bg-secondary);
}

.legend-item.active {
  background: rgba(37, 99, 235, 0.06);
  border-color: var(--color-primary);
}

.legend-swatch {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.legend-text {
  font-size: 14px;
  color: var(--text-primary);
}

.legend-item.active .legend-text {
  font-weight: 500;
}

.legend-num {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  font-family: 'DIN Alternate', 'Roboto', inherit;
}

.perm-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

@media (max-width: 1200px) {
  .perm-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 700px) {
  .perm-grid {
    grid-template-columns: 1fr;
  }
}

.perm-card {
  background: var(--bg-card);
  border-radius: 8px;
  box-shadow: var(--shadow-1);
  padding: 16px;
  cursor: pointer;
  transition: all 200ms;
  border: 1px solid transparent;
}

.perm-card:hover {
  box-shadow: var(--shadow-2);
}

.perm-card.active {
  border-color: var(--color-primary);
  box-shadow: var(--shadow-2);
}

.perm-card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.perm-card-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-primary);
  line-height: 24px;
}

.perm-card-icon {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  background: rgba(37, 99, 235, 0.08);
  display: flex;
  align-items: center;
  justify-content: center;
}

.perm-card-icon svg {
  width: 16px;
  height: 16px;
  stroke: var(--color-primary);
  fill: none;
  stroke-width: 1.8;
}

.perm-total {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 8px;
}

.perm-total-num {
  font-size: 28px;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 36px;
  font-family: 'DIN Alternate', 'Roboto', inherit;
}

.perm-total-label {
  font-size: 12px;
  color: var(--text-secondary);
}

.perm-coverage {
  font-size: 12px;
  color: var(--color-success);
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border);
}

.perm-coverage::before {
  content: '↑ ';
}

.perm-detail {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.perm-row {
  display: grid;
  grid-template-columns: 10px 1fr auto;
  align-items: center;
  gap: 10px;
  font-size: 12px;
}

.perm-row-dot {
  width: 8px;
  height: 8px;
  border-radius: 2px;
}

.perm-row-name {
  color: var(--text-secondary);
}

.perm-row-num {
  font-size: 14px;
  color: var(--text-primary);
  font-weight: 500;
  font-family: 'DIN Alternate', 'Roboto', inherit;
}
</style>
