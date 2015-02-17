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
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

public class FragmentSlidePage extends Fragment {
    public static final String ARG_BOOKLOC = "bookloc";
    public static final String ARG_PAGENUM = "page";
    private int mPageNumber;
    private TextBookLoc mBookLoc;
    public static ViewPage viewPage;

    /**
     * Called when getting a FragmentSlidePage to populate the FragmentView ViewPage
     *
     * @param pageNum   page number of this specific fragmentslider, usually within reasonablly small, can be negative.
     * @param bookLoc   book location this page represents
     * @param vp
     * @return  new FragmentSlidePage which can identify with the data previously added
     */
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_slide_page, container, false);

//        Bitmap doge = BitmapFactory.decodeResource(getResources(), R.drawable.doge);
//        ((ImageView)rootView.findViewById(R.id.image)).setImageBitmap(doge);


        //Populate the FragmentView
        ((ImageView) rootView.findViewById(R.id.image)).setImageBitmap(ImageCache.getImage(mBookLoc));
        //TODO implement code to make this image scrollable, zoomable
        ((TextView) rootView.findViewById(R.id.question_text)).setText("Chapter: " + mBookLoc.getChapter() + "\nSection: " + mBookLoc.getSection() + "\nProblem: " +mBookLoc.getProblem());

        return rootView;
    }

    public static void updateImage(Bitmap image) {
        ((ImageView)viewPage.findViewById(R.id.image)).setImageBitmap(image);
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }

}
