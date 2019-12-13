package com.man.controller;

import com.man.common.result.CommonResult;
import com.man.service.MqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/mq")
public class MqController {

    @Autowired
    MqService mqService;

    @PostMapping("/pay")
    public CommonResult<String> payAction(@RequestParam String message, @RequestParam String userName, @RequestParam String source) {
        return mqService.payAction(message, userName, source);
    }

    @PostMapping("/login")
    public CommonResult<String> loginAction(@RequestParam String message, @RequestParam String userName, @RequestParam String source) {
        return mqService.loginAction(message, userName, source);
    }

    @PostMapping("/casual")
    public CommonResult<String> casual() {
        return mqService.casual();
    }

}
