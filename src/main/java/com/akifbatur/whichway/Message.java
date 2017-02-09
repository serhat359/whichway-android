package com.akifbatur.whichway;

import android.content.Context;
import android.widget.Toast;

public class Message{
	public enum MessageDuration{
		SHORT, LONG
	}

	public static void showMessage(String text, Message.MessageDuration duration, Context context){
		int durationInt;

		switch(duration){
			case LONG:
				durationInt = Toast.LENGTH_LONG;
				break;
			case SHORT:
				durationInt = Toast.LENGTH_SHORT;
				break;

			default:
				throw new RuntimeException();
		}

		Toast.makeText(context, text, durationInt).show();
	}
}
