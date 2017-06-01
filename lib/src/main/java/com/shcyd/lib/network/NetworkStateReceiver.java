package com.shcyd.lib.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;

public class NetworkStateReceiver extends BroadcastReceiver {

    private static final String ANDROID_NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private static final String CUSTOME_ANDROID_NET_CHANGE_ACTION = "com.tiangong.yipai.CONNECTIVITY_CHANGE";
    private static BroadcastReceiver self;

    private boolean isNetworkAvailable = false;
    private static ArrayList<ConnectionObserver> observers = new ArrayList<ConnectionObserver>();

    public NetworkStateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ANDROID_NET_CHANGE_ACTION.equalsIgnoreCase(intent.getAction())) {
            if (!NetworkUtils.isNetworkAvailable(context)) {
                isNetworkAvailable = false;
            } else {
                isNetworkAvailable = true;
            }
            notifyObserver();
        }
    }

    private void notifyObserver() {
        if (observers.isEmpty()) {
            return;
        }
        for (ConnectionObserver co : observers) {
            if (co != null) {
                if (isNetworkAvailable) {
                    co.onNetConnected();
                } else {
                    co.onNetDisconnect();
                }
            }
        }
    }

    public static void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ANDROID_NET_CHANGE_ACTION);
        filter.addAction(CUSTOME_ANDROID_NET_CHANGE_ACTION);
        context.getApplicationContext().registerReceiver(getReceiver(), filter);
    }

    public static void removerReceiver(Context context) {
        if (self != null) {
            try {
                context.getApplicationContext().unregisterReceiver(self);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static BroadcastReceiver getReceiver() {
        if (self == null) {
            self = new NetworkStateReceiver();
        }
        return self;
    }

    public static void registerObserver(ConnectionObserver observer) {
        if (observers == null) {
            observers = new ArrayList<ConnectionObserver>();
        }
        observers.add(observer);
    }

    public static void removeObserver(ConnectionObserver observer) {
        if (observers != null && observers.contains(observer)) {
            observers.remove(observer);
        }
    }

    public boolean isNetworkAvailable() {
        return isNetworkAvailable;
    }

    public static void checkNetworkState(Context context) {
        Intent intent = new Intent();
        intent.setAction(CUSTOME_ANDROID_NET_CHANGE_ACTION);
        context.sendBroadcast(intent);
    }
}
