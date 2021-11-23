package com.tokeninc.sardis.application_template.UI.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tokeninc.sardis.application_template.BaseActivity;
import com.tokeninc.sardis.application_template.R;

public class TriggerActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trigger);

        ((TextView)findViewById(R.id.tvActionName)).setText(getString(R.string.app_name) +  " " + getIntent().getAction());
        new Handler().postDelayed(this::finish, 2000);
    }
}
