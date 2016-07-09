/**
 * <br>
 * do what you want to do and never stop it.
 * <br>
 */
package com.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * @author Jack
 * Jun 26, 2016
 * <br>
 */
public class Test extends Thread {

	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	static int i =0;
	static ArrayList<String> list = null;
	public static void main(String[] args) {
		list = new ArrayList<String>(Arrays.asList("a","b","c","d"));
		Iterator<String>  iterator = list.iterator();
		while(iterator.hasNext()){
			String temp = (String) iterator.next();
			if("a".equals(temp)){
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				iterator.remove();
			}
		}
		
		new Thread(new Test()).start();
		
		System.out.println(i);
//		System.out.println("Test"+list);
		
//		for(String s:list){
//		   if(s.equals("a")){
//		     list.remove(s);
//		   }
//		}

	}
	
	@Override
	public void run(){
		System.out.println("run()");
		System.out.println(++i);
		if(list == null) {
			list = new ArrayList<String>();
		} else {
			for(int i = 0 ; i < 50; i ++) {
				list.add("22");
			}
		}
	}

}
