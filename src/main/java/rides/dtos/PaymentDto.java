package rides.dtos;

public class PaymentDto {
	
	private double price;
	public PaymentDto(double price) {this.price = price;}
	public PaymentDto() {}
	public double getPrice() {return price;}
}
