package cn.stone.android.liteexplorer;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
	private Modle modle = Modle.browserfile;
	private FileIconHelper mFileIconHelper;
	private FileBrowserActivity mContext;
	private FileSortHelper mFileSortHelper;
	private ICheckStateChangeLitener mCheckStateChangeLitener;
	
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

	public FileBrowserAdapter(FileBrowserActivity context) {
		mContext = context;
		mCheckStateChangeLitener=context;
		mInflater = LayoutInflater.from(context);
		mFileIconHelper = new FileIconHelper(context);
		mFileSortHelper = new FileSortHelper();
		mFileSortHelper.setFileFirst(false);
		mFileSortHelper.setSortMethog(SortMethod.name);
	}

	public void refresh(){
		setFolder(mFolder);
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

	public void setModle(Modle mdole) {
		mCheckedFiles.clear();
		this.modle = mdole;
		notifyDataSetChanged();
	}

	public void setSortType(SortMethod sortMethod) {
		mFileSortHelper.setSortMethog(sortMethod);
		Collections.sort(mFiles, mFileSortHelper.getComparator());
		notifyDataSetChanged();
	}

	public Modle getModle() {
		return modle;
	}

	public boolean isRootFolder() {
		return TextUtils.equals(mFolder.getPath(),
				FileBrowserActivity.ROOT_FOLDER.getPath());
	}

	public void doAllSelect(){
		mCheckedFiles.clear();
		mCheckedFiles.addAll(mFiles);
		notifyDataSetChanged();
		notifySelectChange();
	} 
	public void doAllUnselect(){
		mCheckedFiles.clear();
		notifyDataSetChanged();
		notifySelectChange();
	}

	public List<File> getSelected(){
		return mCheckedFiles;
	} 
	public boolean isAllSelected(){
		return mCheckedFiles.size()==mFiles.size();
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
			switch (modle) {
			case selectboth:
				itemView.checkState.setVisibility(View.VISIBLE);
				break;
			case selectfolder:
				if (file.isDirectory()) {
					itemView.checkState.setVisibility(View.VISIBLE);
				} else {
					itemView.checkState.setVisibility(View.INVISIBLE);
				}
				break;
			case selectfile:
				if (file.isDirectory()) {
					itemView.checkState.setVisibility(View.INVISIBLE);
				} else {
					itemView.checkState.setVisibility(View.VISIBLE);
				}
				break;
			case browserfile:
			case paste:
				itemView.checkState.setVisibility(View.GONE);
				break;
			}

			if (modle != Modle.browserfile) {
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
						notifySelectChange();
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

	public static enum Modle {
		selectfile, selectfolder, selectboth, browserfile,paste
	}

	private void notifySelectChange(){
		if(mCheckStateChangeLitener!=null){
			mCheckStateChangeLitener.onChange();
		}
	}
	
	public static interface ICheckStateChangeLitener{
		public void onChange();
	}
}
