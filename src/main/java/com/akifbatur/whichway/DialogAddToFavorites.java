package com.akifbatur.whichway;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class DialogAddToFavorites extends DialogFragment{

	private String text = "";
	private DialogClickListener mListener;

	public DialogAddToFavorites setMessage(String text){
		this.text = text;
		return this;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View view = getActivity().getLayoutInflater().inflate(R.layout.menu, null);
		builder.setView(view).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int id){
			}
		}).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int id){
				// Will be overridden
			}
		});
		return builder.create();
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			mListener = (DialogClickListener)activity;
		}
		catch(ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement DialogListener");
		}
	}

	@Override
	public void onStart(){
		super.onStart();
		AlertDialog d = (AlertDialog)getDialog();
		EditText edittext = (EditText)getDialog().findViewById(R.id.locationName);
		edittext.setText(text);

		if(d != null){
			Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
			positiveButton.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					String tag = "notSet";
					EditText edittext = (EditText)getDialog().findViewById(R.id.locationName);
					RadioGroup radioGroup = (RadioGroup)getDialog().findViewById(R.id.radioGroup);
					int checked = radioGroup.getCheckedRadioButtonId();
					if(edittext.getText().toString().equals(""))
						new DialogMessage().setMessage("Please set a name").show(getFragmentManager(), tag);
					else if(checked < 0)
						new DialogMessage().setMessage("Please choose an option").show(getFragmentManager(), tag);
					else if(checked == R.id.radioCurrent && !HelloAndroidActivity.gpsSet)
						new DialogMessage().setMessage("No GPS data").show(getFragmentManager(), tag);
					else if(checked == R.id.radioSearched && !HelloAndroidActivity.geoSet)
						new DialogMessage().setMessage("Please search a place first").show(getFragmentManager(), tag);
					else{
						mListener.onDialogPositiveClick(DialogAddToFavorites.this);
						dismiss();
					}
				}
			});
		}
	}
}