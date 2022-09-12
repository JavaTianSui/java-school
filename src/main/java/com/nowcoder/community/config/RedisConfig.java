package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {


    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        //要序列化  设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string());
        // 设置普通的序列化方式
        template.setValueSerializer(RedisSerializer.json());
        //设置hash的key的序列化
        template.setHashKeySerializer(RedisSerializer.string());
        //设置hash的value的序列化
        template.setHashValueSerializer(RedisSerializer.json());
        //出发生效
        template.afterPropertiesSet();

        return template;
    }
}
