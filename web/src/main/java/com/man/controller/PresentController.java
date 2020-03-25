package com.man.controller;

import com.man.dto.request.AddPresentRequest;
import com.man.service.PresentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/present")
public class PresentController {

    @Autowired
    PresentService presentService;

    @PostMapping("/minusCount")
    public int minusPresentCount(@RequestParam String id, @RequestParam Integer count) throws InterruptedException {
        int result = presentService.minusPresentCount(id, count);
        return result;
    }

    @PostMapping("/minusCountRedis")
    public int minusPresentCountbyRedis(@RequestParam String id, @RequestParam Integer count) {
        int result = presentService.minusPresentCountByRedis(id, count);
        log.info("minusCountRedis result: " + result);
        return result;
    }

    @PostMapping("/minusCountSema")
    public int minusPresentCountbySemaphore(@RequestParam String id, @RequestParam Integer count) throws InterruptedException {
        int result = presentService.minusPresentCountBySemaphore(id, count);
        log.info("minusCountSema result: " + result);
        return result;
    }

    @PostMapping("/minusCountInte")
    public int minusPresentCountbyRedis2(@RequestParam String id, @RequestParam Integer count) throws InterruptedException {
        int result = 0;
        result = presentService.minusPresentCountByRedisLock(id, count);
        log.info("minusCountSema result: " + result);
        return result;
    }

    @PostMapping("/add")
    public int addPresent(@RequestBody AddPresentRequest addReq) {
        int result = presentService.insertRequest(addReq);
        log.info("add result: " + result);
        return result;
    }

    @GetMapping("/test/transaction")
    public void addPresent() {
        presentService.testTransaction();
    }
}
