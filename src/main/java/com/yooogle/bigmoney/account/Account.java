package com.yooogle.bigmoney.account;

import com.yooogle.bigmoney.BigMoney;
import com.yooogle.bigmoney.response.AccountResponse;
import com.yooogle.bigmoney.response.ResponseType;

import java.util.UUID;

public class Account {

    private UUID ownerUUID;
    private double balance;
    private AccountStatus accountStatus;
    private BigMoney plugin;

    public Account(UUID ownerUUID, BigMoney plugin) {
        this.ownerUUID = ownerUUID;
        this.accountStatus = AccountStatus.OPEN;
        this.balance = 0D;
        this.plugin = plugin;
    }

    public double getBalance() {
        return this.balance;
    }

    public AccountResponse setBalance(double amount) {
        if(amount < this.plugin.getConfig().getDouble("min-balance")) {
            return new AccountResponse(this, ResponseType.FAILURE, "Value exceeds bounds.");
        }

        this.balance = amount;
        return new AccountResponse(this, ResponseType.SUCCESS, "Success");
    }

    public AccountResponse addBalance(double amount) {
        double newBalance = this.balance + amount;

        if(newBalance < this.plugin.getConfig().getDouble("min-balance")) {
            return new AccountResponse(this, ResponseType.FAILURE, "Value exceeds bounds.");
        }

        this.balance = newBalance;
        return new AccountResponse(this, ResponseType.SUCCESS, "Success");
    }

    public AccountResponse subtractBalance(double amount) {
        double newBalance = this.balance - amount;

        if(newBalance < this.plugin.getConfig().getDouble("min-balance")) {
            return new AccountResponse(this, ResponseType.FAILURE, "Value exceeds bounds.");
        }

        this.balance = newBalance;
        return new AccountResponse(this, ResponseType.SUCCESS, "Success");
    }

    public AccountStatus getAccountStatus() {
        return this.accountStatus;
    }

    public void setAccountStatus(AccountStatus status) {
        this.accountStatus = status;
    }

    public UUID getOwnerUUID() { return this.ownerUUID; }

}
