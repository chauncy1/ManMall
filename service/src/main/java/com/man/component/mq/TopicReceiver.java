package com.man.component.mq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
    public void payHandler(String msg, Message message, Channel channel) throws IOException {
        /* 消息接收端防丢失 ACK方式
        try {
            log.info("小富收到消息：{}", msg);

            //TODO 具体业务
            // 成功接收消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        }  catch (Exception e) {

            if (message.getMessageProperties().getRedelivered()) {

                log.error("消息已重复处理失败,拒绝再次接收...");

                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false); // 拒绝消息
            } else {

                log.error("消息即将再次返回队列处理...");

                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }
        }*/
        log.info("payHandler received message: " + msg);
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
