package com.tokeninc.sardis.application_template.UI.Activities;

import android.os.Bundle;

import com.token.uicomponents.ListMenuFragment.IListMenuItem;
import com.token.uicomponents.ListMenuFragment.ListMenuFragment;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;
import com.tokeninc.sardis.application_template.BaseActivity;
import com.tokeninc.sardis.application_template.R;
import com.tokeninc.sardis.application_template.UI.Definitions.MenuItem;

import java.util.ArrayList;
import java.util.List;

/*
*
* [ConfirmationDialog](https://developer.tokeninc.com/pos-projects/token%20integrations/ui-ux/ui-components)
*
* */

public class ConfirmationDialogActivity extends BaseActivity implements InfoDialogListener {

    private List<IListMenuItem> menuItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos_txn);

        prepareData();
        ListMenuFragment fragment = ListMenuFragment.newInstance(menuItems, "Confirmation", true, R.drawable.token_logo);
        addFragment(R.id.container, fragment, false);
    }

    public void prepareData() {

        menuItems.add(new MenuItem("Confirmed", (menuItem) ->
                showConfirmationDialog(InfoDialog.InfoType.Confirmed,"Confirmed", "Confirmation: Confirmed", InfoDialog.InfoDialogButtons.Both, 99, ConfirmationDialogActivity.this)));

        menuItems.add(new MenuItem("Warning", (menuItem) ->
                showConfirmationDialog(InfoDialog.InfoType.Warning,"Warning", "Confirmation: Warning", InfoDialog.InfoDialogButtons.Both, 98, ConfirmationDialogActivity.this)));

        menuItems.add(new MenuItem("Error", (menuItem) ->
                showConfirmationDialog(InfoDialog.InfoType.Error,"Error", "Confirmation: Error", InfoDialog.InfoDialogButtons.Both, 97, ConfirmationDialogActivity.this)));

        menuItems.add(new MenuItem("Info", (menuItem) ->
                showConfirmationDialog(InfoDialog.InfoType.Info,"Info", "Confirmation: Info", InfoDialog.InfoDialogButtons.Both, 96, ConfirmationDialogActivity.this)));

        menuItems.add(new MenuItem("Declined", (menuItem) ->
                showConfirmationDialog(InfoDialog.InfoType.Declined,"Declined", "Confirmation: Declined", InfoDialog.InfoDialogButtons.Both, 95, ConfirmationDialogActivity.this)));

        menuItems.add(new MenuItem("Connecting", (menuItem) ->
                showConfirmationDialog(InfoDialog.InfoType.Connecting,"Connecting", "Confirmation: Connecting", InfoDialog.InfoDialogButtons.Both, 94, ConfirmationDialogActivity.this)));

        menuItems.add(new MenuItem("Downloading", (menuItem) ->
                showConfirmationDialog(InfoDialog.InfoType.Downloading,"Downloading", "Confirmation: Downloading", InfoDialog.InfoDialogButtons.Both, 93, ConfirmationDialogActivity.this)));

        menuItems.add(new MenuItem("Uploading", (menuItem) ->
                showConfirmationDialog(InfoDialog.InfoType.Uploading,"Uploading", "Confirmation: Uploading", InfoDialog.InfoDialogButtons.Both, 92, ConfirmationDialogActivity.this)));

        menuItems.add(new MenuItem("Processing", (menuItem) ->
                showConfirmationDialog(InfoDialog.InfoType.Processing,"Processing", "Confirmation: Processing", InfoDialog.InfoDialogButtons.Both, 91, ConfirmationDialogActivity.this)));

        menuItems.add(new MenuItem("Progress", (menuItem) ->
                showConfirmationDialog(InfoDialog.InfoType.Progress,"Progress", "Confirmation: Progress", InfoDialog.InfoDialogButtons.Both, 90, ConfirmationDialogActivity.this)));

        menuItems.add(new MenuItem("None", (menuItem) ->
                showConfirmationDialog(InfoDialog.InfoType.None,"None", "Confirmation: None", InfoDialog.InfoDialogButtons.Both, 89, ConfirmationDialogActivity.this)));
    }

    @Override
    public void confirmed(int arg) {
        if (arg == 99) {
            showInfoDialog(InfoDialog.InfoType.Confirmed, "Confirmed!", true);
        }
        //else if (arg == ***) { Do something else... }
        if (arg == 98) {
            showInfoDialog(InfoDialog.InfoType.Warning, "Warning!", true);
        }
        if (arg == 97) {
            showInfoDialog(InfoDialog.InfoType.Error, "Error!", true);
        }
        if (arg == 96) {
            showInfoDialog(InfoDialog.InfoType.Info, "Info!", true);
        }
        if (arg == 95) {
            showInfoDialog(InfoDialog.InfoType.Declined, "Declined!", true);
        }
        if (arg == 94) {
            showInfoDialog(InfoDialog.InfoType.Connecting, "Connecting!", true);
        }
        if (arg == 93) {
            showInfoDialog(InfoDialog.InfoType.Downloading, "Downloading!", true);
        }
        if (arg == 92) {
            showInfoDialog(InfoDialog.InfoType.Uploading, "Uploading!", true);
        }
        if (arg == 91) {
            showInfoDialog(InfoDialog.InfoType.Processing, "Processing!", true);
        }
        if (arg == 90) {
            showInfoDialog(InfoDialog.InfoType.Progress, "Progress!", true);
        }
        if (arg == 89) {
            showInfoDialog(InfoDialog.InfoType.None, "None!", true);
        }
    }

    @Override
    public void canceled(int arg) {
        if (arg <= 99|| arg >= 89) {
            showInfoDialog(InfoDialog.InfoType.Error, "Canceled", true);
        }
        //else if (arg == ***) { Do something else... }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

