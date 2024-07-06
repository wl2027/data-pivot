package com.datapivot.plugin.test1;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.datapivot.plugin.test1.base.TenantEntity;

@TableName("sys_dept")
public class SysDept extends TenantEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    @TableId(value = "dept_id")
    private Long deptId;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 部门名称
     */
    @TableField(value = "dept_name")
    private String deptName;

    private String notExistField;
    @TableField(value = "not_exist_field2")
    private String notExistField2;

}
