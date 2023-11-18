package rides.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Account {
    private int id;
    @JsonIgnore
    private LocalDate registrationDate;
    private double balance;
    private String mercadoPagoId;
    private boolean isActive;
    public Account() {
    }
    public Account(int id, LocalDate registrationDate, double balance, String mercadoPagoId, boolean isActive) {
        this.id = id;
        this.registrationDate = registrationDate;
        this.balance = balance;
        this.mercadoPagoId = mercadoPagoId;
        this.isActive = isActive;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }
    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }
    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }
    public String getMercadoPagoId() {
        return mercadoPagoId;
    }
    public void setMercadoPagoId(String mercadoPagoId) {
        this.mercadoPagoId = mercadoPagoId;
    }
    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    
}
