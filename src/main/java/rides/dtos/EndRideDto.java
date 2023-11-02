package rides.dtos;

public class EndRideDto {
	
	private double distance;
	
	public EndRideDto(double distance) {
		this.distance = distance;
	}
	
	public EndRideDto() {}

	public double getDistance() {return distance;}
}
