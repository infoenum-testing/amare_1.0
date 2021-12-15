package com.swiftdating.app.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.swiftdating.app.BuildConfig;
import com.swiftdating.app.R;
import com.swiftdating.app.callbacks.OnInAppInterface;
import com.swiftdating.app.data.network.CallRestoreApi;
import com.swiftdating.app.data.preference.SharedPreference;
import com.swiftdating.app.model.requestmodel.PremiumTokenCountModel;
import com.swiftdating.app.ui.base.BaseActivity;
import com.swiftdating.app.ui.homeScreen.fragment.LikesFrament;
import com.swiftdating.app.ui.homeScreen.fragment.SearchFragment;
import com.swiftdating.app.ui.settingScreen.SliderAdapter;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class CommonDialogs {

    public static final String[] timeTokenArr = new String[]{"swift_time_token_1", "swift_time_token_5", "swift_time_token_10", "swift_time_tokens_20"};
    public static final String[] crushTokenArr = new String[]{"swift_crush_token_1", "swift_crush_token_5", "swift_crush_token_10", "swift_crush_token_20"};
    public static final String[] vipTokenArr = new String[]{"swift_vip_token_1", "swift_vip_token_5", "swift_vip_token_10", "swift_vip_token_20"};
    public static final String[] PremiumArr = new String[]{"swift_premium_1",/*"swift_premium",*/ "swift_premium_3", "swift_premium_6", "swift_premium_12"};

    public static final String[] DeluxeArr = new String[]{"deluxe_1", "deluxe_3", "deluxe_6", "deluxe_12"};
    // public static final Double[] DeluxePriceArr = new Double[]{19.99, 49.99, 79.99, 119.99};
    public static final Double[] PremiumPriceArr = new Double[]{19.99, 39.99, 59.99, 89.99};
    private static final long TIME_PERIOD = 3000;
    private static final String TAG = "CommonDialogs";
    public static List<InAppPriceValue> timeTokenPriceList = new ArrayList<>();
    public static List<InAppPriceValue> vipTokenPriceList = new ArrayList<>();
    public static List<InAppPriceValue> crushTokenPriceList = new ArrayList<>();
    public static List<InAppPriceValue> PremiumPriceList = new ArrayList<>();
    public static List<InAppPriceValue> DeluxePriceList = new ArrayList<>();
    /*-----------------------------------------------------------------*/
    public static List<SkuDetails> PremiumSkuList = new ArrayList<>();
    public static List<SkuDetails> vipTokenSkuList = new ArrayList<>();
    public static List<SkuDetails> crushTokenSkuList = new ArrayList<>();
    public static List<SkuDetails> timeTokenSkuList = new ArrayList<>();
    /*-----------------------------------------------------------------*/
    public static Timer timer;
    public static boolean isDialogOpen = false;
    static Dialog dialog;
    static int currentPage;
    static Runnable Update;
    static Handler handler;
    static String subscriptiontype = "";
    private static onPurchaseDeluxe onPurchaseDeluxe;
    private static CommonDialogs commonDialogs;
    private static int finalI;
    private static int indexOfSelectedLayout = 0;
    private static BillingClient myBC;
    private static ProgressDialog progressDialog;
    private Context context;

    public CommonDialogs(Activity context) {
        this.context = context;
    }

    public static void dismiss() {
        if (dialog != null)
            dialog.dismiss();
    }

    private static void gotoManageSubscription(Context context) {
        String PACKAGE_NAME = BuildConfig.APPLICATION_ID;
        Log.d(TAG, "gotoManageSubscription: " + PACKAGE_NAME);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/account/subscriptions?package=" + PACKAGE_NAME));
        context.startActivity(browserIntent);
    }

    /*
     *** Method to show dialog on Selfie Screen
     */
    public static void selfieDialog(Context context, View.OnClickListener clickListener) {
        dialog = new Dialog(context, R.style.MyDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.selfie_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView tvBack = dialog.findViewById(R.id.tvBack);
        Button btn_ready = dialog.findViewById(R.id.btn_ready);
        btn_ready.setOnClickListener(clickListener);
        tvBack.setOnClickListener(clickListener);
    }

    /**
     * alert dialog with two buttons
     *
     * @param clickListener : click listener for buttons
     * @param message       : text message in the dialog
     * @return : return dialog
     */
    public static Dialog alertDialogTwoButtons(Context context, final View.OnClickListener clickListener, String message) {
        dialog = new Dialog(context);
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
        tv_message.setText(message);

        tv_yes.setOnClickListener(clickListener);
        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public static void alertDialogToSureBirthDate(Context context, final View.OnClickListener clickListener, String message) {
        dialog = new Dialog(context);
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
        tv_message.setText(message);
        tv_yes.setText("Continue");
        tv_yes.setOnClickListener(clickListener);
        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public static Dialog alertDialogDailyDelux(Context context, final View.OnClickListener clickListener) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.alertdirect_msg_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        TextView tv_message = dialog.findViewById(R.id.tv_message);
        TextView tv_yes = dialog.findViewById(R.id.tv_yes);
        TextView tv_no = dialog.findViewById(R.id.tv_no);
        tv_yes.setOnClickListener(clickListener);
        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public static Dialog alertDialogTwoButtonsWithConfirm(Context context, final View.OnClickListener clickListener, String message) {
        dialog = new Dialog(context);
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
        tv_yes.setText("Confirm");
        tv_message.setText(message);
        tv_yes.setOnClickListener(clickListener);
        tv_no.setOnClickListener(view -> dialog.dismiss());
        return dialog;
    }

    public static Dialog newTwoButtonsDialog(Context context, final View.OnClickListener clickListener, String message) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.new_dialog_two_button);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        TextView tv_message = dialog.findViewById(R.id.tv_message);
        TextView tv_yes = dialog.findViewById(R.id.tv_yes);
        TextView tv_no = dialog.findViewById(R.id.tv_no);
        tv_yes.setText("Yes");
        tv_message.setText(message);
        tv_yes.setOnClickListener(view -> {
            tv_no.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.btn_bg_shadow, null));
            tv_no.setTextColor(context.getResources().getColor(R.color.pink));
            tv_yes.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.pink_btn_bg, null));
            tv_yes.setTextColor(context.getResources().getColor(R.color.white));

        });
        tv_no.setOnClickListener(view -> {
            tv_no.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.pink_btn_bg, null));
            tv_no.setTextColor(context.getResources().getColor(R.color.white));
            tv_yes.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.btn_bg_shadow, null));
            tv_yes.setTextColor(context.getResources().getColor(R.color.pink));
            dialog.dismiss();
        });
        return dialog;
    }

    public static void showAlreadyPremiumUser(Context context, String message) {
        if (dialog == null || !dialog.isShowing()) {
            dialog = new Dialog(Objects.requireNonNull(context));
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
            tv_no.setVisibility(View.GONE);
            tv_yes.setText("OK");
            tv_message.setText(message);

            tv_yes.setOnClickListener(view -> dialog.dismiss());
        }
    }

    /*
     *** Method to show subscription or crushToken Dialog
     */
    public static void purchaseDialog(Context mContext, String message, String description, final OnInAppInterface clickListener) {
        dialog = new Dialog(mContext, R.style.MyDialogTheme);
        int[] count;
        final String[] productid = new String[1];
        if (message.equalsIgnoreCase("Crush Tokens")) {
            count = new int[]{5};
            productid[0] = "crush_token_5";
        } else {
            count = new int[]{1};
            if (message.equalsIgnoreCase("Time Tokens")) {
                productid[0] = "time_token_1";
            } else {
                productid[0] = "premium_1";
            }
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setContentView(R.layout.custom_buy_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        ImageView ivclose = dialog.findViewById(R.id.ivclose);
        TextView tvTerms = dialog.findViewById(R.id.tvTerms);
        TextView tvHeading = dialog.findViewById(R.id.tvHeading);
        TextView tvSubHeading = dialog.findViewById(R.id.tvSubHeading);
        TextView tvSubHeading1 = dialog.findViewById(R.id.tvSubHeading1);
        RadioGroup rgPackage = dialog.findViewById(R.id.rgPackage);
        Button btnContinue = dialog.findViewById(R.id.btn_continue);
        RadioButton rb1 = dialog.findViewById(R.id.rb5Likes);
        RadioButton rb2 = dialog.findViewById(R.id.rb25Likes);
        RadioButton rb3 = dialog.findViewById(R.id.rb50Likes);
        ImageView ivIcon = dialog.findViewById(R.id.ivIcon);
        tvHeading.setText(message);
        tvSubHeading.setText(description);

        if (message.equalsIgnoreCase("Time Tokens")) {
            rb1.setText(mContext.getResources().getString(R.string.timeExtend1));
            rb2.setText(mContext.getResources().getString(R.string.timeExtend3));
            rb3.setText(mContext.getResources().getString(R.string.timeExtend5));
            ivIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.timeextend_large));
            tvTerms.setVisibility(View.GONE);
            tvSubHeading1.setText("Use Time Tokens to add another 24 hours to matches and enable chatting after they expire.");
        } else if (message.equalsIgnoreCase("Crush Tokens")) {
            tvTerms.setVisibility(View.GONE);
            tvSubHeading1.setText("Use crush tokens to let people know you have already liked them when they see your profile.");
        } else {
            rb1.setText(mContext.getResources().getString(R.string.premium1month));
            rb2.setText(mContext.getResources().getString(R.string.premium6month));
            rb3.setText(mContext.getResources().getString(R.string.premium12month));
            ivIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.bg_big_ic));
            tvSubHeading1.setText("Premium account enables unlimited likes, rewinds, and no ads. ");

        }

        rgPackage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId = rgPackage.getCheckedRadioButtonId();
                RadioButton radioDrinkButton = radioGroup.findViewById(selectedId);
                if (message.equalsIgnoreCase("Time Tokens")) {
                    count[0] = ((radioDrinkButton.getId() == R.id.rb5Likes) ? 1 :
                            (radioDrinkButton.getId() == R.id.rb25Likes) ? 3 : 5);
                    productid[0] = ((radioDrinkButton.getId() == R.id.rb5Likes) ? "time_token_1" :
                            (radioDrinkButton.getId() == R.id.rb25Likes) ? "time_token_3" : "time_token_5");
                } else if (message.equalsIgnoreCase("Crush Tokens")) {
                    count[0] = ((radioDrinkButton.getId() == R.id.rb5Likes) ? 5 :
                            (radioDrinkButton.getId() == R.id.rb25Likes) ? 25 : 50);
                    productid[0] = ((radioDrinkButton.getId() == R.id.rb5Likes) ? "crush_token_5" :
                            (radioDrinkButton.getId() == R.id.rb25Likes) ? "crush_token_25" : "crush_token_50");
                } else {
                    count[0] = ((radioDrinkButton.getId() == R.id.rb5Likes) ? 1 :
                            (radioDrinkButton.getId() == R.id.rb25Likes) ? 6 : 12);
                    productid[0] = ((radioDrinkButton.getId() == R.id.rb5Likes) ? "premium_1" :
                            (radioDrinkButton.getId() == R.id.rb25Likes) ? "premium_6" : "premium_12");
                }
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (message.equalsIgnoreCase("Crush Tokens")) {
                    clickListener.OnItemClick(count[0], 0, productid[0]);
                } else if (message.equalsIgnoreCase("Time Tokens")) {
                    clickListener.OnItemClick(count[0], 1, productid[0]);
                } else {
                    clickListener.OnItemClick(count[0], 2, productid[0]);
                }
                new SharedPreference(mContext).setDialogOpen(false);
                dialog.dismiss();
            }
        });

        ivclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new SharedPreference(mContext).setDialogOpen(false);
                dialog.dismiss();
            }
        });

    }

    public static void CrushPurChaseDialog(Context ctx, final onProductConsume clickListener) {
        if (dialog == null || !dialog.isShowing()) {
            currentPage = 0;
            indexOfSelectedLayout = 0;

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ctx, R.style.BottomSheetDialog);
            bottomSheetDialog.setContentView(R.layout.custom_buy_crush_dialog);
            bottomSheetDialog.show();
            bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetDialog.getBehavior().setDraggable(false);
            dialog = bottomSheetDialog;

            /*dialog = new Dialog(ctx, R.style.PauseDialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_buy_crush_dialog);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
*/

            ImageView ivclose = dialog.findViewById(R.id.image_back);

            LinearLayout[] layouts = new LinearLayout[4];
            layouts[0] = dialog.findViewById(R.id.rb5Likes);
            layouts[1] = dialog.findViewById(R.id.rb25Likes);
            layouts[2] = dialog.findViewById(R.id.tokens10Lay);
            layouts[3] = dialog.findViewById(R.id.tokens20Lay);
            for (int i = 0; i < layouts.length; i++) {
                int finalI = i;
                layouts[i].setOnClickListener(v -> {
                    indexOfSelectedLayout = finalI;
                    unSelectAll(layouts);
                    layouts[finalI].setBackgroundResource(R.drawable.yellow_radial);
                });
            }

            Button btn_continue = dialog.findViewById(R.id.btn_continue);
            btn_continue.setOnClickListener(v -> {
                if (indexOfSelectedLayout == 0) {
                    clickListener.onClickToken("crushToken", 1, indexOfSelectedLayout);//0.99
                } else if (indexOfSelectedLayout == 1) {
                    clickListener.onClickToken("crushToken", 5, indexOfSelectedLayout);//3.99
                } else if (indexOfSelectedLayout == 2) {
                    clickListener.onClickToken("crushToken", 10, indexOfSelectedLayout);//6.99
                } else {
                    clickListener.onClickToken("crushToken", 20, indexOfSelectedLayout);//12.99
                }
                dialog.dismiss();
            });


            ivclose.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }
    }

    private static void unSelectAll(LinearLayout[] layouts) {
        for (LinearLayout layout : layouts) {
            layout.setBackgroundResource(R.drawable.img_rectangle_outline);
        }
    }

    public static void TimeTokenPurChaseDialog(Context ctx, final onProductConsume clickListener) {
        if (dialog == null || !dialog.isShowing()) {
            indexOfSelectedLayout = 0;
            currentPage = 0;
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ctx, R.style.BottomSheetDialog);
            bottomSheetDialog.setContentView(R.layout.custom_time_token_dialog);
            bottomSheetDialog.show();
            bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetDialog.getBehavior().setDraggable(false);
            dialog = bottomSheetDialog;
          /*  dialog = new Dialog(ctx, R.style.PauseDialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_time_token_dialog);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            */


            TextView[] tvToken1Price = new TextView[4];
            tvToken1Price[0] = dialog.findViewById(R.id.tvToken1Price);
            tvToken1Price[1] = dialog.findViewById(R.id.tvToken5Price);
            tvToken1Price[2] = dialog.findViewById(R.id.tvToken10Price);
            tvToken1Price[3] = dialog.findViewById(R.id.tvToken20Price);

      /*      if (timeTokenPriceList.size() > 0) {
                //for (int i = 0; i < tvToken1Price.length; i++) {
                for (int i = 0; i < timeTokenPriceList.size(); i++) {
                    if (!TextUtils.isEmpty(timeTokenPriceList.get(i).getPriceTxt())) {
                        tvToken1Price[i].setText(timeTokenPriceList.get(i).getPriceTxt());
                    }
                }
            }*/

            ImageView ivclose = dialog.findViewById(R.id.image_back);

            LinearLayout[] layouts = new LinearLayout[4];
            layouts[0] = dialog.findViewById(R.id.rb5Likes);
            layouts[1] = dialog.findViewById(R.id.rb25Likes);
            layouts[2] = dialog.findViewById(R.id.tokens10Lay);
            layouts[3] = dialog.findViewById(R.id.tokens20Lay);
            for (int i = 0; i < layouts.length; i++) {
                int finalI = i;
                layouts[i].setOnClickListener(v -> {
                    indexOfSelectedLayout = finalI;
                    unSelectAll(layouts);
                    layouts[finalI].setBackgroundResource(R.drawable.blue_radial);
                });
            }
            Button btn_continue = dialog.findViewById(R.id.btn_continue);
            btn_continue.setOnClickListener(v -> {
                if (indexOfSelectedLayout == 0) {
                    clickListener.onClickToken("timeToken", 1, indexOfSelectedLayout);//0.99
                } else if (indexOfSelectedLayout == 1) {
                    clickListener.onClickToken("timeToken", 5, indexOfSelectedLayout);//3.99
                } else if (indexOfSelectedLayout == 2) {
                    clickListener.onClickToken("timeToken", 10, indexOfSelectedLayout);//6.99
                } else {
                    if (indexOfSelectedLayout < timeTokenPriceList.size()) {
                        clickListener.onClickToken("timeToken", 20, indexOfSelectedLayout);//12.99
                    } else {
                        clickListener.onClickToken("timeToken", 10, indexOfSelectedLayout);//6.99
                    }
                }
                dialog.dismiss();
            });


            ivclose.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }
        //  homeViewModel.addTimeToken(new TimeTokenRequestModel(selectedPosition, price));
    }

    public static void VIPPurChaseDialog(Context ctx, final onProductConsume clickListener) {
        if (dialog == null || !dialog.isShowing()) {
            currentPage = 0;
            indexOfSelectedLayout = 0;

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ctx, R.style.BottomSheetDialog);
            bottomSheetDialog.setContentView(R.layout.custom_buy_vip_dialog);
            bottomSheetDialog.show();
            bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetDialog.getBehavior().setDraggable(false);
            dialog = bottomSheetDialog;
           /* dialog = new Dialog(ctx, R.style.PauseDialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_buy_vip_dialog);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                        dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);*/


            TextView[] tvToken1Price = new TextView[4];
            tvToken1Price[0] = dialog.findViewById(R.id.tvToken1Price);
            tvToken1Price[1] = dialog.findViewById(R.id.tvToken5Price);
            tvToken1Price[2] = dialog.findViewById(R.id.tvToken10Price);
            tvToken1Price[3] = dialog.findViewById(R.id.tvToken20Price);

           /* if (vipTokenPriceList.size() > 0) {
                for (int i = 0; i < tvToken1Price.length; i++) {
                    if (!TextUtils.isEmpty(vipTokenPriceList.get(i).getPriceTxt())) {
                        tvToken1Price[i].setText(vipTokenPriceList.get(i).getPriceTxt());
                    }
                }
            }*/

            ImageView ivclose = dialog.findViewById(R.id.image_back);
            LinearLayout[] layouts = new LinearLayout[4];
            layouts[0] = dialog.findViewById(R.id.rb5Likes);
            layouts[1] = dialog.findViewById(R.id.rb25Likes);
            layouts[2] = dialog.findViewById(R.id.tokens10Lay);
            layouts[3] = dialog.findViewById(R.id.tokens20Lay);
            for (int i = 0; i < layouts.length; i++) {
                int finalI = i;
                layouts[i].setOnClickListener(v -> {
                    indexOfSelectedLayout = finalI;
                    unSelectAll(layouts);
                    layouts[finalI].setBackgroundResource(R.drawable.vip_token_red_bg);
                });
            }
            Button btn_continue = dialog.findViewById(R.id.btn_continue);
            btn_continue.setOnClickListener(v -> {
                if (indexOfSelectedLayout == 0) {
                    clickListener.onClickToken("vipToken", 1, indexOfSelectedLayout);//4.99
                } else if (indexOfSelectedLayout == 1) {
                    clickListener.onClickToken("vipToken", 5, indexOfSelectedLayout);//19.99
                } else if (indexOfSelectedLayout == 2) {
                    clickListener.onClickToken("vipToken", 10, indexOfSelectedLayout);//34.99
                } else {
                    clickListener.onClickToken("vipToken", 20, indexOfSelectedLayout);//59.99
                }
                dialog.dismiss();
            });
            ivclose.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }
    }

    private static void showLoader(Context context) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = CommonUtils.showLoadingDialog(context);
        }
    }

    private static void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public static Dialog PremuimPurChaseDialog(Context ctx, final onProductConsume clickListener, SharedPreference sp) {
        if (!sp.getPremium()) {

            ViewPager viewPager;
            TabLayout text_pager_indicator;
            SliderAdapter sliderAdapter;
            if (dialog == null || !dialog.isShowing()) {
                currentPage = 0;
                indexOfSelectedLayout = 0;
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ctx, R.style.BottomSheetDialog);
                bottomSheetDialog.setContentView(R.layout.custom_buy_premium_dialog);
                bottomSheetDialog.show();
                bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetDialog.getBehavior().setDraggable(false);
                dialog = bottomSheetDialog;
/*

            dialog = new Dialog(ctx, R.style.PauseDialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_buy_premium_dialog);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

*/

                LinearLayout[] layouts = new LinearLayout[4];
                layouts[0] = dialog.findViewById(R.id.rb5Likes);
                layouts[1] = dialog.findViewById(R.id.rb25Likes);
                layouts[2] = dialog.findViewById(R.id.tokens10Lay);
                layouts[3] = dialog.findViewById(R.id.tokens20Lay);
                for (int i = 0; i < layouts.length; i++) {
                    int finalI = i;
                    layouts[i].setOnClickListener(v -> {
                        indexOfSelectedLayout = finalI;
                        unSelectAll(layouts);
                        layouts[finalI].setBackgroundResource(R.drawable.vip_token_red_bg);
                    });
                }
                Button btn_continue = dialog.findViewById(R.id.btn_continue);
                btn_continue.setOnClickListener(v -> {
                    if (indexOfSelectedLayout == 0) {
                        clickListener.onClickToken("PremiumPurchase", 1, indexOfSelectedLayout);//9.99
                    } else if (indexOfSelectedLayout == 1) {
                        clickListener.onClickToken("PremiumPurchase", 3, indexOfSelectedLayout);//24.99
                    } else if (indexOfSelectedLayout == 2) {
                        clickListener.onClickToken("PremiumPurchase", 6, indexOfSelectedLayout);//39.99
                    } else {
                        clickListener.onClickToken("PremiumPurchase", 12, indexOfSelectedLayout);//59.99
                    }
                    dialog.dismiss();
                });

                TextView[] tvToken1Price = new TextView[4];
                if (sp != null) {
                    TextView tv_subscribe = dialog.findViewById(R.id.tv_subscribe);
                    tv_subscribe.setVisibility(sp.getPremium() ? View.VISIBLE : View.GONE);
                }

                tvToken1Price[0] = dialog.findViewById(R.id.tvToken1Price);
                tvToken1Price[1] = dialog.findViewById(R.id.tvToken5Price);
                tvToken1Price[2] = dialog.findViewById(R.id.tvToken10Price);
                tvToken1Price[3] = dialog.findViewById(R.id.tvToken20Price);

                TextView tv_restore = dialog.findViewById(R.id.tv_restore);
                text_pager_indicator = dialog.findViewById(R.id.text_pager_indicator);

                viewPager = dialog.findViewById(R.id.pagerSlider);
                String[] tab_names = ctx.getResources().getStringArray(R.array.arr_premium_txt);
                List<String> titleList = new ArrayList<>();
                Collections.addAll(titleList, tab_names);
                sliderAdapter = new SliderAdapter(Objects.requireNonNull(ctx), titleList, null);
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
                handler = new Handler();
                Update = () -> {
                    if (currentPage == viewPager.getAdapter().getCount()) {
                        currentPage = 0;
                    }
                    viewPager.setCurrentItem(currentPage, true);
                    currentPage++;
                };

                if (timer != null) {
                    timer.cancel();
                }
                timer = new Timer();

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(Update);
                    }
                }, 0, TIME_PERIOD);

                String one = "Already paid for Premium?";
                Spannable word = new SpannableString(one);
                word.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(ctx.getResources(), R.color.grey_new, null)), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv_restore.setText(word);

                Spannable wordTwo = new SpannableString(" Restore Purchase.");
                wordTwo.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(ctx.getResources(), R.color.red_start, null)), 0, wordTwo.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                tv_restore.append(wordTwo);

                tv_restore.setOnClickListener(v -> {
                    if ((myBC != null && myBC.isReady())) {
                       /* myBC.queryPurchaseHistoryAsync(BillingClient.SkuType.SUBS, new PurchaseHistoryResponseListener() {
                            @Override
                            public void onPurchaseHistoryResponse(@NonNull BillingResult billingResult,List<PurchaseHistoryRecord> list) {
                                String id;
                                if (list!=null) {
                                    for (PurchaseHistoryRecord record : list) {
                                        id = record.getSkus().get(0);
                                        if (id.equals("swift_premium_1") || id.equals("swift_premium_3") || id.equals("swift_premium_6") || id.equals("swift_premium_12")) {
                                            double price = 0;
                                            if (PremiumSkuList != null && !PremiumSkuList.isEmpty())
                                                for (int i = 0; i < PremiumSkuList.size(); i++) {
                                                    if (PremiumSkuList.get(i).getSku().equalsIgnoreCase(id)) {
                                                        price = PremiumPriceArr[i];
                                                        break;
                                                    }
                                                }
                                            PremiumTokenCountModel model=null;
                                            Purchase purchase= null;
                                            try {
                                                purchase = new Purchase(record.getOriginalJson(),record.getSignature());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            if (price>0&&purchase!=null) {
                                                model = new PremiumTokenCountModel("1",
                                                id,
                                                price,
                                                Integer.parseInt(id.split("_")[2]),
                                                purchase.getOrderId(),
                                                purchase.getPurchaseToken(),
                                                CommonUtils.getDateForPurchase(purchase.getPurchaseTime()),
                                                purchase.getSignature(),
                                                BaseActivity.purchaseState);
                                            }
                                            if (model != null) {
                                                new CallRestoreApi().callApi(model, new SharedPreference(ctx), subscriptiontype, new onPurchaseRestore() {
                                                    @Override
                                                    public void onError(String msg) {
                                                        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
                                                        hideLoading();
                                                        dialog.dismiss();
                                                    }

                                                    @Override
                                                    public void onSucces() {
                                                        Toast.makeText(ctx, "Purchase Restored Successfully", Toast.LENGTH_SHORT).show();
                                                        hideLoading();
                                                        dialog.dismiss();
                                                    }
                                                });
                                            } else {
                                                //Toast.makeText(ctx,"Purchase Restore unavailable.",Toast.LENGTH_SHORT).show();
                                                hideLoading();
                                                //dialog.dismiss();
                                            }
                                            break;
                                        }
                                    }
                                }else {
                                    hideLoading();
                                }
                            }
                        });*/
                        showLoader(ctx);

                        myBC.queryPurchasesAsync(BillingClient.SkuType.SUBS, new PurchasesResponseListener() {
                            @Override
                            public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                                Log.e(TAG, "onQueryPurchasesResponse: "+list );
                                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && !list.isEmpty()) {
                                    String productId;
                                    for (Purchase purchase : list) {
                                         productId = purchase.getSkus().get(0);
                                        if (productId.equals("swift_premium_1") || productId.equals("swift_premium_3") || productId.equals("swift_premium_6") || productId.equals("swift_premium_12")) {
                                            PremiumTokenCountModel model = null;
                                            double price = 0;
                                            if (PremiumSkuList != null && !PremiumSkuList.isEmpty()) {
                                                for (int i = 0; i < PremiumSkuList.size(); i++) {
                                                    if (PremiumSkuList.get(i).getSku().equalsIgnoreCase(productId)) {
                                                        price = PremiumPriceArr[i];
                                                        break;
                                                    }
                                                }
                                            }
                                            if (price > 0) {
                                                model = new PremiumTokenCountModel(subscriptiontype,
                                                        productId,
                                                        price,
                                                        Integer.parseInt(productId.split("_")[1]),
                                                        purchase.getOrderId(),
                                                        purchase.getPurchaseToken(),
                                                        CommonUtils.getDateForPurchase(purchase.getPurchaseTime()),
                                                        purchase.getSignature(),
                                                        BaseActivity.purchaseState);
                                            }
                                            if (model != null) {
                                                new CallRestoreApi().callApi(model, new SharedPreference(ctx), subscriptiontype, new onPurchaseRestore() {
                                                    @Override
                                                    public void onError(String msg) {
                                                        hideLoading();
                                                        showToast(msg,ctx);
//                                                        dialog.dismiss();
                                                    }

                                                    @Override
                                                    public void onSucces() {
                                                        hideLoading();
                                                        showToast("Purchase Restored Successfully",ctx);
                                                        dialog.dismiss();
                                                    }
                                                });
                                            } else {
                                                hideLoading();
                                                showToast("Purchase not available.",ctx);
                                                //dialog.dismiss();
                                            }
                                            break;
                                        }
                                    }
                                } else {
                                    hideLoading();
                                    showToast("Purchase not available.",ctx);
//                                    Toast.makeText(ctx, "unable to Restore", Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(ctx, "Purchase not available.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                if (PremiumPriceList.size() > 0) {
                    for (int i = 0; i < tvToken1Price.length; i++) {
                        if (!TextUtils.isEmpty(PremiumPriceList.get(i).getPriceTxt())) {
                            tvToken1Price[i].setText(PremiumPriceList.get(i).getPriceTxt());
                        }
                    }
                }
                ImageView ivclose = dialog.findViewById(R.id.image_back);


                ivclose.setOnClickListener(v -> dialog.dismiss());

                dialog.show();
            }
            return dialog;
        } else {
            showAlreadyPremiumUser(ctx, ctx.getString(R.string.you_have_active_subscription));
        }
        return null;
    }

    private static void showToast(String s, Context ctx) {
        Log.e(TAG, "showToast: "+s );
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }

    /*public static CommonDialogs DeluxePurChaseDialog(Context ctx, onProductConsume clickListener) {

        if (dialog == null || !dialog.isShowing()) {
            currentPage = 0;
            indexOfSelectedLayout = 0;
            dialog = new Dialog(ctx, R.style.PauseDialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_buy_deluxe_dialog);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            TextView tv_upgradetxt = dialog.findViewById(R.id.tv_upgradetxt);
            tv_upgradetxt.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            MaskFilter mmm = tv_upgradetxt.getPaint().setMaskFilter(null);
            tv_upgradetxt.getPaint().setMaskFilter(mmm);
            TextView tv_txt = dialog.findViewById(R.id.tv_txt);
            TextView tv_restore = dialog.findViewById(R.id.tv_restore);
            tv_restore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PremiumTokenCountModel model;
                    if ((myBp != null && myBp.isInitialized())) {
                        showLoader(ctx);
                        model = checkExistingSubscriptionForDeluxe(myBp, ctx);
                        if (model != null) {
                            Log.e(TAG, "onClick: " + model.getPurchaseState());
                            new CallRestoreApi().callApi(model, new SharedPreference(ctx), subscriptiontype, new onPurchaseRestore() {
                                @Override
                                public void onError(String msg) {
                                    Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
                                    hideLoading();
                                    dialog.dismiss();
                                }

                                @Override
                                public void onSucces() {
                                    Toast.makeText(ctx, "Purchase Restored Successfully", Toast.LENGTH_SHORT).show();
                                    hideLoading();
                                    dialog.dismiss();
                                }
                            });
                        } else {
                            // Toast.makeText(ctx,"Purchase Restore unavailable.",Toast.LENGTH_SHORT).show();
                            hideLoading();
                            // dialog.dismiss();
                        }
                    }
                    //gotoManageSubscription(ctx);
                }
            });
            tv_txt.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            MaskFilter mm = tv_txt.getPaint().setMaskFilter(null);
            tv_txt.getPaint().setMaskFilter(mm);
            Button btn_continue = dialog.findViewById(R.id.btn_continue);

            ViewPager pager_text = dialog.findViewById(R.id.pager_text);
            pager_text.setAdapter(new TextPagerAdapter(Arrays.asList(ctx.getResources().getStringArray(R.array.DeluxeStringArray)), ctx));
            WormDotsIndicator indicator = dialog.findViewById(R.id.text_pager_indicator);
            indicator.setViewPager(pager_text);

            pager_text.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

            handler = new Handler();
            Update = () -> {
                if (currentPage == pager_text.getAdapter().getCount()) {
                    currentPage = 0;
                }
                pager_text.setCurrentItem(currentPage, true);
                currentPage++;
            };

            if (timer != null) {
                timer.cancel();
            }
            timer = new Timer();
            timer.schedule(new TimerTask() { // task to be scheduled
                @Override
                public void run() {
                    handler.post(Update);
                }
            }, 0, TIME_PERIOD);

            LinearLayout[] layouts = new LinearLayout[4];
            layouts[0] = dialog.findViewById(R.id.rb5Likes);
            layouts[1] = dialog.findViewById(R.id.rb25Likes);
            layouts[2] = dialog.findViewById(R.id.tokens10Lay);
            layouts[3] = dialog.findViewById(R.id.tokens20Lay);
            for (int i = 0; i < layouts.length; i++) {
                int finalI = i;
                layouts[i].setOnClickListener(va -> {
                    indexOfSelectedLayout = finalI;
                    unSelectAll(layouts);
                    layouts[finalI].setBackgroundResource(R.drawable.ic_bg_premium_selected);
                });
            }

            TextView[] tvToken1Price = new TextView[4];
            tvToken1Price[0] = dialog.findViewById(R.id.tvToken1Price);
            tvToken1Price[1] = dialog.findViewById(R.id.tvToken5Price);
            tvToken1Price[2] = dialog.findViewById(R.id.tvToken10Price);
            tvToken1Price[3] = dialog.findViewById(R.id.tvToken20Price);

            if (DeluxePriceList.size() > 0) {
                for (int i = 0; i < tvToken1Price.length; i++) {
                    if (!TextUtils.isEmpty(DeluxePriceList.get(i).getPriceTxt())) {
                        tvToken1Price[i].setText(DeluxePriceList.get(i).getPriceTxt());
                    }
                }
            }


            btn_continue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //onPurchaseDeluxe.OnClickContinue();
                    if (indexOfSelectedLayout == 0) {
                        clickListener.onClickToken("DeluxePurChase", 1, indexOfSelectedLayout);//19.99
                    } else if (indexOfSelectedLayout == 1) {
                        clickListener.onClickToken("DeluxePurChase", 3, indexOfSelectedLayout);//49.99
                    } else if (indexOfSelectedLayout == 2) {
                        clickListener.onClickToken("DeluxePurChase", 6, indexOfSelectedLayout);//79.99
                    } else {
                        clickListener.onClickToken("DeluxePurChase", 12, indexOfSelectedLayout);//119.99
                    }
                    isDialogOpen = false;
                    dialog.dismiss();
                }
            });

            ImageView ivclose = dialog.findViewById(R.id.ivclose);
            ivclose.setOnClickListener(v -> {
                isDialogOpen = false;
                dialog.dismiss();
                if (clickListener.getClass() == SearchFragment.class) {
                    SearchFragment.setRecycleToTop();
                } else if (clickListener.getClass() == LikesFrament.class) {
                    LikesFrament.setRecycleToTop();
                }
            });
            dialog.show();
        }
        if (commonDialogs == null)
            commonDialogs = new CommonDialogs(((Activity) ctx));
        return commonDialogs;
    }*/

    /*public static void DeluxePurChaseDialogWithMessage(Context ctx, onProductConsume clickListener, String msg) {
        if (dialog == null || !dialog.isShowing()) {
            currentPage = 0;
            indexOfSelectedLayout = 0;
            dialog = new Dialog(ctx, R.style.PauseDialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_buy_deluxe_dialog);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            TextView tv_upgradetxt = dialog.findViewById(R.id.tv_upgradetxt);
            tv_upgradetxt.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            MaskFilter mmm = tv_upgradetxt.getPaint().setMaskFilter(null);
            tv_upgradetxt.getPaint().setMaskFilter(mmm);
            TextView tv_txt = dialog.findViewById(R.id.tv_txt);
            tv_txt.setText(msg);
            tv_txt.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            MaskFilter mm = tv_txt.getPaint().setMaskFilter(null);
            tv_txt.getPaint().setMaskFilter(mm);
            Button btn_continue = dialog.findViewById(R.id.btn_continue);
            TextView tv_restore = dialog.findViewById(R.id.tv_restore);
            tv_restore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PremiumTokenCountModel model;
                    if ((myBp != null && myBp.isInitialized())) {
                        showLoader(ctx);
                        model = checkExistingSubscriptionForDeluxe(myBp, ctx);
                        if (model != null) {
                            new CallRestoreApi().callApi(model, new SharedPreference(ctx), subscriptiontype, new onPurchaseRestore() {
                                @Override
                                public void onError(String msg) {
                                    Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
                                    hideLoading();
                                    dialog.dismiss();
                                }

                                @Override
                                public void onSucces() {
                                    Toast.makeText(ctx, "Purchase Restored Successfully", Toast.LENGTH_SHORT).show();
                                    hideLoading();
                                    dialog.dismiss();
                                }
                            });
                        } else {
                            //Toast.makeText(ctx,"Purchase Restore unavailable.",Toast.LENGTH_SHORT).show();
                            hideLoading();
                            //dialog.dismiss();
                        }
                    }
                }
            });
            ViewPager pager_text = dialog.findViewById(R.id.pager_text);
            pager_text.setAdapter(new TextPagerAdapter(Arrays.asList(ctx.getResources().getStringArray(R.array.DeluxeStringArray)), ctx));
            WormDotsIndicator indicator = dialog.findViewById(R.id.text_pager_indicator);
            indicator.setViewPager(pager_text);

            pager_text.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

            handler = new Handler();
            Update = () -> {
                if (currentPage == pager_text.getAdapter().getCount()) {
                    currentPage = 0;
                }
                pager_text.setCurrentItem(currentPage, true);
                currentPage++;
            };

            if (timer != null) {
                timer.cancel();
            }
            timer = new Timer();
            timer.schedule(new TimerTask() { // task to be scheduled
                @Override
                public void run() {
                    handler.post(Update);
                }
            }, 0, TIME_PERIOD);

            LinearLayout[] layouts = new LinearLayout[4];
            layouts[0] = dialog.findViewById(R.id.rb5Likes);
            layouts[1] = dialog.findViewById(R.id.rb25Likes);
            layouts[2] = dialog.findViewById(R.id.tokens10Lay);
            layouts[3] = dialog.findViewById(R.id.tokens20Lay);
            for (int i = 0; i < layouts.length; i++) {
                int finalI = i;
                layouts[i].setOnClickListener(v -> {
                    indexOfSelectedLayout = finalI;
                    unSelectAll(layouts);
                    layouts[finalI].setBackgroundResource(R.drawable.ic_bg_premium_selected);
                });
            }

            TextView[] tvToken1Price = new TextView[4];
            tvToken1Price[0] = dialog.findViewById(R.id.tvToken1Price);
            tvToken1Price[1] = dialog.findViewById(R.id.tvToken5Price);
            tvToken1Price[2] = dialog.findViewById(R.id.tvToken10Price);
            tvToken1Price[3] = dialog.findViewById(R.id.tvToken20Price);

            if (DeluxePriceList.size() > 0) {
                for (int i = 0; i < tvToken1Price.length; i++) {
                    if (!TextUtils.isEmpty(DeluxePriceList.get(i).getPriceTxt())) {
                        tvToken1Price[i].setText(DeluxePriceList.get(i).getPriceTxt());
                    }
                }
            }


            btn_continue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //onPurchaseDeluxe.OnClickContinue();
                    if (indexOfSelectedLayout == 0) {
                        clickListener.onClickToken("DeluxePurChase", 1, indexOfSelectedLayout);//19.99
                    } else if (indexOfSelectedLayout == 1) {
                        clickListener.onClickToken("DeluxePurChase", 3, indexOfSelectedLayout);//49.99
                    } else if (indexOfSelectedLayout == 2) {
                        clickListener.onClickToken("DeluxePurChase", 6, indexOfSelectedLayout);//79.99
                    } else {
                        clickListener.onClickToken("DeluxePurChase", 12, indexOfSelectedLayout);//119.99
                    }
                    dialog.dismiss();
                }
            });

            ImageView ivclose = dialog.findViewById(R.id.ivclose);
            ivclose.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }
        *//*if (commonDialogs == null)
            commonDialogs = new CommonDialogs(((Activity) ctx));
        return commonDialogs;*//*
    }*/

    /**
     * Method for report Dialog
     */
    public static void reportDialogNew(Activity mActivity, onClick click) {
        dialog = new Dialog(mActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_report);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout[] lls = new LinearLayout[6];

        lls[0] = dialog.findViewById(R.id.ll_photo);
        lls[1] = dialog.findViewById(R.id.ll_content);
        lls[2] = dialog.findViewById(R.id.ll_behave);
        lls[3] = dialog.findViewById(R.id.ll_stolen);
        lls[4] = dialog.findViewById(R.id.ll_age);
        lls[5] = dialog.findViewById(R.id.ll_other);

        dialog.findViewById(R.id.btn_submit).setOnClickListener(view -> dialog.dismiss());

        for (int i = 0; i < lls.length; i++) {
            int k = i;
            lls[i].setOnClickListener(v -> {
                finalI = k;

                if (k == 5)
                    click.callDefaultDialog(k);

                click.callDefaultDialog(finalI);

            });
        }

    }

    private static void clearTextViewTxt(TextView viewById) {
    }

    public static Dialog alertDialogConfirm(Context context, final View.OnClickListener clickListener, String message) {
        dialog = new Dialog(context);
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
        tv_no.setText("No");
        tv_message.setText(message);
        tv_yes.setOnClickListener(clickListener);
        tv_no.setOnClickListener(clickListener);
        return dialog;
    }

    public static void showAlreadyDeluxe(Context mActivity) {
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
        tv_no.setVisibility(View.GONE);
        tv_yes.setText("ok");
        tv_message.setText("Your are already subscribed to Deluxe and cannot downgrade to Premium until your subscription is over. Deluxe subscription has all features of Premium plus much more.");
        tv_yes.setOnClickListener(v -> dialog.dismiss());
    }

    public static void showPendingSelfieStatus(Context mActivity) {
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
        tv_no.setVisibility(View.GONE);
        tv_yes.setText("ok");
        tv_message.setText("Your selfie is pending verification.\n Please wait for admin to verify your profile");
        tv_yes.setOnClickListener(v -> dialog.dismiss());
    }

    /*public static void onBillingInitialized(BillingProcessor bp) {
        myBp = bp;*//*
        setPurchaseData(vipTokenArr, vipTokenPriceList, bp);
        setPurchaseData(timeTokenArr, timeTokenPriceList, bp);
        setPurchaseData(crushTokenArr, crushTokenPriceList, bp);
        setSubcripData(PremiumArr, PremiumPriceList, bp, PremiumPriceArr);*//*
        // setSubcripData(DeluxeArr, DeluxePriceList, bp, DeluxePriceArr);
    }*/

    public static void onBillingInitialized(BillingClient bc) {
        myBC = bc;
        myBC.querySkuDetailsAsync(getParams(PremiumArr, BillingClient.SkuType.SUBS), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null && !list.isEmpty()) {

                    PremiumPriceList.clear();
                    PremiumSkuList = list;
                    Collections.sort(PremiumSkuList, new Comparator<SkuDetails>() {
                        @Override
                        public int compare(SkuDetails details, SkuDetails t1) {
                            return Long.compare(details.getPriceAmountMicros(), t1.getPriceAmountMicros());
                        }
                    });
                    Log.e(TAG, "onBillingInitialized: " + PremiumSkuList);
                    for (int i = 0; i < PremiumSkuList.size(); i++)
                        PremiumPriceList.add(new InAppPriceValue("$" + PremiumPriceArr[i] /*skuDetails.priceText*/, PremiumPriceArr[i] /*skuDetails.priceValue*/));
                }
            }
        });
        myBC.querySkuDetailsAsync(getParams(vipTokenArr, BillingClient.SkuType.INAPP), (billingResult, list) -> {

            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null && !list.isEmpty()) {
                String price;
                vipTokenPriceList.clear();
                vipTokenSkuList = list;
                Collections.sort(vipTokenSkuList, (details, t1) -> Long.compare(details.getPriceAmountMicros(), t1.getPriceAmountMicros()));
                for (int i = 0; i < vipTokenSkuList.size(); i++) {
                    price = vipTokenSkuList.get(i).getPrice();
                    price = price.substring(1);
                    vipTokenPriceList.add(new InAppPriceValue(vipTokenSkuList.get(i).getPrice() /*skuDetails.priceText*/, Double.parseDouble(price) /*skuDetails.priceValue*/));
                }
                Log.e(TAG, "onBillingInitialized: " + vipTokenPriceList);
            }
        });
        myBC.querySkuDetailsAsync(getParams(timeTokenArr, BillingClient.SkuType.INAPP), (billingResult, list) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null && !list.isEmpty()) {
                String price;
                timeTokenPriceList.clear();
                timeTokenSkuList = list;
                Collections.sort(timeTokenSkuList, (details, t1) -> Long.compare(details.getPriceAmountMicros(), t1.getPriceAmountMicros()));
                for (int i = 0; i < timeTokenSkuList.size(); i++) {
                    price = timeTokenSkuList.get(i).getPrice();
                    price = price.substring(1);
                    timeTokenPriceList.add(new InAppPriceValue(timeTokenSkuList.get(i).getPrice() /*skuDetails.priceText*/, Double.parseDouble(price) /*skuDetails.priceValue*/));
                }
            }
        });

        myBC.querySkuDetailsAsync(getParams(crushTokenArr, BillingClient.SkuType.INAPP), (billingResult, list) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null && !list.isEmpty()) {
                String price;
                crushTokenPriceList.clear();
                crushTokenSkuList = list;
                Collections.sort(crushTokenSkuList, (details, t1) -> Long.compare(details.getPriceAmountMicros(), t1.getPriceAmountMicros()));
                for (int i = 0; i < crushTokenSkuList.size(); i++) {
                    price = crushTokenSkuList.get(i).getPrice();
                    price = price.substring(1);
                    crushTokenPriceList.add(new InAppPriceValue(crushTokenSkuList.get(i).getPrice() /*skuDetails.priceText*/, Double.parseDouble(price) /*skuDetails.priceValue*/));
                }
            }
        });
    }
/*
    public static void setBilling(BillingProcessor bp) {
        myBp = bp;
    }

    public static void setMyBC(BillingClient myBC) {
        CommonDialogs.myBC = myBC;
    }*/

   /* public static void setSubcripData(String[] arr, List<InAppPriceValue> PriceList, BillingProcessor bp, Double[] pricearr) {
        PriceList.clear();
        //SkuDetails skuDetails;
        for (int i = 0; i < arr.length; i++) {
            String s = arr[i];
            SkuDetails skuDetails = bp.getSubscriptionListingDetails(s);
            PriceList.add(new InAppPriceValue("$" + pricearr[i] *//*skuDetails.priceText*//*, pricearr[i] *//*skuDetails.priceValue*//*));
        }
    }

    public static void setSubcripData(String[] arr, List<SkuDetails> PriceList, BillingClient bp, Double[] pricearr) {
        PriceList.clear();
        bp.queryPurchasesAsync();
        //SkuDetails skuDetails;
        *//*for (int i = 0; i < arr.length; i++) {
            String s = arr[i];
            SkuDetails skuDetails = bp.getSubscriptionListingDetails(s);
            PriceList.add(new InAppPriceValue("$" + pricearr[i] *//**//*skuDetails.priceText*//**//*, pricearr[i] *//**//*skuDetails.priceValue*//**//*));
        }*//*
    }

    public static void setPurchaseData(String[] TokenArr, List<InAppPriceValue> TokenPriceList, BillingProcessor bp) {
        TokenPriceList.clear();
        SkuDetails purchaseListingDetails;
        for (String s : TokenArr) {
            purchaseListingDetails = bp.getPurchaseListingDetails(s);
            if (purchaseListingDetails != null)
                TokenPriceList.add(new InAppPriceValue(purchaseListingDetails.priceText, purchaseListingDetails.priceValue));

        }
        Log.e(TAG, "setPurchaseData:\n " + TokenPriceList);
    }*/

    private static SkuDetailsParams getParams(String[] arr, String type) {
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(Arrays.asList(arr)).setType(type);
        return params.build();
    }
/*
    private static PremiumTokenCountModel checkExistingSubscriptionForPremium(BillingProcessor bp, Context ctx) {
        String productid = "";
        double price = 0.0;
        if (bp.loadOwnedPurchasesFromGoogle()) {
            if (bp.getSubscriptionTransactionDetails("premium_1") != null ||
                    bp.getSubscriptionTransactionDetails("premium_3") != null ||
                    bp.getSubscriptionTransactionDetails("premium_6") != null ||
                    bp.getSubscriptionTransactionDetails("premium_12") != null) {

                if (bp.getSubscriptionTransactionDetails("premium_1") != null) {
                    productid = "premium_1";
                    price = PremiumPriceList.get(0).getPriceValue();
                } else if (bp.getSubscriptionTransactionDetails("premium_3") != null) {
                    productid = "premium_3";
                    price = PremiumPriceList.get(1).getPriceValue();
                } else if (bp.getSubscriptionTransactionDetails("premium_6") != null) {
                    productid = "premium_6";
                    price = PremiumPriceList.get(2).getPriceValue();
                } else if (bp.getSubscriptionTransactionDetails("premium_12") != null) {
                    productid = "premium_12";
                    price = PremiumPriceList.get(3).getPriceValue();
                }
                subscriptiontype = "1";
                Date c = bp.getSubscriptionTransactionDetails(productid).purchaseInfo.purchaseData.purchaseTime;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
                String formattedDate = df.format(c);
                return new PremiumTokenCountModel(subscriptiontype, productid, price, Integer.parseInt(productid.split("_")[1]), bp.getSubscriptionTransactionDetails(productid).purchaseInfo.purchaseData.orderId, bp.getSubscriptionTransactionDetails(productid).purchaseInfo.purchaseData.purchaseToken, formattedDate, bp.getSubscriptionTransactionDetails(productid).purchaseInfo.signature, bp.getSubscriptionTransactionDetails(productid).purchaseInfo.purchaseData.purchaseState.toString());
            } else {
                new AlertDialog.Builder(ctx).setTitle("Nothing to restore").setMessage("No previous purchases were found").setPositiveButton("ok", null).show();
                return null;
            }
        } else {
            new AlertDialog.Builder(ctx).setTitle("Nothing to restore").setMessage("No previous purchases were found").setPositiveButton("ok", null).show();
            return null;
        }
    }

    private static PremiumTokenCountModel checkExistingSubscriptionForDeluxe(BillingProcessor bp, Context ctx) {
        String productid = "";
        double price = 0.0;
        if (bp.loadOwnedPurchasesFromGoogle()) {
            if (bp.getSubscriptionTransactionDetails("deluxe_1") != null ||
                    bp.getSubscriptionTransactionDetails("deluxe_3") != null ||
                    bp.getSubscriptionTransactionDetails("deluxe_6") != null ||
                    bp.getSubscriptionTransactionDetails("deluxe_12") != null) {

                if (bp.getSubscriptionTransactionDetails("deluxe_1") != null) {
                    productid = "deluxe_1";
                    price = DeluxePriceList.get(0).getPriceValue();
                } else if (bp.getSubscriptionTransactionDetails("deluxe_3") != null) {
                    productid = "deluxe_3";
                    price = DeluxePriceList.get(1).getPriceValue();
                } else if (bp.getSubscriptionTransactionDetails("deluxe_6") != null) {
                    productid = "deluxe_6";
                    price = DeluxePriceList.get(2).getPriceValue();
                } else if (bp.getSubscriptionTransactionDetails("deluxe_12") != null) {
                    productid = "deluxe_12";
                    price = DeluxePriceList.get(3).getPriceValue();
                }
                subscriptiontype = "2";
                Date c = bp.getSubscriptionTransactionDetails(productid).purchaseInfo.purchaseData.purchaseTime;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
                String formattedDate = df.format(c);
                Log.e(TAG, "checkExistingSubscriptionForDeluxe: " + bp.getSubscriptionTransactionDetails(productid).purchaseInfo.toString());
                return new PremiumTokenCountModel(subscriptiontype, productid, price, Integer.parseInt(productid.split("_")[1]), bp.getSubscriptionTransactionDetails(productid).purchaseInfo.purchaseData.orderId, bp.getSubscriptionTransactionDetails(productid).purchaseInfo.purchaseData.purchaseToken, formattedDate, bp.getSubscriptionTransactionDetails(productid).purchaseInfo.signature, bp.getSubscriptionTransactionDetails(productid).purchaseInfo.purchaseData.purchaseState.toString());
            } else {
                new AlertDialog.Builder(ctx).setTitle("Nothing to restore").setMessage("No previous purchases were found").setPositiveButton("ok", null).show();
                return null;
            }
        } else {
            new AlertDialog.Builder(ctx).setTitle("Nothing to restore").setMessage("No previous purchases were found").setPositiveButton("ok", null).show();
            return null;
        }
    }*/

    /*
     *** Method to show dialog with 1 button
     */
    public static void alertDialogOneButton(Context mContext, String message) {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_one_button);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView tv_message = dialog.findViewById(R.id.tv_message);
        TextView tv_ok = dialog.findViewById(R.id.tv_ok);
        tv_message.setText(message);
        tv_ok.setOnClickListener(view -> dialog.dismiss());
    }

    public void setOnDeluxeContinuebtn(onPurchaseDeluxe onPurchaseDeluxe) {
        this.onPurchaseDeluxe = onPurchaseDeluxe;
    }


    public interface onClick {
        void callDefaultDialog(int pos);
    }


    public interface onPurchaseDeluxe {
        void OnClickContinue();
    }

    public interface onPurchaseRestore {
        void onSucces();

        void onError(String msg);
    }

    public interface onProductConsume {
        void onClickToken(String tokenType, int tokensNum, int selectedPos);
    }


}
