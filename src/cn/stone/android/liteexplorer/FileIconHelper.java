package cn.stone.android.liteexplorer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import cn.stone.android.liteexplorer.FileCategoryHelper.FileCategory;
import cn.stone.android.liteexplorer.FileIconLoader.IconLoadFinishListener;

public class FileIconHelper implements IconLoadFinishListener{

	private final static Map<String, Integer> sFileExtToIcons = new HashMap<String, Integer>();
    private static HashMap<ImageView, ImageView> imageFrames = new HashMap<ImageView, ImageView>();

	private FileIconLoader mIconLoader;
	
	static {
		// txt
		sFileExtToIcons.put("txt", R.drawable.icon_file_text);
		sFileExtToIcons.put("log", R.drawable.icon_file_text);
		// doc
		sFileExtToIcons.put("doc", R.drawable.icon_file_doc);
		sFileExtToIcons.put("docx", R.drawable.icon_file_doc);
		sFileExtToIcons.put("rtf", R.drawable.icon_file_doc);
		sFileExtToIcons.put("dot", R.drawable.icon_file_doc);
		// xls
		sFileExtToIcons.put("xls", R.drawable.icon_file_xls);
		sFileExtToIcons.put("xlsx", R.drawable.icon_file_xls);
		sFileExtToIcons.put("xlt", R.drawable.icon_file_xls);
		// ppt
		sFileExtToIcons.put("dps", R.drawable.icon_file_ppt);
		sFileExtToIcons.put("ppt", R.drawable.icon_file_ppt);
		sFileExtToIcons.put("pptx", R.drawable.icon_file_ppt);
		sFileExtToIcons.put("pot", R.drawable.icon_file_ppt);
		// pps
		sFileExtToIcons.put("pps", R.drawable.icon_file_pps);
		// wps
		sFileExtToIcons.put("wps", R.drawable.icon_file_wps);
		sFileExtToIcons.put("wpt", R.drawable.icon_file_wps);
		// et
		sFileExtToIcons.put("et", R.drawable.icon_file_et);
		sFileExtToIcons.put("ett", R.drawable.icon_file_et);
		// rar zip
		sFileExtToIcons.put("rar", R.drawable.icon_file_rar);
		sFileExtToIcons.put("zip", R.drawable.icon_file_rar);
		sFileExtToIcons.put("7z", R.drawable.icon_file_rar);
		// pic
		sFileExtToIcons.put("bmp", R.drawable.icon_file_picture);
		sFileExtToIcons.put("jpg", R.drawable.icon_file_picture);
		sFileExtToIcons.put("jpeg", R.drawable.icon_file_picture);
		sFileExtToIcons.put("png", R.drawable.icon_file_picture);
		sFileExtToIcons.put("gif", R.drawable.icon_file_picture);
		// video
		sFileExtToIcons.put("wmv", R.drawable.icon_file_video);
		sFileExtToIcons.put("rm", R.drawable.icon_file_video);
		sFileExtToIcons.put("rmvb", R.drawable.icon_file_video);
		sFileExtToIcons.put("avi", R.drawable.icon_file_video);
		sFileExtToIcons.put("mpg", R.drawable.icon_file_video);
		sFileExtToIcons.put("mpeg", R.drawable.icon_file_video);
		sFileExtToIcons.put("mp4", R.drawable.icon_file_video);
		sFileExtToIcons.put("3gp", R.drawable.icon_file_video);
		sFileExtToIcons.put("3gpp", R.drawable.icon_file_video);
		sFileExtToIcons.put("m4v", R.drawable.icon_file_video);
		sFileExtToIcons.put("mkv", R.drawable.icon_file_video);
		// music
		sFileExtToIcons.put("wav", R.drawable.icon_file_music);
		sFileExtToIcons.put("mp3", R.drawable.icon_file_music);
		sFileExtToIcons.put("wma", R.drawable.icon_file_music);
		sFileExtToIcons.put("mid", R.drawable.icon_file_music);
		sFileExtToIcons.put("midi", R.drawable.icon_file_music);
		sFileExtToIcons.put("amr", R.drawable.icon_file_music);
		sFileExtToIcons.put("amb", R.drawable.icon_file_music);
		sFileExtToIcons.put("acc", R.drawable.icon_file_music);
		sFileExtToIcons.put("ogg", R.drawable.icon_file_music);
		// code
		sFileExtToIcons.put("xml", R.drawable.icon_file_code);
		sFileExtToIcons.put("c", R.drawable.icon_file_code);
		sFileExtToIcons.put("java", R.drawable.icon_file_code);
		sFileExtToIcons.put("py", R.drawable.icon_file_code);
		sFileExtToIcons.put("cpp", R.drawable.icon_file_code);
		sFileExtToIcons.put("h", R.drawable.icon_file_code);
		sFileExtToIcons.put("js", R.drawable.icon_file_code);
		sFileExtToIcons.put("html", R.drawable.icon_file_code);
		sFileExtToIcons.put("vbs", R.drawable.icon_file_code);
		// dll
		sFileExtToIcons.put("dll", R.drawable.icon_file_dll);
		// pdf
		sFileExtToIcons.put("pdf", R.drawable.icon_file_pdf);
		// apk
		sFileExtToIcons.put("apk", R.drawable.icon_file_apk);

	}

    public FileIconHelper(Context context) {
        mIconLoader = new FileIconLoader(context, this);
    }
    
	public static int getFileIcon(String ext) {
		Integer i = sFileExtToIcons.get(ext.toLowerCase());
		if (i != null) {
			return i.intValue();
		} else {
			return R.drawable.icon_file_other;
		}

	}

	public static int getFileIcon(File file) {
		if (file.isFile()) {
			int nIconResId = R.drawable.icon_file_other;
			String extensionstr = Util.getExtFromFilename(file.getName());
			Integer integer = sFileExtToIcons.get(extensionstr);
			if (null != integer) {
				nIconResId = integer.intValue();
			}
			return nIconResId;
		} else {
			return R.drawable.icon_file_folder;
		}

	}

	public static Drawable getApkFileIcon(String apkPath, Context context) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkPath,
				PackageManager.GET_ACTIVITIES);
		if (info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			if (appInfo != null) {
				try {
					appInfo.publicSourceDir = apkPath;
					return pm.getApplicationIcon(appInfo);
				} catch (OutOfMemoryError e) {
				}
			}
		}
		return null;
	}

    public void setIcon(File file, ImageView fileImage, ImageView fileImageFrame) {
        String filePath = file.getPath();
        String extFromFilename = Util.getExtFromFilename(filePath);
        FileCategory fc = FileCategoryHelper.getCategoryFromPath(filePath);
        fileImageFrame.setVisibility(View.GONE);
        boolean set = false;
        int id = getFileIcon(extFromFilename);
        fileImage.setImageResource(id);

        mIconLoader.cancelRequest(fileImage);
        switch (fc) {
            case Apk:
                set = mIconLoader.loadIcon(fileImage, filePath, fc);
                break;
            case Picture:
            case Video:
                set = mIconLoader.loadIcon(fileImage, filePath, fc);
                if (set)
                    fileImageFrame.setVisibility(View.VISIBLE);
                else {
                    fileImage.setImageResource(fc == FileCategory.Picture ? R.drawable.icon_file_picture
                            : R.drawable.icon_file_video);
                    imageFrames.put(fileImage, fileImageFrame);
                    set = true;
                }
                break;
            default:
                set = true;
                break;
        }

        if (!set)
            fileImage.setImageResource(id);
    }

	@Override
	public void onIconLoadFinished(ImageView view) {
        ImageView frame = imageFrames.get(view);
        if (frame != null) {
            frame.setVisibility(View.VISIBLE);
            imageFrames.remove(view);
        }
	}
}
