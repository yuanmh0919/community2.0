package com.yuanmh.community.dao;

import com.yuanmh.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: Yuanmh
 * @Date: 下午7:27 2024/6/24
 * @Describe:
 */
@Mapper
public interface MessageMapper {
    //查询当前用户的会话列表，针对每个会话只返回最新的一条私信 并且分页查询
    List<Message> selectConversations(int userId, int offset, int limit);

    //查询当前用户的会话数量
    int selectConversationsCount(int userId);

    //查询某个会话包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    //查询某个会话包含的私信数量
    int selectLettersCount(String conversationId);

    //查询未读私信数量 conversationId为空时查询所有未读私信 使用动态sql
    int selectLettersUnreadCount(int userId, String conversationId);

    //添加私信
    int insertMessage(Message message);

    //更新私信状态 已读或者已删除
    int updateStatus(List<Integer> ids, int status);


    /**
     * 查询某个主题下最新的通知
     *
     * @param userId 谁的通知
     * @param topic  哪个主题
     * @return 最新的通知
     */
    Message selectLatestMessage(int userId, String topic);


    /**
     * 查询某个主题的消息数量
     *
     * @param userId 谁的通知
     * @param topic  哪个主题
     * @return 主题消息数量
     */
    int selectNoticeCount(int userId, String topic);

    //查询某个主题未读的消息数量

    /**
     * 查询某个主题的未读消息数量
     *
     * @param userId 谁的通知
     * @param topic  哪个主题
     * @return 主题未读消息数量
     */
    int selectNoticeUnreadCount(int userId, String topic);

    /**
     * 查询某个主题的通知列表
     *
     * @param userId 谁的通知
     * @param topic  哪个主题
     * @param offset 页码
     * @param limit  数量
     * @return 主题通知列表
     */
    List<Message> selectNotices(int userId, String topic, int offset, int limit);


    //查找所有信息 添加到首页消息中


}
