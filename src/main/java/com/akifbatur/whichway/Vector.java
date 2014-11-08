package com.akifbatur.whichway;

public class Vector{
	private double x;
	private double y;
	private double z;
	private double lat = 90;
	private double lng = 0;

	// Uzay koordinatlarından vektör oluşturur
	private Vector(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// Coğrafik koordinatlardan vektör oluşturur
	public Vector(double lat, double lng){
		setCoordinates(lat, lng);
	}

	public void setCoordinates(double lat, double lng){
		this.z = Math.sin(Math.toRadians(lat));
		double r = Math.cos(Math.toRadians(lat));
		this.y = r * Math.sin(Math.toRadians(lng));
		this.x = r * Math.cos(Math.toRadians(lng));
		this.lat = lat;
		this.lng = lng;
	}

	public double getLat(){
		return lat;
	}

	public double getLong(){
		return lng;
	}

	public double sqr(){
		return x * x + y * y + z * z;
	}

	public double magn(){
		return Math.sqrt(sqr());
	}

	// İki vektörün dot product'ını döndürür
	public double dot(Vector v){
		Vector a = this;
		return a.x * v.x + a.y * v.y + a.z * v.z;
	}

	// İki vektör arasındaki açıyı döndürür
	public double angle(Vector v){
		Vector a = this;
		double dot = a.dot(v);
		double magn = a.magn() * v.magn();
		double value = dot / magn;
		if(value > 1)
			value = 1;
		if(value < -1)
			value = -1;
		double result = Math.toDegrees(Math.acos(value));
		return result;
	}

	// Vectörün v'ye dik düzlemdeki izdişümünü döndürür
	public Vector project(Vector v){
		Vector a = this;
		double diff = a.dot(v);
		double effect = v.sqr();
		double op = diff / effect;
		double x = a.x - op * v.x;
		double y = a.y - op * v.y;
		double z = a.z - op * v.z;
		return new Vector(x, y, z);
	}

	// V vektörü, bu vektörün batısında mı?
	public boolean isWest(Vector v){
		Vector a = this;
		return v.y * a.x < v.x * a.y;
	}

	public double getDirection(Vector v){
		Vector pole = (new Vector(0, 0, 1)).project(this);
		Vector dest = v.project(this);
		double angle = pole.angle(dest);
		if(this.isWest(v))
			angle *= -1;
		return angle;
	}

	public String getDistance(Vector v){
		double constant = 117.478111951; // 2 * PI * 6731 (Earth's radius) / 360 km
		double dist = constant * this.angle(v);
		try{
			if(dist >= 100)
				return (int)dist + " km";
			else if(dist >= 1)
				return (dist + "").substring(0, 4) + " km";
			else if(dist >= 0.1)
				return (int)(dist * 1000) + " m";
			else
				return (dist * 1000 + "").substring(0, 4) + " m";
		}
		catch(Exception e){
			return "0 m";
		}
	}

	public String toString(){
		return "X: " + x + " Y: " + y + " Z: " + z;
	}
}
