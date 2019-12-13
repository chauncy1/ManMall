package com.man.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresentInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long presentId;
    private String presentName;
    private Integer presentCount;
    private Integer presentScore;
    private String presentDesc;

}
