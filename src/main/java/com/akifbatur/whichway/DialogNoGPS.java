package com.akifbatur.whichway;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DialogNoGPS extends DialogFragment{
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
					}
				}).setMessage("Current location is unknown.");
		return builder.create();
	}
}
