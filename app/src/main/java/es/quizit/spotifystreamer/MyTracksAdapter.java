package es.quizit.spotifystreamer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Chezlui on 07/07/2015.
 */
public class MyTracksAdapter extends ArrayAdapter<MyTrack> {


	public MyTracksAdapter(Context context, ArrayList<MyTrack> objects) {
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyTrack track = getItem(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artists_works, parent, false);
		}

		TextView textViewTrackName = (TextView) convertView.findViewById(R.id.textViewTrackTitle);
		textViewTrackName.setText(track.trackName);

		TextView textViewAlbumName = (TextView) convertView.findViewById(R.id.textViewAlbumTitle);
		textViewAlbumName.setText(track.albumName);

		ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewAlbumCover);
		String image;
		if (track.urlImage != "") {
			image = track.urlImage;
			Picasso.with(getContext()).load(image).into(imageView);
		} else {
			Drawable noImage = ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.no_image, null);
			imageView.setImageDrawable(noImage);
		}
		return convertView;
	}


}
