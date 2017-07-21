package com.example.android.bakingapp.tools;

import android.content.ContentResolver;
import android.util.Log;

import com.madrapps.asyncquery.AsyncQueryHandler;

public class DatabaseHandler extends AsyncQueryHandler {

    public DatabaseHandler(ContentResolver cr) {
        super(cr);
    }

    @Override
    protected void onBulkInsertComplete(int token, Object cookie, int result) {
        super.onBulkInsertComplete(token, cookie, result);
        Log.d("DatabaseHandler", "Bulk Insert Done");
    }
}