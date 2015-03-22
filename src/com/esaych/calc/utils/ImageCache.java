package com.esaych.calc.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.esaych.calc.activities.fragments.FragmentSlidePage;
import com.esaych.calc.R;

import java.io.InputStream;

/**
 * Created by Samuel Holmberg on 2/17/2015.
 * This class is designed to hold the images that have been rendered already, for faster access.
 * Also this loads the previous and next images before scroll is complete, this allows for faster, easier scrolling.
 */
public class ImageCache {
    private static LruCache<String, Bitmap> imageCache;

    public static void initCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 4;

        imageCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kb - not # of items
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static void addBitmapToMemoryCache(TextBookLoc key, Bitmap bitmap) {
        if (imageCache.get(key.toString()) == null) {
            imageCache.put(key.toString(), bitmap);
        }
    }
    
    /**
     * Gets the image for the book location - will return doge if not cached yet, or a negative problem number
     * @param bookLoc
     * @param fragment
     * @return
     */
    public static Bitmap getImage(TextBookLoc bookLoc, FragmentSlidePage fragment, ViewGroup rootView) {

        if (bookLoc.getProblem() < 0)
            return BitmapFactory.decodeResource(FragmentSlidePage.viewPage.getResources(), R.drawable.doge);

        if (imageCache.get(bookLoc.toString()) != null) {
            stockCacheImages(bookLoc, null);
            rootView.findViewById(R.id.progress_bar).setVisibility(View.GONE);
            return imageCache.get(bookLoc.toString());
        } else {
            stockCacheImages(bookLoc, fragment);
            rootView.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
            return BitmapFactory.decodeResource(FragmentSlidePage.viewPage.getResources(), R.drawable.doge);
        }
    }

    public static void resetCache() {
        imageCache.evictAll();
    }

    /**
     * Stock the images before and after bookLoc assuming they're not already cached
     * @param bookLoc
     */
    private static void stockCacheImages(TextBookLoc bookLoc, FragmentSlidePage fragment) {
        if (imageCache.get(bookLoc.toString()) == null)
            urlImage(bookLoc, fragment);
        if (imageCache.get(bookLoc.getNextProb().toString()) == null)
            urlImage(bookLoc.getNextProb(), null);
        if (imageCache.get(bookLoc.getPrevProb().toString()) == null)
            urlImage(bookLoc.getPrevProb(), null);
        System.out.println("DOWNLOADING imageCache Size: " + imageCache.snapshot().keySet().toString());
    }

    private static void urlImage(TextBookLoc bookLoc, FragmentSlidePage fragment) {
        new DownloadImageTask(bookLoc, fragment).execute(bookLoc.getURL(FragmentSlidePage.viewPage));
    }

    /**
     * Used for downloading and caching of image, and updating to fragment if fragment is not null
     */
    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        TextBookLoc bookLoc;
        FragmentSlidePage fragment;
        String error = "";

        public DownloadImageTask(TextBookLoc bookLoc, FragmentSlidePage fragment) {
            this.bookLoc = bookLoc;
            this.fragment = fragment;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap download = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                download = cropImage(BitmapFactory.decodeStream(in));
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                error = e.getMessage();
                e.printStackTrace();
                return BitmapFactory.decodeResource(FragmentSlidePage.viewPage.getResources(), R.drawable.doge);
            }
            return download;
        }

        protected void onPostExecute(Bitmap result) {
            if (error.contains("connect"))
                Toast.makeText(FragmentSlidePage.viewPage, "Internet Required", Toast.LENGTH_LONG).show();
            if (!error.equals("")) {
                System.out.println("NONFATAL ERROR: " + error);
                Toast.makeText(FragmentSlidePage.viewPage, "You have reached the last question of the section", Toast.LENGTH_LONG).show();
            }
            ImageCache.addBitmapToMemoryCache(bookLoc, result);
            if (fragment != null) {
                fragment.updateImage(result);
            }
        }

        /**
         * Clears white space around borders for more zoomed image
         * @param img
         * @return
         */
        protected Bitmap cropImage(Bitmap img) {

            int outerX=0;

            loopoutX:
            for (int x = img.getWidth()-1; x > 0; x--) {
                for (int y = img.getHeight()-1; y > 0; y--) {
                    int  clr = img.getPixel(x, y);
                    int  darkness = ((clr & 0x00ff0000) >> 16) + ((clr & 0x0000ff00) >> 8) + (clr & 0x000000ff);
                    if (darkness < 750) {
                        outerX = x;
                        break loopoutX;
                    }
                }
            }

            if (outerX < img.getWidth()-10)
                outerX += 10;

            //CROP AND SCALE
            img = Bitmap.createBitmap(img, 10, 10, outerX-10, img.getHeight()-10);

            Point size = new Point();
            FragmentSlidePage.viewPage.getWindowManager().getDefaultDisplay().getSize(size);
            int width = size.x;
            int height = (int)((float)img.getHeight()*((float)width/(float)img.getWidth()));

            return Bitmap.createScaledBitmap(img, width, height, true);
        }
    }
}
