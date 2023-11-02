package rides.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

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
	
	@Column
	private int accountId;
	
	@Column 
	private int scooterId;
	
    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL)
    private List<Pause> pauses;

	public Ride(LocalDateTime startTime, int accountId, int scooterId) {
		this.startTime = startTime;
		this.accountId = accountId;
		this.scooterId = scooterId;
		this.pauses = new ArrayList<>();
	}
	
	public Ride() {}

	public int getId() {return id;}
	public LocalDateTime getStartTime() {return startTime;}
	public LocalDateTime getEndTime() {return endTime;}
	public double getDistance() {return distance;}
	public double getPrice() {return price;}
	public int getAccountId() {return accountId;}
	public int getScooterId() {return scooterId;}
    public List<Pause> getPauses() {return pauses;}
	
	public void setEndTime(LocalDateTime endTime) {this.endTime = endTime;}
	public void setDistance(double distance) {this.distance = distance;}
	public void setPrice(double price) {this.price = price;}
//    public void addPause(Pause pause) {
//        pauses.add(pause);
//        pause.setRide(this);
//    }
}
