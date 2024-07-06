package com.datapivot.plugin.test3;

import com.datapivot.plugin.test3.base.TenantEntity;

public class SysDept extends TenantEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 部门名称
     */
    private String deptName;

    private String notExistField;
    private String notExistField2;

}