package com.man.test;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostToServerTest {

    @Test
    public void contextLoads() {//给自己发送post请求
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, Integer> map = new LinkedMultiValueMap<>();
        map.add("userId", 4);
        map.add("score", 2);
        HttpEntity<MultiValueMap<String, Integer>> requestEntity = new HttpEntity<>(map);
        Integer result = restTemplate.postForObject("http://localhost:8081/user/minusScore", requestEntity, Integer.class);

        log.info("result is : __________________________________________________________________" + result);
    }
}
