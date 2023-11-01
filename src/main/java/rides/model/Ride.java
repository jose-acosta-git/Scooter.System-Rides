package rides.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Ride {
	
	@Id @GeneratedValue (strategy = GenerationType.AUTO)
	private int id;
	
	@Column
	private LocalDateTime startTime;
	
	@Column
	private LocalDateTime endTime;
	
	@Column
	private double distance;
	
	@Column
	private double price;

	public Ride(LocalDateTime startTime, LocalDateTime endTime, double distance, double price) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.distance = distance;
		this.price = price;
	}
	
	public Ride() {}

	public int getId() {return id;}
	public LocalDateTime getStartTime() {return startTime;}
	public LocalDateTime getEndTime() {return endTime;}
	public double getDistance() {return distance;}
	public double getPrice() {return price;}
}
