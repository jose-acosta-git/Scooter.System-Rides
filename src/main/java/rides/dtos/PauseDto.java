package rides.dtos;

import java.time.LocalTime;

public class PauseDto {
	
	private LocalTime startTime;
	private LocalTime endTime;
    private EndRideDto ride;
    
	public PauseDto(LocalTime startTime, LocalTime endTime, EndRideDto ride) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.ride = ride;
	}
    
    public PauseDto() {}

	public LocalTime getStartTime() {return startTime;}
	public LocalTime getEndTime() {return endTime;}
	public EndRideDto getRide() {return ride;}
}