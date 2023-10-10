package com.yue.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 */
@Data   //lombok生成dataset的方法
public class UserRegisterRequest implements Serializable {
    //serialVersionUID防止序列化过程中出现冲突
    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 校验密码
     */
    private String checkPassword;

    /**
     * 校验密码
     */
    private String username;

    /**
     * 校验密码
     */
    private String email;

    /**
     * 校验密码
     */
    private String phone;

}
