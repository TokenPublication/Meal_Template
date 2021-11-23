package com.tokeninc.sardis.application_template.UI.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.token.uicomponents.ListMenuFragment.IAuthenticator;
import com.token.uicomponents.ListMenuFragment.IListMenuItem;
import com.token.uicomponents.ListMenuFragment.ListMenuFragment;
import com.token.uicomponents.ListMenuFragment.MenuItemClickListener;
import com.token.uicomponents.infodialog.InfoDialog;
import com.tokeninc.sardis.application_template.BaseActivity;
import com.tokeninc.sardis.application_template.R;

import java.util.ArrayList;
import java.util.List;

/*
 *
 * [InfoDialog](https://developer.tokeninc.com/pos-projects/token%20integrations/ui-ux/ui-components)
 *
 * */

public class InfoDialogActivity extends BaseActivity {

    class InfoDialogItem implements IListMenuItem {

        private InfoDialog.InfoType mType;
        private String mText;
        private MenuItemClickListener mListener;
        private IAuthenticator mAuthenticator;

        public InfoDialogItem(InfoDialog.InfoType type, String text, MenuItemClickListener listener, IAuthenticator authenticator) {
            mType = type;
            mText = text;
            mAuthenticator = authenticator;
            mListener = listener;
        }

        @Override
        public String getName() {
            return mText;
        }

        @Nullable
        @Override
        public List<IListMenuItem> getSubMenuItemList() {
            return null;
        }

        @Nullable
        @Override
        public MenuItemClickListener getClickListener() {
            return mListener;
        }

        @Nullable
        @Override
        public IAuthenticator getAuthenticator() {
            return mAuthenticator;
        }
    }

    private List<IListMenuItem> menuItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos_txn);

        prepareData();
        ListMenuFragment fragment = ListMenuFragment.newInstance(menuItems, "Info Dialog", true, R.drawable.token_logo);
        addFragment(R.id.container, fragment, false);
    }

    private void prepareData() {
        MenuItemClickListener listener = new MenuItemClickListener<InfoDialogItem>() {
            @Override
            public void onClick(InfoDialogItem item) {
                showPopup(item);
            }
        };

        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Confirmed, "Confirmed", listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Warning, "Warning", listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Error, "Error", listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Info,"Info", listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Declined, "Declined", listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Connecting,"Connecting", listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Downloading, "Downloading", listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Uploading, "Uploading", listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Processing, "Processing", listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Progress, "Progress", listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.None, "None", listener, null));

    }

    private void showPopup(InfoDialogItem item){
        InfoDialog dialog = showInfoDialog(item.mType, item.mText, true);
        //Dismiss dialog by calling dialog.dismiss() when needed.
    }

    public void onPosTxnResponse() {
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        //bundle.putString("ResponseCode", PosTxnResponse);
        resultIntent.putExtras(bundle);
        setResult(Activity.RESULT_OK, resultIntent);//PosTxn_Request_Code:13
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }
}
