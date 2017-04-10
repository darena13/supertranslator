package darena13.supertranslator;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
            view = lInflater.inflate(R.layout.dict_row_layout, parent, false);
            Log.w("getveiw", "Veiw is ready");
        }

        JSONObject p = getRow(position);
        Log.w("getrow", p.toString());

        // заполняем View в пункте списка данными
        try {
            ((TextView) view.findViewById(R.id.dict_row_text)).setText(p.getString("text"));
            ((TextView) view.findViewById(R.id.dict_row_pos)).setText(p.getString("pos"));
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
