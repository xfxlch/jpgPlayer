package com.luchenghao.jpgPlayer;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

/**
 * App Class
 */
public class App implements Runnable {
	public App(){}
	public App(String name) {
		Thread.currentThread().setName(name);
	}
	private static Logger log = Logger.getLogger(App.class);
    public static void main( String[] args ) {
    	log.info( "Hello World!" );
    	SwingUtilities.invokeLater(new App("Main Thread"));
//    	Thread thread = new Thread(new App(), "Main Thread");
//    	thread.start();
    }
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		new PicPlayer();
	}
}
