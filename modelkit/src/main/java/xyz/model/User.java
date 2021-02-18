package xyz.model;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashFunction;

import java.nio.charset.Charset;
import java.time.Instant;

public class User extends ModelObject{
    protected String Email;
    protected String PasswordSHA256;
    protected String GroupID;
    protected String FirstName;
    protected String LastName;
    protected String LoginLast;


    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
        updateKey("Email", email);
    }

    public String getPasswordSHA256() {
        return PasswordSHA256;
    }

    public void setPasswordSHA256(String passwordSHA256) {
        PasswordSHA256 = passwordSHA256;
        updateKey("PasswordSHA256", passwordSHA256);
    }

    public String getGroupID() {
        return GroupID;
    }

    public void setGroupID(String groupID) {
        GroupID = groupID;
        updateKey("GroupID", GroupID);
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
        updateKey("FirstName", FirstName);
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
        updateKey("LastName", lastName);
    }

    public String getLoginLast() {
        return LoginLast;
    }

    public void setLoginLast(String loginLast) {
        LoginLast = loginLast;
        updateKey("LoginLast", LoginLast);
    }

    public User(String email, String initialPassword, String username, String first, String last){
        //Verify Email Unique Before Creating User
        super();
        Name = username;
        ClassName = "User";
        Email = email;
        FirstName = first;
        LastName = last;
        GroupID = "user";


        //Generate SHA256 Secure Hash
        HashFunction f =  Hashing.sha256();
        HashCode passHash = f.hashString(initialPassword, Charset.defaultCharset());
        PasswordSHA256 = passHash.toString();
        UID = Email;

        //Login Last Now
        LoginLast = Instant.now().toString();

        //Put Keys
        updateKey("UID", Email);
        updateKey("ClassName", ClassName);
       put("Email", Email);
       put("FirstName", FirstName);
       put("LastName", LastName);
       put("GroupID", GroupID);
        updateKey("Name", Name);
       put("PasswordSHA256", PasswordSHA256);
       put("LoginLast", LoginLast);

    }

}
