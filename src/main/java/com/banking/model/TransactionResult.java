package com.banking.model;

import java.sql.Timestamp;

public class TransactionResult {
    private boolean success;
    private Integer transactionId;
    private String errorCode;
    private String errorMessage;
    private Timestamp timestamp;

    public TransactionResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public TransactionResult(boolean success, Integer transactionId, String errorMessage) {
        this.success = success;
        this.transactionId = transactionId;
        this.errorMessage = errorMessage;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
