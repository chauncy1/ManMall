package com.man.component.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TopicReceiver {

    /**
     * 只订阅所有mall的信息
     *
     * @param message
     */
    @RabbitListener(queues = "mall.all")
    @RabbitHandler
    public void mallHandler(String message) {
        log.info("mallHandler received message: " + message);
    }

    /**
     * 只订阅所有pay的信息
     *
     * @param message
     */
    @RabbitListener(queues = "mall.pay")
    @RabbitHandler
    public void payHandler(String message) {
        log.info("payHandler received message: " + message);
    }

    /**
     * 只订阅所有wechat的信息
     *
     * @param message
     */
    @RabbitListener(queues = "mall.wechat")
    @RabbitHandler
    public void wechatHandler(String message) {
        log.info("wechatHandler received message: " + message);
    }
}
