package org.example.cuican.Controller.user;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.Result;
import org.example.cuican.Bean.user.Address;
import org.example.cuican.Service.user.AddressService;
import org.example.cuican.utils.UserContext;
import org.springframework.web.bind.annotation.*;

/**
 * 用户收货地址控制器
 * 提供用户收货地址的增删改查功能，每个用户可以管理多个收货地址
 */
@RestController
@RequestMapping("/address")
public class AddressController {

    @Resource
    private AddressService addressService;

    /**
     * 获取当前登录用户的所有收货地址接口
     * 接口路径：/address/list
     * 请求方式：GET
     * @return 统一响应结果，返回当前用户的所有收货地址列表
     */
    @GetMapping("/list")
    public Result list() {
        Long userId = UserContext.getUserId();
        return Result.success(addressService.list(userId));
    }

    /**
     * 添加收货地址接口
     * 接口路径：/address/add
     * 请求方式：POST
     * @param address 收货地址实体，包含地址详细信息
     * @return 统一响应结果，添加成功返回成功消息
     */
    @PostMapping("/add")
    public Result add(@RequestBody Address address) {
        addressService.add(address);
        return Result.success();
    }

    /**
     * 更新收货地址接口
     * 接口路径：/address/update
     * 请求方式：POST
     * @param address 收货地址实体，必须包含ID字段，更新对应ID的地址信息
     * @return 统一响应结果，更新成功返回成功消息
     */
    @PostMapping("/update")
    public Result update(@RequestBody Address address) {
        addressService.update(address);
        return Result.success();
    }

    /**
     * 删除收货地址接口
     * 接口路径：/address/delete
     * 请求方式：POST
     * @param id 要删除的收货地址ID
     * @return 统一响应结果，删除成功返回成功消息
     */
    @PostMapping("/delete")
    public Result delete(Long id) {
        addressService.delete(id);
        return Result.success();
    }
}
