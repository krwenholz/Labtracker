package labtracker.blueJ.dataCollection;

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

public class ProgressTracking implements Runnable {
	private BlueJ blueJ;
	private EventAntennae antennae;
	private Map<String, Integer> classLengths;
	private int delay;
	private String lastTracked;
	
	
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
				if(msg.equals("unknown")){
					msg = this.lastTracked;
				}else{
					this.lastTracked = msg;
				}
				this.antennae.emitEvent(EventListener_Main.ACTIVITY_TRACK, this.lastTracked, "unknown");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			}catch (ProjectNotOpenException e){
				System.out.println(e.getMessage());
			}catch (PackageNotFoundException e){
				System.out.println(e.getMessage());
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
				ret = ProgressTracking.findMethod(c, ed);
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
	 * Finds the method the caret location is in.  The updating is imperfect, but it works in a crude sense.
	 * @param c
	 * @param ed
	 * @return
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return c.getName();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return c.getName();
		}
		for(int i = numLine; i>0 && ret.equals(c.getName()); i--){
			//grab each line of text as we go up to the top
			String lineText = ed.getText(new TextLocation(i, 0), new TextLocation(i, ed.getLineLength(i)-1));
			//System.out.println(lineText);
			for(BMethod meth : bm){
				String mSig = meth.toString();
				mSig = mSig.substring(0, mSig.indexOf("("));
				//check if the current line contains any of the method declarations
				if(lineText.contains(mSig)){
					ret = ret+": "+meth.toString();
					return ret;			
				}for(BConstructor con: bc){
					if(lineText.contains(con.toString())){
						ret = con.toString();
						return ret;
					}
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
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return ret;
		} catch (PackageNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return ret;
		}
		for(int i = lineNumber; i>0 && (ret.equals("unknown") || ret.equals(c.getName())); i--){
			//grab each line of text as we go up to the top
			String lineText = ed.getText(new TextLocation(i, 0), new TextLocation(i, ed.getLineLength(i)-1));
			//System.out.println(lineText);
			try {
				for(BMethod m : c.getDeclaredMethods()){
					String mSig = m.toString();
					mSig = mSig.substring(0, mSig.indexOf("("));
					//check if the current line contains any of the method declarations
					if(lineText.contains(mSig)){
						ret = ret+": "+m.toString();
						System.out.println("This is ret for a compile error: "+ret);
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
