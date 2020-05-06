package com.man.test;

import com.man.MallApplication;
import com.man.dto.request.AddPresentRequest;
import com.man.service.PresentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MallApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionTest {
    @Autowired
    private PresentService presentService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    int count = 0;
    /**
     * 手动控制事务
     */
    @Test
    public void test() {
        DefaultTransactionDefinition dfd = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(dfd);
        while(true) {
            try {
                if(status.isCompleted()){
                    status = transactionManager.getTransaction(dfd);
                }
                AddPresentRequest addReq = new AddPresentRequest();
                addReq.setPresentName("giao哥的大牙");
                addReq.setPresentScore(98);
                addReq.setPresentCount(10);
                addReq.setPresentDesc("一给窝里giao");
                count++;

                presentService.insertRequest(addReq);
                if(count == 5){
                    count = 0;
                    transactionManager.commit(status);
                }
            }catch (Exception e){
                transactionManager.rollback(status);
            }

        }

    }
}
