/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package cn.stone.android.liteexplorer;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;

public class FileSortHelper {

    public enum SortMethod {
        name, size, date, type
    }

    private SortMethod mSort;

    private boolean mFileFirst;

    private HashMap<SortMethod, Comparator> mComparatorList = new HashMap<SortMethod, Comparator>();

    public FileSortHelper() {
        mSort = SortMethod.name;
        mComparatorList.put(SortMethod.name, cmpName);
        mComparatorList.put(SortMethod.size, cmpSize);
        mComparatorList.put(SortMethod.date, cmpDate);
        mComparatorList.put(SortMethod.type, cmpType);
    }

    public void setSortMethog(SortMethod s) {
        mSort = s;
    }

    public SortMethod getSortMethod() {
        return mSort;
    }

    public void setFileFirst(boolean f) {
        mFileFirst = f;
    }

    public Comparator getComparator() {
        return mComparatorList.get(mSort);
    }

    private abstract class FileComparator implements Comparator<File> {

        @Override
        public int compare(File object1, File object2) {
            if (object1.isDirectory() == object2.isDirectory()) {
                return doCompare(object1, object2);
            }

            if (mFileFirst) {
                // the files are listed before the dirs
                return (object1.isDirectory() ? 1 : -1);
            } else {
                // the dir-s are listed before the files
                return object1.isDirectory() ? -1 : 1;
            }
        }

        protected abstract int doCompare(File object1, File object2);
    }

    private Comparator cmpName = new FileComparator() {
        @Override
        public int doCompare(File object1, File object2) {
            return object1.getName().compareToIgnoreCase(object2.getName());
        }
    };

    private Comparator cmpSize = new FileComparator() {
        @Override
        public int doCompare(File object1, File object2) {
            return longToCompareInt(object1.length() - object2.length());
        }
    };

    private Comparator cmpDate = new FileComparator() {
        @Override
        public int doCompare(File object1, File object2) {
            return longToCompareInt(object2.lastModified() - object1.lastModified());
        }
    };

    private int longToCompareInt(long result) {
        return result > 0 ? 1 : (result < 0 ? -1 : 0);
    }

    private Comparator cmpType = new FileComparator() {
        @Override
        public int doCompare(File object1, File object2) {
            int result = Util.getExtFromFilename(object1.getName()).compareToIgnoreCase(
                    Util.getExtFromFilename(object2.getName()));
            if (result != 0)
                return result;

            return Util.getNameFromFilename(object1.getName()).compareToIgnoreCase(
                    Util.getNameFromFilename(object2.getName()));
        }
    };
}
