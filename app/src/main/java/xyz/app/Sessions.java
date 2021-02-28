package xyz.app;

import xyz.model.ModelKeys;

import java.time.Instant;
import java.util.HashMap;
import java.util.Set;

//Session activity should be internally maintained
public class Sessions {
    private HashMap<String, UserKeyState> mUserMap;

    //Store Session ID / With the User Key
    public Sessions(){
        mUserMap = new HashMap<String,UserKeyState>();

    }

    public String AddSession(String key, Integer grp){ //Returns Session ID
        UserKeyState uks = new UserKeyState(key, grp);
        String GUID = ModelKeys.genUID();
        mUserMap.put(GUID, uks);
        return GUID;
    }

    public String GetUsername(String GUID){
        UserKeyState myUser = mUserMap.get(GUID);
        if (myUser != null){
            return myUser.getKey();
        }
        return null;
    }

    public Set<String> GetSessions(){
        return mUserMap.keySet();
    }

    public Integer GetAccess(String GUID){
        UserKeyState myUser = mUserMap.get(GUID);
        if (myUser != null){
            return myUser.getAccess();
        }
        return null;
    }

    public Instant GetLastLogin(String GUID){
        UserKeyState myUser = mUserMap.get(GUID);
        if (myUser != null){
            return myUser.getLastLogin();
        }
        return null;
    }


    public void RemoveSession(String GUID){
        mUserMap.remove(GUID);
    }

    //-------------------------------------------
     //   Internal Class UserKeyState
    //-------------------------------------------

    private class UserKeyState{
        String key;
        String lastLogin;
        Integer access;

        public UserKeyState(String k, Integer g){
            key = k;
            access = g;
            lastLogin = Instant.now().toString();
        }

        public String getKey(){
            return key;
        }

        public Instant getLastLogin(){
            return Instant.parse(lastLogin);
        }

        public Integer getAccess(){
            return access;
        }
    }


}
