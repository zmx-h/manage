import request from '@/api/request'

// 数据字典：对应接口 8-04 GET /common/dict/{type}
// 字典项由后端 sys_dict 表下发，前端缓存 1 小时，避免重复请求。
const DICT_CACHE = new Map()
const CACHE_TTL = 60 * 60 * 1000

/**
 * 按字典类型取字典项，返回 [{ label, value }]。
 *
 * <p>骨架阶段未实现：接入后端后按下面注释补全即可。
 */
export async function loadDict(dictType, force = false) {
  // TODO 待实现（接口 8-04）：
  //   const cached = DICT_CACHE.get(dictType)
  //   if (!force && cached && Date.now() - cached.time < CACHE_TTL) return cached.list
  //   const list = await request.get(`/common/dict/${dictType}`)
  //   DICT_CACHE.set(dictType, { list, time: Date.now() })
  //   return list
  void request
  void force
  return DICT_CACHE.get(dictType)?.list || []
}

/** 按值取字典文案，取不到返回原值。 */
export function dictLabel(dictType, value) {
  const list = DICT_CACHE.get(dictType)?.list || []
  const hit = list.find((item) => item.value === value)
  return hit ? hit.label : value
}
