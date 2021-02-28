package xyz.app;

public class Security {
    public static final int ROOT = 1;
    public static final int ADMIN = 2;
    public static final int MANGER = 3;
    public static final int USER_ASSOCIATED = 4;
    public static final int USER_ALL = 5;
    public static final int PUBLISH = 6;

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

 public class Resolver{
        boolean mIsValid;
        boolean mNeedsResolve;

        public Resolver(boolean a, boolean b){
            mIsValid = a;
            mNeedsResolve = b;
        }

        public boolean IsValid(){
            return mIsValid;
        }

        public boolean NeedsResolve(){
            return mNeedsResolve;
        }
 }
}
