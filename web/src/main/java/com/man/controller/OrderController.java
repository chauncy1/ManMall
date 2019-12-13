package com.man.controller;

import com.man.common.result.CommonResult;
import com.man.controller.request.OrderRequest;
import com.man.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    /**
     * 用户下单：
     * 礼品减数量 -> 生成订单信息
     *
     * @param orderRequest
     * @return
     */
    @PostMapping("/generator")
    public CommonResult<String> generatorOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.generateOrder(orderRequest.getUserId(), orderRequest.getPresentId(), orderRequest.getPresentCount());
    }

    /**
     * 用户付款：
     * 用户减积分 -> 更新订单状态
     *
     * @param orderId
     * @return
     */
    @PostMapping("/payment")
    public CommonResult<String> payOrder(@RequestParam String orderId) {
        return orderService.payOrder(orderId);
    }

    /**
     * 用户取消订单:
     * 礼品返还数量 -> 删除订单
     *
     * @param orderId
     * @return
     */
    @PostMapping("/cancel")
    public CommonResult<String> cancelOrder(@RequestParam String orderId) {
        return orderService.cancelOrder(orderId);
    }


}
