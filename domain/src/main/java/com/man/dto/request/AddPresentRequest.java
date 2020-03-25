package com.man.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddPresentRequest {
    @NotBlank(message = "礼品名不能为空")
    private String presentName;

    @PositiveOrZero(message = "礼品数量为正数")
    private Integer presentCount;

    @PositiveOrZero(message = "礼品分数为正数")
    private Integer presentScore;

    private String presentDesc;
}
