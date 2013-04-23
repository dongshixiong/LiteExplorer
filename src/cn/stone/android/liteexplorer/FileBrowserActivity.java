package cn.stone.android.liteexplorer;

import greendroid.app.GDActivity;
import greendroid.widget.ActionBar.Type;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;

import java.io.File;
import java.util.ArrayList;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.stone.android.liteexplorer.FileBrowserAdapter.ICheckStateChangeLitener;
import cn.stone.android.liteexplorer.FileBrowserAdapter.Modle;
import cn.stone.android.liteexplorer.FileOperationHelper.IOperationProgressListener;
import cn.stone.android.liteexplorer.TextInputDialog.OnFinishListener;

public class FileBrowserActivity extends GDActivity implements OnClickListener,
		ICheckStateChangeLitener {
	private final static String LOG_TAG = "";
	private ListView mListView;
	private FileBrowserAdapter mAdapter;
	private TextView mEmptyView;
	private LinearLayout mBottomActionBar;
	private LinearLayout mBottomActionBarPaste;
	private Button mCheckButton;
	private ProgressDialog progressDialog;
	private ActionType mActionType = ActionType.ACTIONBROWSER;

	private IOperationProgressListener mOperationProgressListener = new IOperationProgressListener() {

		@Override
		public void onFinish() {
			runOnUiThread(new Runnable() {
				public void run() {
					if (progressDialog != null)
						progressDialog.dismiss();
					mAdapter.refresh();
					setActionType(ActionType.ACTIONBROWSER);
				}
			});
		}

		@Override
		public void onFileChanged(String path) {
			Util.notifyFileSystemChanged(FileBrowserActivity.this, path);
		}
	};
	private FileOperationHelper mFileOperationHelper = new FileOperationHelper(
			mOperationProgressListener);

	public static final File ROOT_FOLDER = Environment
			.getExternalStorageDirectory();
	private File mCurrentFolder;

	public FileBrowserActivity() {
		super(Type.Empty);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.activity_filebrowser);
		addLeftActionBarItem(
				getGDActionBar().newActionBarItem(NormalActionBarItem.class)
						.setDrawable(R.drawable.gd_action_bar_home), 0);
		addActionBarItem(
				getGDActionBar().newActionBarItem(NormalActionBarItem.class)
						.setDrawable(R.drawable.icon_action_edit), 1);
		mCheckButton = (Button) findViewById(R.id.operation_check);

		mBottomActionBar = (LinearLayout) findViewById(R.id.bottom_actionbar);
		mBottomActionBarPaste = (LinearLayout) findViewById(R.id.bottom_actionbar_past);
		mBottomActionBarPaste.setVisibility(View.GONE);
		mBottomActionBar.setVisibility(View.GONE);
		mListView = (ListView) findViewById(R.id.listview);
		mAdapter = new FileBrowserAdapter(this);
		mListView.setAdapter(mAdapter);
		changeFolder(ROOT_FOLDER);

		mEmptyView = (TextView) findViewById(R.id.empty_view);
		mEmptyView.setVisibility(View.GONE);
		mListView.setEmptyView(mEmptyView);

		mListView.setOnItemClickListener(mItemClickListener);
	}

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		switch (item.getItemId()) {
		case 0:
			changeFolder(ROOT_FOLDER);
			return true;
		case 1:
			setActionType(mActionType == ActionType.ACTIONBROWSER ? ActionType.ACTIONPICK
					: ActionType.ACTIONBROWSER);
			onChange();
		default:
			return super.onHandleActionBarItemClick(item, position);
		}

	}

	@Override
	public void onBackPressed() {
		if (mActionType != ActionType.ACTIONBROWSER) {
			setActionType(ActionType.ACTIONBROWSER);
			return;
		}
		if (!TextUtils.equals(mCurrentFolder.getPath(), ROOT_FOLDER.getPath())) {
			changeFolder(mCurrentFolder.getParentFile());
		} else {
			super.onBackPressed();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (ActionType.ACTIONBROWSER == mActionType
					|| ActionType.ACTIONPICK == mActionType) {
				setActionType(ActionType.ACTIONBROWSER == mActionType ? ActionType.ACTIONPICK
						: ActionType.ACTIONBROWSER);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	  @Override
	  public void onStart() {
	    super.onStart();
	    EasyTracker.getInstance().activityStart(this); // Add this method.
	  }

	  @Override
	  public void onStop() {
	    super.onStop();
	    EasyTracker.getInstance().activityStop(this); // Add this method.
	  }
	private void changeFolder(File folder) {
		setTitle(folder.getName());
		mCurrentFolder = folder;
		mAdapter.setFolder(mCurrentFolder);

	}

	protected OnItemClickListener mItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			File item = (File) parent.getItemAtPosition(position);
			if (item.isDirectory()) {
				changeFolder(item);
			} else {
				if (mActionType == ActionType.ACTIONBROWSER) {
					IntentBuilder.viewFile(FileBrowserActivity.this,
							((File) mAdapter.getItem(position)).getPath());
				}
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.operation_createfolder:
			onOperationCreateFolder();
			break;
		case R.id.operation_delete:
			if (mAdapter.getSelected().size() > 0) {
				onOperationDelete();
			} else {
				Toast.makeText(this, R.string.error_nofile_selected, Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.operation_move:
			if (mAdapter.getSelected().size() > 0) {
				onOperationMove();
			} else {
				Toast.makeText(this, R.string.error_nofile_selected, Toast.LENGTH_SHORT)
				.show();

			}
			
			break;
		case R.id.operation_share:
			if (mAdapter.getSelected().size() > 0) {
				onOperationSend();
			} else {
				Toast.makeText(this, R.string.error_nofile_selected, Toast.LENGTH_SHORT)
				.show();
			}
			
			break;
		case R.id.operation_check:
			if (mAdapter.isAllSelected()) {
				mAdapter.doAllUnselect();
			} else {
				mAdapter.doAllSelect();
			}
			break;
		case R.id.operation_cancel:
			setActionType(ActionType.ACTIONBROWSER);
			break;
		case R.id.operation_past:
			if (mActionType == ActionType.ACTIONCOPY
					|| mActionType == ActionType.ACTIONMOVE) {
				showProgress(getString(R.string.operation_moving));
				mFileOperationHelper.EndMove(mCurrentFolder.getPath());
			}
		default:
			break;
		}
	}

	@Override
	public void onChange() {
		// change mCheckButton state
		setTitle(mAdapter.getSelected().size() != 0 ? getString(
				R.string.title_selectfile_num, mAdapter.getSelected().size())
				: mCurrentFolder.getName());
		if (!mAdapter.isAllSelected()) {
			mCheckButton.setText(R.string.operation_check);
			mCheckButton.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.icon_action_check, 0, 0);
		} else {
			mCheckButton.setText(R.string.operation_uncheck);
			mCheckButton.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.icon_action_uncheck, 0, 0);

		}

	}

	public void setActionType(ActionType actionType) {
		mActionType = actionType;
		switch (mActionType) {
		case ACTIONPICK:
			mAdapter.setModle(Modle.selectboth);
			mBottomActionBar.setVisibility(View.VISIBLE);
			mBottomActionBarPaste.setVisibility(View.GONE);
			break;
		case ACTIONBROWSER:
			mAdapter.setModle(Modle.browserfile);
			mBottomActionBar.setVisibility(View.GONE);
			mBottomActionBarPaste.setVisibility(View.GONE);
			break;
		case ACTIONCOPY:
			mAdapter.setModle(Modle.paste);
			mBottomActionBar.setVisibility(View.GONE);
			mBottomActionBarPaste.setVisibility(View.VISIBLE);
			break;
		case ACTIONMOVE:
			mAdapter.setModle(Modle.paste);
			mBottomActionBar.setVisibility(View.GONE);
			mBottomActionBarPaste.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	public void onOperationCreateFolder() {
		TextInputDialog dialog = new TextInputDialog(this,
				getString(R.string.title_createfolder), "",
				getString(R.string.defalut_nameofnewfile),
				new OnFinishListener() {
					@Override
					public boolean onFinish(String text) {
						FileOperationHelper fileOperationHelper = new FileOperationHelper(
								mOperationProgressListener);
						boolean result = fileOperationHelper.CreateFolder(
								mCurrentFolder.getPath(), text);
						Log.d("Rename", String.valueOf(result));
						if (result)
							mAdapter.refresh();
						return result;
					}
				});
		dialog.show();
	}

	public void onOperationDelete() {
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle(R.string.title_deletefile);
		dialog.setButton(AlertDialog.BUTTON_POSITIVE,
				getString(android.R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						showProgress(getString(R.string.operation_deleting));
						mFileOperationHelper.Delete((ArrayList<File>) mAdapter
								.getSelected());
					}
				});
		dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
				getString(android.R.string.cancel),
				(DialogInterface.OnClickListener) null);
		dialog.show();

	}

	public void onOperationCopy() {
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle(R.string.title_copyfile);
		dialog.setButton(AlertDialog.BUTTON_POSITIVE,
				getString(android.R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						showProgress(getString(R.string.operation_copying));
						mFileOperationHelper.Copy((ArrayList<File>) mAdapter
								.getSelected());
					}
				});
		dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
				getString(android.R.string.cancel),
				(DialogInterface.OnClickListener) null);
		dialog.show();

	}

	public void onOperationMove() {
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle(R.string.title_movefile);
		dialog.setButton(AlertDialog.BUTTON_POSITIVE,
				getString(android.R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						mFileOperationHelper
								.StartMove((ArrayList<File>) mAdapter
										.getSelected());
						setActionType(ActionType.ACTIONMOVE);
					}
				});
		dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
				getString(android.R.string.cancel),
				(DialogInterface.OnClickListener) null);
		dialog.show();

	}

	public void onOperationSend() {
		ArrayList<File> selectedFileList = (ArrayList<File>) mAdapter
				.getSelected();
		for (File f : selectedFileList) {
			if (f.isDirectory()) {
				AlertDialog dialog = new AlertDialog.Builder(this)
						.setMessage(R.string.error_info_cant_send_folder)
						.setPositiveButton(R.string.confirm, null).create();
				dialog.show();
				return;
			}
		}

		Intent intent = IntentBuilder.buildSendFile(selectedFileList);
		if (intent != null) {
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Log.e(LOG_TAG, "fail to view file: " + e.toString());
			}
		}
		setActionType(ActionType.ACTIONBROWSER);
	}

	private void showProgress(String msg) {
		progressDialog = new ProgressDialog(this);
		// dialog.setIcon(R.drawable.icon);
		progressDialog.setMessage(msg);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	enum ActionType {
		ACTIONBROWSER, ACTIONCOPY, ACTIONMOVE, ACTIONPICK
	}
}
