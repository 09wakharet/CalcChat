package com.esaych.calc;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class ViewPage extends FragmentActivity {

    private static int NUM_PAGES = -2;
    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_page);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        ((ImageView)findViewById(R.id.search)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final View dialogView = getLayoutInflater().inflate(R.layout.chooser_dialog, null);
                (new AlertDialog.Builder(ViewPage.this))
                        .setTitle("Get New Problem")
                        .setView(dialogView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                final String chapter = ((EditText) dialogView.findViewById(R.id.chapter_number)).getText().toString();
                                final String section = ((EditText) dialogView.findViewById(R.id.section_number)).getText().toString();
                                final String problem = ((EditText) dialogView.findViewById(R.id.problem_number)).getText().toString();

                                mPagerAdapter.setBookLoc(new TextBookLoc(chapter, section, problem));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .show();

            }//end onclick

        });//end search button onclicklistener
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
            return FragmentSlidePage.create(position, loc, ViewPage.this);
        }

        @Override
        public int getCount() {
            return NUM_PAGES++;
        }
    }
}
