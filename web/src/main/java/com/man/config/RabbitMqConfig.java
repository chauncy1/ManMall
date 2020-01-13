package com.man.config;

import com.man.common.enums.QueueEnum;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列配置
 * Created by macro on 2018/9/14.
 */
@Configuration
public class RabbitMqConfig {

    /****************************************************** Direct *****************************************************************/

    /**
     * 订单消息实际消费队列所绑定的交换机
     */
    @Bean
    DirectExchange orderDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_ORDER_CANCEL.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 订单延迟队列队列所绑定的交换机
     */
    @Bean
    DirectExchange orderTtlDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 订单实际消费队列
     */
    @Bean
    public Queue orderQueue() {
        return new Queue(QueueEnum.QUEUE_ORDER_CANCEL.getName());
    }

    /**
     * 订单延迟队列（死信队列）
     */
    @Bean
    public Queue orderTtlQueue() {
        return QueueBuilder
                .durable(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getName())
                .withArgument("x-dead-letter-exchange", QueueEnum.QUEUE_ORDER_CANCEL.getExchange())//到期后转发的交换机
                .withArgument("x-dead-letter-routing-key", QueueEnum.QUEUE_ORDER_CANCEL.getRouteKey())//到期后转发的路由键
                .build();
    }

    /**
     * 将订单队列绑定到交换机
     */
    @Bean
    Binding orderBinding(DirectExchange orderDirect, Queue orderQueue) {
        return BindingBuilder
                .bind(orderQueue)
                .to(orderDirect)
                .with(QueueEnum.QUEUE_ORDER_CANCEL.getRouteKey());
    }

    /**
     * 将订单延迟队列绑定到交换机
     */
    @Bean
    Binding orderTtlBinding(DirectExchange orderTtlDirect, Queue orderTtlQueue) {
        return BindingBuilder
                .bind(orderTtlQueue)
                .to(orderTtlDirect)
                .with(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getRouteKey());
    }

    /******************************************************** Topic *****************************************************************/

    /**
     * 商城Topic消息队列，绑定Topic交换机
     */
    @Bean
    public Queue topicMessageMall() {
        return new Queue(QueueEnum.QUEUE_MALL_ACTION.getName());
    }

    /**
     * 支付Topic消息队列，绑定Topic交换机
     */
    @Bean
    public Queue topicMessagePay() {
        return new Queue(QueueEnum.QUEUE_MALL_PAY.getName());
    }

    /**
     * 微信Topic消息队列，绑定Topic交换机
     */
    @Bean
    public Queue topicMessageWechat() {
        return new Queue(QueueEnum.QUEUE_MALL_WECHAT.getName());
    }

    /**
     * topic交换机
     */
    @Bean
    TopicExchange topicExchange() {
        return ExchangeBuilder
                .topicExchange(QueueEnum.QUEUE_MALL_ACTION.getExchange())
                .durable(true)
                .build();
    }

    /**
     * topic交换机绑定商城队列
     */
    @Bean
    Binding topicBindingMall(Queue topicMessageMall, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(topicMessageMall)
                .to(topicExchange)
                .with(QueueEnum.QUEUE_MALL_ACTION.getRouteKey());
    }

    /**
     * topic交换机绑定支付队列
     */
    @Bean
    Binding topicBindingPay(Queue topicMessagePay, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(topicMessagePay)
                .to(topicExchange)
                .with(QueueEnum.QUEUE_MALL_PAY.getRouteKey());
    }

    /**
     * topic交换机绑定微信队列
     */
    @Bean
    Binding topicBindingWechat(Queue topicMessageWechat, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(topicMessageWechat)
                .to(topicExchange)
                .with(QueueEnum.QUEUE_MALL_WECHAT.getRouteKey());
    }

}