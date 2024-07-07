package com.yuanmh.community;

import com.yuanmh.community.dao.*;
import com.yuanmh.community.entity.*;
import com.yuanmh.community.utils.CommunityUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

/**
 * @Author: Yuanmh
 * @Date: 下午5:04 2024/6/17
 * @Describe:
 */

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {
    @Autowired
    private UserMapper userMapper;


    @Autowired
    private DiscussPostMapper discussPostMapper;


    @Autowired
    private LoginTicketMapper loginTicketMapper;


    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private MessageMapper messageMapper;


    //测试查找所有用户
    @Test
    public void testSelectAll() {
        List<User> users = userMapper.selectAll();
        for (User user : users) {
            System.out.println(user);
        }
    }

    //测试根据id查找用户
    @Test
    public void testSelectById() {
        User user = userMapper.selectById(101);
        System.out.println(user);
    }


    @Test
    public void testselectByName() {
        System.out.println(userMapper.selectByName("liubei "));
    }

    //测试插入用户
    @Test
    public void testInsert() {
        User user = new User();
        user.setUsername("jack");
        user.setPassword("123456");
        int i = userMapper.insertUser(user);
        System.out.println(i == 1 ? "success" : "fail");
    }


    //测试更新用户
    @Test
    public void testUpdate() {
        //开启事务
        int i = userMapper.updateStatus(150, 1);
        System.out.println(i == 1 ? "success" : "fail");

        userMapper.updateHeader(150, "http://www.baidu.com");

        userMapper.updatePassword(150, "00000");

    }


    //测试分页查询
    @Test
    public void testSelectDiscussPosts() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(111, 1, 2);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }
    }

    @Test
    public void testSelectDiscussPostsRows() {
        int i = discussPostMapper.selectDiscussPostRows(111);
        System.out.println();
        System.out.println("111共有" + i + "条数据");
    }


    //测试LoginTicketMapper
    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(111);
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        //设置失效时间为10分钟后
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 8 + 1000 * 60 * 10));
        int i = loginTicketMapper.insertLoginTicket(loginTicket);
        System.out.println(i == 1 ? "添加成功" : "添加失败");
    }


    @Test
    public void testSelectLoginTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("123456");
        System.out.println(
                loginTicket == null ? "未找到" : "找到了" + loginTicket
        );
    }

    //更新状态
    @Test
    public void testUpdateStatus() {
        int i = loginTicketMapper.updateStatus("123456", 1);
    }


    //测试插入帖子
    @Test
    public void testInsertDiscussPost() {
        int i = discussPostMapper.insertDiscussPost(new DiscussPost("154", "论多久能写完这个项目", "进度缓慢，加油！", 1, 1, new Date(), 0, 99999.0));
        System.out.println(i == 1 ? "success" : "fail");
    }


    //插入评论
    @Test
    public void testInsertComment() {
        Comment comment = new Comment();
//        user_id, entity_type ,entity_id, target_id, content, status,create_time
        comment.setUserId(154);
        comment.setEntityType(1);
//      帖子id
        comment.setEntityId(283);
        comment.setContent("测试评论");
        comment.setCreateTime(new Date());
        int i = commentMapper.insertComment(comment);
        System.out.println(i == 1 ? "success" : "fail");
    }

    //    测试更新帖子回复数量
    @Test
    public void testUpdateCommentCount() {
        int i = discussPostMapper.updateCommentCount(283, 1);
        System.out.println(i == 1 ? "success" : "fail");
    }


    //测试消息
    @Test
    public void testMessageMapper() {
        List<Message> messages = messageMapper.selectConversations(111, 1, 10);
        System.out.println("查询当前用户的会话列表，针对每个会话只返回最新的一条私信: ");
        for (Message message : messages) {
            System.out.println(message);
        }
        System.out.println();
        System.out.println();
        System.out.print("当前用户的会话数量为：");
        System.out.println(messageMapper.selectConversationsCount(111));

        System.out.println();
        System.out.println();
        System.out.println("查询当前会话包含的私信列表: ");
        messageMapper.selectLetters("111_112", 1, 10);

        System.out.println();
        System.out.println();
        System.out.println("查询某个会话包含的私信数量: ");

        System.out.println(messageMapper.selectLettersCount("111_112"));
        System.out.println();
        System.out.println();
        System.out.println("查询某个用户的未读私信数量: ");
        System.out.println(messageMapper.selectLettersUnreadCount(111, null));

    }


}
