package com.yue.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yue.usercenter.common.BaseResponse;
import com.yue.usercenter.common.ErrorCode;
import com.yue.usercenter.common.ResultUtils;
import com.yue.usercenter.model.domain.User;
import com.yue.usercenter.model.domain.request.UserRegisterRequest;
import com.yue.usercenter.exception.BusinessException;
import com.yue.usercenter.model.domain.request.UserLoginRequest;
import com.yue.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.yue.usercenter.contant.UserConstant.ADMIN_ROLE;
import static com.yue.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口    controller就是API层的设计
 * RestController使用于编写restful风格的API，返回值默认为json类型
 *
 * idea 上方工具栏中的tools中有HTTP client可以用来做API测试
 */
@RestController   // 用这个注解之后这部分所有API返回值都是application JSON, 这个API也是RESTFul API
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService; // 因为API要调用业务逻辑

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    // 这里的@RequestBody UserRegisterRequest userRegisterRequest是在model中自定义的request文件夹中定义的，用来全局定义
    // 接收来自前端的数据格式的
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 校验
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String username = userRegisterRequest.getUsername();
        String email = userRegisterRequest.getEmail();
        String phone = userRegisterRequest.getPhone();
        // controller层倾向于对请求参数本身的校验，不涉及业务逻辑本身（越少越好）
        // service层是对业务逻辑额的校验，有可能被controller之外的调用
        //这就是为什么在service层和这里的controller层都进行了同样的校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, username, email, phone)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, username, email, phone);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前用户
     *
     * @param request
     * @return
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
        // TODO 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }


    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "Admin access required");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 因为开启了mybatis的逻辑删除，所以这里直接自动转变为逻辑删除了
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

}
