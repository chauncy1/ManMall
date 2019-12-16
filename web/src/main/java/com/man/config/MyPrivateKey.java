package com.man.config;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class MyPrivateKey {
    private String privatekey = "helloworld";


}
