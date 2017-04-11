package darena13.supertranslator;

/**
 * Created by darena13 on 23.03.2017.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;

public class TranslationFormFragment extends Fragment {
    //private static final String ARG_SECTION_NUMBER = "section_number";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.translation_fragment, container, false);

        //создаем адаптер с пустым массивом
        DictAdapter dictAdapter = new DictAdapter(getContext(), new JSONArray());
        //находим ListView
        ListView dictListView = (ListView) view.findViewById(R.id.dict_list);
        //назначем ему созданный адаптер
        dictListView.setAdapter(dictAdapter);
        return view;
    }

    /** public static TranslationFormFragment newInstance(int sectionNumber) {
        TranslationFormFragment fragment = new TranslationFormFragment();
        Bundle args = new Bundle(); //A mapping from String keys to various Parcelable values.
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    } **/



}
