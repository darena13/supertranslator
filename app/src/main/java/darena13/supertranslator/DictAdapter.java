package darena13.supertranslator;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
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
        try {
            return rows.get(position);
        } catch (JSONException e) {
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
        //"надуваем" View макетом
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.dict_layout, parent, false);
        }
        //получаем JSONObject для этой position
        JSONObject p = getRow(position);
        // заполняем View в пункте списка данными
        try {
            String text = p.getString("text") + ", ";
            ((TextView) view.findViewById(R.id.dict_text)).setText(text);
            ((TextView) view.findViewById(R.id.dict_pos)).setText(p.getString("pos"));
            //находим и очищаем LinearLayout для вложенного списка
            LinearLayout list = (LinearLayout) view.findViewById(R.id.dict_tr);
            list.removeAllViews();
            //массив с вариантами перевода - поле tr
            JSONArray trArray = p.getJSONArray("tr");
            for (int pos = 0; pos < trArray.length(); pos++) {
                //для каждого JSONObject из массива
                JSONObject tr = trArray.getJSONObject(pos);
                //создаем View c dict_row_layout
                View row = lInflater.inflate(R.layout.dict_row_layout, parent, false);
                //и помещаем поле text в нужный TextView макета
                ((TextView) row.findViewById(R.id.dict_row_text)).setText(tr.getString("text"));
                //и добавляем нумерацию
                String num = Integer.toString(pos + 1) + ". ";
                ((TextView) row.findViewById(R.id.dict_row_num)).setText(num);
                //добавляем получившийся View к LinearLayout для вложенного списка
                list.addView(row);
            }

        } catch (JSONException e) {
            Log.e("getrowerror", e.toString());
        }
        return view;
    }

    private JSONObject getRow(int position) {
        return ((JSONObject) getItem(position));
    }

    public void update(JSONArray arr) {
        rows = arr;
        notifyDataSetChanged();
    }
}
