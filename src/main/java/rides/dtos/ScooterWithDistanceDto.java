package rides.dtos;

public class ScooterWithDistanceDto {
	
	private int id;
	private double totalDistance;
	
	public ScooterWithDistanceDto(int id, double totalDistance) {
		super();
		this.id = id;
		this.totalDistance = totalDistance;
	}
	
	public ScooterWithDistanceDto() {}

	public int getId() {return id;}
	public void setId(int id) {this.id = id;}
	public double getTotalDistance() {return totalDistance;}
	public void setTotalDistance(double totalDistance) {this.totalDistance = totalDistance;}
}
