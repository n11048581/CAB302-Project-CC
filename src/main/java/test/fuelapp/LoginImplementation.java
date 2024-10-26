package test.fuelapp;

/**
 * Class to implement the login interface
 */
public class LoginImplementation implements ILogin {
    private String username;
    private String password;

    /**
     * Stores the user's username and password
     * @param username The fetched username
     * @param password The fetched password
     */
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
