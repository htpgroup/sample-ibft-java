package com.omipay.demo.controller;



public class CashoutRequest {

    private String requestId;

    private String merchantId;

    private String merchantEmail;

    private String bankName;

    private String  bankAccountNumber;

    private String bankAccountName;

    private String amount;

    private String note;

    private String timeTranfer;


    private String channelId = "web";


    private String secureChain;

    private String passcode="";

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantEmail() {
        return merchantEmail;
    }

    public void setMerchantEmail(String merchantEmail) {
        this.merchantEmail = merchantEmail;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTimeTranfer() {
        return timeTranfer;
    }

    public void setTimeTranfer(String timeTranfer) {
        this.timeTranfer = timeTranfer;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getSecureChain() {
        return secureChain;
    }

    public void setSecureChain(String secureChain) {
        this.secureChain = secureChain;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    @Override
    public String toString() {
        return "CashoutRequest{" +
                "requestId='" + requestId + '\'' +
                ", merchantId='" + merchantId + '\'' +
                ", merchantEmail='" + merchantEmail + '\'' +
                ", bankName='" + bankName + '\'' +
                ", bankAccountNumber='" + bankAccountNumber + '\'' +
                ", bankAccountName='" + bankAccountName + '\'' +
                ", amount='" + amount + '\'' +
                ", note='" + note + '\'' +
                ", timeTranfer='" + timeTranfer + '\'' +
                ", channelId='" + channelId + '\'' +
                ", secureChain='" + secureChain + '\'' +
                ", passcode='" + passcode + '\'' +
                '}';
    }
}