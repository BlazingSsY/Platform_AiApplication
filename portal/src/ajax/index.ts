/*
 * @Description:
 * @Author:
 * @Date: 2021-03-03 09:25:04
 * @LastEditTime: 2021-11-24 18:20:23
 * @LastEditTime: 2021-06-03 18:22:19
 */
import { get, post, put, del, patch,postFromData } from './ajax';

const fnRes = (res: any) => JSON.parse(res.request.response);

// ------------------------------登录---------------------------------
export const usersLogin = (data: any) => post(`/portal/v1/users/login?captcha=${data.captcha}`, data).then(fnRes); // 登录
export const getUserInfo = (data: any) => get(`/portal/v1/users/${data.userID}`, data).then(fnRes); // 查询用户
export const getUserLoginInfo = (data: any) => get(`/portal/v1/users/my-login-info`, data).then(fnRes); // 获取当前用户登录信息
export const usersPasswordReset = (data: any) => post(`/portal/v1/users/password-change`, data).then(fnRes); // 重新设置密码

// -----------------------------管理----------------------------------
// ----部门管理----
export const getGroupsPages = (data: any) => get(`/portal/v1/departments/pages`, data).then(fnRes); // 初始加载页面获取tableList
export const getGroupsTree = (data: any) => get(`/portal/v1/departments/tree`, data).then(fnRes); // 初始加载页面获取tableList

export const getGroupsTreeForLeader = (data: any) => get(`/portal/v1/departments/current-user-tree`, data).then(fnRes); // 领导查看部门树
export const delDepartmentList = (data: any) => patch(`/portal/v1/departments`, data).then(fnRes); // 部门删除
export const addDepartmentList = (data: any) => post(`/portal/v1/departments`, data).then(fnRes); // 部门新增/groups
export const editDepartmentList = (data: any) => put(`/portal/v1/departments/${data.id}`, data).then(fnRes); // 部门新增/groups
export const getGroupInfo = (data: any) => get(`/portal/v1/departments/${data.id}`, data).then(fnRes); // 部门详情获取
export const getDepartmentSimpleItems = (data: any) => get(`/portal/v1/departments/simple-items`, data).then(fnRes); // 获取部门简单列表
export const setDeptUser = (data: any) => put(`/portal/v1/users/department-relations`, data).then(fnRes); // 部门用户设置

// ----用户管理----
export const getUserPages = (data: any) => get(`/portal/v1/users`, data).then(fnRes); // 用户页面初始化吊用
export const delUserList = (data: any) => patch(`/portal/v1/users`, data).then(fnRes); // 用户删除接口
export const addUserList = (data: any) => post(`/portal/v1/users`, data).then(fnRes); // 用户新增
export const editUserList = (data: any) => put(`/portal/v1/users/${data.id}`, data).then(fnRes); // users 编辑
export const editUserLock = (data: any) => put(`/portal/v1/users/lock-status/${data.id}`, data).then(fnRes); // users 启用禁用
export const usersPasswordchange = (data: any) => post(`/portal/v1/users/password`, data).then(fnRes); // 修改用户密码

// ----公用 用户新建----
export const UserListRole = (data: any) => get(`/portal/v1/roles`, data).then(fnRes); // 根据用户ID查询用户拥有的角色 根据用户ID查询用户拥有的角色, 如果用户ID为空, 返回所有角色
export const UserListGroup = (data: any) => get(`/portal/v1/departments`, data).then(fnRes); // 获取全部部门list
// -------------日志管理----
export const getLogPages = (data: any) => get(`/portal/v1/log-records/pages`, data).then(fnRes); // 页面初始化调用日志管理接口

// logsManage / views
// -----------角色管理----
export const selectRoleList = (data: any) => get(`/portal/v1/roles/select-roles/pages`, data).then(fnRes); // 获取可选角色列表
export const addUserRole = (data: any) => post(`/portal/v1/roles`, data).then(fnRes); // 新增角色
export const editRole = (data: any) => put(`/portal/v1/roles/${data.id}`, data).then(fnRes); // 修改角色
export const setUserRolePermissions = (data: any) => put(`/portal/v1/rolepowers`, data).then(fnRes); // 角色分配权限
export const getUserRoleList = (data: any) => get(`/portal/v1/roles/pages`, data).then(fnRes); // 分页获取角色列表
export const getUserPermissions = (data: any) => get(`/portal/v1/powers/select-tree?roleId=${data.id}`, data).then(fnRes); // 根据角色id查询对应角色权限  或者id为空查询所有菜单及权限
export const delRoleList = (data: any) => patch(`/portal/v1/roles`, data).then(fnRes); // 角色删除接口
export const roleDetail = (data: any) => get(`/portal/v1/roles/${data.id}`, data).then(fnRes); // 角色详情
export const roleUserEditor = (data: any) => put(`/portal/v1/userroles`, data).then(fnRes); // 修改用户角色

// ----头像上传----
export const usersUploadProfile = (data: any) => post(`/portal/v1/users/upload-profile`, data).then(fnRes); // 头像上传
export const commonImgPicture = (data: any) => get(`/portal/v1/common/picture?objectId=${data}`, data).then(fnRes); // 图片展示

// ----菜单管理----
export const menuList = (data: any) => get(`/portal/v1/powers/select-tree`, data).then(fnRes); // 权限树
export const menuAdd = (data: any) => post(`/portal/v1/powers`, data).then(fnRes); // 新增
export const menuEditor = (data: any) => put(`/portal/v1/powers/${data.id}`, data).then(fnRes); // 编辑
export const menuDelete = (data: any) => patch(`/portal/v1/powers`, data).then(fnRes); // 批量删除

// ----用户组管理----
export const groupList = (data: any) => get(`/portal/v1/groups/select-tree`, data).then(fnRes); // 可选组树
export const groupAdd = (data: any) => post(`/portal/v1/groups`, data).then(fnRes); // 新增
export const groupEditor = (data: any) => put(`/portal/v1/groups/${data.id}`, data).then(fnRes); // 编辑
export const groupDelete = (data: any) => del(`/portal/v1/groups?ids=${data.ids}`, data).then(fnRes); // 批量删除
export const groupLook = (data: any) => get(`/portal/v1/groups/${data.id}`, data).then(fnRes); // 查看详情
export const GroupUserList = (data: any) => get(`/portal/v1/users/pages`, data).then(fnRes); // 分页查询用户列表
export const GroupUserEditor = (data: any) => put(`/portal/v1/usergroups`, data).then(fnRes); // 修改用户组


// 验证码
export const getCaptcha = (data: any) => get(`/portal/v1/captcha`, data).then(fnRes); // 验证码


export const downloadFile = (data: any) => get(`/portal/common/v1/storage/download/${data.fileId}`, data, { 'responseType': `blob` }); 

export const storageUpload = (data: any) => postFromData(`/portal/common/v1/storage/upload`, data).then(fnRes); // 新增
export const storageDownload = (data: any) => get(`/portal/common/v1/storage/download/${data}`, { 'responseType': `blob` }); 

export const getDepartmentsSimpleItems = (data: any) => get(`/portal/v1/departments/child-simple-items`, data).then(fnRes);