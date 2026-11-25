import { invoke } from './client'
export const loginAdmin = data => invoke('1-01', data)
export const loginUser = data => invoke('1-02', data)
export const logout = () => invoke('1-03')
export const getUserInfo = () => invoke('1-05')
export const updateProfile = data => invoke('1-06', data)
export const updatePassword = data => invoke('1-07', data)
export const submitRecruitApply = data => invoke('1-30', data)
export const registerByRecruitCode = data => invoke('1-31', data)
export const getRecruitApplyPage = params => invoke('1-32', params)
export const getRecruitApply = id => invoke('1-33', { id })
export const assessRecruitApply = (id, data) => invoke('1-34', { id, ...data })
export const resendRecruitCode = id => invoke('1-35', { id })
export default { loginAdmin, loginUser, logout, getUserInfo, updateProfile, updatePassword, submitRecruitApply, registerByRecruitCode, getRecruitApplyPage, getRecruitApply, assessRecruitApply, resendRecruitCode }
