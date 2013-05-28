package com.opt.mobipag.data;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.*;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;
import com.opt.mobipag.R;
import com.opt.mobipag.database.TicketDataSource;
import com.opt.mobipag.gui.RevisorActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class TravelTimer extends Service {
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopForeground(true);
        }
    };
    private NotificationManager mNotificationManager;
    private Ticket t;
    private final HandlerThread thread = new HandlerThread("ServiceStartArguments");
    private ServiceHandler mServiceHandler;
    private NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        Looper mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(this, mServiceLooper);
    }

    void handleMessage() {
        TicketDataSource datasource = new TicketDataSource(this.getBaseContext());
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();

        while (Utils.checkValidity(t)) {
            int temp = t.getTempoviagem() - Utils.getTimeDiff(t);
            String s = getText(R.string.remaining) + Utils.parseTime(temp);
            mBuilder.setContentText(s);
            mNotificationManager.notify(1, mBuilder.build());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mNotificationManager.cancelAll();
        datasource.open();
        if (t.getDetails().contains(","))
            datasource.changeTicketStatusById(t.getId(), 0, -1, -1, -1);
        else
            datasource.changeTicketStatusById(t.getId(), 2, -1, -1, -1);
        datasource.close();

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);

        Toast.makeText(this.getBaseContext(), getText(R.string.expired), Toast.LENGTH_LONG).show();
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if(intent!=null){
                String email = intent.getStringExtra("USER_EMAIL");
                String title = intent.getStringExtra("title");
                int titleid = intent.getIntExtra("titleid", -1);
                int time = intent.getIntExtra("time", -1);
                String date = intent.getStringExtra("date");

                ArrayList<Validation> val = new ArrayList<Validation>();
                val.add(new Validation(0, 0, date, 0));
                t = new Ticket(titleid, title, 0.0, time, val, date);

                // For each start request, send a message to start a job and deliver the
                // start ID so we know which request we're stopping when we finish the job
                Message msg = new Message();
                msg.obj = new NotificationInfo(email, title, titleid, time, date);
                msg.arg1 = 1;
                mServiceHandler.sendMessage(msg);

                Intent resultIntent = new Intent(this, RevisorActivity.class);
                resultIntent.putExtra("USER_EMAIL", email);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(RevisorActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                Intent intent2 = new Intent("NOTIFICATION_DELETED");
                PendingIntent pendintIntent = PendingIntent.getBroadcast(this, 0, intent2, 0);
                registerReceiver(receiver, new IntentFilter("NOTIFICATION_DELETED"));

                mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText("")
                        .setContentIntent(resultPendingIntent)
                        .setDeleteIntent(pendintIntent);

                startForeground(1,mBuilder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        thread.quit();
        mNotificationManager.cancelAll();
        stopSelf();
        unregisterReceiver(receiver);
        System.exit(0);
    }

    static class ServiceHandler extends Handler {
        private final WeakReference<TravelTimer> mService;

        ServiceHandler(TravelTimer service, Looper looper) {
            super(looper);
            mService = new WeakReference<TravelTimer>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            TravelTimer service = mService.get();
            if (service != null)
                service.handleMessage();
        }
    }
}