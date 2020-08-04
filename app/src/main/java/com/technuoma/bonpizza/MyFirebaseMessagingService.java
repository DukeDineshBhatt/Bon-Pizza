package com.technuoma.bonpizza;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.View;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {

        SharePreferenceUtils.getInstance().saveString("token" , s);

        Log.d("toekn" , s);



        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        int c = SharePreferenceUtils.getInstance().getInteger("count");

        c++;

        SharePreferenceUtils.getInstance().saveInt("count" , c);

        Log.d("asdasd" , remoteMessage.getData().toString());

        JSONObject object;
        object = new JSONObject(remoteMessage.getData());

        try {
            handleNotification(object.getString("message") , object.getString("image"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Intent registrationComplete = new Intent("count");

        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);


        super.onMessageReceived(remoteMessage);
    }

    private void handleNotification(String message  , String image) {

        Log.d("notificationData", message);
        Log.d("notificationData", image);
        String idChannel = "southman messages";
        Intent mainIntent;

        mainIntent = new Intent(Bean.getContext(), Spalsh.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(Bean.getContext(), 0, mainIntent, 0);

        NotificationManager mNotificationManager = (NotificationManager) Bean.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        final NotificationChannel[] mChannel = new NotificationChannel[1];
        // The id of the channel.

        int importance = NotificationManager.IMPORTANCE_HIGH;

        final NotificationCompat.Builder[] builder = new NotificationCompat.Builder[1];

        if (image.length() > 0)
        {
            ImageLoader loader = ImageLoader.getInstance();
            loader.loadImage("https://technuoma.com/emartindia/admin/upload/nimage/" + image, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    builder[0] = new NotificationCompat.Builder(Bean.getContext(), idChannel);
                    builder[0].setContentTitle(Bean.getContext().getString(R.string.app_name))
                            .setSmallIcon(R.drawable.back)
                            .setContentIntent(pendingIntent)
                            .setContentText(Html.fromHtml(message))
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigPictureStyle()
                                    .bigPicture(loadedImage).setSummaryText(message))
                            .setContentText(Html.fromHtml(message));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mChannel[0] = new NotificationChannel(idChannel, Bean.getContext().getString(R.string.app_name), importance);
                        // Configure the notification channel.
                        mChannel[0].setDescription(Bean.getContext().getString(R.string.alarm_notification));
                        mChannel[0].enableLights(true);
                        mChannel[0].setLightColor(Color.RED);
                        mChannel[0].setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                        if (mNotificationManager != null) {
                            mNotificationManager.createNotificationChannel(mChannel[0]);
                        }
                    } else {
                        builder[0].setContentTitle(Bean.getContext().getString(R.string.app_name))
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setColor(ContextCompat.getColor(Bean.getContext(), R.color.transparent))
                                .setVibrate(new long[]{100, 250})
                                .setLights(Color.YELLOW, 500, 5000)
                                .setAutoCancel(true);
                    }
                    if (mNotificationManager != null) {
                        mNotificationManager.notify(1, builder[0].build());
                    }

                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });


        }
        else
        {
            builder[0] = new NotificationCompat.Builder(Bean.getContext(), idChannel);
            builder[0].setContentTitle(Bean.getContext().getString(R.string.app_name))
                    .setSmallIcon(R.drawable.back)
                    .setContentIntent(pendingIntent)
                    .setContentText(Html.fromHtml(message))
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(Html.fromHtml(message)))
                    .setContentText(Html.fromHtml(message));


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mChannel[0] = new NotificationChannel(idChannel, Bean.getContext().getString(R.string.app_name), importance);
                // Configure the notification channel.
                mChannel[0].setDescription(Bean.getContext().getString(R.string.alarm_notification));
                mChannel[0].enableLights(true);
                mChannel[0].setLightColor(Color.RED);
                mChannel[0].setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                if (mNotificationManager != null) {
                    mNotificationManager.createNotificationChannel(mChannel[0]);
                }
            } else {
                builder[0].setContentTitle(Bean.getContext().getString(R.string.app_name))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setColor(ContextCompat.getColor(Bean.getContext(), R.color.transparent))
                        .setVibrate(new long[]{100, 250})
                        .setLights(Color.YELLOW, 500, 5000)
                        .setAutoCancel(true);
            }
            if (mNotificationManager != null) {
                mNotificationManager.notify(1, builder[0].build());
            }

        }









    }

    public static boolean isAppRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_PERCEPTIBLE) {
                    return true;
                }
            }
        }
        return false;
    }
}
