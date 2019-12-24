package com.man.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true) // 链式调用
public class PresentInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long presentId;
    private String presentName;
    private Integer presentCount;
    private Integer presentScore;
    private String presentDesc;

}
