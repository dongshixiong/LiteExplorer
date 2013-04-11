package cn.stone.android.liteexplorer;

import greendroid.app.GDActivity;
import greendroid.widget.ActionBar.Type;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.stone.android.liteexplorer.FileBrowserAdapter.CheckItem;

public class FileBrowserActivity extends GDActivity implements OnClickListener {
	private ListView mListView;
	private FileBrowserAdapter mAdapter;
	private TextView mEmptyView;
	private LinearLayout mBottomActionBar; 
	private Button mCheckButton;
	
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
		mCheckButton=(Button)findViewById(R.id.operation_check);
		mBottomActionBar=(LinearLayout)findViewById(R.id.bottom_actionbar);
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
			if (mAdapter.getCheckItem() == CheckItem.UNCHECK) {
				mAdapter.setCheckItem(CheckItem.CHECKBOTH);
				mBottomActionBar.setVisibility(View.VISIBLE);
			} else {
				mAdapter.setCheckItem(CheckItem.UNCHECK);
				mBottomActionBar.setVisibility(View.GONE);
			}
		default:
			return super.onHandleActionBarItemClick(item, position);
		}

	}

	@Override
	public void onBackPressed() {
		if (!TextUtils.equals(mCurrentFolder.getPath(), ROOT_FOLDER.getPath())) {
			changeFolder(mCurrentFolder.getParentFile());
		} else {
			super.onBackPressed();
		}

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
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.operation_createfolder:
			break;
		case R.id.operation_delete:
			break;
		case R.id.operation_check:

			break;
		case R.id.operation_move:
			break;
		case R.id.operation_share:
			break;
		default:
			break;
		}
	}
}
