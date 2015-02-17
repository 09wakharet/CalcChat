package com.esaych.calc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Samuel Holmberg on 2/17/2015.
 * This class is designed to hold the images that have been rendered already, for faster access.
 * Also this loads the previous and next images before scroll is complete, this allows for faster, easier scrolling.
 */
public class ImageCache {
    private static Map<TextBookLoc, Bitmap> cacheMap = new HashMap<TextBookLoc, Bitmap>();

    public static Bitmap getImage(TextBookLoc bookLoc) {
        stockCacheImages(bookLoc);
        if (cacheMap.containsKey(bookLoc))
            return cacheMap.get(bookLoc);
        DownloadImageTask.mustUpdateImage = true;
        return BitmapFactory.decodeResource(FragmentSlidePage.viewPage.getResources(), R.drawable.doge);
    }

    public static void setImage(TextBookLoc bookLoc, Bitmap image) {
        cacheMap.put(bookLoc, image);
    }

    /**
     * Stock the images before and after bookLoc assuming they're not already cached
     * @param bookLoc
     */
    private static void stockCacheImages(TextBookLoc bookLoc) {
        if (!cacheMap.containsKey(bookLoc))
            urlImage(bookLoc);
        if (!cacheMap.containsKey(bookLoc.getNextProb()))
            urlImage(bookLoc.getNextProb());
        if (!cacheMap.containsKey(bookLoc.getPrevProb()))
            urlImage(bookLoc.getPrevProb());
    }

    private static void urlImage(TextBookLoc bookLoc) {
        new DownloadImageTask(bookLoc).execute(bookLoc.getURL(FragmentSlidePage.viewPage));
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        static boolean mustUpdateImage = false;
        TextBookLoc bookLoc;
        String error = "";

        public DownloadImageTask(TextBookLoc bookLoc) {
            this.bookLoc = bookLoc;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap download = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                download = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                error = e.getMessage();
                e.printStackTrace();
                //return BitmapFactory.decodeResource(FragmentSlidePage.getResources(), R.drawable.doge); TODO: fix this line of code
            }
            return download;
        }

        protected void onPostExecute(Bitmap result) {
            if (error.contains("connect"))
                Toast.makeText(FragmentSlidePage.viewPage, "No internet connectivity", Toast.LENGTH_LONG).show();
            if (!error.equals(""))
                Toast.makeText(FragmentSlidePage.viewPage, error, Toast.LENGTH_LONG).show();
            Point size = new Point();
            FragmentSlidePage.viewPage.getWindowManager().getDefaultDisplay().getSize(size);
            int width = size.x;
            int height = (int)((float)result.getHeight()*((float)width/(float)result.getWidth()));
            Bitmap bitmap = Bitmap.createScaledBitmap(result, width, height, true);
            ImageCache.setImage(bookLoc, bitmap);
            if (mustUpdateImage)
                FragmentSlidePage.updateImage(bitmap);
        }
    }
}
