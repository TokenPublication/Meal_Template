package com.tokeninc.sardis.application_template.Helpers.DataBase;

public class DataModel {

    public String card_no;
    public String process_time;
    public String sale_amount;
    public String approval_code;
    public String serial_no;
    public String merchant_id;
    public String terminal_id;
    public String batch_no;
    public String tx_no;

    public DataModel(){}

    public DataModel(String card_no, String process_time, String sale_amount, String approval_code, String serial_no, String merchant_id, String terminal_id, String batch_no, String tx_no) {
        this.card_no = card_no;
        this.process_time = process_time;
        this.sale_amount = sale_amount;
        this.approval_code = approval_code;
        this.serial_no = serial_no;
        this.merchant_id = merchant_id;
        this.terminal_id = terminal_id;
        this.batch_no = batch_no;
        this.tx_no = tx_no;
    }

    public String getCard_no() {
        return card_no;
    }

    public void setCard_no(String card_no) { this.card_no = card_no; }

    public String getProcess_time() {
        return process_time;
    }

    public void setProcess_time(String process_time) {
        this.process_time = process_time;
    }

    public String getSale_amount() {
        return sale_amount;
    }

    public void setSale_amount(String sale_amount) {
        this.sale_amount = sale_amount;
    }

    public String getApproval_code() {
        return approval_code;
    }

    public void setApproval_code(String approval_code) {
        this.approval_code = approval_code;
    }

    public String getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(String serial_no) {
        this.serial_no = serial_no;
    }

    public String getMerchant_ID() {
        return merchant_id;
    }

    public void setMerchant_ID(String merchant_id) { this.merchant_id = merchant_id; }

    public String getTerminal_ID() { return terminal_id; }

    public void setTerminal_ID(String terminal_id) { this.terminal_id = terminal_id; }

    public String getBatch_No() { return batch_no; }

    public void setBatch_no(String batch_no) { this.batch_no = batch_no; }

    public String getTx_No() { return tx_no; }

    public void setTx_No(String tx_no) { this.batch_no = tx_no; }
}
