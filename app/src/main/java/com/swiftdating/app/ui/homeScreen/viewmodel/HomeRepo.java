package com.swiftdating.app.ui.homeScreen.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.swiftdating.app.data.network.CallServer;
import com.swiftdating.app.data.network.Resource;
import com.swiftdating.app.data.preference.SharedPreference;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeRepo {

    public static HomeRepo get() {
        return new HomeRepo();
    }

    LiveData<Resource<VerificationResponseModel>> verify(final Context c, String obj) {
        final MutableLiveData<Resource<VerificationResponseModel>> data = new MutableLiveData<>();
        data.setValue(Resource.<VerificationResponseModel>loading(null));
        CallServer.get().getAPIName().getProfile(obj).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    Gson gson = new GsonBuilder().setLenient().create();

                    if (response.code() == 200) {
                        VerificationResponseModel responseBean = gson.fromJson(response.body().string(), VerificationResponseModel.class);
                        data.setValue(Resource.success(responseBean));
                    } else {
                        VerificationResponseModel responseBean = gson.fromJson(response.errorBody().string(), VerificationResponseModel.class);
                        responseBean.setMessage("Wrong OTP entered. Please try again");
                        data.setValue(Resource.success(responseBean));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setValue(Resource.<VerificationResponseModel>error(CallServer.serverError, null, 0, e));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(Resource.<VerificationResponseModel>error(CallServer.serverError, null, 0, t));
            }
        });

        return data;
    }

    LiveData<Resource<UserListResponseModel>> getUserList(final Context c, String obj) {
        final MutableLiveData<Resource<UserListResponseModel>> data = new MutableLiveData<>();
        data.setValue(Resource.<UserListResponseModel>loading(null));
        CallServer.get().getAPIName().getUserList(obj, "1").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    Gson gson = new GsonBuilder().setLenient().create();
                    if (response.code() == 200) {
                        UserListResponseModel responseBean = gson.fromJson(response.body().string(), UserListResponseModel.class);
                        data.setValue(Resource.success(responseBean));
                    } else {
                        UserListResponseModel responseBean = gson.fromJson(response.errorBody().string(), UserListResponseModel.class);
                        data.setValue(Resource.success(responseBean));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setValue(Resource.<UserListResponseModel>error(CallServer.serverError, null, 0, e));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(Resource.<UserListResponseModel>error(CallServer.serverError, null, 0, t));
            }
        });

        return data;
    }

    LiveData<Resource<UserListResponseModel>> getUserListAll(final Context c, String obj) {
        final MutableLiveData<Resource<UserListResponseModel>> data = new MutableLiveData<>();
        data.setValue(Resource.<UserListResponseModel>loading(null));
        CallServer.get().getAPIName().getUserListAll(obj, "1").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    Gson gson = new GsonBuilder().setLenient().create();
                    if (response.code() == 200) {
                        UserListResponseModel responseBean = gson.fromJson(response.body().string(), UserListResponseModel.class);
                        data.setValue(Resource.success(responseBean));
                    } else {
                        UserListResponseModel responseBean = gson.fromJson(response.errorBody().string(), UserListResponseModel.class);
                        data.setValue(Resource.success(responseBean));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setValue(Resource.<UserListResponseModel>error(CallServer.serverError, null, 0, e));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(Resource.<UserListResponseModel>error(CallServer.serverError, null, 0, t));
            }
        });

        return data;
    }

    LiveData<Resource<ReactResponseModel>> getUserReact(final Context c, ReactRequestModel obj) {
        final MutableLiveData<Resource<ReactResponseModel>> data = new MutableLiveData<>();
        SharedPreference sp = new SharedPreference(c);
        data.setValue(Resource.<ReactResponseModel>loading(null));
        CallServer.get().getAPIName().matchReact(sp.getToken(), obj).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    Gson gson = new GsonBuilder().setLenient().create();
                    if (response.code() == 200) {
                        ReactResponseModel responseBean = gson.fromJson(response.body().string(), ReactResponseModel.class);
                        data.setValue(Resource.success(responseBean));
                    } else {
                        ReactResponseModel responseBean = gson.fromJson(response.errorBody().string(), ReactResponseModel.class);
                        data.setValue(Resource.success(responseBean));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setValue(Resource.<ReactResponseModel>error(CallServer.serverError, null, 0, e));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(Resource.<ReactResponseModel>error(CallServer.serverError, null, 0, t));
            }
        });

        return data;
    }

    LiveData<Resource<ReactResponseModel>> getUserReact1(final Context c, ReactRequestModel obj) {
        final MutableLiveData<Resource<ReactResponseModel>> data = new MutableLiveData<>();
        SharedPreference sp = new SharedPreference(c);
        data.setValue(Resource.<ReactResponseModel>loading(null));
        CallServer.get().getAPIName().matchReact(sp.getToken(), obj).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    Gson gson = new GsonBuilder().setLenient().create();

                    if (response.code() == 200) {
                        ReactResponseModel responseBean = gson.fromJson(response.body().string(), ReactResponseModel.class);
                        data.setValue(Resource.success(responseBean));
                    } else {
                        ReactResponseModel responseBean = gson.fromJson(response.errorBody().string(), ReactResponseModel.class);
                        data.setValue(Resource.success(responseBean));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setValue(Resource.<ReactResponseModel>error(CallServer.serverError, null, 0, e));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(Resource.<ReactResponseModel>error(CallServer.serverError, null, 0, t));
            }
        });

        return data;
    }

    LiveData<Resource<BaseModel>> sendLatLong(final Context c, LocationModel obj) {
        final MutableLiveData<Resource<BaseModel>> data = new MutableLiveData<>();
        data.setValue(Resource.<BaseModel>loading(null));
        SharedPreference sharedPrefrence = new SharedPreference(c);
        CallServer.get().getAPIName().sendLatLong(sharedPrefrence.getToken(), obj).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    Gson gson = new GsonBuilder().setLenient().create();

                    if (response.code() == 200) {
                        BaseModel baseModel = gson.fromJson(response.body().string(), BaseModel.class);
                        data.setValue(Resource.success(baseModel));
                    } else {
                        BaseModel responseBean = new BaseModel();
                        responseBean.setSuccess(false);
                        responseBean.setMessage(response.message());
                        data.setValue(Resource.success(responseBean));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setValue(Resource.<BaseModel>error(CallServer.serverError, null, 0, e));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(Resource.<BaseModel>error(CallServer.serverError, null, 0, t));
            }
        });

        return data;
    }

    LiveData<Resource<SettingsResponseModel>> sendSettings(final Context c, SettingsRequestModel obj) {
        final MutableLiveData<Resource<SettingsResponseModel>> data = new MutableLiveData<>();
        data.setValue(Resource.<SettingsResponseModel>loading(null));
        SharedPreference sp = new SharedPreference(c);
        CallServer.get().getAPIName().saveSettings(sp.getToken(), obj).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    Gson gson = new GsonBuilder().setLenient().create();

                    if (response.code() == 200) {
                        SettingsResponseModel settingsResponseModel = gson.fromJson(response.body().string(), SettingsResponseModel.class);
                        data.setValue(Resource.success(settingsResponseModel));
                    } else {
                        SettingsResponseModel responseBean = gson.fromJson(response.errorBody().string(), SettingsResponseModel.class);
                        data.setValue(Resource.success(responseBean));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setValue(Resource.<SettingsResponseModel>error("Something Went Wrong", null, 0, e));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(Resource.<SettingsResponseModel>error(CallServer.serverError, null, 0, t));
            }
        });

        return data;
    }

    LiveData<Resource<SuperLikeResponseModel>> addSuperLike(final Context c, SuperLikeCountModel obj) {
        final MutableLiveData<Resource<SuperLikeResponseModel>> data = new MutableLiveData<>();
        data.setValue(Resource.<SuperLikeResponseModel>loading(null));
        SharedPreference sp = new SharedPreference(c);
        CallServer.get().getAPIName().addSuperLike(sp.getToken(), obj).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    Gson gson = new GsonBuilder().setLenient().create();

                    if (response.code() == 200) {
                        SuperLikeResponseModel responseBean = gson.fromJson(response.body().string(), SuperLikeResponseModel.class);
                        data.setValue(Resource.success(responseBean));
                    } else if (response.code() == 401) {
                        data.setValue(Resource.<SuperLikeResponseModel>error("Something Went Wrong", null, 401, null));
                    } else {
                        SuperLikeResponseModel responseBean = gson.fromJson(response.errorBody().string(), SuperLikeResponseModel.class);
                        data.setValue(Resource.success(responseBean));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setValue(Resource.<SuperLikeResponseModel>error("Something Went Wrong", null, response.code(), e));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(Resource.<SuperLikeResponseModel>error(CallServer.serverError, null, 0, t));
            }
        });

        return data;
    }

    LiveData<Resource<SuperLikeResponseModel>> addTimeToken(final Context c, TimeTokenRequestModel obj) {
        final MutableLiveData<Resource<SuperLikeResponseModel>> data = new MutableLiveData<>();
        data.setValue(Resource.<SuperLikeResponseModel>loading(null));
        SharedPreference sp = new SharedPreference(c);
        CallServer.get().getAPIName().addTokens(sp.getToken(), obj).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    Gson gson = new GsonBuilder().setLenient().create();

                    if (response.code() == 200) {
                        SuperLikeResponseModel responseBean = gson.fromJson(response.body().string(), SuperLikeResponseModel.class);
                        data.setValue(Resource.success(responseBean));
                    } else if (response.code() == 401) {
                        data.setValue(Resource.<SuperLikeResponseModel>error("Something Went Wrong", null, 401, null));
                    } else {
                        SuperLikeResponseModel responseBean = gson.fromJson(response.errorBody().string(), SuperLikeResponseModel.class);
                        data.setValue(Resource.success(responseBean));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setValue(Resource.<SuperLikeResponseModel>error("Something Went Wrong", null, response.code(), e));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(Resource.<SuperLikeResponseModel>error(CallServer.serverError, null, 0, t));
            }
        });

        return data;
    }

    LiveData<Resource<VipTokenResponseModel>> addVipToken(final Context c, VipTokenRequestModel obj) {
        final MutableLiveData<Resource<VipTokenResponseModel>> data = new MutableLiveData<>();
        data.setValue(Resource.<VipTokenResponseModel>loading(null));
        SharedPreference sp = new SharedPreference(c);
        CallServer.get().getAPIName().addVipTokens(sp.getToken(), obj).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    Gson gson = new GsonBuilder().setLenient().create();

                    if (response.code() == 200) {
                        VipTokenResponseModel responseBean = gson.fromJson(response.body().string(), VipTokenResponseModel.class);
                        data.setValue(Resource.success(responseBean));
                    } else if (response.code() == 401) {
                        data.setValue(Resource.<VipTokenResponseModel>error("Something Went Wrong", null, 401, null));
                    } else {
                        VipTokenResponseModel responseBean = gson.fromJson(response.errorBody().string(), VipTokenResponseModel.class);
                        data.setValue(Resource.success(responseBean));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setValue(Resource.<VipTokenResponseModel>error("Something Went Wrong", null, response.code(), e));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(Resource.<VipTokenResponseModel>error(CallServer.serverError, null, 0, t));
            }
        });

        return data;
    }


    LiveData<Resource<BaseModel>> addPremiumUser(final Context c, PremiumTokenCountModel obj) {
        final MutableLiveData<Resource<BaseModel>> data = new MutableLiveData<>();
        data.setValue(Resource.<BaseModel>loading(null));
        SharedPreference sp = new SharedPreference(c);
        CallServer.get().getAPIName().premiumUser(sp.getToken(), obj).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    Gson gson = new GsonBuilder().setLenient().create();

                    if (response.code() == 200) {
                        BaseModel responseBean = gson.fromJson(response.body().string(), BaseModel.class);
                        data.setValue(Resource.success(responseBean));
                    } else if (response.code() == 401) {
                        data.setValue(Resource.<BaseModel>error("Something Went Wrong", null, 401, null));
                    } else {
                        BaseModel responseBean = gson.fromJson(response.errorBody().string(), BaseModel.class);
                        data.setValue(Resource.success(responseBean));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setValue(Resource.<BaseModel>error("Something Went Wrong", null, response.code(), e));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(Resource.<BaseModel>error(CallServer.serverError, null, 0, t));
            }
        });

        return data;
    }

    LiveData<Resource<BaseModel>> addDeluxeUser(final Context c, DeluxeTokenCountModel obj) {
        final MutableLiveData<Resource<BaseModel>> data = new MutableLiveData<>();
        data.setValue(Resource.<BaseModel>loading(null));
        SharedPreference sp = new SharedPreference(c);
        CallServer.get().getAPIName().deluxeUser(sp.getToken(), obj).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    Gson gson = new GsonBuilder().setLenient().create();

                    if (response.code() == 200) {
                        BaseModel responseBean = gson.fromJson(response.body().string(), BaseModel.class);
                        data.setValue(Resource.success(responseBean));
                    } else if (response.code() == 401) {
                        data.setValue(Resource.<BaseModel>error("Something Went Wrong", null, 401, null));
                    } else {
                        BaseModel responseBean = gson.fromJson(response.errorBody().string(), BaseModel.class);
                        data.setValue(Resource.success(responseBean));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setValue(Resource.<BaseModel>error("Something Went Wrong", null, response.code(), e));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(Resource.<BaseModel>error(CallServer.serverError, null, 0, t));
            }
        });

        return data;
    }


    LiveData<Resource<BaseModel>> checkSubscription(final Context c, PremiumTokenCountModel obj) {
        final MutableLiveData<Resource<BaseModel>> data = new MutableLiveData<>();
        data.setValue(Resource.<BaseModel>loading(null));
        SharedPreference sp = new SharedPreference(c);
        CallServer.get().getAPIName().checkSubscription(sp.getToken(), obj).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    Gson gson = new GsonBuilder().setLenient().create();

                    if (response.code() == 200) {
                        BaseModel responseBean = gson.fromJson(response.body().string(), BaseModel.class);
                        data.setValue(Resource.success(responseBean));
                    } else if (response.code() == 401) {
                        data.setValue(Resource.<BaseModel>error("Something Went Wrong", null, 401, null));
                    } else {
                        BaseModel responseBean = gson.fromJson(response.errorBody().string(), BaseModel.class);
                        data.setValue(Resource.success(responseBean));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setValue(Resource.<BaseModel>error("Something Went Wrong", null, response.code(), e));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(Resource.<BaseModel>error(CallServer.serverError, null, 0, t));
            }
        });

        return data;
    }


    LiveData<Resource<BaseModel>> premiumStatus(final Context c, PremiumStatusChange obj) {
        final MutableLiveData<Resource<BaseModel>> data = new MutableLiveData<>();
        data.setValue(Resource.<BaseModel>loading(null));
        SharedPreference sp = new SharedPreference(c);
        CallServer.get().getAPIName().changePremiumStatus(sp.getToken(), obj).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    Gson gson = new GsonBuilder().setLenient().create();

                    if (response.code() == 200) {
                        BaseModel responseBean = gson.fromJson(response.body().string(), BaseModel.class);
                        data.setValue(Resource.success(responseBean));
                    } else if (response.code() == 401) {
                        data.setValue(Resource.<BaseModel>error("Something Went Wrong", null, 401, null));
                    } else {
                        data.setValue(Resource.<BaseModel>error(response.message(), null, 401, null));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setValue(Resource.<BaseModel>error("Something Went Wrong", null, response.code(), e));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(Resource.<BaseModel>error(CallServer.serverError, null, 0, t));
            }
        });

        return data;
    }


    LiveData<Resource<SubscriptionDetailResponseModel>> getPremiumSubscriptionDetail(final Context c, String obj) {
        final MutableLiveData<Resource<SubscriptionDetailResponseModel>> data = new MutableLiveData<>();
        data.setValue(Resource.<SubscriptionDetailResponseModel>loading(null));
        SharedPreference sp = new SharedPreference(c);
        CallServer.get().getAPIName().getSubscriptionDetail(sp.getToken()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    Gson gson = new GsonBuilder().setLenient().create();

                    if (response.code() == 200) {
                        SubscriptionDetailResponseModel responseBean = gson.fromJson(response.body().string(), SubscriptionDetailResponseModel.class);
                        data.setValue(Resource.success(responseBean));
                    } else if (response.code() == 401) {
                        SubscriptionDetailResponseModel responseBean = gson.fromJson(response.errorBody().string(), SubscriptionDetailResponseModel.class);
                        data.setValue(Resource.success(responseBean));
                    } else {
                        SubscriptionDetailResponseModel responseBean = gson.fromJson(response.errorBody().string(), SubscriptionDetailResponseModel.class);
                        data.setValue(Resource.success(responseBean));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setValue(Resource.<SubscriptionDetailResponseModel>error("Something Went Wrong", null, response.code(), e));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(Resource.<SubscriptionDetailResponseModel>error(CallServer.serverError, null, 0, t));
            }
        });

        return data;
    }

    LiveData<Resource<ResponseBody>> logout(final Context c, String obj) {
        final MutableLiveData<Resource<ResponseBody>> data = new MutableLiveData<>();
        data.setValue(Resource.<ResponseBody>loading(null));
        SharedPreference sp = new SharedPreference(c);
        CallServer.get().getAPIName().logout(sp.getToken()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    Gson gson = new GsonBuilder().setLenient().create();

                    if (response.code() == 200) {
                        data.setValue(Resource.success(response.body()));
                    } else {
                        ResponseBody responseBean = gson.fromJson(response.errorBody().string(), ResponseBody.class);
                        data.setValue(Resource.success(responseBean));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setValue(Resource.<ResponseBody>error("Something Went Wrong", null, 0, e));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(Resource.<ResponseBody>error(CallServer.serverError, null, 0, t));
            }
        });

        return data;
    }

    LiveData<Resource<ResponseBody>> deleteProfile(final Context c, String obj) {
        final MutableLiveData<Resource<ResponseBody>> data = new MutableLiveData<>();
        data.setValue(Resource.<ResponseBody>loading(null));
        SharedPreference sp = new SharedPreference(c);
        CallServer.get().getAPIName().deleteProfile(sp.getToken()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    Gson gson = new GsonBuilder().setLenient().create();

                    if (response.code() == 200) {
                        data.setValue(Resource.success(response.body()));
                    } else {
                        if (response.code() == 404) {
                            data.setValue(Resource.success(response.errorBody()));
                        } else {
                            ResponseBody responseBean = gson.fromJson(response.errorBody().string(), ResponseBody.class);
                            data.setValue(Resource.success(responseBean));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setValue(Resource.<ResponseBody>error("Something Went Wrong", null, 0, e));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(Resource.<ResponseBody>error(CallServer.serverError, null, 0, t));
            }
        });

        return data;
    }

    LiveData<Resource<BaseModel>> rewindProfile(final Context c, String obj) {
        final MutableLiveData<Resource<BaseModel>> data = new MutableLiveData<>();
        data.setValue(Resource.<BaseModel>loading(null));
        SharedPreference sp = new SharedPreference(c);
        CallServer.get().getAPIName().rewindProfile(sp.getToken(), obj).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    Gson gson = new GsonBuilder().setLenient().create();

                    if (response.code() == 200) {
                        BaseModel responseBean = gson.fromJson(response.body().string(), BaseModel.class);
                        data.setValue(Resource.success(responseBean));
                    } else {
                        BaseModel responseBean = gson.fromJson(response.errorBody().string(), BaseModel.class);
                        data.setValue(Resource.success(responseBean));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setValue(Resource.<BaseModel>error("Something Went Wrong", null, 0, e));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(Resource.<BaseModel>error(CallServer.serverError, null, 0, t));
            }
        });

        return data;
    }

    LiveData<Resource<BaseModel>> unmatchProfile(final Context c, String obj) {
        final MutableLiveData<Resource<BaseModel>> data = new MutableLiveData<>();
        data.setValue(Resource.<BaseModel>loading(null));
        SharedPreference sp = new SharedPreference(c);
        CallServer.get().getAPIName().unmatchUser(sp.getToken(), obj).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    Gson gson = new GsonBuilder().setLenient().create();

                    if (response.code() == 200) {
                        BaseModel responseBean = gson.fromJson(response.body().string(), BaseModel.class);
                        data.setValue(Resource.success(responseBean));
                    } else {
                        BaseModel responseBean = gson.fromJson(response.errorBody().string(), BaseModel.class);
                        data.setValue(Resource.success(responseBean));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setValue(Resource.<BaseModel>error("Something Went Wrong", null, 0, e));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(Resource.<BaseModel>error(CallServer.serverError, null, 0, t));
            }
        });

        return data;
    }

    LiveData<Resource<BaseModel>> reportProfile(final Context c, ReportRequestModel obj) {
        final MutableLiveData<Resource<BaseModel>> data = new MutableLiveData<>();
        data.setValue(Resource.<BaseModel>loading(null));
        SharedPreference sp = new SharedPreference(c);
        CallServer.get().getAPIName().reportProfile(sp.getToken(), obj).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    Gson gson = new GsonBuilder().setLenient().create();

                    if (response.code() == 200) {
                        BaseModel responseBean = gson.fromJson(response.body().string(), BaseModel.class);
                        data.setValue(Resource.success(responseBean));
                    } else {
                        BaseModel responseBean = gson.fromJson(response.errorBody().string(), BaseModel.class);
                        data.setValue(Resource.success(responseBean));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setValue(Resource.<BaseModel>error("Something Went Wrong", null, 0, e));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(Resource.<BaseModel>error(CallServer.serverError, null, 0, t));
            }
        });

        return data;
    }

    LiveData<Resource<BaseModel>> useTimeToken(final Context c, ApplyTimeTokenRequest obj) {
        final MutableLiveData<Resource<BaseModel>> data = new MutableLiveData<>();
        data.setValue(Resource.<BaseModel>loading(null));
        SharedPreference sp = new SharedPreference(c);
        CallServer.get().getAPIName().applyTimeToken(sp.getToken(), obj).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    Gson gson = new GsonBuilder().setLenient().create();

                    if (response.code() == 200) {
                        BaseModel responseBean = gson.fromJson(response.body().string(), BaseModel.class);
                        data.setValue(Resource.success(responseBean));
                    } else {
                        BaseModel responseBean = gson.fromJson(response.errorBody().string(), BaseModel.class);
                        data.setValue(Resource.success(responseBean));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setValue(Resource.<BaseModel>error("Something Went Wrong", null, 0, e));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(Resource.<BaseModel>error(CallServer.serverError, null, 0, t));
            }
        });

        return data;
    }

    LiveData<Resource<BaseModel>> useVipTokenToken(final Context c, ApplyVipTokenRequest obj) {
        final MutableLiveData<Resource<BaseModel>> data = new MutableLiveData<>();
        data.setValue(Resource.<BaseModel>loading(null));
        SharedPreference sp = new SharedPreference(c);
        CallServer.get().getAPIName().applyVipTimeToken(sp.getToken(), obj).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    Gson gson = new GsonBuilder().setLenient().create();

                    if (response.code() == 200) {
                        BaseModel responseBean = gson.fromJson(response.body().string(), BaseModel.class);
                        data.setValue(Resource.success(responseBean));
                    } else {
                        BaseModel responseBean = gson.fromJson(response.errorBody().string(), BaseModel.class);
                        data.setValue(Resource.success(responseBean));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setValue(Resource.<BaseModel>error("Something Went Wrong", null, 0, e));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(Resource.<BaseModel>error(CallServer.serverError, null, 0, t));
            }
        });

        return data;
    }


    LiveData<Resource<UserListResponseModel>> userData(final Context c, String obj) {
        final MutableLiveData<Resource<UserListResponseModel>> data = new MutableLiveData<>();
        data.setValue(Resource.<UserListResponseModel>loading(null));
        SharedPreference sp = new SharedPreference(c);
        CallServer.get().getAPIName().userData(sp.getToken(), obj).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    Gson gson = new GsonBuilder().setLenient().create();

                    if (response.code() == 200) {
                        UserListResponseModel responseBean = gson.fromJson(response.body().string(), UserListResponseModel.class);
                        data.setValue(Resource.success(responseBean));
                    } else {
                        UserListResponseModel responseBean = gson.fromJson(response.errorBody().string(), UserListResponseModel.class);
                        data.setValue(Resource.success(responseBean));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setValue(Resource.<UserListResponseModel>error("Something Went Wrong", null, 0, e));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(Resource.<UserListResponseModel>error(CallServer.serverError, null, 0, t));
            }
        });

        return data;
    }

    LiveData<Resource<BaseModel>> unreadMessages(final Context c, String obj) {
        final MutableLiveData<Resource<BaseModel>> data = new MutableLiveData<>();
        data.setValue(Resource.<BaseModel>loading(null));
        CallServer.get().getAPIName().getUnreadMessages(obj).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    Gson gson = new GsonBuilder().setLenient().create();

                    if (response.code() == 200) {
                        BaseModel responseBean = gson.fromJson(response.body().string(), BaseModel.class);
                        data.setValue(Resource.success(responseBean));
                    } else {
                        BaseModel responseBean = gson.fromJson(response.errorBody().string(), BaseModel.class);
                        data.setValue(Resource.success(responseBean));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setValue(Resource.<BaseModel>error(CallServer.serverError, null, 0, e));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(Resource.<BaseModel>error(CallServer.serverError, null, 0, t));
            }
        });

        return data;
    }
}
