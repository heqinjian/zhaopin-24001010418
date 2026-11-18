package com.recruitment.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        // 第 1 步：创建 RedisTemplate，并设置 Redis 连接工厂
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 第 2 步：创建 ObjectMapper，用来把 Java 对象转换成 JSON
        ObjectMapper objectMapper = new ObjectMapper();
        // 第 3 步：支持 LocalDate、LocalDateTime 等 Java 时间类型
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 第 4 步：在 JSON 中保存对象类型信息，读回 List<Job> 等数据时才能还原为原 Java 类型
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);

        // 第 5 步：创建 JSON 序列化器
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // 第 6 步：Key 使用普通字符串
        template.setKeySerializer(new StringRedisSerializer());
        // 第 7 步：Value 使用 JSON
        template.setValueSerializer(jsonSerializer);

        // 第 8 步：完成 RedisTemplate 初始化
        template.afterPropertiesSet();
        return template;
    }
}
