package org.ut.biolab.medsavant;

import java.io.Serializable;

/**
 * User information.
 *
 * @author Miroslav Cupak (mirocupak@gmail.com)
 * @version 1.0
 */
public class UserDetails implements Serializable {

    private static final long serialVersionUID = 4L;

    private String email;

    public UserDetails() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserDetails{" + "email=" + email + '}';
    }

}
