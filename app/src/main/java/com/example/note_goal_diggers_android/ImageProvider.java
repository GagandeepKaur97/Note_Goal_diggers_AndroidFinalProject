package com.example.note_goal_diggers_android;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ImageProvider extends ContentProvider {
    public static final String LOG_TAG = ImageProvider.class.getSimpleName();


    private static final int PICTURES = 100;

    private static  final  int PICTURES_ID = 101;

    public static final String CONTENT_AUTHORITY = "com.delaroystudios.imagecamerabitmap";

    public static  final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_IMAGES = "image-path";

    public static  final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_IMAGES);


    private  static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_IMAGES, PICTURES);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_IMAGES + "/" , PICTURES_ID);
    }
    private SimpleDatabase mDatabase;
    @Override
    public boolean onCreate() {
        mDatabase = new SimpleDatabase (getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
//        SQLiteDatabase = mDatabase.getReadableDatabase();

        SQLiteDatabase database = mDatabase.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PICTURES:
                cursor = database.query(SimpleDatabase.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;

            case PICTURES_ID:
                selection = PICTURES_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(SimpleDatabase.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw  new IllegalArgumentException("Cannot query unknown URI " + uri);

        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case PICTURES:
                return insert(uri,values);
            default:
                throw  new IllegalArgumentException("Insertion not supported URI " + uri);

        }

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

}
