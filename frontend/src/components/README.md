# 共享组件

- `common/AppIcon.vue`：本地 SVG 图标，无远程字体依赖。
- `common/PreferencesControl.vue`：系统 / 浅色 / 深色主题，中英文切换。
- `common/ContractDialog.vue`：按生成的 API 字段渲染表单、校验并提交。
- `common/ResourceState.vue`：空数据、未实现接口、网络错误与重试。
- `common/FileUpload.vue`：大小 / 类型预校验及 multipart 上传。
- `common/TrendChart.vue`：ECharts SVG 折线图，仅使用真实响应点。
- `common/DataDetails.vue`：递归展示接口返回数据，不执行富文本 HTML。

新页面优先配置 `src/config/pages.json` 并复用工作台组件。特殊流程应增加独立组件，不要修改接口约定。
