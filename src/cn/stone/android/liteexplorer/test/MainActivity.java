package cn.stone.android.liteexplorer.test;

import greendroid.app.GDActivity;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import cn.stone.android.liteexplorer.FileBrowserActivity;
import cn.stone.android.liteexplorer.R;

public class MainActivity extends GDActivity implements OnClickListener {

	private static final String TAG = "MainActivity";
	private MainService mainService;

	private Thread thread;

	public MainActivity() {
		super(ActionBar.Type.Normal);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.activity_test);
		// addLeftActionBarItem(ActionBarItem.createWithType(getGDActionBar(),
		// ActionBarItem.Type.Help), 0);
		// addLeftActionBarItem(ActionBarItem.createWithType(getGDActionBar(),
		// ActionBarItem.Type.Help), 0);
		// addLeftActionBarItem(ActionBarItem.createWithType(getGDActionBar(),
		// ActionBarItem.Type.Help), 0);
	}

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		switch (item.getItemId()) {
		case 0:
			return true;
		default:
			return super.onHandleActionBarItemClick(item, position);
		}

	}

	public int indexOfCharArray(char[] orgStr, char[] destStr) {
		int result = -1;
		for (int i = 0; i < orgStr.length
				&& orgStr.length - i >= destStr.length; i++) {
			for (int j = 0; j < destStr.length; j++) {
				if (destStr[j] == orgStr[i + j]) {
					if (j == destStr.length - 1) {
						return i;
					} else {
						continue;
					}
				} else {
					break;
				}
			}
		}
		return result;
	}

	public int fibonacci(int n) {
		if (n == 1 || n == 0) {
			return 1;
		} else {
			return fibonacci(n - 1) + fibonacci(n - 2);
		}
	}

	public void hannota(int n, char a, char b, char c) {
		if (n == 0) {
			return;
		} else {
			hannota(n - 1, a, c, b);
			android.util.Log.println(android.util.Log.INFO, "hannota",
					String.format("%s->%s", "a", "b"));// a_b
			hannota(n - 1, c, b, a);
		}
	}

	public int maxSubSum(int[] intarray, Integer start, Integer end) {

		return 0;
	}

	public void roundPrint2DimensionArray(int array[][]) {
		// right -> left
		// up -> down
		// left -> right
		// down -> up

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			Intent intent = new Intent(this, FileBrowserActivity.class);
			startActivity(intent);
			// test();
			// testThread();
			break;
		case R.id.button2:
			// stopstread();
			break;
		// Intent intent = new Intent();
		// intent.setClass(this, MainService.class);
		// bindService(intent, MainServiceConnection,
		// Context.BIND_AUTO_CREATE);
		// break;
		// case R.id.button2:
		// unbindService(MainServiceConnection);
		// break;
		// case R.id.button3:
		// mainService.MyMethod();
		// break;

		default:
			break;
		}

	}

	ServiceConnection MainServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "onServiceDisconnected");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG, "onServiceConnected");
			if (service instanceof MainService.MainServiceBinder) {

				mainService = ((MainService.MainServiceBinder) service)
						.getService();
				mainService.MyMethod();
			}

		}
	};

	public void testThread() {
		try {
			thread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

				@Override
				public void uncaughtException(Thread thread, Throwable ex) {
					ex.printStackTrace();
				}
			});
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopstread() {
		if (thread != null && thread.isAlive())
			thread.stop();
		if (thread.isAlive()) {
			Log.d(TAG, "thread is alive true");
		} else {
			Log.d(TAG, "thread is alive false");
		}
	}

}
