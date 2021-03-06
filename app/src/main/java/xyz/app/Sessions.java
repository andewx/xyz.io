package xyz.app;

import xyz.model.ModelKeys;

import java.time.Instant;
import java.util.HashMap;
import java.util.Set;

/**
 * The type Sessions.
 *
 * @author briananderson  Internally Manages sessions and session cookies using a unique ID to identify session users. Maintains Map of the UID to the Session information
 */
public class Sessions {
    private HashMap<String, UserKeyState> mUserMap;

    /**
     * Store sessions with UID Map - Constructor
     */
    public Sessions(){
        mUserMap = new HashMap<String,UserKeyState>();

    }

    /**
     * Add Session with a specified Key
     * Requirement(App 2.0)
     * @param key UID Key to be added
     * @param grp GroupID Access level
     * @return GUID string
     */
    public String AddSession(String key, Integer grp){ //Returns Session ID
        UserKeyState uks = new UserKeyState(key, grp);
        String GUID = ModelKeys.genUID();
        mUserMap.put(GUID, uks);
        return GUID;
    }

    /**
     * Sets message for UID
     * Requirement(App 2.0)
     * @param s    Message
     * @param GUID User ID for Session
     */
    public void SetMessage(String s, String GUID){
        UserKeyState myUser = mUserMap.get(GUID);
        if (myUser != null){
            myUser.setMessage(s);
        }

    }

    /**
     * Gets Session Message for User with Session UID
     * Requirement(App 2.0)
     * @param GUID Session UID
     * @return Message string
     */
    public String GetMessage(String GUID){
        UserKeyState myUser = mUserMap.get(GUID);
        if (myUser != null){
            return myUser.getMessage();
        }
        return "";
    }

    /**
     * Gets session username
     * Requirement(App 2.0)
     * @param GUID Session ID
     * @return Username string
     */
    public String GetUsername(String GUID){
        UserKeyState myUser = mUserMap.get(GUID);
        if (myUser != null){
            return myUser.getKey();
        }
        return null;
    }

    /**
     * Gets session Map
     * Requirement(App 2.0)
     * @return Set of keys for sessions
     */
    public Set<String> GetSessions(){
        return mUserMap.keySet();
    }

    /**
     * Gets access level of user
     * Requirement(App 2.0)
     * @param GUID User Session ID
     * @return Access Level
     */
    public Integer GetAccess(String GUID){
        UserKeyState myUser = mUserMap.get(GUID);
        if (myUser != null){
            return myUser.getAccess();
        }
        return null;
    }

    /**
     * Gets last login
     * Requirement(App 2.0)
     * @param GUID User session ID
     * @return Instant instant
     */
    public Instant GetLastLogin(String GUID){
        UserKeyState myUser = mUserMap.get(GUID);
        if (myUser != null){
            return myUser.getLastLogin();
        }
        return null;
    }


    /**
     * Removes session
     * Requirement(App 2.0)
     * @param GUID User session ID
     */
    public void RemoveSession(String GUID){
        mUserMap.remove(GUID);
    }

    //-------------------------------------------
     //   Internal Class UserKeyState
    //-------------------------------------------

    /**
     * Store Session State with last login, message, access level, key, and a log
     */
    private class UserKeyState{
        /**
         * The Key.
         */
        String key;
        /**
         * The Last login.
         */
        String lastLogin;
        /**
         * The Access.
         */
        Integer access;
        /**
         * The Message.
         */
        String message;
        /**
         * The Log.
         */
        String log;

        /**
         * Gets session message
         * Requirement(App 2.0)
         * @return message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Sets session message
         * Requirement(App 2.0)
         * @param message Message
         */
        public void setMessage(String message) {
            this.message = message;
        }

        /**
         * Gets session log
         * Requirement(App 2.0)
         * @return returns log
         */
        public String getLog() {
            return log;
        }

        /**
         * Sets session log
         * Requirement(App 2.0)
         * @param log new log
         */
        public void setLog(String log) {
            this.log = log;
        }

        /**
         * Constructor
         * Requirement(App 2.0)
         * @param k User Key
         * @param g User group access level
         */
        public UserKeyState(String k, Integer g){
            key = k;
            access = g;
            lastLogin = Instant.now().toString();
        }

        /**
         * Gets session key
         * Requirement(App 2.0)
         * @return string
         */
        public String getKey(){
            return key;
        }

        /**
         * Gets last login
         * Requirement(App 2.0)
         * @return instant
         */
        public Instant getLastLogin(){
            return Instant.parse(lastLogin);
        }

        /**
         * Gets access Level for user key state
         * Requirement(App 2.0)
         * @return integer
         */
        public Integer getAccess(){
            return access;
        }
    }


}
