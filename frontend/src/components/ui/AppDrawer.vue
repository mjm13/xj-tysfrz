<script setup lang="ts">
defineProps<{
  modelValue: boolean
  title: string
  size?: number
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>()

function close() {
  emit('update:modelValue', false)
}
</script>

<template>
  <Teleport to="body">
    <Transition name="drawer">
      <div v-if="modelValue" class="drawer-overlay" @click.self="close">
        <aside class="drawer" :style="{ width: `${size ?? 480}px` }">
          <header class="drawer-head">
            <h2>{{ title }}</h2>
            <button type="button" class="close-btn" aria-label="关闭" @click="close">×</button>
          </header>
          <div class="drawer-body">
            <slot />
          </div>
          <footer v-if="$slots.footer" class="drawer-foot">
            <slot name="footer" />
          </footer>
        </aside>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.drawer-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
  z-index: 1000;
  display: flex;
  justify-content: flex-end;
}

.drawer {
  height: 100%;
  background: var(--bg-card);
  box-shadow: var(--shadow-3);
  display: flex;
  flex-direction: column;
  max-width: 100%;
}

.drawer-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border);
}

.drawer-head h2 {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-primary);
}

.close-btn {
  font-size: 24px;
  line-height: 1;
  color: var(--text-disabled);
  padding: 4px;
  border-radius: 4px;
}

.close-btn:hover {
  color: var(--text-primary);
  background: var(--bg-secondary);
}

.drawer-body {
  flex: 1;
  overflow: auto;
  padding: 20px;
}

.drawer-foot {
  padding: 12px 20px;
  border-top: 1px solid var(--border);
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.drawer-enter-active,
.drawer-leave-active {
  transition: opacity 200ms;
}

.drawer-enter-active .drawer,
.drawer-leave-active .drawer {
  transition: transform 200ms;
}

.drawer-enter-from,
.drawer-leave-to {
  opacity: 0;
}

.drawer-enter-from .drawer,
.drawer-leave-to .drawer {
  transform: translateX(100%);
}
</style>
