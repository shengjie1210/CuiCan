package org.example.cuican.Service.user;

import org.example.cuican.Bean.user.RechargeRecord;
import org.example.cuican.Bean.user.UserInfo;
import org.example.cuican.Bean.user.UserProfile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户个人信息服务接口
 * 提供用户个人信息获取、更新、头像上传、相册管理、账户充值等功能
 */
public interface UserProfileService {
    /**
     * 获取指定用户个人信息
     * @param userId 用户ID
     * @return 用户个人信息，包含基本信息、扩展信息、标签、照片等
     */
    UserProfile getProfile(Long userId);

    /**
     * 更新用户个人信息
     * @param userProfile 用户个人信息对象
     */
    void updateProfile(UserProfile userProfile);

    /**
     * 修改用户密码
     * @param userId 用户ID
     * @param oldPassword 旧密码明文
     * @param newPassword 新密码明文
     */
    void updatePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 上传/替换头像，返回可访问的相对路径
     * @param userId 用户ID
     * @param file 上传的头像文件
     * @return 可访问的头像相对路径，如 /avatar/xxx.png
     * @throws Exception 文件保存异常
     */
    String updateAvatar(Long userId, MultipartFile file) throws Exception;

    /**
     * 更新用户扩展信息，包括简介、职业、个性签名、地址、生日
     * @param userId 用户ID
     * @param info 用户扩展信息对象
     */
    void updateUserInfo(Long userId, UserInfo info);

    /**
     * 上传相册图片，返回可访问的相对路径
     * @param userId 用户ID
     * @param file 上传的图片文件
     * @return 可访问的图片相对路径
     * @throws Exception 文件保存异常
     */
    String addPhoto(Long userId, MultipartFile file) throws Exception;

    /**
     * 删除相册照片，同时删除数据库记录和磁盘文件
     * @param userId 用户ID
     * @param url 照片URL路径
     */
    void deletePhoto(Long userId, String url);

    /**
     * 账户充值（模拟入账，开发测试用）
     * @param userId 用户ID
     * @param amount 充值金额
     * @return 充值后的账户余额
     */
    BigDecimal recharge(Long userId, BigDecimal amount);

    /**
     * 获取用户充值记录列表
     * @param userId 用户ID
     * @return 用户充值记录列表
     */
    List<RechargeRecord> rechargeRecords(Long userId);
}
