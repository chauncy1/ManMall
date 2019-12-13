package com.man.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TokenInfoDTO implements Serializable {
    private static final long serialVersionUID = -909933341405244133L;

    private Long id;
    private String appId;
    private byte[] token;
    private String buildTime;
    private String expireTime;

}
