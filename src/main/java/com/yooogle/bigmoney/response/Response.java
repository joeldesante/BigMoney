package com.yooogle.bigmoney.response;

public abstract class Response {
    private ResponseType type;
    private String reason;

    public Response(ResponseType type, String reason) {
        this.type = type;
        this.reason = reason;
    }

    public String getReason() {
        return this.reason;
    }

    public ResponseType getType() {
        return this.type;
    }

    public boolean isSuccess() {
        if(this.type.equals(ResponseType.SUCCESS)) {
            return true;
        }

        return false;
    }
}
