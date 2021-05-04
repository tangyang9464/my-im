package com.common.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Component
public class RedisUtil {
    @Resource
    StringRedisTemplate stringRedisTemplate;
    String USER_KEY_PREFIX="im_";

    public void addUserAndServer(String userId,String serverInfo){
        stringRedisTemplate.opsForValue().set(USER_KEY_PREFIX+userId,serverInfo);
    }

    public void deleteUserAndServer(String userId){
        stringRedisTemplate.delete(USER_KEY_PREFIX+userId);
    }

    public String getServerInfo(String userId){
        return stringRedisTemplate.opsForValue().get(USER_KEY_PREFIX+userId);
    }
    public Set<String> getAllUser(){
        return stringRedisTemplate.keys(USER_KEY_PREFIX+"*");
    }
    public Boolean userIsExist(String userId){
        return stringRedisTemplate.hasKey(USER_KEY_PREFIX+userId);
    }
}
