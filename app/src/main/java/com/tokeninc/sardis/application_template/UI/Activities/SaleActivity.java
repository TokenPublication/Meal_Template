package com.tokeninc.sardis.application_template.UI.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.token.uicomponents.ListMenuFragment.IListMenuItem;
import com.token.uicomponents.ListMenuFragment.ListMenuFragment;
import com.token.uicomponents.infodialog.InfoDialog;
import com.tokeninc.sardis.application_template.BaseActivity;
import com.tokeninc.sardis.application_template.Entity.CardReadType;
import com.tokeninc.sardis.application_template.Entity.ICCCard;
import com.tokeninc.sardis.application_template.Entity.ICard;
import com.tokeninc.sardis.application_template.Entity.MSRCard;
import com.tokeninc.sardis.application_template.Entity.ResponseCode;
import com.tokeninc.sardis.application_template.Helpers.DataBase.DatabaseHelper;
import com.tokeninc.sardis.application_template.Helpers.StringHelper;
import com.tokeninc.sardis.application_template.R;
import com.tokeninc.sardis.application_template.UI.Definitions.MenuItem;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SaleActivity extends BaseActivity implements View.OnClickListener {

    private int amount = 0;

    private List<IListMenuItem> menuItemList;
    private ICard card;

    DatabaseHelper databaseHelper;

    int cardReadType = 0;
    String cardData;
    String cardNumber = "**** ****";
    String cardOwner = "";

    protected String qrString = "QR Code Test";
    private boolean QRisSuccess = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);
        //Prevent screen from turning of when sale is active
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        databaseHelper = new DatabaseHelper(this);

        Bundle bundle = getIntent().getExtras();
        amount = bundle.getInt("Amount");
        cardReadType = bundle.getInt("CardReadType");
        cardData = bundle.getString("CardData");

        if (cardReadType == CardReadType.NONE.value || cardReadType == CardReadType.ICC.value) {
            readCard();
        }else{
            getCardDataFromBundle();
            prepareSaleMenu();
        }
    }

    public void getCardDataFromBundle(){
        if (cardReadType == CardReadType.MSR.value ) {
            if(getIntent().getExtras().getString("CardData") != null) {
                String cardData = getIntent().getStringExtra("CardData");
                try {
                    ICard card = new Gson().fromJson(cardData, MSRCard.class);
                    this.card = card;
                    cardServiceBinding.getOnlinePIN(amount, card.getCardNumber(), 0x0A01, 0, 4, 8, 30);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void prepareSaleMenu() {
        menuItemList = new ArrayList<>();
        menuItemList.add(new MenuItem(getString(R.string.sale), (menuItem) -> showInfoDialog()));
        menuItemList.add(new MenuItem(getString(R.string.installment_sale), (menuItem) -> showInfoDialog()));
        menuItemList.add(new MenuItem(getString(R.string.loyalty_sale), (menuItem) -> showInfoDialog()));
        menuItemList.add(new MenuItem(getString(R.string.campaign_sale), (menuItem) -> showInfoDialog()));

        ListMenuFragment fragment = ListMenuFragment.newInstance(menuItemList, getString(R.string.sale_type), false, null);
        addFragment(R.id.container, fragment, false);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSetConfig:
                setConfig();
                break;
            case R.id.btnSetCLConfig:
                setCLConfig();
                break;
        }
    }

    /**
     * Read card data and return result with data back to payment gateway.
     * @see DummySaleActivity onSaleResponseRetrieved(Integer, ResponseCode, Boolean, SlipType)
     *
     */
    private void readCard() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("forceOnline", 1);
            obj.put("zeroAmount", 0);
            obj.put("fallback", 1);
            obj.put("cardReadTypes",6);
            obj.put("qrPay", 1);

            if(cardReadType == CardReadType.ICC.value) {
                obj.put("showCardScreen", 0);
            }

            cardServiceBinding.getCard(amount, 40, obj.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void takeOutICC() {
        cardServiceBinding.takeOutICC(40);
    }

    protected void QrSale() {
        InfoDialog dialog = showInfoDialog(InfoDialog.InfoType.Progress, "Please Wait", true);
        // Request to Show a QR Code ->
        cardServiceBinding.showQR("PLEASE READ THE QR CODE", StringHelper.getAmount(amount), qrString); // Shows QR on the back screen
        dialog.setQr(qrString, "Waiting For the QR Code to Read"); // Shows the same QR on Info Dialog
        // Request a QR Response ->
            if (QRisSuccess) {
                // Dummy Response
                new Handler().postDelayed(() -> {
                    dialog.update(InfoDialog.InfoType.Confirmed,"QR " +getString(R.string.trans_successful));
                    new Handler().postDelayed(() -> {
                    dialog.dismiss();
                    finish();
                    }, 5000);
                }, 3000);
            }
            else {
                dialog.update(InfoDialog.InfoType.Declined, "Error");
            }
        dialog.setDismissedListener(() -> {
            // You can call your QR Payment Cancel method here
            setResult(Activity.RESULT_CANCELED);
            finish();
        });
    }

    private void showInfoDialog() {
        InfoDialog dialog = showInfoDialog(InfoDialog.InfoType.Progress, getString(R.string.connecting), false);
        new Handler().postDelayed(() -> {
            dialog.update(InfoDialog.InfoType.Confirmed, getString(R.string.trans_successful) +"\n" +getString(R.string.confirmation_code) +": " +StringHelper.GenerateApprovalCode(String.valueOf(databaseHelper.getBatchNo()), String.valueOf(databaseHelper.getTxNo()), String.valueOf(databaseHelper.getSaleID())));
            new Handler().postDelayed(() -> {
                dialog.update(InfoDialog.InfoType.Progress, getString(R.string.printing_the_receipt));
                new Handler().postDelayed(() -> {
                    dialog.dismiss();
                    if (card instanceof ICCCard)
                        takeOutICC();
                    else {
                        finishSale(ResponseCode.SUCCESS);
                    }
                }, 2000);
            }, 2000);
        }, 2000);
    }

    public void finishSale(ResponseCode code) {
        // Finish the sale and return the values to the DummySaleActivity
        Bundle bundle = new Bundle();
        bundle.putInt("ResponseCode", code.ordinal()); // #1 Response Code

        bundle.putInt("sCardReadType", cardReadType);

        if(cardReadType != CardReadType.CLCard.value) {
            bundle.putString("sCardOwner", card.getOwnerName());
            bundle.putString("sCardNumber", card.getCardNumber());
        }
        else{
            bundle.putString("sCardOwner", cardOwner);
            bundle.putString("sCardNumber", cardNumber);
        }

        Intent result = new Intent();
        result.putExtras(bundle);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    private void setConfig() {
        try {
            InputStream xmlStream = getApplicationContext().getAssets().open("custom_emv_config.xml");
            BufferedReader r = new BufferedReader(new InputStreamReader(xmlStream));
            StringBuilder total = new StringBuilder();
            for (String line; (line = r.readLine()) != null; ) {
                Log.d("emv_config", "conf line: " + line);
                total.append(line).append('\n');
            }
            int setConfigResult = cardServiceBinding.setEMVConfiguration(total.toString());
            Toast.makeText(getApplicationContext(), "setEMVConfiguration res=" + setConfigResult, Toast.LENGTH_SHORT).show();
            Log.d("emv_config", "setEMVConfiguration: " + setConfigResult);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCLConfig() {
        try {
            InputStream xmlCLStream = getApplicationContext().getAssets().open("custom_emv_cl_config.xml");
            BufferedReader rCL = new BufferedReader(new InputStreamReader(xmlCLStream));
            StringBuilder totalCL = new StringBuilder();
            for (String line; (line = rCL.readLine()) != null; ) {
                Log.d("emv_config", "conf line: " + line);
                totalCL.append(line).append('\n');
            }
            int setCLConfigResult = cardServiceBinding.setEMVCLConfiguration(totalCL.toString());
            Toast.makeText(getApplicationContext(), "setEMVCLConfiguration res=" + setCLConfigResult, Toast.LENGTH_SHORT).show();
            Log.d("emv_config", "setEMVCLConfiguration: " + setCLConfigResult);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCardServiceConnected() { }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    public void onCardDataReceived(String cardData) {

        try {
            prepareSaleMenu();

            JSONObject json = new JSONObject(cardData);
            int type = json.getInt("mCardReadType");

            if (type == CardReadType.QrPay.value) {
                QrSale();
                return;
            }

            if (type == CardReadType.CLCard.value) {
                cardReadType = CardReadType.CLCard.value;
                ICCCard card = new Gson().fromJson(cardData, ICCCard.class);
                this.card = card;
            }
            else if (type == CardReadType.ICC.value) {
                ICCCard card = new Gson().fromJson(cardData, ICCCard.class);
                this.card = card;
            }
            else if (type == CardReadType.ICC2MSR.value || type == CardReadType.MSR.value || type == CardReadType.KeyIn.value) {
                MSRCard card = new Gson().fromJson(cardData, MSRCard.class);
                this.card = card;
                cardServiceBinding.getOnlinePIN(amount, card.getCardNumber(), 0x0A01, 0, 4, 8, 30);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPinReceived(String pin) {
        prepareSaleMenu();
    }

    @Override
    public void onICCTakeOut() {
        finishSale(ResponseCode.SUCCESS);
    }
}
