import xyz.dbkit.*;
import org.junit.jupiter.api.Test;


import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
public class DBNodeTest {

    @Test
    public void CreateDB(){
        boolean db_create = true;
        try {
            DBMain myDatabase = new DBMain("xyx-database");
            assertTrue(db_create);
            myDatabase.CreateNode("Users", "user.keys");
        }catch(IOException e){
            System.out.println("Database Creation Failed\n");
            db_create = false;
            assertTrue(db_create);
            e.printStackTrace();
        }

    }

    @Test
    public void CreateNodes(){

    }

    @Test
    public void DeleteNode(){

    }

    @Test
    public void AddModelNode(){

    }




}
