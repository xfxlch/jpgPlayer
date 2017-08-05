package com.luchenghao.jpgPlayer;

import org.apache.log4j.Logger;

/**
 * App Class
 */
public class App 
{
	private static Logger log = Logger.getLogger(App.class);
    public static void main( String[] args )
    {
    	log.info( "Hello World!" );
        new PicPlayer();
    }
}
