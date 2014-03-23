package com.akifbatur.whichway;

import android.location.Location;

public class Functions{
	public static String convertSeconds(double d){
		String s = Location.convert(d, Location.FORMAT_SECONDS);
		String[] ss = s.split(":|\\.|,");
		return ss[0] + "° " + ss[1] + "\" " + ss[2] + "\'";
	}
}
