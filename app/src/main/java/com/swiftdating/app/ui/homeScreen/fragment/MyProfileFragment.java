package com.swiftdating.app.ui.homeScreen.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.swiftdating.app.R;
import com.swiftdating.app.common.CommonDialogs;
import com.swiftdating.app.common.CommonUtils;
import com.swiftdating.app.common.GpsTracker;
import com.swiftdating.app.common.SubscriptionResponse;
import com.swiftdating.app.data.network.CallServer;
import com.swiftdating.app.data.preference.SharedPreference;
import com.swiftdating.app.model.ImageModel;
import com.swiftdating.app.model.requestmodel.PremiumTokenCountModel;
import com.swiftdating.app.model.requestmodel.SuperLikeCountModel;
import com.swiftdating.app.model.requestmodel.TimeTokenRequestModel;
import com.swiftdating.app.model.requestmodel.VipTokenRequestModel;
import com.swiftdating.app.model.responsemodel.ProfileOfUser;
import com.swiftdating.app.model.responsemodel.VerificationResponseModel;
import com.swiftdating.app.ui.base.BaseActivity;
import com.swiftdating.app.ui.base.BaseFragment;
import com.swiftdating.app.ui.editProfileScreen.EditProfileActivity;
import com.swiftdating.app.ui.homeScreen.viewmodel.HomeViewModel;
import com.swiftdating.app.ui.myCardScreen.MyCardActivity;
import com.swiftdating.app.ui.settingScreen.SettingsActivity;
import com.swiftdating.app.ui.settingScreen.SliderAdapter;
import com.swiftdating.app.ui.slider_fragment;
import com.swiftdating.app.ui.where_do_you_live.WhereYouLiveActivity;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileFragment extends BaseFragment implements View.OnClickListener, CommonDialogs.onProductConsume, BaseActivity.MyProfileResponse, slider_fragment.onReceiveClickCallback, SliderAdapter.OnItemClicked, BaseActivity.OnPurchaseListener {
    private static final long TIME_PERIOD = 3000;
    private static final String TAG = "MyProfileFragment";
    List<ImageModel> imagelist = new ArrayList<>();
    double price;
    String productId, tokenSType, lat, lon;
    int currentPage;
    Runnable Update;
    private LinearLayout llEdit, llSettings;
    private TextView tv_complete, tvName, tvAddress, tvPremium, tvUnlimitedView, tv_active, tvCrushToken, tv_vipNum, tvVipToken, tvTimeTokenTxt;
    private CircleImageView ivProfileImage;
    private HomeViewModel homeViewModel;
    private Button /*btnLikeGetMore, btnExtend, btn_vip,*/ btn_change;
    private int purchaseType, selectedPosition;
    private CardView card_vip, card_time, card_crush;
    private ViewPager viewPager;
    private TabLayout text_pager_indicator;
    private SliderAdapter sliderAdapter;
    private TextView tv_subscribe;
    private GpsTracker gpsTracker;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_profile;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mActivity = (BaseActivity) getActivity();
        mActivity.hideLoading();
        gpsTracker = GpsTracker.getInstance(getContext());
        gpsTracker.dilaog();
        Log.e(TAG, "onViewCreated: ");
        if (getBaseActivity().isNetworkConnected()) {
            initialize(view);
            setData();
            getBaseActivity().getMyProfile(this);
        } else {
            getBaseActivity().showSnackbar(view, "Please connect to internet");
        }
        setSlider(view);
    }

    private void setSlider(View v) {
        currentPage = 0;
        text_pager_indicator = v.findViewById(R.id.text_pager_indicator);
        RelativeLayout rlRootView = v.findViewById(R.id.sliderFragment);
        viewPager = v.findViewById(R.id.pagerSlider);
        tv_subscribe = v.findViewById(R.id.tv_subscribe);

        rlRootView.setOnClickListener(vss -> MyProfileFragment.this.openPurchaseDialog());


        String[] tab_names = getResources().getStringArray(R.array.arr_premium_txt);
        List<String> titleList = new ArrayList<>();
        Collections.addAll(titleList, tab_names);
        sliderAdapter = new SliderAdapter(getContext(), titleList, this);
        viewPager.setAdapter(sliderAdapter);
        text_pager_indicator.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        Handler handler = new Handler();
        Update = () -> {
            if (currentPage == viewPager.getAdapter().getCount()) {
                currentPage = 0;
            }
            viewPager.setCurrentItem(currentPage, true);
            currentPage++;
        };

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 0, TIME_PERIOD);

    }

    private void openPurchaseDialog() {
        CommonDialogs.PremuimPurChaseDialog(getContext(), this, getBaseActivity().sp);
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity = (BaseActivity) getActivity();
        if (getBaseActivity().isNetworkConnected()) {
            //((HomeActivity) Objects.requireNonNull(getActivity())).setToolbarWithTitle("Black Gentry");
            // ((HomeActivity) getActivity()).mToolbar.setVisibility(View.GONE);
            Gson gson = new Gson();
            SharedPreference sp = new SharedPreference(getContext());
            String jsonImage = sp.getUserImage();
            Type type = new TypeToken<List<ImageModel>>() {
            }.getType();
            imagelist = gson.fromJson(jsonImage, type);
            if (imagelist != null && imagelist.size() > 0) {
                Glide.with(MyProfileFragment.this).load(CallServer.BaseImage + imagelist.get(0).getImageUrl()).placeholder(getContext().getResources()
                        .getDrawable(R.drawable.ic_profile_individual)).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivProfileImage);
                getBaseActivity().sp.saveFirstImage(imagelist.get(0).getImageUrl());
            } else {
                getBaseActivity().openActivityOnTokenExpire();
            }
        }

        if (getBaseActivity().sp != null) {
            tv_subscribe.setVisibility(getBaseActivity().sp.getPremium() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onPagerItemClick() {
        openPurchaseDialog();
    }

    /**
     * **  Method to Initialize
     */
    private void initialize(View view) {
        tv_complete = view.findViewById(R.id.tv_complete);
        tvCrushToken = view.findViewById(R.id.tvCrushToken);
        tvVipToken = view.findViewById(R.id.tvVipToken);
        tvTimeTokenTxt = view.findViewById(R.id.tvTimeToken);
        tv_active = view.findViewById(R.id.tv_active);
        card_crush = view.findViewById(R.id.card_crush);
        btn_change = view.findViewById(R.id.btn_change);
        btn_change.setOnClickListener(this);
        card_time = view.findViewById(R.id.card_time);
        card_vip = view.findViewById(R.id.card_vip);
        llSettings = view.findViewById(R.id.llSettings);
        tvName = view.findViewById(R.id.tv_name);
        tvAddress = view.findViewById(R.id.tv_address);
        llEdit = view.findViewById(R.id.llEdit);
        ivProfileImage = view.findViewById(R.id.iv_profile);
        listener();
        subscribeModel();
        initBillingProcess();
    }

    /**
     * **  Method to Initialize Billing Process
     */
    private void initBillingProcess() {
      /*  bp = new BillingProcessor(getActivity(), LICENSE_KEY, this);
        bp.initialize();*/
    }

    /**
     * **  Method to Handle api Response
     */
    private void subscribeModel() {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

        homeViewModel.verifyResponse().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) {
                return;
            }
            switch (resource.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    getBaseActivity().hideLoading();
                    if (resource.data.getSuccess()) {
                        Gson gson = new Gson();
                        String user = getBaseActivity().sp.getUser();
                        VerificationResponseModel obj = gson.fromJson(user, VerificationResponseModel.class);
                        Log.e("TAG", "onChanged: " + obj);
                        obj.setUser(resource.data.getUser());
                        getBaseActivity().sp.saveUserData(obj.getUser().getProfileOfUser(), obj.getProfileCompleted().toString());
                        setData();
                    } else if (resource.data.getError() != null && resource.data.getError().getCode().equalsIgnoreCase("401")) {
                        getBaseActivity().openActivityOnTokenExpire();
                    } else {
                        getBaseActivity().showSnackbar(ivProfileImage, resource.data.getMessage());
                    }
                    break;
                case ERROR:
                    getBaseActivity().hideLoading();
                    getBaseActivity().showSnackbar(ivProfileImage, resource.message);
                    break;
            }
        });

        // super like == crush token
        homeViewModel.addSuperLikeResponse().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) {
                return;
            }
            switch (resource.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    getBaseActivity().hideLoading();
                    if (resource.data.isSuccess()) {
                        Gson gson = new Gson();
                        String user = getBaseActivity().sp.getUser();
                        ProfileOfUser obj = gson.fromJson(user, ProfileOfUser.class);
                        obj.setSuperLikesCount(resource.data.getTotalSuperlikes());
                        getBaseActivity().sp.saveUserData(obj, getBaseActivity().sp.getProfileCompleted());
                        setCrushTokens(obj);
                    } else if (resource.code == 401) {
                        getBaseActivity().openActivityOnTokenExpire();
                    } else {
                        getBaseActivity().showSnackbar(ivProfileImage, "Something went wrong");
                    }
                    break;
                case ERROR:
                    getBaseActivity().hideLoading();
                    getBaseActivity().showSnackbar(ivProfileImage, resource.message);
                    break;
            }
        });

        homeViewModel.vipTokenResponse().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) {
                return;
            }
            switch (resource.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    getBaseActivity().hideLoading();
                    if (resource.data.isSuccess()) {
                        Gson gson = new Gson();
                        String user = getBaseActivity().sp.getUser();
                        ProfileOfUser obj = gson.fromJson(user, ProfileOfUser.class);
                        obj.setVipToken(resource.data.getTotalVIPToken());
                        getBaseActivity().sp.saveUserData(obj, getBaseActivity().sp.getProfileCompleted());
                        setVipTokens(obj);
                    } else if (resource.code == 401) {
                        getBaseActivity().openActivityOnTokenExpire();
                    } else {
                        getBaseActivity().showSnackbar(ivProfileImage, "Something went wrong");
                    }
                    break;
                case ERROR:
                    getBaseActivity().hideLoading();
                    getBaseActivity().showSnackbar(ivProfileImage, resource.message);
                    break;
            }
        });


        homeViewModel.timeTokenResponse().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) {
                return;
            }
            switch (resource.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    getBaseActivity().hideLoading();
                    if (resource.data.isSuccess()) {
                        Gson gson = new Gson();
                        String user = getBaseActivity().sp.getUser();
                        ProfileOfUser obj = gson.fromJson(user, ProfileOfUser.class);
                        obj.setTimeTokenCount(resource.data.getTotalTimeTokens());
                        getBaseActivity().sp.saveUserData(obj, getBaseActivity().sp.getProfileCompleted());
                        setTimeToken(obj);
                    } else if (resource.code == 401) {
                        getBaseActivity().openActivityOnTokenExpire();
                    } else {
                        getBaseActivity().showSnackbar(ivProfileImage, "Something went wrong");
                    }
                    break;
                case ERROR:
                    getBaseActivity().hideLoading();
                    getBaseActivity().showSnackbar(ivProfileImage, resource.message);
                    break;
            }
        });

        homeViewModel.addPremiumResponse().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    getBaseActivity().hideLoading();
                    if (resource.data.getSuccess()) {
                        getBaseActivity().sp.savePremium(true);
                        tv_subscribe.setVisibility(getBaseActivity().sp.getPremium() ? View.VISIBLE : View.GONE);
                      /*  if (preDialog != null)
                            preDialog.dismiss();*/
                        //getBaseActivity().showSnackBar(ivProfileImage, resource.data.getMessage());
                       /* tvPremium.setVisibility(View.INVISIBLE);
                        tvUnlimitedView.setVisibility(View.INVISIBLE);*/
                    } else if (resource.code == 401) {
                        getBaseActivity().openActivityOnTokenExpire();
                    } else {
                        getBaseActivity().showSnackbar(ivProfileImage, "Something went wrong");
                    }
                    break;
                case ERROR:
                    getBaseActivity().hideLoading();
                    getBaseActivity().showSnackbar(ivProfileImage, resource.message);
                    break;
            }
        });

        homeViewModel.subscriptionResponse().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null)
                return;
            switch (resource.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    getBaseActivity().hideLoading();
                    if (resource.data.getSuccess()) {
                        if (resource.data.getSubscription() != null) {
                            boolean isSubscribed = resource.data.getSubscription().getIsPremium().equalsIgnoreCase("Yes");
                            String productId = "";
                            if (isSubscribed)
                                productId = resource.data.getSubscription().getSubscriptionForUser().getSubscriptionId();
                            // new FindMatchFragment().checkSubscription(isSubscribed, productId, resource.data.getSubscription().getSubscriptionForUser().getPurchaseToken());
                        } else {
                            //new FindMatchFragment().checkExistingSubscription();
                        }
                    } else if (resource.code == 401) {
                        getBaseActivity().openActivityOnTokenExpire();
                    }
                    break;
            }
        });
    }

    private void setCrushTokens(@NonNull ProfileOfUser obj) {
        String TokenStr = obj.getSuperLikesCount() == 1 ? obj.getSuperLikesCount() + " Crush Token" : obj.getSuperLikesCount() + " Crush Tokens";
        tvCrushToken.setText(TokenStr);
    }

    private void setVipTokens(@NonNull ProfileOfUser obj) {
        String TokenStr = obj.getVipToken() == 1 ? obj.getVipToken() + " Vip Token" : obj.getVipToken() + " Vip Tokens";
        tvVipToken.setText(TokenStr);
    }

    private void setTimeToken(@NonNull ProfileOfUser obj) {
        String timeToke = obj.getTimeTokenCount() == 1 ? obj.getTimeTokenCount() + " Time Token" : obj.getTimeTokenCount() + " Time Tokens";
        tvTimeTokenTxt.setText(timeToke);
    }

    /**
     * **  Method to implement Listener
     */
    private void listener() {
        card_time.setOnClickListener(this);
        card_crush.setOnClickListener(this);
        card_vip.setOnClickListener(this);
        llSettings.setOnClickListener(this);
        llEdit.setOnClickListener(this);
        ivProfileImage.setOnClickListener(this);
    }

    /**
     * **  Method to Set User Data
     */
    private void setData() {
        Gson gson = new Gson();
        String user = getBaseActivity().sp.getUser();
        ProfileOfUser obj = gson.fromJson(user, ProfileOfUser.class);
        if (obj != null) {
           /* if (!TextUtils.isEmpty(obj.getDob())) {
                int age = getAgeFromDob(obj.getDob());
            }*/
            if (!TextUtils.isEmpty(obj.getVisible())) {
                tv_active.setText(obj.getVisible().equalsIgnoreCase("true") ? "Profile Active" : "Profile Hidden");
            }
            if (!TextUtils.isEmpty(getBaseActivity().sp.getProfileCompleted())) {
                tv_complete.setText("" + getBaseActivity().sp.getProfileCompleted() + "%");
            } else if (obj.getCompleted() != null) {
                tv_complete.setText("" + obj.getCompleted() + "%");
            }
            if (!TextUtils.isEmpty(obj.getName())) {
                tvName.setText(obj.getName() /*+ ", " + age*/);
            }

            if (obj.getLatitude() != null && obj.getLongitude() != null) {
                lat = obj.getLatitude();
                lon = obj.getLongitude();


                tvAddress.setText(CommonUtils.getCityAddress(getContext(), obj.getLatitude(), obj.getLongitude()));
            }


            setCrushTokens(obj);
            setTimeToken(obj);
            setVipTokens(obj);

        } else {
            if (!TextUtils.isEmpty(getBaseActivity().sp.getProfileCompleted())) {
                tv_complete.setText("" + getBaseActivity().sp.getProfileCompleted() + "%");
            }
        }
        final LocationManager manager = (LocationManager) Objects.requireNonNull(getContext()).getSystemService(Context.LOCATION_SERVICE);
        if (getBaseActivity().sp.getPremium()) {
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //  if (&& GpsTracker.getInstance(getContext()).canGetLocation()) {
                if (TextUtils.isEmpty(lat) || !lat.equalsIgnoreCase("" + gpsTracker.getLatitude())) {
                    lat = "" + gpsTracker.getLatitude();
                    lon = "" + gpsTracker.getLongitude();
                    String add = CommonUtils.getCityAddress(getContext(), lat, lon);
                    if (!TextUtils.isEmpty(add))
                        tvAddress.setText(add);
                }
            } else {
                gpsTracker.showSettingsAlert();
            }
        }
    }

    private int getAgeFromDob(String sdate) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        long year = 0L;
        try {
            Date sDate = df.parse(sdate);
            Date cDate = df.parse(df.format(new Date()));
            long timeOne = cDate.getTime();
            long timeTwo = sDate.getTime();
            long diff = timeOne - timeTwo;
            long diffDays = diff / (24 * 60 * 60 * 1000);
            year = diffDays / 365;
            return (int) year;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void onClick(View view) {
        if (view == llSettings) {
            Log.e(TAG, "onClick: settings");
            startActivityForResult(new Intent(getContext(), SettingsActivity.class), 1010);
            getActivity().overridePendingTransition(R.anim.slide_in_top, R.anim.nothing);
        } else if (view == llEdit) {
            Log.e(TAG, "onClick: edit");
            startActivityForResult(new Intent(getContext(), EditProfileActivity.class), 1010);
            getActivity().overridePendingTransition(R.anim.slide_in_top, R.anim.nothing);
        } else if (view.getId() == R.id.card_time) {
            CommonDialogs.TimeTokenPurChaseDialog(getContext(), this);
        } else if (view.getId() == R.id.card_crush) {
            CommonDialogs.CrushPurChaseDialog(getContext(), this);
        } else if (view.getId() == R.id.iv_profile) {
            startActivity(new Intent(getContext(), MyCardActivity.class));
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (view.getId() == R.id.card_vip) {
            CommonDialogs.VIPPurChaseDialog(getContext(), this);
        } else if (view.getId() == R.id.btn_change) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (mActivity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2021);
                } else {
                    startActivityForResult(new Intent(mActivity, WhereYouLiveActivity.class).putExtra("lat", lat).putExtra("lon", lon), 1001);
                }
            } else {
                startActivityForResult(new Intent(mActivity, WhereYouLiveActivity.class).putExtra("lat", lat).putExtra("lon", lon), 1001);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getBaseActivity().isNetworkConnected()) {
            getBaseActivity().hideLoading();
            if (homeViewModel != null)
                homeViewModel.verifyResponse().removeObservers(this);
        }
    }

   /* @Override
    public void OnItemClick(int position, int type, String id) {
        *//*selectedPosition = position;
        purchaseType = type;
        if (type == 0) {
            bp.purchase(getActivity(), id);
        } else if (type == 1) {
            bp.purchase(getActivity(), id);
        } else {
            bp.subscribe(getActivity(), id, getBaseActivity().sp.getUserId());
        }*//*
        mActivity.showLoading();
        homeViewModel.addTimeToken(new TimeTokenRequestModel(1, 0.99));
    }*/
/*

    @Override
    public void onBillingInitialized() {
        */
    /*
     * Called when BillingProcessor was initialized and it's ready to purchase
     *//*

        if (CommonDialogs.vipTokenPriceList.size() == 0 || CommonDialogs.timeTokenPriceList.size() == 0 || CommonDialogs.crushTokenPriceList.size() == 0 || CommonDialogs.PremiumPriceList.size() == 0) {
            CommonDialogs.onBillingInitialized(bp);
        }
        CommonDialogs.setBilling(bp);

    }

    */
/*private void setSubcripData(String[] arr, List<InAppPriceValue> PriceList) {
        PriceList.clear();
        SkuDetails skuDetails;
        for (String s : arr) {
            skuDetails = bp.getSubscriptionListingDetails(s);
            PriceList.add(new InAppPriceValue(skuDetails.priceText, skuDetails.priceValue));
        }
    }

    private void setPurchaseData(String[] TokenArr, List<InAppPriceValue> TokenPriceList) {
        TokenPriceList.clear();
        SkuDetails purchaseListingDetails;
        for (String s : TokenArr) {
            purchaseListingDetails = bp.getPurchaseListingDetails(s);
            TokenPriceList.add(new InAppPriceValue(purchaseListingDetails.priceText, purchaseListingDetails.priceValue));
        }
    }*//*


    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Log.e("TAG", "onProductPurchased: " + productId);

        */
    /*
     * Called when requested PRODUCT ID was successfully purchased
     *//*


        Toast.makeText(getContext(), "Item Purchased", Toast.LENGTH_LONG).show();
        getBaseActivity().showLoading();
        if (tokenSType.equalsIgnoreCase("crushToken")) {
            bp.consumePurchase(productId);
            homeViewModel.addSuperLikeRequest(new SuperLikeCountModel(selectedPosition, price));
        } else if (tokenSType.equalsIgnoreCase("timeToken")) {
            bp.consumePurchase(productId);
            homeViewModel.addTimeToken(new TimeTokenRequestModel(selectedPosition, price));
        } else if (tokenSType.equalsIgnoreCase("vipToken")) {
            bp.consumePurchase(productId);
            homeViewModel.addVipToken(new VipTokenRequestModel(selectedPosition, price));
        } else if (tokenSType.equalsIgnoreCase("PremiumPurchase")) {
            bp.consumePurchase(productId);
            homeViewModel.addPremiumRequest(new PremiumTokenCountModel("1",
                    productId,
                    price,
                    Integer.parseInt(productId.split("_")[2]),
                    details.purchaseInfo.purchaseData.orderId,
                    details.purchaseInfo.purchaseData.purchaseToken,
                    CommonUtils.getDateForPurchase(details),
                    details.purchaseInfo.signature,
                    details.purchaseInfo.purchaseData.purchaseState.toString()));

        }
        Log.e("purchase success", details.purchaseInfo.responseData);
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        */
    /*
     * Called when some error occurred. See Constants class for more details
     *
     * Note - this includes handling the case where the user canceled the buy dialog:
     *//*

//          errorCode = Constants.BILLING_RESPONSE_RESULT_USER_CANCELED;

        Log.e("c", " errorCode=" + errorCode);
        Log.e("c", " errorCode=" + error);
    }

    @Override
    public void onPurchaseHistoryRestored() {
        */
    /*
     * Called when purchase history was restored and the list of all owned PRODUCT ID's
     * was loaded from Google Play
     *//*

    }
*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       /* if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }*/

        if (requestCode == 1001) {
            Gson gson = new Gson();
            String json = getBaseActivity().sp.getUser();
            ProfileOfUser user = gson.fromJson(json, ProfileOfUser.class);
            lat = user.getLatitude();
            lon = user.getLongitude();
            tvAddress.setText(CommonUtils.getCityAddress(mActivity, user.getLatitude(), user.getLongitude()));
        }
        if (requestCode == 1010) setData();
    }

    @Override
    public void onClickToken(String tokenType, int tokensNum, int selectedPos) {
        handleCallBack(tokenType, tokensNum, selectedPos);
    }

    @Override
    public void setProfileData() {
        if (getBaseActivity() != null)
            setData();
    }


    @Override
    public void onPremiumCallback(String tokenType, int tokensNum, int selectedPos) {
        handleCallBack(tokenType, tokensNum, selectedPos);
    }

    void handleCallBack(String tokenType, int tokensNum, int selectedPos) {


        tokenSType = tokenType;
        selectedPosition = tokensNum;

        SkuDetails sku = null;
        if (tokenType.equalsIgnoreCase("crushToken")) {
            price = CommonDialogs.crushTokenPriceList.get(selectedPos).getPriceValue();
            productId = CommonDialogs.crushTokenArr[selectedPos];
            sku = CommonDialogs.crushTokenSkuList.get(selectedPos);
        } else if (tokenType.equalsIgnoreCase("timeToken")) {
            if (selectedPos < CommonDialogs.timeTokenPriceList.size()) {
                price = CommonDialogs.timeTokenPriceList.get(selectedPos).getPriceValue();
                productId = CommonDialogs.timeTokenArr[selectedPos];
                sku = CommonDialogs.timeTokenSkuList.get(selectedPos);
            } else {
                price = CommonDialogs.timeTokenPriceList.get(selectedPos - 1).getPriceValue();
                productId = CommonDialogs.timeTokenArr[selectedPos - 1];
                sku = CommonDialogs.timeTokenSkuList.get(selectedPos - 1);
            }
        } else if (tokenType.equalsIgnoreCase("vipToken")) {
            price = CommonDialogs.vipTokenPriceList.get(selectedPos).getPriceValue();
            productId = CommonDialogs.vipTokenArr[selectedPos];
            sku = CommonDialogs.vipTokenSkuList.get(selectedPos);
        } else if (tokenType.equalsIgnoreCase("PremiumPurchase")) {
            price = CommonDialogs.PremiumPriceList.get(selectedPos).getPriceValue();
            productId = CommonDialogs.PremiumArr[selectedPos];
            sku = CommonDialogs.PremiumSkuList.get(selectedPos);

//            bp.subscribe(mActivity, productId);
            CommonDialogs.dismiss();
            /*
            Dialog dialog = new Dialog(mActivity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dialog_two_button);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
            TextView tv_message = dialog.findViewById(R.id.tv_message);
            TextView tv_yes = dialog.findViewById(R.id.tv_yes);
            TextView tv_no = dialog.findViewById(R.id.tv_no);
            tv_message.setText(getString(R.string.premium_note_txt));
            tv_yes.setOnClickListener(v -> {
                price = CommonDialogs.PremiumPriceList.get(selectedPos).getPriceValue();
                productId = CommonDialogs.PremiumArr[selectedPos];
                bp.subscribe(mActivity, productId);
                CommonDialogs.dismiss();
                dialog.dismiss();
                // here in this case tokenNum is month or year
                *//*getBaseActivity().showLoading();
                String premium = "premium_";
                homeViewModel.addPremiumRequest(new PremiumTokenCountModel(premium.concat(String.valueOf(tokensNum)),
                        price,
                        tokensNum,
                        "abc@123",
                        "",
                        "2020-04-17 12:30:55",
                        "",
                        ""
                ));*//*
            });
            tv_no.setOnClickListener(view -> dialog.dismiss());*/
        }
        if (fragClient != null && fragClient.isReady() && sku != null) {
            setOnPurchaseListener(this);
            fragClient.launchBillingFlow(mActivity, getBillingFlowParam(sku));
        }
//        if (!tokenType.equalsIgnoreCase("PremiumPurchase")) {
//            bp.purchase(getActivity(), productId);
//        }
    }

    @Override
    public void OnSuccessPurchase(Purchase purchase) {
        Log.e("TAG", "onProductPurchased: " + productId);

        /*
         * Called when requested PRODUCT ID was successfully purchased
         */

        Toast.makeText(getContext(), "Item Purchased", Toast.LENGTH_LONG).show();
        if (fragClient != null && fragClient.isReady()&&!TextUtils.isEmpty(tokenSType)) {
            if (tokenSType.equalsIgnoreCase("PremiumPurchase")) {
                getBaseActivity().showLoading();

                fragClient.acknowledgePurchase(getAcknowledgeParams(purchase.getPurchaseToken()),
                        billingResult -> {
                            Log.e(TAG, "OnSuccessPurchase: " + billingResult.getResponseCode());
                            homeViewModel.addPremiumRequest(new PremiumTokenCountModel("1",
                                    productId,
                                    price,
                                    Integer.parseInt(productId.split("_")[2]),
                                    purchase.getOrderId(),
                                    purchase.getPurchaseToken(),
                                    CommonUtils.getDateForPurchase(purchase.getPurchaseTime()),
                                    purchase.getSignature(),
                                    BaseActivity.purchaseState));
                        });
            }else {
                getBaseActivity().showLoading();
                fragClient.consumeAsync(getConsumeParam(purchase.getPurchaseToken()), new ConsumeResponseListener() {
                    @Override
                    public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                        if (tokenSType.equalsIgnoreCase("crushToken")) {
                            homeViewModel.addSuperLikeRequest(new SuperLikeCountModel(selectedPosition, price));
                        } else if (tokenSType.equalsIgnoreCase("timeToken")) {
                            homeViewModel.addTimeToken(new TimeTokenRequestModel(selectedPosition, price));
                        } else if (tokenSType.equalsIgnoreCase("vipToken")) {
                            homeViewModel.addVipToken(new VipTokenRequestModel(selectedPosition, price));
                        }
                    }
                });
            }
        }
        Log.e("purchase success", "" + purchase);
    }

    @Override
    public void OnGetPurchaseDetail(SubscriptionResponse body) {

    }



    /*bp.purchase(YOUR_ACTIVITY, "YOUR PRODUCT ID FROM GOOGLE PLAY CONSOLE HERE");
    bp.subscribe(YOUR_ACTIVITY, "YOUR SUBSCRIPTION ID FROM GOOGLE PLAY CONSOLE HERE");*/
}

