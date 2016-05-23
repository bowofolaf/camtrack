package blf.com.camtrack.request;

public class LoginRequest {

    String username, password;

    public LoginRequest(String username,String password){
        this.password = password;
        this.username = username;
    }
}
