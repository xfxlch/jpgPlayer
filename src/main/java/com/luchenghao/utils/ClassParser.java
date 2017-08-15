/**
 * <br>
 * do what you want to do and never stop it.
 * <br>
 */
package com.luchenghao.utils;

/**
 * @author Jack
 * Aug 15, 2017
 * <br>
 */
public class ClassParser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ClassParser().readClassLoader();
	}
	
	public void readClassLoader() {
		System.out.println(this.getClass().getResource("/"));
	}

}
