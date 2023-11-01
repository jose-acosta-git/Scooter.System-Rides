package rides.dtos;

import java.time.LocalDateTime;

public class RideDto {
	
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private double distance;
	private double price;
	
	public RideDto(LocalDateTime startTime, LocalDateTime endTime, double distance, double price) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.distance = distance;
		this.price = price;
	}
	
	public RideDto() {}

	public LocalDateTime getStartTime() {return startTime;}
	public LocalDateTime getEndTime() {return endTime;}
	public double getDistance() {return distance;}
	public double getPrice() {return price;}
}
