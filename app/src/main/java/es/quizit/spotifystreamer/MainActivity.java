package es.quizit.spotifystreamer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

	public final static String artist_id_extra = "ARTIST_ID";
	public final static String country_code_extra = "COUNTRY_CODE";
	private final static String LOG = MainActivity.class.getName();

	TextView mTextViewArtist2Search;
	SpotifyApi spotifyApi;
	SpotifyService spotifyService;
	ListView listViewArtistsFound;
	MyArtistsAdapter artistsAdapter;
	ArrayList<MyArtist> myArtistsArrayList;
	FetchArtisAsynctask mFetchArtisAsynctask;
	private Context mContext;
	private Toast mToast;

	private int characterCount;	// To detect if an onTextChanged call comes from recreating the activity and not user input

	private String mCountryCode = "ES";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;

		spotifyApi = new SpotifyApi();
		spotifyService = spotifyApi.getService();


		mTextViewArtist2Search = (TextView) findViewById(R.id.editArtist2Search);
		mTextViewArtist2Search.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (characterCount != count) {	// if user input search Artist
					String artist = mTextViewArtist2Search.getText().toString();
					if (mFetchArtisAsynctask != null) {
						mFetchArtisAsynctask.cancel(true);
					}
					mFetchArtisAsynctask = new FetchArtisAsynctask();
					mFetchArtisAsynctask.execute(artist);
				}

				characterCount = count;
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		if(savedInstanceState == null || !savedInstanceState.containsKey("artistsList")) {
			myArtistsArrayList = new ArrayList<MyArtist>();
			characterCount = 0;
		} else {
			myArtistsArrayList = savedInstanceState.getParcelableArrayList("artistsList");
			characterCount = savedInstanceState.getInt("characterCount");
		}

		artistsAdapter = new MyArtistsAdapter(this, myArtistsArrayList);
		listViewArtistsFound = (ListView) findViewById(R.id.listViewArtists);
		listViewArtistsFound.setAdapter(artistsAdapter);

		listViewArtistsFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				MyArtist artist = (MyArtist) listViewArtistsFound.getItemAtPosition(position);
				Intent intent = new Intent(getApplicationContext(), DisplayArtistWorkActivity.class);
				intent.putExtra(artist_id_extra, artist.spotifyId);
				intent.putExtra(country_code_extra, mCountryCode);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList("artistsList", myArtistsArrayList);
		outState.putInt("characterCount", characterCount);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_country_code) {
			Dialog dialog = openDialogSettings();
			dialog.show();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private Dialog openDialogSettings() {
		final View layout = View.inflate(this, R.layout.country_code_dialog, null);
		final EditText countryCodeEditTexts = ((EditText) layout.findViewById(R.id.editTextCountryDialog));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout)
				.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String countryCodeEntered = countryCodeEditTexts.getText().toString();
						mCountryCode = countryCodeEntered;
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});

		return builder.create();
	}

	public class FetchArtisAsynctask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			spotifyService.searchArtists(params[0], new Callback<ArtistsPager>() {
				@Override
				public void success(ArtistsPager artistsPager, Response response) {
					if (artistsPager.artists.total < 1) {
						mToast = Toast.makeText(mContext, R.string.no_artists, Toast.LENGTH_SHORT);
						mToast.show();
					} else {
						if (mToast != null) mToast.cancel();
					}
					Log.d(LOG, artistsPager.toString());
					myArtistsArrayList = artistPager2MyArtistsList(artistsPager);
					artistsAdapter.clear();
					artistsAdapter.addAll(myArtistsArrayList);
					artistsAdapter.notifyDataSetChanged();
				}

				@Override
				public void failure(RetrofitError error) {
					Log.d(LOG, error.toString());
				}
			});

			return null;
		}
	}


	public ArrayList<MyArtist> artistPager2MyArtistsList(ArtistsPager artistsPager) {
		ArrayList<MyArtist> myArtists2Return = new ArrayList<MyArtist>();
		ArrayList<Artist> artists = (ArrayList<Artist>) artistsPager.artists.items;
		Iterator<Artist> artistIterator = artists.iterator();

		MyArtist myArtist;
		Artist artist;
		while (artistIterator.hasNext()) {
			artist = artistIterator.next();
			String imageUrl = "";
			switch (artist.images.size()) {
				case 0:
					break;
				case 1:
					imageUrl = artist.images.get(0).url;
					break;
				default:
					imageUrl = artist.images.get(artist.images.size() - 2).url;
			}

			myArtist = new MyArtist(artist.name, artist.id, imageUrl);
			myArtists2Return.add(myArtist);
		}

		return myArtists2Return;
	}
}
