package com.swiftdating.app.ui.emailScreen;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;

import com.swiftdating.app.R;
import com.swiftdating.app.common.CommonDialogs;
import com.swiftdating.app.data.network.ApiCall;
import com.swiftdating.app.data.network.ApiCallback;
import com.swiftdating.app.data.preference.SharedPreference;
import com.swiftdating.app.model.responsemodel.PhoneLoginResponse;
import com.swiftdating.app.ui.base.BaseActivity;
import com.swiftdating.app.ui.verificationScreen.VerificationActivity;

public class PhoneActivity extends BaseActivity implements TextWatcher, CountryCodePicker.OnCountryChangeListener, ApiCallback.PhoneLoginCallBack {

    private final String TAG = PhoneActivity.class.getSimpleName();
    private ImageView img_cross;
    private EditText et_phone;
    private FloatingActionButton btn_continue;
    private CountryCodePicker cc_picker;
    private ConstraintLayout rootLAY;
    private String c_code;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mActivity = this;
    }

    private void initViews() {
        sp = new SharedPreference(this);
        cc_picker = findViewById(R.id.cc_picker);
        cc_picker.setOnCountryChangeListener(this);
        btn_continue = findViewById(R.id.btn_continue);
        img_cross = findViewById(R.id.img_cross);
        rootLAY = findViewById(R.id.rootLAY);
        et_phone = findViewById(R.id.et_phone);
        btn_continue.setOnClickListener(this::onClick);
        img_cross.setOnClickListener(this::onClick);
        et_phone.addTextChangedListener(this);
        c_code = cc_picker.getDefaultCountryCode();
    }

    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_cross:
                finish();
                break;
            case R.id.tv_yes:
                if (!TextUtils.isEmpty(et_phone.getText().toString().trim())) {
                    sp.setIsFromNumber(true);
                    CommonDialogs.dismiss();
                    // startActivity(new Intent(this, VerificationActivity.class).putExtra("phone", et_phone.getText().toString()));
                    CallPhoneLogin();
                } else showSnackbar(et_phone, "Please enter Cell Phone Number");
                break;
            case R.id.btn_continue:
                hideKeyboard();
                if (!TextUtils.isEmpty(et_phone.getText().toString().trim()))
                    CommonDialogs.alertDialogTwoButtonsWithConfirm(mActivity, this::onClick, "Please confirm that " + et_phone.getText().toString() + " is your correct Cell Phone Number");
                else showSnackbar(et_phone, "Please enter Cell Phone Number");
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(s.toString())) {
            btn_continue.setEnabled(true);
            btn_continue.setBackground(this.getResources().getDrawable(R.drawable.gradientbtn));
        } else {
            btn_continue.setEnabled(false);
            btn_continue.setBackground(this.getResources().getDrawable(R.drawable.disabledbtn));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onCountrySelected() {
        Log.d(TAG, "onCountrySelected: " + cc_picker.getSelectedCountryCode());
        c_code = cc_picker.getSelectedCountryCode();
    }

    private void CallPhoneLogin() {
        phone = "+" + c_code + et_phone.getText().toString().trim();
        HashMap<String, Object> map = new HashMap<>();
        map.put("mobile", phone);
        map.put("socialType", 6);
        showLoading();
        ApiCall.phoneLogin(map, this);
    }

    @Override
    public void onSuccessPhoneLogin(PhoneLoginResponse response) {
        hideLoading();
        if (response.getSuccess()) {
            if (sp != null)
                sp.setPhone(phone);
            Intent intent = new Intent(PhoneActivity.this, VerificationActivity.class);
            intent.putExtra("phone", phone);
            //intent.putExtra("otp", String.valueOf(response.getOtp()));
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            showSnackbar(rootLAY, response.getMessage());
        }
    }

    @Override
    public void onError(String error) {
        hideLoading();
        showSnackbar(rootLAY, error);
    }
}