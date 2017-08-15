/**
 * <br>
 * do what you want to do and never stop it.
 * <br>
 */
package com.luchenghao.utils;

/**
 * @author Jack
 * Aug 14, 2017
 * <br>
 */
public class Native2AsciiUtil {
	public String unicodeEncoder(final String src) {
		char[] chars = src.toCharArray();
		String unicodeBytes = "";
		for(int i = 0; i < chars.length; i++) {
			String hexb = Integer.toHexString(chars[i]);
			System.out.println("hexb:" + hexb);
			if (hexb.length() <= 2) {
				hexb = "00" + hexb;
			}
			unicodeBytes = unicodeBytes + "\\u" + hexb;
			System.out.println("unicodeBytes:" + unicodeBytes);
		}
		return src;
	}
	
	public static void main(String[] args){
		System.out.println(":"+"\u67f4\u8273\u7130");
		new Native2AsciiUtil().unicodeEncoder("柴艳焰");
	}
}
