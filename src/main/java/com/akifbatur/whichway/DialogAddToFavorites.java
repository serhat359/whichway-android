package com.akifbatur.whichway;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DialogAddToFavorites extends DialogFragment{
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setTitle(R.string.addtofav);
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
			}
		});
		builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
			}
		});
		builder.setView(getActivity().getLayoutInflater().inflate(R.layout.menu, null));
		return builder.create();
	}
	
	@Override
	public void onStart(){
		super.onStart();
		AlertDialog d = (AlertDialog)getDialog();
		if(d != null){
			Button positiveButton = (Button)d.getButton(Dialog.BUTTON_POSITIVE);
			positiveButton.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					/*String name = ((EditText)getView().findViewById(R.id.locationName)).getText()
							.toString();
					if(!name.isEmpty())
						HelloAndroidActivity.debug.setText(name);
					else
						HelloAndroidActivity.debug.setText("string is empty");
					int chosen = HelloAndroidActivity.radioGroup.getCheckedRadioButtonId();
					if(name.isEmpty()){
						// TODO display error message
					}
					else if(chosen < 0){
						// TODO display error message
					}
					else if((chosen == 0 && !HelloAndroidActivity.gpsSet)
							| (chosen == 1 && !HelloAndroidActivity.geoSet)){
						// TODO display error message
					}
					else{
						// TODO insert to database
						dismiss();
					}*/
				}
			});
		}
	}
}
