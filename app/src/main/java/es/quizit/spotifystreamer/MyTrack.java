package es.quizit.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Chezlui on 13/07/2015.
 */
public class MyTrack implements Parcelable {
	String trackName;
	String albumName;
	String urlImage;
	String previewAudioUrl;

	public MyTrack(String albumName, String previewAudioUrl, String trackName, String urlImage) {
		this.albumName = albumName;
		this.previewAudioUrl = previewAudioUrl;
		this.trackName = trackName;
		this.urlImage = urlImage;
	}

	protected MyTrack(Parcel in) {
		trackName = in.readString();
		albumName = in.readString();
		urlImage = in.readString();
		previewAudioUrl = in.readString();
	}

	public static final Creator<MyTrack> CREATOR = new Creator<MyTrack>() {
		@Override
		public MyTrack createFromParcel(Parcel in) {
			return new MyTrack(in);
		}

		@Override
		public MyTrack[] newArray(int size) {
			return new MyTrack[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(trackName);
		dest.writeString(albumName);
		dest.writeString(urlImage);
		dest.writeString(previewAudioUrl);
	}
}
