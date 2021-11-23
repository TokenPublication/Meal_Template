package com.tokeninc.sardis.application_template.UI.Activities.PosOperations;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.token.uicomponents.infodialog.InfoDialog;
import com.tokeninc.sardis.application_template.BaseActivity;
import com.tokeninc.sardis.application_template.Entity.CardReadType;
import com.tokeninc.sardis.application_template.Entity.ICCCard;
import com.tokeninc.sardis.application_template.Entity.ICard;
import com.tokeninc.sardis.application_template.Entity.MSRCard;
import com.tokeninc.sardis.application_template.Entity.ResponseCode;
import com.tokeninc.sardis.application_template.Helpers.DataBase.DatabaseHelper;
import com.tokeninc.sardis.application_template.R;
import com.tokeninc.sardis.application_template.UI.Activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

/*
*
* This Activity is triggered from PaymentGateway to demonstrate "Refund" and "Void" actions.
*
* */

public class VoidActivity extends BaseActivity {

    private ICard card;
    private String amount = "";
    DatabaseHelper databaseHelper;
    String  batch_no;
    private int resultCode = Activity.RESULT_OK;
    public int Amount = 0;
    public String refNo = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_void);

        databaseHelper = new DatabaseHelper(this);
        checkExtras();
    }

    private void checkExtras() {
        if (getIntent().getExtras() == null || getIntent().getExtras().getString("RefundInfo") == null) {
            finish();
            Intent myIntent = new Intent(VoidActivity.this, MainActivity.class);
            startActivity(myIntent);
        }
        else {
            String refundInfo = getIntent().getStringExtra("RefundInfo");
            try {
                JSONObject json = new JSONObject(refundInfo);

                if (json.has("RefNo")) {
                    refNo = json.getString("RefNo");
                }
                this.Amount = json.getInt("Amount");

                if(!refNo.equals("") && Amount != 0) {
                    getSaleData(refNo);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                finishWithResult();
            }
            }
        }

    public void getSaleData(String myCode){
        databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String currentBatchNo = String.valueOf(databaseHelper.getBatchNo());

        Cursor cursor = db.rawQuery("SELECT * FROM sale_table WHERE sale_id = " + myCode, null);

        if (cursor.moveToNext()) {
            batch_no = cursor.getString(cursor.getColumnIndexOrThrow("batch_no"));
            amount = cursor.getString(cursor.getColumnIndexOrThrow("sale_amount"));
            cursor.close();
        }

        if (!currentBatchNo.equals(batch_no)){
            /* REFUND */
            showRefund();
        }
        else{
            /* VOID */
            showVoid();
            Amount = Integer.parseInt(amount);
        }
        db.close();

    }

    private void readCard() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("forceOnline", 1);
            obj.put("zeroAmount", 0);
            obj.put("fallback", 1);

            //obj.put("cardReadTypes", 5);

            obj.put("showAmount", 1);
            cardServiceBinding.getCard(Amount, 40, obj.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showVoid(){
        InfoDialog dialog = showInfoDialog(InfoDialog.InfoType.Progress, getString(R.string.trans_cancelling), false);
        new Handler().postDelayed(() -> {
            dialog.dismiss();
            readCard();
        }, 3000);
    }

    public void showRefund(){
        InfoDialog dialog = showInfoDialog(InfoDialog.InfoType.Progress, getString(R.string.trans_refunding), false);
        new Handler().postDelayed(() -> {
            dialog.dismiss();
            readCard();
        }, 3000);
    }


    private void takeOutICC() {
        cardServiceBinding.takeOutICC(40);
    }

    private void showInfoDialog() {
        InfoDialog dialog = showInfoDialog(InfoDialog.InfoType.Progress, getString(R.string.connecting), false);
        new Handler().postDelayed(() -> {
            dialog.update(InfoDialog.InfoType.Confirmed,  getString(R.string.trans_successful) +"\n" +getString(R.string.confirmation_code) +": 000385");
            new Handler().postDelayed(() -> {
                dialog.update(InfoDialog.InfoType.Progress, getString(R.string.printing_the_receipt));
                new Handler().postDelayed(() -> {
                    dialog.dismiss();
                    if (card instanceof ICCCard)
                        takeOutICC();
                    else {
                        finishSale(ResponseCode.SUCCESS);
                        finishWithResult();
                    }
                }, 3000);
            }, 3000);
        }, 3000);
    }

    private void finishSale(ResponseCode code) {
        Bundle bundle = new Bundle();
        bundle.putInt("ResponseCode", code.ordinal());
        if (card != null) {
            bundle.putString("CardOwner", card.getOwnerName());
            bundle.putString("CardNumber", card.getCardNumber());
        }
        Intent result = new Intent();
        result.putExtras(bundle);
        setResult(Activity.RESULT_OK, result);

        finishWithResult();
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    public void onCardDataReceived(String cardData) {

        try {
            JSONObject json = new JSONObject(cardData);
            int type = json.getInt("mCardReadType");

            if (type == CardReadType.CLCard.value) {
                showInfoDialog();
            }

            if (type == CardReadType.ICC.value) {
                ICCCard card = new Gson().fromJson(cardData, ICCCard.class);
                this.card = card;
                showInfoDialog();
            }if (type == CardReadType.ICC2MSR.value || type == CardReadType.MSR.value || type == CardReadType.KeyIn.value) {
                MSRCard card = new Gson().fromJson(cardData, MSRCard.class);
                this.card = card;
                cardServiceBinding.getOnlinePIN(Integer.parseInt(amount), card.getCardNumber(), 0x0A01, 0, 4, 8, 30);
                //TODO Do transaction after pin verification
            }
            //TODO
            //..check and process other read types
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPinReceived(String pin) {
        showInfoDialog();
    }

    @Override
    public void onICCTakeOut() {
        finishSale(ResponseCode.SUCCESS);
    }
    private void finishWithResult() {
        setResult(resultCode);
        finish();
    }
}
