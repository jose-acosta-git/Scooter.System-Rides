package rides.dtos;

public class ScooterWithTimeDto {
	
	private Integer scooterId;
	private Long totalTimeSeconds;
	
	public ScooterWithTimeDto(Integer scooterId, Long totalTime) {
		super();
		this.scooterId = scooterId;
        this.totalTimeSeconds = totalTime;
	}
	public ScooterWithTimeDto() {}

	public Integer getScooterId() {return scooterId;}
	public void setScooterId(Integer scooterId) {this.scooterId = scooterId;}
	public Long getTotalTimeSeconds() {return totalTimeSeconds;}
	public void setTotalTimeSeconds(Long totalTime) {this.totalTimeSeconds = totalTime;}
}