package com.esaych.calc;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;

public class FragmentSlidePage extends Fragment {
    public static final String ARG_BOOKLOC = "bookloc";
    public static final String ARG_PAGENUM = "page";
    private int mPageNumber;
    private TextBookLoc mBookLoc;
    private static ViewPage viewPage;

    public static FragmentSlidePage create(int pageNum, TextBookLoc bookLoc, ViewPage vp) {
        FragmentSlidePage fragment = new FragmentSlidePage();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGENUM, pageNum);
        args.putString(ARG_BOOKLOC, bookLoc.toString());
        fragment.setArguments(args);
        viewPage = vp;
        return fragment;
    }

    public FragmentSlidePage() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGENUM);
        mBookLoc = new TextBookLoc(getArguments().getString(ARG_BOOKLOC));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_slide_page, container, false);

//        Bitmap doge = BitmapFactory.decodeResource(getResources(), R.drawable.doge);
//        ((ImageView)rootView.findViewById(R.id.image)).setImageBitmap(doge);

        new DownloadImageTask((ImageView) rootView.findViewById(R.id.image)).execute(mBookLoc.getURLForPageNum(viewPage, mPageNumber));
        //TODO implement code to make this image scrollable, zoomable

        return rootView;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        String error = "";

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                error = e.getMessage();
                e.printStackTrace();
                return BitmapFactory.decodeResource(getResources(), R.drawable.doge);
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (error.contains("connect"))
                Toast.makeText(viewPage, "You are not connected to the internet.", Toast.LENGTH_LONG).show();
            if (!error.equals(""))
                Toast.makeText(viewPage, error, Toast.LENGTH_LONG).show();
//                Toast.makeText(viewPage, "The problem you've entered does not exist.", Toast.LENGTH_LONG).show();
            Point size = new Point();
            viewPage.getWindowManager().getDefaultDisplay().getSize(size);
            int width = size.x;
            int height = (int)((float)result.getHeight()*((float)width/(float)result.getWidth()));
            Bitmap bitmap = Bitmap.createScaledBitmap(result, width, height, true);
            bmImage.setImageBitmap(bitmap);
        }
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }

}
