package test.fuelapp;

public class LoginImplementation implements ILogin {
    private String username;
    private String password;


    public LoginImplementation(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String getUsername() {
        return username;
    }


    @Override
    public String getPassword() {
        return password;
    }
}
