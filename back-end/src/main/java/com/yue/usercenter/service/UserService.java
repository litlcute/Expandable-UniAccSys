package com.yue.usercenter.service;

import com.yue.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务--业务逻辑定义
 * 这部分只写接口接收和返回的参数是什么，不实现功能
 * 具体实现写在同一级中的impl文件夹中
 */
public interface UserService extends IService<User> {

    /**
     *
     * @param userAccount 账号
     * @param userPassword 密码
     * @param checkPassword 二次密码
     * @param username 用户名
     * @param email 邮箱
     * @param phone 电话
     * @return 脱敏后的用户信息
     */
    // 写完userRegister业务逻辑之后，选中userRegister点alt+enter选implement就能自动跳转到代码实现部分进行代码编写
    long userRegister(String userAccount, String userPassword, String checkPassword, String username, String email, String phone);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     *    如何记录用户登陆状态： 连接服务器之后，得到一个session状态，返回给前端
     *    登陆成功之后，得到了登陆成功的session，并且给该session设置一些值（例如用户信息），
     *    返回给前端一个设置cookie的命令
     *    前端接收到后端的命令之后，设置cookie，保存到浏览器内
     *    前端再次请求后端的时候（从相同的域名），在请求头中带上cookie请求
     *    后端拿到前端传来的cookie，找到对应的session
     *    后端从session中取出基于该session存储的变量（例如用户的登录信息）
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);
}
