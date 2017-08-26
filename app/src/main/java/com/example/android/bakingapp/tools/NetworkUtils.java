package com.example.android.bakingapp.tools;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.android.bakingapp.data.RecipeData;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the weather servers.
 */
public final class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    private static final String DATA_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    private static final String[] mImageEndings = new String[]{
            ".gif",
            ".jpg",
            ".jpeg",
            ".png",
            ".svg"
    };

    private static final String[] mVideoEndings = new String[]{
            ".mp4",
            ".flv",
            ".f4v",
            ".ogv",
            ".avi"
    };

    public static boolean isImageFile(String urlString) {
        return hasFileEnding(urlString, mImageEndings);
    }

    public static boolean isVideoFile(String urlString) {
        return hasFileEnding(urlString, mVideoEndings);
    }

    private static boolean hasFileEnding(String urlString, String[] validEndings) {
        for (String ending : validEndings) {
            if (urlString.toLowerCase().endsWith(ending)) return true;
        }
        return false;
    }

    /**
     * @return
     */
    public static URL buildUrl() {
        Uri builtUri = Uri.parse(DATA_URL).buildUpon().build();
        return getUrl(builtUri);
    }

    /**
     * @param uri
     * @return
     */
    private static URL getUrl(Uri uri) {
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * @param url
     * @return
     * @throws IOException
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * @return
     */
    @Nullable
    public static RecipeData fetch() {
        URL movieQueryUrl = buildUrl();
        try {
            String jsonString = getResponseFromHttpUrl(movieQueryUrl);
            RecipeData collection = new RecipeData(jsonString);
            return collection;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}