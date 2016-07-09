/**
 * <br>
 * do what you want to do and never stop it.
 * <br>
 */
package com.test;

/**
 * @author Jack
 * Jul 2, 2016
 * <br>
 */
public class Test2 {
	private int a=222;
	public void printA(){
		System.out.println("Outer a:"+ this.a);
	}
	
	public Inner inner(){
		return new Inner();
	}
	
	public class Inner{
		
		private int a = 333;
		public void printInnerA(){
			System.out.println("Inner a:" + this.a);
		}
	}
	
	public static void main(String[] args){
		new Test2().printA();
		
		new Test2().inner().printInnerA();
	}
}
