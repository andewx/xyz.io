package xyz.app;

/**
 * Implements Ring Security method for user sessions with a default ring behavior style of ROOT -> PUBLIC.
 * Change this file if you wish to change how you identify access levels
 */
public class Security {
    /**
     * The constant ROOT.
     */
    public static final int ROOT = 1;
    /**
     * The constant ADMIN.
     */
    public static final int ADMIN = 2;
    /**
     * The constant MANGER.
     */
    public static final int MANGER = 3;
    /**
     * The constant USER_ASSOCIATED.
     */
    public static final int USER_ASSOCIATED = 4;
    /**
     * The constant USER_ALL.
     */
    public static final int USER_ALL = 5;
    /**
     * The constant PUBLISH.
     */
    public static final int PUBLISH = 6;

    /**
     * Checks if the current user can access a given route security level
     * Requirement(App 2.0)
     * @param UserSecurity  User security level
     * @param RouteSecurity Route Security level
     * @return Resolver for the route
     */
    public Resolver HasPermission(int UserSecurity, int RouteSecurity) {
        if (UserSecurity < RouteSecurity) {
            return new Resolver(true, false);
        }
        if (UserSecurity > RouteSecurity) {
            return new Resolver(false, false);
        }
        if (UserSecurity == USER_ASSOCIATED) {
            return new Resolver(true, true);
        }
        return new Resolver(true, false);
    }

    /**
     * Resolver stores the state of an attempted access to a given route profile. If security is specific
     * for a given route the Resolver will identify that the user may have access but that further resolve is
     * required. Other wise the route is either valid or invalid for access.
     */
    public class Resolver{
        /**
         * The M is valid.
         */
        boolean mIsValid;
        /**
         * The M needs resolve.
         */
        boolean mNeedsResolve;

        /**
         * Resolver state constructor
         * Requirement(App 2.0)
         * @param a Valid Route Resolve
         * @param b Route needs further resolution
         */
        public Resolver(boolean a, boolean b){
            mIsValid = a;
            mNeedsResolve = b;
        }

        /**
         * Returns if Resolver is valid
         * Requirement(App 2.0)
         * @return mIsValid boolean
         */
        public boolean IsValid(){
            return mIsValid;
        }

        /**
         * Getter does route need futher resolution
         * Requirement(App 2.0)
         * @return mNeedsResolve boolean
         */
        public boolean NeedsResolve(){
            return mNeedsResolve;
        }
 }
}
