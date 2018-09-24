package rohit.com.uberappdemo.utility;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;

import java.util.Random;

import rohit.com.uberappdemo.R;

import static rohit.com.uberappdemo.utility.Constants.NOTIFICATION_CHANNEL_ID;
import static rohit.com.uberappdemo.utility.Constants.NOTIFICATION_NAME;

public class NotificationUtil {

    public static void showNotification(Context context, String title, String content) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return;

        // Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //Created channel.
            NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);

            Notification notification = new Notification.Builder(context, mChannel.getId())
                    .setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setChannelId(mChannel.getId())
                    .build();

            notificationManager.notify(new Random().nextInt(100), notification);
        } else { // Below Oreo(8.0)
            Notification notification = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .build();

            notificationManager.notify(new Random().nextInt(100), notification);
        }

    }
}
