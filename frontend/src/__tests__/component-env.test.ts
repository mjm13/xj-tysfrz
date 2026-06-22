import { describe, it, expect } from 'vitest'
import { defineComponent, h, ref } from 'vue'
import { mount } from '@vue/test-utils'

// 验证组件测试环境（jsdom + @vue/test-utils）可用：挂载、交互、DOM 断言。
describe('component test environment', () => {
  it('mounts a component and reacts to clicks', async () => {
    const Counter = defineComponent({
      setup() {
        const count = ref(0)
        return () =>
          h('button', { onClick: () => count.value++ }, `count: ${count.value}`)
      },
    })

    const wrapper = mount(Counter)
    expect(wrapper.text()).toBe('count: 0')

    await wrapper.trigger('click')
    expect(wrapper.text()).toBe('count: 1')
  })
})
