package darena13.supertranslator;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by darena13 on 19.04.2017.
 */

public class HistoryAdapter extends ArrayAdapter<HistoryItem> {
    private ArrayList<HistoryItem> objects;

    public HistoryAdapter(Context context, int textViewResourceId, ArrayList<HistoryItem> objects) {
        super(context, textViewResourceId, objects);
        this.objects = new ArrayList<>(objects);
    }

//    public void setObjects(ArrayList<HistoryItem> objects) {
//        this.objects.clear();
//        this.objects.addAll(objects);
//    }

//    @Override
//    public void notifyDataSetChanged() {
//        super.notifyDataSetChanged();
//    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.history_list_item, null);
        }

        HistoryItem i = objects.get(position);

        if (i != null) {

            ImageView image_fav = (ImageView) v.findViewById(R.id.image_fav);
            TextView text_1 = (TextView) v.findViewById(R.id.text_1);
            TextView text_2 = (TextView) v.findViewById(R.id.text_2);
            TextView text_lang = (TextView) v.findViewById(R.id.text_lang);

            if (i.getFavorite() == 1){
                image_fav.setImageResource(android.R.drawable.btn_star_big_on);
            }
            if (text_1 != null) {
                text_1.setText(i.getText());
            }
            if (text_2 != null) {
                text_2.setText(i.getTranslation());
            }
            if (text_lang != null) {
                text_lang.setText(i.getLanguage());
            }
        }

        return v;
    }
}
