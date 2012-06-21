package com.vibhinna.binoy;

import java.io.File;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class NewDialogFragmentOld extends SherlockDialogFragment {

	private static final String TAG = "com.vibhinna.binoy.NewVSDialogMakerICS";
	private static Context context;
	private static VibhinnaFragment mVibhinnaFragment;
	private static ContentResolver contentResolver;

	private int iconid;
	private int CACHE_SIZE;
	private int DATA_SIZE;
	private int SYSTEM_SIZE;

	private boolean validName = true;
	private boolean validSize = false;

	private boolean nullCache;
	private boolean nullData;
	private boolean nullSystem;

	private String newvsdesc;
	private String newName;
	private File defaultFolder;

	// ProcessManager processManager;

	/**
	 * creates a new instance of NewDialogFragmentOld
	 * 
	 * @param vibhinnaFragment
	 */
	static NewDialogFragmentOld newInstance(VibhinnaFragment vibhinnaFragment) {
		NewDialogFragmentOld fragment = new NewDialogFragmentOld();
		mVibhinnaFragment = vibhinnaFragment;
		context = mVibhinnaFragment.getSherlockActivity();
		contentResolver = context.getContentResolver();
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		iconid = 1;
		CACHE_SIZE = Constants.CACHE_SIZE;
		DATA_SIZE = Constants.DATA_SIZE;
		SYSTEM_SIZE = Constants.SYSTEM_SIZE;

		validName = true;

		newName = context.getString(R.string.untitled);
		defaultFolder = new File(Constants.MULTI_BOOT_PATH + newName);
		newvsdesc = context.getString(R.string.newvfsi)
				+ defaultFolder.getPath() + ")";

		// processManager = new ProcessManager();

		LayoutInflater newVFSDialogInflater = LayoutInflater.from(context);
		final View view = newVFSDialogInflater.inflate(R.layout.new_vs_dialog,
				null);
		if (MiscMethods.getMemColor(CACHE_SIZE, DATA_SIZE, SYSTEM_SIZE) != Color.RED) {
			validSize = true;
		} else
			validSize = false;

		if (context == null) {
			Log.d(TAG, "context is null");
		}
		final AlertDialog dialog = new HoloAlertDialogBuilder(context)
				.setTitle(context.getString(R.string.createvfs))
				.setView(view)
				.setPositiveButton(context.getString(R.string.okay),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								final File newFolder = MiscMethods
										.avoidDuplicateFile(new File(
												"/mnt/sdcard/multiboot/"
														+ newName));
								ContentValues values = new ContentValues();
								values.put(
										DataBaseHelper.VIRTUAL_SYSTEM_COLUMN_NAME,
										newFolder.getName());
								values.put(
										DataBaseHelper.VIRTUAL_SYSTEM_COLUMN_PATH,
										newFolder.getPath());
								values.put(
										DataBaseHelper.VIRTUAL_SYSTEM_COLUMN_DESCRIPTION,
										newvsdesc);
								values.put(
										DataBaseHelper.VIRTUAL_SYSTEM_COLUMN_TYPE,
										iconid + "");
								contentResolver.insert(
										VibhinnaProvider.CONTENT_URI, values);
								newFolder.mkdir();
								final ProgressDialog processdialog = ProgressDialog
										.show(context,
												Constants.EMPTY,
												(context.getString(R.string.mknewfold) + newFolder
														.getPath()), true);
								final Handler handler = new Handler() {
									@Override
									public void handleMessage(Message msg) {
										switch (msg.arg1) {
										case 1: {
											processdialog.setMessage(context
													.getString(R.string.creating)
													+ newFolder.getPath()
													+ context
															.getString(R.string.cacheimg));
											return;
										}
										case 2: {
											processdialog.setMessage(context
													.getString(R.string.formating)
													+ newFolder.getPath()
													+ context
															.getString(R.string.cachext3));
											return;
										}
										case 3: {
											processdialog.setMessage(context
													.getString(R.string.creating)
													+ newFolder.getPath()
													+ context
															.getString(R.string.dataimg));
											return;
										}
										case 4: {
											processdialog.setMessage(context
													.getString(R.string.formating)
													+ newFolder.getPath()
													+ context
															.getString(R.string.dataext3));
											return;
										}
										case 5: {
											processdialog.setMessage(context
													.getString(R.string.creating)
													+ newFolder.getPath()
													+ context
															.getString(R.string.systemimg));
											return;
										}
										case 6: {
											processdialog.setMessage(context
													.getString(R.string.formating)
													+ newFolder.getPath()
													+ context
															.getString(R.string.systemext3));
											return;

										}
										default: {
											mVibhinnaFragment.restartLoading();
											processdialog.dismiss();
											return;
										}
										}
									}
								};
								Thread createVFS = new Thread() {

									@Override
									public void run() {
										String cachesize = CACHE_SIZE + "";
										String datasize = DATA_SIZE + "";
										String systemsize = SYSTEM_SIZE + "";
										String[] shellinput = { "", "", "", "",
												"" };
										shellinput[1] = newFolder.getPath();
										shellinput[0] = Constants.CMD_DD;
										shellinput[2] = "/cache.img bs=1000000 count=";

										shellinput[3] = cachesize;
										final Message m1 = new Message();
										m1.arg1 = 1;
										handler.sendMessage(m1);
										ProcessManager
												.errorStreamReader(shellinput);
										shellinput[0] = Constants.CMD_MKE2FS_EXT3;
										shellinput[2] = Constants.CACHE_IMG;
										shellinput[3] = "";
										final Message m2 = new Message();
										m2.arg1 = 2;
										handler.sendMessage(m2);
										ProcessManager.inputStreamReader(
												shellinput, 20);
										final Message m3 = new Message();
										m3.arg1 = 3;
										handler.sendMessage(m3);
										shellinput[0] = Constants.CMD_DD;
										shellinput[2] = "/data.img bs=1000000 count=";
										shellinput[3] = datasize;
										ProcessManager
												.errorStreamReader(shellinput);
										shellinput[0] = Constants.CMD_MKE2FS_EXT3;
										shellinput[2] = Constants.DATA_IMG;
										shellinput[3] = "";
										final Message m4 = new Message();
										m4.arg1 = 4;
										handler.sendMessage(m4);
										ProcessManager.inputStreamReader(
												shellinput, 20);
										shellinput[0] = Constants.CMD_DD;
										shellinput[2] = "/system.img bs=1000000 count=";

										shellinput[3] = systemsize;
										final Message m5 = new Message();
										m5.arg1 = 5;
										handler.sendMessage(m5);
										ProcessManager
												.errorStreamReader(shellinput);
										shellinput[0] = Constants.CMD_MKE2FS_EXT3;
										shellinput[2] = Constants.SYSTEM_IMG;
										shellinput[3] = "";
										final Message m6 = new Message();
										m6.arg1 = 6;
										handler.sendMessage(m6);
										ProcessManager.inputStreamReader(
												shellinput, 20);
										final Message endmessage = new Message();
										handler.sendMessage(endmessage);
									}
								};
								createVFS.start();
							}
						})
				.setNegativeButton(context.getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// Canceled.
							}
						}).show();

		// declare widgets
		final EditText evsname = (EditText) view.findViewById(R.id.vsname);
		final EditText evsdesc = (EditText) view.findViewById(R.id.vsdesc);
		final Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
		final TextView memory = (TextView) view
				.findViewById(R.id.icon_and_memory);
		final EditText cacheSizePicker = (EditText) view
				.findViewById(R.id.cache_size_editer);
		final EditText dataSizePicker = (EditText) view
				.findViewById(R.id.data_size_editer);
		final EditText systemSizePicker = (EditText) view
				.findViewById(R.id.system_size_editer);

		// set up widgets - evsdesc
		evsdesc.setText(newvsdesc);
		evsname.setText(newName);
		memory.setText(MiscMethods.getTotalSize(CACHE_SIZE, DATA_SIZE,
				SYSTEM_SIZE) + " MB");
		memory.setTextColor(MiscMethods.getMemColor(CACHE_SIZE, DATA_SIZE,
				SYSTEM_SIZE));
		cacheSizePicker.setText(CACHE_SIZE + "");
		dataSizePicker.setText(DATA_SIZE + "");
		systemSizePicker.setText(SYSTEM_SIZE + "");
		// set up spinner
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				context, R.array.icon_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(iconid);
		spinner.setAdapter(adapter);
		spinner.setSelection(iconid);

		// set up listeners - evsname
		TextWatcher nameTextWatcher = new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				String filtered_str = s.toString();
				if (filtered_str.matches(".*[\\s&/&*].*")) {
					filtered_str = filtered_str.replaceAll("[\\s&/&*]", "");
					s.clear();
					s.append(filtered_str);
					Toast.makeText(context, "Illegal character!",
							Toast.LENGTH_SHORT).show();
				}
				if (s.length() > 0) {
					validName = true;
				} else {
					validName = false;
				}
				dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(
						newDialogButtonState());
				newName = filtered_str;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

		};
		evsname.addTextChangedListener(nameTextWatcher);

		// add textwatcher to evsdesc
		TextWatcher vsDescWatcher = new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence paramCharSequence,
					int paramInt1, int paramInt2, int paramInt3) {

			}

			@Override
			public void onTextChanged(CharSequence paramCharSequence,
					int paramInt1, int paramInt2, int paramInt3) {

			}

			@Override
			public void afterTextChanged(Editable paramEditable) {
				newvsdesc = paramEditable.toString();
			}
		};
		evsdesc.addTextChangedListener(vsDescWatcher);
		// Listener for spinner
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				iconid = arg2;
				memory.setCompoundDrawablesWithIntrinsicBounds(0,
						MiscMethods.getIconRes(arg2), 0, 0);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		// add textwatcher to cacheSizePicker
		TextWatcher cacheSizeWatcher = new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() < 1) {
					CACHE_SIZE = 0;
					nullData = true;
				} else {
					int newVal = Integer.parseInt(s.toString());
					CACHE_SIZE = newVal;
					nullData = false;
				}
				dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(
						newDialogButtonState());
				memory.setText(MiscMethods.getTotalSize(CACHE_SIZE, DATA_SIZE,
						SYSTEM_SIZE) + " MB");
				memory.setTextColor(MiscMethods.getMemColor(CACHE_SIZE,
						DATA_SIZE, SYSTEM_SIZE));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}
		};
		cacheSizePicker.addTextChangedListener(cacheSizeWatcher);

		// add textwatcher to dataSizeWatcher
		TextWatcher dataSizeWatcher = new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() < 1) {
					DATA_SIZE = 0;
					nullData = true;
				} else {
					int newVal = Integer.parseInt(s.toString());
					DATA_SIZE = newVal;
					nullData = false;
				}
				dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(
						newDialogButtonState());
				memory.setText(MiscMethods.getTotalSize(CACHE_SIZE, DATA_SIZE,
						SYSTEM_SIZE) + " MB");
				memory.setTextColor(MiscMethods.getMemColor(CACHE_SIZE,
						DATA_SIZE, SYSTEM_SIZE));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		};
		dataSizePicker.addTextChangedListener(dataSizeWatcher);

		// add textwatcher to systemSizeWatcher
		TextWatcher systemSizeWatcher = new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() < 1) {
					SYSTEM_SIZE = 0;
					nullSystem = true;
				} else {
					int newVal = Integer.parseInt(s.toString());
					SYSTEM_SIZE = newVal;
					nullSystem = false;
				}
				dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(
						newDialogButtonState());
				memory.setText(MiscMethods.getTotalSize(CACHE_SIZE, DATA_SIZE,
						SYSTEM_SIZE) + " MB");
				memory.setTextColor(MiscMethods.getMemColor(CACHE_SIZE,
						DATA_SIZE, SYSTEM_SIZE));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		};
		systemSizePicker.addTextChangedListener(systemSizeWatcher);
		return dialog;
	}

	protected boolean newDialogButtonState() {
		if (validSize & validName & !nullData & !nullCache & !nullSystem)
			return true;
		else
			return false;
	}
}