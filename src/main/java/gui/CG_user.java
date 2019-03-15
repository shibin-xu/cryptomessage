package gui;

import java.security.PublicKey;

/**
 * User information after login
 */
public class CG_user {
    private PublicKey pkr;
    private String username;
    private String password;

    public CG_user(PublicKey pkr, String username, String password){
        this.pkr = pkr;
        this.username = username;
        this.password = password;
    }

    public PublicKey getPublicKey(){
        return this.pkr;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

}
