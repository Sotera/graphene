package graphene.model.sql;

import java.io.Serializable;
import javax.annotation.Generated;

/**
 * GUser is a Querydsl bean type
 */
@Generated("com.mysema.query.codegen.BeanSerializer")
public class GUser implements Serializable {

    private java.sql.Timestamp accountcreated;

    private Boolean active;

    private String avatar;

    private String email;

    private String fullname;

    private String hashedpassword;

    private java.sql.Timestamp lastlogin;

    private Integer numberlogins;

    private String salt;

    private String username;

    public java.sql.Timestamp getAccountcreated() {
        return accountcreated;
    }

    public void setAccountcreated(java.sql.Timestamp accountcreated) {
        this.accountcreated = accountcreated;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getHashedpassword() {
        return hashedpassword;
    }

    public void setHashedpassword(String hashedpassword) {
        this.hashedpassword = hashedpassword;
    }

    public java.sql.Timestamp getLastlogin() {
        return lastlogin;
    }

    public void setLastlogin(java.sql.Timestamp lastlogin) {
        this.lastlogin = lastlogin;
    }

    public Integer getNumberlogins() {
        return numberlogins;
    }

    public void setNumberlogins(Integer numberlogins) {
        this.numberlogins = numberlogins;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String toString() {
         return "accountcreated = " + accountcreated + ", active = " + active + ", avatar = " + avatar + ", email = " + email + ", fullname = " + fullname + ", hashedpassword = " + hashedpassword + ", lastlogin = " + lastlogin + ", numberlogins = " + numberlogins + ", salt = " + salt + ", username = " + username;
    }

}

