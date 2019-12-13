package com.man.service;

import com.man.common.constant.RedisConstant;
import com.man.dto.JWTInfoDTO;
import com.man.util.JWTUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class JWTRedisService {

    @Autowired
    private RedisService redisService;

    public JWTInfoDTO create(Long userId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(JWTUtil.SUBJECT, userId);
        String token = JWTUtil.createJWT(map, JWTUtil.JWT_EXPTIME_SECONDS);
        redisService.setStringExpire(RedisConstant.REDIS_SYS_USER_JWT_PREFIX_KEY + userId, token, RedisConstant.REDIS_SYS_USER_JWT_EXPIRES);
        return new JWTInfoDTO(userId, token);
    }

    public boolean check(String token) {
        if (null == token) {
            return false;
        }

        try {
            Claims claims = JWTUtil.parseJWT(token);

//			claim过期时间设置不了，这里应该以redistribution过期时间为准
//			Date expDate = claims.getExpiration();
//			if(expDate.before(new Date())) {
//				log.warn("ǩ���ѹ���!");
//				return false;
//			}

            String redisKey = RedisConstant.REDIS_SYS_USER_JWT_PREFIX_KEY + claims.get(JWTUtil.SUBJECT);
            if (!redisService.hasKey(redisKey)) {
                log.warn("invalid token!");
                return false;
            }

            if (!claims.containsKey(JWTUtil.SUBJECT)) {
                log.warn("invalid token!");
                return false;
            }

            String redisToken = redisService.getString(redisKey);
            if (StringUtils.isEmpty(redisToken) || !token.equals(redisToken)) {
                log.warn("invalid token");
                return false;
            }
            redisService.setStringExpire(redisKey, redisToken, RedisConstant.REDIS_SYS_USER_JWT_EXPIRES);
            return true;
        } catch (Exception e) {
            log.error("check token error", e, e.toString());
            e.printStackTrace();
        }
        return false;
    }

    public JWTInfoDTO get(String token) throws Exception {
        Claims claims = JWTUtil.parseJWT(token);
        Integer userId = claims.get(JWTUtil.SUBJECT, Integer.class);
        return new JWTInfoDTO(userId.longValue(), token);
    }

    public void remove(Long userId) {
        if (null != userId) {
            String redisKey = RedisConstant.REDIS_SYS_USER_JWT_PREFIX_KEY + userId;
            redisService.delString(redisKey);
        }
    }

}

