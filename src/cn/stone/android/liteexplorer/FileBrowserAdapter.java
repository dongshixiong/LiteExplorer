package cn.stone.android.liteexplorer;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.stone.android.liteexplorer.FileSortHelper.SortMethod;

public class FileBrowserAdapter extends BaseAdapter {
	private File mFolder;
	private List<File> mFiles;
	private List<File> mCheckedFiles = new ArrayList<File>();
	private LayoutInflater mInflater;
	private CheckItem mCheckItem = CheckItem.UNCHECK;
	private FileIconHelper mFileIconHelper;
	private Context mContext;
	private FileSortHelper mFileSortHelper;

	private FileFilter mFileFilter = new FileFilter() {
		@Override
		public boolean accept(File file) {
			if (file.isHidden() || !file.canRead()) {
				return false;
			} else {
				return true;
			}
		}
	};

	public FileBrowserAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mFileIconHelper = new FileIconHelper(context);
		mFileSortHelper = new FileSortHelper();
		mFileSortHelper.setFileFirst(false);
		mFileSortHelper.setSortMethog(SortMethod.name);
	}

	public void setFolder(File folder) {
		if (folder.isDirectory()) {
			mCheckedFiles.clear();
			mFolder = folder;

			mFiles=Arrays.asList(mFolder.listFiles(mFileFilter));

			
			Collections.sort(mFiles, mFileSortHelper.getComparator());

			notifyDataSetChanged();
		} else {
			throw new IllegalArgumentException(String.format("\"%s\" %s",
					folder.getAbsolutePath(),
					"is not exists or not a directory!"));
		}
	}

	public void setCheckItem(CheckItem checkItem) {
		mCheckedFiles.clear();
		mCheckItem = checkItem;
		notifyDataSetChanged();
	}

	public void setSortType(SortMethod sortMethod) {
		mFileSortHelper.setSortMethog(sortMethod);
		Collections.sort(mFiles, mFileSortHelper.getComparator());
		notifyDataSetChanged();
	}

	public CheckItem getCheckItem() {
		return mCheckItem;
	}

	public boolean isRootFolder() {
		return TextUtils.equals(mFolder.getPath(),
				FileBrowserActivity.ROOT_FOLDER.getPath());
	}

	@Override
	public int getCount() {
		if (mFolder == null)
			return 0;
		if (isRootFolder()) {
			return mFiles.size();
		} else {
			return mFiles == null ? 1 : mFiles.size() + 1;
		}
	}

	@Override
	public Object getItem(int position) {
		if (!isRootFolder() && position == 0) {
			return mFolder.getParentFile();
		} else {
			return mFiles.get(isRootFolder() ? position : position - 1);
		}
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemViewHolder itemView = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.filebrowser_list_item,
					null);
			itemView = new ListItemViewHolder();
			itemView.fileIcon = (ImageView) convertView
					.findViewById(R.id.file_image);
			itemView.fileName = (TextView) convertView
					.findViewById(R.id.file_name);
			itemView.fileSize = (TextView) convertView
					.findViewById(R.id.file_size);
			itemView.modifyTime = (TextView) convertView
					.findViewById(R.id.modified_time);
			itemView.fileCount = (TextView) convertView
					.findViewById(R.id.file_count);
			itemView.imageFrame = (ImageView) convertView
					.findViewById(R.id.file_image_frame);
			itemView.checkState = (ImageView) convertView
					.findViewById(R.id.file_checkbox);
			convertView.setTag(itemView);

		} else {
			itemView = (ListItemViewHolder) convertView.getTag();
		}
		itemView.imageFrame.setVisibility(View.GONE);
		if (!isRootFolder() && position == 0) {
			itemView.fileIcon.setImageResource(R.drawable.icon_file_folder);
			itemView.fileName.setText(String.format("%s", ".."));
			itemView.fileSize.setVisibility(View.GONE);
			itemView.fileCount.setVisibility(View.GONE);
			itemView.modifyTime.setVisibility(View.GONE);
			itemView.checkState.setVisibility(View.GONE);
		} else {
			final File file = mFiles.get(isRootFolder() ? position
					: position - 1);
			// if (file.isDirectory())
			// itemView.fileIcon.setImageResource(R.drawable.icon_file_folder);
			// else
			// mFileIconHelper.setIcon(
			// FileInfo.GetFileInfo(file, mFileFilter, false),
			// itemView.fileIcon, itemView.imageFrame);
			itemView.fileIcon.setImageResource(FileIconHelper
					.getFileIcon(file));
			itemView.fileName.setText(file.getName());
			if (file.isDirectory()) {
				itemView.fileSize.setVisibility(View.GONE);
				itemView.fileCount.setVisibility(View.VISIBLE);
				itemView.modifyTime.setVisibility(View.GONE);
				File[] children = file.listFiles();
				itemView.fileCount.setText(children == null ? "" : String
						.format("(%d)", file.listFiles(mFileFilter).length));
			} else {
				itemView.fileSize.setVisibility(View.VISIBLE);
				itemView.fileCount.setVisibility(View.GONE);
				itemView.modifyTime.setVisibility(View.VISIBLE);
				itemView.fileSize.setText(Util.convertStorage(file.length()));
				itemView.modifyTime.setText(Util.formatDateString(mContext,
						file.lastModified()));
			}
			switch (mCheckItem) {
			case CHECKBOTH:
				itemView.checkState.setVisibility(View.VISIBLE);
				break;
			case CHECKFOLDER:
				if (file.isDirectory()) {
					itemView.checkState.setVisibility(View.VISIBLE);
				} else {
					itemView.checkState.setVisibility(View.INVISIBLE);
				}
				break;
			case CHECKFILE:
				if (file.isDirectory()) {
					itemView.checkState.setVisibility(View.INVISIBLE);
				} else {
					itemView.checkState.setVisibility(View.VISIBLE);
				}
				break;
			default:
				itemView.checkState.setVisibility(View.GONE);
				break;
			}

			if (mCheckItem != CheckItem.UNCHECK) {
				boolean checked = mCheckedFiles.contains(file);
				itemView.checkState
						.setImageResource(checked ? R.drawable.icon_checked
								: R.drawable.icon_unchecked);
				itemView.checkState.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						boolean checked = mCheckedFiles.contains(file);
						if (checked) {
							mCheckedFiles.remove(file);
						} else {
							mCheckedFiles.add(file);
						}
						((ImageView) v)
								.setImageResource(checked ? R.drawable.icon_unchecked
										: R.drawable.icon_checked);

					}
				});
			}
		}
		return convertView;
	}

	public class ListItemViewHolder {
		protected ImageView imageFrame;
		protected ImageView fileIcon;
		protected TextView fileName;
		protected TextView modifyTime;
		protected TextView fileSize;
		protected TextView fileCount;
		protected ImageView checkState;
	}

	public static enum CheckItem {
		CHECKFILE, CHECKFOLDER, CHECKBOTH, UNCHECK
	}
}
