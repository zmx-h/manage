import { invoke } from './client'
export const loginAdmin = data => invoke('1-01', data)
export const loginUser = data => invoke('1-02', data)
export const logout = () => invoke('1-03')
export const getUserInfo = () => invoke('1-05')
export const updateProfile = data => invoke('1-06', data)
export const updatePassword = data => invoke('1-07', data)
export default { loginAdmin, loginUser, logout, getUserInfo, updateProfile, updatePassword }
