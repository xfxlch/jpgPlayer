/**
 * 
 */
package com.luchenghao.jpgPlayer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.JLabel;
import javax.swing.JSlider;

/**
 * @author luchenghao
 *
 */
public class PlayingTimer2 extends Thread {
	private DateFormat dateFormater = new SimpleDateFormat("HH:mm:ss");
	private boolean isRunning = false;
	private boolean isPause = false;
	private boolean isReset = false;
	private long startTime;
	private long pauseTime;
	
	private JLabel labelRecordTime;
	private JSlider slider;
	private javazoom.jl.player.Player audioPlayer;
	
	public void setAudioPlayer(javazoom.jl.player.Player audioPlayer) {
		this.audioPlayer = audioPlayer;
	}
	
	PlayingTimer2 (JLabel labelRecordTime, JSlider slider) {
		this.labelRecordTime = labelRecordTime;
		this.slider = slider;
	}
	
	public void run(){
		isRunning = true;
		startTime = System.currentTimeMillis();
		while(isRunning){
			try{
				Thread.sleep(100);
				if(!isPause){
					if(audioPlayer != null && !audioPlayer.isComplete()) {
						labelRecordTime.setText(toTimeString());
						int currentSecond = (int) audioPlayer.getPosition()/1_000;
						slider.setValue(currentSecond);
					}
				} else {
					pauseTime += 100;
				}
			} catch(InterruptedException ex){
				ex.printStackTrace();
				if(isReset){
					slider.setValue(0);
					labelRecordTime.setText("00:00:00");
					isRunning = false;
					break;
				}
			}
		}
	}

	private String toTimeString() {
		// TODO Auto-generated method stub
		long now = System.currentTimeMillis();
		Date current = new Date(now - startTime - pauseTime);
		dateFormater.setTimeZone(TimeZone.getTimeZone("GMT"));
		String timeCounter = dateFormater.format(current);
		return timeCounter;
	}
	
	public void reset(){
		isReset = true;
		isRunning =false;
	}
	
	public void pauseTimer(){
		isPause = true;
	}
	
	public void resumeTimer(){
		isPause = false;
	}
}
