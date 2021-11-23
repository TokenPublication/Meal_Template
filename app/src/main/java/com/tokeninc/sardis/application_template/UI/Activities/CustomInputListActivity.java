package com.tokeninc.sardis.application_template.UI.Activities;

import android.os.Bundle;

import com.token.uicomponents.CustomInput.CustomInputFormat;
import com.token.uicomponents.CustomInput.EditTextInputType;
import com.token.uicomponents.CustomInput.InputListFragment;
import com.token.uicomponents.CustomInput.InputValidator;
import com.tokeninc.sardis.application_template.BaseActivity;
import com.tokeninc.sardis.application_template.R;

import java.util.ArrayList;
import java.util.List;

/*
*
* This activity shows how to use custom input edit lines as list and info fragment
*
*[InputListFragment](https://developer.tokeninc.com/pos-projects/token%20integrations/ui-ux/ui-components)
*
 */

public class CustomInputListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_input_list);

        InputValidator validator = new InputValidator() {
            @Override
            public boolean validate(CustomInputFormat input) {
                return input.getText().length() == 19;
            }
        };

        InputValidator validator2 = new InputValidator() {
            @Override
            public boolean validate(CustomInputFormat input) {
                return input.getText().length() == 10;
            }
        };

        List<CustomInputFormat> inputList = new ArrayList<>();
        CustomInputFormat customInputFormat = new CustomInputFormat("Text",
                EditTextInputType.Text, 8, null,
                null);
        customInputFormat.setText("00000016");

        inputList.add(customInputFormat);

        inputList.add(new CustomInputFormat("Card Number", EditTextInputType.CreditCardNumber, null, "Invalid card number!", validator));
        inputList.add(new CustomInputFormat("Expire Date", EditTextInputType.ExpiryDate, null, null, null));
        inputList.add(new CustomInputFormat("CVV", EditTextInputType.CVV, null, null, null));
        inputList.add(new CustomInputFormat("Date", EditTextInputType.Date, null, null, null));
        inputList.add(new CustomInputFormat("Time", EditTextInputType.Time, null, null, null));
        inputList.add(new CustomInputFormat("Number", EditTextInputType.Number, null, null, null));
        inputList.add(new CustomInputFormat("Amount", EditTextInputType.Amount, null, null, null));
        inputList.add(new CustomInputFormat("IP", EditTextInputType.IpAddress, null, null, null));
        inputList.add(new CustomInputFormat("Password", EditTextInputType.Password, null, null, null));
        inputList.add(new CustomInputFormat("Password (Num)", EditTextInputType.NumberPassword, null, null, null));
        inputList.add(new CustomInputFormat("New Text", EditTextInputType.Text, null, "Max text size 10", validator2));

        inputList.get(1).setText("1234");

        InputListFragment fragment = InputListFragment.newInstance(inputList);
        addFragment(R.id.container, fragment, false);
        fragment.setActionLayout("Custom Input List", true, null); // Fragment has a back button and a title
    }
}
