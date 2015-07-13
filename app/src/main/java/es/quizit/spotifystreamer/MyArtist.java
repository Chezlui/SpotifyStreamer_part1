package es.quizit.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by Chezlui on 13/07/2015.
 */
public class MyArtist implements Parcelable {
	String name;
	String urlImage;
	String spotifyId;

	public MyArtist(String name, String spotifyId, String urlImage) {
		this.name = name;
		this.spotifyId = spotifyId;
		this.urlImage = urlImage;
	}

	protected MyArtist(Parcel in) {
		name = in.readString();
		urlImage = in.readString();
		spotifyId = in.readString();
	}

	public static final Creator<MyArtist> CREATOR = new Creator<MyArtist>() {
		@Override
		public MyArtist createFromParcel(Parcel in) {
			return new MyArtist(in);
		}

		@Override
		public MyArtist[] newArray(int size) {
			return new MyArtist[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(spotifyId);
		dest.writeString(urlImage);
	}


}
