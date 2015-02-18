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

    /**
     * Gets the image for the book location - will return doge if not cached yet
     * @param bookLoc
     * @param fragment
     * @return
     */
    public static Bitmap getImage(TextBookLoc bookLoc, FragmentSlidePage fragment) {
        if (cacheMap.containsKey(bookLoc)) {
            stockCacheImages(bookLoc, null);
            return cacheMap.get(bookLoc);
        } else {
            stockCacheImages(bookLoc, fragment);
            return BitmapFactory.decodeResource(FragmentSlidePage.viewPage.getResources(), R.drawable.doge);
        }
    }

    public static void resetCache() {
        cacheMap.clear();
    }

    public static void setImage(TextBookLoc bookLoc, Bitmap image) {
        cacheMap.put(bookLoc, image);
    }

    /**
     * Stock the images before and after bookLoc assuming they're not already cached
     * @param bookLoc
     */
    private static void stockCacheImages(TextBookLoc bookLoc, FragmentSlidePage fragment) {
        if (!cacheMap.containsKey(bookLoc))
            urlImage(bookLoc, fragment);
        if (!cacheMap.containsKey(bookLoc.getNextProb()))
            urlImage(bookLoc.getNextProb(), null);
        if (!cacheMap.containsKey(bookLoc.getPrevProb()))
            urlImage(bookLoc.getPrevProb(), null);
        clearOldImages(bookLoc);
    }

    private static void clearOldImages(TextBookLoc loc) {
        for (TextBookLoc tempLoc : cacheMap.keySet()) {
            if (Math.abs(Integer.parseInt(tempLoc.getProblem()) - Integer.parseInt(loc.getProblem())) > 4) {//store up to 5 images, two on each side
                cacheMap.remove(tempLoc);
                break;
            }
        }
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
                download = BitmapFactory.decodeStream(in);
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
                Toast.makeText(FragmentSlidePage.viewPage, "No internet connectivity", Toast.LENGTH_LONG).show();
            if (!error.equals(""))
                Toast.makeText(FragmentSlidePage.viewPage, error, Toast.LENGTH_LONG).show();
            Bitmap bitmap = cropImage(result);
            ImageCache.setImage(bookLoc, bitmap);
            if (fragment != null) {
                fragment.updateImage(bitmap);
            }
        }

        /**
         * Clears white space around borders for more zoomed image
         * @param img
         * @return
         */
        protected Bitmap cropImage(Bitmap img) {

            int innerX=0;
            int innerY=0;
            int outerX=0;
            int outerY=0;

            loopinY:
            for (int y = 0; y < img.getHeight(); y+=2) {
                for (int x = 0; x < img.getWidth(); x+=2) {
                    int  clr = img.getPixel(x, y);
                    int  darkness = ((clr & 0x00ff0000) >> 16) + ((clr & 0x0000ff00) >> 8) + (clr & 0x000000ff);
                    if (darkness < 100) {
                        innerY = y;
                        break loopinY;
                    }
                }
            }

            loopinX:
            for (int x = 0; x < img.getWidth(); x+=2) {
                for (int y = 0; y < img.getHeight(); y+=2) {
                    int  clr = img.getPixel(x, y);
                    int  darkness = ((clr & 0x00ff0000) >> 16) + ((clr & 0x0000ff00) >> 8) + (clr & 0x000000ff);
                    if (darkness < 100) {
                        innerX = x;
                        break loopinX;
                    }
                }
            }

            loopoutY:
            for (int y = img.getHeight()-1; y > 0; y-=2) {
                for (int x = img.getWidth()-1; x > 0; x-=2) {
                    int  clr = img.getPixel(x, y);
                    int  darkness = ((clr & 0x00ff0000) >> 16) + ((clr & 0x0000ff00) >> 8) + (clr & 0x000000ff);
                    if (darkness < 100) {
                        outerY = y;
                        break loopoutY;
                    }
                }
            }

            loopoutX:
            for (int x = img.getWidth()-1; x > 0; x-=2) {
                for (int y = img.getHeight()-1; y > 0; y-=2) {
                    int  clr = img.getPixel(x, y);
                    int  darkness = ((clr & 0x00ff0000) >> 16) + ((clr & 0x0000ff00) >> 8) + (clr & 0x000000ff);
                    if (darkness < 100) {
                        outerX = x;
                        break loopoutX;
                    }
                }
            }
            
            if (innerX > 10)
                innerX -= 10;
            if (innerY > 10)
                innerY -= 10;
            if (outerX < img.getWidth()-10)
                outerX += 10;
            if (outerY < img.getHeight()-10)
                outerY += 10;

            img = Bitmap.createBitmap(img, innerX, innerY, outerX, outerY);


            Point size = new Point();
            FragmentSlidePage.viewPage.getWindowManager().getDefaultDisplay().getSize(size);
            int width = size.x;
            int height = (int)((float)img.getHeight()*((float)width/(float)img.getWidth()));

            return Bitmap.createScaledBitmap(img, width, height, true);
        }
    }
}
