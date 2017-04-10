package darena13.supertranslator;

import android.content.Context;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by darena13 on 10.04.2017.
 */

public class DictAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    JSONArray rows;

    DictAdapter(Context context, JSONArray data) {
        ctx = context;
        rows = data;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return rows.length();
    }

    @Override
    public Object getItem(int position) {
        Log.w("dictadapter", rows.toString());
        try {
            return rows.get(position);
        } catch (JSONException e) {
            Log.e("dictadapter", e.toString());
            return new JSONObject();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.dict_layout, parent, false);
            Log.w("getveiw", "Veiw is ready");
        }

        JSONObject p = getRow(position);
        Log.w("getrow", p.toString());

        // заполняем View в пункте списка данными
        try {
            ((TextView) view.findViewById(R.id.dict_text)).setText(p.getString("text"));
            ((TextView) view.findViewById(R.id.dict_pos)).setText(p.getString("pos"));

            //находим и очищаем LinearLayout для вложенного списка
            LinearLayout list = (LinearLayout) view.findViewById(R.id.dict_tr);
            list.removeAllViews();

            //массив с вариантами перевода
            JSONArray trs = p.getJSONArray("tr");
            for (int pos = 0; pos < trs.length(); pos++) {
                JSONObject tr = trs.getJSONObject(pos);

                //создаем View c dict_row_layout
                View row = lInflater.inflate(R.layout.dict_row_layout, parent, false);
                ((TextView) row.findViewById(R.id.dict_row_text)).setText(tr.getString("text"));

                try {
                    JSONArray syns = tr.getJSONArray("syn");
                    StringBuilder synBuilder = new StringBuilder();
                    for (int synpos = 0; synpos < syns.length(); synpos++) {
                        if (synpos != 0) synBuilder.append(", ");
                        synBuilder.append(syns.getJSONObject(synpos).getString("text"));
                    }

                    ((TextView) row.findViewById(R.id.dict_row_syn)).setText(synBuilder.toString());
                } catch (JSONException e) {
                    ((TextView) row.findViewById(R.id.dict_row_syn)).setText("");
                }
                list.addView(row);
            }

        } catch (JSONException e) {
            Log.e("getrowerror", e.toString());
        }

        return view;
    }

    JSONObject getRow(int position) {
        return ((JSONObject) getItem(position));
    }

    public void update (JSONArray arr) {
        rows = arr;
        notifyDataSetChanged();
    }
}
