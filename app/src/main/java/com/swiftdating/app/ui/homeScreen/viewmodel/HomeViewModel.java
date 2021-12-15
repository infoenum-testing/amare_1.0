package com.swiftdating.app.ui.homeScreen.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.swiftdating.app.data.network.Resource;
import com.swiftdating.app.model.BaseModel;
import com.swiftdating.app.model.requestmodel.ApplyVipTokenRequest;
import com.swiftdating.app.model.requestmodel.DeluxeTokenCountModel;
import com.swiftdating.app.model.requestmodel.LocationModel;
import com.swiftdating.app.model.requestmodel.ApplyTimeTokenRequest;
import com.swiftdating.app.model.requestmodel.PremiumStatusChange;
import com.swiftdating.app.model.requestmodel.PremiumTokenCountModel;
import com.swiftdating.app.model.requestmodel.ReactRequestModel;
import com.swiftdating.app.model.requestmodel.ReportRequestModel;
import com.swiftdating.app.model.requestmodel.SettingsRequestModel;
import com.swiftdating.app.model.requestmodel.SuperLikeCountModel;
import com.swiftdating.app.model.requestmodel.TimeTokenRequestModel;
import com.swiftdating.app.model.requestmodel.VipTokenRequestModel;
import com.swiftdating.app.model.responsemodel.ReactResponseModel;
import com.swiftdating.app.model.responsemodel.SettingsResponseModel;
import com.swiftdating.app.model.responsemodel.SubscriptionDetailResponseModel;
import com.swiftdating.app.model.responsemodel.SuperLikeResponseModel;
import com.swiftdating.app.model.responsemodel.UserListResponseModel;
import com.swiftdating.app.model.responsemodel.VerificationResponseModel;
import com.swiftdating.app.model.responsemodel.VipTokenResponseModel;
import okhttp3.ResponseBody;

public class HomeViewModel extends AndroidViewModel {

    private MutableLiveData<String> signIn = new MutableLiveData<>();
    private LiveData<Resource<VerificationResponseModel>> signInLD;

    private MutableLiveData<String> userListRequest = new MutableLiveData<>();
    private LiveData<Resource<UserListResponseModel>> userResponseLD;
    private MutableLiveData<String> userListAllRequest = new MutableLiveData<>();
    private LiveData<Resource<UserListResponseModel>> userResponseAllLD;

    private MutableLiveData<ReactRequestModel> userReactRequest = new MutableLiveData<>();
    private LiveData<Resource<ReactResponseModel>> userReactResponseLD;
    private MutableLiveData<ReactRequestModel> userReactRequest1 = new MutableLiveData<>();
    private LiveData<Resource<ReactResponseModel>> userReactResponseLD1;

    private MutableLiveData<LocationModel> LocModelRequest = new MutableLiveData<>();
    private LiveData<Resource<BaseModel>> LocModelResponseLD;

    private MutableLiveData<SettingsRequestModel> sendSettingsRequest = new MutableLiveData<>();
    private LiveData<Resource<SettingsResponseModel>> sendSettingsResponse;

    private MutableLiveData<SuperLikeCountModel> addSuperLikeRequest = new MutableLiveData<>();
    private LiveData<Resource<SuperLikeResponseModel>> addSuperLikeResponse;

    private MutableLiveData<TimeTokenRequestModel> addTimeTokenRequest = new MutableLiveData<>();

    private MutableLiveData<VipTokenRequestModel> addVipTokenRequest = new MutableLiveData<>();

    private LiveData<Resource<SuperLikeResponseModel>> addTimeTokenResponse;

    private LiveData<Resource<VipTokenResponseModel>> addVipTokenResponse;

    private MutableLiveData<PremiumTokenCountModel> addPremiumRequest = new MutableLiveData<>();
    private LiveData<Resource<BaseModel>> addPremiumLD;

    private MutableLiveData<DeluxeTokenCountModel> addDeluxeRequest = new MutableLiveData<>();
    private LiveData<Resource<BaseModel>> addDeluxeLD;


    private MutableLiveData<PremiumTokenCountModel> checkSubscriptionRequestMD = new MutableLiveData<>();
    private LiveData<Resource<BaseModel>> checkSubscriptionLD;


    private MutableLiveData<PremiumStatusChange> changePremiumRequest = new MutableLiveData<>();
    private LiveData<Resource<BaseModel>> changePremiumLD;

    private MutableLiveData<String> subscriptionRequest = new MutableLiveData<>();
    private LiveData<Resource<SubscriptionDetailResponseModel>> getSubscriptionLD;

    private MutableLiveData<String> logoutRequest = new MutableLiveData<>();
    private LiveData<Resource<ResponseBody>> logoutResponse;

    private MutableLiveData<String> deleteRequest = new MutableLiveData<>();
    private LiveData<Resource<ResponseBody>> deleteResponse;

    private MutableLiveData<String> rewindRequest = new MutableLiveData<>();
    private LiveData<Resource<BaseModel>> rewindResponse;

    private MutableLiveData<String> unMatchRequest = new MutableLiveData<>();
    private LiveData<Resource<BaseModel>> unMatchResponse;

    private MutableLiveData<ReportRequestModel> reportRequest = new MutableLiveData<>();
    private LiveData<Resource<BaseModel>> reportResponse;

    private MutableLiveData<ApplyTimeTokenRequest> useTimeToken = new MutableLiveData<>();
    private LiveData<Resource<BaseModel>> useTimeTokenResponse;

    private MutableLiveData<ApplyVipTokenRequest> useApplyVipTokenRequest = new MutableLiveData<>();

    private LiveData<Resource<BaseModel>> useApplyVipTokenTokenResponse;

    private MutableLiveData<String> getUserData = new MutableLiveData<>();
    private LiveData<Resource<UserListResponseModel>> getUserResponseLD;

    private MutableLiveData<String> getUnreadMessage = new MutableLiveData<>();
    private LiveData<Resource<BaseModel>> unreadMessageLD;

    public HomeViewModel(@NonNull Application application) {
        super(application);

        signInLD = Transformations.switchMap(signIn, input -> HomeRepo.get().verify(getApplication().getApplicationContext(), input));

        userResponseLD = Transformations.switchMap(userListRequest, new Function<String, LiveData<Resource<UserListResponseModel>>>() {
            @Override
            public LiveData<Resource<UserListResponseModel>> apply(String input) {
                return HomeRepo.get().getUserList(getApplication().getApplicationContext(), input);
            }
        });
        userResponseAllLD = Transformations.switchMap(userListAllRequest, new Function<String, LiveData<Resource<UserListResponseModel>>>() {
            @Override
            public LiveData<Resource<UserListResponseModel>> apply(String input) {
                return HomeRepo.get().getUserListAll(getApplication().getApplicationContext(), input);
            }
        });

        userReactResponseLD = Transformations.switchMap(userReactRequest, new Function<ReactRequestModel, LiveData<Resource<ReactResponseModel>>>() {
            @Override
            public LiveData<Resource<ReactResponseModel>> apply(ReactRequestModel input) {
                return HomeRepo.get().getUserReact(getApplication().getApplicationContext(), input);
            }
        });
        userReactResponseLD1 = Transformations.switchMap(userReactRequest1, new Function<ReactRequestModel, LiveData<Resource<ReactResponseModel>>>() {
            @Override
            public LiveData<Resource<ReactResponseModel>> apply(ReactRequestModel input) {
                return HomeRepo.get().getUserReact1(getApplication().getApplicationContext(), input);
            }
        });

        LocModelResponseLD = Transformations.switchMap(LocModelRequest, new Function<LocationModel, LiveData<Resource<BaseModel>>>() {
            @Override
            public LiveData<Resource<BaseModel>> apply(LocationModel input) {
                return HomeRepo.get().sendLatLong(getApplication().getApplicationContext(), input);
            }
        });

        sendSettingsResponse = Transformations.switchMap(sendSettingsRequest, new Function<SettingsRequestModel, LiveData<Resource<SettingsResponseModel>>>() {
            @Override
            public LiveData<Resource<SettingsResponseModel>> apply(SettingsRequestModel input) {
                return HomeRepo.get().sendSettings(getApplication().getApplicationContext(), input);
            }
        });

        addSuperLikeResponse = Transformations.switchMap(addSuperLikeRequest, new Function<SuperLikeCountModel, LiveData<Resource<SuperLikeResponseModel>>>() {
            @Override
            public LiveData<Resource<SuperLikeResponseModel>> apply(SuperLikeCountModel input) {
                return HomeRepo.get().addSuperLike(getApplication().getApplicationContext(), input);
            }
        });

        addTimeTokenResponse = Transformations.switchMap(addTimeTokenRequest, new Function<TimeTokenRequestModel, LiveData<Resource<SuperLikeResponseModel>>>() {
            @Override
            public LiveData<Resource<SuperLikeResponseModel>> apply(TimeTokenRequestModel input) {
                return HomeRepo.get().addTimeToken(getApplication().getApplicationContext(), input);
            }
        });

        addVipTokenResponse = Transformations.switchMap(addVipTokenRequest, new Function<VipTokenRequestModel, LiveData<Resource<VipTokenResponseModel>>>() {
            @Override
            public LiveData<Resource<VipTokenResponseModel>> apply(VipTokenRequestModel input) {
                return HomeRepo.get().addVipToken(getApplication().getApplicationContext(), input);
            }
        });

        addPremiumLD = Transformations.switchMap(addPremiumRequest, new Function<PremiumTokenCountModel, LiveData<Resource<BaseModel>>>() {
            @Override
            public LiveData<Resource<BaseModel>> apply(PremiumTokenCountModel input) {
                return HomeRepo.get().addPremiumUser(getApplication().getApplicationContext(), input);
            }
        });

        addDeluxeLD = Transformations.switchMap(addDeluxeRequest, new Function<DeluxeTokenCountModel, LiveData<Resource<BaseModel>>>() {
            @Override
            public LiveData<Resource<BaseModel>> apply(DeluxeTokenCountModel input) {
                return HomeRepo.get().addDeluxeUser(getApplication().getApplicationContext(), input);
            }
        });

        checkSubscriptionLD = Transformations.switchMap(checkSubscriptionRequestMD, new Function<PremiumTokenCountModel, LiveData<Resource<BaseModel>>>() {
            @Override
            public LiveData<Resource<BaseModel>> apply(PremiumTokenCountModel input) {
                return HomeRepo.get().checkSubscription(getApplication().getApplicationContext(), input);
            }
        });


        changePremiumLD = Transformations.switchMap(changePremiumRequest, new Function<PremiumStatusChange, LiveData<Resource<BaseModel>>>() {
            @Override
            public LiveData<Resource<BaseModel>> apply(PremiumStatusChange input) {
                return HomeRepo.get().premiumStatus(getApplication().getApplicationContext(), input);
            }
        });

        getSubscriptionLD = Transformations.switchMap(subscriptionRequest, new Function<String, LiveData<Resource<SubscriptionDetailResponseModel>>>() {
            @Override
            public LiveData<Resource<SubscriptionDetailResponseModel>> apply(String input) {
                return HomeRepo.get().getPremiumSubscriptionDetail(getApplication().getApplicationContext(), input);
            }
        });

        logoutResponse = Transformations.switchMap(logoutRequest, new Function<String, LiveData<Resource<ResponseBody>>>() {
            @Override
            public LiveData<Resource<ResponseBody>> apply(String input) {
                return HomeRepo.get().logout(getApplication().getApplicationContext(), input);
            }
        });

        deleteResponse = Transformations.switchMap(deleteRequest, new Function<String, LiveData<Resource<ResponseBody>>>() {
            @Override
            public LiveData<Resource<ResponseBody>> apply(String input) {
                return HomeRepo.get().deleteProfile(getApplication().getApplicationContext(), input);
            }
        });

        rewindResponse = Transformations.switchMap(rewindRequest, new Function<String, LiveData<Resource<BaseModel>>>() {
            @Override
            public LiveData<Resource<BaseModel>> apply(String input) {
                return HomeRepo.get().rewindProfile(getApplication().getApplicationContext(), input);
            }
        });
        unMatchResponse = Transformations.switchMap(unMatchRequest, new Function<String, LiveData<Resource<BaseModel>>>() {
            @Override
            public LiveData<Resource<BaseModel>> apply(String input) {
                return HomeRepo.get().unmatchProfile(getApplication().getApplicationContext(), input);
            }
        });

        reportResponse = Transformations.switchMap(reportRequest, new Function<ReportRequestModel, LiveData<Resource<BaseModel>>>() {
            @Override
            public LiveData<Resource<BaseModel>> apply(ReportRequestModel input) {
                return HomeRepo.get().reportProfile(getApplication().getApplicationContext(), input);
            }
        });

        useTimeTokenResponse = Transformations.switchMap(useTimeToken, new Function<ApplyTimeTokenRequest, LiveData<Resource<BaseModel>>>() {
            @Override
            public LiveData<Resource<BaseModel>> apply(ApplyTimeTokenRequest input) {
                return HomeRepo.get().useTimeToken(getApplication().getApplicationContext(), input);
            }
        });

        useApplyVipTokenTokenResponse = Transformations.switchMap(useApplyVipTokenRequest, new Function<ApplyVipTokenRequest, LiveData<Resource<BaseModel>>>() {
            @Override
            public LiveData<Resource<BaseModel>> apply(ApplyVipTokenRequest input) {
                return HomeRepo.get().useVipTokenToken(getApplication().getApplicationContext(), input);
            }
        });

        getUserResponseLD = Transformations.switchMap(getUserData, new Function<String, LiveData<Resource<UserListResponseModel>>>() {
            @Override
            public LiveData<Resource<UserListResponseModel>> apply(String input) {
                return HomeRepo.get().userData(getApplication().getApplicationContext(), input);
            }
        });

        unreadMessageLD = Transformations.switchMap(getUnreadMessage, new Function<String, LiveData<Resource<BaseModel>>>() {
            @Override
            public LiveData<Resource<BaseModel>> apply(String input) {
                return HomeRepo.get().unreadMessages(getApplication().getApplicationContext(), input);
            }
        });

    }




    public LiveData<Resource<VerificationResponseModel>> verifyResponse() {
        return signInLD;
    }

    public void getUserListRequest(String s) {
        userListRequest.setValue(s);
    }

    public void getUserListAllRequest(String s) {
        userListAllRequest.setValue(s);
    }

    public LiveData<Resource<UserListResponseModel>> userListResponse() {
        return userResponseLD;
    }

    public LiveData<Resource<UserListResponseModel>> userListAllResponse() {
        return userResponseAllLD;
    }

    public void getUserReactRequest(ReactRequestModel s) {
        userReactRequest.setValue(s);
    }

    public LiveData<Resource<ReactResponseModel>> userReactResponse() {
        return userReactResponseLD;
    }

    public void getUserReactRequest1(ReactRequestModel s) {
        userReactRequest1.setValue(s);
    }

    public LiveData<Resource<ReactResponseModel>> userReactResponse1() {
        return userReactResponseLD1;
    }

    public void settingsRequest(SettingsRequestModel s) {
        sendSettingsRequest.setValue(s);
    }

    public LiveData<Resource<SettingsResponseModel>> settingsResponse() {
        return sendSettingsResponse;
    }

    public void addSuperLikeRequest(SuperLikeCountModel s) {
        addSuperLikeRequest.postValue(s);
    }

    public LiveData<Resource<SuperLikeResponseModel>> addSuperLikeResponse() {
        return addSuperLikeResponse;
    }

    public void addTimeToken(TimeTokenRequestModel s) {
        addTimeTokenRequest.postValue(s);
    }

    public void addVipToken(VipTokenRequestModel s) {
        addVipTokenRequest.postValue(s);
    }

    public void applyVipToken(ApplyVipTokenRequest s) {
        useApplyVipTokenRequest.setValue(s);
    }

    public LiveData<Resource<SuperLikeResponseModel>> timeTokenResponse() {
        return addTimeTokenResponse;
    }

    public LiveData<Resource<VipTokenResponseModel>> vipTokenResponse() {
        return addVipTokenResponse;
    }

    public void addPremiumRequest(PremiumTokenCountModel s) {
        addPremiumRequest.postValue(s);//setValue(s);
    }

    public LiveData<Resource<BaseModel>> addPremiumResponse() {
        return addPremiumLD;
    }

    public void addDeluxeRequest(DeluxeTokenCountModel s) {
        addDeluxeRequest.setValue(s);
    }

    public LiveData<Resource<BaseModel>> addDeluxeResponse() {
        return addDeluxeLD;
    }

    public void checkSubscriptionRequest(PremiumTokenCountModel s) {
        checkSubscriptionRequestMD.setValue(s);
    }

    public LiveData<Resource<BaseModel>> checkSubscriptionResponse() {
        return checkSubscriptionLD;
    }


    public void changePremiumRequest(PremiumStatusChange s) {
        changePremiumRequest.postValue(s);
    }

    public LiveData<Resource<BaseModel>> changePremiumResponse() {
        return changePremiumLD;
    }

    public void getSubscriptionRequest(String s) {
        subscriptionRequest.postValue(s);
    }

    public LiveData<Resource<SubscriptionDetailResponseModel>> subscriptionResponse() {
        return getSubscriptionLD;
    }

    public void sendLatLong(LocationModel s) {
        LocModelRequest.setValue(s);
    }

    public LiveData<Resource<BaseModel>> sendLatLongResponse() {
        return LocModelResponseLD;
    }


    public void logoutRequest(String s) {
        logoutRequest.setValue(s);
    }

    public LiveData<Resource<ResponseBody>> logoutResponse() {
        return logoutResponse;
    }

    public void deleteRequest(String s) {
        deleteRequest.setValue(s);
    }

    public LiveData<Resource<ResponseBody>> deleteResponse() {
        return deleteResponse;
    }

    public void rewindRequest(String s) {
        rewindRequest.setValue(s);
    }

    public LiveData<Resource<BaseModel>> rewindResponse() {
        return rewindResponse;
    }

    public void unMatchRequest(String s) {
        unMatchRequest.setValue(s);
    }

    public LiveData<Resource<BaseModel>> unMatchResponse() {
        return unMatchResponse;
    }

    public void reportRequest(ReportRequestModel s) {
        reportRequest.setValue(s);
    }

    public LiveData<Resource<BaseModel>> reportResponse() {
        return reportResponse;
    }

    public void useTimeToken(ApplyTimeTokenRequest s) {
        useTimeToken.setValue(s);
    }

    public LiveData<Resource<BaseModel>> useTimeTokenResponse() {
        return useTimeTokenResponse;
    }

    public LiveData<Resource<BaseModel>> useApplyVipTokenResponse() {
        return useApplyVipTokenTokenResponse;
    }


    public void userDataRequest(String s) {
        getUserData.setValue(s);
    }

    public LiveData<Resource<UserListResponseModel>> userDataResponse() {
        return getUserResponseLD;
    }

    public void unreadRequest(String s) {
        getUnreadMessage.setValue(s);
    }

    public LiveData<Resource<BaseModel>> unreadResponse() {
        return unreadMessageLD;
    }
}