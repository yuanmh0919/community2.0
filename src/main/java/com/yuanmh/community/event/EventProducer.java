package com.yuanmh.community.event;

import com.alibaba.fastjson.JSONObject;
import com.yuanmh.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author: Yuanmh
 * @Date: 下午1:02 2024/7/1
 * @Describe:
 */

@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    //处理事件
    public void fireEvent(Event event) {
        //将事件发布到指定的主题
        //将事件转换为JSON字符串 并发送到指定的主题
        //为什么需要装换为JSON字符串呢？消费者得到这个event之后 能还原为Event对象 就能得到event所有的数据
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));

    }
}
