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

	private ArrayList<MyTrack> myTracksList;
	int currentPlayerTrack = 0;
	int currentTrackTime = 0;
	SpotifyApi spotifyApi;
	SpotifyService spotifyService;
	private Context mContext;
	private ArrayMap<String, Object> mArrayMap;
	private MediaPlayer mediaPlayer;
	private boolean shouldPlay = true;
	private final Handler mHandler = new Handler();

	// Butter niceties
	@Bind(R.id.artistNameTxtVw) TextView artistName;
	@Bind(R.id.albumTxtVw) TextView albumTitle;
	@Bind(R.id.albumImgVw)ImageView albumImage;
	@Bind(R.id.trackTxtVw) TextView trackTitle;
	@Bind(R.id.previousButton)Button previousButton;
	@Bind(R.id.playButton)Button playButton;
	@Bind(R.id.nextButton)Button nextButton;
	@Bind(R.id.pauseButton)Button pauseButton;
	@Bind(R.id.seekBar)	SeekBar seekBar;
	@Bind(R.id.actualTimeTvw) TextView actualTimeTvw;
	@Bind(R.id.trackTotalTimeTvw) TextView totalTimeTvw;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		ButterKnife.bind(this);

		mContext = this;


		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			myTracksList = extras.getParcelableArrayList(ArtistTracksActivity.tracksList_extra);
			currentPlayerTrack = extras.getInt(ArtistTracksActivity.trackChosenIdx_extra);
		}

		if(savedInstanceState != null) {
			if (savedInstanceState.containsKey("playerTimePosition")) {
				currentTrackTime = savedInstanceState.getInt("playerTimePosition");
			}

			if (savedInstanceState.containsKey("playerCurrenTrack")) {
				currentPlayerTrack = savedInstanceState.getInt("playerCurrenTrack");
			}

			if(savedInstanceState.containsKey("playerPlaying")) {
				shouldPlay = savedInstanceState.getBoolean("playerPlaying");
			}
		}

		if (shouldPlay) {
			playButton.setVisibility(View.INVISIBLE);
			pauseButton.setVisibility(View.VISIBLE);
		} else {
			pauseButton.setVisibility(View.INVISIBLE);
			playButton.setVisibility(View.VISIBLE);
		}

		loadSong(currentTrackTime);

		playButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				playButton.setVisibility(View.INVISIBLE);
				pauseButton.setVisibility(View.VISIBLE);
				mediaPlayer.start();
				shouldPlay = true;
			}
		});

		pauseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pauseButton.setVisibility(View.INVISIBLE);
				playButton.setVisibility(View.VISIBLE);
				mediaPlayer.pause();
				shouldPlay = false;
			}
		});

		previousButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(currentPlayerTrack == 0){
					Toast.makeText(mContext, "Tracklists's limit reached", Toast.LENGTH_SHORT).show();
				} else {
					currentPlayerTrack -= 1;
					mediaPlayer.stop();
					loadSong(0);
				}
			}
		});

		nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(currentPlayerTrack == myTracksList.size()-1){
					Toast.makeText(mContext, "Tracklists's limit reached", Toast.LENGTH_SHORT).show();
				} else {
					currentPlayerTrack += 1;
					mediaPlayer.stop();
					loadSong(0);
				}
			}
		});

		int duration = mediaPlayer.getDuration();
		if (duration > 0) { seekBar.setMax(duration/1000);}
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if((mediaPlayer != null) & fromUser) {
					mediaPlayer.seekTo(progress * 1000);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		PlayerActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(mediaPlayer != null) {
					seekBar.setProgress(mediaPlayer.getCurrentPosition() / 1000);
					actualTimeTvw.setText(timeMsec2TimeString(mediaPlayer.getCurrentPosition()));
					currentTrackTime = 	mediaPlayer.getCurrentPosition();
					if(!mediaPlayer.isPlaying()) {
						pauseButton.setVisibility(View.INVISIBLE);
						playButton.setVisibility(View.VISIBLE);
					}
				}
				mHandler.postDelayed(this, 1000);
			}
		});

	}

	@Override
	protected void onPause() {
		if ((mediaPlayer != null) && (mediaPlayer.isPlaying())) {
			mediaPlayer.stop();
		}
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_player, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("playerTimePosition", currentTrackTime);
		outState.putInt("playerCurrenTrack", currentPlayerTrack);
		outState.putBoolean("playerPlaying", shouldPlay);
		super.onSaveInstanceState(outState);
	}

	private void fillScreen() {
		MyTrack myTrack = myTracksList.get(currentPlayerTrack);

		if (myTrack != null) {
			// album image
			if (myTrack.urlImageFull != null) {
				String image = myTrack.urlImageFull;
				Picasso.with(this).load(image).into(albumImage);
			} else {
				Drawable noImage = ResourcesCompat.getDrawable(this.getResources(), R.drawable.no_image, null);
				albumImage.setImageDrawable(noImage);
			}

			// Titles
			artistName.setText(myTrack.artistName);
			albumTitle.setText(myTrack.albumName);
			trackTitle.setText(myTrack.trackName);
			if(mediaPlayer != null){
				totalTimeTvw.setText(timeMsec2TimeString(mediaPlayer.getDuration()));
			}
		}
	}

	private void loadSong(int mSeconds) {
		try {
			String url = myTracksList.get(currentPlayerTrack).previewAudioUrl;
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setDataSource(url);
			mediaPlayer.prepare();
			fillScreen();

			if(shouldPlay) {
				mediaPlayer.start();
			}
			if(mSeconds > 0) {
				mediaPlayer.seekTo(mSeconds);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			Toast.makeText(mContext, "Problem accesing song", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String timeMsec2TimeString(int msec) {
		int sec = msec/1000;

		String time = sec / 60 + ":" + (
				(sec % 60 > 9) ? (sec % 60) : ("0" + sec % 60)
				);

		return time;
	}

}
