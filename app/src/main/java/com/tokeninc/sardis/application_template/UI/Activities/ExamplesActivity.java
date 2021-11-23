package com.tokeninc.sardis.application_template.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.token.uicomponents.ListMenuFragment.IListMenuItem;
import com.token.uicomponents.ListMenuFragment.ListMenuFragment;
import com.token.uicomponents.ListMenuFragment.MenuItemClickListener;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;
import com.token.uicomponents.numpad.NumPadDialog;
import com.token.uicomponents.numpad.NumPadListener;
import com.tokeninc.barcodescannerimpl.TokenBarcodeScanner;
import com.tokeninc.deviceinfo.DeviceInfo;
import com.tokeninc.sardis.application_template.BaseActivity;
import com.tokeninc.sardis.application_template.Helpers.StringHelper;
import com.tokeninc.sardis.application_template.R;
import com.tokeninc.sardis.application_template.UI.Definitions.MenuItem;
import com.tokeninc.sardis.application_template.Helpers.PrintHelpers.PrintServiceBinding;
import com.tokeninc.sardis.application_template.Helpers.PrintHelpers.PrintHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.tokeninc.sardis.application_template.Helpers.KeyInjectHelper.exampleKey;

public class ExamplesActivity  extends BaseActivity implements InfoDialogListener {

    private List<IListMenuItem> menuItems = new ArrayList<>();
    private PrintServiceBinding printService;

    protected int qrAmount = 100;
    protected String qrString = "QR Code Test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examples);
        printService = new PrintServiceBinding();

        prepareData();
        ListMenuFragment fragment = ListMenuFragment.newInstance(menuItems, getString(R.string.examples), false, R.drawable.token_logo);
        addFragment(R.id.container, fragment, false);
    }

    private void prepareData() {

        List<IListMenuItem> subList1 = new ArrayList<>();
        subList1.add(new MenuItem("Menu Item 1", (menuItem) -> {
            Toast.makeText(this,"Sub Menu 1", Toast.LENGTH_LONG).show();

        }, null));

        subList1.add(new MenuItem("Menu Item 2", (menuItem) -> {

            Toast.makeText(this,"Sub Menu 2", Toast.LENGTH_LONG).show();

        }, null));
        subList1.add(new MenuItem("Menu Item 3", (menuItem) -> {

            Toast.makeText(this,"Sub Menu 3", Toast.LENGTH_LONG).show();

        }, null));

        menuItems.add(new MenuItem("Sub Menu", subList1, null));

        menuItems.add(new MenuItem("Custom Input List", new MenuItemClickListener<MenuItem>() {
            @Override
            public void onClick(MenuItem menuItem) {
                Intent myIntent = new Intent(ExamplesActivity.this, CustomInputListActivity.class);
                startActivity(myIntent);
            }
        }));

        menuItems.add(new MenuItem("Info Dialog", (menuItem) -> {
            Intent myIntent = new Intent(ExamplesActivity.this, InfoDialogActivity.class);
            startActivity(myIntent);
        }));

        menuItems.add(new MenuItem("Confirmation Dialog", (menuItem) -> {
            Intent myIntent = new Intent(ExamplesActivity.this, ConfirmationDialogActivity.class);
            startActivity(myIntent);
        }));

        menuItems.add(new MenuItem("Device Info", (menuItem) -> {
         /*    [Device Info](https://github.com/TokenPublication/DeviceInfoClientApp)    */

            DeviceInfo deviceInfo = new DeviceInfo(getApplicationContext());
            deviceInfo.getFields(
                    fields -> {
                        if (fields == null) return;

                        Log.d("Example 0", "Fiscal ID:   "    + fields[0]);
                        Log.d("Example 1", "IMEI Number: "    + fields[1]);
                        Log.d("Example 2", "IMSI Number: "    + fields[2]);
                        Log.d("Example 3", "Modem Version : " + fields[3]);
                        Log.d("Example 4", "LYNX Number: "    + fields[4]);
                        Log.d("Example 5", "POS Mode: "       + fields[5]);

                        showInfoDialog(InfoDialog.InfoType.Info,
                                "Fiscal ID: "     +fields[0] +"\n"
                                    +"IMEI Number: "   +fields[1] +"\n"
                                    +"IMSI Number: "   +fields[2] +"\n"
                                    +"Modem Version: " +fields[3] +"\n"
                                    +"Lynx Version: "  +fields[4] +"\n"
                                    +"Pos Mode: "      +fields[5],true);
                        deviceInfo.unbind();
                    },
                    // write requested fields
                    DeviceInfo.Field.FISCAL_ID,
                    DeviceInfo.Field.IMEI_NUMBER,
                    DeviceInfo.Field.IMSI_NUMBER,
                    DeviceInfo.Field.MODEM_VERSION,
                    DeviceInfo.Field.LYNX_VERSION,
                    DeviceInfo.Field.OPERATION_MODE
            );
        }));

        menuItems.add(new MenuItem("Inject Key", (menuItem) -> {
            exampleKey();
        }));

        menuItems.add(new MenuItem("Barcode Scanner", iListMenuItem -> {
            new TokenBarcodeScanner(new WeakReference<>(ExamplesActivity.this), data -> {
                Toast.makeText(this, "Barcode Data: " + data, Toast.LENGTH_SHORT).show();
            });
        }));

        menuItems.add(new MenuItem("Num Pad", (menuItem) -> {

            NumPadDialog dialog = NumPadDialog.newInstance(new NumPadListener(){

                @Override
                public void enter(String pin) {

                }

                @Override
                public void onCanceled() {
                    //Num pad canceled callback
                }

            }, "Please enter PIN", 8);
            dialog.show(getSupportFragmentManager(), "Num Pad");
        }));

        menuItems.add(new MenuItem("Show QR", (menuItem) -> {
            InfoDialog dialog = showInfoDialog(InfoDialog.InfoType.Progress, "QR Loading", true);
            // For detailed usage; SaleActivity
            cardServiceBinding.showQR("PLEASE READ THE QR CODE", StringHelper.getAmount(qrAmount), qrString); // Shows QR on the back screen
            dialog.setQr(qrString, "WAITING FOR THE QR CODE"); // Shows the same QR on Info Dialog
        }));

        List<IListMenuItem> subListPrint = new ArrayList<>();
        subListPrint.add(new MenuItem("Print Load Success", (menuItem) -> {
            printService.print(PrintHelper.PrintSuccess()); // Message print: Load Success

        }, null));

        subListPrint.add(new MenuItem("Print Load Error", (menuItem) -> {
            printService.print(PrintHelper.PrintError()); // Message print: Load Error

        }, null));
        subListPrint.add(new MenuItem("Print Bitmap", (menuItem) -> {
            // printService.printBitmap("/*Prints Preloaded BMP*/", 100);
        }, null));

        menuItems.add(new MenuItem("Print Functions", subListPrint, null));


    }

    @Override
    public void confirmed(int arg) {
        if (arg == 99) {
            Toast.makeText(this, "Confirmed.", Toast.LENGTH_SHORT).show();
        }
        //else if (arg == ***) { Do something else... }
    }

    @Override
    public void canceled(int arg) {
        if (arg == 99) {
            Toast.makeText(this, "Canceled.", Toast.LENGTH_SHORT).show();
        }
        //else if (arg == ***) { Do something else... }
    }
}
