package com.yue.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yue.usercenter.common.ErrorCode;
import com.yue.usercenter.exception.BusinessException;
import com.yue.usercenter.mapper.UserMapper;
import com.yue.usercenter.model.domain.User;
import com.yue.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yue.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 *这部分是服务实现的具体代码
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    /*在给出的代码中，UserMapper被注入到UserServiceImpl服务类中。
    这是为了使UserServiceImpl能够访问数据库并进行与User实体相关的数据库操作。
    说白了usermapper就相当于数据库*/
    @Resource
    private UserMapper userMapper;


    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "yue";

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    // 编写玩register代码实现之后，如果要进行单元测试，alt+enter点击userRegister选中创建test就能跳转到test编辑test
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String username, String email, String phone) {
        // 1. 校验    利用的是apache commons-lang3的包来做验证逻辑
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, username, email, phone)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }
        // 账户不能重复
        // QueryWrapper是Mybatis-Plus的查询构造器，用于构建SQL查询条件。
        QueryWrapper<User> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("userAccount", userAccount);
        long userAccountCount = userMapper.selectCount(userAccountQueryWrapper);
        if (userAccountCount > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        // 用户名不重复
        // QueryWrapper是Mybatis-Plus的查询构造器，用于构建SQL查询条件。
        QueryWrapper<User> usernameQueryWrapper = new QueryWrapper<>();
        usernameQueryWrapper.eq("username", username);
        long usernameCount = userMapper.selectCount(usernameQueryWrapper);
        if (usernameCount > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名重复");
        }

        // 2. 加密  DigestUtils是spring自带的工具包进行密码加密的作用
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone(phone);
        // 因为User类中返回的userid是long类型，如果返回为null就会报错，因此要判断一下是否save成功
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }
        return user.getId();
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount does not match userPassword");
            return null;
        }
        // 3. 用户脱敏   生成用户信息，除了密码之外的。脱敏就是脱去敏感信息 防止用户的敏感信息泄露给前端
        //      getSafetyUser方法在下方直接定义
        User safetyUser = getSafetyUser(user);
        // 4. 记录用户的登录态   session中的attribute可以当成一个map
        //     USER_LOGIN_STATE这种constant可以统一在constant文件夹中的UserConstant来进行定义
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        return safetyUser;
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

}

// [加入我们](https://yupi.icu) 从 0 到 1 项目实战，经验拉满！10+ 原创项目手把手教程、7 日项目提升训练营、1000+ 项目经验笔记、60+ 编程经验分享直播