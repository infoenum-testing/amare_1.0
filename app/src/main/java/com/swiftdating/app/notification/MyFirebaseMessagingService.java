package com.swiftdating.app.notification;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import com.swiftdating.app.BuildConfig;
import com.swiftdating.app.R;
import com.swiftdating.app.ui.chatScreen.ChatWindow;
import com.swiftdating.app.data.preference.SharedPreference;
import com.swiftdating.app.model.NotificationModel;
import com.swiftdating.app.ui.homeScreen.HomeActivity;
import com.swiftdating.app.ui.itsAMatchScreen.ItsAMatchActivity;
import com.swiftdating.app.ui.loginScreen.LoginActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private Intent notificationIntent;
    private SharedPreference sharePref;

    @Override
    public void onNewToken(@NotNull String token) {
        Log.d("Refreshed token:", "Refreshed token: " + token);
        sharePref = new SharedPreference(this);
        sharePref.saveDeviceToken(token);
    }

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        Log.e("Notification Received", "Notification Received");
        setNotification(remoteMessage);
    }
    /**
     *  Method to send notification data on Notification Bar
     * @return
     */
    private void sendNotification(String title, String messageBody, NotificationModel match) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon);
        notificationIntent.putExtra("match", match);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

//        int pushIcon = R.mipmap.ic_launcher_round;
        int notificationId = 1;
        String channelId = getApplicationContext().getString(R.string.notification_channel_id);
        String channelName = getApplicationContext().getString(R.string.notification_channel_name);

        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            }
        }
        PendingIntent intent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setContentIntent(intent)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setContentIntent(intent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                .setSmallIcon(R.mipmap.app_icon)
                .setLargeIcon(bitmap)
                .setAutoCancel(true)
                .setTicker(messageBody)
                .setWhen(System.currentTimeMillis())
                .setOngoing(false);

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        if (mNotificationManager != null) {
            mNotificationManager.notify(notificationId, notification);
        }
    }
    /**
     *  Method to set notification data
     * @return
     */
    public void setNotification(RemoteMessage remoteMessage) {
        Log.d("DATA", "push notification data : " + remoteMessage.getData());

        sharePref = new SharedPreference(this);

        String title = getString(R.string.app_name);

        try {
            JSONObject object = new JSONObject(remoteMessage.getData().get("notification"));
            Gson gson = new GsonBuilder().setLenient().create();
            NotificationModel responseBean = gson.fromJson(object.toString(), NotificationModel.class);
            String message = responseBean.getMessage();
            String key = responseBean.getKey();

            if (isAppOnForeground()) {
                LocalBroadcastManager broadcaster;
                if (responseBean.getisDenied()) {
                    broadcaster = LocalBroadcastManager.getInstance(this);
                    Intent intent = new Intent("MyData");
                    intent.putExtra("rejected", true);
                    intent.putExtra("message", message);
                    broadcaster.sendBroadcast(intent);
                } else if (responseBean.getIsDestroyed() || responseBean.getisDeactivated()) {
                    broadcaster = LocalBroadcastManager.getInstance(this);
                    Intent intent = new Intent("MyData");
                    intent.putExtra("deactivated", true);
                    intent.putExtra("message", message);
                    broadcaster.sendBroadcast(intent);
                } else if (responseBean.getApproval()) {
                    broadcaster = LocalBroadcastManager.getInstance(this);
                    Intent intent = new Intent("MyData");
                    intent.putExtra("approved", true);
                    intent.putExtra("message", message);
                    broadcaster.sendBroadcast(intent);
                } else if (responseBean.getPushmessage()) {
                    broadcaster = LocalBroadcastManager.getInstance(this);
                    Intent intent = new Intent("MyData");
                    intent.putExtra("pushMessage", true);
                    intent.putExtra("message",  responseBean.getName());
                    intent.putExtra("messageSent",responseBean.getMessage());
                    intent.putExtra("id",  responseBean.getUserId());
                    intent.putExtra("image",responseBean.getProfilePic());
                    broadcaster.sendBroadcast(intent);
                }  else {
                    if (responseBean.getAction() == 1 || responseBean.getAction() == 2) {
                        if (responseBean.getMessage().contains("match.")) {
                            if (!TextUtils.isEmpty(key)) {
                                if (new SharedPreference(this).isFirstTime().equalsIgnoreCase("true")) {
                                    startActivity(new Intent(this, ItsAMatchActivity.class)
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            .putExtra("match", responseBean));
                                }
                            }
                        }

                        broadcaster = LocalBroadcastManager.getInstance(this);
                        Intent intent = new Intent("MyData");
                        intent.putExtra("message", responseBean.getMessage());
                        intent.putExtra("match", responseBean);
                        intent.putExtra("message", message);
                        intent.putExtra("isAppForeground", true);
                        broadcaster.sendBroadcast(intent);
                    } else if (responseBean.getAction() == 3) {
                        broadcaster = LocalBroadcastManager.getInstance(this);
                        Intent intent = new Intent("MyData");
                        intent.putExtra("message", responseBean.getMessage());
                        intent.putExtra("match", responseBean);
                        broadcaster.sendBroadcast(intent);
                    }else if (responseBean.getAction() == 4) {
                        broadcaster = LocalBroadcastManager.getInstance(this);
                        Intent intent = new Intent("MyData");
                        intent.putExtra("message", responseBean.getMessage());
                        intent.putExtra("tokenApplied", true);
                        broadcaster.sendBroadcast(intent);
                    }
                }
            } else {
                if (responseBean.getisDenied()) {
                    notificationIntent = new Intent(this, HomeActivity.class);
                    notificationIntent.putExtra("rejected", true);
                    notificationIntent.putExtra("message", message);
                } else if (responseBean.getApproval()) {
                    notificationIntent = new Intent(this, HomeActivity.class);
                    notificationIntent.putExtra("approved", true);
                    notificationIntent.putExtra("message", message);
                } else if (responseBean.getIsDestroyed() || responseBean.getisDeactivated()) {
                    String deviceToken = sharePref.getDeviceToken();
                    sharePref.clearData();
                    sharePref.saveDeviceToken(deviceToken);
                    notificationIntent = new Intent(this, LoginActivity.class);
                    notificationIntent.putExtra("deactivated", true);
                    notificationIntent.putExtra("message", message);
                } else if (responseBean.getPushmessage()) {
                    message = responseBean.getName();
                    notificationIntent = new Intent(this, ChatWindow.class);
                    notificationIntent.putExtra("message", true);
                    notificationIntent.putExtra("id",responseBean.getUserId());
                    notificationIntent.putExtra("image",responseBean.getProfilePic());
                    notificationIntent.putExtra("outside",true);
                    notificationIntent.putExtra("name",responseBean.getName().split(" ")[0]);
                } else {
                    if (responseBean.getAction() == 1 || responseBean.getAction() == 2) {
                        notificationIntent = new Intent(this, HomeActivity.class)
                                .putExtra("match", responseBean);
                    } else if (responseBean.getAction() == 3) {
                        notificationIntent = new Intent(this, HomeActivity.class);
                    } else if (responseBean.getAction() == 4) {
                        notificationIntent = new Intent(this, HomeActivity.class);
                    }
                }
                sendNotification(title, message, responseBean);
            }

        } catch (JSONException ae) {
            ae.printStackTrace();
            Log.e("FCM", "setNotification: "+ae.getMessage() );
        }

    }

    /**
     *  Method to check if app is in foreground
     * @return
     */
    private boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
//        final String packageName = "app.blackgentry";
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(BuildConfig.APPLICATION_ID)) {
                return true;
            }
        }
        return false;
    }
}