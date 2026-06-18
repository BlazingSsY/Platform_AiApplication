package com.starmol.portal.backend.service.user.impl;

import com.google.common.collect.Lists;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.portal.backend.bean.bo.ImportDepartmentBO;
import com.starmol.portal.backend.bean.vo.DepartmentPageVO;
import com.starmol.portal.backend.bean.vo.DepartmentTreeVO;
import com.starmol.portal.backend.bean.vo.LoginNameDepartmentVO;
import com.starmol.portal.backend.bean.vo.SimpleItemVO;
import com.starmol.portal.backend.exception.KnowException;
import com.starmol.portal.backend.model.Department;
import com.starmol.portal.backend.model.User;
import com.starmol.portal.backend.model.base.CascadeServiceItem;
import com.starmol.portal.backend.model.base.DeleteDTO;
import com.starmol.portal.backend.repository.DepartmentMapper;
import com.starmol.portal.backend.service.base.BaseCascadeService;
import com.starmol.portal.backend.service.base.impl.BaseCascadeServiceImpl;
import com.starmol.portal.backend.service.user.DepartmentService;
import com.starmol.portal.backend.service.user.UserService;
import com.starmol.portal.backend.utils.BaseModelBeanUtils;
import com.starmol.portal.backend.utils.DownloadUtil;
import com.starmol.portal.backend.utils.HttpRequestUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


/**
 * DepartmentService接口的实现类
 *
 * @author : Yuexiaopeng
 * @date :2019/12/10
 **/
@Service
@Slf4j
public class DepartmentServiceImpl extends BaseCascadeServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    private final UserService userService;

    private ObjectMapper objectMapper;

    @Lazy
    public DepartmentServiceImpl(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    public void downLoadTemplate(HttpServletResponse response) throws Exception{
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("templates/部门信息.xlsx");
        DownloadUtil.download(response, inputStream, "部门信息.xlsx");
    }


    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public Integer importDepartmentInfo(MultipartFile file) throws Exception {
        try {
            List<ImportDepartmentBO> departmentImportBOList = importDepartmentData(file);
            //数据入库,
            this.saveBatch(checkAndConvertDepartmentImportBOListToDepartmentList(departmentImportBOList));
            return departmentImportBOList.size();
        } catch (Exception e) {
            log.error(String.format("import Info error:%s", e.getMessage()), e);
            throw e;
        }
    }

    private List<Department> checkAndConvertDepartmentImportBOListToDepartmentList(List<ImportDepartmentBO> departmentImportBOList) {
        //获取当前部门所属部门的部门的Map
        List<SimpleItemVO> departmentList = this.getDepartmentSimpleItemAndChild(HttpRequestUtil.getDepartmentId());
        //将departmentList转成已name为key的Map,这里假定同一个部门下的部门名称不会重复
        Map<String, SimpleItemVO> departmentMap = departmentList.stream().collect(Collectors.toMap(SimpleItemVO::getName, item -> item, (existing, replacement) -> existing));
        List<Department> addDepartmentList = new ArrayList<>();
        for (int i = 0; i < departmentImportBOList.size() ; i++) {
            Department newDepartment = new Department();
            newDepartment = BaseModelBeanUtils.initModelBaseInfo(newDepartment);
            newDepartment.setName(departmentImportBOList.get(i).getName());
            newDepartment.setFId(HttpRequestUtil.getDepartmentId());//导入的部门必须为当前用户所在部门下的科室
            newDepartment.setIsOffice(true);
            addDepartmentList.add(newDepartment);
        }
        return addDepartmentList;
    }


    /***导入 "部门信息表" sheet页的数据**/
    private List<ImportDepartmentBO> importDepartmentData(MultipartFile file) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            List<ImportDepartmentBO> list = EasyExcel.read(inputStream)
                    .sheet() // 默认读取第一个sheet，也可以使用.sheet(index/name)指定sheet
                    .head(ImportDepartmentBO.class)
                    .doReadSync(); // 同步读取数据

            // 校验name、loginName和email字段不为空
            for (ImportDepartmentBO department : list) {
                if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isBlank(department.getName())) {
                    throw new KnowException("部门姓名不能为空");
                }
            }

            // 校验部门名称不重复
            List<String> loginNames = list.stream().map(ImportDepartmentBO::getName).collect(Collectors.toList());
            List<String> duplicateLoginNames = loginNames.stream()
                    .collect(Collectors.groupingBy(name -> name, Collectors.counting()))
                    .entrySet().stream()
                    .filter(entry -> entry.getValue() > 1)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            if (!duplicateLoginNames.isEmpty()) {
                throw new KnowException("导入表中存在重复的部门名: " + String.join(", ", duplicateLoginNames));
            }

            return list;
        } catch (Exception e) {
            log.error("import department Info error:{}", e.getMessage(), e);
            throw e;
        }
    }    
    
    
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public Department saveAndReturnObjectWithFidCheck(Department department){
        Long fidFieldValue = department.getFId();
        String nameFieldValue = department.getName();
        if (Objects.nonNull(fidFieldValue) && (getById(fidFieldValue) == null)) {
            throw new KnowException("保存异常: 当前节点关联的父节点已不存在");
        }
        /**如果无同级目录下不能重名的限制，请删除此判断代码 */
        if (this.count(Wrappers.<Department>lambdaQuery().eq(Department::getName, nameFieldValue).eq(Department::getFId, fidFieldValue)) > 0) {
            throw new KnowException("保存异常: 当前目录下已存在同名的节点");
        }
        return this.saveAndReturnObject(department);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void removeDepartmentsWithTransaction(List<DeleteDTO> removeDepartments) {
        removeObjectsByObjectList(removeDepartments);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public Department updateByIdAndReturnObjectWithFidCheck(Department department) {
        Long idFieldValue = department.getId();
        Long fidFieldValue = department.getFId();
        String nameFieldValue = department.getName();
        if (Objects.nonNull(fidFieldValue) && !fidFieldValue.equals(0L) && (getById(fidFieldValue) == null)) {
            throw new KnowException("保存异常: 当前节点关联的父节点已不存在");
        }
        /**如果无同级目录下不能重名的限制，请删除此判断代码 */
        if (this.count(Wrappers.<Department>lambdaQuery().eq(Department::getName, nameFieldValue).eq(Department::getFId, fidFieldValue).ne(Department::getId, idFieldValue)) > 0) {
            throw new KnowException("保存异常: 当前目录下已存在同名的节点");
        }
        return this.updateByIDAndReturnObject(department);
    }


    private DepartmentTreeVO createDepartmentTree(Department fDepartment, List<Department> departments, List<Long> selectedIds) {
        DepartmentTreeVO departmentTreeVO = null;
        if ((Objects.nonNull(selectedIds))) {
            List<Department> childDepartmentList = departments.stream().filter(department -> fDepartment.getId().equals(department.getFId())).collect(Collectors.toList());
            if (childDepartmentList.size() > 0) {
                for (Department childDepartment : childDepartmentList) {
                    DepartmentTreeVO childDepartmentTreeVO = createDepartmentTree(childDepartment, departments, selectedIds);
                    if (Objects.nonNull(childDepartmentTreeVO)) {
                        if (Objects.isNull(departmentTreeVO)) {
                            departmentTreeVO = DepartmentTreeVO.fromDepartment(fDepartment);
                            departmentTreeVO.setChildList(new ArrayList<>());
                        }
                        departmentTreeVO.getChildList().add(childDepartmentTreeVO);
                    }
                }
            } else {
                if (selectedIds.contains(fDepartment.getId())) {
                    departmentTreeVO = DepartmentTreeVO.fromDepartment(fDepartment);
                }
            }
        }
        return departmentTreeVO;
    }


    private List<DepartmentTreeVO> convertToDepartmentTree(List<Department> departments, List<Long> selectedIds) {
        List<DepartmentTreeVO> departmentTreeVoS = new ArrayList<>();
        List<Department> topDepartmentList = departments.stream().filter(department -> Objects.isNull(department.getFId()) || department.getFId().equals(0L)).collect(Collectors.toList());

        for (Department topDepartment : topDepartmentList) {
            DepartmentTreeVO childDepartmentTreeVO = createDepartmentTree(topDepartment, departments, selectedIds);
            if (Objects.nonNull(childDepartmentTreeVO)) {
                departmentTreeVoS.add(childDepartmentTreeVO);
            }
        }
        return departmentTreeVoS;
    }

    @Override
    public List<DepartmentTreeVO> createDepartmentTreeByDepartmentNameWithOrder(String departmentName) {
        List<Department> departmentList = list(new QueryWrapper<Department>().orderBy(true, true, "name"));
        QueryWrapper<Department> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(departmentName) && !StringUtils.isEmpty(departmentName.trim())) {
            queryWrapper.lambda().like(Department::getName, departmentName);
        }
        List<Long> selectedIds = list(queryWrapper).stream().map(Department::getId).collect(Collectors.toList());
        return convertToDepartmentTree(departmentList, selectedIds);
    }


    private List<DepartmentTreeVO> convertToOnlyCurrentDepartmentAndOfficeTree(List<Department> departments, List<Long> selectedIds) {
        List<DepartmentTreeVO> departmentTreeVoS = new ArrayList<>();
        List<Department> topDepartmentList = list(new QueryWrapper<Department>().lambda().eq(Department::getId, HttpRequestUtil.getDepartmentId()));

        for (Department topDepartment : topDepartmentList) {
            DepartmentTreeVO childDepartmentTreeVO = createDepartmentTree(topDepartment, departments, selectedIds);
            if (Objects.nonNull(childDepartmentTreeVO)) {
                departmentTreeVoS.add(childDepartmentTreeVO);
            }
        }
        return departmentTreeVoS;
    }

    @Override
    public List<DepartmentTreeVO> createOnlyCurrentDepartmentAndOfficeTreeByDepartmentNameWithOrder(String departmentName) {
        List<Department> departmentList = list(new QueryWrapper<Department>().lambda().eq(Department::getIsOffice, true).orderBy(true, true, Department::getName));
        QueryWrapper<Department> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(departmentName) && !StringUtils.isEmpty(departmentName.trim())) {
            queryWrapper.lambda().like(Department::getName, departmentName);
        }
        List<Long> selectedIds = list(queryWrapper).stream().map(Department::getId).collect(Collectors.toList());
        return convertToOnlyCurrentDepartmentAndOfficeTree(departmentList, selectedIds);
    }



    @Override
    public IPage<DepartmentPageVO> getDepartmentPageWithType(IPage<DepartmentPageVO> page, String name) {
        name = Objects.isNull(name) ? name : name.trim();
        return this.getBaseMapper().getDepartmentVO(page, name);
    }


    @Override
    @SneakyThrows
    public List<SimpleItemVO> getSimpleItem(String type){
        List<Department> departmentList =   this.list(Wrappers.<Department>lambdaQuery().select(Department::getId, Department::getName).like(Objects.nonNull(type), Department::getType, type));
        return departmentList.stream().map(department -> objectMapper.convertValue(department, SimpleItemVO.class)).toList();
    }

    //根据部门id获取部门及其子孙部门的列表
    @Override
    @SneakyThrows
    public List<SimpleItemVO> getDepartmentSimpleItemAndChild(Long departmentId){
        if (Objects.isNull(departmentId)) {
            List<Department> departmentList =  this.list();
            return departmentList.stream().map(department -> objectMapper.convertValue(department, SimpleItemVO.class)).toList();
        }
        else {
            List<Department> departmentList = this.list(Wrappers.<Department>lambdaQuery().select(Department::getId, Department::getName).eq(Objects.nonNull(departmentId), Department::getId, departmentId).orderBy(true,true, Department::getName));
            List<SimpleItemVO> simpleItemVOList = new ArrayList<>();
            for (Department department : departmentList) {
                simpleItemVOList.add(objectMapper.convertValue(department, SimpleItemVO.class));
                addChildDepartment(department, simpleItemVOList);
            }
            //将simpleItemVOList按照name排序
            simpleItemVOList.sort(Comparator.comparing(SimpleItemVO::getName));
            return simpleItemVOList;
        }
    }


    private void addChildDepartment(Department fDepartment, List<SimpleItemVO> departments) {
        List<Department> childDepartmentList = this.list(Wrappers.<Department>lambdaQuery().eq(Department::getFId, fDepartment.getId()));
        if (childDepartmentList.size() > 0) {
            for (Department childDepartment : childDepartmentList) {
                addChildDepartment(childDepartment, departments);
            }
            departments.addAll(childDepartmentList.stream().map(department -> objectMapper.convertValue(department, SimpleItemVO.class)).toList());
        }
    }




    /**
     * 有关联的子数据，此方法必须重载
     */
    @Override
    public List<CascadeServiceItem> getAllChildSevice() {
        if (Objects.isNull(childServiceList)) {
            childServiceList = new ArrayList<CascadeServiceItem>();
            childServiceList.add(new CascadeServiceItem("department_id", userService));
        }
        return childServiceList;
    }

    /**
     * 重载此方法 是为了给用户抛出更友好的提示信息
     */
    @Override
    protected void checkChildEmpty(BaseCascadeService childService, String refId, List<Department> removeObjectList) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        for (Department removeObject : removeObjectList) {
            Long idValue = Long.valueOf(org.apache.commons.beanutils.BeanUtils.getSimpleProperty(removeObject, "id"));
            long childCount = childService.count(new QueryWrapper<>().eq(refId, idValue));
            if (childCount > 0) {
                String parentName = getById(removeObject.getId()).getName();
                /** 判断当前类是否为用户服务 **/
                if (childService instanceof UserService) {
                    throw new KnowException(String.format("删除失败:名称为 \"%s\" 的部门下有关联的用户数据存在，请先删除关联记录！", parentName));
                }
            }
        }
    }

    @Override
    public void getLeafDepartment(Long departmentId, List<Department> results) {
        final List<Department> departments = this.list();
        final Map<Long, Department> departmentMap = departments.stream().collect(Collectors.toMap(Department::getId, v -> v, (a, b) -> a));
        final Map<Long, List<Department>> departmentMapByParentId = departments.stream().collect(Collectors.groupingBy(Department::getFId));
        getLeafDepartment(departmentId, departmentMap, departmentMapByParentId, results);
    }

    private void getLeafDepartment(Long departmentId, Map<Long, Department> allDepartments, Map<Long, List<Department>> departmentMapByParentId, List<Department> results) {
        final Department departmentOptional = allDepartments.get(departmentId);
        if (Objects.nonNull(departmentOptional)) {
            final List<Department> childDepartmentList = departmentMapByParentId.getOrDefault(departmentOptional.getId(), Lists.newArrayList());
            if (CollectionUtils.isEmpty(childDepartmentList)) {
                results.add(departmentOptional);
            }
            for (Department childDepartment : childDepartmentList) {
                getLeafDepartment(childDepartment.getId(), allDepartments, departmentMapByParentId, results);
            }
        }
    }

    @Override
    public List<Department> getNonEmptyDepartments(Long departmentId) {
        List<Department> departmentList = list(
                Wrappers.<Department>lambdaQuery()
                        .eq(Department::getId, departmentId)
                        .or()
                        .eq(Department::getFId, departmentId)
        );
        if (CollectionUtils.isNotEmpty(departmentList)) {
            List<Long> departmentIdList = departmentList.stream().map(Department::getId).toList();
            List<User> userList = userService.list(Wrappers.<User>lambdaQuery().in(User::getDepartmentId, departmentIdList));
            Set<Long> userDepartmentIdSet = userList.stream().map(User::getDepartmentId).collect(Collectors.toSet());
            return departmentList.stream().filter(department -> userDepartmentIdSet.contains(department.getId())).toList();
        }
        else {
            return departmentList;
        }
    }

    @Override
    public List<LoginNameDepartmentVO> getLoginNameDepartmentVO(List<String> loginNameList) {
        if (loginNameList == null || loginNameList.isEmpty()) {
            return new ArrayList<>();
        }
        loginNameList = loginNameList.stream().distinct().toList();
        return this.getBaseMapper().getLoginNameDepartmentVO(loginNameList);
    }

    @Override
    public List<DepartmentTreeVO> createOnlyCurrentDepartmentAndOfficeTreeWithCreateUserName(String departmentName) {
        // 获取带有创建用户名的部门列表（只查询科室）
        List<DepartmentTreeVO> departmentList = this.getBaseMapper().getDepartmentsWithCreateUserName(true);
        
        // 构建查询条件，根据部门名称模糊查询
        QueryWrapper<Department> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(departmentName) && !StringUtils.isEmpty(departmentName.trim())) {
            queryWrapper.lambda().like(Department::getName, departmentName);
        }
        // 获取符合条件的部门ID列表
        List<Long> selectedIds = list(queryWrapper).stream().map(Department::getId).collect(Collectors.toList());
        
        // 将DepartmentTreeVO转换为Department用于树形结构构建
        List<Department> departmentModelList = departmentList.stream()
                .map(vo -> {
                    Department dept = vo.toDepartment();
                    return dept;
                })
                .collect(Collectors.toList());
        
        // 转换为树形结构
        List<DepartmentTreeVO> treeList = convertToOnlyCurrentDepartmentAndOfficeTree(departmentModelList, selectedIds);
        
        // 将createUserName填充到树形结构中
        fillCreateUserName(treeList, departmentList);
        
        return treeList;
    }

    @Override
    public List<DepartmentTreeVO> createDepartmentTreeByDepartmentNameWithOrderAndCreateUserName(String departmentName) {
        // 获取带有创建用户名的所有部门列表
        List<DepartmentTreeVO> departmentList = this.getBaseMapper().getAllDepartmentsWithCreateUserName();
        
        // 构建查询条件，根据部门名称模糊查询
        QueryWrapper<Department> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(departmentName) && !StringUtils.isEmpty(departmentName.trim())) {
            queryWrapper.lambda().like(Department::getName, departmentName);
        }
        // 获取符合条件的部门ID列表
        List<Long> selectedIds = list(queryWrapper).stream().map(Department::getId).collect(Collectors.toList());
        
        // 将DepartmentTreeVO转换为Department用于树形结构构建
        List<Department> departmentModelList = departmentList.stream()
                .map(vo -> {
                    Department dept = vo.toDepartment();
                    return dept;
                })
                .collect(Collectors.toList());
        
        // 转换为树形结构
        List<DepartmentTreeVO> treeList = convertToDepartmentTree(departmentModelList, selectedIds);
        
        // 将createUserName填充到树形结构中
        fillCreateUserName(treeList, departmentList);
        
        return treeList;
    }

    /**
     * 填充创建用户名到树形结构中
     * @param treeList 树形结构列表
     * @param sourceList 源数据列表（包含createUserName）
     */
    private void fillCreateUserName(List<DepartmentTreeVO> treeList, List<DepartmentTreeVO> sourceList) {
        if (treeList == null || sourceList == null) {
            return;
        }
        
        // 构建ID到createUserName的映射
        Map<Long, String> createUserNameMap = sourceList.stream()
                .filter(vo -> vo.getId() != null && vo.getCreateUserName() != null)
                .collect(Collectors.toMap(DepartmentTreeVO::getId, DepartmentTreeVO::getCreateUserName, (v1, v2) -> v1));
        
        // 递归填充树形结构
        fillCreateUserNameRecursive(treeList, createUserNameMap);
    }

    /**
     * 递归填充创建用户名
     * @param treeList 树形结构列表
     * @param createUserNameMap 创建用户名映射
     */
    private void fillCreateUserNameRecursive(List<DepartmentTreeVO> treeList, Map<Long, String> createUserNameMap) {
        if (treeList == null || treeList.isEmpty()) {
            return;
        }
        
        for (DepartmentTreeVO vo : treeList) {
            // 设置当前节点的createUserName
            if (vo.getId() != null && createUserNameMap.containsKey(vo.getId())) {
                vo.setCreateUserName(createUserNameMap.get(vo.getId()));
            }
            
            // 递归处理子节点
            if (vo.getChildList() != null && !vo.getChildList().isEmpty()) {
                fillCreateUserNameRecursive(vo.getChildList(), createUserNameMap);
            }
        }
    }
}
