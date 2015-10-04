package es.quizit.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Chezlui on 13/07/2015.
 */
public class MyTrack implements Parcelable {
	String artistName;
	String trackName;
	String albumName;
	String urlImage;
	String urlImageFull;
	String previewAudioUrl;

	public MyTrack(String artistName, String albumName, String previewAudioUrl, String trackName, String urlImage, String urlImageFull) {
		this.artistName = artistName;
		this.albumName = albumName;
		this.previewAudioUrl = previewAudioUrl;
		this.trackName = trackName;
		this.urlImage = urlImage;
		this.urlImageFull = urlImageFull;
	}

	protected MyTrack(Parcel in) {
		artistName = in.readString();
		trackName = in.readString();
		albumName = in.readString();
		urlImage = in.readString();
		urlImageFull = in.readString();
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
		dest.writeString(artistName);
		dest.writeString(trackName);
		dest.writeString(albumName);
		dest.writeString(urlImage);
		dest.writeString(urlImageFull);
		dest.writeString(previewAudioUrl);
	}
}
