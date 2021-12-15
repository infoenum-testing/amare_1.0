package com.swiftdating.app.ui.homeScreen.fragment

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import com.android.billingclient.api.*
import com.bumptech.glide.Glide
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.swiftdating.app.R
import com.swiftdating.app.callbacks.ReportInterface
import com.swiftdating.app.common.*
import com.swiftdating.app.common.AppConstants.PERMISSION_REQUEST_CODE_LOC
import com.swiftdating.app.data.network.*
import com.swiftdating.app.data.preference.SharedPreference
import com.swiftdating.app.model.BaseModel
import com.swiftdating.app.model.requestmodel.*
import com.swiftdating.app.model.responsemodel.*
import com.swiftdating.app.ui.base.BaseActivity
import com.swiftdating.app.ui.base.BaseFragment
import com.swiftdating.app.ui.chatScreen.ChatWindow
import com.swiftdating.app.ui.homeScreen.HomeActivity
import com.swiftdating.app.ui.homeScreen.adapter.CardDetailAdapter
import com.swiftdating.app.ui.homeScreen.viewmodel.HomeViewModel
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import com.yuyakaido.android.cardstackview.*
import kotlinx.android.synthetic.main.activity_chat_window.*
import kotlinx.android.synthetic.main.fragment_find_match.*
import kotlinx.android.synthetic.main.native_ads_view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*
import kotlin.collections.HashMap

@Suppress("CAST_NEVER_SUCCEEDS")
class FindMatchFragment : BaseFragment(), CardStackListener,
        ReportInterface, View.OnClickListener, CommonDialogs.onProductConsume,
        BaseActivity.MyProfileResponse, ApiCallback.FilterCallBack, BaseActivity.OnPurchaseListener {

    override fun getLayoutId(): Int {
        return R.layout.fragment_find_match
    }

    private var handleDirection: Direction = Direction.Bottom
    lateinit var homeViewModel: HomeViewModel
    private val cardStackView by lazy { view?.findViewById<CardStackView>(R.id.card_stack_view) }
    private val img_vip_star by lazy { view?.findViewById<ImageView>(R.id.img_vip_star) }
    private val cl_NoPermission by lazy { view?.findViewById<RelativeLayout>(R.id.cl_NoPermission) }
    private val chat by lazy { view?.findViewById<ImageView>(R.id.chat) }
    private val like by lazy { view?.findViewById<ImageView>(R.id.love) }
    private val superlike2 by lazy { view?.findViewById<ImageView>(R.id.superlike2) }
    private val rlFilter by lazy { view?.findViewById<RelativeLayout>(R.id.rlFilter) }
    private val tv_likeCount by lazy { view?.findViewById<TextView>(R.id.tv_likeCount) }

    //   val img_vip_star = view.findViewById(R.id.img_vip_star) as ImageView
    private val manager by lazy { CardStackLayoutManager(context, this) }
    private var locationCheckCount = 0
    private var gotLocation: Boolean = false
    private var lat = ""
    private var lng = ""

    /* private var bp: BillingProcessor? = null*/
    private var cardId = -1
    private var selectedPosition = -1
    lateinit var list: List<User>
    var cardSwipeCount = 0
    var shakeListener: ShakeListener? = null
    private lateinit var mAdView: NativeAdView
    private var isSecond: Boolean = false
    lateinit var adLoader: AdLoader
    lateinit var btn_open_loc_setting: Button
    private var isLiked: Boolean = false
    private var isMySwiped: Boolean = false
    private var timer: String = ""
    private var tokenSType: String = ""
    private var productId: String = ""
    private var purchaseType: Int = 0

    lateinit var imageView: ConstraintLayout
    lateinit var clView: LinearLayout
    private var pos = -1
    var isExpired = false
    var price = 0.0
    private lateinit var contextMy: Context
    var filterRequest: FilterRequest? = null
    override fun onResume() {
        super.onResume()
        BaseActivity.mActivity = this.activity
        baseActivity.isCardScreen = true
        baseActivity.sp.saveFirstTime("true")
        (activity as HomeActivity).swipeCount = 0
        //(activity as HomeActivity?)!!.setToolbarWithTitle("Blackgentry")
        (activity as HomeActivity?)!!.mToolbar.visibility = GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        contextMy = this.context!!
        if (baseActivity.isNetworkConnected) {
            if (baseActivity.sp.isSettingsChanged || (activity as HomeActivity).cardList.isEmpty()) {
                list = (activity as HomeActivity).cardList
                checkLocationPermission()
            } else {
                list = (activity as HomeActivity).cardList
                setupCardStackView()
            }
        } else {
            baseActivity.showSnackbar(view, "Please connect to internet")
        }
    }

    /**
     * Method to initialize
     */
    private fun init(view: View) {
        if (baseActivity.sp.filterModel != null) {
            filterRequest = baseActivity.sp.filterModel
        }

        /* (activity as HomeActivity?)!!.setIsDeluxePopOpen {
               CommonDialogs.DeluxePurChaseDialog(mActivity, this)
         }*/

        baseActivity.getMyProfile(this);
        /*  val rectUser = baseActivity.sp.noOfLikes

          if (rectUser != null) {
              val countLike=100//rectUser.users;
              Log.e(TAG, "init: $countLike")
              if (countLike > 99) {
                  tv_likeCount?.text = "99+"
              } else
                  tv_likeCount?.text = "" +countLike
          } else {
              tv_likeCount?.text = "0"
          }*/
        setProfileData()
        subscribeModel()
        initBillingProcess()
        btn_open_loc_setting = view.findViewById(R.id.btn_open_loc_setting)
        btn_open_loc_setting.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", activity?.packageName, null)
            intent.data = uri
            startActivityForResult(intent, 4040)
        }
        clView = view.findViewById(R.id.clView)
        baseActivity.sp.saveisSettingsChanged(false)
        shakeListener = ShakeListener(context)
        //shakeListener!!.setOnShakeListener(this)
        MobileAds.initialize(context) {}
        mAdView = view.findViewById(R.id.adView)
        tvNoMatch.visibility = GONE
        // constraint_verify.visibility = GONE
        img_vip_star?.isEnabled = true
        chat?.isEnabled = true
        rlFilter?.isEnabled = true
        superlike2?.visibility = GONE
        love.visibility = VISIBLE
        cancel.isEnabled = false
        superlike.isEnabled = false
        love.isEnabled = false
        setupButton()
        // set on-click listener
        img_vip_star?.setOnClickListener {
            val user = baseActivity.sp.user
            val obj: ProfileOfUser = Gson().fromJson(user, ProfileOfUser::class.java)
            if (obj.vipToken > 0) {
                callAlertForVipConsume();
            } else {
                callAlertForVipTokenWithNoAction(false);
                //CommonDialogs.VIPPurChaseDialog(mActivity, this)
            }
        }

        val relativeLiked = view.findViewById(R.id.rv_who_liked) as RelativeLayout
        relativeLiked.setOnClickListener {
            // if (baseActivity.sp.deluxe) {
            (mContext as HomeActivity).replaceLikeTabIcon(1)
            //(mContext as HomeActivity).setCurrentTabFragment(1)
            /*} else {
                CommonDialogs.DeluxePurChaseDialog(mActivity, this)
            }*/
        }



        imageView = view.findViewById(R.id.imageView)
        // Glide.with(this).load(R.raw.bg).into(imageView)
    }

    private fun callAlertForVipConsume() {
        val dialog = Dialog(mActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        Objects.requireNonNull(dialog.window)!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_two_button)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        val window = dialog.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()
        val tv_message = dialog.findViewById<TextView>(R.id.tv_message)
        val tv_yes = dialog.findViewById<TextView>(R.id.tv_yes)
        val tv_no = dialog.findViewById<TextView>(R.id.tv_no)
        tv_message.text = getString(R.string.vip_msg_one)
        tv_yes.setOnClickListener { v: View? ->
            baseActivity.showLoading()
            homeViewModel.applyVipToken(ApplyVipTokenRequest(30, 1))
            dialog.dismiss()
        }
        tv_no.setOnClickListener { view: View? -> dialog.dismiss() }
    }

    private var max: Int = 0
    private var mini: Int = 0
    private fun showDistanceBottomsheet() {
        val ageArrayList: ArrayList<String> = arrayListOf()
        var interseted = "Both"
        if (filterRequest == null) {
            filterRequest = FilterRequest()
            filterRequest!!.minAgePrefer = 21
            filterRequest!!.maxAgePrefer = 40
        }
        val bottomSheetDialog: BottomSheetDialog?
        bottomSheetDialog = BottomSheetDialog(contextMy, R.style.BottomSheetDialogStyle)

        val view = LayoutInflater.from(context).inflate(R.layout.filter_sheet, null)
        bottomSheetDialog.setContentView(view)
        val tvDistance = view.findViewById<TextView>(R.id.tvDistance)
        val btnApply = view.findViewById<Button>(R.id.btnApply)
        val btnReset = view.findViewById<Button>(R.id.btnReset)
        val tvAgeRange = view.findViewById<TextView>(R.id.tvAgeRange)
        val tgGender = view.findViewById<RadioGroup>(R.id.tgGender)
        val otherTb = view.findViewById<RadioButton>(R.id.otherTb)
        val tbMale = view.findViewById<RadioButton>(R.id.tbMale)
        val femaleTb = view.findViewById<RadioButton>(R.id.femaleTb)
        val seekDistance: IndicatorSeekBar = view.findViewById(R.id.seek_distance)
        val seekAgeRange: RangeSeekBar<Int> = view.findViewById(R.id.seek_age_range)
        val ivClose = view.findViewById<ImageView>(R.id.ivClose)


        ivClose.setOnClickListener(OnClickListener(fun(v: View?) {
            bottomSheetDialog.cancel()
        }))
        for (i in 18..80) {
            ageArrayList.add("" + i)
        }
        tgGender.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener(fun(radioGroup: RadioGroup, i: Int) {
            when (i) {
                R.id.tbMale -> {
                    filterRequest!!.interested = "Men"
                    interseted = "Men"
                }
                R.id.femaleTb -> {
                    filterRequest!!.interested = "Women"
                    interseted = "Women"
                }
                R.id.otherTb -> {
                    filterRequest!!.interested = "Both"
                    interseted = "Both"
                }
            }
        }))
        val size = ageArrayList.size - 1
        seekAgeRange.setRangeValues(0 as Int?, size as Int?)
        seekAgeRange.selectedMinValue = 0
        seekAgeRange.selectedMaxValue = size

        seekAgeRange.setOnRangeSeekBarChangeListener(fun(bar: RangeSeekBar<out Number>, minValue: Number, maxValue: Number) {
            if (maxValue.toInt() - minValue.toInt() < 6) {
                if (max - minValue.toInt() < 6) {
                    seekAgeRange.setSelectedMinValue(mini)
                } else seekAgeRange.setSelectedMaxValue(max)
                return
            }
            max = maxValue.toInt()
            mini = minValue.toInt()
            tvAgeRange.text = ageArrayList[minValue as Int] + " to " + ageArrayList[maxValue as Int]
            filterRequest!!.maxAgePrefer = ageArrayList[maxValue.toInt()].toInt()
            filterRequest!!.minAgePrefer = ageArrayList[minValue.toInt()].toInt()
        })

        seekDistance.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams) {
                tvDistance.text = seekParams.progress.toString() + " " + getString(R.string.miles)
                filterRequest!!.distance = seekDistance.progress
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {

            }
        }

/////////////////////////////////        setting data to view from shared pref model ////////////////////////

        if (filterRequest != null && !TextUtils.isEmpty(filterRequest!!.interested)) {
            when (filterRequest!!.interested) {
                "Men" -> {
                    tbMale.isChecked = true
                    femaleTb.isChecked = false
                    otherTb.isChecked = false
                    interseted = "Men"
                }
                "Women" -> {
                    tbMale.isChecked = false
                    femaleTb.isChecked = true
                    otherTb.isChecked = false
                    interseted = "Women"
                }
                "Both" -> {
                    tbMale.isChecked = false
                    femaleTb.isChecked = false
                    otherTb.isChecked = true
                    interseted = "Both"
                }
            }
        }
//        if (filterRequest != null) {
        for (i in ageArrayList.indices) {
            if (ageArrayList[i].equals("" + filterRequest!!.minAgePrefer, ignoreCase = true)) {
                seekAgeRange.selectedMinValue = i
                break
            }
        }
        for (i in ageArrayList.indices) {
            if (ageArrayList[i].equals("" + filterRequest!!.maxAgePrefer, ignoreCase = true)) {
                seekAgeRange.selectedMaxValue = i
                break
            }
        }
        tvAgeRange.text = ageArrayList[seekAgeRange.selectedMinValue as Int] + " to " + ageArrayList[seekAgeRange.selectedMaxValue as Int]
//        }
        if (filterRequest != null) {
            seekDistance.setProgress(filterRequest!!.distance.toFloat())
            tvDistance.text = "" + filterRequest!!.distance + " miles"
        } else {
            seekDistance.setProgress(500f)
        }
        /////////////////////////////////   ^^^^^^^^^^^ setting data to view from shared pref model ////////////////////////
        btnApply.setOnClickListener(OnClickListener(fun(v: View?) {
            baseActivity.sp.saveFilterModel(filterRequest)
            val map = HashMap<String, Any>()
            map["distance"] = seekDistance.progress
            map["minAgePrefer"] = ageArrayList[seekAgeRange.selectedMinValue].toInt()
            map["maxAgePrefer"] = ageArrayList[seekAgeRange.selectedMaxValue].toInt()
            map["interested"] = interseted
            // baseActivity.showLoading()
            imageView.visibility = VISIBLE
            ApiCall.setFilters(baseActivity.sp.token, map, this)
            bottomSheetDialog.cancel()
        }))

        btnReset.setOnClickListener(OnClickListener {
            filterRequest?.distance = 500
            filterRequest?.maxAgePrefer = 40
            filterRequest?.minAgePrefer = 21
            filterRequest?.gender = "Both"
            baseActivity.sp.saveFilterModel(filterRequest)
            seekAgeRange.selectedMinValue = 3
            seekAgeRange.selectedMaxValue = 22
//            tvAgeRange.text = "21 to 40"
            seekDistance.setProgress(500f)
            tvDistance.text = "500 miles"
            otherTb.isChecked = true
            tbMale.isChecked = false
            femaleTb.isChecked = false
        })
        bottomSheetDialog.show()
    }


    private fun callAlertForVipTokenWithNoAction(afterConsume: Boolean) {
        val dialog = Dialog(mActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        Objects.requireNonNull(dialog.window)!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_two_button)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        val window = dialog.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()
        val tv_message = dialog.findViewById<TextView>(R.id.tv_message)
        val tv_yes = dialog.findViewById<TextView>(R.id.tv_yes)
        val tv_no = dialog.findViewById<TextView>(R.id.tv_no)
        tv_no.visibility = GONE
        if (afterConsume) {
            tv_message.text = getString(R.string.vip_msg_activated)
        } else {
            tv_message.text = getString(R.string.vip_msg_two)
        }
        tv_yes.text = getString(R.string.ok_text)
        tv_yes.setOnClickListener { v: View? ->
            dialog.dismiss()
        }
    }


    /**
     * Method to initialize Billing
     */
    private fun initBillingProcess() {
        if (fragClient != null && fragClient.isReady) {
            Log.e(TAG, "initBillingProcess: Fragment client")
            if (baseActivity != null && baseActivity.sp != null) {
                if (BaseActivity.isPaymentLoadShow)
                    baseActivity.showLoading()
                homeViewModel.getSubscriptionRequest(baseActivity.sp.token)
            }
        }
        /*bp = BillingProcessor(context as Activity, LICENSE_KEY, this)
        bp!!.initialize()*/

    }


    private fun callApiSub(status: String, productId: String, bool: Boolean) {
        baseActivity.sp.savePremium(bool)
        homeViewModel.changePremiumRequest(PremiumStatusChange(productId, status, 1))
    }

    var subscriptiontype = ""


    private fun callApi(mPremiumTokenCountModel: PremiumTokenCountModel, spr: SharedPreference) {
        Log.e(TAG, "callApi: $mPremiumTokenCountModel")
        val api = CallServer.get().apiName
        val github = Retrofit.Builder()
                .baseUrl(CallServer.BASE_URL)
                .build()
                .create(ApiUtils::class.java)
        val call = api.checkSubscription(baseActivity.sp.token, mPremiumTokenCountModel)
        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    val gson = Gson()
                    if (response.isSuccessful) {
                        Log.e("check1", response.body().toString())
                        val responseBean: BaseModel = gson.fromJson(response.body()!!.string(), BaseModel::class.java)
                        if (responseBean.isavaiable) {
                            spr.savePremium(false)
                        } else {
                            spr.savePremium(true)
                        }
                    } else {
                        Log.e("check1", response.errorBody().toString())
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable?) {
                Log.e("check1", t.toString())
            }
        })
    }

    /**
     * Method to initialize view model and handle response
     */
    private fun subscribeModel() {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        homeViewModel.userListResponse().observe(viewLifecycleOwner, Observer<Resource<UserListResponseModel>> { resource ->
            if (resource == null) {
                return@Observer
            }
            when (resource.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    if (resource.data!!.success!!) {
                        baseActivity.sp.saveisSettingsChanged(false)
                        if (resource.data.error != null && resource.data.error.code == "401") {
                            baseActivity.hideLoading()
                            baseActivity.openActivityOnTokenExpire()
                        }/* else if (resource.data.message == "SELFIE NOT VERIFIED YET !") {
                            if (baseActivity != null && !baseActivity.sp.isRejected) {
                                constraint_verify.visibility = VISIBLE
                                img_vip_star?.isEnabled = false
                                rewind?.isEnabled = false
                                chat?.isEnabled = false
                                rlFilter?.isEnabled = false
                                imageView.visibility = GONE
                            }
                            SharedPreference(context).saveSelfieVerificationStatus("No")
                        }*/
                        else if (resource.data.error != null && resource.data.error.code == "404") {
                             baseActivity.hideLoading()
                            img_vip_star?.isEnabled = false
                            chat?.isEnabled = false
                            rlFilter?.isEnabled = false
                            tvNoMatch.visibility = VISIBLE
                            btn_settings.visibility = VISIBLE
                            clView.visibility = GONE
                            imageView.visibility = GONE
                        } else {
                            list = resource.data.users
                            setupCardStackView()
                            // constraint_verify.visibility = GONE
                            img_vip_star?.isEnabled = true
                            chat?.isEnabled = true
                            rlFilter?.isEnabled = true
                            rewind?.isEnabled = true
                            cardStackView!!.visibility = VISIBLE
                            // baseActivity.hideLoading()
                            //SharedPreference(context).saveSelfieVerificationStatus("Yes")
                        }
                    } else {
                           baseActivity.hideLoading()
                        baseActivity.sp.saveisSettingsChanged(false)
                        img_vip_star?.isEnabled = false
                        chat?.isEnabled = false
                        rlFilter?.isEnabled = false
                        tvNoMatch.visibility = VISIBLE
                        btn_settings.visibility = VISIBLE
                        clView.visibility = GONE
                        imageView.visibility = GONE
                        cardStackView!!.visibility = GONE
                    }
                }
                Status.ERROR -> {
                    baseActivity.hideLoading()
                    baseActivity.sp.saveisSettingsChanged(false)
                    baseActivity.showSnackbar(card_stack_view, resource.message)
                }
            }
        })

        homeViewModel.userReactResponse().observe(viewLifecycleOwner, Observer<Resource<ReactResponseModel>> { resource ->
            if (resource == null) {
                return@Observer
            }
            when (resource.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    baseActivity.hideLoading()
                    if (resource.data!!.success!!) {
                        if (resource.data.react.reaction.contains("dislike")) {
                            baseActivity.sp.dislikeApi = true
                        }
                        (activity as HomeActivity).swipeCount++
                        if (resource.data.key != null && resource.data.key == "Second") {
                            rewind.isEnabled = false
                            isSecond = true
                        } else {
                            isSecond = false
                        }
                        /*if (removeItemFromList) {
                            (activity as HomeActivity).swipeCount--
                            removeItemFromList = false
                            list = list.drop(1)
                            cardStackView?.adapter = CardDetailAdapter(context, list, myHt, this)
                            cardStackView?.adapter!!.notifyDataSetChanged()
                            isSwipedCalled = true
                            if (list.isEmpty())
                                onCardDis()
                        } else*/
                        cardStackView?.swipe()
                        baseActivity.sp.isDialogOpen = false
                        cardSwipeCount--
                        baseActivity.sp.saveSwipeCount(baseActivity.sp.swipeCount + 1)
                        val count = baseActivity.sp.swipeCount
                        Log.e("count", "count$count")
                        if (cardSwipeCount == 0) {
                            btn_settings.isEnabled = true
                        }
                        if (baseActivity.sp.swipeCount % 18 == 0 && !baseActivity.sp.premium) {
                            showAd()
                        }
                    } else if (resource.data.error != null && resource.data.error.code.contains("401")) {
                        baseActivity.openActivityOnTokenExpire()
                    } else {
                        if (resource.data.message == "Your super likes tokens are finished.") {
                            baseActivity.sp.isDialogOpen = true
                            CommonDialogs.CrushPurChaseDialog(context, this)
                            /*CommonDialogs.purchaseDialog(context, "Crush Tokens", "You have run out of crush tokens. " +"Purchase more tokens below.", this)*/
                        } else if (resource.data.message.contains("SWIPECOUNTERS REACHED TO 100", ignoreCase = true)) {
                            isSwipedCalled = true
                          //  CommonDialogs.CrushPurChaseDialog(context, this)
                         //   CommonDialogs.PremuimPurChaseDialog(context, this)
                            /* if (resource.data.swipesData != null && resource.data.swipesData.updatedAt != null)
                             {
                                 val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                 formatter.timeZone = TimeZone.getTimeZone("UTC")
                                 val result = formatter.parse(resource.data.swipesData.updatedAt.replace("T", " ").split("\\.").toTypedArray()[0])
                                 val calendar = Calendar.getInstance()
                                 calendar.time = result
                                 calendar.add(Calendar.HOUR_OF_DAY, 12)
                                 Log.e("Time here", calendar.time.toString())
                                 var hours = calendar.time.hours
                                 val min = calendar.time.minutes
                                 var am = "AM"
                                 if (hours >= 12) {
                                     hours -= 12
                                     am = "PM"
                                 }
                                 if (hours == 0) {
                                     hours = 12
                                     am = "AM"
                                 }
                                 timer = if (min < 10) {
                                     "$hours:0$min $am"
                                 } else {
                                     "$hours:$min $am"
                                 }
                                 baseActivity.sp.isDialogOpen = true
                                 if (!TextUtils.isEmpty(timer)) {
                                     //CommonDialogs.PremuimPurChaseDialog(context, this)
                                     CommonDialogs.DeluxePurChaseDialogWithMessage(context, this, "You have reached the likes limit. You will get more likes at $timer. Want unlimited likes? Subscribe below to BlackGentry Deluxe.")
                                     *//* CommonDialogs.purchaseDialog(context, "BlackGentry Premium", "You have reached the likes limit. You can send more likes " +
                                            "at $timer. Want unlimited likes? Subscribe below to BlackGentry Premium.", this)*//*
                                } else {
                                    Log.e("test", "test")
                                    baseActivity.sp.isDialogOpen = true
                                    //CommonDialogs.PremuimPurChaseDialog(context, this)
                                    CommonDialogs.DeluxePurChaseDialogWithMessage(context, this, "You have reached the likes limit. You will get more likes " + "within 12 hours. Want unlimited likes? Subscribe below to BlackGentry Deluxe.")
                                    *//*CommonDialogs.purchaseDialog(context, "BlackGentry Premium", "You have reached the likes limit. You can send more likes " +
                                            "within 12 hours. Want unlimited likes? Subscribe below to BlackGentry Premium.", this)*//*
                                }
                            } else {
                                baseActivity.sp.isDialogOpen = true
                                // CommonDialogs.PremuimPurChaseDialog(context, this)
                                CommonDialogs.DeluxePurChaseDialogWithMessage(context, this, "You have reached the likes limit. You will get more likes " + "within 12 hours. Want unlimited likes? Subscribe below to BlackGentry Deluxe.")
                                *//*  CommonDialogs.purchaseDialog(context, "BlackGentry Premium", "You have reached the likes limit. You can send more likes " +
                                          "within 12 hours. Want unlimited likes? Subscribe below to BlackGentry Premium.", this)*//*
                            }*/

                        } else {
                            baseActivity.showSnackbar(card_stack_view, resource.data.message)
                        }
                        rewind.isEnabled = true
                    }
                }
                Status.ERROR -> {
                    /* if (removeItemFromList) {
                         removeItemFromList = false
                         list = list.drop(1)
                         cardStackView?.adapter = CardDetailAdapter(context, list, myHt, this)
                         cardStackView?.adapter!!.notifyDataSetChanged()
                         if (list.isEmpty())
                             onCardDis()
                     }*/
                    baseActivity.hideLoading()
                    baseActivity.showSnackbar(card_stack_view, resource.message)
                    btn_settings.isEnabled = true
                }
            }
        })
        homeViewModel.userReactResponse1().observe(viewLifecycleOwner, Observer<Resource<ReactResponseModel>> { resource ->
            if (resource == null) {
                return@Observer
            }
            when (resource.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    Log.e(TAG, "userReactResponse1: ")
                    baseActivity.hideLoading()
                    isCardDisCalled = true
                    if (resource.data!!.success!!) {

                        if (resource.data.react.reaction.contains("dislike")) {
                            baseActivity.sp.dislikeApi = true
                        }
                        (activity as HomeActivity).swipeCount++
                        if (resource.data.key != null && resource.data.key == "Second") {
                            rewind.isEnabled = false
                            isSecond = true
                        } else {
                            isSecond = false
                        }
                        //cardStackView?.swipe()
                        baseActivity.sp.isDialogOpen = false
                        cardSwipeCount--
                        baseActivity.sp.saveSwipeCount(baseActivity.sp.swipeCount + 1)
                        val count = baseActivity.sp.swipeCount
                        Log.e("count", "count$count")
                        if (cardSwipeCount == 0) {
                            btn_settings.isEnabled = true
                        }
                        if (baseActivity.sp.swipeCount % 18 == 0 && !baseActivity.sp.premium) {
                            showAd()
                        }
                    } else if (resource.data.error != null && resource.data.error.code.contains("401")) {
                        baseActivity.openActivityOnTokenExpire()
                    } else {
                        if (resource.data.message == "Your super likes tokens are finished.") {
                            baseActivity.sp.isDialogOpen = true
                            CommonDialogs.CrushPurChaseDialog(context, this)
                            /*CommonDialogs.purchaseDialog(context, "Crush Tokens", "You have run out of crush tokens. " +
                                    "Purchase more tokens below.", this)*/
                        } else if (resource.data.message.contains("REACHED TO 100", ignoreCase = true)) {
                            if (isMySwiped) {
                                isMySwiped = false
                                val setting = RewindAnimationSetting.Builder()
                                        .setDirection(handleDirection)
                                        .setDuration(Duration.Fast.duration)
                                        .setInterpolator(DecelerateInterpolator())
                                        .build()
                                manager.setRewindAnimationSetting(setting)
                                cardStackView?.rewind()
                            }

                            /* if (resource.data.swipesData != null && resource.data.swipesData.updatedAt != null) {
                                 val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                 formatter.timeZone = TimeZone.getTimeZone("UTC")
                                 val result = formatter.parse(resource.data.swipesData.updatedAt.replace("T", " ").split("\\.").toTypedArray()[0])
                                 val calendar = Calendar.getInstance()
                                 calendar.time = result
                                 calendar.add(Calendar.HOUR_OF_DAY, 12)
                                 Log.e("Time here", calendar.time.toString())
                                 var hours = calendar.time.hours
                                 val min = calendar.time.minutes
                                 var am = "AM"
                                 if (hours >= 12) {
                                     hours -= 12
                                     am = "PM"
                                 }
                                 if (hours == 0) {
                                     hours = 12
                                     am = "AM"
                                 }
                                 timer = if (min < 10) {
                                     "$hours:0$min $am"
                                 } else {
                                     "$hours:$min $am"
                                 }
                                 baseActivity.sp.isDialogOpen = true
                                 if (!TextUtils.isEmpty(timer)) {
                                     //CommonDialogs.PremuimPurChaseDialog(context, this)
                                     CommonDialogs.DeluxePurChaseDialogWithMessage(context, this, "You have reached the likes limit. You can send more likes at $timer. Want unlimited likes? Subscribe below to BlackGentry Deluxe.")
                                     *//*  CommonDialogs.purchaseDialog(context, "BlackGentry Premium", "You have reached the likes limit. You can send more likes " +
                                              "at $timer. Want unlimited likes? Subscribe below to BlackGentry Premium.", this)*//*
                                } else {
                                    Log.e("test", "test")
                                    baseActivity.sp.isDialogOpen = true
                                    //CommonDialogs.PremuimPurChaseDialog(context, this)
                                    CommonDialogs.DeluxePurChaseDialogWithMessage(context, this, "You have reached the likes limit. You can send more likes " + "within 12 hours. Want unlimited likes? Subscribe below to BlackGentry Deluxe.")
                                    *//* CommonDialogs.purchaseDialog(context, "BlackGentry Premium", "You have reached the likes limit. You can send more likes " +
                                             "within 12 hours. Want unlimited likes? Subscribe below to BlackGentry Premium.", this)*//*
                                }
                            } else {
                                baseActivity.sp.isDialogOpen = true
                                //CommonDialogs.PremuimPurChaseDialog(context, this)
                                CommonDialogs.DeluxePurChaseDialogWithMessage(context, this, "You have reached the likes limit. You can send more likes " + "within 12 hours. Want unlimited likes? Subscribe below to BlackGentry Deluxe.")
                                *//*    CommonDialogs.purchaseDialog(context, "BlackGentry Premium", "You have reached the likes limit. You can send more likes " +
                                            "within 12 hours. Want unlimited likes? Subscribe below to BlackGentry Premium.", this)*//*
                            }*/


                        } else {
                            baseActivity.showSnackbar(card_stack_view, resource.data.message)
                        }
                        rewind.isEnabled = true
                    }
                    Log.d(TAG, "subscribeModel:1025 " + list.size + "  " + manager.topPosition)
                    if (list.size == manager.topPosition) {
                        onCardDis()
                    }
                }
                Status.ERROR -> {
                    baseActivity.hideLoading()
                    if (list.size == manager.topPosition) {
                        onCardDis()
                    }
                    isCardDisCalled = true
                    baseActivity.showSnackbar(card_stack_view, resource.message)
                    btn_settings.isEnabled = true
                    if (isMySwiped) {
                        isMySwiped = false
                        val setting = RewindAnimationSetting.Builder()
                                .setDirection(handleDirection)
                                .setDuration(Duration.Fast.duration)
                                .setInterpolator(DecelerateInterpolator())
                                .build()
                        manager.setRewindAnimationSetting(setting)
                        cardStackView?.rewind()
                    }
                }
            }
        })

        homeViewModel.rewindResponse().observe(viewLifecycleOwner, Observer<Resource<BaseModel>> { resource ->
            if (resource == null) {
                return@Observer
            }
            when (resource.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    baseActivity.hideLoading()
                    if (resource.data!!.success!!) {
                        (activity as HomeActivity).swipeCount--
                        val setting = RewindAnimationSetting.Builder()
                                .setDirection(handleDirection)
                                .setDuration(Duration.Normal.duration)
                                .setInterpolator(DecelerateInterpolator())
                                .build()
                        manager.setRewindAnimationSetting(setting)
                        cardStackView?.rewind()
                        cardSwipeCount++
                        rewind.isEnabled = false
                    } else if (resource.data.error != null && resource.data.error.code.contains("401")) {
                        baseActivity.openActivityOnTokenExpire()
                    } else {
                        baseActivity.showSnackbar(card_stack_view, resource.data.message)
                    }
                }
                Status.ERROR -> {
                    baseActivity.hideLoading()
                    baseActivity.showSnackbar(card_stack_view, resource.message)
                }
            }
        })

        homeViewModel.sendLatLongResponse().observe(viewLifecycleOwner, Observer { resource ->
            if (resource == null) {
                return@Observer
            }
            when (resource.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> if (resource.data!!.success) {
                    if (resource.data.error != null && resource.data.error.code.contains("401")) {
                        baseActivity.hideLoading()
                        baseActivity.openActivityOnTokenExpire()
                    } else {
                        val user = baseActivity.sp.user
                        val obj: ProfileOfUser = Gson().fromJson(user, ProfileOfUser::class.java)
                        baseActivity.sp.saveUserData(obj, baseActivity.sp.profileCompleted)
                        if (obj.visible == "True") {
                            if (baseActivity.sp.status == Global.statusActive) {
                                img_vip_star!!.isEnabled = true
                                chat!!.isEnabled = true
                                imageView.visibility = VISIBLE
                                homeViewModel.getUserListRequest(baseActivity.sp.token)
                            } else {
                                baseActivity.hideLoading()
                                img_vip_star!!.isEnabled = false
                                chat!!.isEnabled = false
                            }
                        }
                    }
                }
                Status.ERROR -> {
                    baseActivity.hideLoading()
                }
            }
        })

        homeViewModel.addSuperLikeResponse().observe(viewLifecycleOwner, Observer { resource ->
            if (resource == null) {
                return@Observer
            }
            when (resource.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    baseActivity.hideLoading()
                    if (resource.data!!.isSuccess) {
                        val gson = Gson()
                        val user = baseActivity.sp.user
                        val obj = gson.fromJson(user, ProfileOfUser::class.java)
                        obj.superLikesCount = resource.data.totalSuperlikes

                        baseActivity.sp.saveUserData(obj, baseActivity.sp.profileCompleted)
                        Toast.makeText(context, "Item Purchased", Toast.LENGTH_LONG).show()

                    } else if (resource.code == 401) {
                        baseActivity.openActivityOnTokenExpire()
                    } else {
                        baseActivity.showSnackbar(card_stack_view, "Something went wrong")
                    }
                }
                Status.ERROR -> {
                    baseActivity.hideLoading()
                    baseActivity.showSnackbar(card_stack_view, resource.message)
                }
            }
        })

        homeViewModel.reportResponse().observe(viewLifecycleOwner, Observer { resource ->
            if (resource == null) {
                return@Observer
            }
            when (resource.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    CommonDialogs.dismiss()
                    baseActivity.hideLoading()
                    baseActivity.hideKeyboard()
                    if (resource.data!!.success) {
                        if (resource.data.error != null && resource.data.error.code.contains("401")) {
                            baseActivity.openActivityOnTokenExpire()

                        } else {
                            showSnackBar(card_stack_view, "User successfully reported.")
                            // view?.findViewById<View>(R.id.cancel)!!.performClick()
                            rewind.isEnabled = false
                        }
                    } else {
                        showSnackBar(card_stack_view, "User has already been reported.")
                    }
                }
                Status.ERROR -> {
                    CommonDialogs.dismiss()
                    baseActivity.hideLoading()
                    baseActivity.showSnackbar(card_stack_view, resource.message)
                }
            }
        })

        homeViewModel.settingsResponse().observe(viewLifecycleOwner, Observer { resource ->
            if (resource == null) {
                return@Observer
            }
            when (resource.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    baseActivity.hideLoading()
                    if (resource.data!!.error != null && resource.data.error.code.equals("401")) {
                        baseActivity.openActivityOnTokenExpire()
                    } else {
                        val gson = Gson()
                        val user: String = baseActivity.sp.user
                        val obj = gson.fromJson(user, ProfileOfUser::class.java)
                        obj.visible = resource.data.settings.visible
                        obj.matchNotify = resource.data.settings.matchNotify
                        obj.chatNotify = resource.data.settings.chatNotify
                        obj.expiredMatches = resource.data.settings.expiredMatches
                        obj.maxAgePrefer = resource.data.settings.maxAgePrefer
                        obj.minAgePrefer = resource.data.settings.minAgePrefer
                        obj.distance = resource.data.settings.distance.toDouble()
                        baseActivity.sp.saveUserData(obj, baseActivity.sp.profileCompleted)
                        btn_unhide.visibility = GONE
                        tvHideProfile.visibility = GONE
                        cl_hide.visibility = GONE
                        hitLocApi()
                    }
                }
                Status.ERROR -> {
                    baseActivity.hideLoading()
                    baseActivity.showSnackbar(tvHideProfile, resource.message)
                }
            }
        })

        homeViewModel.subscriptionResponse().observe(viewLifecycleOwner, Observer { resource ->
            if (resource == null) {
                return@Observer
            }
            when (resource.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    if (resource.data!!.error != null && resource.data.error.code == "401") {
                        baseActivity.hideLoading()
                        baseActivity.openActivityOnTokenExpire()
                    } else {
                        if (resource.data.subscription != null) {
/*                            var isSubscribed = resource.data.subscription.isDeluxe.equals("Yes", ignoreCase = true)
                            if (!isSubscribed!!) {*/
                            val isSubscribed = resource.data.subscription.isPremium.equals("Yes", ignoreCase = true)
                            //                          }
                            var productId: String = ""
                            if (isSubscribed) {
                                productId = resource.data.subscription.subscriptionForUser.subscriptionId
                                checkSubscription(productId, resource.data.subscription.subscriptionForUser.purchaseToken)
                            }else{
                                baseActivity.hideLoading()
                            }
                        } else {
                            /*if (baseActivity.sp.deluxe) {
                                checkExistingSubscriptionDeluxe()
                            } else {*/
                            Log.e(TAG, "subscribeModel:  ")
                            checkExistingSubscription()
                            //}
                        }
                    }
                }
                Status.ERROR -> {
                    baseActivity.hideLoading()
                    /*if (baseActivity.sp.deluxe) {
                        checkExistingSubscriptionDeluxe()
                    } else {*/
                    try {
                        checkExistingSubscription()
                    } catch (er: Exception) {
                    }
                    //}
                }
            }
        })

        homeViewModel.vipTokenResponse().observe(viewLifecycleOwner, Observer { resource ->
            if (resource == null) {
                return@Observer
            }
            when (resource.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    baseActivity.hideLoading()
                    if (resource.data!!.isSuccess) {
                        val gson = Gson()
                        val user = baseActivity.sp.user
                        val obj = gson.fromJson(user, ProfileOfUser::class.java)
                        obj.vipToken = resource.data!!.totalVIPToken
                        baseActivity.sp.saveUserData(obj, baseActivity.sp.profileCompleted)
                        //tv_vipNum.setText(if (obj.vipToken == 1) "" + obj.vipToken else "" + obj.vipToken)  Your profile will be shown to more users for the next 30 minutes.
                        baseActivity.showSnackbar(cardStackView, " Your profile will be shown to more users for the next 30 minutes.")
                    } else if (resource.code == 401) {
                        baseActivity.openActivityOnTokenExpire()
                    } else {
                        baseActivity.showSnackbar(cardStackView, "Something went wrong")
                    }
                }
                Status.ERROR -> {
                    baseActivity.hideLoading()
                    baseActivity.showSnackbar(cardStackView, resource.message)
                }
            }
        })

        homeViewModel.addPremiumResponse().observe(viewLifecycleOwner, Observer {
            if (it == null) {
                return@Observer
            }
            when (it.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
//                    baseActivity.hideLoading()
                    imageView.visibility = GONE
                    if (it.data!!.success) {
                        baseActivity.sp.savePremium(true)
                    } else if (it.code == 401) {
                        baseActivity.openActivityOnTokenExpire()
                    }
                }
                Status.ERROR -> {
                    imageView.visibility = GONE
//                    baseActivity.hideLoading()
                }
            }
        })

        homeViewModel.changePremiumResponse().observe(viewLifecycleOwner, Observer { resource ->
            if (resource == null) {
                return@Observer
            }
            when (resource.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    if (resource.code == 401) {
                        baseActivity.openActivityOnTokenExpire()
                    }
                }
                Status.ERROR -> {
                    baseActivity.hideLoading()
                }
            }
        })
        /*homeViewModel.userReactResponse1().observe(this, Observer<Resource<ReactResponseModel>> { resource ->
            if (resource == null) {
                return@Observer
            }
            when (resource.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    if (resource.data!!.success!!) {
                        (activity as HomeActivity).swipeCount++
                        if (resource.data.key != null && resource.data.key == "Second") {
                            rewind.isEnabled = false
                            isSecond = true
                        } else {
                            isSecond = false
                        }
                        //cardStackView?.swipe()
                        baseActivity.sp.isDialogOpen = false
                        cardSwipeCount--
                        baseActivity.sp.saveSwipeCount(baseActivity.sp.swipeCount + 1)
                        val count = baseActivity.sp.swipeCount
                        Log.e("count", "count$count")
                        if (cardSwipeCount == 0) {
                            btn_settings.isEnabled = true
                        }
                        if (baseActivity.sp.swipeCount % 20 == 0 && !baseActivity.sp.premium) {
                            showAd()
                        }
                    } else if (resource.data.error != null && resource.data.error.code.contains("401")) {
                        baseActivity.openActivityOnTokenExpire()
                    } else {
                        if (resource.data.message == "Your super likes tokens are finished.") {
                            baseActivity.sp.isDialogOpen = true
                            CommonDialogs.purchaseDialog(context, "Crush Tokens", "You have run out of crush tokens. " +
                                    "Purchase more tokens below.", this)
                        } else if (resource.data.message.contains("REACHED TO 100", ignoreCase = true)) {
                            if (resource.data.swipesData != null && resource.data.swipesData.updatedAt != null) {
                                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                formatter.timeZone = TimeZone.getTimeZone("UTC")
                                val result = formatter.parse(resource.data.swipesData.updatedAt.replace("T", " ").split("\\.").toTypedArray()[0])
                                val calendar = Calendar.getInstance()
                                calendar.time = result
                                calendar.add(Calendar.HOUR_OF_DAY, 12)
                                Log.e("Time here", calendar.time.toString())
                                var hours = calendar.time.hours
                                val min = calendar.time.minutes
                                var am = "AM"
                                if (hours >= 12) {
                                    hours -= 12
                                    am = "PM"
                                }
                                if (hours == 0) {
                                    hours = 12
                                    am = "AM"
                                }
                                timer = if (min < 10) {
                                    "$hours:0$min $am"
                                } else {
                                    "$hours:$min $am"
                                }
                                baseActivity.sp.isDialogOpen = true
                                if (!TextUtils.isEmpty(timer)) {
                                    CommonDialogs.purchaseDialog(context, "BlackGentry Premium", "You have reached the likes limit. You can send more likes " +
                                            "at $timer. Want unlimited likes? Subscribe below to BlackGentry Premium.", this)
                                } else {
                                    Log.e("test", "test")
                                    baseActivity.sp.isDialogOpen = true
                                    CommonDialogs.purchaseDialog(context, "BlackGentry Premium", "You have reached the likes limit. You can send more likes " +
                                            "within 12 hours. Want unlimited likes? Subscribe below to BlackGentry Premium.", this)
                                }
                            } else {
                                baseActivity.sp.isDialogOpen = true
                                CommonDialogs.purchaseDialog(context, "BlackGentry Premium", "You have reached the likes limit. You can send more likes " +
                                        "within 12 hours. Want unlimited likes? Subscribe below to BlackGentry Premium.", this)
                            }
                        } else {
                            baseActivity.showSnackBar(card_stack_view, resource.data.message)
                        }
                        rewind.isEnabled = true
                    }
                    if (list.size == manager.topPosition) {
                        onCardRemoved()
                    }
                }
                Status.ERROR -> {
                    if (list.size == manager.topPosition) {
                        onCardRemoved()
                    }
                    baseActivity.showSnackBar(card_stack_view, resource.message)
                    btn_settings.isEnabled = true
                }
            }
        })*/

        homeViewModel.useApplyVipTokenResponse().observe(viewLifecycleOwner, Observer { resource ->
            if (resource == null) {
                return@Observer
            }
            when (resource.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    mActivity.hideLoading()
                    if (resource.data!!.success) {
                        if (resource.data.error != null && resource.data.error.code.contains("401")) {
                            mActivity.openActivityOnTokenExpire()
                        } else {
                            val gson = Gson()
                            val user: String = baseActivity.sp.user
                            val obj = gson.fromJson(user, ProfileOfUser::class.java)
                            obj.vipToken = obj.vipToken - 1
                            baseActivity.sp.saveUserData(obj, baseActivity.sp.profileCompleted)
                            callAlertForVipTokenWithNoAction(true)
                            //baseActivity.showSnackBar(card_stack_view, "One Vip Token Consumed Successfully")
                        }
                    }
                }
                Status.ERROR -> {
                    mActivity.hideLoading()
                    baseActivity.showSnackbar(card_stack_view, resource.message)
                }
            }
        })
    }

    /**
     * Method to handle ads
     */
    private fun showAd() {
        val videoOptions = VideoOptions.Builder()
                .setStartMuted(false)
                .build()
        val adOptions = com.google.android.gms.ads.nativead.NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build()
        adLoader = AdLoader.Builder(context, "ca-app-pub-9278644902816701/4260560788")
                .forNativeAd { ad: NativeAd ->
                    // Show the ad.
                    if (!adLoader.isLoading) {
                        val vc = ad.mediaContent.videoController
                        mAdView.mediaView = media_view
                        mAdView.mediaView.setMediaContent(ad.mediaContent)
                        mAdView.callToActionView = superlike2
                        mAdView.callOnClick()

                        vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
                        }
                        if (ad.mediaContent.hasVideoContent()) {
                            Log.e("hasVideoContent", "hasVideoContent")
                            vc.play()
                            vc.mute(true)
                        } else {
                            Log.e("NOVideoContent", "NOVideoContent")
                            ivView.visibility = VISIBLE
                            if (ad.mediaContent.mainImage != null) {
                                Glide.with(ivView).load(ad.mediaContent.mainImage).into(ivView)
                            } else {
                                Glide.with(ivView).load(ad.images[0].drawable).into(ivView)
                            }
                        }
                        if (tvTitle != null)
                            tvTitle.text = ad.headline
                        if (cancel != null)
                            cancel.isEnabled = true
                        if (superlike != null)
                            superlike.isEnabled = true
                        if (love != null)
                            love.isEnabled = true

                        mAdView.setNativeAd(ad)

                    }
                }
                .withNativeAdOptions(adOptions)
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(errorCode: Int) {
                        Log.e("onAdFailedToLoad", "errorCode$errorCode")
                        // Handle the failure by logging, altering the UI, and so on.

                        superlike2?.visibility = GONE
                        love.visibility = VISIBLE
                        linear.visibility = INVISIBLE
                        card_stack_view.visibility = VISIBLE
                        mAdView.visibility = INVISIBLE
//                        cl_ad.background = context!!.resources.getDrawable(R.drawable.koloda_card_view_background)
                    }

                    override fun onAdOpened() {
                        Log.e("onAdOpened", "onAdOpened")

                        linear.visibility = VISIBLE
                        love.visibility = GONE
                        superlike2?.visibility = VISIBLE
                        mAdView.visibility = VISIBLE
                        card_stack_view.visibility = INVISIBLE
                    }

                    override fun onAdClosed() {
                        Log.e("onAdClosed", "onAdClosed")
                        card_stack_view.visibility = VISIBLE
                        superlike2?.visibility = GONE
                        love.visibility = VISIBLE
                        linear.visibility = INVISIBLE
                        mAdView.visibility = INVISIBLE
//                        cl_ad.background = context!!.resources.getDrawable(R.drawable.koloda_card_view_background)

                    }

                    override fun onAdLoaded() {
                        Log.e("onAdLoaded", "onAdLoaded")

                        linear.visibility = VISIBLE
                        love.visibility = GONE
                        superlike2?.visibility = VISIBLE
                        mAdView.visibility = VISIBLE
                        card_stack_view.visibility = INVISIBLE
                    }

                    override fun onAdLeftApplication() {
                        Log.e("onAdLeftApplication", "onAdLeftApplication")
                        card_stack_view.visibility = VISIBLE
                        superlike2?.visibility = GONE
                        linear.visibility = INVISIBLE
                        love.visibility = VISIBLE
                        cardStackView!!.visibility = VISIBLE
                        mAdView.visibility = INVISIBLE
                    }

                    override fun onAdClicked() {
                        Log.e("onAdClicked", "onAdClicked")
                    }

                    override fun onAdImpression() {
                        Log.e("onAdImpression", "onAdImpression")
                    }

                })
                .withNativeAdOptions(com.google.android.gms.ads.nativead.NativeAdOptions.Builder()
                        .build())
                .build()
        adLoader.loadAd(AdRequest.Builder().build())

    }

    /**
     * check permission for location
     */
    private fun checkLocationPermission() {
        if (checkPermissionLOC()) {
            checkLocation()
        } else
            requestPermissionLOC()
    }

    /**
     * requesting permission for location
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissionLOC() {
        try {
            if (ActivityCompat.shouldShowRequestPermissionRationale(baseActivity, Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(baseActivity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST_CODE_LOC)
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST_CODE_LOC)
            }
        } catch (e: Exception) {
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE_LOC -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocation()
                    cl_NoPermission?.visibility = GONE
                } else {
                    cl_NoPermission?.visibility = VISIBLE
                }
            }
        }
    }

    /**
     * method to check permission of location
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun checkPermissionLOC(): Boolean {
        val result = context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) }
        val result1 = context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION) }
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }


    override fun onPause() {
        super.onPause()
        /*  if (baseActivity.isNetworkConnected) {
              baseActivity.isCardScreen = false
              baseActivity.sp.saveFirstTime("false")
              (activity as HomeActivity?)!!.cardList = arrayListOf<User>()
              (activity as HomeActivity?)!!.cardList = list
          }*/
    }

    /**
     * Method for checking location (whether location is on/off)
     */
    private fun checkLocation() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
        builder.setAlwaysShow(true)
        val client = context?.let { LocationServices.getSettingsClient(it) }
        val task = client?.checkLocationSettings(builder.build())


        if (task != null) {
            task.addOnSuccessListener { getLocation() }
            task.addOnFailureListener() { e ->
                if (e is ResolvableApiException) {
                    try {
                        val resolvable = e as ResolvableApiException
                        resolvable?.startResolutionForResult(context as Activity?, 1000)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        sendEx.message // Ignore the error.
                    } catch (nullEx: NullPointerException) {
                        nullEx.message // Ignore the error.
                    }
                }
            }
        }
    }

    //var likePerfomed = false
    //var removeItemFromList = false
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 4040) {
            if (checkPermissionLOC()) {
                cl_NoPermission?.visibility = GONE
                checkLocationPermission()
            }
        }
        Log.e(TAG, "onActivityResult: before  $resultCode")
        /* if (resultCode == 9898) {
            /////// (activity as HomeActivity).swipeCount++
            *//* Log.e(TAG, "onActivityResult: "+9898 )
             likePerfomed = true
             removeItemFromList = true

             isSwipedCalled = false
             isCardDisCalled = true
                 val setting = SwipeAnimationSetting.Builder()
                         .setDirection(Direction.Right)
                         .setDuration(Duration.Normal.duration)
                         .setInterpolator(AccelerateInterpolator())
                         .build()
                 manager.setSwipeAnimationSetting(setting)
                 isLiked = false
                 if (list.isNotEmpty()) {
                     cardId = list[0].id
                     homeViewModel.getUserReactRequest(ReactRequestModel("like", list[0].id))
                     if (rewind != null) {
                        handleDirection = Direction.Right
                        rewind.isEnabled = true
                    }
                }*//*

        }*/
        if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {
                Handler().postDelayed({ this.getLocation() }, 1000)
            } else if (data != null && data.hasExtra("hitApi")) {
                val gson = Gson()
                val user = baseActivity.sp.user
                val obj = gson.fromJson(user, ProfileOfUser::class.java)
                if (obj.visible.equals("True", ignoreCase = true)) {
                    //  baseActivity.showLoading()
                    imageView.visibility = VISIBLE
                    homeViewModel.getUserListRequest(baseActivity.sp.token)
                } else {
                    imageView.visibility = GONE
                    btn_unhide.visibility = VISIBLE
                    tvHideProfile.visibility = VISIBLE
                    cl_hide.visibility = VISIBLE
                }
            }/* else if (!bp!!.handleActivityResult(requestCode, resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data)
            }*/
        }
    }

    private val TAG = "FindMatchFragment"

    /**
     * method to get current location using fused location provider client
     */
    private fun getLocation() {
        try {
            if (context?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) } != PackageManager.PERMISSION_GRANTED && context?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION) } != PackageManager.PERMISSION_GRANTED) {
                return
            }

            val mFusedLocationClient = context?.let { LocationServices.getFusedLocationProviderClient(it) }
            mFusedLocationClient?.lastLocation?.addOnSuccessListener { currentLocation ->
                // Got last known location. In some rare situations this can be null.
                if (currentLocation != null) {
                    val latLng: LatLng
                    if (baseActivity != null && baseActivity.sp != null && baseActivity.sp.isLocation && baseActivity.sp.premium) {
                        Log.d(TAG, "getLocation: else")
                        val gson = Gson()
                        val user: String = baseActivity.sp.user
                        val obj = gson.fromJson(user, ProfileOfUser::class.java)
                        latLng = LatLng(obj.latitude.toDouble(), obj.longitude.toDouble())
                        //latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                    } else {
                        if (baseActivity != null && baseActivity.sp != null && baseActivity.sp.isLocation) {
                            baseActivity.sp.clearByKey("loc")
                        }
                        latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                        Log.d(TAG, "getLocation: if")
                    }
                    lat = String.format("%.4f", latLng.latitude)
                    lng = String.format("%.4f", latLng.longitude)
                    hitLocApi()
                } else {
                    locationCheckCount++
                    if (locationCheckCount > 50)
                        getLocationFromNetworkProvider()
                    else
                        checkLocationPermission()
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    /**
     * method to get location from network provider
     */
    private fun getLocationFromNetworkProvider() {
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        Log.e("isNetworkEnabled", isNetworkEnabled.toString() + "")

        if (isNetworkEnabled) {
            if (context?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) } != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e("permission", "permission not granted")
                return
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1000f, object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    if (!gotLocation) {
                        lat = String.format("%.4f", location.latitude)
                        lng = String.format("%.4f", location.longitude)
                        gotLocation = true
                        hitLocApi()
                    }
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

                override fun onProviderEnabled(provider: String) {}

                override fun onProviderDisabled(provider: String) {}
            })
            val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (location != null) {
                if (!gotLocation) {
                    lat = String.format("%.4f", location.latitude)
                    lng = String.format("%.4f", location.longitude)
                    gotLocation = true
                    hitLocApi()
                }
            }
        }
    }

    /**
     * Hit Location Api
     */
    private fun hitLocApi() {
        if (baseActivity != null) {
            /*if (constraint_verify != null)
                constraint_verify.visibility == GONE
            */
            img_vip_star?.isEnabled = true
            chat?.isEnabled = true
            rlFilter?.isEnabled = true
            val gson = Gson()
            val user = baseActivity.sp.user
            Log.e(TAG, "hitLocApi: $user")
            val obj = gson.fromJson(user, ProfileOfUser::class.java)
            if (obj != null) {
                obj.latitude = lat
                obj.longitude = lng
                Log.e("latiCheck", "$lat $lng")
                //    baseActivity.showLoading()
                baseActivity.sp.saveUserData(obj, baseActivity.sp.profileCompleted)
                homeViewModel.sendLatLong(LocationModel(lat, lng))
                if (obj.visible == "True") {
                    if (baseActivity.sp.status == Global.statusActive) {
                        btn_unhide.visibility = GONE
                        tvHideProfile.visibility = GONE
                        cardStackView!!.visibility = GONE
                    } else {
                        /* if (!baseActivity.sp.isRejected) {
                             constraint_verify.visibility = VISIBLE
                             tvProfileReject.visibility = GONE
                         } else {
                             tvProfileReject.visibility = VISIBLE
                             constraint_verify.visibility = GONE
                         }*/
                        imageView.visibility = GONE
                        clView.visibility = GONE
                        img_vip_star?.isEnabled = false
                        rewind?.isEnabled = false
                        chat?.isEnabled = false
                        rlFilter?.isEnabled = true
                    }
                } else {
                    imageView.visibility = GONE
                    btn_unhide.visibility = VISIBLE
                    tvHideProfile.visibility = VISIBLE
                    cl_hide.visibility = VISIBLE
                    btn_unhide.setOnClickListener {
                        baseActivity.showLoading()
                        homeViewModel.settingsRequest(SettingsRequestModel("True",
                                if (obj.matchNotify.equals("on", true)) "On" else "Off", if (obj.emailNotify.equals("on", true)) "On" else "Off", if (obj.reactionNotify.equals("on", true)) "On" else "Off", if (obj.expiredMatches.equals("on", true)) "On" else "Off",
                                if (obj.chatNotify.equals("on", true)) "On" else "Off", if (obj.maxAgePrefer > 0) obj.maxAgePrefer else 80, if (obj.minAgePrefer > 0) obj.minAgePrefer else 18, obj.distance))
                    }
                }

            } else {
                baseActivity.openActivityOnTokenExpire()
            }
        }
    }

    override fun onCardDragging(direction: Direction, ratio: Float) {
        var percent = 75.0
        if (baseActivity.sp.profileCompleted != null && !baseActivity.sp.profileCompleted.equals(""))
            percent = baseActivity.sp.profileCompleted.toDouble()
        else
            baseActivity.openActivityOnTokenExpire()
        if (percent < 75) {
            CommonDialogs.showAlreadyPremiumUser(contextMy, getString(R.string.dialog_txt))
        }
        /* if (direction == Direction.Right || direction == Direction.Left) {
             isMySwiped = true
         }*/
        isCardDisCalled = false

        Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
        Log.d("CardStackView", "onCardDragging: $isMySwiped")
    }

    var isSwipedCalled = true

    override fun onCardSwiped(direction: Direction) {
        if (direction == Direction.Bottom) {
            cardStackView?.rewind()
        } else if (isSwipedCalled) {
            if (direction == Direction.Right) {
                isMySwiped = true
                var percent = 75.0
                if (baseActivity.sp.profileCompleted != null && baseActivity.sp.profileCompleted != "")
                    percent = baseActivity.sp.profileCompleted.toDouble()
                else
                    baseActivity.openActivityOnTokenExpire()
                if (percent >= 75) {
                    isLiked = false
                    if (list.size >= manager.topPosition) {
                        Log.e("list size", "1440  " + list.size)
                        Log.e("manager pos", "1441  " + manager.topPosition)
                        Log.e("manager pos", "1441  " + list[manager.topPosition - 1].id)
                        cardId = list[manager.topPosition - 1].id
                        // baseActivity.showLoading()
                        homeViewModel.getUserReactRequest1(ReactRequestModel("like", list[manager.topPosition - 1].id))
                        if (rewind != null) {
                            handleDirection = Direction.Right
                            rewind.isEnabled = true
                        }
                    }
                } else {
                    CommonDialogs.showAlreadyPremiumUser(contextMy, getString(R.string.dialog_txt))
                }
            } else {
                if (direction == Direction.Left) {
                    isMySwiped = true
                    var percent = 75.0
                    if (baseActivity.sp.profileCompleted != null && !baseActivity.sp.profileCompleted.equals(""))
                        percent = baseActivity.sp.profileCompleted.toDouble()
                    else
                        baseActivity.openActivityOnTokenExpire()
                    if (percent >= 75) {
                        isLiked = false
                        Log.e("list size", "1440  " + list.size)
                        Log.e("manager pos", "1441  " + manager.topPosition)
                        Log.e("manager pos", "1441  " + list[manager.topPosition - 1].id)
                        if (linear.visibility == VISIBLE) {
                            card_stack_view.visibility = VISIBLE
                            superlike2?.visibility = GONE
                            love.visibility = VISIBLE
                            linear.visibility = INVISIBLE
                            mAdView.visibility = INVISIBLE
                        } else {
                            if (list.isNotEmpty() && list.size >= manager.topPosition) {
                                cardId = list[manager.topPosition - 1].id
                                //  baseActivity.showLoading()
                                homeViewModel.getUserReactRequest1(ReactRequestModel("dislike", list[manager.topPosition - 1].id))
                                handleDirection = Direction.Left
                                rewind.isEnabled = true
                            }
                        }
                    } else {
                        if (linear.visibility == VISIBLE) {
                            card_stack_view.visibility = VISIBLE
                            superlike2?.visibility = GONE
                            love.visibility = VISIBLE
                            linear.visibility = INVISIBLE
                            mAdView.visibility = INVISIBLE
                        } else {
                            CommonDialogs.showAlreadyPremiumUser(contextMy, getString(R.string.dialog_txt))
                        }
                    }
                } else {
                    if (rewind != null)
                        rewind.isEnabled = true
                    handleDirection = direction
                }
            }
        } else {
            isSwipedCalled = true
        }
        Log.e(TAG, "onCardSwiped: $isSwipedCalled")
    }

    override fun onCardRewound() {
        if (tvNoMatch.visibility == VISIBLE) {
            btn_settings.isEnabled = true
            cardStackView!!.visibility = VISIBLE
            cardStackView?.adapter!!.notifyItemInserted(list.size - 1)
            Log.e(TAG, "onCardRewound: adaprernotufg")
        }
        Log.e(TAG, "onCardRewound: 1781 list Size=>" + list.size)
    }

    override fun onCardCanceled() {
    }

    override fun onCardAppeared(view: View, position: Int) {
        try {
            clView.visibility = VISIBLE
            imageView.visibility = GONE
            cancel.isEnabled = true
            superlike.isEnabled = true
            love.isEnabled = true
            rewind.isEnabled = true
            /*if (likePerfomed) {
                likePerfomed = false
                rewind.isEnabled = false
            }*/
            tvNoMatch.visibility = GONE
            btn_settings.visibility = GONE
            if (img_vip_star != null)
                img_vip_star?.isEnabled = true
            if (chat != null)
                chat?.isEnabled = true
            if (rlFilter != null)
                rlFilter?.isEnabled = true
        } catch (e: Exception) {
        }
    }

    var isCardDisCalled = true
    override fun onCardDisappeared(view: View, position: Int) {
        Log.e(TAG, "onCardDisappeared: $isCardDisCalled" + "  $position")
        try {
            if (isCardDisCalled) {
                val gson = Gson()
                val user = baseActivity.sp.user
                val obj = gson.fromJson(user, ProfileOfUser::class.java)
                if (list.size == position + 1) {
                    cancel.isEnabled = false
                    superlike.isEnabled = false
                    love.isEnabled = false
                    btn_settings.isEnabled = true
                    if (obj.visible == "True") {
                        //  baseActivity.showLoading()
                        imageView.visibility = VISIBLE
                        homeViewModel.getUserListRequest((activity as HomeActivity).sp.token)
                    }
                }
            }
        } catch (e: Exception) {
        }
    }


    private fun onCardDis() {
        val gson = Gson()
        val user = baseActivity.sp.user
        val obj = gson.fromJson(user, ProfileOfUser::class.java)
        cancel.isEnabled = false
        superlike.isEnabled = false
        love.isEnabled = false
        btn_settings.isEnabled = true
        if (obj.visible == "True") {
            // baseActivity.showLoading()
            imageView.visibility = VISIBLE
            homeViewModel.getUserListRequest((activity as HomeActivity).sp.token)
        }
    }
    var myHt = 0;

    /**
     * Setup for User Cards
     */
    private fun setupCardStackView() {
        if (list.isEmpty()) {
            img_vip_star?.isEnabled = false
            chat?.isEnabled = false
            rlFilter?.isEnabled = false
            tvNoMatch.visibility = VISIBLE
            btn_settings.visibility = VISIBLE
            clView.visibility = GONE
            imageView.visibility = GONE
        } else {
            clView.visibility = View.VISIBLE
            img_vip_star?.isEnabled = true
            chat?.isEnabled = true
            rlFilter?.isEnabled = true
            tvNoMatch.visibility = GONE
            btn_settings.visibility = GONE
            cardStackView!!.visibility = VISIBLE
        }
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(80.0f)
        var percent = 75.0
        if (baseActivity.sp.profileCompleted != null && baseActivity.sp.profileCompleted != "")
            percent = baseActivity.sp.profileCompleted.toDouble()
        else
            baseActivity.openActivityOnTokenExpire()
        if (percent >= 75)
            manager.setDirections(listOf(Direction.Left, Direction.Right))
        else {
            manager.setDirections(listOf())
        }
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(false)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        cardStackView?.layoutManager = manager
        cardSwipeCount = list.size
        val i = ScreenUtils.getScreenHeight(context) - (2 * ScreenUtils.getActionBarHeight(context)) - 30
        myHt = i
        cardStackView?.adapter = CardDetailAdapter(context, list, i, this, baseActivity)
        cardStackView?.adapter!!.notifyDataSetChanged()
        cardStackView?.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }

    }


    /**
     * Setup button for Swipe cards
     */
    private fun setupButton() {
        var percent = 75.0
        if (baseActivity.sp.profileCompleted != null && baseActivity.sp.profileCompleted != "")
            percent = baseActivity.sp.profileCompleted.toDouble()
        else
            baseActivity.openActivityOnTokenExpire()
        rlFilter?.setOnClickListener {
            showDistanceBottomsheet()
/*
            if (percent >= 75) {
                showDistanceBottomsheet()
            } else                     CommonDialogs.showAlreadyPremiumUser(contextMy, getString(R.string.dialog_txt))

*/

        }
        val dislike = view?.findViewById<View>(R.id.cancel)
        dislike?.setOnClickListener {
            //isMySwiped = false
            isSwipedCalled = false
            isCardDisCalled = true
            if (percent >= 75) {
                val setting = SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Left)
                        .setDuration(Duration.Normal.duration)
                        .setInterpolator(AccelerateInterpolator())
                        .build()
                manager.setSwipeAnimationSetting(setting)
                isLiked = false
                if (linear.visibility == VISIBLE) {
                    card_stack_view.visibility = VISIBLE
                    superlike2?.visibility = GONE
                    love.visibility = VISIBLE
                    linear.visibility = INVISIBLE
                    mAdView.visibility = INVISIBLE
                } else {
                    if (list.isNotEmpty() && list.size > manager.topPosition) {
                        cardId = list[manager.topPosition].id
                        Log.d(TAG, "setupButton: 1802   " + cardId)
                        baseActivity.showLoading()
                        homeViewModel.getUserReactRequest(ReactRequestModel("dislike", list[manager.topPosition].id))
                        handleDirection = Direction.Left
                        rewind.isEnabled = true
                    }
                }
            } else {
                if (linear.visibility == VISIBLE) {
                    card_stack_view.visibility = VISIBLE
                    superlike2?.visibility = GONE
                    love.visibility = VISIBLE
                    linear.visibility = INVISIBLE
                    mAdView.visibility = INVISIBLE
                } else {
                    CommonDialogs.showAlreadyPremiumUser(contextMy, getString(R.string.dialog_txt))
                }
            }
        }

        val rewind = view?.findViewById<View>(R.id.rewind)
        rewind?.setOnClickListener {
            if (baseActivity.sp.premium) {
                baseActivity.showLoading()
                homeViewModel.rewindRequest(cardId.toString())
            } else {
                CommonDialogs.PremuimPurChaseDialog(mActivity, this, baseActivity.sp)
            }
        }

        chat?.setOnClickListener {
            if (percent >= 75) {
                if (linear.visibility == VISIBLE) {
                    card_stack_view.visibility = VISIBLE
                    superlike2?.visibility = GONE
                    love.visibility = VISIBLE
                    linear.visibility = INVISIBLE
                    mAdView.visibility = INVISIBLE
                    handleDirection = Direction.Top
                } else if (list.isNotEmpty() && list.size > manager.topPosition) {
                    startActivityForResult(Intent(mContext, ChatWindow::class.java).putExtra("id", list[manager.topPosition].id)
                            .putExtra("name", list[manager.topPosition].profileOfUser.name)
                            .putExtra("tabPos", 0)
                            .putExtra("isExpired", false)
                            .putExtra("isFromCard", true)
                            .putExtra("image", list[manager.topPosition].imageForUser[0].imageUrl), 10000)
                    baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
            } else {
                if (linear.visibility == VISIBLE) {
                    card_stack_view.visibility = VISIBLE
                    superlike2?.visibility = GONE
                    love.visibility = VISIBLE
                    linear.visibility = INVISIBLE
                    mAdView.visibility = INVISIBLE
                    handleDirection = Direction.Top
                } else
                    CommonDialogs.showAlreadyPremiumUser(contextMy, getString(R.string.dialog_txt))
            }
        }

        like?.setOnClickListener {
            //isMySwiped = false
            isSwipedCalled = false
            isCardDisCalled = true
            if (percent >= 75) {
                val setting = SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Right)
                        .setDuration(Duration.Normal.duration)
                        .setInterpolator(AccelerateInterpolator())
                        .build()
                manager.setSwipeAnimationSetting(setting)
                isLiked = false
                if (list.size > manager.topPosition) {
                    cardId = list[manager.topPosition].id
                    baseActivity.showLoading()
                    homeViewModel.getUserReactRequest(ReactRequestModel("like", list[manager.topPosition].id))
                    if (rewind != null) {
                        handleDirection = Direction.Right
                        rewind.isEnabled = true
                    }
                }
            } else {
                CommonDialogs.showAlreadyPremiumUser(contextMy, getString(R.string.dialog_txt))
            }
        }

        val superlike = view?.findViewById<View>(R.id.superlike)
        superlike?.setOnClickListener {
            //  isMySwiped = false

            isCardDisCalled = true
            val user = baseActivity.sp.user
            val obj: ProfileOfUser = Gson().fromJson(user, ProfileOfUser::class.java)
            if (obj.superLikesCount > 0) {
                if (percent >= 75) {
                    val setting = SwipeAnimationSetting.Builder()
                            .setDirection(Direction.Top)
                            .setDuration(Duration.Normal.duration)
                            .setInterpolator(AccelerateInterpolator())
                            .build()
                    manager.setSwipeAnimationSetting(setting)
                    isLiked = false
                    if (linear.visibility == VISIBLE) {
                        card_stack_view.visibility = VISIBLE
                        superlike2?.visibility = GONE
                        love.visibility = VISIBLE
                        linear.visibility = INVISIBLE
                        mAdView.visibility = INVISIBLE
                        handleDirection = Direction.Top
                    } else {
                        val gson1 = Gson()
                        val user1 = baseActivity.sp.user
                        val obj1 = gson1.fromJson(user1, ProfileOfUser::class.java)
                        if (obj1.superLikesCount > 0 && list.size > manager.topPosition) {
                            cardId = list[manager.topPosition].id
                            baseActivity.showLoading()
                            homeViewModel.getUserReactRequest(ReactRequestModel("superlike", list[manager.topPosition].id))
                            obj1.superLikesCount = obj1.superLikesCount - 1
                            baseActivity.sp.saveUserData(obj1, baseActivity.sp.profileCompleted)
                            if (rewind != null) {
                                handleDirection = Direction.Top
                                rewind.isEnabled = true
                            }
                        } else {
                            baseActivity.sp.isDialogOpen = true
                            CommonDialogs.CrushPurChaseDialog(context, this)
                            /*CommonDialogs.purchaseDialog(context, "Crush Tokens", "You have run out of crush tokens. " +
                                    "Purchase more tokens below.", this)*/
                        }
                    }

                } else {
                    if (linear.visibility == VISIBLE) {
                        card_stack_view.visibility = VISIBLE
                        superlike2?.visibility = GONE
                        love.visibility = VISIBLE
                        linear.visibility = INVISIBLE
                        mAdView.visibility = INVISIBLE
                        handleDirection = Direction.Top
                    } else {
                        CommonDialogs.showAlreadyPremiumUser(contextMy, getString(R.string.dialog_txt))
                    }
                }
            } else {
                CommonDialogs.CrushPurChaseDialog(mActivity, this)
            }

        }

        val btnSettings = view?.findViewById<Button>(R.id.btn_settings)
        btnSettings?.setOnClickListener {

            showDistanceBottomsheet()
            /* startActivityForResult(Intent(context, SettingsActivity::class.java)
                     .putExtra("isCardScreen", true), 20000)
             */
        }

    }


    /* override fun onProductPurchased(productId: String?, details: TransactionDetails?) {
         *//*Toast.makeText(context, "Item Purchased", Toast.LENGTH_LONG).show()
        mActivity.showLoading()
        when {
            tokenSType.equals("crushToken", ignoreCase = true) -> {
                bp!!.consumePurchase(productId)
                homeViewModel.addSuperLikeRequest(SuperLikeCountModel(selectedPosition, price))
            }
            tokenSType.equals("vipToken", ignoreCase = true) -> {
                bp!!.consumePurchase(productId)
                homeViewModel.addVipToken(VipTokenRequestModel(selectedPosition, price))
            }
            tokenSType.equals("PremiumPurchase", ignoreCase = true) -> {
                bp!!.consumePurchase(productId)
                homeViewModel.addPremiumRequest(PremiumTokenCountModel("1", productId, price, productId!!.split("_").toTypedArray()[2].toInt(), details!!.purchaseInfo.purchaseData.orderId,
                        details.purchaseInfo.purchaseData.purchaseToken, CommonUtils.getDateForPurchase(details), details.purchaseInfo.signature,
                        details.purchaseInfo.purchaseData.purchaseState.toString()))
            }
        }*//*
    }

    override fun onPurchaseHistoryRestored() {
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        Log.e("purchase failure", "Error errorCode=$errorCode")
    }

    override fun onBillingInitialized() {

        *//*
         * Called when BillingProcessor was initialized and it's ready to purchase
         *//*
        *//* if (CommonDialogs.vipTokenPriceList.size == 0 || CommonDialogs.timeTokenPriceList.size == 0 || CommonDialogs.crushTokenPriceList.size == 0 || CommonDialogs.PremiumPriceList.size == 0) {
             CommonDialogs.onBillingInitialized(bp)
         }
         CommonDialogs.setBilling(bp)*//*

        Log.e("in-app purchase", "initialize")
    }*/
/*
    override fun OnItemClick(position: Int, type: Int, id: String?) {
        purchaseType = type
        if (type == 0) run {

//            bp!!.purchase(activity, id)
            selectedPosition = position
        } else if (type == 2) {
            bp!!.subscribe(activity, id)
            selectedPosition = position
        }
    }*/

    /* override fun onShake() {
         if (baseActivity != null && baseActivity.sp.premium && baseActivity.isCardScreen) {
             if (!isSecond) {
                 if (cardId != -1 && baseActivity != null && !baseActivity.sp.isDialogOpen) {
                     homeViewModel.rewindRequest(cardId.toString())
                 }
             }
         } else {
             if (cardId != -1 && baseActivity != null && !baseActivity.sp.isDialogOpen && baseActivity.isCardScreen) {
                 baseActivity.sp.isDialogOpen = true
                 CommonDialogs.purchaseDialog(mContext, "BlackGentry Premium", "The undo feature is available to only premium " +
                         "users. Use the buttons below to subscribe to premium.", this)
                 cardId = -1
             }
         }
     }*/

    override fun OnReportClick(id: Int) {
        val dialog = Dialog(mActivity, R.style.PauseDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        Objects.requireNonNull(dialog.window)!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_report)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        val window = dialog.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val lls = arrayOfNulls<LinearLayout>(6)
        (dialog.findViewById<View>(R.id.tv_photo) as TextView).setLayerType(LAYER_TYPE_SOFTWARE, null)
        val mm = (dialog.findViewById<View>(R.id.tv_photo) as TextView).paint.setMaskFilter(null)
        (dialog.findViewById<View>(R.id.tv_content) as TextView).setLayerType(LAYER_TYPE_SOFTWARE, null)
        val mm1 = (dialog.findViewById<View>(R.id.tv_content) as TextView).paint.setMaskFilter(null)
        (dialog.findViewById<View>(R.id.tv_stolen) as TextView).setLayerType(LAYER_TYPE_SOFTWARE, null)
        val mm2 = (dialog.findViewById<View>(R.id.tv_stolen) as TextView).paint.setMaskFilter(null)
        (dialog.findViewById<View>(R.id.tv_other) as TextView).setLayerType(LAYER_TYPE_SOFTWARE, null)
        val mm3 = (dialog.findViewById<View>(R.id.tv_other) as TextView).paint.setMaskFilter(null)
        (dialog.findViewById<View>(R.id.tv_behave) as TextView).setLayerType(LAYER_TYPE_SOFTWARE, null)
        val mm4 = (dialog.findViewById<View>(R.id.tv_behave) as TextView).paint.setMaskFilter(null)
        (dialog.findViewById<View>(R.id.tv_photo) as TextView).paint.maskFilter = mm
        (dialog.findViewById<View>(R.id.tv_content) as TextView).paint.maskFilter = mm1
        (dialog.findViewById<View>(R.id.tv_stolen) as TextView).paint.maskFilter = mm2
        (dialog.findViewById<View>(R.id.tv_other) as TextView).paint.maskFilter = mm3
        (dialog.findViewById<View>(R.id.tv_behave) as TextView).paint.maskFilter = mm4
        lls[0] = dialog.findViewById(R.id.ll_photo)
        lls[1] = dialog.findViewById(R.id.ll_content)
        lls[2] = dialog.findViewById(R.id.ll_behave)
        lls[3] = dialog.findViewById(R.id.ll_stolen)
        lls[4] = dialog.findViewById(R.id.ll_age)
        lls[5] = dialog.findViewById(R.id.ll_other)
        val btnSubmit: Button = dialog.findViewById<Button>(R.id.btn_submit)
        btnSubmit.setOnClickListener(OnClickListener {
            Log.e(TAG, "OnReportClick: cancel ")
            dialog.dismiss()
        })

        for (i in lls.indices) {
            lls[i]?.setOnClickListener {
                pos = i
                if (i == 5) {
                    dialog.dismiss()
                    showOtherDialog(dialog, id)
                } else {
                    var reason = ""
                    baseActivity.showLoading()
                    baseActivity.hideKeyboard()
                    when (pos) {
                        0 -> {
                            reason = "Inappropriate Photos"
                        }
                        1 -> {
                            reason = "Inappropriate Content"
                        }
                        2 -> {
                            reason = "Inappropriate Behaviour"
                        }
                        3 -> {
                            reason = "Stolen Photo"
                        }
                        4 -> {
                            reason = "Under 18"
                        }
                    }
                    dialog.dismiss()
                    homeViewModel.reportRequest(ReportRequestModel(id, reason))
                }
            }
        }
    }

    private fun showOtherDialog(dialog1: Dialog, myId: Int) {
        val dialog = Dialog(mContext!!, R.style.PauseDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_edit_text)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        val window = dialog.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val tvMessage = dialog.findViewById<TextView>(R.id.tv_message)
        val etReason = dialog.findViewById<EditText>(R.id.et_reason)
        val ivCross = dialog.findViewById<ImageView>(R.id.ivcross)
        val btnOk = dialog.findViewById<Button>(R.id.btn_ok)
        tvMessage.text = this.resources.getString(R.string.reasontoReport)
        btnOk.text = "Report"
        ivCross.setOnClickListener {
            baseActivity.hideKeyboardFromView(etReason)
            dialog.dismiss()
        }
        btnOk.setOnClickListener {
            if (!TextUtils.isEmpty(etReason.text.toString())) {
                baseActivity.showLoading()
                baseActivity.hideKeyboardFromView(etReason)
                homeViewModel.reportRequest(ReportRequestModel(myId, etReason.text.toString()))
                dialog.dismiss()
                dialog1.dismiss()
            } else {
                Toast.makeText(context, "Please enter the reason for report", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onClick(p0: View?) {
        if (p0!!.id == R.id.btn_continue) {
            Toast.makeText(context, "TODO", Toast.LENGTH_LONG).show()
        }
    }

    override fun onClickToken(tokenType: String, tokensNum: Int, selectedPos: Int) {
        tokenSType = tokenType
        selectedPosition = tokensNum
        var sku: SkuDetails?
        sku = null
        when {
            tokenType.equals("crushToken", ignoreCase = true) -> {
                sku = CommonDialogs.crushTokenSkuList[selectedPos]
                price = CommonDialogs.crushTokenPriceList[selectedPos].priceValue
                productId = sku.sku// CommonDialogs.crushTokenArr[selectedPos]
            }
            tokenType.equals("vipToken", ignoreCase = true) -> {
                price = CommonDialogs.vipTokenPriceList[selectedPos].priceValue
                sku = CommonDialogs.vipTokenSkuList[selectedPos]
                productId = sku.sku
            }
            tokenType.equals("PremiumPurchase", ignoreCase = true) -> {
                price = CommonDialogs.PremiumPriceList[selectedPos].priceValue
                sku = CommonDialogs.PremiumSkuList[selectedPos]
                productId = sku.sku
                CommonDialogs.dismiss()
            }
        }
        if (fragClient != null && fragClient.isReady && sku != null) {
            Log.e(TAG, "onClickToken: $sku   Position $selectedPos")
            setOnPurchaseListener(this)
            fragClient.launchBillingFlow(BaseActivity.mActivity, getBillingFlowParam(sku))
        }
    }

    override fun setProfileData() {
        if (baseActivity != null) {
            val rectUser = baseActivity.sp.noOfLikes
            if (rectUser != null) {
                val countLike = rectUser.users;
                Log.e(TAG, "init: $countLike")
                if (countLike > 99) {
                    tv_likeCount?.text = "99+"
                } else
                    tv_likeCount?.text = "" + countLike
            } else {
                tv_likeCount?.text = "0"
            }
        }
    }

    override fun onSuccessFilter(filterRequestResponse: FilterResponse?) {
        if (baseActivity != null)
            baseActivity.hideLoading()
        if (filterRequestResponse != null && filterRequestResponse.success && baseActivity != null) {
            val resObj = filterRequestResponse.settings
            Log.e(TAG, "onSuccessFilter: $filterRequestResponse")
            filterRequest?.interested = resObj?.interested
            filterRequest?.distance = resObj?.distance
            filterRequest?.minAgePrefer = resObj?.minAgePrefer
            filterRequest?.maxAgePrefer = resObj?.maxAgePrefer
            baseActivity.sp.saveFilterModel(filterRequest)
            if (baseActivity.sp.status == Global.statusActive) {
                // baseActivity.showLoading()
                imageView.visibility = VISIBLE
                homeViewModel.getUserListRequest(baseActivity.sp.token)
            }
        } else {
            if (baseActivity != null)
                baseActivity.showSnackbar(like, filterRequestResponse?.message)
        }
    }

    override fun onError(error: String?) {
        if (baseActivity != null) {
            baseActivity.hideLoading()
            baseActivity.showSnackbar(like, error)
        }
        Log.e(TAG, "onError: ")
    }

    override fun OnSuccessPurchase(purchase: Purchase) {
//        purchase.skus
        Toast.makeText(context, "Item Purchased", Toast.LENGTH_LONG).show()
        // val params = ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        if (fragClient != null && fragClient.isReady && !TextUtils.isEmpty(tokenSType)) {
            baseActivity.showLoading()
            if (tokenSType.equals("PremiumPurchase", ignoreCase = true)) {
                fragClient.acknowledgePurchase(getAcknowledgeParams(purchase.purchaseToken)) { billingResult: BillingResult ->
                    homeViewModel.addPremiumRequest(PremiumTokenCountModel("1",
                            productId,
                            price,
                            productId.split("_").toTypedArray()[2].toInt(),
                            purchase.orderId,
                            purchase.purchaseToken,
                            CommonUtils.getDateForPurchase(purchase.purchaseTime),
                            purchase.signature,
                            BaseActivity.purchaseState))
                }
            } else {
                fragClient.consumeAsync(getConsumeParam(purchase.purchaseToken)) { billingResult, s ->
                    when {
                        tokenSType.equals("crushToken", ignoreCase = true) -> {
                            homeViewModel.addSuperLikeRequest(SuperLikeCountModel(selectedPosition, price))
                        }
                        tokenSType.equals("vipToken", ignoreCase = true) -> {
                            homeViewModel.addVipToken(VipTokenRequestModel(selectedPosition, price))
                        }
                    }
                }
            }
        }
    }

    /**
     * Method to check Subscription
     */
    private fun checkSubscription(productId: String, purchaseToken: String) {
        this.productId = productId
        if (fragClient != null && fragClient.isReady) {
            if (baseActivity != null && BaseActivity.isPaymentLoadShow) {
                BaseActivity.isPaymentLoadShow = false;
                baseActivity.showLoading()
            }
            queryPurchasesAsync() { isSuccess, list ->
                if (isSuccess) {
                    if (!list.isNullOrEmpty()) {
                        var count = 0
                        for (purchase: Purchase in list) {
                            if (purchase.skus[0].equals(productId, true) && purchase.purchaseToken == purchaseToken) {
                                isFromGoogle = false
                                setOnPurchaseListener(this)
                                callPurchaseDetail(productId, purchase.purchaseToken)
                                break
                            } else {
                                count++
                            }
                        }
                        if (count == list.size) {
                            callApiSub("Cancel", productId, false)
                        }
                    } else {
                        callApiSub("Cancel", productId, false)
                    }
                } else {
                    callApiSub("Cancel", productId, false)
                }
            }
        }else {
            if (baseActivity != null)
                baseActivity.hideLoading()
        }
    }

    /**
     * Method to check Subscription
     */
    private fun checkExistingSubscription() {
        if (fragClient != null && fragClient.isReady) {
            queryPurchaseHistoryAsync() { isSuccess, list ->
                if (isSuccess && !list.isNullOrEmpty()) {
                    for (queryPurchase: PurchaseHistoryRecord in list) {
                        purchase = Purchase(queryPurchase.originalJson, queryPurchase.signature)
                        productId = purchase.skus[0]
                        if (productId == "swift_premium_1" || productId == "swift_premium_3" || productId == "swift_premium_6" || productId == "swift_premium_12") {
                            isFromGoogle = true
                            setOnPurchaseListener(this)
                            callPurchaseDetail(productId, purchase.purchaseToken)
                            break
                        }
                    }
                }
            }
        }
    }

    lateinit var purchase: Purchase
    private var isFromGoogle = false
    override fun OnGetPurchaseDetail(body: SubscriptionResponse) {
       /* val analytic=FirebaseAnalytics.getInstance(mContext)
        val bundle=Bundle();
        bundle.putString("SubscriptionResponse","isFromGoogle ? = $isFromGoogle")
        bundle.putString("autoResumeTimeMillis", "${body.autoResumeTimeMillis}")
        bundle.putString("expiryTimeMillis", body.expiryTimeMillis)
        bundle.putString("purchaseToken",purchase.purchaseToken)
        analytic.logEvent(FirebaseAnalytics.Event.ADD_PAYMENT_INFO ,bundle)*/
        val expireDate = Date(body.expiryTimeMillis.toLong())
        val nowDate = Calendar.getInstance().time
//        val productid = if(isFromGoogle)purchase.skus[0]else this.productId
        if (nowDate.after(expireDate)) {
            Log.e(TAG, "OnGetPurchaseDetail: its Expire")
            if (!isFromGoogle) {
                if (TextUtils.isEmpty(body.autoResumeTimeMillis)) {
                    callApiSub("Cancel", productId, false)
                } else {
                    if (baseActivity != null && baseActivity.sp != null && baseActivity.sp.premium) {
                        baseActivity.sp.savePremium(false)
                    }
                }
            } else if (baseActivity != null && baseActivity.sp != null && baseActivity.sp.premium) {
                baseActivity.sp.savePremium(false)
            }
        } else {
            if (isFromGoogle) {
                Log.e(TAG, "OnGetPurchaseDetail:>>> $isFromGoogle")
                subscriptiontype = "1"
                if (body.acknowledgementState == 0 && body.isAutoRenewing) {
                    fragClient.acknowledgePurchase(getAcknowledgeParams(purchase.purchaseToken)) { billingResult: BillingResult ->
                        homeViewModel.addPremiumRequest(PremiumTokenCountModel("1",
                                productId,
                                price,
                                productId.split("_").toTypedArray()[2].toInt(),
                                body.orderId,
                                purchase.purchaseToken,
                                CommonUtils.getDateForPurchase(body.startTimeMillis.toLong()),
                                purchase.signature,
                                BaseActivity.purchaseState))
                    }
                } else {
                    if (baseActivity != null && baseActivity.sp != null) {
                        callApi(PremiumTokenCountModel(subscriptiontype,
                                productId,
                                price,
                                productId.split("_").toTypedArray()[2].toInt(),
                                body.orderId,
                                purchase.purchaseToken,
                                CommonUtils.getDateForPurchase(body.startTimeMillis.toLong()),
                                purchase.signature,
                                BaseActivity.purchaseState),
                                baseActivity.sp)
                    }
                }
            } else {
                if (TextUtils.isEmpty(body.autoResumeTimeMillis)) {
                    if (baseActivity != null && baseActivity.sp != null && !baseActivity.sp.premium) {
                        baseActivity.sp.savePremium(true)
                    }
                }
            }/* else {
                if (!body.isAutoRenewing) {
                    callApiSub("Cancel", productId, false)
                }
            }*/
        }
    }
}