package com.akifbatur.whichway;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DialogShowFavorites extends DialogFragment{
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.favorites)
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
					}
				}).setItems(HelloAndroidActivity.favorites, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
						// TODO nedense ekrandaki koordinat update olmuyor
						Favorite chosen = HelloAndroidActivity.favoriteList.get(id);
						HelloAndroidActivity.geo.setCoordinates(chosen.getLatitude(), chosen.getLongitude());
						HelloAndroidActivity.geoSet = true;
					}
				});
		return builder.create();
	}
}
