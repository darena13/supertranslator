package darena13.supertranslator;

import android.widget.BaseAdapter;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static darena13.supertranslator.HistoryOpenHelper.COLUMN_TEXT;
import static darena13.supertranslator.HistoryOpenHelper.COLUMN_TRNS;

/**
 * Created by darena13 on 21.04.2017.
 */

public class CustomAdapter extends BaseAdapter {
    Cursor cursor;
    Context mContext;
    LayoutInflater inflater;

    public CustomAdapter(Context context, Cursor cursor) {
        mContext = context;
        this.cursor = cursor;
        inflater = (LayoutInflater) mContext
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Holder holder;
        cursor.moveToPosition(position);
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
        holder.text_1.setText(cursor.getString(2));
        holder.text_2.setText(cursor.getString(3));
        holder.text_lang.setText(cursor.getString(4));
        holder.image_fav.setImageResource(android.R.drawable.btn_star_big_on);

//        String imageUri = cursor.getString(cursor
//                .getColumnIndex(Phone.PHOTO_URI));
//        try {
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
//                    mContext.getContentResolver(), Uri.parse(imageUri));
//            holder.image_fav.setImageBitmap(bitmap);
////            scaleImage(holder.image_fav);
//        } catch (Exception e) {
//            holder.image_fav.setImageResource(android.R.drawable.btn_star_big_on);
////            scaleImage(holder.image_fav);
//        }
        return view;
    }

    class Holder {
        TextView text_1, text_2, text_lang;
        ImageView image_fav;
    }

//    private void scaleImage(ImageView imageView) {
//
//        Drawable drawing = imageView.getDrawable();
//        if (drawing == null) {
//        }
//        Bitmap bitmap = ((BitmapDrawable) drawing).getBitmap();
//
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        int bounding = dpToPx(50);
//
//        float xScale = ((float) bounding) / width;
//        float yScale = ((float) bounding) / height;
//        float scale = (xScale <= yScale) ? xScale : yScale;
//        Matrix matrix = new Matrix();
//        matrix.postScale(scale, scale);
//
//        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
//                matrix, true);
//        width = scaledBitmap.getWidth(); // re-use
//        height = scaledBitmap.getHeight(); // re-use
//        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
//
//        imageView.setImageDrawable(result);
//
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView
//                .getLayoutParams();
//        params.width = width;
//        params.height = height;
//        imageView.setLayoutParams(params);
//
//    }

//    private int dpToPx(int dp) {
//        float density = mContext.getResources().getDisplayMetrics().density;
//        return Math.round((float) dp * density);
//    }
}
