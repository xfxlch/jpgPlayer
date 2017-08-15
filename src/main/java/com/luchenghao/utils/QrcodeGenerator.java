/**
 * <br>
 * do what you want to do and never stop it.
 * <br>
 */
package com.luchenghao.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * @author Jack Aug 15, 2017 <br>
 */
public class QrcodeGenerator {
	
	public static void main(String[] args) {
		
		File file = new File("F:\\qrcode.jpg");
		String fileType = "jpg";
		String text = "http://mvnrepository.com/artifact/com.google.zxing/core/3.3.0";
		int width = 256;
		new QrcodeGenerator().writeQRCode(file, fileType, text, width);
	}

	public static void writeQRCode(File file, String fileType,String text, int width) {
		QRCodeWriter writer = new QRCodeWriter();
		//int width = 256, height = 256;
		BufferedImage image = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB); // create an empty image
		int white = 255 << 16 | 255 << 8 | 255;
		int black = 0;
		try {
			BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, width);
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < width; j++) {
					image.setRGB(i, j, bitMatrix.get(i, j) ? black : white); // set pixel one by one
				}  
			}
				
				try { 
					ImageIO.write(image, fileType, file); // save QR image to disk
				} catch (IOException e) {
					e.printStackTrace();
		}
		}catch (WriterException e1) {
			e1.printStackTrace();
		}
	}
}
