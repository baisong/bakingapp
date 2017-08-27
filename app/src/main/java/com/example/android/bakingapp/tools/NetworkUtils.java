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
 * Communicate with the recipe resources on remote server, and provide other HTTP/Web utilities.
 */
public final class NetworkUtils {

    private static final String DATA_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    // Used to determine validity of image URLs for Picasso.
    private static final String[] mImageEndings = new String[]{
            ".gif",
            ".jpg",
            ".jpeg",
            ".png",
            ".svg"
    };

    // Used to determine validity of video URLs for ExoPlayer.
    private static final String[] mVideoEndings = new String[]{
            ".mp4",
            ".flv",
            ".f4v",
            ".ogv",
            ".avi"
    };

    /**
     * Returns true if the URL file ending is recognized as an image format.
     *
     * @param urlString
     * @return
     */
    public static boolean isImageFile(String urlString) {
        return hasFileEnding(urlString, mImageEndings);
    }

    /**
     * Returns true if the URL file ending is recognized as a video format.
     *
     * @param urlString
     * @return
     */
    public static boolean isVideoFile(String urlString) {
        return hasFileEnding(urlString, mVideoEndings);
    }

    /**
     * Helper function used to determine whether a URL has one of several specified file endings.
     *
     * @param urlString
     * @param validEndings
     * @return
     */
    private static boolean hasFileEnding(String urlString, String[] validEndings) {
        for (String ending : validEndings) {
            if (urlString.toLowerCase().endsWith(ending)) return true;
        }
        return false;
    }

    /**
     * Helper function to build the static final datasource URL String into a URL object.
     *
     * @return
     */
    private static URL buildUrl() {
        URL url = null;
        try {
            Uri builtUri = Uri.parse(DATA_URL).buildUpon().build();
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * General purpose function to read the body of an HTTP response.
     *
     * @param url
     * @return
     * @throws IOException
     */
    private static String getResponseFromHttpUrl(URL url) throws IOException {
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
     * Fetch remote recipe data from datasource.
     *
     * @return
     */
    @Nullable
    public static RecipeData fetch() {
        URL movieQueryUrl = buildUrl();
        try {
            String jsonString = getResponseFromHttpUrl(movieQueryUrl);
            return new RecipeData(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}