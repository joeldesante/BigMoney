package com.yooogle.bigmoney.response;

import com.yooogle.bigmoney.account.Account;
import com.yooogle.bigmoney.response.Response;
import com.yooogle.bigmoney.response.ResponseType;

public class DatabaseResponse extends Response {

    private Account account = null;

    public DatabaseResponse(ResponseType type, String reason) {
        super(type, reason);
    }

    public DatabaseResponse(Account account, ResponseType type, String reason) {
        super(type, reason);
        this.account = account;
    }

    public Account getAccount() {
        return this.account;
    }

}
