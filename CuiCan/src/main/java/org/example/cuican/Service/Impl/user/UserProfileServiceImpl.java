package org.example.cuican.Service.Impl.user;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.cuican.Bean.auth.AuthUser;
import org.example.cuican.Bean.user.RechargeRecord;
import org.example.cuican.Bean.user.UserInfo;
import org.example.cuican.Bean.user.UserProfile;
import org.example.cuican.Mapper.auth.AuthUserMapper;
import org.example.cuican.Mapper.auth.UserTokenMapper;
import org.example.cuican.Mapper.user.TagMapper;
import org.example.cuican.Mapper.user.UserInfoMapper;
import org.example.cuican.Mapper.user.UserPhotoMapper;
import org.example.cuican.Mapper.user.UserProfileMapper;
import org.example.cuican.Mapper.user.UserRechargeMapper;
import org.example.cuican.Service.admin.SystemSettingService;
import org.example.cuican.Service.user.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户个人资料业务服务实现类
 * 实现用户资料查询、更新、密码修改、头像上传、相册管理、账户充值等功能
 */
@Slf4j
@Service
public class UserProfileServiceImpl implements UserProfileService {
    @Autowired
    private AuthUserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Resource
    private UserProfileMapper userProfileMapper;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private UserPhotoMapper userPhotoMapper;
    @Resource
    private UserRechargeMapper userRechargeMapper;
    @Resource
    private SystemSettingService systemSettingService;
    @Resource
    private TagMapper tagMapper;
    @Autowired
    private UserTokenMapper tokenMapper;

    /**
     * 获取用户完整个人资料
     * 包含基础信息、扩展信息、兴趣标签、相册照片
     * @param userId 用户ID
     * @return 用户完整资料实体，不存在则返回null
     */
    @Override
    public UserProfile getProfile(Long userId) {
        UserProfile profile = userProfileMapper.selectById(userId);
        if (profile == null) return null;
        UserInfo info = userInfoMapper.selectByUserId(userId);
        profile.setUserInfo(info);
        profile.setTags(tagMapper.selectTagNamesByUserId(userId));
        profile.setPhotos(userPhotoMapper.selectPhotosByUserId(userId));
        log.info("获取用户信息: userId={}, hasInfo={}, tags={}, photos={}",
                userId, info != null,
                profile.getTags() != null ? profile.getTags().size() : 0,
                profile.getPhotos() != null ? profile.getPhotos().size() : 0);
        return profile;
    }

    /**
     * 更新用户基础资料
     * 自动设置更新时间为当前时间
     * @param userProfile 用户资料实体，包含要更新的信息
     */
    @Override
    public void updateProfile(UserProfile userProfile) {
        userProfile.setUpdateTime(LocalDateTime.now());
        userProfileMapper.update(userProfile);
    }

    /**
     * 修改用户密码
     * 验证旧密码正确后更新为新密码，并使该用户所有登录token失效，强制下线
     * @param userId 用户ID
     * @param oldPwd 旧密码明文
     * @param newPwd 新密码明文
     */
    @Override
    public void updatePassword(Long userId, String oldPwd, String newPwd) {
        log.info("修改密码: userId={}", userId);
        AuthUser user = userMapper.selectById(userId);
        if (!passwordEncoder.matches(oldPwd, user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }
        String newEncode = passwordEncoder.encode(newPwd);
        userMapper.updatePassword(userId, newEncode);
        tokenMapper.invalidateByUserId(userId);
        log.info("密码修改成功 + 强制下线: userId={}", userId);
    }

    /**
     * 更新用户头像
     * 验证图片格式和大小，存储到服务器，返回访问URL
     * 大小上限从系统配置读取，默认10MB
     * @param userId 用户ID
     * @param file 上传的头像图片文件
     * @return 头像访问URL
     * @throws Exception 存储文件时可能抛出异常
     */
    @Override
    public String updateAvatar(Long userId, MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) throw new RuntimeException("请选择图片");
        String name = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        boolean ok = name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")
                || name.endsWith(".webp") || name.endsWith(".gif");
        if (!ok) throw new RuntimeException("仅支持 png/jpg/jpeg/webp/gif 图片");
        int maxMb = systemSettingService.getInt("upload.avatar_max_size_mb", 10);
        if (file.getSize() > (long) maxMb * 1024 * 1024) {
            throw new RuntimeException("图片不能超过 " + maxMb + "MB");
        }
        File avatarDir = new File(System.getProperty("user.dir"), "upload" + File.separator + "avatar");
        if (!avatarDir.exists() && !avatarDir.mkdirs()) throw new RuntimeException("头像目录创建失败");
        String ext = "png";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) ext = "jpg";
        else if (name.endsWith(".webp")) ext = "webp";
        else if (name.endsWith(".gif")) ext = "gif";
        String fileName = userId + "_avatar." + ext;
        File target = new File(avatarDir, fileName);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        String url = "/avatar/" + fileName;
        userProfileMapper.updateAvatar(userId, url);
        log.info("头像更新成功 userId={} url={}", userId, url);
        return url;
    }

    /**
     * 更新用户扩展信息（个人简介、职业等）
     * 如果不存在则插入，存在则更新，使用upsert操作
     * @param userId 用户ID
     * @param info 用户扩展信息实体
     */
    @Override
    public void updateUserInfo(Long userId, UserInfo info) {
        info.setUserId(userId);
        userInfoMapper.upsert(info);
        log.info("扩展信息更新 userId={}", userId);
    }

    /**
     * 添加照片到用户相册
     * 验证图片格式和大小（上限8MB），按用户分目录存储，记录到数据库
     * @param userId 用户ID
     * @param file 上传的照片文件
     * @return 照片访问URL
     * @throws Exception 存储文件时可能抛出异常
     */
    @Override
    public String addPhoto(Long userId, MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) throw new RuntimeException("请选择图片");
        String name = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        boolean ok = name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")
                || name.endsWith(".webp") || name.endsWith(".gif");
        if (!ok) throw new RuntimeException("仅支持 png/jpg/jpeg/webp/gif 图片");
        if (file.getSize() > 8 * 1024 * 1024) throw new RuntimeException("图片不能超过 8MB");
        File userDir = new File(System.getProperty("user.dir"),
                "upload" + File.separator + "image" + File.separator + userId);
        if (!userDir.exists() && !userDir.mkdirs()) throw new RuntimeException("相册目录创建失败");
        String ext = "png";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) ext = "jpg";
        else if (name.endsWith(".webp")) ext = "webp";
        else if (name.endsWith(".gif")) ext = "gif";
        String fileName = userId + "_" + System.currentTimeMillis() + "." + ext;
        File target = new File(userDir, fileName);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        String url = "/image/" + userId + "/" + fileName;
        userPhotoMapper.insert(userId, url);
        log.info("相册新增 userId={} url={}", userId, url);
        return url;
    }

    /**
     * 从用户相册删除照片
     * 同时删除数据库记录和磁盘文件，做路径安全校验防止路径穿越
     * @param userId 用户ID
     * @param url 照片访问URL
     */
    @Override
    public void deletePhoto(Long userId, String url) {
        if (url == null || url.isEmpty()) throw new RuntimeException("参数不能为空");
        int n = userPhotoMapper.deleteByUserIdAndUrl(userId, url);
        if (n == 0) throw new RuntimeException("照片不存在");
        try {
            if (url.startsWith("/image/")) {
                String rest = url.substring("/image/".length());
                int slash = rest.indexOf('/');
                if (slash > 0) {
                    String uid = rest.substring(0, slash);
                    String fileName = rest.substring(slash + 1);
                    if (uid.matches("\\d+")
                            && !fileName.contains("/") && !fileName.contains("\\") && !fileName.contains("..")) {
                        File f = new File(new File(System.getProperty("user.dir"), "upload/image/" + uid), fileName);
                        if (f.exists()) f.delete();
                    }
                } else {
                    String fileName = rest;
                    if (!fileName.contains("/") && !fileName.contains("\\") && !fileName.contains("..")) {
                        File f = new File(new File(System.getProperty("user.dir"), "upload/image"), fileName);
                        if (f.exists()) f.delete();
                    }
                }
            }
        } catch (Exception ignored) {
        }
        log.info("相册删除 userId={} url={}", userId, url);
    }

    /**
     * 给用户账户充值，原子操作累加余额，并记录充值流水
     * 单次充值上限10万元，保留两位小数
     * @param userId 用户ID
     * @param amount 充值金额，必须大于0
     * @return 充值后的账户余额
     */
    @Override
    @Transactional
    public BigDecimal recharge(Long userId, BigDecimal amount) {
        if (amount == null) throw new RuntimeException("请输入充值金额");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new RuntimeException("充值金额必须大于 0");
        amount = amount.setScale(2, RoundingMode.HALF_UP);
        if (amount.compareTo(new BigDecimal("100000")) > 0) throw new RuntimeException("单次充值不能超过 100000 元");
        userMapper.addBalance(userId, amount);
        userRechargeMapper.insert(userId, amount);
        BigDecimal balance = userMapper.selectBalance(userId);
        log.info("账户充值成功 userId={} amount={} balance={}", userId, amount, balance);
        return balance == null ? amount : balance;
    }

    /**
     * 查询用户的充值记录列表
     * @param userId 用户ID
     * @return 充值记录列表，按时间倒序排列
     */
    @Override
    public List<RechargeRecord> rechargeRecords(Long userId) {
        return userRechargeMapper.selectByUserId(userId);
    }
}
