package es.quizit.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class ArtistSearchFragment extends Fragment {
	private View rootView;

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

	private OnArtistSelectedListener mCallback;

	private int characterCount;	// To detect if an onTextChanged call comes from recreating the activity and not user input

	public interface OnArtistSelectedListener {
		public void onArtistSelected(MyArtist artistSelected);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mCallback = (OnArtistSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnArtistSelectedListener");
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_artist_search, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();

		spotifyApi = new SpotifyApi();
		spotifyService = spotifyApi.getService();

		mTextViewArtist2Search = (TextView) rootView.findViewById(R.id.editArtist2Search);
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

		artistsAdapter = new MyArtistsAdapter(mContext, myArtistsArrayList);
		listViewArtistsFound = (ListView) rootView.findViewById(R.id.listViewArtists);
		listViewArtistsFound.setAdapter(artistsAdapter);

		listViewArtistsFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				MyArtist artist = (MyArtist) listViewArtistsFound.getItemAtPosition(position);
				mCallback.onArtistSelected(artist);
			}
		});

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList("artistsList", myArtistsArrayList);
		outState.putInt("characterCount", characterCount);
		super.onSaveInstanceState(outState);
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
