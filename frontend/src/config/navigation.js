import pages from './pages.json'
export { pages }
export const groups = ['dashboard', 'members', 'repo', 'budget', 'asset', 'seat', 'honor', 'duty', 'common']
export const dictionaryTypes = {
  '1-25': 'registerApplyStatus', '2-07': null, '3-01': 'budgetPeriodStatus', '3-10': 'budgetPeriodStatus', '3-06': 'budgetApplyStatus', '3-20': 'budgetApplyStatus',
  '4-01': 'assetStatus', '4-08': 'assetStatus', '4-06': 'assetBorrowStatus', '4-20': 'assetBorrowStatus', '5-05': 'budgetApplyStatus', '5-18': 'budgetApplyStatus', '5-21': 'seatSwapStatus', '5-23': 'seatSwapStatus',
  '6-01': 'honorAlumniStatus', '6-05': 'honorAlumniStatus', '6-15': 'honorAlumniStatus', '6-17': 'honorAlumniStatus', '7-01': 'dutyStatus', '7-17': 'dutyStatus', '7-08': 'dutyPlanStatus', '7-06': 'dutySwapStatus', '8-06': 'noticeStatus'
}
export const keyAliases = { id: ['userId','roleId','accountId','projectId','periodId','itemId','applyId','expenseId','assetId','borrowId','inventoryId','areaId','seatId','swapId','alumniId','recordId','planId','scheduleId','noticeId','videoId','messageId','inviteCodeId','inviteId','logId','id'] }
