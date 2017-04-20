package darena13.supertranslator;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;

import android.support.v4.widget.SimpleCursorAdapter;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;

/**
 * Created by darena13 on 18.04.2017.
 */

public class HistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    //private static final String ARG_SECTION_NUMBER = "section_number";
    private HistoryDataSource datasource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_fragment, container, false);

        getLoaderManager().initLoader(0, null, this);

        //создаем ДАО
        datasource = new HistoryDataSource(getContext());
        //создаем/открываем БД
        datasource.open();
        //получаем список всех объектов в БД
        ArrayList<HistoryItem> values = (ArrayList<HistoryItem>) datasource.getAllItems();
        //и отдаем его адаптеру
        final HistoryAdapter adapterHistory = new HistoryAdapter(getContext(),
                R.layout.history_list_item, values);

//        adapterHistory.registerDataSetObserver(new DataSetObserver() {
//            @Override
//            public void onChanged() {
//                adapterHistory.setObjects((ArrayList<HistoryItem>) datasource.getAllItems());
//            }
//        });

        ListView historyList = (ListView) view.findViewById(R.id.history_list);
        historyList.setAdapter(adapterHistory);

        return view;
    }

    void onActivityCreated() {

    }

    @Override
    public void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    public void onPause() {
        datasource.close();
        super.onPause();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri CONTACT_URI = RequestProvider.urlForItems(0);
        CursorLoader cursorLoader = new CursorLoader(getContext(), CONTACT_URI, null, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // do nothing
    }
}
