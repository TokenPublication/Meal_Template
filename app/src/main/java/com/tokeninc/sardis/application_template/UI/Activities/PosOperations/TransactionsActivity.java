package com.tokeninc.sardis.application_template.UI.Activities.PosOperations;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.token.uicomponents.infodialog.InfoDialog;
import com.tokeninc.sardis.application_template.BaseActivity;
import com.tokeninc.sardis.application_template.Helpers.DataBase.DataModel;
import com.tokeninc.sardis.application_template.Helpers.DataBase.DatabaseHelper;
import com.tokeninc.sardis.application_template.Helpers.Adapters.TransactionsRecycleAdapter;
import com.tokeninc.sardis.application_template.R;

import java.util.ArrayList;
import java.util.List;

/*
*
* This Activity is created to show transactions from internal Database.
* Can be modified to use to list transactions under the "Void Menu".
*
* */

public class TransactionsActivity extends BaseActivity implements View.OnClickListener {
    DatabaseHelper databaseHelper;
    List<DataModel> dataModel;

    private RecyclerView rvTransactions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        databaseHelper = new DatabaseHelper(this);

        rvTransactions = findViewById(R.id.rvTransactions);
        dataModel =new ArrayList<DataModel>();
        CheckTable();
    }

    public void CheckTable(){
        boolean empty = databaseHelper.CheckTableIsEmpty();
        if(empty){
            InfoDialog dialog = showInfoDialog(InfoDialog.InfoType.Info, getString(R.string.no_trans_found), false);
            new Handler().postDelayed(() -> {
                dialog.dismiss();
                setResult(Activity.RESULT_CANCELED);
                super.onBackPressed();
            }, 2000);
        }
        else{
            readDataSQLite();
        }
    }

    public void readDataSQLite(){
        databaseHelper = new DatabaseHelper(TransactionsActivity.this);
        dataModel =  databaseHelper.getData();
        TransactionsRecycleAdapter adapter = new TransactionsRecycleAdapter(dataModel);
        rvTransactions.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
    }
}
