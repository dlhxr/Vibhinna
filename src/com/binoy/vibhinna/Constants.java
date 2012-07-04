package com.binoy.vibhinna;

import java.io.File;

import android.provider.BaseColumns;

public class Constants {
	public static final String CMD_DD = "dd if=/dev/zero of=";
	public static final String CMD_DD_CACHE = "/cache.img bs=1M count=";
	public static final String CMD_DD_DATA = "/data.img bs=1M count=";
	public static final String CMD_DD_SYSTEM = "/system.img bs=1M count=";
	public static final String CMD_MKE2FS_EXT3 = "/data/data/com.binoy.vibhinna/bin/mke2fs -F -t ext3 ";
	public static final String CMD_TUNE2FS = "/data/data/com.binoy.vibhinna/bin/tune2fs -l ";
	public static final String CMD_RESIZE2FS = "/data/data/com.binoy.vibhinna/bin/resize2fs ";
	public static final String CMD_E2FSCK = "/data/data/com.binoy.vibhinna/bin/e2fsck -fp ";
	public static final String CACHE_IMG = "/cache.img ";
	public static final String DATA_IMG = "/data.img ";
	public static final String SYSTEM_IMG = "/system.img ";
	public static final String LOG_FO_1 = "Error Code (Formating ";
	public static final String VALUE_DET = "value";
	public static final String NAME_DET = "name";
	public static final String DESC_DET = "desc";
	public static final String STATUS_DET = "status";
	public static final String PATH_DET = "path";
	public static final String FOLDER_DET = "folder";
	public static final String EMPTY = "";
	public static final String CACHE_EXT3 = "/cache.img to ext3 ) :";
	public static final String DATA_EXT3 = "/data.img to ext3 ) :";
	public static final String SYSTEM_EXT3 = "/system.img to ext3 ) :";
	public static final String CORR_S = "corrupted";
	public static final String N_A = "N/A";
	public static final String SD_PATH = "/mnt/sdcard";
	public static final String MULTI_BOOT_PATH = "/mnt/sdcard/multiboot/";
	public static final String BINARY_PATH = "/data/data/com.binoy.vibhinna/bin";
	public static final File BINARY_FOLDER = new File(BINARY_PATH);
	public static final int CACHE_SIZE = 10;
	public static final int DATA_SIZE = 20;
	public static final int SYSTEM_SIZE = 30;
	public static final int MAX_IMG_SIZE = 100;
	public static final int MIN_IMG_SIZE = 1;
	public static final String[] allColumns = { BaseColumns._ID,
			DatabaseHelper.VIRTUAL_SYSTEM_COLUMN_NAME,
			DatabaseHelper.VIRTUAL_SYSTEM_COLUMN_PATH,
			DatabaseHelper.VIRTUAL_SYSTEM_COLUMN_TYPE,
			DatabaseHelper.VIRTUAL_SYSTEM_COLUMN_DESCRIPTION };
	public static final String[] MATRIX_COLUMN_NAMES = { "_id", "name", "desc",
			"family", "folder", "status", "vdstatus", "path" };
	public static final File MBM_ROOT = new File("/mnt/sdcard/multiboot/");
	public static final String SLASH = "/";
}
