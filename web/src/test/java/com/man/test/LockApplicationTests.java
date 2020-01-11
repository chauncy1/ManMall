package com.man.test;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.man.entity.PresentInfo;
import com.man.MallApplication;
import com.man.service.PresentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MallApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LockApplicationTests {
    @Autowired
    PresentService presentService;

    private static final int USER_NUM = 100;//总人数
    private static final Integer P_COUNT = 100;//商品总数
    private static final String P_ID = "1";//商品id

    private static int successPreson = 0;

    private static int saleNum = 0;

    private static CountDownLatch countDownLatch = new CountDownLatch(USER_NUM);

    @Before
    public void before() {
        PresentInfo p = new PresentInfo();
        p.setPresentId(1L); // 商品id
        p.setPresentCount(P_COUNT); // 商品总数
        presentService.update(p);
    }

    @Test
    public void testMinusPresentCount() throws InterruptedException {
        for (int i = 0; i < USER_NUM; i++) {
            //买id为5的商品，每人买3个
            new Thread(new minusRequest(P_ID, 3)).start();
            countDownLatch.countDown();
        }

        Thread.currentThread();
        Thread.sleep(10000);
        log.info("成功购买的人数： " + successPreson);
        log.info("卖出的商品个数： " + saleNum);
        log.info("剩余商品个数： " + presentService.selectById(P_ID).getPresentCount());

    }

    @AllArgsConstructor
    public class minusRequest implements Runnable {
        private String id;
        private Integer count;

        @Override
        public void run() {
            int updateResult = 0;
            try {
                countDownLatch.await();
                updateResult = presentService.minusPresentCountByRedis(id, count);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //计数
            if (updateResult > 0) {
                synchronized (countDownLatch) {
                    successPreson++;
                    saleNum += count;
                }
            }
        }

    }

}
