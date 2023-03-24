package com.luckcat.controller;



import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luckcat.dto.UserLogin;
import com.luckcat.dto.UserRegister;
import com.luckcat.pojo.User;
import com.luckcat.service.UserService;
import com.luckcat.utils.LuckResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.luckcat.utils.LuckResult.success;

/**
 * (User)表控制层
 *
 * @author makejava
 * @since 2023-03-23 09:06:07
 */
@RestController
@RequestMapping("user")
@Api("用户接口")
public class UserController  {
    @Resource
    private UserService userService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param user 查询实体
     * @return 所有数据
     */
    @GetMapping
    public LuckResult selectAll(Page<User> page, User user) {
        return success(this.userService.page(page, new QueryWrapper<>(user)));
    }

    /**
     * 登录用户接口
     *
     * @param userLogin 实体对象
     * @return 登录结果
     */
    @ApiOperation("登录用户接口")
    @GetMapping("/loginUser")
    public LuckResult selectOne(@RequestBody UserLogin userLogin) {
        if(userLogin.getAccount() != null && userLogin.getPassword() != null){
            userService.LoginUser(userLogin);
            return success("登录成功的喔！");
        }
        return success("账号和密码不能为空的喔！");
    }

    /**
     * 注册用户接口
     *
     * @param userRegister 实体对象
     * @return 新增结果
     */
    @ApiOperation("新增用户接口")
    @PostMapping("/registerUser")
    public LuckResult insert(@RequestBody UserRegister userRegister) {
        //邮箱的正则校验
        String EmailMatch = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        if(userRegister.getEmail() != null && userRegister.getEmail().matches(EmailMatch)){
            if(userRegister.getPassword() != null && userRegister.getUsername() != null){
                SaResult saResult = userService.addUser(userRegister);
                return success(saResult);
            }
        }
        return LuckResult.error("参数不合法，请重新传递");
    }

    /**
     * 修改数据
     *
     * @param user 实体对象
     * @return 修改结果
     */
    @PutMapping
    public LuckResult update(@RequestBody User user) {
        return success(this.userService.updateById(user));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public LuckResult delete(@RequestParam("idList") List<Long> idList) {
        return success(this.userService.removeByIds(idList));
    }
}

