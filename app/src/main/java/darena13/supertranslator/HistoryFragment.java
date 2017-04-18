package darena13.supertranslator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * Created by darena13 on 18.04.2017.
 */

public class HistoryFragment extends Fragment {
    //private static final String ARG_SECTION_NUMBER = "section_number";
    private HistoryDataSource datasource;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_fragment, container, false);

        //создаем ДАО
        datasource = new HistoryDataSource(getContext());
        //создаем/открываем БД
        datasource.open();
        //получаем список всех объектов в БД
        List<HistoryItem> values = datasource.getAllItems();
        //и отдаем его адаптеру
        ArrayAdapter<HistoryItem> adapterHistory = new ArrayAdapter<HistoryItem>(getContext(),
                android.R.layout.simple_list_item_1, values);
        ListView historyList = (ListView) view.findViewById(R.id.history_list);
        historyList.setAdapter(adapterHistory);

        return view;
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
}
