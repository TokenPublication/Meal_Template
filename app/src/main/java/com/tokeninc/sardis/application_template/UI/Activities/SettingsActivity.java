package com.tokeninc.sardis.application_template.UI.Activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.token.uicomponents.CustomInput.CustomInputFormat;
import com.token.uicomponents.CustomInput.EditTextInputType;
import com.token.uicomponents.CustomInput.InputListFragment;
import com.token.uicomponents.CustomInput.InputValidator;
import com.token.uicomponents.ListMenuFragment.IListMenuItem;
import com.token.uicomponents.ListMenuFragment.ListMenuFragment;
import com.token.uicomponents.infodialog.InfoDialog;
import com.tokeninc.sardis.application_template.BaseActivity;
import com.tokeninc.sardis.application_template.Helpers.DataBase.DatabaseHelper;
import com.tokeninc.sardis.application_template.Helpers.PrintHelpers.PrintHelper;
import com.tokeninc.sardis.application_template.R;
import com.tokeninc.sardis.application_template.UI.Definitions.MenuItem;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/*
 *
 * This activity demonstrates the usage of the Settings menus, can be copied and used exactly same other than dummy responses.
 * Settings Activity has to appear under TLauncher > Settings > System Settings > Service Applications > Banking Setup > Application Template.
 *
 * */

public class SettingsActivity extends BaseActivity {

    private InputListFragment hostFragment, TidMidFragment;
    private ListMenuFragment menuFragment;
    private String terminalId;
    private String merchantId, ip_no, port_no;
    private static Context context;
    DatabaseHelper databaseHelper;
    private boolean isFoodActivateAction = true, isFoodUpdateParameterAction = true, DB_getAllTransactionsCount = true;

    public SettingsActivity() {
    }

    @Override
    protected int getTimeOutSec() {
        return TIME_OUT;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SettingsActivity.context = getApplicationContext();
        databaseHelper = new DatabaseHelper(this);

        isFoodActivateAction = getIntent() != null && getIntent().getAction() != null
                && getIntent().getAction().equals("Activate_Food");
        isFoodUpdateParameterAction = getIntent() != null && getIntent().getAction() != null
                && getIntent().getAction().equals("Update_Parameter");
        if (isFoodActivateAction) {
            terminalId = getIntent().getStringExtra("terminalID");
            merchantId = getIntent().getStringExtra("merchantID");
            startActivation(terminalId, merchantId);
        } else if (isFoodUpdateParameterAction) {
            terminalId = getIntent().getStringExtra("terminalID");
            merchantId = getIntent().getStringExtra("merchantID");
            startUpdateParameter(terminalId, merchantId);
        } else {
            showMenu();
        }
    }

    private void showMenu() {
        List<IListMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(getString(R.string.setup), iListMenuItem -> {
            addTidMidFragment();
        }));
        menuItems.add(new MenuItem(getString(R.string.host_settings), iListMenuItem -> addIpFragment()));

        menuFragment = ListMenuFragment.newInstance(menuItems, getString(R.string.settings),
                true, R.drawable.token_logo);
        addFragment(R.id.container, menuFragment, false);

    }

    private void addIpFragment() {
        List<CustomInputFormat> inputList = new ArrayList<>();
        inputList.add(new CustomInputFormat("IP", EditTextInputType.IpAddress, null, getString(R.string.invalid_ip), new InputValidator() {
            @Override
            public boolean validate(CustomInputFormat customInputFormat) {
                String text = customInputFormat.getText();
                boolean isValid = StringUtils.countMatches(text, ".") == 3 && text.split("\\.").length == 4;
                if (isValid) {
                    String[] array = text.split("\\.");
                    int index = 0;
                    while (isValid && index < array.length) {
                        isValid = StringUtils.isNumeric(array[0]);
                        index++;
                    }
                }
                return isValid;
            }
        }));

        inputList.add(new CustomInputFormat("Port", EditTextInputType.Number, 4, getString(R.string.invalid_port), new InputValidator() {
            @Override
            public boolean validate(CustomInputFormat customInputFormat) {
                return customInputFormat.getText().length() >= 2 && Integer.parseInt(customInputFormat.getText()) > 0;
            }
        }));
        inputList.get(0).setText(databaseHelper.getIP_NO());
        inputList.get(1).setText(databaseHelper.getPort());

        hostFragment = InputListFragment.newInstance(inputList, getString(R.string.save), new InputListFragment.ButtonListener() {
            @Override
            public void onButtonAction(List<String> list) {
                // TODO If Batch Close SUCCESS

                // Dummy response for Host and IP settings.
                InfoDialog dialog = showInfoDialog(InfoDialog.InfoType.Progress, getString(R.string.batch_close), false);
                new Handler().postDelayed(() -> {
                    dialog.update(InfoDialog.InfoType.Confirmed, getString(R.string.batch_close) +": " +getString(R.string.success));
                    databaseHelper.batchClose();
                new Handler().postDelayed(() -> {
                    // TODO Set Host Settings
                    dialog.update(InfoDialog.InfoType.Confirmed, getString(R.string.activation_completed));

                    ip_no = inputList.get(0).getText();
                    port_no = inputList.get(1).getText();

                    databaseHelper.updateIP_NO(ip_no);
                    databaseHelper.updatePort(port_no);

                    new Handler().postDelayed(() -> {
                        dialog.dismiss();
                        }, 2000);
                    }, 2000);
                }, 2000);
            }
        });
        addFragment(R.id.secondContainer, hostFragment, true);
    }

    private void addTidMidFragment() {
        List<CustomInputFormat> inputList = new ArrayList<>();
        inputList.add(new CustomInputFormat(getString(R.string.merchant_no), EditTextInputType.Number, 10, getString(R.string.invalid_merchant_no), new InputValidator() {
                @Override
                public boolean validate(CustomInputFormat input) {
                    return input.getText().length() == 10;
                }
        }));

        inputList.add(new CustomInputFormat(getString(R.string.terminal_no), EditTextInputType.Text, 8, getString(R.string.invalid_terminal_no), new InputValidator() {
            @Override
            public boolean validate(CustomInputFormat input) {
                return input.getText().length() == 8;
            }
        }));

        inputList.get(0).setText(databaseHelper.getMerchantId());
        inputList.get(1).setText(databaseHelper.getTerminalId());

        TidMidFragment = InputListFragment.newInstance(inputList, getString(R.string.save), new InputListFragment.ButtonListener() {
            @Override
            public void onButtonAction(List<String> list) {

                merchantId = inputList.get(0).getText();
                terminalId = inputList.get(1).getText();

                databaseHelper.updateMerchantId(merchantId);
                databaseHelper.updateTerminalId(terminalId);

                startActivation("","");
            }
        });
        addFragment(R.id.thirdContainer, TidMidFragment, true);
    }

    // Dummy activation response
    private void startActivation(String terminalId, String merchantId) {
        Log.i("SettingsActivity", "Start Activation called with terminalId " + terminalId + "merchantId " + merchantId);
        if (DB_getAllTransactionsCount) {

            InfoDialog dialog = showInfoDialog(InfoDialog.InfoType.Processing, getString(R.string.starting_activation), false);
                new Handler(Looper.myLooper()).postDelayed(() -> {
                    dialog.update(InfoDialog.InfoType.Progress, getString(R.string.parameter_loading));
                    new Handler().postDelayed(() -> {
                        dialog.update(InfoDialog.InfoType.Confirmed, getString(R.string.member_act_completed));
                        new Handler().postDelayed(() -> {
                            dialog.update(InfoDialog.InfoType.Progress, getString(R.string.rkl_loading));
                        new Handler().postDelayed(() -> {
                            dialog.update(InfoDialog.InfoType.Confirmed, getString(R.string.rkl_loaded));
                            new Handler().postDelayed(() -> {
                                dialog.update(InfoDialog.InfoType.Progress, getString(R.string.key_block_loading));
                                new Handler().postDelayed(() -> {
                                    dialog.update(InfoDialog.InfoType.Confirmed, getString(R.string.activation_completed));
                            new Handler().postDelayed(() -> {
                                dialog.dismiss();
                                printService.print(PrintHelper.PrintSuccessWithIdentification(terminalId, merchantId)); // Print success
                                new Handler().postDelayed(() -> {
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                }, 1000);
                                  }, 2000);
                             }, 2000);
                            }, 2000);
                        }, 2000);
                    }, 2000);
                    }, 2000);
                }, 2000);
            }

        else {
            new Handler().postDelayed(() -> {
            InfoDialog progress = showInfoDialog(InfoDialog.InfoType.Progress, getString(R.string.parameter_loading), false);
                new Handler().postDelayed(() -> {
                    setResult(Activity.RESULT_OK);
                    finish();
                }, 1000);
            }, 2000);
        }
    }

    private void startUpdateParameter(String terminalId, String merchantId) {
        // update parameter
        Log.i("SettingsActivity", "Start Update Parameter called with terminalId " + terminalId + "merchantId " + merchantId);
        printService.print(PrintHelper.PrintSuccessWithIdentification(terminalId, merchantId)); // Print success
        new Handler().postDelayed(() -> {
            setResult(Activity.RESULT_OK);
            finish();
        }, 5000);
    }
}
