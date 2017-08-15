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
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
	
	private static final String CLASS = "PicPlayer";
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
	private JMenuItem exitItem;
	private JMenuItem aboutItem;
	private JMenuItem howtoItem;
	private JMenuItem imageSettingItem;
	// Panel
	private JScrollPane jsp;
	private JPanel imagepanel;
	private JPanel processPanel;
	private JLabel label;
	private ImageIcon imgIcon;
	private String title = "这是一个简单的图片和音频播放器，如果不会用，点击setting->how to，这里有使用说明－Forever";
	private String imageFilePath = "";
	private String[] webImages;
	private File[] files;
	private URL currentUrl = null;
	private File currentFile = null;
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
	private String audioFilePath="untilTheEndOfTheWorld.mp3";

	JLabel labelFileName;
	JLabel labelTimeCounter;
	JLabel labelDuration;

	JButton btnOpen;
	JButton btnPlay;
	JButton btnPause;
	JSlider sliderTime;

	private GridBagLayout gridBagLayout = new GridBagLayout();

	public PicPlayer() {
		String libpath = System.getProperty("java.library.path");
		log.info("PATH:" + libpath);
		try {
			initComponent();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initComponent() throws IOException {
		log.info("Here we are in initComponent method.");
		
//		CodeSource src = getClass().getProtectionDomain().getCodeSource();
//		if (src != null) {
//		  URL jar = src.getLocation();
//		  System.out.println("jar:" + jar);
//		  ZipInputStream zip = new ZipInputStream(jar.openStream());
//		  while(true) {
//		    ZipEntry e = zip.getNextEntry();
//		    System.out.println("e:" + e);
//		    if (e == null)
//		      break;
//		    String name = e.getName();
//		    System.out.println("name:" + name);
//		    if (name.startsWith("resources/images/") && name.endsWith(".jpg")) {
//		    	System.out.println("Success:" + name);
//		    }
//		  }
//		} else {
//			System.out.println("Fail:");
//		}
		System.out.println("Class Name:" + getClass().getName()+",XX"+"\u4e01");
		imageFilePath = getClass().getResource("/images").getPath();//direct run
//		imageFilePath = getClass().getResource("/resources/images").getPath(); 
		log.info("Image File Path:" + imageFilePath);
		//mp3path = imageFilePath + File.separatorChar + "Bo-toxx mind Society-Cheeno.mp3";
		frame = new JFrame(title);
		frame.setSize(WIDTH, HEIGHT);
		int w = (Toolkit.getDefaultToolkit().getScreenSize().width - WIDTH) / 2;
		int h = (Toolkit.getDefaultToolkit().getScreenSize().height - HEIGHT) / 2;
		System.out.println("x:" + w + ", y:" +h );
		frame.setLocation(w, h);
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
		howtoItem = new JMenuItem("How to");
		imageSettingItem = new JMenuItem("Pictures Path Setting");

		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		aboutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				javax.swing.JOptionPane.showMessageDialog(frame, "Author:Luchenghao \nmailto:luch2046#gmail.com",
						"关于作者", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		howtoItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				javax.swing.JOptionPane.showConfirmDialog(frame, "使用很简单，就两步操作：\n 1. 在Setting->Pic Path Chooser 指定一个本地电脑有.png/.jpg文件的一个目录，用来播放图片。\n 2. 在窗口上部的Open File 按钮上选中一个mp3文件，用来播放音频。 \n 就这么简单。",
						"关于使用", JOptionPane.PLAIN_MESSAGE);
			}
		});
		
		imageSettingItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				FileFilter fileFilter = new FileFilter(){

					@Override
					public boolean accept(File f) {
						// TODO Auto-generated method stub
						if(f.isDirectory()) return true;
						else {
							log.info("###" +f.getName());
							return f.getName().toLowerCase().endsWith(".png")
									|| f.getName().toLowerCase().endsWith(".jpg");
						}
					}

					@Override
					public String getDescription() {
						// TODO Auto-generated method stub
						return "Image Support(*.png/jpg)";
					}
				};
				fc.setFileFilter(fileFilter);
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setDialogTitle("Please Select A Folder");
				fc.setAcceptAllFileFilterUsed(false);
				int userChoice = fc.showOpenDialog(frame);
				if (userChoice == JFileChooser.APPROVE_OPTION) {
					log.info("i am in the imageSettingItems!");
					log.info(fc.getSelectedFile().getAbsolutePath() + " with name:" + fc.getSelectedFile().getName());
					imageFilePath = fc.getSelectedFile().getAbsolutePath();
//					image = Toolkit.getDefaultToolkit().getImage(imageFilePath);
					File file = new File(imageFilePath);
					files = file.listFiles(new PicFilter());
					playPic();
				}
			}
		});

		fileMenu.add(exitItem);
		aboutMenu.add(aboutItem);
		settingMenu.add(howtoItem);
		settingMenu.add(imageSettingItem);

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
						if (f.isDirectory()) {
							return true;
						} else {
							return f.getName().toLowerCase().endsWith(".mp3")
									|| f.getName().toLowerCase().endsWith(".wav");
						}
					}

					@Override
					public String getDescription() {
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
					if (isPlaying || isPause) {
						stopPlaying();
						while (player.getAudioClip().isRunning()) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException ex) {
								ex.printStackTrace();
							}
						}
					}
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
		
		btnPause.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isPause) {
					pausePlaying();
				} else {
					resumePlaying();
				}
				
			}
		});
		
		sliderTime = new JSlider();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.anchor = GridBagConstraints.WEST;
		// constraints.fill = GridBagConstraints.HORIZONTAL;

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
//		frame.add(processPanel, BorderLayout.NORTH);
		frame.add(jsp, BorderLayout.CENTER);
		File file = new File(imageFilePath);
		System.out.println("file test:" + file.listFiles());
		System.out.println("imageFilePath:" + imageFilePath);
		files = new File[]{new File(imageFilePath+"/1399522307.jpg"), new File(imageFilePath+"/1406932098.jpg"), new File("resources/images/191715820.jpg") , new File("resources/images/455590920.jpg") , new File("resources/images/701747985.jpg") , new File("resources/images/854304604.jpg") , new File("resources/images/869582792.jpg")};//
//		files = file.listFiles(new PicFilter());
		webImages = new String[]{"1399522307.jpg", "1406932098.jpg", "191715820.jpg" , "455590920.jpg" , "701747985.jpg" , "854304604.jpg" , "869582792.jpg"};
		System.out.println("files list:" + files);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(true);// disable the max button
		playPic();
		audioFilePath =  getClass().getResource("/").getPath() + audioFilePath;//direct run
//		audioFilePath =  getClass().getResource("/resources/").getPath() + audioFilePath;
		playback();
	}

	protected void stopPlaying() {
		isPause = false;
		btnPause.setText("Pause");
		btnPause.setEnabled(false);
		timer.reset();
		timer.interrupt();
		player.stop();
		playbackThread.interrupt();
	}

	
	private void pausePlaying() {
		btnPause.setText("Resume");
		isPause = true;
		player.pause();
		timer.pauseTimer();
		playbackThread.interrupt();
	}
	
	private void resumePlaying() {
		btnPause.setText("Pause");
		isPause = false;
		player.resume();
		timer.resumeTimer();
		playbackThread.interrupt();		
	}
	PlayingTimer timer;
	private boolean isPlaying = false;
	private boolean isPause = false;
	Thread playbackThread;
	URL audioUrl = getClass().getResource("/untilTheEndOfTheWorld.mp3");//untilTheEndOfTheWorld.mp3
	
	protected void playback() {
		System.out.println("audioUrl:" + audioUrl);
		timer = new PlayingTimer(labelTimeCounter, sliderTime);
		timer.start();
		isPlaying = true;
		playbackThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					btnPlay.setText("Stop");
					btnPlay.setEnabled(true);

					btnPause.setText("Pause");
					btnPause.setEnabled(true);
//					mp3path = audioFilePath;
					System.out.println("audioUrl:" + audioUrl);
					player.load(audioFilePath);
//					FileInputStream fis = new FileInputStream(mp3path);
//					BufferedInputStream bis = new BufferedInputStream(fis);
//					player = new javazoom.jl.player.Player(bis);
					timer.setAudioPlayer(player.getAudioClip());
					labelFileName.setText("Playing File: " + audioFilePath);
					sliderTime.setMaximum((int) player.getClipSecondLength());

					labelDuration.setText(player.getClipLengthString());
					player.play();
					resetControls();
				}  catch (UnsupportedAudioFileException ex) {
					JOptionPane.showMessageDialog(frame,  
							"The audio format is unsupported!", "Error", JOptionPane.ERROR_MESSAGE);
					resetControls();
					ex.printStackTrace();
				} catch (LineUnavailableException ex) {
					JOptionPane.showMessageDialog(frame,  
							"Could not play the audio file because line is unavailable!", "Error", JOptionPane.ERROR_MESSAGE);
					resetControls();
					ex.printStackTrace();
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(frame,  
							"I/O error while playing the audio file!", "Error", JOptionPane.ERROR_MESSAGE);
					resetControls();
					ex.printStackTrace();
				}
			}

		});

		playbackThread.start();
	}

	protected void resetControls() {
		timer.reset();
		timer.interrupt();
		btnPlay.setText("Play");
		btnPause.setEnabled(false);
		isPlaying = false;
	}

	protected String getClipLengthString() {
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

//	class JlThread implements Runnable {
//
//		@Override
//		public void run() {
//			try {
//				player.play();
//				if (!player.getAudioClip().isRunning()) {
//					log.info("mp3 play completed");
//					playMusic();
//				}
//			} catch (Exception e) {
//				log.info(e);
//			}
//		}
//	}

//	public void playMusic() {
//		try {
////			FileInputStream fis = new FileInputStream(mp3path);
////			BufferedInputStream bis = new BufferedInputStream(fis);
//			player.load(mp3path);
//			
//		} catch (Exception e) {
//			log.info("Problem playing file " + mp3path);
//			log.info(e);
//		}
//
//		// run in new thread to play in background
//		// new Thread() {
//		// public void run() {
//		// try {
//		// player.play();
//		// if (player.isComplete()) {
//		// log.info("mp3 play completed");
//		// player.play();
//		// }
//		// } catch (Exception e) {
//		// log.info(e);
//		// }
//		// }
//		// }.start();
//		new Thread(new JlThread()).start();
//	}

	private void playMp3() {
		log.info("Mp3 location:" + mp3path);
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

		byte tempBuffer[] = new byte[320];

		@Override
		public void run() {
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
				e.printStackTrace();
			}

		}

	}

	private void playPic() {
		Thread play = new Thread(new PlayPicThread());
		play.start();
	}

	class PlayPicThread implements Runnable {
		@Override
		public void run() {
			isPicPlayEnd = false;
			System.out.println(CLASS+ files);
			System.out.println("Image Size:" + files.length);
			log.info("Image Size:" + files.length);
//			URL currentUrl = null;
			for (int i = 0; i < files.length; i++) {
				currentFile = files[i];
//				currentUrl = this.getClass().getResource("/resources/images/"+webImages[i]);
				currentUrl = this.getClass().getResource("/images/"+webImages[i]);
				System.out.println("currentUrl:" + currentUrl);
				log.info(currentFile);
				isActionFlag = true;
				try {
					Thread.sleep(15000);
				} catch (InterruptedException e) {
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

		private void setImageByURL(URL currentFile, boolean isActionFlag) {
			// TODO Auto-generated method stub
//			imgIcon = new ImageIcon(currentFile.getPath());
			imgIcon = new ImageIcon(currentFile);
			System.out.println("Pic Path: " + currentFile.getPath());
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
						imgIcon.setImage(setFixedByURL(currentFile, iw, ih));
					} else {
						ih = (cw - 50) * ih / iw;
						iw = cw - 50;
						imgIcon.setImage(setFixedByURL(currentFile, iw, ih));
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
		
		private void setImage(File currentFile, boolean isActionFlag) {
			// TODO Auto-generated method stub
			imgIcon = new ImageIcon(currentFile.getPath());
			System.out.println("Pic Path: " + currentFile.getPath());
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

		private Image setFixedByURL(URL currentFile, int iw, int ih) {
			BufferedImage bi = null;
			try {
				bi = javax.imageio.ImageIO.read(currentFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return bi.getScaledInstance(iw, ih, Image.SCALE_SMOOTH);
		}
		
		private Image setFixed(File currentFile, int iw, int ih) {
			BufferedImage bi = null;
			try {
				bi = javax.imageio.ImageIO.read(currentFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return bi.getScaledInstance(iw, ih, Image.SCALE_SMOOTH);
		}
	}
}