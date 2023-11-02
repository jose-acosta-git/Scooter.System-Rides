package rides.dtos;

public class StartRideDto {
	
	private int accountId;
	private int scooterId;
	
	public StartRideDto(int accountId, int scooterId) {
		this.accountId = accountId;
		this.scooterId = scooterId;
	}
	
	public StartRideDto() {}

	public int getAccountId() {return accountId;}
	public int getScooterId() {return scooterId;}
}
