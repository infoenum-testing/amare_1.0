package com.swiftdating.app.ui.homeScreen;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.swiftdating.app.R;
import com.swiftdating.app.common.Global;
import com.swiftdating.app.data.network.Resource;
import com.swiftdating.app.data.preference.SharedPreference;
import com.swiftdating.app.model.BaseModel;
import com.swiftdating.app.model.NotificationModel;
import com.swiftdating.app.model.responsemodel.User;
import com.swiftdating.app.ui.base.BaseActivity;
import com.swiftdating.app.ui.homeScreen.fragment.FindMatchFragment;
import com.swiftdating.app.ui.homeScreen.fragment.LikesFrament;
import com.swiftdating.app.ui.homeScreen.fragment.MatchFragment;
import com.swiftdating.app.ui.homeScreen.fragment.MyProfileFragment;
import com.swiftdating.app.ui.homeScreen.fragment.SearchFragment;
import com.swiftdating.app.ui.homeScreen.viewmodel.HomeViewModel;
import com.swiftdating.app.ui.itsAMatchScreen.ItsAMatchActivity;

public class HomeActivity extends BaseActivity implements TabLayout.BaseOnTabSelectedListener {
    private static final String TAG = "HomeActivity";
    public TabLayout tabHome;
    public Toolbar mToolbar;
    public TextView mTitle, badge_notification_1;
    public ImageView ivLogo;
    public Integer type = 0;
    public Fragment fragment = null;
    public List<User> cardList = new ArrayList<>();
    public int swipeCount;
    public String timer = "";
    public isDeluxePopOpen isDeluxePopOpen;
    int tabPos = 0;
    public List<User> LikeUserlist = new ArrayList<>();
    public List<User> DisLikeUserlist = new ArrayList<>();
    HomeViewModel homeViewModel;
    boolean isShowDirect = false;
    boolean isShowSecondChance = false;
    private boolean isBack;

    public void setIsDeluxePopOpen(HomeActivity.isDeluxePopOpen isDeluxePopOpen) {
        this.isDeluxePopOpen = isDeluxePopOpen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("replace")) {
            isShowDirect = MatchFragment.isShowDirect;
            isShowSecondChance = LikesFrament.showSencondChance;
            tabPos = getIntent().getExtras().getInt("replace");
        }
        setToolbar();
        onNewIntent(getIntent());
        sp = new SharedPreference(this);
        // sp.setDislikeApi(true);
        init(tabPos);
        //Daily Popup Code
       /* if (!sp.getPremium() && !sp.getDeluxe() && sp.getVerified().equalsIgnoreCase("Yes")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String today = simpleDateFormat.format(new Date());
            String diff = sp.getDate();
            if (diff == null) {
                sp.saveDate(today);
                new AlertDialog.Builder(this).setCancelable(false).setTitle(R.string.daily_popup_title).setMessage(*//*"Have you tried BlackGentry Deluxe?"*//*R.string.daily_popup_msg).setPositiveButton("Get Deluxe", (dialog, which) -> {
                    if (isDeluxePopOpen != null) isDeluxePopOpen.openDeluxe();
                }).setNegativeButton("Not now", null).show();
            } else if (CommonUtils.getCountOfDays(today, diff) > 0) {
                Log.d("find", "onCreate: " + CommonUtils.getCountOfDays(today, diff));
                sp.saveDate(today);
                new AlertDialog.Builder(this).setCancelable(false).setTitle(R.string.daily_popup_title).setMessage(*//*"Have you tried BlackGentry Deluxe?"*//*R.string.daily_popup_msg).setPositiveButton("Get Deluxe", (dialog, which) -> {
                    //new AlertDialog.Builder(this).setTitle().setMessage("Have you tried BlackGentry Deluxe?").setPositiveButton("Get Deluxe", (dialog, which) -> {
                    if (isDeluxePopOpen != null) isDeluxePopOpen.openDeluxe();
                }).setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
            }
        }*/
    }

    /**
     * **  Method to Handle api Response
     */
    private void subscribeModel() {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        homeViewModel.unreadResponse().observe(this, (Observer<? super Resource<BaseModel>>) new Observer<Resource<BaseModel>>() {
            @Override
            public void onChanged(@Nullable Resource<BaseModel> resource) {
                if (resource == null) {
                    return;
                }
                switch (resource.status) {
                    case LOADING:
                        break;
                    case SUCCESS:
                        hideLoading();
                        if (resource.data.getSuccess()) {
                            if (resource.data.getError() != null && resource.data.getError().getCode().equalsIgnoreCase("401")) {
                                openActivityOnTokenExpire();
                            } else {
                                if (resource.data.getSuccess()) {
                                    Log.e("check", resource.data.getUnreadMessages() + "");
                                    if (!resource.data.getUnreadMessages()) {
                                        badge_notification_1.setVisibility(View.GONE);
                                    } else {
                                        badge_notification_1.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        } else if (resource.code == 401) {
                            openActivityOnTokenExpire();
                        } else {
                            badge_notification_1.setVisibility(View.GONE);
                        }
                        break;
                    case ERROR:
                        hideLoading();
                        badge_notification_1.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    /**
     * **  Method to Set Toolbar
     */
    private void setToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        mTitle = findViewById(R.id.tv_title);
        ivLogo = findViewById(R.id.ivLogo);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
    }

    /**
     * **  Method to Initialize View
     */
    private void init(Integer tabPos) {
        tabHome = findViewById(R.id.tabHome);
        badge_notification_1 = findViewById(R.id.badge_notification_1);
        tabHome.addOnTabSelectedListener(this);
        setupTabIcons(false, tabPos);
        if (isShowDirect) {
            MatchFragment.isShowDirect = true;
        }
        if (isShowSecondChance) {
            LikesFrament.showSencondChance = true;
        }
    }

    /**
     * **  Method to setup TabIcon on Bottom Bar
     */
    private void setupTabIcons(boolean show, Integer tabPos) {
        setCustomLayout(R.drawable.card_selector);
        setCustomLayout(R.drawable.likes_selector);//66-66
        setCustomLayout(R.drawable.search_selector);///63-63
        setCustomLayout(R.drawable.match_selector);//6363
        setCustomLayout(R.drawable.profile_selector);//6363
        badge_notification_1.setVisibility(!show ? View.GONE : View.VISIBLE);
        tabHome.getTabAt(tabPos).select();
    }

    /**
     * **  Method to Replace Tab Icon
     */
    public void replaceTabIcon(int pos) {
        tabHome.removeAllTabs();
        tabPos = pos;
        setCustomLayout(R.drawable.card_selector);
        setCustomLayout(R.drawable.likes_selector);
        setCustomLayout(R.drawable.search_selector);
        setCustomLayout(R.drawable.match_selector);
        setCustomLayout(R.drawable.profile_selector);
        tabHome.getTabAt(pos).select();

    }

    public void replaceLikeTabIcon(int pos) {
        tabPos = pos;
        tabHome.getTabAt(pos).select();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("MyData"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    protected void onResume() {
        mActivity=this;
        isHomeScreen = true;
        if (getIntent().hasExtra("type")) {
            type = getIntent().getExtras().getInt("type");
        }
        subscribeModel();
        ///"isVerified" ke check ko replace kr do "status" se
        //if (sp.getVerified().equals("Yes")) {
        if (sp.getStatus().equalsIgnoreCase(Global.statusActive)) {
            homeViewModel.unreadRequest(sp.getToken());
        }
        super.onResume();

    }

    @Override
    protected void onPause() {
        isHomeScreen = false;
        super.onPause();

    }

    /**
     * method to set custom view for tabs items
     */
    private void setCustomLayout(int tab_selector) {
        ConstraintLayout customTab = (ConstraintLayout) LayoutInflater.from(mActivity)
                .inflate(R.layout.custom_item_tab_home, null);
        ImageView iv_icon = customTab.findViewById(R.id.iv_icon);
        iv_icon.setImageResource(tab_selector);

        tabHome.addTab(tabHome.newTab().setCustomView(customTab));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Bundle extra = intent.getExtras();
        super.onNewIntent(intent);
        if (extra != null) {
            if (extra.containsKey("approved")) {
                //sp.saveVerified("Yes");
                sp.saveIsRejected(false);
            } else if (extra.containsKey("rejected")) {
                //   sp.saveVerified("No");
                sp.saveIsRejected(true);
            } else if (extra.containsKey("deactivated")) {
                openActivityOnTokenExpire();
            } else if (extra.containsKey("match") && extra.getSerializable("match") != null) {
                NotificationModel match = (NotificationModel) extra.getSerializable("match");
                if (extra.containsKey("isAppForeground")) {
                    if (match != null && match.getMatch() != null) {
                        startActivityForResult(new Intent(this, ItsAMatchActivity.class).putExtra("match", match), 100);
                    }
                } else {
                    tabPos = 3;
                }
            }
        }
    }

    /**
     * set fragment according to the tab selected
     *
     * @param position tab selected position
     */
    public void setCurrentTabFragment(int position) {
        if (position == 0) {
            fragment = new FindMatchFragment();
            tabPos = 0;
            if (cardList.size() == 0 || sp.isSettingsChanged()) {
                cardList = new ArrayList<>();
            } else {
                for (int i = 0; i < swipeCount; i++) {
                    if (cardList.size() > 0)
                        cardList.remove(0);
                }
            }
            replaceFragment(fragment, getSupportFragmentManager());
            previousSelectedPos = 0;
        } else if (position == 1) {
            fragment = new LikesFrament();
            tabPos = 1;
            replaceFragment(fragment, getSupportFragmentManager());
            previousSelectedPos = 1;
        } else if (position == 2) {
            fragment = new SearchFragment();
            tabPos = 2;
            replaceFragment(fragment, getSupportFragmentManager());
            previousSelectedPos = 2;
        } else if (position == 3) {
            fragment = new MatchFragment();
            tabPos = 3;
            replaceFragmentWithBackStack(fragment, getSupportFragmentManager());
            previousSelectedPos = 3;
        } else if (position == 4) {
            fragment = new MyProfileFragment();
            tabPos = 4;
            replaceFragmentWithBackStack(fragment, getSupportFragmentManager());
            previousSelectedPos = 4;
        }
    }

    /**
     * **  Method to set text of toolbar
     */
    public void setToolbarWithTitle(String title) {
        if (title.equalsIgnoreCase("BlackGentry")) {
            mTitle.setVisibility(View.GONE);
            ivLogo.setVisibility(View.VISIBLE);
        } else {
            mTitle.setVisibility(View.VISIBLE);
            ivLogo.setVisibility(View.GONE);
        }

        if (title.equalsIgnoreCase("BlackGentry") || title.equalsIgnoreCase("My Profile")
                || title.equalsIgnoreCase("Matches")) {
            enableBack(false);
        } else {
            enableBack(true);
        }

        mTitle.setText(title);

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        setCurrentTabFragment(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    /**
     * **  Method to Enable Back
     */
    private void enableBack(boolean isBack) {
        this.isBack = isBack;
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(isBack);
        getSupportActionBar().setDisplayShowHomeEnabled(isBack);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(isBack ? R.drawable.backward_ic : 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (isBack) {
                onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 100) {
            replaceTabIcon(3);
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragments);
            if (fragment != null)
                fragment.onActivityResult(requestCode, resultCode, intent);
            if (resultCode == RESULT_OK) {
                if (intent != null && intent.hasExtra("tabPos")) {
                    replaceTabIcon(3);
                }
            }
        }
    }

    public interface isDeluxePopOpen {
        void openDeluxe();
    }
}
