package com.man.common.enumerate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderStatusEnum {
    NO_PAID(0, "已下单，未付款"),

    PAID(1, "已下单，已付款"),

    ;

    /**
     * 订单状态：0 已下单，未付款；1 已下单，已付款
     */
    private Integer status;

    private String discription;
}
