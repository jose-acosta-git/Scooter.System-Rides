package rides.dtos;

public class StartRideDto {
	
	private int scooterId;
	public StartRideDto(int scooterId) {
		this.scooterId = scooterId;
	}
	public StartRideDto() {}
	public int getScooterId() {return scooterId;}
}
