package com.man.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true) // 链式调用
public class OrderInfo {
    String id;
    Long userId;
    Long presentId;
    Integer presentCount;
    Integer presentScore;
    Integer orderType;
    Date createTime;
    String createBy;

    public OrderInfo(String id, Long userId, Long presentId, Integer presentCount, Integer presentScore, Integer orderType) {
        this.id = id;
        this.userId = userId;
        this.presentId = presentId;
        this.presentCount = presentCount;
        this.presentScore = presentScore;
        this.orderType = orderType;
    }
}
