import java.awt.GraphicsEnvironment;
import java.io.BufferedOutputStream;
import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.swing.JOptionPane;

public class ConsoleStarter {
	
    private String title = "Console Application";
	private int columns = 130;
	private int lines = 40;
	private boolean debug = false;
	private int ram = -1;
	private Class<?> classInstance = null;
	private String[] vm_args = null;
	private String[] additional_args = null;
	
	public ConsoleStarter() {
	    this("Console Application", -1, -1, false, -1, null, null);
	}
	
	public ConsoleStarter(String title) {
	    this(title, -1, -1, false, -1, null, null);
	}
	
	public ConsoleStarter(String title, int ram) {
	    this(title, -1, -1, false, ram, null, null);
	}
	
	public ConsoleStarter(String title, boolean debug) {
	    this(title, -1, -1, debug, -1, null, null);
	}
	
	public ConsoleStarter(String title, int lines, int columns) {
        this(title, lines, columns, false, -1, null, null);
    }
	
	public ConsoleStarter(String title, String... additional_args) {
	    this(title, -1, -1, false, -1, null, additional_args);
	}
	
	public ConsoleStarter(String title, String[] vm_args, String... additional_args) {
        this(title, -1, -1, false, -1, vm_args, additional_args);
    }
	
	public ConsoleStarter(String title, int lines, int columns, boolean debug) {
	    this(title, lines, columns, debug, -1, null, null);
	}
	
	public ConsoleStarter(String title, int lines, int columns, int ram, boolean debug) {
        this(title, lines, columns, debug, ram, null, null);
    }
	
	public ConsoleStarter(String title, int lines, int columns, boolean debug, int ram, String[] vm_args, String... additional_args) {
	    if (title != null)
	        this.title = title;
	    if (lines != -1)
	        this.lines = lines;
	    if (columns != -1)
	        this.columns = columns;
	    this.debug = debug;
	    if (ram != -1)
	        this.ram = ram;
	    this.classInstance = ConsoleStarter.class;
	    this.vm_args = vm_args;
	    this.additional_args = additional_args;
	        
	}
	
	public void start() {
	    if (!startNative())
	        System.exit(0);
	}
	
	private boolean startNative() {
		Console console = System.console();
		if (console == null && !GraphicsEnvironment.isHeadless()) {
			int debugging = 0;
			if (this.debug == true) {
			    debugging = JOptionPane.showOptionDialog(null, "Starting " + title + "...\nDo you want enable the Debug mode?", title, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[] {"No", "Yes"}, 0);
			}
			String filename = null;
			try {
				filename = URLDecoder.decode(classInstance.getProtectionDomain().getCodeSource().getLocation().toString().substring(6), "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			try {
				File batch = new File("Launcher.bat");
				if(batch.exists()) {
	                batch.delete();
	                batch.createNewFile();
	            }
                PrintWriter writer = new PrintWriter(batch);
                writer.println("@echo off");
                writer.println("title " + title +  " ");
                writer.println("mode con: cols=" + columns  + " lines=" + lines);
                writer.println("java " + (vm_args == null ? "" : String.join(" ", vm_args)) + (ram != -1 ? "-Xmx" +ram + "MB" : "") + " -jar \"" + filename + "\"" + (debugging == 1 ? " debug" : "") + " " + (additional_args == null ? "" : String.join(" ", additional_args)));
                writer.println("pause");
                writer.flush();
                writer.close();
	            Runtime.getRuntime().exec(new String[] {"cmd", "/c", "start", "\"\"", batch.getPath()});
	            return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return true;
		}
	}
	
	
	public PrintStream outputFile(String name) throws IOException {
	       return new PrintStream(new BufferedOutputStream(new FileOutputStream(name)), true);
	   }
}