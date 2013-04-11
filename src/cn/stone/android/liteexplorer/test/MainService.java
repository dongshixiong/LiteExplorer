package cn.stone.android.liteexplorer.test;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MainService extends Service {
	private final static String TAG = "MainService";
	private Binder MainServiceBinder = new MainServiceBinder();

	public class MainServiceBinder extends Binder {
		public MainService getService() {
			return MainService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return MainServiceBinder;
	}

	public void MyMethod() {
		Log.i(TAG, "BindService-->MyMethod()");
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.i(TAG, "onUnbind");
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		super.onCreate();
	}
	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "onStart");
		super.onStart(intent, startId);
	}
	@Override
	public void onRebind(Intent intent) {
		Log.i(TAG, "onRebind");
		super.onRebind(intent);
	}
//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//		Log.i(TAG, "onStartCommand");
//		return super.onStartCommand(intent, flags, startId);
//	}
}
