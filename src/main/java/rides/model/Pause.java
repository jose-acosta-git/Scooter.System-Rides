package rides.model;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Pause {
	
	@Id @GeneratedValue (strategy = GenerationType.AUTO)
	private int id;
	
	@Column
	private LocalTime startTime;
	
	@Column
	private LocalTime endTime;
	
    @ManyToOne
    @JoinColumn(name = "ride_id")
    private Ride ride;
	
	public Pause(LocalTime startTime, LocalTime endTime, Ride ride) {
		this.startTime = startTime;
		this.endTime = endTime;
        this.ride = ride;
	}
	
	public Pause() {}

	public int getId() {return id;}
	public LocalTime getStartTime() {return startTime;}
	public LocalTime getEndTime() {return endTime;}
    public Ride getRide() {return ride;}
    
    public void setRide(Ride ride) {
        this.ride = ride;
    }
}
