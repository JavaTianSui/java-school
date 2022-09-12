package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisUtilKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    //点赞实现
    public void like(int userId,int entityType,int entityId,int entityUserId){
//        //返回一个key
//        String entityLikeKey = RedisUtilKey.getEntityLike(entityId,entityType);
//        //判断当前用户有没有点过赞
//        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
//        if (isMember){
//            redisTemplate.opsForSet().remove(entityLikeKey,userId);
//        }else {
//            redisTemplate.opsForSet().add(entityLikeKey,userId);
//        }
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisUtilKey.getEntityLike(entityId,entityType);
                String userLikeKey = RedisUtilKey.getUserLikeKey(entityUserId);
                //判断当前有没有点赞--查询在事务之外完成
                boolean isMember = operations.opsForSet().isMember(entityLikeKey,userId);

                //开启事务
                operations.multi();

                if (isMember){
                    operations.opsForSet().remove(entityLikeKey,userId);
                    //用户得到的赞减1
                    operations.opsForValue().decrement(userLikeKey);
                }else {
                    operations.opsForSet().add(entityLikeKey,userId);
                    operations.opsForValue().increment(userLikeKey);
                }

                //提交事务
                return operations.exec();
            }
        });
    }


    //查询某人对某实体的点赞状态
    public int findEntityLikeStatus(int userId,int entityType,int entityId){
        //返回一个key
        String entityLikeKey = RedisUtilKey.getEntityLike(entityId,entityType);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId) ? 1 : 0;
    }

    //查询实体点赞数量
    public long findEntitylikeCount(int entityType,int entityId){
        //返回一个key
        String entityLikeKey = RedisUtilKey.getEntityLike(entityId,entityType);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    //查询用户获得的赞的数量
    public int findUserLikeCount(int userId){
        String userLikeKey = RedisUtilKey.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }




}
