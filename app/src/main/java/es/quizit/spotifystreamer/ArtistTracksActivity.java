package es.quizit.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ArtistTracksActivity extends AppCompatActivity {

	private static final String LOG = ArtistTracksActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_tracks);

		String artistId = "";
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			artistId = extras.getString(MainActivity.artist_id_extra);

		}

		if(savedInstanceState == null) {
			ArtistTracksFragment artistTracksFragment = ArtistTracksFragment.newInstance(artistId);
			getFragmentManager().beginTransaction()
					.add(R.id.listTracks_container, artistTracksFragment)
					.commit();
		}

	}
}
