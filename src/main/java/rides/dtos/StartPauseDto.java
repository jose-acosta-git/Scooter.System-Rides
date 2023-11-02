package rides.dtos;

public class StartPauseDto {
	
	private int rideId;
	
	public StartPauseDto(int rideId) {
		this.rideId = rideId;
	}
	public StartPauseDto() {}
	public int getRideId() {return rideId;}
}