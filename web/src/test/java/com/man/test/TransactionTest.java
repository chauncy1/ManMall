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

    /**
     * 手动控制事务
     */
    @Test
    public void test() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);//新发起一个事务
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            AddPresentRequest addReq = new AddPresentRequest();
            addReq.setPresentName("giao哥的大牙");
            addReq.setPresentScore(98);
            addReq.setPresentCount(10);
            addReq.setPresentDesc("一给窝里giao");

            presentService.insertRequest(addReq);
            transactionManager.commit(status);

        }catch (Exception e){
            transactionManager.rollback(status);
        }
    }
}
