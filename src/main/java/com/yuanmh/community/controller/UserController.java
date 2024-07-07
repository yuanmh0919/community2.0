package com.yuanmh.community.controller;

import com.yuanmh.community.annotation.LoginRequired;
import com.yuanmh.community.entity.User;
import com.yuanmh.community.service.FollowService;
import com.yuanmh.community.service.LikeService;
import com.yuanmh.community.service.UserService;
import com.yuanmh.community.utils.CommunityConstant;
import com.yuanmh.community.utils.CommunityUtil;
import com.yuanmh.community.utils.HostHolder;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * @Author: Yuanmh
 * @Date: 上午10:28 2024/6/18
 * @Describe:
 */

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {


    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    //先将需要使用的对象注入
    @Autowired
    private UserService userService;

    //文件上传路径
    @Value("${community.path.upload-path}")
    private String uploadPath;

    //域名
    @Value("${community.path.domain-name}")
    private String domain;

    //项目访问路径
    @Value("${server.servlet.context-path}")
    private String contextPath;

    //更新当前用户的信息 那么就需要用到当前用户 从hostHolder中获取
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;


    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage() {
        return "site/setting";
    }

    /**
     * 上传头像
     *
     * @param headerImage 头像文件
     * @param model       向页面返回信息
     * @return
     */
    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "请选择图片...");
            return "site/setting";
        }
        //文件存在 上传文件 注意文件名字不能重复 所以需要加上随机字符串
        //先读取文件的后缀
        String originalFilename = headerImage.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //判断一下文件是否有后缀
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式不正确！");
            return "site/setting";
        }
        //生成新的文件名
        String newFilename = CommunityUtil.generateUUID() + suffix;
        //确定文件上传路径
        File file = new File(uploadPath + newFilename);
        //将当前文件写入到指定路径文件中
        try {
            //存储文件
            headerImage.transferTo(file);
        } catch (IOException e) {
            LOGGER.error("上传文件失败！" + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发送异常！" + e);
        }
        //更新用户头像的路径（web访问路径）
        //http://localhost:8080/community/user/header/xxx.png
        String headerUrl = domain + contextPath + "/user/header/" + newFilename;
        User user = hostHolder.getUser();
        userService.updateHeader(user.getId(), headerUrl);
        return "redirect:/index";
    }

    /**
     * 获取头像
     *
     * @param filename 文件名
     * @param response 向浏览器响应头像文件
     * @return
     */
    @GetMapping("/header/{filename}")
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response) {
        //确定文件路径
        filename = uploadPath + filename;
        //文件后缀
        String suffix = filename.substring(filename.lastIndexOf("."));
        //响应图片 固定写法
        response.setContentType("image/" + suffix);

        try (// 获取输出流
             OutputStream os = response.getOutputStream();
             //读取文件 得到输入流
             FileInputStream fis = new FileInputStream(filename);) {

            //将输入流写入到输出流中
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = fis.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
        } catch (IOException e) {
            LOGGER.error("获取头像失败！" + e.getMessage());
            throw new RuntimeException("获取头像失败,服务器访问异常！" + e);
        }
    }

    /**
     * 更新密码
     *
     * @param oldPassword
     * @param newPassword
     * @param model
     * @return
     */
    @LoginRequired
    @PostMapping("/updatePassword")
    public String updatePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, Model model) {
        if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)) {
            model.addAttribute("passwordError", "密码不能为空！");
            return "site/setting";
        }
        User user = hostHolder.getUser();
        //旧密码不对
        if (!user.getPassword().equals(CommunityUtil.md5(oldPassword + user.getSalt()))) {
            model.addAttribute("passwordError", "密码不正确！");
        }
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        userService.updatePassword(user.getId(), newPassword);
        return "redirect:/index";
    }

    @GetMapping("/profile")
    public String getProfilePage() {
        return "site/profile";
    }

    //个人主页

    /**
     * 查看个人主页信息
     *
     * @param userId 用户id
     * @param model  向页面返回信息
     * @return
     */
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在！");
        }

        model.addAttribute("user", user);
        long userLikeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("userLikeCount", userLikeCount);

        //关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        //是否已经关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.isFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);
        return "site/profile";
    }
}
