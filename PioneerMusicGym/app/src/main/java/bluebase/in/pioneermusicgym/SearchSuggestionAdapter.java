package bluebase.in.pioneermusicgym;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


public class SearchSuggestionAdapter extends CursorAdapter {
    private LayoutInflater mLayoutInflater;

    public SearchSuggestionAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);

        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = mLayoutInflater.inflate(R.layout.search_suggestion_item, parent, false);

        return view;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor.moveToPosition(cursor.getPosition())) {
            String searchSuggestion = cursor.getString(1);
            String searchCategory = cursor.getString(2);

            TextView searchSuggestionTextView = view.findViewById(R.id.searchSuggestion);
            TextView searchCategoryTextView = view.findViewById(R.id.searchCategory);

            searchSuggestionTextView.setText(searchSuggestion);
            searchCategoryTextView.setText(searchCategory);
        }
    }
}