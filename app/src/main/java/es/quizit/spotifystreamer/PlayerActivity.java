package es.quizit.spotifystreamer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

public class PlayerActivity extends AppCompatActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);

		ArrayList<MyTrack> myTracksList = new ArrayList<>();
		int currentPlayerTrack = 0;
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			myTracksList = extras.getParcelableArrayList(ArtistTracksFragment.tracksList_extra);
			currentPlayerTrack = extras.getInt(ArtistTracksFragment.trackChosenIdx_extra);
		}

		if (savedInstanceState == null) {
			PlayerFragment playerFragment = PlayerFragment.newInstance(myTracksList,
					currentPlayerTrack);
			getFragmentManager().beginTransaction()
					.add(R.id.player_container, playerFragment)
					.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_player, menu);
		return true;
	}

}
