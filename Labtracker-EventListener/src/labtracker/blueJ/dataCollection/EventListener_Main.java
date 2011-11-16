package labtracker.blueJ.dataCollection;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import bluej.extensions.BlueJ;
import bluej.extensions.Extension;
import bluej.extensions.event.CompileEvent;
import bluej.extensions.event.CompileListener;

/**
 * Listens for events from BlueJ.  Delays all compiles by 15 seconds.
 * @author Veneratio
 *
 */
public class EventListener_Main extends Extension implements CompileListener {
	private EventAntennae antennae;
	private BlueJ blueJ;
	private ProgressTracking actTrack;
	private final static int TRACKING_DELAY = 5;
	public final static int COMPILE_ERROR = 11;
	public final static int COMPILE_WARNING = 13;
	public final static int ACTIVITY_TRACK = 17;
	public final static int OPT_OUT = 19;
	
	public EventListener_Main(){
		this.antennae = new EventAntennae();
	}
	
	/**
	 * Called on startup of BlueJ.  Registers as a listener for compile and class events.
	 */
	public void startup(BlueJ blueJ) {
		int n = JOptionPane.showConfirmDialog(
				new JFrame(),
			    "Is it okay if BlueJ sends anonymous information about your session to the professor? \n " +
			    "The information includes which methods you edit and what compiler errors you experience. \n" +
			    "During lab, this information helps the professor to "+
				"identify common problems that students are experiencing.",
			    "Labtracker Listenerk",
			    JOptionPane.YES_NO_OPTION);
		if(n == JOptionPane.NO_OPTION){
			//do not register any listeners if the student opted out
			this.antennae.emitEvent(EventListener_Main.OPT_OUT, "unknown", "unknown");
			return;
		}//the student did not opt out so we may register listeners
		this.blueJ = blueJ;
		this.blueJ.addCompileListener(this);	
		this.actTrack = new ProgressTracking(this.blueJ, this.antennae, EventListener_Main.TRACKING_DELAY);
		
		//Start activity tracking
		Thread t = new Thread(this.actTrack);
        t.start();
	}
	
	/**
	 * 
	 */
	public void terminate(){
		this.antennae.closeConnection();
	}
	
	/**
	 * Logs a compile error.
	 * @param compErr the compilation error event
	 */
	public void compileError(CompileEvent compErr) {
		String methodName = "unknown";
		File[] f = compErr.getFiles();
		if(f.length>0){
			methodName = this.getMethodSig(f[0].getName(), compErr.getErrorLineNumber());
		}
		this.antennae.emitEvent(EventListener_Main.COMPILE_ERROR, ""+methodName,"  Message: "
				+compErr.getErrorMessage());
	}

	/**
	 * Logs a compilation failure.
	 * @param compFail a failed compilation event
	 */
	public void compileFailed(CompileEvent compFail) {
		//this.antennae.emitEvent("Compile", "Failure","ErrLine: "+compFail.getErrorLineNumber()+"  Message: "+
		//	compFail.getErrorMessage());
	}

	/**
	 * Logs a compile warning.
	 * @param compWarn a compilation event containing a compiler warning
	 */
	public void compileWarning(CompileEvent compWarn) {
		String methodName = "unknown";
		File[] f = compWarn.getFiles();
		if(f.length>0){
			methodName = this.getMethodSig(f[0].getName(), compWarn.getErrorLineNumber());
		}
		this.antennae.emitEvent(EventListener_Main.COMPILE_WARNING, ""+methodName,"  Message: "
				+compWarn.getErrorMessage());
	}
	
	/**
	 * Logs the start of a compilation and initiates a 15 second delay.
	 * @param compInit the initialization of a compilation
	 */
	public void compileStarted(CompileEvent compInit) {
		//this.antennae.emitEvent("Compile", "Start", compInit.getClass().getName());
	}

	/**
	 * Logs the success of a compilation.  May delay the compile time.
	 * @param compFin the finished compilation event
	 */
	public void compileSucceeded(CompileEvent compFin){
		//this.antennae.emitEvent("Compile", "Success", compFin.getClass().getName());
	}

	/**
	 * Reports the extension name.
	 * @return the extension name
	 */
	public String getName() {
		return "LabtrackerLive-EventListener";
	}

	/**
	 * Reports the version number.
	 * @return the version number (MM.DD.YYYY)
	 */
	public String getVersion() {
		return "10.12.2011";
	}

	/**
	 * Reports compatibility with BlueJ.
	 * @return true
	 */
	public boolean isCompatible() {
		return true;
	}
	
	/**
	 * Dummy main to allow main-class in JAR file.
	 * @param args
	 */
	public static void main(String[] args){
		
	}
	
	private String getMethodSig(String className, int errLine){
		String theClassName = className.substring(0, className.length()-5);
		String methodName = this.actTrack.findMethod(theClassName, errLine);
		return methodName;
	}
}













