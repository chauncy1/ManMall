package com.man.dto.response;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BaseResponse<T> {

    /**
     * 响应码
     */
    private String Code;
    /**
     * 响应信息描述
     */
    private String Message;
    private T body;

    public BaseResponse(String Code, String Message) {
        this.Code = Code;
        this.Message = Message;
    }

    /**
     * @param body
     * @return
     */
    public static BaseResponse newInstanceSuccess(Object body) {
        BaseResponse response = new BaseResponse("000000", "成功");
        response.setBody(body);
        return response;
    }


    /**
     * 返回成功信息
     *
     * @return
     */
    public static BaseResponse newInstanceSuccess() {
        return new BaseResponse("000000", "成功");
    }

    /**
     * 返回失败信息
     *
     * @return
     */
    public static BaseResponse newInstanceFailed() {
        return new BaseResponse("999999", "失败");
    }

    public static BaseResponse newInstanceFailed(String msg) {
        return new BaseResponse("999999", msg);
    }


}
