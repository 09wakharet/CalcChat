package com.esaych.calc.activities.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.esaych.calc.R;
import com.esaych.calc.activities.ViewPage;
import com.esaych.calc.utils.ImageCache;
import com.esaych.calc.utils.TextBookLoc;

import java.util.HashMap;
import java.util.Map;

public class FragmentSlidePage extends Fragment {
    public static final String ARG_BOOKLOC = "bookloc";
    public static final String ARG_PAGENUM = "page";
    public static Map<Integer, FragmentSlidePage> pages = new HashMap<Integer, FragmentSlidePage>();
    private int mPageNumber;
    private TextBookLoc mBookLoc;
    public static ViewPage viewPage;

    /**
     * Called when getting a FragmentSlidePage to populate the FragmentView ViewPage
     *
     * @param pageNum   page number of this specific fragmentslider, usually within reasonably small, can be negative.
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
        pages.put(pageNum, fragment);
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

        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_slide_page, container, false);

        //Populate the FragmentView
        ((ImageView) rootView.findViewById(R.id.image)).setImageBitmap(ImageCache.getImage(mBookLoc, this, rootView));
        ((TextView) rootView.findViewById(R.id.question_text)).setText("Section " + mBookLoc.getChapter() + "." + mBookLoc.getSection() + " Problem #" +mBookLoc.getProblem());

        return rootView;
    }

    public void updateImage(Bitmap image) {
        if (getView() == null) //for rapid flicking of fingers
            return;
        ((ImageView)getView().findViewById(R.id.image)).setImageBitmap(image);
        getView().findViewById(R.id.progress_bar).setVisibility(View.GONE);
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }

}
