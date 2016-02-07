package com.akifbatur.whichway;

public class Favorite{
	public int id;
	public String location;
	public double latitude;
	public double longitude;

	public Favorite(){

	}

	public Favorite(String name, double lat, double lng){
		this.location = name;
		this.latitude = lat;
		this.longitude = lng;
	}

}
