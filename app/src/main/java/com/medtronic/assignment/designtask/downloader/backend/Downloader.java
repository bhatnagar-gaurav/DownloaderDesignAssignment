package com.medtronic.assignment.designtask.downloader.backend;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.medtronic.assignment.designtask.downloader.ui.ProgressResponseBody;
import com.medtronic.assignment.designtask.downloader.R;

import java.io.File;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

/**
 *
 *  The Backend module of the assignment
 *  which is used to download the file through an Intent Service.
 *  Developer Name : Gaurav Bhatnagar
 *  Created Date : Dec 03,2017
 */

public class Downloader extends IntentService {
  private static int FOREGROUND_ID=1338;
  // Customized Action to be broadcasted to the UI component of the application.
    public static final String ACTION_COMPLETE=
            "com.medtronic.assignment.designtask.backend.downloader.action.COMPLETE";

  // ---------------------------------------------------------------------------------------------
  // Fields
  // ---------------------------------------------------------------------------------------------
  private NotificationManager mgr;

  public Downloader() {
    super("Downloader");
  }

    // ---------------------------------------------------------------------------------------------
    // IntentService lifecycle method when the download process will be handled.
    // ---------------------------------------------------------------------------------------------
  @Override
  public void onHandleIntent(Intent intent) {
    mgr=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);

    try {
      String filename = null;
      if (intent.getData()!= null){
          filename=intent.getData().getLastPathSegment();
      }
      final NotificationCompat.Builder builder=
        buildForeground(filename);

      startForeground(FOREGROUND_ID, builder.build());

      File root=
              Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

      root.mkdirs();

      File output=new File(root, filename);

      if (output.exists()) {
        output.delete();
      }

      // Sending progress status through notification.
      final ProgressResponseBody.Listener progressListener=
        new ProgressResponseBody.Listener() {
          long lastUpdateTime=0L;

          @Override
          public void onProgressChange(long bytesRead,
                                       long contentLength,
                                       boolean done) {
            long now=SystemClock.uptimeMillis();

            if (now-lastUpdateTime>1000) {
              builder.setProgress((int)contentLength,
                (int)bytesRead, false);
              mgr.notify(FOREGROUND_ID, builder.build());
              lastUpdateTime=now;
            }
          }
        };

      Interceptor nightTrain=new Interceptor() {
        @Override
        public Response intercept(Chain chain)
          throws IOException {
          Response original=chain.proceed(chain.request());
          Response.Builder b=original
            .newBuilder()
            .body(
              new ProgressResponseBody(original.body(),
                  progressListener));

          return(b.build());
        }
      };

      OkHttpClient client=new OkHttpClient.Builder()
        .addNetworkInterceptor(nightTrain)
        .build();
      Request request=
        new Request.Builder().url(intent.getData().toString()).build();
      Response response=client.newCall(request).execute();
      String contentType=response.header("Content-type");
      BufferedSink sink=Okio.buffer(Okio.sink(new File(output.getPath())));

      sink.writeAll(response.body().source());
      sink.close();

      stopForeground(true);
      raiseNotification(contentType, output, null);
    }
    catch (IOException e2) {
      stopForeground(true);
      raiseNotification(null, null, e2);
    }
  }

  // Utility Function to show notification even if application is in background.
  private void raiseNotification(String contentType, File output,
                                 Exception e) {
    NotificationCompat.Builder b=new NotificationCompat.Builder(this);

    b.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
     .setWhen(System.currentTimeMillis());

    if (e == null) {
      b.setContentTitle(getString(R.string.download_complete))
       .setContentText(getString(R.string.fun))
       .setSmallIcon(android.R.drawable.stat_sys_download_done)
       .setTicker(getString(R.string.download_complete));

      Intent outbound=new Intent(Intent.ACTION_VIEW);

      outbound.setDataAndType(Uri.fromFile(output), contentType);

      LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent(ACTION_COMPLETE));

      b.setContentIntent(PendingIntent.getActivity(this, 0, outbound, 0));
    }
    else {
      b.setContentTitle(getString(R.string.exception))
       .setContentText(e.getMessage())
       .setSmallIcon(android.R.drawable.stat_notify_error)
       .setTicker(getString(R.string.exception));
    }

    int NOTIFY_ID = 1337;
    mgr.notify(NOTIFY_ID, b.build());
  }

  private NotificationCompat.Builder buildForeground(
    String filename) {
    NotificationCompat.Builder b=new NotificationCompat.Builder(this);

    b.setContentTitle(getString(R.string.downloading))
     .setContentText(filename)
     .setSmallIcon(android.R.drawable.stat_sys_download)
     .setTicker(getString(R.string.downloading))
     .setOngoing(true);

    return(b);
  }
}
