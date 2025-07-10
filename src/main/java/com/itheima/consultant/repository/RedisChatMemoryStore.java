package com.itheima.consultant.repository;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

/**
 * Copyright (C), 2021, 北京同创永益科技发展有限公司
 *
 * @author YueMing
 * @version 3.0.0
 * @description
 * @date $ $
 */

@Repository
public class RedisChatMemoryStore implements ChatMemoryStore {

    // 注入 RedisTemplate
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 获取指定会话消息
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        // 获取会话消息
        String json = stringRedisTemplate.opsForValue().get(memoryId);
        // 反序列化消息
        List<ChatMessage> chatMessageList = ChatMessageDeserializer.messagesFromJson(json);
        return chatMessageList;
    }

    // 更新指定会话消息
    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        // 序列化消息
        String json = ChatMessageSerializer.messagesToJson(messages);
        // 将 json 数据 存储在 redis 中
        stringRedisTemplate.opsForValue().set(memoryId.toString(),json, Duration.ofDays(1));
    }

    // 删除指定会话消息
    @Override
    public void deleteMessages(Object memoryId) {
        // 删除会话消息
        stringRedisTemplate.delete(memoryId.toString());

    }
}
