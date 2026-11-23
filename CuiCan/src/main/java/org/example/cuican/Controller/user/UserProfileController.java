package org.example.cuican.Controller.user;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.cuican.Bean.Result;
import org.example.cuican.Bean.user.UserProfile;
import org.example.cuican.Bean.user.UserInfo;
import org.example.cuican.Service.user.UserProfileService;
import org.example.cuican.utils.UserContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.LinkedHashMap;

import java.util.Map;

/**
 * 用户个人信息控制器
 * 提供当前登录用户的个人信息管理功能：获取信息、更新信息、头像上传、相册管理、账户充值、修改密码等
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserProfileController {

    @Resource
    private UserProfileService userProfileService;

    /**
     * 获取当前登录用户个人信息接口
     * 接口路径：/api/user/profile
     * 请求方式：GET
     * @return 统一响应结果，返回当前用户完整个人信息
     */
    @GetMapping("/profile")
    public Result profile() {
        Long userId = UserContext.getUserId();
        log.info("获取用户信息 userId={}", userId);
        return Result.success(userProfileService.getProfile(userId));
    }

    /**
     * 更新当前登录用户个人信息接口
     * 接口路径：/api/user/profile
     * 请求方式：PUT
     * @param userProfile 用户个人信息对象，包含更新后的信息
     * @return 统一响应结果，更新成功返回成功消息
     */
    @PutMapping("/profile")
    public Result update(@RequestBody UserProfile userProfile) {
        Long userId = UserContext.getUserId();
        userProfile.setId(userId);
        log.info("更新用户信息 userId={}", userId);
        userProfileService.updateProfile(userProfile);
        log.info("更新成功 userId={}", userId);
        return Result.success("更新成功");
    }

    /**
     * 上传/替换用户头像接口
     * 接口路径：/api/user/avatar
     * 请求方式：POST
     * @param file 上传的头像图片文件，前端裁剪后上传PNG格式
     * @return 统一响应结果，上传成功返回新头像URL；失败返回错误信息
     */
    @PostMapping("/avatar")
    public Result uploadAvatar(@RequestParam("file") MultipartFile file) {
        Long userId = UserContext.getUserId();
        try {
            String avatar = userProfileService.updateAvatar(userId, file);
            Map<String, String> data = new LinkedHashMap<>();
            data.put("avatar", avatar);
            return Result.success(data);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("头像上传失败 userId={}", userId, e);
            return Result.fail("头像上传失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户扩展信息接口
     * 更新简介、职业、个性签名、地址、生日等扩展信息
     * 接口路径：/api/user/info
     * 请求方式：PUT
     * @param info 用户扩展信息对象
     * @return 统一响应结果，保存成功返回成功消息；失败返回错误信息
     */
    @PutMapping("/info")
    public Result updateInfo(@RequestBody UserInfo info) {
        Long userId = UserContext.getUserId();
        try {
            userProfileService.updateUserInfo(userId, info);
            return Result.success("保存成功");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 用户相册上传照片接口
     * 接口路径：/api/user/photo
     * 请求方式：POST
     * @param file 上传的照片文件
     * @return 统一响应结果，上传成功返回照片URL；失败返回错误信息
     */
    @PostMapping("/photo")
    public Result uploadPhoto(@RequestParam("file") MultipartFile file) {
        Long userId = UserContext.getUserId();
        try {
            String url = userProfileService.addPhoto(userId, file);
            Map<String, String> data = new LinkedHashMap<>();
            data.put("url", url);
            return Result.success(data);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("相册上传失败 userId={}", userId, e);
            return Result.fail("相册上传失败：" + e.getMessage());
        }
    }

    /**
     * 删除用户相册照片接口
     * 接口路径：/api/user/photo
     * 请求方式：DELETE
     * @param url 要删除的照片URL
     * @return 统一响应结果，删除成功返回成功消息；失败返回错误信息
     */
    @DeleteMapping("/photo")
    public Result deletePhoto(@RequestParam String url) {
        Long userId = UserContext.getUserId();
        try {
            userProfileService.deletePhoto(userId, url);
            return Result.success("删除成功");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 账户充值接口（模拟入账，开发测试用）
     * 接口路径：/api/user/recharge
     * 请求方式：POST
     * @param body 请求体，包含amount充值金额
     * @return 统一响应结果，充值成功返回最新账户余额；失败返回错误信息
     */
    @PostMapping("/recharge")
    public Result recharge(@RequestBody Map<String, Object> body) {
        Long userId = UserContext.getUserId();
        Object amt = body == null ? null : body.get("amount");
        if (amt == null || amt.toString().trim().isEmpty()) return Result.fail("请输入充值金额");
        BigDecimal amount;
        try {
            amount = new BigDecimal(amt.toString().trim());
        } catch (NumberFormatException e) {
            return Result.fail("充值金额格式不正确");
        }
        try {
            BigDecimal balance = userProfileService.recharge(userId, amount);
            Map<String, String> data = new LinkedHashMap<>();
            data.put("balance", balance.toPlainString());
            return Result.success(data);
        } catch (RuntimeException e) {
            log.error("账户充值失败 userId={} amount={}", userId, amt, e);
            String msg = e.getMessage();
            if (msg == null || msg.isEmpty()) msg = e.getClass().getSimpleName();
            return Result.fail("充值失败：" + msg);
        }
    }

    /**
     * 获取当前用户充值记录列表接口
     * 接口路径：/api/user/recharge/list
     * 请求方式：GET
     * @return 统一响应结果，返回当前用户的所有充值记录列表
     */
    @GetMapping("/recharge/list")
    public Result rechargeList() {
        Long userId = UserContext.getUserId();
        return Result.success(userProfileService.rechargeRecords(userId));
    }

    /**
     * 修改密码接口
     * 接口路径：/api/user/password
     * 请求方式：PUT
     * @param body 请求体，包含oldPassword旧密码和newPassword新密码
     * @return 统一响应结果，修改成功返回成功消息；验证失败返回错误信息
     */
    @PutMapping("/password")
    public Result updatePassword(@RequestBody Map<String, String> body) {
        Long userId = UserContext.getUserId();
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        log.info("修改密码: userId={}", userId);
        userProfileService.updatePassword(userId, oldPassword, newPassword);
        return Result.success("修改成功");
    }
}
