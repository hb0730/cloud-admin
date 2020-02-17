package com.hb0730.cloud.admin.server.user.system.controller;


import cn.hutool.core.lang.ObjectId;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hb0730.cloud.admin.common.web.controller.AbstractBaseController;
import com.hb0730.cloud.admin.common.web.response.ResultJson;
import com.hb0730.cloud.admin.common.web.utils.ResponseResult;
import com.hb0730.cloud.admin.server.user.system.model.entity.SystemUserEntity;
import com.hb0730.cloud.admin.server.user.system.service.ISystemUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static com.hb0730.cloud.admin.common.util.RequestMappingConstants.USER_SERVER_REQUEST;

/**
 * <p>
 * 系统用户  前端控制器
 * </p>
 *
 * @author bing_huang
 * @since 2020-02-15
 */
@RestController
@RequestMapping(USER_SERVER_REQUEST)
public class SystemUserController extends AbstractBaseController<SystemUserEntity> {
    @Autowired
    private ISystemUserService systemUserService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/save")
    @Override
    public ResultJson save(@RequestBody SystemUserEntity target) {
        //参数校验
        if (Objects.isNull(target)) {
            return ResponseResult.resultFall("参数为空");
        }
        if (StringUtils.isBlank(target.getLogin()) && StringUtils.isBlank(target.getLoginEmail())) {
            return ResponseResult.resultFall("用户账号或者email为空");
        }
        if (StringUtils.isBlank(target.getLoginPasswd())) {
            return ResponseResult.resultFall("用户密码为空");
        }
        //判断用户名或者邮箱是否已绑定
        SystemUserEntity userEntity = getUserEntity(target.getLogin());
        if (!Objects.isNull(userEntity)) {
            return ResponseResult.resultFall("用户账号或者邮箱已绑定");
        }
        userEntity = getUserEntity(target.getLoginEmail());
        if (!Objects.isNull(userEntity)) {
            return ResponseResult.resultFall("用户账号或者邮箱已绑定");
        }
        //生成salt
        String salt = ObjectId.next();
        target.setSalt(salt);
        //用户加密
//        String password = CryptoMD5Utils.encode(target.getLoginPasswd(), salt);
//        target.setLoginPasswd(password);
        // spring security
        String password = passwordEncode(target.getLoginPasswd());
        target.setLoginPasswd(password);
        systemUserService.save(target);
        return ResponseResult.resultSuccess("新增成功");
    }

    @GetMapping("/delete/{id}")
    @Override
    public ResultJson delete(@PathVariable Object id) {
        if (Objects.isNull(id)) {
            return ResponseResult.resultFall("参数id为空");
        }
        //删除清空缓存
        SystemUserEntity entity = new SystemUserEntity();
        entity.setId(Long.valueOf(id.toString()));
        entity.setIsEnabled(0);
        UpdateWrapper<SystemUserEntity> updateWrapper = new UpdateWrapper<>(entity);
        systemUserService.remove(updateWrapper);
        return ResponseResult.resultSuccess("修改成功");
    }

    @Override
    public ResultJson submit(SystemUserEntity target) {
        return null;
    }

    @GetMapping("/{id}")
    @Override
    public ResultJson gitObject(@PathVariable Object id) {
        if (Objects.isNull(id)) {
            return ResponseResult.resultFall("参数id为空");
        }
        SystemUserEntity entity = systemUserService.getById(id.toString());
        return ResponseResult.resultSuccess(entity);
    }

    /**
     * <p>
     * 用户登录
     * </p>
     *
     * @param login    用户名
     * @param password 用户密码
     * @return 是否成功
     */
    @GetMapping("/login")
    public ResultJson login(@RequestParam("login") String login, @RequestParam("password") String password) {
        if (StringUtils.isBlank(login)) {
            return ResponseResult.resultFall("请输入用户名或者登陆邮箱");
        }
        if (StringUtils.isBlank(password)) {
            return ResponseResult.resultFall("请输入密码");
        }
        //根据用户账号
        SystemUserEntity userEntity = getUserEntity(login);

        if (Objects.isNull(userEntity)) {
            return ResponseResult.resultFall("登陆账号不存在");
        }
        //校验密码是否正确
//        String salt = userEntity.getSalt();
//        boolean matches = CryptoMD5Utils.matches(password, salt, userEntity.getLoginPasswd());

        // spring security
        boolean matches = passwordMatches(password, userEntity.getLoginPasswd());
        if (!matches) {
            return ResponseResult.resultFall("密码不正确");
        }
        return ResponseResult.resultSuccess(userEntity);
    }

    /**
     * <p>
     * 用户修改
     * </p>
     *
     * @return
     */
    public ResultJson update() {
        return ResponseResult.resultFall(null);
    }

    /**
     * <p>
     * 根据登录名查找
     * </p>
     *
     * @param login 登录账号
     * @return 用户
     */
    @GetMapping("/findUser/{login}")
    public ResultJson findUserByLogin(@PathVariable String login) {
        SystemUserEntity userEntity = getUserEntity(login);
        return ResponseResult.resultSuccess(userEntity);
    }

    /**
     * <p>
     * 用户登出
     * </p>
     *
     * @return 是否成功
     */
    public ResultJson<String> loginOut() {
        return ResponseResult.resultSuccess("登出成功");
    }


    /**
     * <p>
     * 根据用户账号或者email得到用户
     * </p>
     *
     * @param login 账号/email
     * @return 用户
     */
    private SystemUserEntity getUserEntity(String login) {
        SystemUserEntity entity = new SystemUserEntity();
        QueryWrapper<SystemUserEntity> queryWrapper = new QueryWrapper<>(entity.setLogin(login));
        SystemUserEntity userEntity = systemUserService.getOne(queryWrapper);
        if (!Objects.isNull(userEntity)) {
            return userEntity;
        }
        entity.setLogin(null);
        entity.setLoginEmail(login);
        queryWrapper.setEntity(entity);
        userEntity = systemUserService.getOne(queryWrapper);
        if (!Objects.isNull(userEntity)) {
            return userEntity;
        }
        return null;
    }

    /**
     * <p>
     * 用户密码加密
     * </p>
     *
     * @param password 密码文明
     * @return 加密后密码
     */
    private String passwordEncode(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * <p>
     * 密码校验
     * </p>
     *
     * @param password       文明
     * @param encodePassword 加密后
     * @return 是否一致
     */
    private boolean passwordMatches(String password, String encodePassword) {
        return passwordEncoder.matches(password, encodePassword);
    }
}
