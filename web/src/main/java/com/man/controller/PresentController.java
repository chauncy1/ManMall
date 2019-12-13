package com.man.controller;

import com.man.service.PresentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/present")
public class PresentController {

    @Autowired
    PresentService presentService;

    @PostMapping("/minusCount")
    public int minusPresentCount(@RequestParam String id, @RequestParam Integer count) {
        int result = 0;
        try {
            result = presentService.minusPresentCount(id, count);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    @PostMapping("/minusCountRedis")
    public int minusPresentCountbyRedis(@RequestParam String id, @RequestParam Integer count) {
        int result = 0;
        result = presentService.minusPresentCountByRedis(id, count);
        log.info("minusCountRedis result: " + result);
        return result;
    }

    @PostMapping("/minusCountSema")
    public int minusPresentCountbySemaphore(@RequestParam String id, @RequestParam Integer count) {
        int result = 0;
        try {
            result = presentService.minusPresentCountBySemaphore(id, count);
            log.info("minusCountSema result: " + result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    @PostMapping("/minusCountInte")
    public int minusPresentCountbyRedis2(@RequestParam String id, @RequestParam Integer count) {
        int result = 0;
        try {
            result = presentService.minusPresentCountByRedisLock(id, count);
            log.info("minusCountSema result: " + result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
