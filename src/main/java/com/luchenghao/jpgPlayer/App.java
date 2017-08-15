package com.luchenghao.jpgPlayer;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

/**
 * App Class
 */
public class App implements Runnable {
	private static Logger log = Logger.getLogger(App.class);
    public static void main( String[] args ) {
    	log.info( "Hello World!" );
    	SwingUtilities.invokeLater(new App());
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
