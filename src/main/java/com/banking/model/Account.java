package com.banking.model;

import java.math.BigDecimal;

public class Account {
    private String accountNumber;
    private int userId;
    private BigDecimal balance;
    private String accountType; // SAVINGS or CURRENT

    public Account() {}

    public Account(String accountNumber, int userId, BigDecimal balance, String accountType) {
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.balance = balance;
        this.accountType = accountType;
    }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
}
