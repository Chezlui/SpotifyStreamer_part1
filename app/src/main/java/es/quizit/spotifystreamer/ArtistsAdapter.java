package es.quizit.spotifystreamer;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by Chezlui on 07/07/2015.
 */
public class ArtistsAdapter extends ArrayAdapter<Artist> {


	public ArtistsAdapter(Context context, ArrayList<Artist> objects) {
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Artist artist = getItem(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artists, parent, false);
		}

		TextView textViewArtistName = (TextView) convertView.findViewById(R.id.textViewArtistName);
		textViewArtistName.setText(artist.name);
		ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewArtist);
		int size = artist.images.size();
		Image image;
		if (size > 1) {
			image = (Image) artist.images.get(artist.images.size() - 2);
			Picasso.with(getContext()).load(image.url).into(imageView);
		} else {
			Drawable noImage = ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.no_image, null);
			imageView.setImageDrawable(noImage);
		}
		return convertView;
	}


}
