package rides.model;

import java.util.List;

public class User {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String role;
    private List<Account> accounts;
    
    public User(int id, String name, String email, String phone, String password, String role, List<Account> accounts) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.accounts = accounts;
    }

    

    public User() {
    }



    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

        
}
