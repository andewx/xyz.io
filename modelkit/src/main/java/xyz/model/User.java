package xyz.model;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashFunction;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.time.Instant;

public class User extends ModelObject{
    protected String Email;
    protected String Password;
    protected String GroupID;
    protected String FirstName;
    protected String LastName;
    protected String LoginLast;


    public User(String email, String initialPassword, String username, String first, String last){
        //Verify Email Unique Before Creating User
        super();
        Name = username;
        ClassName = "User";
        Email = email;
        FirstName = first;
        LastName = last;
        GroupID = "User";


        //Generate SHA256 Secure Hash
        HashFunction f =  Hashing.sha256();
        HashCode passHash = f.hashString(initialPassword, Charset.defaultCharset());
        Password = passHash.toString();
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
    }

    public User(JSONObject jObj){
        super(jObj);

        try { //Assumes jObj is a ModelObject internally
            Email = (String)jObj.get("Email");
            Password = (String)jObj.get("Password");
            GroupID = (String)jObj.get("GroupID");
            FirstName = (String)jObj.get("FirstName");
            LastName = (String)jObj.get("LastName");
            LoginLast = (String)jObj.get("LoginLast");
            put("Email", Email);
            put("FirstName", FirstName);
            put("LastName", LastName);
            put("GroupID", GroupID);
            put("Password", Password);
            put("LoginLast", LoginLast);

        }catch(JSONException e){
            for (String key : jObj.keySet()){
                JSONObject jModel = (JSONObject)jObj.get(key);
                ModelObject mModel = new ModelObject(jModel);
                addModel(mModel);
            }
        }

    }


    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
        updateKey("Email", email);
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
        updateKey("Password", Password);
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

    public String getKey(){
        return "Email";
    }



    @Override
    public String Form(){ //Override for custom form element processing
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='width:100%'><div style='float:right'><div class='form-close' onclick='clCloseUser()'>X</div></div></div>" );
        sb.append("<form id='user-form' action='model/user_submit/User' method='post' value='Submit'  enctype='multipart/form-data' class='model-form'><legend><label> Create User</label></legend>");
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
