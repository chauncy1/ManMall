package com.man.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.man.dto.request.AddPresentRequest;
import lombok.extern.slf4j.Slf4j;
import com.man.entity.PresentInfo;
import com.man.mapper.PresentMapping;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Service
public class PresentService {
    @Autowired
    PresentMapping presentMapping;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedisLockRegistry redisLockRegistry;

    private static Semaphore semaphore = new Semaphore(1);

    /**
     * 无锁
     *
     * @param id
     * @param count
     * @return
     * @throws InterruptedException
     */
    public int minusPresentCount(String id, Integer count) throws InterruptedException {
        //注意并发
        PresentInfo judge = selectById(id);
        if (judge.getPresentCount() - count < 0) {
            return 0;
        }
        PresentInfo pres = new PresentInfo();
        pres.setPresentId(Long.valueOf(id));
        pres.setPresentCount(count);
        return presentMapping.minusPresentCount(pres);

    }

    /**
     * 单机加锁
     *
     * @param id
     * @param count
     * @return
     * @throws InterruptedException
     */
    public int minusPresentCountBySemaphore(String id, Integer count) throws InterruptedException {
        // 信号量大法
        semaphore.acquire();
        try {
            PresentInfo judge = selectById(id);
            if (judge.getPresentCount() - count < 0) {
                return 0;
            }

            PresentInfo pres = new PresentInfo();
            pres.setPresentId(Long.valueOf(id));
            pres.setPresentCount(count);
            return presentMapping.minusPresentCount(pres);
        } finally {
            semaphore.release();
        }

    }

    /**
     * opsForValue().setIfAbsent分布式锁
     * setIfAbsent处理太慢，让每个线程多重试3次
     *
     * @param id
     * @param count
     * @return
     */
    public int minusPresentCountByRedis(String id, Integer count) {
        int result = 0;
        PresentInfo pres = new PresentInfo();
        pres.setPresentId(Long.valueOf(id));
        pres.setPresentCount(count);

        //redis分布式锁
        if (stringRedisTemplate.opsForValue().setIfAbsent(id, id, 3L, TimeUnit.SECONDS)) {
            try {
                PresentInfo judge = selectById(id);
                if (judge.getPresentCount() - count < 0) {
                    return -1;
                }
                result = presentMapping.minusPresentCount(pres);
            } finally {
                stringRedisTemplate.delete(id);
            }
            return result;
        }
        this.RamdomSleep();
        return minusPresentCountByRedis(id, count);

    }

    /**
     * Spring Integration分布式锁
     *
     * @param id
     * @param count
     * @return
     * @throws InterruptedException
     */
    public int minusPresentCountByRedisLock(String id, Integer count) throws InterruptedException {
        int res = 0;
        PresentInfo pres = new PresentInfo();
        pres.setPresentId(Long.valueOf(id));
        pres.setPresentCount(count);

        //redis integration分布式锁
        Lock lock = redisLockRegistry.obtain("minus-present");
        if (lock.tryLock(3, TimeUnit.SECONDS)) { // 等待锁 3s
            try {
                // 如果库存不足，则不更新
                if (this.selectById(id).getPresentCount() - count < 0) {
                    return 0;
                }
                res = presentMapping.minusPresentCount(pres);
            } finally {
                lock.unlock();
            }
        }
        return res;
    }

    /**
     * 削峰填谷
     */
    public void RamdomSleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(new Random().nextInt(10) + 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int insertRequest(AddPresentRequest addReq) {
        PresentInfo pInfo = new PresentInfo();
        BeanUtils.copyProperties(addReq, pInfo);
        return presentMapping.insert(pInfo);
    }

    public PresentInfo selectById(String id) {
        return presentMapping.selectOne(new QueryWrapper<PresentInfo>()
                .eq("present_id", id));
    }

    public int update(PresentInfo p) {
        return presentMapping.update(p, new UpdateWrapper<PresentInfo>()
                .eq("present_id", p.getPresentId()));
    }

    @Autowired
    private PlatformTransactionManager transactionManager;

    public void testTransaction(){
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);//新发起一个事务
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            AddPresentRequest addReq = new AddPresentRequest();
            addReq.setPresentName("giao哥的大牙");
            addReq.setPresentScore(98);
            addReq.setPresentCount(10);
            addReq.setPresentDesc("一给窝里giao");

            insertRequest(addReq);
            transactionManager.commit(status);

        }catch (Exception e){
            transactionManager.rollback(status);
        }
    }
}
