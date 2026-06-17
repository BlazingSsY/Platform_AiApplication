package com.starmol.portal.backend.service.user.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.starmol.portal.backend.bean.bo.ImportUserBO;
import com.starmol.portal.backend.bean.dto.ChangeUserLockStatusDTO;
import com.starmol.portal.backend.bean.dto.RelationDTO;
import com.starmol.portal.backend.bean.dto.ResetPasswordDTO;
import com.starmol.portal.backend.bean.dto.UpdatePasswordDTO;
import com.starmol.portal.backend.bean.dto.UserLoginDTO;
import com.starmol.portal.backend.bean.vo.SimpleItemVO;
import com.starmol.portal.backend.bean.vo.UserListVO;
import com.starmol.portal.backend.bean.vo.UserLoginVO;
import com.starmol.portal.backend.bean.vo.UserVO;
import com.starmol.portal.backend.common.ResponseWrapper;
import com.starmol.portal.backend.common.UniUserDTO;
import com.starmol.portal.backend.config.token.JwtTokenComponent;
import com.starmol.portal.backend.constant.SysRoleTypeEnum;
import com.starmol.portal.backend.exception.AuthException;
import com.starmol.portal.backend.exception.KnowException;
import com.starmol.portal.backend.model.Department;
import com.starmol.portal.backend.model.Role;
import com.starmol.portal.backend.model.User;
import com.starmol.portal.backend.model.UserRole;
import com.starmol.portal.backend.model.base.DeleteDTO;
import com.starmol.portal.backend.model.base.ServiceItem;
import com.starmol.portal.backend.repository.UserMapper;
import com.starmol.portal.backend.service.base.impl.BaseCascadeServiceImpl;
import com.starmol.portal.backend.service.user.DepartmentService;
import com.starmol.portal.backend.service.user.PowerService;
import com.starmol.portal.backend.service.user.RoleService;
import com.starmol.portal.backend.service.user.UserRoleService;
import com.starmol.portal.backend.service.user.UserService;
import com.starmol.portal.backend.utils.BaseModelBeanUtils;
import com.starmol.portal.backend.utils.DownloadUtil;
import com.starmol.portal.backend.utils.HttpRequestUtil;
import com.starmol.portal.backend.utils.PasswordUtil;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl extends BaseCascadeServiceImpl<UserMapper, User> implements UserService {

    private final DepartmentService departmentService;
    private final UserRoleService userRoleService;
    private final JwtTokenComponent jwtTokenComponent;
    private final PowerService powerService;
    private final RoleService roleService;

    private final ObjectMapper objectMapper;


    @Lazy
    public UserServiceImpl(DepartmentService departmentService, JwtTokenComponent jwtTokenComponent, UserRoleService userRoleService, PowerService powerService, RoleService roleService, ObjectMapper objectMapper) {
        this.departmentService = departmentService;
        this.jwtTokenComponent = jwtTokenComponent;
        this.userRoleService = userRoleService;
        this.powerService = powerService;
        this.roleService = roleService;
        this.objectMapper = objectMapper;
    }

    public void downLoadTemplate(HttpServletResponse response) throws Exception{
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("templates/用户信息.xlsx");
        DownloadUtil.download(response, inputStream, "用户信息.xlsx");
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public Integer importUserInfo(MultipartFile file) throws Exception {
        try {
            List<ImportUserBO> userImportBOList = importUserData(file);
            checkIfLoginNameExits(userImportBOList);
            //数据入库,
            this.saveBatch(checkAndConvertUserImportBOListToUserList(userImportBOList));
            return userImportBOList.size();
        } catch (Exception e) {
            log.error(String.format("import Info error:%s", e.getMessage()), e);
            throw e;
        }
    }

    private List<User> checkAndConvertUserImportBOListToUserList(List<ImportUserBO> userImportBOList) {
        //获取当前用户所属部门的部门的Map
        List<SimpleItemVO> departmentList = departmentService.getDepartmentSimpleItemAndChild(HttpRequestUtil.getDepartmentId());
        //将departmentList转成已name为key的Map,这里假定同一个部门下的部门名称不会重复
        Map<String, SimpleItemVO> departmentMap = departmentList.stream().collect(Collectors.toMap(SimpleItemVO::getName, item -> item, (existing, replacement) -> existing));
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < userImportBOList.size() ; i++) {
            User newUser = new User();
            newUser = BaseModelBeanUtils.initModelBaseInfo(newUser);
            newUser.setLoginName(userImportBOList.get(i).getLoginName());
            newUser.setName(userImportBOList.get(i).getName());
            newUser.setPassword(PasswordUtil.generateHash(newUser.getLoginName(), PasswordUtil.ENCODE_INITIALIZE_PSW));
            if (StringUtils.isBlank(userImportBOList.get(i).getDepartmentName())){
                newUser.setDepartmentId(HttpRequestUtil.getDepartmentId());//默认为当前部门
            }
            else if (Objects.nonNull(departmentMap.get(userImportBOList.get(i).getDepartmentName()))) {
                newUser.setDepartmentId(departmentMap.get(userImportBOList.get(i).getDepartmentName()).getId());
            } else {
                throw new KnowException(String.format("部门名称为:{}的部门不存在", userImportBOList.get(i).getDepartmentName()));
            }
            newUser.setEmail(userImportBOList.get(i).getEmail());
            newUser.setTelephone(userImportBOList.get(i).getTelephone());
            userList.add(newUser);
        }
        return userList;
    }

    /***检查导入数据中的用户登录名是否已存在**/
    private void checkIfLoginNameExits(List<ImportUserBO> userImportBOList) {
        List<String> loginNameList = userImportBOList.stream().map(ImportUserBO::getLoginName).collect(Collectors.toList());
        List<User> existUserList = this.list(new QueryWrapper<User>().in("login_name", loginNameList));
        if (CollectionUtils.isNotEmpty(existUserList)) {
            String loginNameString = existUserList.stream().map(User::getLoginName).collect(Collectors.joining(","));
            throw new KnowException(String.format("用户登录名:{} 已存在", loginNameString));
        }
    }

    /***导入 "用户信息表" sheet页的数据**/
    private List<ImportUserBO> importUserData(MultipartFile file) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            List<ImportUserBO> list = EasyExcel.read(inputStream)
                    .sheet() // 默认读取第一个sheet，也可以使用.sheet(index/name)指定sheet
                    .head(ImportUserBO.class)
                    .doReadSync(); // 同步读取数据

            // 校验name、loginName和email字段不为空
            for (ImportUserBO user : list) {
                if (StringUtils.isBlank(user.getName())) {
                    throw new KnowException("用户姓名不能为空");
                }
                if (StringUtils.isBlank(user.getLoginName())) {
                    throw new KnowException("登录名不能为空");
                }
                if (StringUtils.isBlank(user.getEmail())) {
                    throw new KnowException("邮箱不能为空");
                }
            }

            // 校验loginName不重复
            List<String> loginNames = list.stream().map(ImportUserBO::getLoginName).collect(Collectors.toList());
            List<String> duplicateLoginNames = loginNames.stream()
                    .collect(Collectors.groupingBy(name -> name, Collectors.counting()))
                    .entrySet().stream()
                    .filter(entry -> entry.getValue() > 1)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            if (!duplicateLoginNames.isEmpty()) {
                throw new KnowException("导入表中存在重复的用户登录名: " + String.join(", ", duplicateLoginNames));
            }

            return list;
        } catch (Exception e) {
            log.error("import user Info error:{}", e.getMessage(), e);
            throw e;
        }
    }


    @SneakyThrows
    private void checkParent(User user) {
        Object departmentIdValue = BeanUtils.getSimpleProperty(user, "departmentId");
        Long relationFieldValue = (Objects.nonNull(departmentIdValue) && NumberUtils.isParsable(departmentIdValue.toString())) ? Long.parseLong(BeanUtils.getSimpleProperty(user, "departmentId")) : 0;
        //if (StringUtils.isEmpty(relationFieldValue) || (departmentService.getById(relationFieldValue) == null))  //增加子数据时，关联字段必填用此代码
        if (!relationFieldValue.equals(0L) && departmentService.getById(relationFieldValue) == null) {
            throw new KnowException("数据异常: 当前数据关联的数据已不存在");
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public User saveAndReturnObjectWithRelationCheck(User user) {
        if (org.apache.commons.lang3.StringUtils.isBlank(user.getLoginName()) || org.apache.commons.lang3.StringUtils.isBlank(user.getPassword())) {
            throw new KnowException("登陆名或密码不能为空");
        }
        checkParent(user);
        //创建用户时,需要指定密码(可随机生成), 需要指定下次登录是否需要用户自己修改密码
        //用户的Password保存"登录名+MD5(输入的密码)" 的Hash值(此Hash值使用随机盐)
        user.setPassword(PasswordUtil.generateHash(user.getLoginName(), user.getPassword()));
        // 要求用户名称不能重复的保存方式
        return this.saveAndReturnObject(user, "login_name", "登陆名");
    }



    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void removeUsersWithTransaction(List<DeleteDTO> removeUsers) {
        removeObjectsByObjectList(removeUsers);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public User updateByIdAndReturnObjectWithRelationCheck(User user) {
        checkParent(user);
        // 要求用户名称不能重复的保存方式
        //return this.updateByIDAndReturnObject(user, "login_name", "登陆名"); //这里不能使用通用代码, 因为前端不能保存密码,可以考虑简化  --- yxp
        final String loginName = user.getLoginName();
        final User targetLoginNameOne = this.getOne(Wrappers.<User>lambdaQuery().eq(User::getLoginName, loginName).ne(User::getId, user.getId()).last("LIMIT 1"));
        if (Objects.nonNull(targetLoginNameOne)) {
            throw new KnowException("登陆名已存在");
        }
        User savedUser = this.getById(user.getId());
        //只复制可修改的属性
        savedUser.setDepartmentId(user.getDepartmentId())
                .setName(user.getName())
                .setProfile(user.getProfile())
                .setWorkNo(user.getWorkNo())
                .setEmail(user.getEmail())
                .setTelephone(user.getTelephone())
                .setComments(user.getComments())
                .setVersion(user.getVersion());
        if (org.apache.commons.lang3.StringUtils.isNotBlank(user.getPassword())) { //密码不为空时重新计算,
            savedUser.setPassword(PasswordUtil.generateHash(user.getLoginName(), user.getPassword()));
        }
        this.updateByIDAndReturnObject(savedUser);
        return user;
    }

    private User createUserFromRelationDto(RelationDTO relationDto){
        User user = this.getById(relationDto.getId());
        user.setDepartmentId(Objects.isNull(relationDto.getOwnerId()) ? 0 : relationDto.getOwnerId());
        user.setVersion(relationDto.getVersion());
        return user;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void updateDepartmentRelationsByIdWithRelationCheck(List<RelationDTO> relations) {
        List<User> users = relations.stream().map(this::createUserFromRelationDto).toList();
        for (User user : users) {
            checkParent(user);
            this.updateByIDAndReturnObject(user);
        }
    }
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void changeUserLockStatusById(Long id, ChangeUserLockStatusDTO changeUserLockStatusDTO) {
        User user = this.getById(id);
        user.setLocked(changeUserLockStatusDTO.getNewStatus());
        user.setVersion(changeUserLockStatusDTO.getVersion());
        this.updateByIDAndReturnObject(user);
        // final LambdaUpdateWrapper<User> set = Wrappers.<User>lambdaUpdate().eq(User::getId, id).set(User::getLocked, newLockStatus);//这种模式跳过了乐观锁
        // this.update(set);
    }


    @Override
    public void updatePasswordWithCheck(UpdatePasswordDTO updatePasswordDTO) {
        User user = findByLoginName(updatePasswordDTO.getLoginName());
        if (Objects.isNull(user)) {
            throw new KnowException("用户名或密码错误");
        }
        final boolean verify = PasswordUtil.verify(updatePasswordDTO.getOldPassword(), user.getPassword());
        if (BooleanUtils.isFalse(verify)) {
            throw new KnowException("密码错误，请重新输入");
        }
        String newHash = PasswordUtil.generateHash(user.getLoginName(), updatePasswordDTO.getPassword());
        user.setPassword(newHash);
        //更新密码后把"下次登录是否需要修改密码"设置为false
        user.setNeedChangePassword(false);
        this.updateByIDAndReturnObject(user);
    }

    @Override
    public String updatePassword(ResetPasswordDTO resetPasswordDTO) {
        User user = findByLoginName(resetPasswordDTO.getLoginName());
        if (Objects.isNull(user)) {
            throw new KnowException("用户名或密码错误");
        }
        String newPassword = resetPasswordDTO.getPassword();
        String newHash = PasswordUtil.generateHash(user.getLoginName(), newPassword);
        user.setPassword(newHash);
        //更新密码后把"下次登录是否需要修改密码"设置为true
        user.setNeedChangePassword(true);
        this.updateByIDAndReturnObject(user);
        return newPassword;
    }


    @Override
    public void clearRedundantFieldValue(IPage<UserVO> userVoByPage) {
        userVoByPage.getRecords().forEach(userVO -> {
            userVO.setPassword(null);
            userVO.setProfile(null);
            userVO.setWorkNo(null);
        });
    }


    @Override
    public User findByLoginName(String name) {
        return getOne(Wrappers.<User>lambdaQuery().eq(User::getLoginName, name));
    }

    @Override
    public IPage<UserVO> getUserPageWithDepartmentName(IPage<UserVO> page, Long departmentId, String loginName, String userName, String email,List<Long> hasRoleUserIdList) {
        return this.getBaseMapper().getUserVO(page, departmentId, loginName, userName, email, hasRoleUserIdList);
    }

    @Override
    public List<UserVO> getUserVOListByIds(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        return this.getBaseMapper().getUserVOListByIds(userIds);
    }

    @Override
    public List<UserVO> getUserVOListByRoleId(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        return this.getBaseMapper().getUserVOListByRoleId(roleId);
    }


    @Override
    public List<User> getUserListByGroupId(String name, String groupId, String loginName) {
        return this.getBaseMapper().getUserListByGroupId(name, groupId, loginName);
    }

    @Override
    public IPage<User> getUserListPageByGroupId(IPage<User> page, String name, Long groupId, String loginName) {
        return this.getBaseMapper().getUserListPageByGroupId(page, name, groupId, loginName);
    }

    @Override
    public IPage<User> getExcludedUserListPageByGroupId(IPage<User> page, String name, Long groupId, String loginName) {
        return this.getBaseMapper().getExcludedUserListPageByGroupId(page, name, groupId, loginName);
    }

    @Override
    public List<User> getUserListByRoleId(String name, Long roleId, String loginName) {
        return this.getBaseMapper().getUserListByRoleId(name, roleId, loginName);
    }

    @Override
    public IPage<User> getUserListPageByRoleId(IPage<User> page, String name, Long roleId, String loginName) {
        return this.getBaseMapper().getUserListPageByRoleId(page, name, roleId, loginName);
    }

    @Override
    public IPage<User> getExcludedUserListPageByRoleId(IPage<User> page, String name, Long roleId, String loginName) {
        return this.getBaseMapper().getExcludedUserListPageByRoleId(page, name, roleId, loginName);
    }

    @Override
    public QueryWrapper<User> completeQueryWrapper(Long departmentId, QueryWrapper<User> queryWrapper) {
        if (Objects.nonNull(departmentId)) {
            if (!departmentId.equals(0L)) {
                queryWrapper.lambda().eq(User::getDepartmentId, departmentId).orderBy(true, true, User::getName);
            } else {
                queryWrapper.lambda().eq(User::getDepartmentId, 0L).or().isNull(User::getDepartmentId).orderBy(true, true, User::getName);
            }
        }
        else {
            queryWrapper.lambda().orderBy(true, true, User::getName);
        }
        return queryWrapper;
    }

    @Override
    public List<UserListVO> listUserByFilter(Long departmentId){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper = completeQueryWrapper(departmentId, queryWrapper);
        List<User> userList = list(queryWrapper);
        return userList.stream().map(user -> objectMapper.convertValue(user, UserListVO.class)).toList();
    }


    @Override
    public ResponseWrapper<UserLoginVO> login(UserLoginDTO userLoginDTO) {
        ResponseWrapper<UserLoginVO> wrapper = ResponseWrapper.success();
        User user = findByLoginName(userLoginDTO.getLoginName());
        if (user == null) {
            throw new KnowException("用户名或密码错误，登录失败");
        }

        final boolean verify = PasswordUtil.verify(userLoginDTO.getPassword(), user.getPassword());
        if (BooleanUtils.isFalse(verify)) {
            throw new KnowException("用户名或密码错误，登录失败");
        }

        if (PasswordUtil.ENCODE_INITIALIZE_PSW.equals(userLoginDTO.getPassword())) {
            wrapper.setSucc(false);
            wrapper.setCode(1001);
            wrapper.setMsg("请修改密码后重新登录");
            return wrapper;
        }

        if (user.getLocked()) {
            wrapper.setSucc(false);
            wrapper.setMsg("用户被锁定，请联系管理员");
            return wrapper;
        }

        if (userRoleService.count(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getUserId, user.getId())) == 0) {
            throw new KnowException("用户无角色，请联系管理员进行角色添加");
        }

        if (Objects.isNull(user.getDepartmentId()) || user.getDepartmentId().equals(0L)) {
            throw new KnowException("用户无部门，请联系管理员进行部门分配");
        }

        String token = generateTokenByUser(user);
        String refreshToken = jwtTokenComponent.generateRefreshToken(user.getLoginName(), user.getName());
        user.setPassword(null);
        UserLoginVO userLoginVO = getUserLoginVoFromUser(user);
        wrapper.setContent(userLoginVO);
        wrapper.addOption("token", token);
        wrapper.addOption("refreshToken", refreshToken);
        return wrapper;
    }

    @Override
    public UserLoginVO getUserLoginVoFromUser(User user) {
        UserLoginVO userLoginVO = UserLoginVO.fromUser(user);
        userLoginVO.setPowerAliasTree(powerService.getPowersByUserId(user.getId()));
        List<Role> roleList = roleService.getRoleListByUserId(null, user.getId());
        if (!CollectionUtils.isEmpty(roleList)) {
            userLoginVO.setRole(roleList.get(0));
        }
        Department department = departmentService.getById(user.getDepartmentId());
        if (Objects.nonNull(department)) {
            userLoginVO.setDepartmentName(department.getName());
        }
        return userLoginVO;
    }

    @Override
    public ResponseWrapper<Void> refreshToken(Map<String, String> header) {
        String refreshTokenStr = header.get(JwtTokenComponent.AUTHORIZATION.toLowerCase());

        if (refreshTokenStr == null || !refreshTokenStr.startsWith(JwtTokenComponent.AUTH_PREFIX)) {
            throw new AuthException(JwtTokenComponent.TOKEN_INVALID);
        }

        String refreshToken = refreshTokenStr.substring(JwtTokenComponent.AUTH_PREFIX.length());
        String loginname = jwtTokenComponent.parseRefreshToken(refreshToken);
        if (!org.apache.commons.lang3.StringUtils.isEmpty(loginname)) {
            User user = findByLoginName(loginname);
            if (user == null) {
                throw new AuthException(JwtTokenComponent.TOKEN_INVALID);
            }
            ResponseWrapper<Void> wrapper = new ResponseWrapper<>();
            String newToken = generateTokenByUser(user);
            String newRefreshToken = jwtTokenComponent.generateRefreshToken(user.getLoginName(),user.getName());
            wrapper.addOption("token", newToken);
            wrapper.addOption("refreshToken", newRefreshToken);

            return wrapper;
        } else {
            throw new AuthException(JwtTokenComponent.TOKEN_INVALID);
        }
    }


    /**
     * 通过用户对象生成Token
     *
     * @param user 用户对象
     * @return 生成的Token
     */
    private String generateTokenByUser(User user) {
        final Map<String, Object> userTokenAttribute = getUserTokenAttribute(user);
        return jwtTokenComponent.generateToken(userTokenAttribute);
    }

    public Map<String, Object> getUserTokenAttribute(User userProfile) {
        SysRoleTypeEnum sysRoleTypeEnum = roleService.getSysRoleTypeByUserId(userProfile.getId());
        final UniUserDTO tokenAttribute = new UniUserDTO()
                .setId(userProfile.getId())
                .setDepartmentId(userProfile.getDepartmentId())
                .setName(userProfile.getName())
                .setLoginName(userProfile.getLoginName())
                .setSysRoleType(sysRoleTypeEnum);
        return objectMapper.convertValue(tokenAttribute, TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, Object.class));
    }

    /**
     * 有附加属性，此方法必须重载
     */
    @Override
    public List<ServiceItem> getAllAdditionalAttributeSevice() {
        if (Objects.isNull(additionalAttributeServiceList)) {
            additionalAttributeServiceList = new ArrayList<>();
            additionalAttributeServiceList.add(new ServiceItem("user_id", userRoleService));
        }
        return additionalAttributeServiceList;
    }


    @Override
    public User getUserWithoutSensitiveInfo(String loginName) {
        User user = findByLoginName(loginName);
        //清除不需要的冗余字段
        user.setPassword(null);
        user.setProfile(null);
        user.setPassword(null);
        user.setWorkNo(null);
        return user;
    }

    @Override
    public IPage<UserVO> getUserListByPage(Long pageNumber, Long pageSize, Boolean includeDepartmentName, Long departmentId, Long groupId, Long roleId, Boolean include, String loginName, String userName, String email) {
        IPage<UserVO> userVoByPage;
        if (includeDepartmentName) {
            //获取拥有角色id为roleId的用户
            List<Long> hasRoleUserIdList = null;
            if (Objects.nonNull(roleId)) {
                List<UserRole> userRoleList = userRoleService.list(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getRoleId, roleId));
                hasRoleUserIdList = userRoleList.stream().map(UserRole::getUserId).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(hasRoleUserIdList)) {
                    return new Page<>();
                }
            }
            userVoByPage = this.getUserPageWithDepartmentName(new Page<>(pageNumber, pageSize), departmentId, loginName, userName, email, hasRoleUserIdList);
        } else {
            IPage<User> userByPage;
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().like(StringUtils.isNotBlank(loginName), User::getLoginName, loginName);
            queryWrapper.lambda().like(StringUtils.isNotBlank(userName), User::getName, userName);
            queryWrapper.lambda().like(StringUtils.isNotBlank(email), User::getEmail, email);

            // include为空时按照部门条件查询， include不为空时按照用户组或角色条件查询
            if (Objects.isNull(include)) {
                this.completeQueryWrapper(departmentId, queryWrapper);
                userByPage = this.page(new Page<>(pageNumber, pageSize), queryWrapper);
            } else {
                // groupId不为空时按照用户组条件查询，groupId为空时按照角色条件查询
                if (Objects.nonNull(groupId)) {
                    if (groupId.equals(0L)) {
                        userByPage = this.page(new Page<>(pageNumber, pageSize), queryWrapper);
                    } else {
                        if (include) {
                            userByPage = this.getUserListPageByGroupId(new Page<>(pageNumber, pageSize), null, groupId, loginName);
                        } else {
                            userByPage = this.getExcludedUserListPageByGroupId(new Page<>(pageNumber, pageSize), null, groupId, loginName);
                        }
                    }
                } else {
                    if (Objects.isNull(roleId) || roleId.equals(0L)) {
                        userByPage = this.page(new Page<>(pageNumber, pageSize), queryWrapper);
                    } else {
                        if (include) {
                            userByPage = this.getUserListPageByRoleId(new Page<>(pageNumber, pageSize), null, roleId, loginName);
                        } else {
                            userByPage = this.getExcludedUserListPageByRoleId(new Page<>(pageNumber, pageSize), null, roleId, loginName);
                        }
                    }
                }
            }
            userVoByPage = userByPage.convert(i -> objectMapper.convertValue(i, UserVO.class));
        }
        userVoByPage =  AddRoleInfo(userVoByPage);
        // 清除在列表中不显示的冗余字段
        this.clearRedundantFieldValue(userVoByPage);
        return userVoByPage;
    }


    private IPage<UserVO> AddRoleInfo(IPage<UserVO> userPage) {
        List<Long> userIds = userPage.getRecords().stream().map(User::getId).collect(Collectors.toList());
        List<UserRole> userRoleList =  CollectionUtils.isNotEmpty(userIds) ? userRoleService.list(Wrappers.<UserRole>lambdaQuery().in(UserRole::getUserId, userIds)) : Collections.emptyList();
        List<Long>  roleIds = userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        final Map<Long, List<UserRole>> userRoleMap = CollectionUtils.isNotEmpty(userIds) ? userRoleService.list(Wrappers.<UserRole>lambdaQuery().in(UserRole::getUserId, userIds)).stream().collect(Collectors.groupingBy(UserRole::getUserId)) : new HashMap<>();
        final Map<Long, Role> roleMap = CollectionUtils.isNotEmpty(roleIds) ? roleService.list(Wrappers.<Role>lambdaQuery().in(Role::getId, roleIds)).stream().collect(Collectors.toMap(Role::getId, role -> role)) : new HashMap<>();
        IPage<UserVO>  userVoByPage =   userPage.convert(user -> {
            List<UserRole> currentUserRoleList = userRoleMap.get(user.getId());
            if (CollectionUtils.isNotEmpty(currentUserRoleList)) {
                user.setDefaultRoleId(userRoleList.get(0).getRoleId());
                user.setDefaultRoleName(Optional.ofNullable(roleMap.get(currentUserRoleList.get(0).getRoleId())).map(Role::getName).orElse( ""));
            }
            return user;
        });
        return userVoByPage;
    }


}
