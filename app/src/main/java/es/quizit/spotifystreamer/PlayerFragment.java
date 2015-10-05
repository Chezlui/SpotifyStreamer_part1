package es.quizit.spotifystreamer;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.util.ArrayMap;
import android.text.AndroidCharacter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.interfaces.RSAKey;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.quizit.spotifystreamer.service.PlayerService;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;


public class PlayerFragment extends DialogFragment {
	private ArrayList<MyTrack> myTracksList;
	int currentPlayerTrack = 0;
	int currentTrackTime = 0;
	SpotifyApi spotifyApi;
	SpotifyService spotifyService;
	private Context mContext;
	private ArrayMap<String, Object> mArrayMap;

	private boolean shouldPlay = true;
	private final Handler mHandler = new Handler();

	private MediaPlayer mediaPlayer;

	private View rootView;

	// Butter niceties
	@Bind(R.id.artistNameTxtVw)
	TextView artistName;
	@Bind(R.id.albumTxtVw) TextView albumTitle;
	@Bind(R.id.albumImgVw)ImageView albumImage;
	@Bind(R.id.trackTxtVw) TextView trackTitle;
	@Bind(R.id.previousButton)Button previousButton;
	@Bind(R.id.playButton)Button playButton;
	@Bind(R.id.nextButton)Button nextButton;
	@Bind(R.id.pauseButton)Button pauseButton;
	@Bind(R.id.seekBar)
	SeekBar seekBar;
	@Bind(R.id.actualTimeTvw) TextView actualTimeTvw;
	@Bind(R.id.trackTotalTimeTvw) TextView totalTimeTvw;

	public static PlayerFragment newInstance(ArrayList<MyTrack> trackArrayList,
											 int currentPlayerTrack) {
		PlayerFragment fragment = new PlayerFragment();
		Bundle args = new Bundle();
		args.putParcelableArrayList(ArtistTracksFragment.tracksList_extra, trackArrayList);
		args.putInt(ArtistTracksFragment.trackChosenIdx_extra, currentPlayerTrack);
		fragment.setArguments(args);
		return fragment;
	}

	public PlayerFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			this.myTracksList = getArguments().getParcelableArrayList(
					ArtistTracksFragment.tracksList_extra);
			this.currentPlayerTrack = getArguments().getInt(
					ArtistTracksFragment.trackChosenIdx_extra);
		}

	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_player, container, false);
		return rootView;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return super.onCreateDialog(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ButterKnife.bind(this, rootView);

		mContext = getActivity();

		// Media Player
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				if(shouldPlay) {
					mp.start();
					pauseButton.setVisibility(View.VISIBLE);
					playButton.setVisibility(View.INVISIBLE);

					// Only when prepared, duration is well known
					if(mediaPlayer != null){
						int duration = mediaPlayer.getDuration();
						if (duration > 0) {
							seekBar.setMax(duration / 1000);
							totalTimeTvw.setText(timeMsec2TimeString(duration));
						}

					}
				}
			}
		});

		Bundle extras = getActivity().getIntent().getExtras();
		if(extras != null) {
			myTracksList = extras.getParcelableArrayList(ArtistTracksFragment.tracksList_extra);
			currentPlayerTrack = extras.getInt(ArtistTracksFragment.trackChosenIdx_extra);
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
				if (currentPlayerTrack == myTracksList.size() - 1) {
					Toast.makeText(mContext, "Tracklists's limit reached", Toast.LENGTH_SHORT).show();
				} else {
					currentPlayerTrack += 1;
					mediaPlayer.stop();
					loadSong(0);
				}
			}
		});


		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if ((mediaPlayer != null) & fromUser) {
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

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mediaPlayer != null) {
					seekBar.setProgress(mediaPlayer.getCurrentPosition() / 1000);
					actualTimeTvw.setText(timeMsec2TimeString(mediaPlayer.getCurrentPosition()));
					currentTrackTime = mediaPlayer.getCurrentPosition();
					if (!mediaPlayer.isPlaying()) {
						pauseButton.setVisibility(View.INVISIBLE);
						playButton.setVisibility(View.VISIBLE);
					}
				}
				mHandler.postDelayed(this, 250);
			}
		});

	}

	@Override
	public void onStop() {
		if ((mediaPlayer != null) && (mediaPlayer.isPlaying())) {
			mediaPlayer.stop();
		}
		super.onStop();
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
	public void onSaveInstanceState(Bundle outState) {
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
				Picasso.with(getActivity()).load(image).into(albumImage);
			} else {
				Drawable noImage = ResourcesCompat.getDrawable(this.getResources(), R.drawable.no_image, null);
				albumImage.setImageDrawable(noImage);
			}

			// Titles
			artistName.setText(myTrack.artistName);
			albumTitle.setText(myTrack.albumName);
			trackTitle.setText(myTrack.trackName);
			// Track duration moved to onPrepared Media Player
		}
	}

	private void loadSong(int mSeconds) {
		try {
			String url = myTracksList.get(currentPlayerTrack).previewAudioUrl;
			// TODO startService or Change song in service
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.reset();
			mediaPlayer.setDataSource(url);
			mediaPlayer.prepareAsync();
			if(mSeconds > 0) {
				mediaPlayer.seekTo(mSeconds);
			}
			fillScreen();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			Toast.makeText(mContext, "Problem accessing song", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
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
