package com.akifbatur.whichway;

import android.location.Location;

public class Functions{
	public static String convertSeconds(double d){
		String s = Location.convert(d, Location.FORMAT_SECONDS);
		String[] ss = s.split(":|\\.|,");
		return ss[0] + "° " + ss[1] + "\" " + ss[2] + "\'";
	}

	public static String formatToN(double number, int decimals){
		StringBuffer str = new StringBuffer(number + "");

		if(str.length() == decimals)
			return str.append("0").toString();

		int dotIndex = 0;
		int i = 0;
		while(true){
			char c = str.charAt(i);
			if(c == '.'){
				dotIndex = i;
				break;
			}
			if(i >= decimals)
				str.setCharAt(i, '0');
			i++;
		}

		if(dotIndex >= decimals)
			return str.substring(0, dotIndex);
		else
			return str.substring(0, decimals + 1);
	}
}
