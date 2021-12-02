package com.tokeninc.sardis.application_template.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tokeninc.sardis.application_template.Entity.ResponseCode;
import com.tokeninc.sardis.application_template.Entity.SlipType;
import com.tokeninc.sardis.application_template.Helpers.DataBase.DataModel;
import com.tokeninc.sardis.application_template.Helpers.DataBase.DatabaseHelper;

public class CheckSaleReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra("UUID")) {
            String uuid = intent.getExtras().getString("UUID");
            DataModel transaction = querySale(context, uuid);
            //check if a successful transaction with uuid exists
            Intent resultIntent = new Intent();
            if (transaction != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("ResponseCode", ResponseCode.SUCCESS.ordinal());
                bundle.putInt("PaymentStatus", 0);
                bundle.putInt("Amount", Integer.parseInt(transaction.sale_amount));
                bundle.putInt("Amount2", 0);
                bundle.putString("customerSlipData", "");
                bundle.putString("merchantSlipData", "");
                bundle.putInt("BatchNo", Integer.parseInt(transaction.batch_no));
                bundle.putInt("TxnNo", Integer.parseInt(transaction.tx_no));
                bundle.putInt("SlipType", SlipType.BOTH_SLIPS.value);
                bundle.putBoolean("IsSlip", true);
                resultIntent.putExtras(bundle);
                resultIntent.setAction("check_sale_result");
                resultIntent.setPackage("com.tokeninc.sardis.paymentgateway");
                context.sendBroadcast(resultIntent);
            }
        }
    }

    // Dummy
    private DataModel querySale(Context context, String uuid) {
        return new DatabaseHelper(context).getSale(uuid);
    }


}
