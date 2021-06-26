
package main.entities;

import java.io.Serializable;

public class AccountFollowAccountPK implements Serializable {

    private static final long serialVersionUID = 1L;

    
    private int idAccountFollower;

    
    private int idAccountFollowed;

    public AccountFollowAccountPK() {
    }

    public AccountFollowAccountPK( int idAccountFollower, int idAccountFollowed) {
        this.idAccountFollower = idAccountFollower;
        this.idAccountFollowed = idAccountFollowed;
    }

    public int getIdAccountFollower() {
        return idAccountFollower;
    }

    public void setIdAccountFollower(int idAccountFollower) {
        this.idAccountFollower = idAccountFollower;
    }

    public int getIdAccountFollowed() {
        return idAccountFollowed;
    }

    public void setIdAccountFollowed(int idAccountFollowed) {
        this.idAccountFollowed = idAccountFollowed;
    }
}
