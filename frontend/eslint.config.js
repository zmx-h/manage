import js from '@eslint/js'
import pluginVue from 'eslint-plugin-vue'

// 骨架阶段的宽松规则：只做语法级检查，避免阻塞开发。
// 全局忽略：构建产物与依赖目录不属于源码检查范围。
const ignores = { ignores: ['dist/**', 'node_modules/**', 'public/**'] }

export default [
  ignores,
  js.configs.recommended,
  ...pluginVue.configs['flat/recommended'],
  {
    files: ['**/*.{js,vue}'],
    languageOptions: {
      ecmaVersion: 'latest',
      sourceType: 'module',
      globals: {
        window: 'readonly',
        document: 'readonly',
        localStorage: 'readonly',
        console: 'readonly',
        process: 'readonly'
      }
    },
    rules: {
      'vue/multi-word-component-names': 'off',
      'no-unused-vars': 'warn'
    }
  }
]
