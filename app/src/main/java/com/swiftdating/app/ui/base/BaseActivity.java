package com.swiftdating.app.ui.base;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.androidadvance.topsnackbar.TSnackbar;
import com.google.android.material.snackbar.Snackbar;
import com.swiftdating.app.R;
import com.swiftdating.app.common.CommonDialogs;
import com.swiftdating.app.common.CommonUtils;
import com.swiftdating.app.common.Global;
import com.swiftdating.app.common.MyProgressDialog;
import com.swiftdating.app.common.SubscriptionKeys;
import com.swiftdating.app.common.SubscriptionResponse;
import com.swiftdating.app.data.network.ApiCall;
import com.swiftdating.app.data.network.ApiCallback;
import com.swiftdating.app.data.network.Resource;
import com.swiftdating.app.data.preference.SharedPreference;
import com.swiftdating.app.model.NotificationModel;
import com.swiftdating.app.model.responsemodel.AccessTokenResponce;
import com.swiftdating.app.model.responsemodel.ProfileOfUser;
import com.swiftdating.app.model.responsemodel.VerificationResponseModel;
import com.swiftdating.app.ui.editProfileScreen.viewmodel.EditProfileViewModel;
import com.swiftdating.app.ui.homeScreen.HomeActivity;
import com.swiftdating.app.ui.homeScreen.fragment.LikesFrament;
import com.swiftdating.app.ui.loginScreen.LoginActivity;
import com.swiftdating.app.websocket.WebSocketService;

import java.util.List;


public abstract class BaseActivity extends AppCompatActivity implements PurchasesUpdatedListener, ApiCallback.PurchaseDetailCallback, ApiCallback.RefreshTokenCallback {
    public static final int PERMISSION_REQUEST_CODE_CG = 101;
    public static final String purchaseState = "PurchasedSuccessfully";
    private static final String TAG = "BaseActivity";
    public static Activity mActivity;
    protected static BillingClient client;
    private static OnPurchaseListener onPurchaseListener;
    public SharedPreference sp;
    public static boolean isPaymentLoadShow=true;
    public Context mContext;
    public boolean settings = false;
    public boolean isCardScreen = false;
    public boolean isChatScreen = false;
    public boolean isMatchScreen = false;
    public boolean isHomeScreen = false;
    public boolean isLikeScreen = false;
    public String message;
    public int userId;
    public int previousSelectedPos = 1;
    EditProfileViewModel editProfileViewModel;
    boolean isMyProfileApiCall = false;
    private ProgressDialog mProgressDialog;
    private MyProgressDialog myProgressDialog;
    private MyProfileResponse myProfileResponse;
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            TSnackbar snackbar = null;
            message =
                    intent.getExtras().getString("message");
            if (intent.getExtras().containsKey("approved")) {
                if (intent.getExtras().getBoolean("approved")) {
                    // sp.saveVerified("Yes");
                    sp.saveIsRejected(false);

                    //Save Date for Daily Popup
                    /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String today = simpleDateFormat.format(new Date());
                    sp.saveDate(today);*/

                    snackbar = TSnackbar.make(findViewById(android.R.id.content), message, TSnackbar.LENGTH_LONG);
                    if (isCardScreen) {
                        ((HomeActivity) mActivity).onTabSelected(((HomeActivity) mActivity).tabHome.getTabAt(0));
                    } else if (isMatchScreen) {
                        ((HomeActivity) mActivity).onTabSelected(((HomeActivity) mActivity).tabHome.getTabAt(3));
                    }
                }
            } else if (intent.getExtras().containsKey("rejected")) {
                snackbar = TSnackbar.make(findViewById(android.R.id.content), message, TSnackbar.LENGTH_LONG);
                // sp.saveVerified("No");
                sp.saveIsRejected(true);
                if (isCardScreen) {
                    ((HomeActivity) mActivity).onTabSelected(((HomeActivity) mActivity).tabHome.getTabAt(0));
                } else if (isMatchScreen) {
                    ((HomeActivity) mActivity).onTabSelected(((HomeActivity) mActivity).tabHome.getTabAt(3));
                }
            } else if (intent.getExtras().containsKey("deactivated")) {
                openActivityOnTokenExpire();
            } else if (intent.getExtras().containsKey("tokenApplied")) {
                snackbar = TSnackbar.make(findViewById(android.R.id.content), message, TSnackbar.LENGTH_LONG);

//                if (isMatchScreen) {
//                    userId = intent.getExtras().getInt("id");
//                    Intent i = new Intent("unique_name");
//                    i.putExtra("message", message);
//                    i.putExtra("id", userId);
//                    i.putExtra("myId", sp.getUserId());
//                    i.putExtra("image", intent.getExtras().getString("image"));
//                    mActivity.sendBroadcast(i);
//                }
            } else if (intent.getExtras().containsKey("pushMessage")) {
                if (isHomeScreen) {
                    ((HomeActivity) mActivity).badge_notification_1.setVisibility(View.VISIBLE);
                }
                if (isMatchScreen) {
                    userId = intent.getExtras().getInt("id");
                    Intent i = new Intent("unique_name");
                    i.putExtra("message", message);
                    i.putExtra("messageSent", intent.getExtras().getString("messageSent"));
                    i.putExtra("id", userId);
                    i.putExtra("myId", sp.getUserId());
                    i.putExtra("image", intent.getExtras().getString("image"));
                    mActivity.sendBroadcast(i);
                }
                if (!isChatScreen) {
                    if (!TextUtils.isEmpty(intent.getExtras().getString("message"))) {
                        snackbar = TSnackbar.make(findViewById(android.R.id.content), message, TSnackbar.LENGTH_LONG);

                    }
                } else {
                    if (userId != Integer.parseInt(sp.getChatUserId())) {
                        if (!TextUtils.isEmpty(intent.getExtras().getString("message")))
                            snackbar = TSnackbar.make(findViewById(android.R.id.content), message, TSnackbar.LENGTH_LONG);
                    }
                }

            } else {
                NotificationModel notificationModel = (NotificationModel) intent.getExtras().getSerializable("match");
                Log.e("BroadCaste", "onReceive: " + notificationModel.getMessage());
                snackbar = TSnackbar.make(findViewById(android.R.id.content), notificationModel.getMessage(), TSnackbar.LENGTH_LONG);
                if ((isLikeScreen || isCardScreen) && notificationModel.getMessage().contains("liked")) {//XYZ has liked you.
                    getMyProfile(myProfileResponse);
                }
            }
            if (snackbar != null) {
                snackbar.setActionTextColor(Color.WHITE);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(Color.parseColor("#EB4F5A"));
                TextView textView = (TextView) snackBarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();
            }
        }
    };

    protected static void setOnPurchaseListener(OnPurchaseListener onPurchaseListener) {
        BaseActivity.onPurchaseListener = onPurchaseListener;
        Log.e(TAG, "setOnPurchaseListener: " + (BaseActivity.onPurchaseListener == null));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
        subscribeModel();
    }

    @Override
    protected void onResume() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
            hitProfileApi();
        }
        super.onResume();
    }

    /***
     *  Hit Api if user open app without clicking on notification
     */
    private void hitProfileApi() {
        if (editProfileViewModel == null) {
            editProfileViewModel = ViewModelProviders.of(this).get(EditProfileViewModel.class);
        }
        if (sp.isloggedIn().equalsIgnoreCase("true") && !sp.getStatus().equalsIgnoreCase(Global.statusActive)) {
            editProfileViewModel.myProfileRequest(sp.getToken());
        }
    }

    public void getMyProfile(MyProfileResponse myProfileResponse) {
        this.myProfileResponse = myProfileResponse;
        if (sp == null) {
            sp = new SharedPreference(this);
        }
        if (myProfileResponse != null && sp.getStatus().equalsIgnoreCase(Global.statusActive) && !isMyProfileApiCall) {
            if (editProfileViewModel == null) {
                editProfileViewModel = ViewModelProviders.of(this).get(EditProfileViewModel.class);
            }
            isMyProfileApiCall = true;
            editProfileViewModel.myProfileRequest(sp.getToken());
        }
    }

    private void subscribeModel() {
        editProfileViewModel = ViewModelProviders.of(this).get(EditProfileViewModel.class);
        editProfileViewModel.myProfileResponse().observe(this, new Observer<Resource<VerificationResponseModel>>() {
            @Override
            public void onChanged(@Nullable Resource<VerificationResponseModel> resource) {
                if (resource == null) {
                    return;
                }
                switch (resource.status) {
                    case LOADING:
                        break;
                    case SUCCESS:
                        isMyProfileApiCall = false;
                        if (resource.data.getSuccess()) {
                            if (resource.data.getNoOfLikes() != null) {
                                if (isLikeScreen) {
                                    LikesFrament.MyLikeCount = sp.getPreviousLikeCount();
                                }
                                sp.saveNoOfLikes(resource.data.getNoOfLikes());
                            }
//                            Gson gson = new Gson();
//                            String user = sp.getUser();
                            ProfileOfUser obj = resource.data.getUser().getProfileOfUser();// gson.fromJson(user, ProfileOfUser.class);
//                            obj.setSuperLikesCount(resource.data.getUser().getProfileOfUser().getSuperLikesCount());
//                            obj.setDirectMessageCount(resource.data.getUser().getProfileOfUser().getDirectMessageCount());
                            sp.saveUserData(obj, null);
                            sp.saveString(SharedPreference.userEmail, resource.data.getUser().getEmail());
                            sp.saveString(SharedPreference.userPhone, resource.data.getUser().getMobile());
                            Log.e(TAG, "onChanged: " + resource.data.getUser().getSelfieVerificationStatus());
                            if (resource.data.getUser() != null && !TextUtils.isEmpty(resource.data.getUser().getSelfieVerificationStatus())) {
                                sp.saveSelfieVerificationStatus(resource.data.getUser().getSelfieVerificationStatus());
                            }

                            if (myProfileResponse != null)
                                myProfileResponse.setProfileData();

                            sp.saveStatus(resource.data.getUser().getStatus());
                            /*if (!sp.getVerified().equals(resource.data.getUser().getIsVerified()) *//*|| sp.isRejected() != resource.data.getUser().getisRejected().equals("1")*//*) {
                                if (isCardScreen) {
                                    ((HomeActivity) mActivity).onTabSelected(((HomeActivity) mActivity).tabHome.getTabAt(0));
                                } else if (isMatchScreen) {
                                    ((HomeActivity) mActivity).onTabSelected(((HomeActivity) mActivity).tabHome.getTabAt(3));
                                }
                            }*/
                        }
                        break;
                    case ERROR:
                        isMyProfileApiCall = false;
                        if (isLikeScreen) {
                            LikesFrament.MyLikeCount = -1;
                            if (myProfileResponse != null)
                                myProfileResponse.setProfileData();
                        }
                        break;
                }
            }
        });
    }

    protected void callPurchaseDetail(String subscriptionId, String purchaseToken) {
        Log.e(TAG, "callPurchaseDetail: " + subscriptionId + "   " + purchaseToken);

        if (!TextUtils.isEmpty(SubscriptionKeys.GOOGLE_ACCESS_TOKEN)) {
            ApiCall.getPurchaseDetails(SubscriptionKeys.GOOGLE_ACCESS_TOKEN, subscriptionId, purchaseToken, this);
        } else {
            callRefreshTokenApi(purchaseToken, subscriptionId);
        }
    }

    /**
     * * Method to Initialize
     */
    private void initialize() {
        mContext = this;
        mActivity = BaseActivity.this;
        sp = new SharedPreference(this);
        if (!WebSocketService.getInstance().isIs_opened() && sp.isloggedIn().equalsIgnoreCase("true") && isNetworkConnected()) {
            WebSocketService.getInstance().connectWebSocket(getApplicationContext(), true);
        }
        initializeBillingClient();
    }

    protected void initializeBillingClient() {
        Log.e(TAG, "initializeBillingClient: ");
        if (sp==null){
            sp = new SharedPreference(this);
        }
        if (client == null && isNetworkConnected() && sp.isloggedIn().equalsIgnoreCase("True")) {
            Log.e(TAG, "initializeBillingClient: client == null");
            Log.e(TAG, "initialize: client is Null");
            client = BillingClient.newBuilder(this)
                    .enablePendingPurchases()
                    .setListener(this)
                    .build();
            client.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingServiceDisconnected() {
                    Log.e(TAG, "onBillingServiceDisconnected: ");
//                    Toast.makeText(mContext, "BillingServiceDisconnected", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                    Log.e(TAG, "onBillingSetupFinished:");
                    if (CommonDialogs.vipTokenPriceList.size() == 0 || CommonDialogs.timeTokenPriceList.size() == 0 || CommonDialogs.crushTokenPriceList.size() == 0 || CommonDialogs.PremiumPriceList.size() == 0)
                        CommonDialogs.onBillingInitialized(client);
                }
            });
        }
    }

    /**
     * * Method to replaceFragment
     */
    public void replaceFragment(Fragment fragment, FragmentManager fm) {
        FragmentTransaction ft = fm.beginTransaction();
        // ft.setCustomAnimations(R.anim.slide_in_top,R.anim.slide_out_down);
        //ft.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_to_bottom, R.anim.slide_out_down, R.anim.slide_out_up);
        ft.replace(R.id.fragments, fragment);
        //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    /**
     * * Method to replaceFragment
     */
    public void openFragment(Fragment fragment, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStackImmediate();
        }
    }

    /**
     * * Method to replaceFragmentWith Backstack
     */
    public void replaceFragmentWithBackStack(Fragment fragment, FragmentManager fm) {
        FragmentTransaction ft = fm.beginTransaction();
        //ft.setCustomAnimations(R.anim.slide_in_top,R.anim.slide_out_down);
        //ft.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_to_bottom, R.anim.slide_out_down, R.anim.slide_out_up);
        if (previousSelectedPos == 1) {
            ft.add(R.id.fragments, fragment);
            ft.addToBackStack(fragment.getClass().getName());
        } else {
            ft.replace(R.id.fragments, fragment);
        }
        // ft.setTransition(FragmentTransaction.);
        ft.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * * Method to Show Toast
     */
    public void showToast(String msg) {
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * * Method to hide keyboard
     */
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    /**
     * * Method to hide keyboard
     */
    public void hideKeyboardFromView(View view) {
        //View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    /**
     * * Method to hide Loader
     */
    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * * Method to hide Loader
     */
    public void hideMyCustomLoading() {
        if (myProgressDialog != null && myProgressDialog.isShowing()) {
            myProgressDialog.dismiss();
        }
    }

    /**
     * * Method to check if net is connected
     */
    public boolean isNetworkConnected() {
        return CommonUtils.isNetworkConnected(getApplicationContext());
    }

    /**
     * Method to clear all preference and logout user
     */
    public void openActivityOnTokenExpire() {
        String deviceToken = sp.getDeviceToken();
        sp.clearData();
        sp.saveDeviceToken(deviceToken);
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void showLoading() {
        if (mProgressDialog == null || !mProgressDialog.isShowing())
            mProgressDialog = CommonUtils.showLoadingDialog(this);
    }

    public void showMyCustomLoading() {
        if (myProgressDialog == null || !myProgressDialog.isShowing()) {
            myProgressDialog = new MyProgressDialog(mContext);
            myProgressDialog.show();
        }
    }

    public void showSnackbar(View v, String text) {
        Snackbar snack = Snackbar.make(v, text, Snackbar.LENGTH_LONG);
        View view = snack.getView();
        view.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.pink));
        TextView tv = view.findViewById(R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snack.show();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkPermissionCG() {
        int camera = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA);
        int read = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int write = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return camera == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED
                && read == PackageManager.PERMISSION_GRANTED;
    }

    /*
     *** requesting permission
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionCG() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.CAMERA)
                && ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE_CG);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE_CG);
        }
    }

    public void alertDialogDeny(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setMessage(message)
                .setNegativeButton(mActivity.getString(R.string.ok_text),
                        (dialog, id) -> {
                            final Intent i = new Intent();
                            i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            i.addCategory(Intent.CATEGORY_DEFAULT);
                            i.setData(Uri.parse("package:" + mActivity.getPackageName()));
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            startActivity(i);
                            dialog.dismiss();
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void alertDialogPermission(final String permissionType) {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        String message = "";
        message = mActivity.getString(R.string.camera_gallery_permission_text);

        alertDialogBuilder.setMessage(message)
                .setPositiveButton(mActivity.getString(R.string.proceed_text),
                        (dialog, id) -> {
                            requestPermissionCG();
                        })
                .setNegativeButton(mActivity.getString(R.string.exit_text),
                        (dialog, id) -> {
                            //////////////
                        });
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null && !list.isEmpty()) {
            Log.e(TAG, "onPurchasesUpdated: " + list);
            try {
                if (onPurchaseListener != null)
                    onPurchaseListener.OnSuccessPurchase(list.get(0));
            } catch (Exception e) {
                Log.e(TAG, "onPurchasesUpdated: ", e.fillInStackTrace());
            }
        } else {
            Log.e(TAG, "onPurchasesUpdated: billingResultCode : " + billingResult.getResponseCode() + " billingResultMsg : " + billingResult.getDebugMessage());
        }
    }

    @Override
    public void onSuccessPurchaseDetail(SubscriptionResponse body) {
        if (onPurchaseListener != null)
            onPurchaseListener.OnGetPurchaseDetail(body);
    }

    @Override
    public void refreshAccessToken(String purchaseToken, String subscriptionId) {
        callRefreshTokenApi(purchaseToken, subscriptionId);
    }

    private void callRefreshTokenApi(String purchaseToken, String subscriptionId) {
        ApiCall.refreshAccessToken(SubscriptionKeys.REFRESH_TOEKN, purchaseToken, subscriptionId, this);
    }

    @Override
    public void onError(String error) {
        hideLoading();
        showSnackbar(findViewById(android.R.id.content), error);
        Log.e(TAG, "onError: " + error);
    }

    @Override
    public void onSuccessAccessToken(AccessTokenResponce body, String purchaseToken, String subscriptionId) {
        SubscriptionKeys.GOOGLE_ACCESS_TOKEN = body.getAccessToken();
        callPurchaseDetail(subscriptionId, purchaseToken);
    }

    protected void queryPurchasesAsync(OnQueryPurchasesListener listener) {
        if (listener != null) {
            if (client.isReady()) {
                client.queryPurchasesAsync(BillingClient.SkuType.SUBS, (billingResult, list) -> {
                    Log.e(TAG, "onQueryPurchasesResponse: " + billingResult.getResponseCode() + "   " + list);
                    if (listener != null)
                    listener.OnGetSuccessQueryPurchase(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK, list);
                });
            } else {
                listener.OnGetSuccessQueryPurchase(false, null);
            }
        }
    }

    protected void queryPurchaseHistoryAsync(OnQueryPurchasesHistoryListener listener) {
        if (listener != null) {
            if (client!=null&&client.isReady()) {
                client.queryPurchaseHistoryAsync(BillingClient.SkuType.SUBS, (billingResult, list) -> {
                    Log.e(TAG, "queryPurchaseHistoryAsyncResponse: " + billingResult.getResponseCode() + "   " + list);
                    if (listener != null)
                    listener.OnGetSuccessQueryPurchaseHistory(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK, list);
                });
            } else {
                listener.OnGetSuccessQueryPurchaseHistory(false, null);
            }
        }
    }

    protected BillingFlowParams getBillingFlowParam(SkuDetails sku) {
        return BillingFlowParams.newBuilder().setSkuDetails(sku).build();
    }

    protected ConsumeParams getConsumeParam(String purchaseToken) {
        return ConsumeParams.newBuilder().setPurchaseToken(purchaseToken).build();
    }

    protected AcknowledgePurchaseParams getAcknowledgeParams(String purchaseToken) {
        return AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchaseToken).build();
    }

    public interface OnPurchaseListener {
        void OnSuccessPurchase(Purchase purchase);

        void OnGetPurchaseDetail(SubscriptionResponse body);
    }

    public interface OnQueryPurchasesListener {
        void OnGetSuccessQueryPurchase(boolean isSuccess, List<Purchase> list);
    }

    public interface OnQueryPurchasesHistoryListener {
        void OnGetSuccessQueryPurchaseHistory(boolean isSuccess, List<PurchaseHistoryRecord> list);
    }

    public interface MyProfileResponse {
        void setProfileData();
    }
}

