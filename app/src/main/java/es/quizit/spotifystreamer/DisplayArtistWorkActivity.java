package es.quizit.spotifystreamer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DisplayArtistWorkActivity extends AppCompatActivity {

	private ArrayList<MyTrack> myTracksList;
	SpotifyApi spotifyApi;
	SpotifyService spotifyService;
	private MyTracksAdapter tracksAdapter;
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


		if(savedInstanceState == null || !savedInstanceState.containsKey("tracksList")) {
			myTracksList = new ArrayList<MyTrack>();
			FetchTracksAsynctask fetchTracksAsynctask = new FetchTracksAsynctask();
			fetchTracksAsynctask.execute(artistId);
		} else {
			myTracksList = savedInstanceState.getParcelableArrayList("tracksList");
		}

		spotifyApi = new SpotifyApi();
		spotifyService = spotifyApi.getService();

		tracksAdapter = new MyTracksAdapter(this, myTracksList);
		ListView listViewTracks = (ListView) findViewById(R.id.listViewTracks);
		listViewTracks.setAdapter(tracksAdapter);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList("tracksList", myTracksList);
		super.onSaveInstanceState(outState);
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
					myTracksList = tracksPager2MyTracksList(tracks.tracks);
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

	public ArrayList<MyTrack> tracksPager2MyTracksList(List<Track> tracks) {
		ArrayList<MyTrack> myTracks2Return = new ArrayList<MyTrack>();
		//ArrayList<Track> tracks = (ArrayList<Track>) tracks.artists.items;
		Iterator<Track> trackIterator = tracks.iterator();

		MyTrack myTrack;
		Track track;
		while (trackIterator.hasNext()) {
			track = trackIterator.next();
			String imageUrl = "";
			switch (track.album.images.size()) {
				case 0:
					break;
				case 1:
					imageUrl = track.album.images.get(0).url;
					break;
				default:
					imageUrl = track.album.images.get(track.album.images.size() - 2).url;
			}

			myTrack = new MyTrack(track.album.name, track.preview_url, track.name, imageUrl);
			myTracks2Return.add(myTrack);
		}

		return myTracks2Return;
	}
}
