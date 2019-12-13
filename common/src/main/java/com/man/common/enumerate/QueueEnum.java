package com.man.common.enumerate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QueueEnum {
    /**
     * 消息通知队列
     */
    QUEUE_ORDER_CANCEL("mall.order.direct", "mall.order.cancel", "mall.order.cancel"),
    /**
     * 消息通知延时队列
     */
    QUEUE_TTL_ORDER_CANCEL("mall.order.direct.ttl", "mall.order.cancel.ttl", "mall.order.cancel.ttl"),

    /**
     * Topic消息队列 :
     * *表示一个词.
     * #表示零个或多个词.
     */
    // 商城大队列
    QUEUE_MALL_ACTION("mall.topic.direct", "mall.all", "mall.#"),
    // 商城 支付行为
    QUEUE_MALL_PAY("mall.topic.direct", "mall.pay", "mall.pay.#"),
    // 商城 微信行为
    QUEUE_MALL_WECHAT("mall.topic.direct", "mall.wechat", "mall.*.wechat.#"),

    ;
    /**
     * 交换名称
     */
    private String exchange;
    /**
     * 队列名称
     */
    private String name;
    /**
     * 路由键
     */
    private String routeKey;

}