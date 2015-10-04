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

public class MainActivity extends AppCompatActivity
	implements ArtistSearchFragment.OnArtistSelectedListener {

	public final static String artist_id_extra = "ARTIST_ID";
	public final static String country_code_extra = "COUNTRY_CODE";
	private String mCountryCode = "ES";

	private static final String TRACKSFRAGMENT_TAG = "TRACKFRAGTAG";
	public boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
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

	@Override
	public void onArtistSelected(MyArtist artistSelected) {

		if(findViewById(R.id.listTracks_container) != null) {
			mTwoPane = true;

			ArtistTracksFragment artistTracksFragment = ArtistTracksFragment.newInstance(artistSelected.spotifyId);
			getFragmentManager().beginTransaction()
				.add(R.id.listTracks_container, artistTracksFragment, TRACKSFRAGMENT_TAG)
				.commit();
		} else {
			mTwoPane = false;
			Intent intent = new Intent(this, ArtistTracksActivity.class);
			intent.putExtra(artist_id_extra, artistSelected.spotifyId);
			intent.putExtra(country_code_extra, "ES");
			startActivity(intent);
		}

	}
}
