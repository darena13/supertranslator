package darena13.supertranslator;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by darena13 on 21.04.2017.
 */

public class CustomAdapter extends BaseAdapter {
    private Cursor cursor;
    private Context context;
    private LayoutInflater inflater;
    private HistoryDataSource dataSource;
    private OnChangeHistoryListener onChangeHistoryListener;

    private View selectedItem = null;

    public CustomAdapter(Context context,
                         HistoryDataSource dataSource,
                         Cursor cursor,
                         OnChangeHistoryListener onChangeHistoryListener) {
        this.context = context;
        this.dataSource = dataSource;
        this.cursor = cursor;
        this.onChangeHistoryListener = onChangeHistoryListener;
        inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Holder holder;
        cursor.moveToPosition(position);
        final Translation item = HistoryDataSource.cursorToTranslation(cursor);
        if (view == null) {
            view = inflater.inflate(R.layout.history_list_item, parent,
                    false);
            holder = new Holder();
            holder.text_1 = (TextView) view
                    .findViewById(R.id.text_1);
            holder.text_2 = (TextView) view
                    .findViewById(R.id.text_2);
            holder.text_lang = (TextView) view
                    .findViewById(R.id.text_lang);
            holder.image_fav = (ImageView) view.findViewById(R.id.image_fav);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        holder.text_1.setText(item.getText());
        holder.text_2.setText(item.getTranslation());
        holder.text_lang.setText(item.getLanguage());
        if (item.getFavorite() == 0) {
            holder.image_fav.setImageResource(android.R.drawable.btn_star_big_off);
        } else {
            holder.image_fav.setImageResource(android.R.drawable.btn_star_big_on);
        }
        holder.image_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataSource.toggleFavorites(item);
                onChangeHistoryListener.onChange();
            }
        });
        final View finalView = view;
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteDialog(item, v);
                finalView.setSelected(true);
                return true;
            }
        });

        return view;
    }

    private void showDeleteDialog(final Translation item, final View v) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        dataSource.deleteHistoryItem(item);
                        onChangeHistoryListener.onChange();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        v.setSelected(false);
                }
            }
        };
        AlertDialog.Builder ab = new AlertDialog.Builder(context);
        ab.setMessage("Are you sure to delete \"" + item.getText() + "\"?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    class Holder {
        TextView text_1, text_2, text_lang;
        ImageView image_fav;
    }


}
