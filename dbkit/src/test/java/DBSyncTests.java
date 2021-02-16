import xyz.dbkit.*;
import org.junit.jupiter.api.Test;
import xyz.model.User;

import java.io.IOException;

public class DBSyncTests {
    DBMain mDataBase;

    public DBSyncTests(){
        try {
            mDataBase = new DBMain("MyDatabase");
        } catch (IOException e) {
            System.out.println("Error Creating Database");
        }
    }

    @Test
    public void SyncNodes() throws InterruptedException {

        mDataBase.AddModel(mDataBase.GetNode("Users"), new User("brian.michael.anderson@gmail.com", "brbri", "andewx", "Brian", "Anderson"));
        mDataBase.start();

    }
}
