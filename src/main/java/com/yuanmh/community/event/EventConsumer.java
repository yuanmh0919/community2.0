package com.yuanmh.community.event;

import com.alibaba.fastjson.JSONObject;
import com.yuanmh.community.dao.MessageMapper;
import com.yuanmh.community.entity.Event;
import com.yuanmh.community.entity.Message;
import com.yuanmh.community.utils.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Yuanmh
 * @Date: 下午1:39 2024/7/1
 * @Describe:
 */

@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageMapper messageMapper;

    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record) {
        //如果消息为空
        if (record == null || record.value() == null) {
            LOGGER.error("消息内容为空！");
            return;
        }
        //将发送过来的信息还原为event
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            LOGGER.error("消息格式错误！");
            return;
        }
        //系统后台发信息给所有用户
        //发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        //消息接收者
        message.setToId(event.getEntityUserId());
        //系统消息的类型 评论 点赞 关注
        message.setConversationId(event.getTopic());
        message.setStatus(0);
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());
        //还有其他信息
        if (!event.getData().isEmpty()) {
            //遍历data，将其添加到content中
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageMapper.insertMessage(message);
    }

}
