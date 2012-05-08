package labtracker.blueJ.dataCollection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A class for sending lines of output via a websocket.  All events are 
 * currently sent to a MySQL database.
 * @author Kyle Wenholz, Katie Mueller
 * @version 5.02.2012
 */
public class EventAntennae{
    private String userID;
    private Connection conn;
    private static final String SQL_URL = 
	"jdbc:mysql://10.150.0.169:3306/labtracker";
    
    /**
     * Sets up the initial file with name "name".
     * @param name the name of the file (padded with 0000 and such after 
     *     initialization)
     * @param dir the path for the file to be stored
     */
    public EventAntennae(){
	this.userID = System.getProperty("user.name");
	this.userID = ""+this.userID.hashCode();
	
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        }
        catch (java.lang.ClassNotFoundException e) {
            System.err.print("ClassNotFoundException: ");
            System.err.println(e.getMessage());
        } catch (InstantiationException e) {
			e.printStackTrace();
	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	}
	
        try {
            System.out.println("Trying to connect...");
            this.conn = DriverManager.getConnection (EventAntennae.SQL_URL, 
						     "testuser", "testpass");
            System.out.println("connected!");
        }catch(SQLException ex) {
            System.err.print("SQLException: ");
            System.err.println(ex.getMessage());
        }
    }
    
    /**
     * Broadcasts an event to the hub.
     * @param eventID the type of event (Compile, etc.)
     * @param details1 details on the event (method name, time estimate, etc.)
     * @param details2 specifics about the details (error type, code example, 
     *     etc.)
     * @return the event sent to an appropriate event table in the database
     */
    public void emitEvent(int eventID, String details1, String details2){
	String qString = "";
	if(eventID == EventListener_Main.ACTIVITY_TRACK){
	    qString = "INSERT INTO activity_logs (`MethodName`, "+
		"UserID) VALUES (?, ?);";
	}if(eventID == EventListener_Main.CODE_SNIPPET){
		qString = "INSERT INTO code_diffs (`MethodName`, "+
		"`Diff`, UserID) VALUES (?, ?, ?);";
	}if(eventID == EventListener_Main.COMPILE_ERROR){
	    qString = "INSERT INTO compile_errors (`MethodName`, "+
		"`ErrorType`, UserID) VALUES (?, ?, ?);";
	}if(eventID == EventListener_Main.COMPILE_WARNING){
	    qString = "INSERT INTO compile_errors (`MethodName`, "+
		"`ErrorType`, UserID) VALUES (?, ?, ?);";
	    System.out.println(qString);
	    return;
	}if(eventID == EventListener_Main.OPT_OUT){
	    qString = "opted out";
	    System.out.println(qString);
	    return;
	}
	PreparedStatement st;
	try {
	    st = this.conn.prepareStatement(qString);
	    if(eventID == EventListener_Main.ACTIVITY_TRACK){
		st.setString(1, details1);
		st.setString(2, this.userID);
	    }else{
		st.setString(1, details1);
		st.setString(2, details2);
		st.setString(3, this.userID);
	    }
	    try{
		st.executeUpdate();
	    }catch(SQLException e){
				System.out.println(e.getMessage());
	    }finally{
		st.close();
		System.out.println("Statement closed.");
	    } 
	}catch (SQLException e) {
	    System.out.println(e.getMessage());
	}
    }	
    
    /**
     * Closes the connection with the database.
     */
    public void closeConnection(){
	System.out.println("Closing Connection.");
	try {
	    this.conn.close();
		} catch (SQLException e) {
	    e.printStackTrace();
	}System.out.println("Connection Closed.");
    }    
}
