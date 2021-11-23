package com.tokeninc.sardis.application_template;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;
import com.token.uicomponents.timeoutmanager.TimeOutActivity;
import com.tokeninc.cardservicebinding.CardServiceBinding;
import com.tokeninc.cardservicebinding.CardServiceListener;
import com.tokeninc.sardis.application_template.Helpers.PrintHelpers.PrintServiceBinding;

public abstract class BaseActivity extends TimeOutActivity implements CardServiceListener {

    protected CardServiceBinding cardServiceBinding;
    protected PrintServiceBinding printService;

    protected final int TIME_OUT = 60;
    /**
     * Returns time out value in seconds for activities which extend
     * @see TimeOutActivity
     * In case of any user interaction, timeout timer will be reset.
     *
     * If any activity will not have time out,
     * override this method from that activity and @return '0'.
     */
    @Override
    protected int getTimeOutSec() {
        return 60;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cardServiceBinding = new CardServiceBinding(this, this);
        printService = new PrintServiceBinding();
    }

    @Override
    protected void onDestroy() {
        cardServiceBinding.unBind();
        super.onDestroy();
    }

    /**
     * Callback for Card Service connected method. You will not need to check whether card service is connected or not in most cases.
     * When you make an asynchronous method call on CardServiceBinding,it will be executed automatically as soon as CardService is connected.
     */
    @Override
    public void onCardServiceConnected() {

    }

    protected void addFragment(@IdRes Integer resourceId, Fragment fragment, Boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(resourceId, fragment);
        if (addToBackStack) {
            ft.addToBackStack("");
        }
        ft.commit();
    }

    protected void replaceFragment(@IdRes Integer resourceId, Fragment fragment, Boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(resourceId, fragment);
        if (addToBackStack) {
            ft.addToBackStack("");
        }
        ft.commit();
    }

    protected void removeFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }

    /**
     * Shows a dialog to the user which informs the user about current progress.
     * See {@link InfoDialog#newInstance(InfoDialog.InfoType, String, boolean)}
     * Dialog can dismissed by calling .dismiss() method of the fragment instance returned from this method.
     */
    protected InfoDialog showInfoDialog(InfoDialog.InfoType type, String text, boolean isCancelable) {
        InfoDialog fragment = InfoDialog.newInstance(type, text, isCancelable);
        fragment.show(getSupportFragmentManager(), "");
        return fragment;
    }

    /**
     * Shows a dialog to the user which asks for a confirmation.
     * Dialog will be dismissed automatically when user taps on to confirm/cancel button.
     * See {@link InfoDialog#newInstance(InfoDialog.InfoType, String, String, InfoDialog.InfoDialogButtons, int, InfoDialogListener)}
     */
    protected InfoDialog showConfirmationDialog(InfoDialog.InfoType type, String title, String info, InfoDialog.InfoDialogButtons buttons, int arg, InfoDialogListener listener) {
        InfoDialog dialog = InfoDialog.newInstance(type, title, info, buttons, arg, listener);
        dialog.show(getSupportFragmentManager(), "");
        return dialog;
    }


    /**
     * Callback for TCardService getCard method.
     * As getCard is an asynchronous service operation, you will get the response after operation is done in this callback.
     * @apiNote Override this method in your activity in order to get card data after calling #getData() method of Card Service.
     * @param cardData: Card data json string
     */
    public void onCardDataReceived(String cardData) { }

    /**
     * Callback for TCardService getOnlinePin method.
     * As getOnlinePin is an asynchronous real time operation, you will get the response after operation is done in this callback.
     * @apiNote Override this method in your activity in order to get pin data after calling #getOnlinePin() method of Card Service.
     * @param pin: Pin string
     */
    public  void onPinReceived(String pin) {}

    /**
     * Callback for TCardService iccTakeOut method.
     *
     * @apiNote Override this method in your activity in order to get icc take out callback after calling #iccTakeOut() method of Card Service.
     */
    public  void onICCTakeOut() {}
}
