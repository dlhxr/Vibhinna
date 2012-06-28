package com.binoy.vibhinna;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class TasksQueueFragment extends SherlockListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final int TASK_LIST_LOADER = 0x02;
	protected ContentResolver mResolver;
	protected TasksAdapter adapter;
	protected LoaderManager mLoaderManager;
	private LocalBroadcastManager mLocalBroadcastManager;
	private BroadcastReceiver mBroadcastReceiver;
	private Context mContext;
	private boolean clearAllTasks = false;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		this.setEmptyText("No tasks are available");
		mContext = getSherlockActivity();
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(mContext);
		mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(
						VibhinnaService.ACTION_TASK_QUEUE_UPDATED)) {
					restartLoading();
				}
			}
		};

		mResolver = mContext.getContentResolver();
		mLoaderManager = getLoaderManager();
		setHasOptionsMenu(true);
		startLoading();
	}

	private void startLoading() {
		adapter.notifyDataSetChanged();
		getListView().invalidateViews();
		mLoaderManager.initLoader(TASK_LIST_LOADER, null, this);
	}

	protected void restartLoading() {
		mLoaderManager.restartLoader(TASK_LIST_LOADER, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		CursorLoader cursorLoader = new CursorLoader(mContext,
				TasksProvider.CONTENT_URI, null, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		adapter.swapCursor(arg1);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		adapter = new TasksAdapter(getSherlockActivity(), R.layout.main_row,
				null, new String[] {}, new int[] {},
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		setListAdapter(adapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(VibhinnaService.ACTION_TASK_QUEUE_UPDATED);
		mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, filter);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.tasks_options_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_clear:
			clearFinishedTasks();
			restartLoading();
			return true;
		case R.id.menu_new:
			showNewVFSDialog();
			return true;
		}
		return false;
	}

	private void clearFinishedTasks() {
		AlertDialog.Builder builder;
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.clear_tasks_dialog, null);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
			builder = new AlertDialog.Builder(mContext);
		else
			builder = new HoloAlertDialogBuilder(mContext);
		builder.setView(view).setTitle(getString(R.string.clear_tasks_title))
				.setPositiveButton(R.string.okay, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (!clearAllTasks)
							mResolver
									.delete(TasksProvider.CONTENT_URI,
											DatabaseHelper.TASK_STATUS
													+ " IS ?",
											new String[] { TasksAdapter.TASK_STATUS_FINISHED
													+ Constants.EMPTY });
						else {
							mResolver.delete(TasksProvider.CONTENT_URI, null,
									null);
							clearAllTasks = false;
						}

					}
				}).setNegativeButton(R.string.cancel, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create().show();

		final TextView message = (TextView) view
				.findViewById(R.id.clear_tasks_message);
		CheckBox checkBox = (CheckBox) view
				.findViewById(R.id.clear_all_tasks_checkbox);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					message.setText(R.string.clear_all_tasks_message);
					clearAllTasks = true;
				} else {
					message.setText(R.string.clear_tasks_message);
					clearAllTasks = false;
				}

			}
		});
		restartLoading();

	}

	/**
	 * Shows New VFS dialog according to API, which will create a new VFS
	 * 
	 * @param vibhinnaFragment
	 */
	private void showNewVFSDialog() {
		NewDialogFragmentOld.newInstance(mContext).show(getFragmentManager(),
				"new_dialog");
	}
}
