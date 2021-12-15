package com.swiftdating.app.data.network;

import java.util.HashMap;

import com.swiftdating.app.common.SubscriptionResponse;
import com.swiftdating.app.model.requestmodel.ApplyVipTokenRequest;
import com.swiftdating.app.model.requestmodel.DeluxeTokenCountModel;
import com.swiftdating.app.model.requestmodel.FilterRequest;
import com.swiftdating.app.model.requestmodel.MessageListRequestModel;
import com.swiftdating.app.model.requestmodel.LocationModel;
import com.swiftdating.app.model.requestmodel.AnswerProfileRequest;
import com.swiftdating.app.model.requestmodel.ApplyTimeTokenRequest;
import com.swiftdating.app.model.requestmodel.OrderImageModel;
import com.swiftdating.app.model.requestmodel.PremiumStatusChange;
import com.swiftdating.app.model.requestmodel.PremiumTokenCountModel;
import com.swiftdating.app.model.requestmodel.ReactRequestModel;
import com.swiftdating.app.model.requestmodel.ReportRequestModel;
import com.swiftdating.app.model.requestmodel.ResendRequest;
import com.swiftdating.app.model.requestmodel.SettingsRequestModel;
import com.swiftdating.app.model.requestmodel.SignUpRequestModel;
import com.swiftdating.app.model.requestmodel.SuperLikeCountModel;
import com.swiftdating.app.model.requestmodel.TimeTokenRequestModel;
import com.swiftdating.app.model.requestmodel.VerficationRequestModel;
import com.swiftdating.app.model.requestmodel.VipTokenRequestModel;
import com.swiftdating.app.model.requestmodel.createaccountmodel.CreateAccountBirthModel;
import com.swiftdating.app.model.requestmodel.createaccountmodel.CreateAccountCityModel;
import com.swiftdating.app.model.requestmodel.createaccountmodel.CreateAccountDrinkModel;
import com.swiftdating.app.model.requestmodel.createaccountmodel.CreateAccountEducationModel;
import com.swiftdating.app.model.requestmodel.createaccountmodel.CreateAccountEmailModel;
import com.swiftdating.app.model.requestmodel.createaccountmodel.CreateAccountExerciseModel;
import com.swiftdating.app.model.requestmodel.createaccountmodel.CreateAccountGenderModel;
import com.swiftdating.app.model.requestmodel.createaccountmodel.CreateAccountHeightModel;
import com.swiftdating.app.model.requestmodel.createaccountmodel.CreateAccountInterestedModel;
import com.swiftdating.app.model.requestmodel.createaccountmodel.CreateAccountKidsModel;
import com.swiftdating.app.model.requestmodel.createaccountmodel.CreateAccountLocationModel;
import com.swiftdating.app.model.requestmodel.createaccountmodel.CreateAccountNameModel;
import com.swiftdating.app.model.requestmodel.createaccountmodel.CreateAccountOccupationModel;
import com.swiftdating.app.model.requestmodel.createaccountmodel.CreateAccountPetModel;
import com.swiftdating.app.model.requestmodel.createaccountmodel.CreateAccountPoliticalModel;
import com.swiftdating.app.model.requestmodel.createaccountmodel.CreateAccountQuestionModel;
import com.swiftdating.app.model.requestmodel.createaccountmodel.CreateAccountRelegionModel;
import com.swiftdating.app.model.requestmodel.createaccountmodel.CreateAccountSchoolModel;
import com.swiftdating.app.model.requestmodel.createaccountmodel.CreateAccountSignModel;
import com.swiftdating.app.model.requestmodel.createaccountmodel.CreateAccountSmokeModel;
import com.swiftdating.app.model.requestmodel.createaccountmodel.CreateAccoutAboutModel;
import com.swiftdating.app.model.requestmodel.createaccountmodel.CreateAccoutAmbitionModel;
import com.swiftdating.app.model.requestmodel.createaccountmodel.CreateAccoutLookingModel;
import com.swiftdating.app.model.responsemodel.AccessTokenResponce;
import com.swiftdating.app.model.responsemodel.FilterResponse;
import com.swiftdating.app.model.responsemodel.PhoneLoginResponse;
import com.swiftdating.app.model.responsemodel.VerificationResponseModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;;

public interface ApiUtils {

//    ------------------------Chat-------------------------------------

    @GET("/api/chat/checkunread")
    Call<ResponseBody> getUnreadMessages(@Header("Authorization") String header);

    @GET("/api/chat/getChatList")
    Call<ResponseBody> getChatList(@Header("Authorization") String header);

    @POST("/api/chat/getMessagelist")
    Call<ResponseBody> getMessageList(@Header("Authorization") String header, @Body MessageListRequestModel data);

    @POST("/api/chat/changeStatus/{id}")
    Call<ResponseBody> changeStatus(@Header("Authorization") String header, @Path("id") Integer id);
//    ------------------------Auth-------------------------------------

    @POST("users/login")
    Call<ResponseBody> loginRegisterApi(@Body SignUpRequestModel data);

    @POST("users/resendOtp")
    Call<ResponseBody> resend(@Body ResendRequest data);

    @POST("users/verifyOtp")
    Call<ResponseBody> verifyApi(@Body VerficationRequestModel data);

    @GET("/api/users/logout")
    Call<ResponseBody> logout(@Header("Authorization") String header);

    @POST("/api/users/reportUser")
    Call<ResponseBody> reportProfile(@Header("Authorization") String header,
                                     @Body ReportRequestModel settingsRequestModel);


    //    ------------------------Update Profile-------------------------------------

    @POST("users/completeRegistration")
    Call<ResponseBody> completeRegistration(@Header("Authorization") String header,
                                            @Body HashMap<String, String> data);

    @POST("users/updateProfile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String header,
            @Body CreateAccountNameModel data);

    @POST("users/updateProfile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String header,
            @Body CreateAccountEmailModel data);


    @POST("users/updateProfile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String header,
            @Body CreateAccountBirthModel data);

    @POST("users/updateProfile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String header,
            @Body CreateAccountLocationModel data);

    @POST("users/updateProfile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String header,
            @Body CreateAccountGenderModel data);

    @POST("users/updateProfile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String header,
            @Body CreateAccountInterestedModel data);

    @POST("users/updateProfile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String header,
            @Body CreateAccountPetModel data);

    @POST("users/updateProfile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String header,
            @Body CreateAccountKidsModel data);

    @POST("users/updateProfile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String header,
            @Body CreateAccoutLookingModel data);

    @POST("users/updateProfile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String header,
            @Body CreateAccoutAmbitionModel data);

    @POST("users/updateProfile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String header,
            @Body CreateAccountHeightModel data);

    @POST("users/updateProfile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String header,
            @Body CreateAccountSignModel data);

    @POST("users/updateProfile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String header,
            @Body CreateAccountEducationModel data);

    @POST("users/updateProfile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String header,
            @Body CreateAccountSchoolModel data);

    @POST("users/updateProfile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String header,
            @Body CreateAccountOccupationModel data);

    @POST("users/updateProfile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String header,
            @Body CreateAccountRelegionModel data);

    @POST("users/updateProfile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String header,
            @Body CreateAccountPoliticalModel data);

    @POST("users/updateProfile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String header,
            @Body CreateAccountDrinkModel data);

    @POST("users/updateProfile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String header,
            @Body CreateAccountSmokeModel data);

    @POST("users/updateProfile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String header,
            @Body CreateAccountExerciseModel data);

    @POST("users/updateProfile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String header,
            @Body CreateAccountCityModel data);

    @POST("users/updateProfile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String header,
            @Body CreateAccoutAboutModel data);

    @POST("users/updateProfile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String header,
            @Body CreateAccountQuestionModel data);

    @POST("users/updateProfile")
    Call<ResponseBody> answerQuestions(
            @Header("Authorization") String header,
            @Body AnswerProfileRequest settingsRequestModel);

    /* api to upload files for new post */
    @Multipart
    @POST("users/uploadImages")
    Call<ResponseBody> uploadFilesAPI(
            @Header("Authorization") String header,
            @Part MultipartBody.Part[] files);

    /* api to replace files for new post */
    @Multipart
    @POST("users/replaceImages")
    Call<ResponseBody> replaceImages(
            @Header("Authorization") String header,
            @Part MultipartBody.Part[] files,
            @Part("id") RequestBody id);

    /* api to upload Selfie */
    @Multipart
    @POST("users/selfies")
    Call<ResponseBody>
    uploadSelfie(@Header("Authorization") String header,
                 @Part MultipartBody.Part[] files);

    /* api to upload files for new post */
    @GET("/api/users/Profile")
    Call<ResponseBody> getProfile(@Header("Authorization") String header);

    /* api to get User Cards */
    @GET("/api/matches/{id}?limit=10")
    Call<ResponseBody> getUserList(@Header("Authorization") String header,
                                   @Path("id") String id);

    /* api to get User Cards noLimit*/
    @GET("/api/matches/{id}")
    Call<ResponseBody> getUserListAll(@Header("Authorization") String header,
                                      @Path("id") String id);
    /* api to get New Matches List */
    @GET("/api/matches/newMatchList/{id}")
    Call<ResponseBody> getMatchList(@Header("Authorization") String header,
                                    @Path("id") String id);

    /* api to react on cards */
    @POST("/api/matches/react/v2")
    Call<ResponseBody> matchReact(@Header("Authorization") String header,
                                  @Body ReactRequestModel reactRequestModel);

    /* api to get Update Current Location */
    @POST("/api/users/latlong")
    Call<ResponseBody> sendLatLong(@Header("Authorization") String header,
                                   @Body LocationModel locationModel);

    /* api to Update Settings */
    @PUT("/api/settings")
    Call<ResponseBody> saveSettings(@Header("Authorization") String header,
                                    @Body SettingsRequestModel settingsRequestModel);

    /* api to Change Image Order */
    @PUT("/api/users/imageOrder")
    Call<ResponseBody> orderApi(@Header("Authorization") String header,
                                @Body OrderImageModel settingsRequestModel);

    /* api to Buy Crush TOkens */
    @POST("/api/matches/addSuperLikes")
    Call<ResponseBody> addSuperLike(@Header("Authorization") String header,
                                    @Body SuperLikeCountModel settingsRequestModel);

    /* api to Buy Time Tokens */
    @POST("/api/matches/addTimeTokens")
    Call<ResponseBody> addTokens(@Header("Authorization") String header,
                                 @Body TimeTokenRequestModel settingsRequestModel);

    /* api to Buy Vip Tokens */
    @POST("/api/matches/addVipTokens")
    Call<ResponseBody> addVipTokens(@Header("Authorization") String header,
                                    @Body VipTokenRequestModel settingsRequestModel);


    /* api to Buy premium User */
    @POST("/api/subscription/add")
    Call<ResponseBody> premiumUser(@Header("Authorization") String header,
                                   @Body PremiumTokenCountModel settingsRequestModel);

    /* api to Buy deluxe User */
    @POST("/api/subscription/add")
    Call<ResponseBody> deluxeUser(@Header("Authorization") String header,
                                  @Body DeluxeTokenCountModel settingsRequestModel);

    /* api to check premium User */
    @POST("/api/subscription/checksubscription")
    Call<ResponseBody> checkSubscription(@Header("Authorization") String header,
                                         @Body PremiumTokenCountModel settingsRequestModel);

    /* api to change status of Premium User */
    @PUT("/api/subscription/updateSubscription")
    Call<ResponseBody> changePremiumStatus(@Header("Authorization") String header,
                                           @Body PremiumStatusChange settingsRequestModel);

    /* api to get Subscription details of Premium User */
    @GET("/api/subscription/getDetails")
    Call<ResponseBody> getSubscriptionDetail(@Header("Authorization") String header);

    /* api to get Use Time Tokens */
    @POST("/api/matches/applyTimeTokens")
    Call<ResponseBody> applyTimeToken(@Header("Authorization") String header,
                                      @Body ApplyTimeTokenRequest applyTimeTokenRequest);


    /* api to Vip Tokens */
    @POST("/api/matches/applyVipTokens")
    Call<ResponseBody> applyVipTimeToken(@Header("Authorization") String header,
                                         @Body ApplyVipTokenRequest applyVipTokenRequest);

    /* api to get Get Other User Data*/
    @GET("/api/matches/getMatchData/{id}")
    Call<ResponseBody> userData(@Header("Authorization") String header,
                                @Path("id") String id);   /* api to get Get Other User Data*/


    @GET("/api/users/Profile")
    Call<ResponseBody> profile(@Header("Authorization") String header);

    /* api to Delete Image*/
    @DELETE("/api/users/deleteimage/{id}")
    Call<ResponseBody> deleteImage(@Header("Authorization") String header,
                                   @Path("id") String id);

    /* api to unmatch User*/
    @DELETE("/api/matches/unmatch/{id}")
    Call<ResponseBody> unmatchUser(@Header("Authorization") String header,
                                   @Path("id") String id);

    /* api to delete Account*/
    @DELETE("/api/users/deleteprofile")
    Call<ResponseBody> deleteProfile(@Header("Authorization") String header);

    /* api to rewind Reaction*/
    @DELETE("/api/matches/rewind/{id}")
    Call<ResponseBody> rewindProfile(@Header("Authorization") String header,
                                     @Path("id") String id);


    /*Fetch Short Term Access Token*/
    @FormUrlEncoded
    @POST("/oauth/access_token/")
    Call<ResponseBody> getAccessToken(@Field("client_id") String client_id,
                                      @Field("client_secret") String client_secret,
                                      @Field("code") String code,
                                      @Field("grant_type") String grant_type,
                                      @Field("redirect_uri") String redirect_uri);

    /*Send Short Term Access Token*/
    @GET("/api/users/instaToken/{token}")
    Call<ResponseBody> sendToken(@Header("Authorization") String header,
                                 @Path("token") String token);


    // Api call for Phone Login
    @POST("users/login")
    Call<PhoneLoginResponse> PhoneloginApi(@Body HashMap<String, Object> map);

    // Api call for verify otp
    @POST("users/verifyOtp")
    Call<VerificationResponseModel> VerifyOtpApi(@Body HashMap<String, Object> map);

    // Api call for link number
    @POST("users/linkNumber")
    Call<VerificationResponseModel> LinkNumber(@Body HashMap<String, Object> map);

    /* api to get list of like user Account*/
    @GET("matches/get/liked/user/{pagecount}")
    Call<ResponseBody> getUserListWhoLikedYou(@Header("Authorization") String header, @Path("pagecount") String pagecount);/* api to get list of like user Account*/

    @GET("matches/get/disliked/user")
    Call<ResponseBody> getUserDislikedList(@Header("Authorization") String header);

    @POST("matches/search/users")
    Call<ResponseBody> getSearchFilterList(@Header("Authorization") String header, @Body HashMap<String, Object> map);

    @DELETE("matches/resetSkippedProfiles")
    Call<ResponseBody> resetAllSkippedProfile(@Header("Authorization") String header);

    @GET("filters")
    Call<ResponseBody> getFilters(@Header("Authorization") String header);

    @PUT("filters")
    Call<FilterResponse> setFilters(@Header("Authorization") String header, @Body HashMap<String, Object> map);

    /*
     * get Google Access Token
     * */
    @FormUrlEncoded
    @POST("https://accounts.google.com/o/oauth2/token?grant_type=refresh_token&client_id=469150149014-70s9ou5i9oqqstt8h8tjft8hl7ajamcq.apps.googleusercontent.com&client_secret=wlUP0Jfo8xSeYUwKJe_Hynvl")
    Call<AccessTokenResponce> refreshAccessToken(@Field("refresh_token") String refresh_token);

    /*
     * Get Purchase Detail
     * */
    @GET("https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{packageName}/purchases/subscriptions/{subscriptionId}/tokens/{token}")
    Call<SubscriptionResponse> getPurchasesDetail(
                                          @Path("packageName") String packageName,
                                          @Path("subscriptionId") String subscriptionId,
                                          @Path("token") String token,
                                          @Query("access_token") String accessToken);

}
