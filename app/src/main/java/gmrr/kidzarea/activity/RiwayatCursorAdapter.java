package gmrr.kidzarea.activity;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Rifqi Zuliansyah on 28/04/2016.
 */
public class RiwayatCursorAdapter extends CursorAdapter {

    public RiwayatCursorAdapter(Context context, Cursor c) {
        super(context, c, false);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        TextView textView = new TextView(context);
        textView.setTextSize(18);
        textView.setPadding(16, 16, 16, 16);
        return textView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String nama = cursor.getString(cursor.getColumnIndexOrThrow("nama"));
        ((TextView) view).setText(nama);
    }

}