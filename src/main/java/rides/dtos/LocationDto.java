package rides.dtos;

public class LocationDto {
	
	private double latitude;
	private double longitude;
	
	public LocationDto(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public double getLatitude() {return latitude;}
	public double getLongitude() {return longitude;}
}
