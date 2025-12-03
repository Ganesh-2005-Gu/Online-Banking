package com.banking.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transaction {
    private int id;
    private String accountNumber;
    private String type;
    private BigDecimal amount;
    private String relatedAccount;
    private Timestamp timestamp;

    public Transaction() {
    }

    public Transaction(int id, String accountNumber, String type, BigDecimal amount, String relatedAccount,
            Timestamp timestamp) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.relatedAccount = relatedAccount;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRelatedAccount() {
        return relatedAccount;
    }

    public void setRelatedAccount(String relatedAccount) {
        this.relatedAccount = relatedAccount;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public boolean getIncome() {
        if ("DEPOSIT".equals(type)) {
            return true;
        }
        if ("WITHDRAWAL".equals(type)) {
            return false;
        }
        // For TRANSFER, positive amount means received (income), negative means sent
        // (expense)
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
}
