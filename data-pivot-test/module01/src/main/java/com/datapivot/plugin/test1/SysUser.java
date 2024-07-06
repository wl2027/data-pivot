//package com.datapivot.plugin.test1;
//
//import com.baomidou.mybatisplus.annotation.*;
//import com.datapivot.plugin.test1.base.TenantEntity;
//
//@TableName("sys_user")
//public class SysUser extends TenantEntity {
//
//    /**
//     * 用户ID
//     */
//    @TableId(value = "user_id")
//    private Long userId;
//
//    /**
//     * 部门ID
//     */
//    private Long deptId;
//
//    /**
//     * 用户账号
//     */
//    private String userName;
//
//    /**
//     * 用户昵称
//     */
//    private String nickName;
//
//    /**
//     * 用户类型（sys_user系统用户）
//     */
//    private String userType;
//
//    /**
//     * 用户邮箱
//     */
//    private String email;
//
//    /**
//     * 手机号码
//     */
//    private String phonenumber;
//
//    /**
//     * 用户性别
//     */
//    private String sex;
//
//    /**
//     * 用户头像
//     */
//    private Long avatar;
//
//    /**
//     * 密码
//     */
//    @TableField(
//        insertStrategy = FieldStrategy.NOT_EMPTY,
//        updateStrategy = FieldStrategy.NOT_EMPTY,
//        whereStrategy = FieldStrategy.NOT_EMPTY
//    )
//    private String password;
//
//    /**
//     * 帐号状态（0正常 1停用）
//     */
//    private String status;
//
//    /**
//     * 删除标志（0代表存在 2代表删除）
//     */
//    @TableLogic
//    private String delFlag;
//
//    /**
//     * 最后登录IP
//     */
//    private String loginIp;
//
//    /**
//     * 最后登录时间
//     */
//    private Date loginDate;
//
//    /**
//     * 备注
//     */
//    private String remark;
//
//
//    public SysUser(Long userId) {
//        this.userId = userId;
//    }
//
//}
