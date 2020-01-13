package com.man.config.db;

import com.man.common.enums.DBTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class DBContext {

    private static final ThreadLocal<DBTypeEnum> dbContext = new ThreadLocal<>();

    private static final AtomicInteger counter = new AtomicInteger(0);

    public static void set(DBTypeEnum dbType) {
        dbContext.set(dbType);
    }

    public static DBTypeEnum get() {
        return dbContext.get();
    }

    public static void master() {
        set(DBTypeEnum.MASTER);
        log.info("切换到master库");
    }

    public static void slave() {
        //  读库负载均衡(轮询方式)
        int index = counter.get();
        log.info("slave库访问线程数==>{}", index);
        if (index == 0) {
            set(DBTypeEnum.SLAVE1);
            counter.incrementAndGet();
            log.info("切换到slave1库");
        } else {
            set(DBTypeEnum.SLAVE2);
            counter.decrementAndGet();
            log.info("切换到slave2库");
        }
    }

}
