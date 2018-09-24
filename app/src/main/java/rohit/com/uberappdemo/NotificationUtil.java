package rohit.com.uberappdemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.TaskStackBuilder;

import java.util.Random;

public class NotificationUtil {

    public static void showNotification(Context context, Class<?> cls, String title, String content) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Oreo and above
            String CHANNEL_ID = "UBER_APP_NOTIFICATION_CHANNEL";// The id of the channel.
            CharSequence name = "UBER_DEMO";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

            notificationManager.createNotificationChannel(mChannel);

            Notification notification = new Notification.Builder(context, mChannel.getId())
                    .setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                            R.mipmap.ic_launcher))
                    .setChannelId(mChannel.getId())
                    .build();

            notificationManager.notify(new Random().nextInt(100), notification);
        } else {
            Notification notification = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                            R.mipmap.ic_launcher))
                    .build();

            notificationManager.notify(new Random().nextInt(100), notification);
        }

    }
}
