package es.quizit.spotifystreamer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DisplayArtistWorkActivity extends AppCompatActivity {

	private ArrayList<Track> myTracksList;
	SpotifyApi spotifyApi;
	SpotifyService spotifyService;
	private TracksAdapter tracksAdapter;
	private Context mContext;
	private Toast mToast;
	private ArrayMap<String, Object> mArrayMap;

	private static final String LOG = DisplayArtistWorkActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_artist_work);

		mContext = this;

		mArrayMap = new ArrayMap<String, Object>();
		mArrayMap.put("country", "ES");
		String artistId = "";
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			artistId = extras.getString(MainActivity.artist_id_extra);
			mArrayMap.put("country", extras.getString(MainActivity.country_code_extra));
		}


		myTracksList = new ArrayList<Track>();

		spotifyApi = new SpotifyApi();
		spotifyService = spotifyApi.getService();

		tracksAdapter = new TracksAdapter(this, myTracksList);
		ListView listViewTracks = (ListView) findViewById(R.id.listViewTracks);
		listViewTracks.setAdapter(tracksAdapter);

		FetchTracksAsynctask fetchTracksAsynctask = new FetchTracksAsynctask();
		fetchTracksAsynctask.execute(artistId);
	}


	public class FetchTracksAsynctask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			spotifyService.getArtistTopTrack(params[0], mArrayMap, new Callback<Tracks>() {
				@Override
				public void success(Tracks tracks, Response response) {
					if (tracks.tracks.size() < 1) {
						mToast = Toast.makeText(mContext, R.string.no_tracks, Toast.LENGTH_SHORT);
						mToast.show();
					} else {
						if (mToast != null) mToast.cancel();
					}
					Log.d(LOG, tracks.toString());
					myTracksList = (ArrayList<Track>) tracks.tracks;
					tracksAdapter.clear();
					tracksAdapter.addAll(myTracksList);
					tracksAdapter.notifyDataSetChanged();
				}

				@Override
				public void failure(RetrofitError error) {
					Log.d(LOG, error.toString());
				}
			});

			return null;
		}
	}
}
