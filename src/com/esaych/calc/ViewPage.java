package com.esaych.calc;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;

import java.lang.reflect.Field;

public class ViewPage extends FragmentActivity {

    private static int NUM_PAGES = 10;
    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_page);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        // search glass listener
        ((ImageView)findViewById(R.id.search)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final View dialogView = getLayoutInflater().inflate(R.layout.chooser_dialog, null);

                NumberPicker chptNP = (NumberPicker) dialogView.findViewById(R.id.chapter_number);
                chptNP.setMaxValue(199);
                chptNP.setMinValue(1);
                chptNP.setWrapSelectorWheel(false);

                NumberPicker sectNP = (NumberPicker) dialogView.findViewById(R.id.section_number);
                sectNP.setMaxValue(199);
                sectNP.setMinValue(1);
                sectNP.setWrapSelectorWheel(false);

                NumberPicker probNP = (NumberPicker) dialogView.findViewById(R.id.problem_number);

                String[] values = new String[100];
                for(int i = 0 ; i < values.length ; i++) {
                    values[i] = Integer.toString(i*2+1);
                }
                probNP.setMaxValue(values.length-1);
                probNP.setMinValue(0);
                probNP.setDisplayedValues(values);
                probNP.setWrapSelectorWheel(false);

                (new AlertDialog.Builder(ViewPage.this))
                        .setView(dialogView)
                        .setInverseBackgroundForced(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                final int chapter = ((NumberPicker) dialogView.findViewById(R.id.chapter_number)).getValue();
                                final int section = ((NumberPicker) dialogView.findViewById(R.id.section_number)).getValue();
                                final int problem = ((NumberPicker) dialogView.findViewById(R.id.problem_number)).getValue()*2+1; //this is necessary because we gave the picker returns indexes the values.
                                mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
                                mPagerAdapter.setBookLoc(new TextBookLoc(chapter, section, problem));
                                mPager.setAdapter(mPagerAdapter);
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

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private TextBookLoc loc = new TextBookLoc("0;0;0");

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setBookLoc(TextBookLoc loc) {
            this.loc = loc;
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentSlidePage.create(position, loc.offSet(position), ViewPage.this);
        }

        @Override
        public int getCount() {
            return NUM_PAGES++;
        }
    }
}
