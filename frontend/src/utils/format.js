// 格式化工具：与后端约定保持一致（见接口文档 2.5 节）
/**
 * 格式化日期时间字符串（后端已按 yyyy-MM-dd HH:mm:ss 返回，此函数仅做空值兜底）。
 */
export function formatDateTime(value) {
  return value || '-'
}

/** 金额格式化：两位小数，千分位。 */
export function formatAmount(value) {
  if (value === null || value === undefined || value === '') {
    return '-'
  }
  return Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

/** 布尔语义（1 是 / 0 否）转中文。 */
export function formatYesNo(value) {
  return value === 1 ? '是' : '否'
}
