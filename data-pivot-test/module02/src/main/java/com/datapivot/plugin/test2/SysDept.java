package com.datapivot.plugin.test2;
import com.datapivot.plugin.test2.base.TenantEntity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "sys_dept")
public class SysDept extends TenantEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    @Id
    private Long deptId;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 部门名称
     */
    @Column(name = "dept_name")
    private String deptName;

    private String notExistField;
    @Column(name = "not_exist_field2")
    private String notExistField2;

}