package com.akifbatur.whichway;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DialogMessage extends DialogFragment{

	private String message = "";

	public DialogMessage setMessage(String message){
		this.message = message;
		return this;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
			}
		}).setMessage(message);
		return builder.create();
	}
}
