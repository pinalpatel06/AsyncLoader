package com.knoxpo.runtracker.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.knoxpo.runtracker.R;
import com.knoxpo.runtracker.activity.MainActivity;
import com.knoxpo.runtracker.data.RunDatabaseHelper;
import com.knoxpo.runtracker.data.SQLiteCursorLoader;
import com.knoxpo.runtracker.model.RunManager;

/**
 * Created by Tejas Sherdiwala on 12/6/2016.
 * &copy; Knoxpo
 */

public class RunListFragment extends ListFragment
                                implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int REQUEST_NEW_RUN = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(0,null,this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.run_list_options,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_new_run:
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivityForResult(i,REQUEST_NEW_RUN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_NEW_RUN){
          getLoaderManager().restartLoader(0,null,this);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(getActivity(),MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_RUN_ID,id);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new RunListCursorLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        RunCursorAdapter adapter =
                new RunCursorAdapter(getActivity(),(RunDatabaseHelper.RunCursor) data);
        setListAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        setListAdapter(null);
    }

    private static class RunCursorAdapter extends CursorAdapter{
        private RunDatabaseHelper.RunCursor mCursor;

        public RunCursorAdapter(Context context , RunDatabaseHelper.RunCursor cursor){
            super(context,cursor,0);
            mCursor = cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(android.R.layout.simple_list_item_1,parent,false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            com.knoxpo.runtracker.model.Run run = mCursor.getRun();
            TextView startDateTV = (TextView) view;
            String startDate = context.getString(com.knoxpo.runtracker.R.string.cell_text , run.getStartDate());
            startDateTV.setText(startDate);

        }
    }

    private static class RunListCursorLoader extends SQLiteCursorLoader{

        public RunListCursorLoader(Context context){
            super(context);
        }

        @Override
        protected Cursor loadCursor() {
            return RunManager.get(getContext()).queryRuns();
        }
    }
}
