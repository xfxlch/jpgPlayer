/**
 * 
 */
package com.luchenghao.jpgPlayer;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author luchenghao
 *
 */
public class PicFilter implements FilenameFilter {

	/* (non-Javadoc)
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	@Override
	public boolean accept(File dir, String name) {
		// TODO Auto-generated method stub
		if(name.endsWith("jpg") || name.endsWith("JPG")  || name.endsWith("gif") || name.endsWith("png")) return true;
		return false;
	}

}
