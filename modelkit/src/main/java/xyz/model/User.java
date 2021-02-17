package xyz.model;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashFunction;

import java.nio.charset.Charset;
import java.time.Instant;

public class User extends ModelObject{
    public String Email;
    public String PasswordSHA256;
    public String GroupID;
    public String FirstName;
    public String LastName;
    public String LoginLast;


    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPasswordSHA256() {
        return PasswordSHA256;
    }

    public void setPasswordSHA256(String passwordSHA256) {
        PasswordSHA256 = passwordSHA256;
    }

    public String getGroupID() {
        return GroupID;
    }

    public void setGroupID(String groupID) {
        GroupID = groupID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getLoginLast() {
        return LoginLast;
    }

    public void setLoginLast(String loginLast) {
        LoginLast = loginLast;
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

    public void update(){
        //Put Keys
        super.update();
        updateKey("FirstName", FirstName);
        updateKey("LastName", LastName);
        updateKey("GroupID", GroupID);
        updateKey("Name", Name);
        updateKey("PasswordSHA256", PasswordSHA256);
        updateKey("LoginLast", LoginLast);
    }
}
