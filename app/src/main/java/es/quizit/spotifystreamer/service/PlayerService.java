package es.quizit.spotifystreamer.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.View;

import java.io.IOException;

public class PlayerService extends IntentService {

	private MediaPlayer mediaPlayer;

	// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
	private static final String ACTION_INIT_PLAYER = "es.quizit.spotifystreamer.service.action.INIT_PLAYER";
	private static final String ACTION_PLAY_SONG = "es.quizit.spotifystreamer.service.action.PLAY_SONG";

	// TODO: Rename parameters
	private static final String EXTRA_URL_SONG = "es.quizit.spotifystreamer.service.extra.EXTRA_URL_SONG";

	public static void startInitPlayer(Context context) {
		Intent intent = new Intent(context, PlayerService.class);
		intent.setAction(ACTION_INIT_PLAYER);
		context.startF(intent);
	}

	/**
	 * Starts this service to perform action Baz with the given parameters. If
	 * the service is already performing a task this action will be queued.
	 *
	 * @see IntentService
	 */
	// TODO: Customize helper method
	public static void startPlaySong(Context context, String urlSong) {
		Intent intent = new Intent(context, PlayerService.class);
		intent.setAction(ACTION_PLAY_SONG);
		intent.putExtra(EXTRA_URL_SONG, urlSong);
		context.startService(intent);
	}

	public PlayerService() {
		super("PlayerService");
	}

	// TODO mandar una accion diferente para cargar canción, cambiar, avanzar, etc??
	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent != null) {
			final String action = intent.getAction();
			if (ACTION_INIT_PLAYER.equals(action)) {
				handleActionInitPlayer();
			} else if (ACTION_PLAY_SONG.equals(action)) {
				final String urlSong = intent.getStringExtra(EXTRA_URL_SONG);
				handleActionPlaySong(urlSong);
			}
		}
	}

	/**
	 * Handle action Foo in the provided background thread with the provided
	 * parameters.
	 */
	private void handleActionInitPlayer() {
		// Media Player
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
//				if(shouldPlay) {
					mp.start();
//					pauseButton.setVisibility(View.VISIBLE);
//					playButton.setVisibility(View.INVISIBLE);

					// Only when prepared, duration is well known
//					if(mediaPlayer != null){
//						int duration = mediaPlayer.getDuration();
//						if (duration > 0) {
//							seekBar.setMax(duration / 1000);
//							totalTimeTvw.setText(timeMsec2TimeString(duration));
//						}
//
//					}
//				}
			}
		});

	}

	/**
	 * Handle action Baz in the provided background thread with the provided
	 * parameters.
	 */
	private void handleActionPlaySong(String urlSong) {
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.reset();
		try {
			mediaPlayer.setDataSource(urlSong);
			mediaPlayer.prepareAsync();
//			if(mSeconds > 0) {
//				mediaPlayer.seekTo(mSeconds);
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
