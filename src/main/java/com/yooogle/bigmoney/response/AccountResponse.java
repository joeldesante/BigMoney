package com.yooogle.bigmoney.response;

import com.yooogle.bigmoney.account.Account;
import com.yooogle.bigmoney.response.Response;
import com.yooogle.bigmoney.response.ResponseType;

public class AccountResponse extends Response {

    private Account account;
    private ResponseType type;
    private String reason;

    public AccountResponse(Account account, ResponseType type, String reason) {
        super(type, reason);
        this.account = account;
    }

    public Account getAccount() {
        return this.account;
    }

}
