package labtracker.blueJ.dataCollection;

import java.io.*;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import bluej.extensions.BClass;
import bluej.extensions.BConstructor;
import bluej.extensions.BMethod;
import bluej.extensions.BlueJ;
import bluej.extensions.ClassNotFoundException;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;
import bluej.extensions.editor.Editor;
import bluej.extensions.editor.TextLocation;

/** 
 * A class for tracking progress in BlueJ.  The progress tracked is method
 * being worked on.  Some utility methods are included to help with this.
 * @author Kyle Wenholz, Katie Mueller
 **/
public class ProgressTracking implements Runnable {
	private BlueJ blueJ;
	private EventAntennae antennae;
	private Map<String, Integer> classLengths;
	private int delay;
	private String lastTracked;
	
    /**
     * A very basic constructor that just takes necessary components.
     * @param blueJ an instance of BlueJ for getting class names and such
     * @param antennae a class for connecting to a database for sending 
     *     information
     * @param delay how often to actually check the activity of the user
     **/
    public ProgressTracking(BlueJ blueJ, EventAntennae antennae, int delay){
	this.blueJ = blueJ;
	this.antennae = antennae;
	this.classLengths = new HashMap<String, Integer>();
	this.delay = delay*1000;
	this.lastTracked = "unknown";
    }
    
    /**
     * This method is called when the instance is passed to a new thread.
     */
    public void run() {
	while(true){
	    try {
		Thread.sleep((long)this.delay);
		String msg = this.getCurrentMethod();
		//now handle if the msg is null, we want to simply return the last one being worked on
		//THIS MAY NEED TO BE DEPRECATED SINCE "class name" IS NOW THE DEFAULT RETURN
		//    7/12/11
		if(msg.equals("unknown")){
		    msg = this.lastTracked;
		}else{
		    this.lastTracked = msg;
		}
		this.antennae.emitEvent(EventListener_Main.ACTIVITY_TRACK, this.lastTracked, "unknown");		
	    } catch (InterruptedException e) {
		System.out.println(e.getMessage());
	    }catch (ProjectNotOpenException e){
		System.out.println(e.getMessage());
	    }catch (PackageNotFoundException e){
		System.out.println(e.getMessage());
	    }
	    

	    //this is the diff code!
	    try {
	    	
	        Runtime rt = Runtime.getRuntime();
	        File path = this.blueJ.getCurrentPackage().getProject().getDir();
	        path = new File (path, "extensions");
	        path = new File(path, "diffj");
	        path = new File(path, "bin");
	        Process pr = rt.exec("cmd /c diffj ../../../starter ../../../ ", null, path);
	        
	        BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));     
	        
	        String line = null;
	        String className = null;
	        String methodName = null;
	        String addedCode = "";
	        boolean hasOffset = false;
	        int offset = 1;
	        
	        while((line = input.readLine()) != null) {

	        	//grab the class name
	            if(line.endsWith(".java")) {
	                className = line.substring(line.lastIndexOf("\\")+1,line.length()-5);
	            }
	            
	            String partLine = line;
	            //check if the message says they have added code
	            while(partLine.length() > 15) {
	                if(partLine.startsWith("code added in ") || partLine.startsWith("method added: ")) {
		            	if(addedCode!=""){
		            		//emit the previous event
		            		this.antennae.emitEvent(EventListener_Main.CODE_SNIPPET, className +": "+ methodName, addedCode);
		            		addedCode = "";
		            	}
		            	//reset things
		            	hasOffset = false;
		                offset = 1;
	                    methodName = partLine.substring(14);
	                }
	                partLine = partLine.substring(1);
	            }
	            
	            if(line.startsWith(">")) {
	            	//find the indentation of this chunk of code
	            	if(!hasOffset) {
	                    while(line.charAt(offset)=='\u0020') {
	                        offset++;
	                    }
	                    hasOffset = true;
	                }
	            	//add the line, minus the chunk's indentation
	            	addedCode += line.substring(offset) + "\n";
	            }
	        }

	        //emit the final event
	        if(addedCode!=""){
        		JOptionPane.showMessageDialog(null, className +": "+ methodName +"\n"+ addedCode);
	        	this.antennae.emitEvent(EventListener_Main.CODE_SNIPPET, className +": "+ methodName, addedCode);
	        	addedCode = "";
	        }
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
    }
    
    /**
     * Retrieves the method currently being worked on. If no 
     * method name is found (i.e. anything above methods is 
     * being worked on) the method returns the class name.
     * LIMITATIONS: Based on most recently changed in terms of changes in text length, 
     * probably isn't capable of working with more than one package open.
     * @return name of the method currently being worked on
     * @throws PackageNotFoundException 
     * @throws ProjectNotOpenException 
     */
    private String getCurrentMethod() throws ProjectNotOpenException, PackageNotFoundException{
	String ret = "unknown";
	BClass[] classes = this.blueJ.getCurrentPackage().getClasses();
	for(BClass c : classes){
	    //System.out.println("I'm looping through :"+c.toString());
	    Editor ed = c.getEditor();
	    if(!this.classLengths.containsKey(c.getName())){
		//the class was not in the length map so we add it and return it's method being worked on
		this.classLengths.put(c.getName(), ed.getTextLength());
		//return the method being worked on
		if(ret=="unknown"){
			ret = ProgressTracking.findMethod(c, ed);
		}
	    }if(this.classLengths.get(c.getName())!=ed.getTextLength()){
		//the class has changed, find the method being worked on and add it to the return
		this.classLengths.put(c.getName(), ed.getTextLength());
		//return the method being worked on  
		ret = ProgressTracking.findMethod(c, ed);
	    }
	}
	return ret;
    }
    
    /**
     * Finds the method the caret location is in.  The updating is imperfect, 
     * but it works in a crude sense.
     * The current class being worked on is the first found with a changed \
     * length since the last check.
     * @param c A BlueJ class instance where the method should be
     * @param ed an editor instance for the class
     * @return a string for the method name
     */
    public static String findMethod(BClass c,Editor ed){
	String ret = c.getName();
	int numLine = ed.getCaretLocation().getLine();
	BMethod[] bm = null;
	BConstructor[] bc = null;
	try {
	    bm = c.getDeclaredMethods();
	    bc = c.getConstructors();
	} catch (ProjectNotOpenException e) {
	    e.printStackTrace();
	    return c.getName();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	    return c.getName();
	}
	for(int i = numLine; i>0 && ret.equals(c.getName()); i--){
	    //grab each line of text as we go up to the top
	    String lineText = ed.getText(new TextLocation(i, 0), 
	        new TextLocation(i, ed.getLineLength(i)-1));
	    //System.out.println(lineText);
	    for(BMethod meth : bm){
			String mSig = meth.toString();
			mSig = mSig.substring(0, mSig.indexOf("("));

		/** THIS IS KYLE'S OLD VERSION. IT DID NOT WORK WITH RETURN TYPES.
		//check if the current line contains any of the method 
		//declarations
		if(lineText.contains(mSig)){
		    ret = ret+": "+meth.toString();
		    return ret;			
		}for(BConstructor con: bc){
		    if(lineText.contains(con.toString())){
			ret = con.toString();
			return ret;
		    }
		}
		*/

			String methSig[] = mSig.split(" ");
			if(lineText.contains(methSig[0]) && lineText.contains(methSig[methSig.length-1])){
				ret += ": " + meth.toString();
				return ret;
			}
	    }
	    
		for(BConstructor con : bc){
			String conSig[] = con.toString().split(" ");
			if(lineText.contains(conSig[0]) && lineText.contains(conSig[conSig.length-1])){
				ret = con.toString();
				return ret;
			}
		}
		
	}
	//System.out.println("This is ret in findMethod: "+ret);
	return ret;
    }
    
    /**
     * Finds the method the lineNumber is associated with.
     * @param className the name of the class
     * @param lineNumber line number of location
     * @return method name as a string
     */
    public String findMethod(String className, int lineNumber){
	String ret = "unknown";
	System.out.println("getting "+className);
	BClass c = null;
	//grab the blueJ class
	try {
	    c = this.blueJ.getCurrentPackage().getBClass(className);
	} catch (ProjectNotOpenException e1) {
	    System.out.println(e1.getMessage());
	    
	    JOptionPane.showMessageDialog(new JFrame(), ret);
	    return ret;
	} catch (PackageNotFoundException e1) {
	    System.out.println(e1.getMessage());
	    
	    JOptionPane.showMessageDialog(new JFrame(), ret);
	    return ret;
	}ret = c.getName();
	Editor ed = null;
	//grab an editor for the class
	try {
	    ed = c.getEditor();
	} catch (ProjectNotOpenException e1) {
	    e1.printStackTrace();
	    return ret;
	} catch (PackageNotFoundException e1) {
	    e1.printStackTrace();
	    return ret;
	}
	for(int i = lineNumber; i>0 && (ret.equals("unknown") 
	    || ret.equals(c.getName())); i--){
	    //grab each line of text as we go up to the top
	    String lineText = ed.getText(new TextLocation(i, 0), 
	    new TextLocation(i, ed.getLineLength(i)-1));
	    //System.out.println(lineText);
	    try {
		for(BMethod m : c.getDeclaredMethods()){
		    String mSig = m.toString();
		    mSig = mSig.substring(0, mSig.indexOf("("));
		    //check if the current line contains any of the method 
		    //declarations
		    if(lineText.contains(mSig)){
			ret = ret+": "+m.toString();
			System.out.println("This is ret for a compile error: "+
			    ret);
			return ret;
		    }for(BConstructor con: c.getConstructors()){
			if(lineText.contains(con.toString())){
			    ret = con.toString();
			    return ret;
			    
			}
		    }
		}
	    } catch (ProjectNotOpenException e) {
		System.out.println(e.getMessage());
	    } catch (ClassNotFoundException e) {
		System.out.println(e.getMessage());
	    }
	}
	return ret;
    }
    
}
