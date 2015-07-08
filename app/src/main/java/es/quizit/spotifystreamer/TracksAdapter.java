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
public class TracksAdapter extends ArrayAdapter<Track> {


	public TracksAdapter(Context context, ArrayList<Track> objects) {
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Track track = getItem(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artists_works, parent, false);
		}

		TextView textViewTrackName = (TextView) convertView.findViewById(R.id.textViewTrackTitle);
		textViewTrackName.setText(track.name);

		TextView textViewAlbumName = (TextView) convertView.findViewById(R.id.textViewAlbumTitle);
		textViewAlbumName.setText(track.album.name);

		ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewAlbumCover);
		int size = track.album.images.size();
		Image image;
		if (size > 1) {
			image = (Image) track.album.images.get(track.album.images.size() - 2);
			Picasso.with(getContext()).load(image.url).into(imageView);
		} else {
			Drawable noImage = ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.no_image, null);
			imageView.setImageDrawable(noImage);
		}
		return convertView;
	}


}
