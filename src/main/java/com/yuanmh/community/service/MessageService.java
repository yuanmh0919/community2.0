package com.yuanmh.community.service;

import com.yuanmh.community.entity.Message;

import java.util.List;

/**
 * @Author: Yuanmh
 * @Date: 下午8:19 2024/6/24
 * @Describe:
 */
public interface MessageService {
    List<Message> findConversations(int userId, int offset, int limit);

    /**
     * 查询当前用户的会话列表
     *
     * @param userId
     * @return
     */
    int findConversationsCount(int userId);

    //查询某个会话包含的私信列表
    List<Message> findLetters(String conversationId, int offset, int limit);

    //查询某个会话包含的私信数量
    int findLettersCount(String conversationId);

    //查询未读私信数量 conversationId为空时查询所有未读私信 使用动态sql
    int findLettersUnreadCount(int userId, String conversationId);

    //添加私信
    int addMessage(Message message);

    //更新私信状态
    int readMessage(List<Integer> ids);


    /**
     * 查询某个主题下最新的通知
     *
     * @param userId 谁的通知
     * @param topic  哪个主题
     * @return 最新的通知
     */
    Message findLatestNotice(int userId, String topic);


    /**
     * 查询某个主题的消息数量
     *
     * @param userId 谁的通知
     * @param topic  哪个主题
     * @return 主题消息数量
     */
    int findNoticeCount(int userId, String topic);

    //查询某个主题未读的消息数量

    /**
     * 查询某个主题的未读消息数量
     *
     * @param userId 谁的通知
     * @param topic  哪个主题
     * @return 主题未读消息数量
     */
    int findNoticeUnreadCount(int userId, String topic);

    /**
     * 查询某个主题的通知列表
     *
     * @param userId 谁的通知
     * @param topic  哪个主题
     * @param offset 页码
     * @param limit  数量
     * @return 主题通知列表
     */
    List<Message> findNotices(int userId, String topic, int offset, int limit);
}
