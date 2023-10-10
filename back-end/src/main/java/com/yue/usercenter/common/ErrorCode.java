package com.yue.usercenter.common;

// 讲师 【coder_鱼_皮】 https://space.bilibili.com/12890453/

/**
 * 错误码
 *
 */
public enum ErrorCode {

    SUCCESS(0, "ok", ""),
    PARAMS_ERROR(40000, "Request parameter error", ""),
    NULL_ERROR(40001, "Request data is empty", ""),
    NOT_LOGIN(40100, "Not logged in", ""),
    NO_AUTH(40101, "No authority", ""),
    SYSTEM_ERROR(50000, "Internal error", "");

    private final int code;

    /**
     * 状态码信息
     */
    private final String message;

    /**
     * 状态码描述（详情）
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    // https://t.zsxq.com/0emozsIJh

    public String getDescription() {
        return description;
    }
}
