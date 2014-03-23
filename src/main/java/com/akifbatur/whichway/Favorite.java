package com.akifbatur.whichway;

public class Favorite{
	private int id;
	private String location;
	private double latitude;
	private double longitude;

	public Favorite(){
	}

	public Favorite(String name, double lat, double lng){
		this.location = name;
		this.latitude = lat;
		this.longitude = lng;
	}

	public int getId(){
		return id;
	}

	public void setID(int id){
		this.id = id;
	}

	public String getLocation(){
		return location;
	}

	public void setLocation(String l){
		this.location = l;
	}

	public double getLatitude(){
		return latitude;
	}

	public void setLatitude(double l){
		this.latitude = l;
	}

	public double getLongitude(){
		return longitude;
	}

	public void setLongitude(double l){
		this.longitude = l;
	}
}
