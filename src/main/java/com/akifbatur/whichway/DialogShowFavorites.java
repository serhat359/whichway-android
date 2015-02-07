package com.akifbatur.whichway;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DialogShowFavorites extends DialogFragment{

	private DialogFavoriteListener mListener;

	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.favorites)
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
					}
				}).setItems(HelloAndroidActivity.favorites, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
						mListener.favoriteChosen(id);
					}
				});
		return builder.create();
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			mListener = (DialogFavoriteListener)activity;
		}
		catch(ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement DialogListener");
		}
	}
}
