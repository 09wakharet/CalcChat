package com.esaych.calc.activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;

import com.esaych.calc.activities.fragments.FragmentSlidePage;
import com.esaych.calc.utils.ImageCache;
import com.esaych.calc.R;
import com.esaych.calc.utils.TextBookLoc;

public class ViewPage extends FragmentActivity {

    private static int NUM_PAGES = 100;
    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_page);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        TextBookLoc storedLoc = new TextBookLoc(sharedPref.getString(getString(R.string.pref_store), "0;0;0"));
        int position = storedLoc.getProblem()/2;
        storedLoc.setProblem(1);
        mPagerAdapter.setBookLoc(storedLoc);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(position);


        // search glass listener
        ((ImageView)findViewById(R.id.search)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final View dialogView = getLayoutInflater().inflate(R.layout.chooser_dialog, null);

                NumberPicker chptNP = (NumberPicker) dialogView.findViewById(R.id.chapter_number);
                chptNP.setMaxValue(199);
                chptNP.setMinValue(1);
                chptNP.setWrapSelectorWheel(false);
                chptNP.setValue(mPagerAdapter.getBookLoc().getChapter());

                NumberPicker sectNP = (NumberPicker) dialogView.findViewById(R.id.section_number);
                sectNP.setMaxValue(199);
                sectNP.setMinValue(1);
                sectNP.setWrapSelectorWheel(false);
                sectNP.setValue(mPagerAdapter.getBookLoc().getSection());

                NumberPicker probNP = (NumberPicker) dialogView.findViewById(R.id.problem_number);

                String[] values = new String[100];
                for (int i = 0; i < values.length; i++) {
                    values[i] = Integer.toString(i * 2 + 1);
                }
                probNP.setMaxValue(values.length - 1);
                probNP.setMinValue(0);
                probNP.setDisplayedValues(values);
                probNP.setWrapSelectorWheel(false);
                probNP.setValue(mPagerAdapter.getBookLoc().getProblem()/2);

                (new AlertDialog.Builder(ViewPage.this))
                        .setView(dialogView)
                        .setInverseBackgroundForced(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                final int chapter = ((NumberPicker) dialogView.findViewById(R.id.chapter_number)).getValue();
                                final int section = ((NumberPicker) dialogView.findViewById(R.id.section_number)).getValue();
                                final int problem = ((NumberPicker) dialogView.findViewById(R.id.problem_number)).getValue(); //this is necessary because we gave the picker returns indexes the values.
                                mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
                                mPagerAdapter.setBookLoc(new TextBookLoc(chapter, section, 1));
                                mPager.setAdapter(mPagerAdapter);
                                mPager.setCurrentItem(problem);
                                ImageCache.resetCache();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .show();

            }//end onclick

        });//end search button onclicklistener

        ImageCache.initCache(); //allocate mem to the image cache
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);//The menu is currently empty, but you can always add stuff
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Currently empty, add stuff if and only if you add items to the menu first
		return super.onOptionsItemSelected(item);
	}

    @Override
    public void onPause() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.pref_store), mPagerAdapter.getBookLoc().toString());
        editor.commit();
        super.onPause();
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private TextBookLoc baseLoc;

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setBookLoc(TextBookLoc loc) {
            baseLoc = loc;
        }

        public TextBookLoc getBookLoc() {
            return baseLoc.offSet(mPager.getCurrentItem());
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentSlidePage.create(position, baseLoc.offSet(position), ViewPage.this);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
