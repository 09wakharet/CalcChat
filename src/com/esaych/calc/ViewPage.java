package com.esaych.calc;

import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

//TODO later make a fragment to support a two panel layout?
public class ViewPage extends Activity {

	private String MY_URL_STRING = "";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_page);
		
		Bitmap doge = BitmapFactory.decodeResource(getResources(), R.drawable.doge);
		((ImageView)findViewById(R.id.image)).setImageBitmap(doge);
		//TODO implement code to make this image scrollable, zoomable
		//could use this http://stackoverflow.com/questions/3058164/android-scrolling-an-imageview
		
		((ImageView)findViewById(R.id.search)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				final View dialogView = getLayoutInflater().inflate(R.layout.chooser_dialog, null);
				(new AlertDialog.Builder(ViewPage.this))
				.setTitle("Get New Problem")
				.setView(dialogView)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
                        try {
                            final String chapter = ((EditText) dialogView.findViewById(R.id.chapter_number)).getText().toString();
                            final String section = ((EditText) dialogView.findViewById(R.id.section_number)).getText().toString();
                            final String problem = ((EditText) dialogView.findViewById(R.id.problem_number)).getText().toString();

                            String linkChapter = ("00" + chapter);
                            linkChapter = linkChapter.substring(linkChapter.length() - 2, linkChapter.length());
                            String linkSection = "abcdefghhijklmnopqrstuvwxyz".charAt(Integer.parseInt(section) - 1) + "";
                            String linkProblem = ("000" + problem);
                            linkProblem = linkProblem.substring(linkProblem.length() - 3, linkProblem.length());

                            MY_URL_STRING = "http://c811118.r18.cf2.rackcdn.com/se" + linkChapter + linkSection + "01" + linkProblem + ".gif";

                            new DownloadImageTask((ImageView) findViewById(R.id.image)).execute(MY_URL_STRING);
                        } catch (Exception e) {
                            Toast.makeText(ViewPage.this, "The problem you've entered does not exist.", Toast.LENGTH_LONG).show();
                            Bitmap doge = BitmapFactory.decodeResource(getResources(), R.drawable.doge);
                            ((ImageView)findViewById(R.id.image)).setImageBitmap(doge);
                        }
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {}
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

	class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
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
            if (!error.equals(""))
                Toast.makeText(ViewPage.this, "The problem you've entered does not exist.", Toast.LENGTH_LONG).show();
            Point size = new Point();
            getWindowManager().getDefaultDisplay().getSize(size);
            int width = size.x;
            int height = (int)((float)result.getHeight()*((float)width/(float)result.getWidth()));
            Bitmap bitmap = Bitmap.createScaledBitmap(result, width, height, true);
			bmImage.setImageBitmap(bitmap);
		}
	}
}