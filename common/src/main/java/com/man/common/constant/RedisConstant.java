package com.man.common.constant;

/**
 * redis常量类
 *
 * @Description:
 * @Author: tanyang
 * @Date: 2019-03-22 14:14
 */
public class RedisConstant {

    /**
     * 系统用户redis jwt key前缀
     */
    public static final String REDIS_SYS_USER_JWT_PREFIX_KEY = "REDIS_SYS_USER_JWT_";

    /**
     * 系统用户redis jwt过期时间
     */
    public static final int REDIS_SYS_USER_JWT_EXPIRES = 7200;

    /**
     * user redis jwt key 前缀
     */
    public static final String REDIS_WECHAT_USER_JWT_PREFIX_KEY = "REDIS_WECHAT_JWT_";

    /**
     * user redis jwt expires
     */
    public static final int REDIS_WECHAT_USER_JWT_EXPIRES = 7200;

    /**
     * user redis jwt key 前缀
     */
    public static final String REDIS_DRIVE_USER_JWT_PREFIX_KEY = "REDIS_DRIVE_JWT_";

    /**
     * user redis jwt expires
     */
    public static final int REDIS_DRIVE_USER_JWT_EXPIRES = 7200;

}
