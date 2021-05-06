package xyz.model;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashFunction;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.time.Instant;

/**
 * User model used by framework to identify user sessions and login with passsword. UID key is the email for users
 * and users should be associated with GroupID for access priveleges
 */
public class User extends ModelObject{
    protected String Email;
    protected String Password;
    protected String GroupID;
    protected String FirstName;
    protected String LastName;
    protected String LoginLast;

    /**
     * User constructor
     * @param email - prevalidated (HTML5) email string
     * @param initialPassword - Non hashed password
     * @param username - Username
     * @param first - First name
     * @param last - Last name
     * Requirement(Modelkit 1.0)
     */
    public User(String email, String initialPassword, String username, String first, String last){
        //Verify Email Unique Before Creating User
        super();
        Name = username;
        ClassName = "User";
        Email = email;
        FirstName = first;
        LastName = last;
        GroupID = "User";
        Password = User.PasswordSHA(initialPassword);
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
        put("Password", Password);
        put("LoginLast", LoginLast);

    }

    /**
     * User Constructor from JSON string
     * @param json User json string
     * Requirement(Modelkit 1.1)
     */
    public User(String json){
        super(json);
        Email = (String)get("Email");
        Password = (String)get("Password");
        GroupID = (String)get("GroupID");
        FirstName = (String)get("FirstName");
        LastName = (String)get("LastName");
        LoginLast = (String)get("LoginLast");
        put("Email", Email);
        put("FirstName", FirstName);
        put("LastName", LastName);
        put("GroupID", GroupID);
        put("Password", Password);
        put("LoginLast", LoginLast);
        put("Name", Name);
    }

    /**
     * User constructor from ModelObject
     * @param mObj - User ModelObject
     * @throws JSONException Not well formatted User ModelObject
     * Requirement(Modelkit 1.1)
     */
    public User(ModelObject mObj)throws JSONException{
        super(mObj);
        try {
            Email = mObj.GetString("Email");
        }catch(Exception e){ Email = "";}
        try {
            FirstName = mObj.GetString("FirstName");
        }catch(Exception e){FirstName = "";}
        try {
            LastName = mObj.GetString("LastName");
        }catch(Exception e){LastName = "";}
        try {
            Password = mObj.GetString("Password");
        }catch(Exception e){Password = "";}
        try {
            GroupID = mObj.GetString("GroupID");
        }catch(Exception e){ GroupID = "";}
        try {
            LoginLast = mObj.GetString("Email");
        }catch(Exception e){LoginLast = "";}


        put("Email", Email);
        put("FirstName", FirstName);
        put("LastName", LastName);
        put("GroupID", GroupID);
        put("Password", Password);
        put("LoginLast", LoginLast);

    }

    /**
     * Generates Password hash from sha256 algorithm. Uses Guava hash library
     * @param pass - Password to be hashed
     * @return - 256 bit hashed password
     * Requirement(App 2.0)
     */
    public static String PasswordSHA(String pass){
        //Generate SHA256 Secure Hash
        HashFunction f =  Hashing.sha256();
        HashCode passHash = f.hashString(pass, Charset.defaultCharset());
        return passHash.toString();
    }

    /**
     * Construct User Model from JSONObject
     * @param jObj - User JSONObject
     * Requirement(Modelkit 1.1)
     */
    public User(JSONObject jObj){
        super(jObj);
        for(String key : jObj.keySet()){
            if(key.equals("Email")){
                Email = getString(key);
            }
            if(key.equals("Password")){
                Password = getString(key);
            }
            if(key.equals("GroupID")){
                GroupID = getString(key);
            }
            if(key.equals("FirstName")){
                FirstName = getString(key);
            }
            if(key.equals("LastName")){
                LastName = getString(key);
            }
            if(key.equals("LoginLast")){
                LoginLast = getString(key);
            }

        }

    }

    /**
     * Getter Email
     * @return Email
    * Requirement(Modelkit 1.1)
     */
    public String getEmail() {
        return Email;
    }

    /**
     * Setter Email
     * @param email Email
     * Requirement(Modelkit 1.1)
     */
    public void setEmail(String email) {
        Email = email;
        updateKey("Email", email);
    }

    /**
     * Getter password
     * @return hashed password
     * Requirement(Modelkit 1.1)
     */
    public String getPassword() {
        return Password;
    }

    /**
     * Setter Password
     * @param password - Prehashed password from sha256
     * Requirement(Modelkit 1.1)
     */
    public void setPassword(String password) {
        Password = password;
        updateKey("Password", Password);
    }

    /**
     * Getter GroupID
     * @return GroupID
     * Requirement(Modelkit 1.1)
     */
    public String getGroupID() {
        return GroupID;
    }

    /**
     * Setter GroupID
     * @param groupID GroupID
     * Requirement(Modelkit 1.1)
     */
    public void setGroupID(String groupID) {
        GroupID = groupID;
        updateKey("GroupID", GroupID);
    }

    /**
     * Getter  First Name
     * @return FirstName
     * Requirement(Modelkit 1.1)
     */
    public String getFirstName() {
        return FirstName;
    }

    /**
     * Setter FirstName
     * @param firstName FirstName
     * Requirement(Modelkit 1.1)
     */
    public void setFirstName(String firstName) {
        FirstName = firstName;
        updateKey("FirstName", FirstName);
    }

    /**
     * Getter LastName
     * @return LastName
     * Requirement(Modelkit 1.1)
     */
    public String getLastName() {
        return LastName;
    }

    /**
     * Setter LastName
     * @param lastName LastName
     */
    public void setLastName(String lastName) {
        LastName = lastName;
        updateKey("LastName", lastName);
    }

    /**
     * Gets last user login
     * @return TimeFormatted last login from Java Time library
     * Requirement(Modelkit 1.1)
     */
    public String getLoginLast() {
        return LoginLast;
    }

    /**
     * Sets last login
     * @param loginLast Well formatted Time of login last
     * Requirement(Modelkit 1.1)
     */
    public void setLoginLast(String loginLast) {
        LoginLast = loginLast;
        updateKey("LoginLast", LoginLast);
    }




    /**
     * Form tags for User Object
     * @return
     */
    @Override
    public String Form(){ //Override for custom form element processing
        StringBuilder sb = new StringBuilder();
        sb.append("<form id='user-form' action='model/user_submit/User' method='post' value='Submit'  enctype='multipart/form-data'>");
        boolean required = false;
        for(String key : keySet()){ //No UID / ClassName
            if (!key.equals("UID") && !key.equals("ClassName") && !key.equals("GroupID") && !key.equals("LoginLast")){
                sb.append("<div class='form-item'><div class='form-item-label col-1-2'>");
                sb.append(key);
                sb.append("</div>");
                sb.append(GetInputTag(key));
                sb.append( "</div>");
            }
        }
        sb.append("<input id='submitUser' type='submit' value='Submit'/>");
        sb.append("</form>");
        return sb.toString();
    }

    /**
     * Input tags for User object
     * @param key key value of input tag
     * @return
     */
    @Override
    public String GetInputTag(String key){
        String type = "<input class='col-1-2' type='text' maxLength='45'" +" value='" + get(key) + "' id='" + key + "' name='" + key + "'></input>";
        if(key.equals("Password")){
            type = "<input class='col-1-2' type='password' maxLength='45'" +" value='" + get(key) + "' id='" + key + "' name='" + key + "' required></input>";
        }
        if(key.equals("Email")){
            type = "<input class='col-1-2' type='email' maxLength='45'" +" value='" + get(key) + "' id='" + key + "' name='" + key + "' required></input>";
        }
        if(key.equals("Name")){
            type = "<input class='col-1-2' type='text' maxLength='45'" +" value='" + get(key) + "' id='" + key + "' name='Username' required></input>";
        }
        return type;
    }

}
