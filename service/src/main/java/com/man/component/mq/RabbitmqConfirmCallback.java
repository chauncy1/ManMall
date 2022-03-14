package com.man.component.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Map;

/**
 * @description:
 * @author: mchangx
 * @email: mancx95c@visionvera.com
 * @date: 2022/3/14 11:52
 */
@Component
@Slf4j
public class RabbitmqConfirmCallback implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    /**
     * 监听消息是否到达Exchange
     *
     * @param correlationData 包含消息的唯一标识的对象
     * @param ack             true 标识 ack，false 标识 nack
     * @param cause           nack 投递失败的原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("消息投递成功~消息Id：{}", correlationData.getId());
        } else {
            log.error("消息投递失败，Id：{}，错误提示：{}", correlationData.getId(), cause);
        }
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.info("消息没有路由到队列，获得返回的消息");
        Map map = byteToObject(message.getBody(), Map.class);
        log.info("message body: {}", map == null ? "" : map.toString());
        log.info("replyCode: {}", replyCode);
        log.info("replyText: {}", replyText);
        log.info("exchange: {}", exchange);
        log.info("routingKey: {}", exchange);
        log.info("------------> end <------------");
    }

    @SuppressWarnings("unchecked")
    private <T> T byteToObject(byte[] bytes, Class<T> clazz) {
        T t = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            t = (T) ois.readObject();
        } catch (Exception e) {
            log.error("mq 字节消息转换为对象失败", e);
        }
        return t;
    }
}