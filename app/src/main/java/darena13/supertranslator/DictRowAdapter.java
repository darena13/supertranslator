package darena13.supertranslator;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by darena13 on 10.04.2017.
 */

public class DictRowAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    JSONArray rows;

    DictRowAdapter(Context context, JSONArray data) {
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
            Log.w("dictrowadapter", rows.get(position).toString());
            return rows.get(position);
        } catch (JSONException e) {
            Log.e("dictrowadapter", e.toString());
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
            view = lInflater.inflate(R.layout.dict_row_layout, parent, false);
        }

        JSONObject p = getRow(position);

        // заполняем View в пункте списка данными
        try {
            ((TextView) view.findViewById(R.id.dict_row_text)).setText(p.getString("text"));
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
