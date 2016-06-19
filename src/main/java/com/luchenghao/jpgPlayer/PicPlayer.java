/**
 * 
 */
package com.luchenghao.jpgPlayer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

/**
 * @author luchenghao
 *
 */
public class PicPlayer {
	private static final int SECONDS_IN_HOUR = 60 * 60;
	private static final int SECONDS_IN_MINUTE = 60;

	private JFrame frame;
	private final int WIDTH = 800;
	private final int HEIGHT = 600;
	// Menu
	private JMenuBar menubar;
	private JMenu fileMenu;
	private JMenu aboutMenu;
	private JMenu settingMenu;
	private JMenuItem openItem;
	private JMenuItem exitItem;
	private JMenuItem aboutItem;
	private JMenuItem settingItem;
	// Panel
	private JScrollPane jsp;
	private JPanel imagepanel;
	private JPanel processPanel;
	private JLabel label;
	private ImageIcon imgIcon;
	private String title = "零五计算机－Forever";
	private Image image;
	private String imageFilePath = "";
	private File[] files;
	private File currentFile = null;
	private int fp = 0;// file pointer
	private boolean isActionFlag = true;
	private static Logger log = Logger.getLogger(PicPlayer.class);
	private boolean isPicPlayEnd = false;
	private String mp3path = "";
	private AudioInputStream audioIS = null;
	private SourceDataLine sourceDataLine = null;
	private AudioFormat audioFormat = null;
	private boolean isStop = true;// control the play thread
	private boolean hasStop = true; // display the play thread status
	private String lastOpenPath;
	private String audioFilePath;

	JLabel labelFileName;
	JLabel labelTimeCounter;
	JLabel labelDuration;

	JButton btnOpen;
	JButton btnPlay;
	JButton btnPause;
	JSlider sliderTime;

	private GridBagLayout gridBagLayout = new GridBagLayout();

	static {
		String libpath = System.getProperty("java.library.path");
//    	libpath = libpath + "/usr/local/lib";
//    	System.setProperty("java.library.path",libpath);
    	System.out.println("TTT:" + libpath);
    	System.loadLibrary("casampledsp");
//		System.load("/usr/local/lib/casampledsp-0.9.11.dylib");
//		System.loadLibrary("casampledsp-0.9.11.dylib");
//		System.load("/Users/luchenghao/.m2/repository/com/tagtraum/casampledsp/0.9.11/casampledsp-0.9.11.dylib");
	}
	public PicPlayer() {
//		System.loadLibrary("casampledsp-0.9.11.dylib");
//		System.setProperty("java.library.path", "/Users/luchenghao/.m2/repository/com/tagtraum/casampledsp/0.9.11/");
//		System.load("/Users/luchenghao/.m2/repository/com/tagtraum/casampledsp/0.9.11/casampledsp-0.9.11.dylib");
		initComponent();
	}

	private void initComponent() {
		log.info("Here we are in initComponent method.");
		imageFilePath = System.getProperty("user.dir") + File.separatorChar + "/src/main/resource/images";
		log.info("Image File Path:" + imageFilePath);
		mp3path = imageFilePath + File.separatorChar + "Bo-toxx mind Society-Cheeno.mp3";
		frame = new JFrame();
		frame.setSize(WIDTH, HEIGHT);
		int w = (Toolkit.getDefaultToolkit().getScreenSize().width - WIDTH) / 2;
		int h = (Toolkit.getDefaultToolkit().getScreenSize().height - HEIGHT) / 2;
		frame.setLocation(w, h);
		frame.setTitle(title);
		menubar = new JMenuBar();
		frame.setJMenuBar(menubar);
		fileMenu = new JMenu("File");
		aboutMenu = new JMenu("About");
		settingMenu = new JMenu("Setting");
		menubar.add(fileMenu);
		menubar.add(aboutMenu);
		menubar.add(settingMenu);

		exitItem = new JMenuItem("Exit");
		aboutItem = new JMenuItem("About Author");
		settingItem = new JMenuItem("Setting");

		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		aboutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				javax.swing.JOptionPane.showMessageDialog(frame, "Author:Luchenghao \nmailto:luch2046@gmail.com",
						"关于作者", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		settingItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				javax.swing.JOptionPane.showConfirmDialog(frame, "Author: Luchenghao \nmailto:luch2046@gmail.com",
						"关于作者", JOptionPane.PLAIN_MESSAGE);
			}
		});

		fileMenu.add(exitItem);
		aboutMenu.add(aboutItem);
		settingMenu.add(settingItem);

		label = new JLabel();
		imagepanel = new JPanel();

		// processPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,20,5));
		processPanel = new JPanel(gridBagLayout);
		labelFileName = new JLabel("Playing File:");
		labelTimeCounter = new JLabel("00:00:00");
		labelDuration = new JLabel("00:00:00");

		btnOpen = new JButton("Open File");
		btnPlay = new JButton("Play");
		btnPause = new JButton("Pause");

		btnOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser fileChooser = null;
				if (lastOpenPath != null && !lastOpenPath.equals("")) {
					fileChooser = new JFileChooser(lastOpenPath);
				} else {
					fileChooser = new JFileChooser();
				}
				FileFilter wavFilter = new FileFilter() {

					@Override
					public boolean accept(File f) {
						// TODO Auto-generated method stub
						if (f.isDirectory()) {
							return true;
						} else {
							return f.getName().toLowerCase().endsWith(".mp3")
									|| f.getName().toLowerCase().endsWith(".wav");
						}
					}

					@Override
					public String getDescription() {
						// TODO Auto-generated method stub
						return "Sound file (*.wav/mp3)";
					}
				};

				fileChooser.setFileFilter(wavFilter);
				fileChooser.setDialogTitle("Open Audio File");
				fileChooser.setAcceptAllFileFilterUsed(false);
				int userChoice = fileChooser.showOpenDialog(frame);
				if (userChoice == JFileChooser.APPROVE_OPTION) {
					audioFilePath = fileChooser.getSelectedFile().getAbsolutePath();
					log.info("audioFilePath:" + audioFilePath);
					lastOpenPath = fileChooser.getSelectedFile().getParent();
					playback();
				}
			}
		});
		
		btnPlay.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(!isPlaying){
					playback();
				} else {
					stopPlaying();
				}
			}
		});
		
		sliderTime = new JSlider();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.anchor = GridBagConstraints.WEST;
		// constraints.fill = GridBagConstraints.HORIZONTAL;//shui ping kuo
		// zhang zujian

		sliderTime.setPreferredSize(new Dimension(400, 20));
		sliderTime.setEnabled(false);
		sliderTime.setValue(0);

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 3;
		processPanel.add(labelFileName, constraints);

		constraints.anchor = GridBagConstraints.CENTER;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		processPanel.add(labelTimeCounter, constraints);

		constraints.gridx = 1;
		processPanel.add(sliderTime, constraints);

		constraints.gridx = 2;
		processPanel.add(labelDuration, constraints);

		JPanel panelBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 2));
		panelBtns.add(btnOpen);
		panelBtns.add(btnPlay);
		panelBtns.add(btnPause);

		constraints.gridwidth = 3;
		constraints.gridx = 0;
		constraints.gridy = 2;
		processPanel.add(panelBtns, constraints);

		imagepanel.add(label);
		jsp = new JScrollPane(imagepanel);
		frame.add(processPanel, BorderLayout.NORTH);
		frame.add(jsp, BorderLayout.CENTER);
		image = Toolkit.getDefaultToolkit().getImage(imageFilePath);
		File file = new File(imageFilePath);
		files = file.listFiles(new PicFilter());

		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(false);// disable the max button
		playPic();
		// playMp3();
		// playMusic();
	}

	protected void stopPlaying() {
		// TODO Auto-generated method stub
		isPause = false;
		btnPause.setText("Pause");
		btnPause.setEnabled(false);
		timer.reset();
		timer.interrupt();
		
		playbackThread.interrupt();
	}

	PlayingTimer timer;
	private boolean isPlaying = false;
	private boolean isPause = false;
	Thread playbackThread;
	protected void playback() {
		// TODO Auto-generated method stub
		timer = new PlayingTimer(labelTimeCounter, sliderTime);
		timer.start();
		isPlaying = true;
		playbackThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					btnPlay.setText("Stop");
					btnPlay.setEnabled(true);

					btnPause.setText("Pause");
					btnPause.setEnabled(true);
					mp3path = audioFilePath;
					player.load(mp3path);
//					FileInputStream fis = new FileInputStream(mp3path);
//					BufferedInputStream bis = new BufferedInputStream(fis);
//					player = new javazoom.jl.player.Player(bis);
					timer.setAudioPlayer(player.getAudioClip());
					labelFileName.setText("Playing File: " + audioFilePath);
					sliderTime.setMaximum((int) player.getClipSecondLength());

					labelDuration.setText(player.getClipLengthString());
					player.play();
					resetControls();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

		playbackThread.start();
	}

	protected void resetControls() {
		// TODO Auto-generated method stub
		timer.reset();
		timer.interrupt();
		btnPlay.setText("Play");
		btnPlay.setEnabled(false);
		isPlaying = false;
	}

	protected String getClipLengthString() {
		// TODO Auto-generated method stub
		String length = "“";
		long hour = 0;
		long minute = 0;
		long seconds = player.getClipSecondLength();//player.getPosition() / 1_000;
		log.info("Seconds: " + seconds);
		if (seconds >= SECONDS_IN_HOUR) {
			hour = seconds / SECONDS_IN_HOUR;
			length = String.format("%02d:", hour);
		} else {
			length += "00:";
		}

		minute = seconds - hour * SECONDS_IN_HOUR;
		if (minute >= SECONDS_IN_MINUTE) {
			minute = minute / SECONDS_IN_MINUTE;
			length += String.format("%02d:", minute);
		} else {
			minute = 0;
			length += "00:";
		}

		long second = seconds - hour * SECONDS_IN_HOUR - minute * SECONDS_IN_MINUTE;
		length += String.format("%02d", second);

		return length;
	}

	private AudioPlayer player = new AudioPlayer();

	class JlThread implements Runnable {

		@Override
		public void run() {
			try {
				player.play();
				if (!player.getAudioClip().isRunning()) {
					log.info("mp3 play completed");
					playMusic();
				}
			} catch (Exception e) {
				log.info(e);
			}
		}
	}

	public void playMusic() {
		try {
//			FileInputStream fis = new FileInputStream(mp3path);
//			BufferedInputStream bis = new BufferedInputStream(fis);
			player.load(mp3path);
			
		} catch (Exception e) {
			log.info("Problem playing file " + mp3path);
			log.info(e);
		}

		// run in new thread to play in background
		// new Thread() {
		// public void run() {
		// try {
		// player.play();
		// if (player.isComplete()) {
		// log.info("mp3 play completed");
		// player.play();
		// }
		// } catch (Exception e) {
		// log.info(e);
		// }
		// }
		// }.start();
		new Thread(new JlThread()).start();
	}

	private void playMp3() {
		log.info("Mp3 location:" + mp3path);
		// TODO Auto-generated method stub
		isStop = true;
		while (!hasStop) {
			log.info(".");
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// 取得文件输入流
		try {
			audioIS = AudioSystem.getAudioInputStream(new File(mp3path));
			log.info(new File(mp3path).toURI().toURL());
			audioFormat = audioIS.getFormat();
			// 转换MP3文件编码
			if (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
				audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(), 16,
						audioFormat.getChannels(), audioFormat.getChannels() * 2, audioFormat.getSampleRate(), false);
				audioIS = AudioSystem.getAudioInputStream(audioFormat, audioIS);
			}
			// 打开输出设备
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
			sourceDataLine.open(audioFormat, sourceDataLine.getBufferSize());
			sourceDataLine.start();
			// 创建独立线程进行播放
			isStop = false;
			Thread playThread = new Thread(new PlayMp3Thread());
			log.info("Mp3 thread: " + playThread.getName());
			playThread.start();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class PlayMp3Thread implements Runnable {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		byte tempBuffer[] = new byte[320];

		@Override
		public void run() {
			// TODO Auto-generated method stub
			int cnt;
			hasStop = false;
			// 读取数据到缓存数据
			try {
				while ((cnt = audioIS.read(tempBuffer, 0, tempBuffer.length)) != -1) {
					if (isStop) {
						break;
					}
					if (cnt > 0) {
						// 写入缓存数据
						sourceDataLine.write(tempBuffer, 0, cnt);
					}
				}

				// block 等待临时数据被输出为空
				sourceDataLine.drain();
				sourceDataLine.close();
				hasStop = true;
				if (audioIS.read(tempBuffer, 0, tempBuffer.length) == -1) {
					playMp3();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private void playPic() {
		// TODO Auto-generated method stub
		Thread play = new Thread(new PlayPicThread());
		play.start();
	}

	class PlayPicThread implements Runnable {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			isPicPlayEnd = false;
			log.info("Image Size:" + files.length);
			for (int i = 0; i < files.length; i++) {
				currentFile = files[i];
				log.info(currentFile);
				isActionFlag = true;
				try {
					Thread.sleep(15000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				setImage(currentFile, isActionFlag);
				if (i == files.length - 1) {
					isPicPlayEnd = true;
				}
				int count = i + 1;
				log.info("counting " + count);
				log.info("picture: " + count + "/" + files.length);
			}

			if (isPicPlayEnd) {
				playPic();
				log.info("Playing one more time");
			}
		}

		private void setImage(File currentFile, boolean isActionFlag) {
			// TODO Auto-generated method stub
			imgIcon = new ImageIcon(currentFile.getPath());
			log.info("Pic Path: " + currentFile.getPath());
			int cw;
			int ch;
			int iw = imgIcon.getIconWidth();
			int ih = imgIcon.getIconHeight();
			if (isActionFlag) {
				cw = jsp.getWidth();
				ch = jsp.getHeight();
				if (iw > cw || ih > ch) {
					if (cw / ch > iw / ih) {
						iw = iw * (ch - 50) / ih;
						ih = ch - 50;
						imgIcon.setImage(setFixed(currentFile, iw, ih));
					} else {
						ih = (cw - 50) * ih / iw;
						iw = cw - 50;
						imgIcon.setImage(setFixed(currentFile, iw, ih));
					}
				}
				imagepanel.setLayout(null);
				label.setBounds((cw - iw) / 2, (ch - ih) / 2, iw, ih);
			} else {
				cw = imagepanel.getWidth();
				ch = imagepanel.getHeight();
				imagepanel.setLayout(new java.awt.FlowLayout());
			}
			label.setIcon(imgIcon);
		}

		private Image setFixed(File currentFile, int iw, int ih) {
			// TODO Auto-generated method stub
			BufferedImage bi = null;
			try {
				bi = javax.imageio.ImageIO.read(currentFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return bi.getScaledInstance(iw, ih, Image.SCALE_SMOOTH);
		}

	}

}