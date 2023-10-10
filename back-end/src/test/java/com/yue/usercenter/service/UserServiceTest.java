package com.yue.usercenter.service;



import com.yue.usercenter.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 用户服务测试
 *
 */
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    /**
     * 测试添加用户
     */
    @Test
    public void testAddUser() {
        User user = new User();
        user.setUsername("dogYupi");
        user.setUserAccount("123");
        user.setAvatarUrl("https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("123");
        user.setEmail("456");
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    // https://www.code-nav.cn/

    /**
     * 测试更新用户
     */
    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("dogYupi");
        user.setUserAccount("123");
        user.setAvatarUrl("https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("123");
        user.setEmail("456");
        boolean result = userService.updateById(user);
        Assertions.assertTrue(result);
    }

    /**
     * 测试删除用户
     */
    @Test
    public void testDeleteUser() {
        boolean result = userService.removeById(1L);
        Assertions.assertTrue(result);
    }

    // https://space.bilibili.com/12890453/

    /**
     * 测试获取用户
     */
    @Test
    public void testGetUser() {
        User user = userService.getById(1L);
        Assertions.assertNotNull(user);
    }

    /**
     * 测试用户注册
     */
    @Test
    void userRegister() {
        String userAccount = "yueniu";
        String userPassword = "";
        String checkPassword = "123456";
        String username = "dvalover";
        String email = "123";
        String phone = "12345666";
        //1. 校验password非空
        long result = userService.userRegister(userAccount, userPassword, checkPassword, username, email, phone);
        Assertions.assertEquals(-1, result);
        //2. 校验账户过短
        userAccount = "yu";
        result = userService.userRegister(userAccount, userPassword, checkPassword, username, email, phone);
        Assertions.assertEquals(-1, result);
        //3.校验密码不能小于8位
        userAccount = "yueniu";
        userPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword, username, email, phone);
        Assertions.assertEquals(-1, result);
        //4.校验不能包含特殊字符
        userAccount = "yue niu";
        userPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword, username, email, phone);
        Assertions.assertEquals(-1, result);
        //5. 二次密码不同
        checkPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword, username, email, phone);
        Assertions.assertEquals(-1, result);
        //6.密码和原密码相同
        userAccount = "dogYupi";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword, username, email, phone);
        Assertions.assertEquals(-1, result);
        userAccount = "yueniu";
        result = userService.userRegister(userAccount, userPassword, checkPassword, username, email, phone);
        Assertions.assertEquals(-1, result);
    }
}