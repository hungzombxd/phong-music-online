package zing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import zing.audio.AudioPlayer;
import zing.audio.AudioPlayerListener;
import zing.audio.Streaming;
import zing.model.Album;
import zing.model.AlbumRenderer;
import zing.model.Format;
import zing.model.History;
import zing.model.ItemCombo;
import zing.model.Playlist;
import zing.model.Song;
import zing.model.SongRenderer;
import zing.sites.ChiaSeNhac;
import zing.sites.MusicSite;
import zing.sites.NhacCuaTui;
import zing.sites.Radio;
import zing.sites.Site;
import zing.sites.Zing;
import zing.utils.FileUtils;
import zing.utils.Utils;

public class Main extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public static final DataFlavor DRAG_DROP_URI = new DataFlavor("application/x-java-url;class=java.net.URL", "URL");
	
	public static final ImageIcon PLAY = new ImageIcon(Main.class.getResource("/images/play.png"));
	public static final ImageIcon PAUSED = new ImageIcon(Main.class.getResource("/images/pause.png"));
	
	private JList songs;
	private JList albums;
	private DefaultListModel modelSongs;
	private DefaultListModel modelAlbums;
	private JButton search;
	private JTextField page, total, value;
	private JComboBox types, repeats, sites;
	private JComboBox filters, bys;
	private DefaultComboBoxModel modelFilters, modelBys;
	private JFileChooser chooser;
	private Zing zing;
	private Radio radio = Radio.getIntance();
	private Toolkit toolkit = Toolkit.getDefaultToolkit();
	private Clipboard clipboard = toolkit.getSystemClipboard();
	private boolean allowSaveFiles = false;
	private Runtime runtime = Runtime.getRuntime();
	private JMenuBar menuBar;
	private JMenuItem itemOpenLink, itemTopVietNamese, itemTopEnglish, itemTopKorea, itemShowLyric, itemShare, itemUpdate, itemSaveMP3, itemSaveCurrentSong, itemUndo, itemRedo, itemSetProxy, itemOpen, itemSave, itemExit, itemAlbumStartup, itemTopStartup, itemLoadFirstPlaylist, itemSendStatus, itemIcludeAlbum;
	private JMenu menuFile, menuSetup, menuMedia, menuUserPlaylists, menuAddToPlaylist;
	private JMenuItem mediaPlay, mediaNext, mediaPrevious, menuTopSong;
	private ButtonGroup group;
	private ProxySelector dialogProxy;
	private FrameLyric frameLyric;
	private Configure configure;
	private Class<?> object = this.getClass();
	private ColorSlider slider;
	private JLabel startDuration, endDuration, info, songInfo, labelSearch, iconStatus;
	private JLabel selectQuality;
	private JPopupMenu popupMenu;
	private History<List<Song>> history;
	private Dimension dimension;
	private Thread updateSong = null, updateAlbum = null;
	private int toPage;
	private int fromPage;
	private MusicSite musicSite;
	private AudioPlayer player;
	private String currentTitle;
	private int currentIndex = -1;
	
	public Main() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1020, 700);
//		setResizable(false);
		setIconImage(toolkit.getImage((object.getResource("/images/zing.png"))));

		configure = Configure.getInstance();
		player = new AudioPlayer();
		player.setListener(new AudioPlayerListener() {
			
			public void playing(AudioPlayer player) {
				startDuration.setText(Utils.toDuaration(player.getCurrentDuration()));
				if (!slider.dragging) slider.setValue(player.getCurrentSize());
				setTitle(currentTitle + " " + player.getPlayingInfo());
				mediaPlay.setIcon(PAUSED);
			}
			
			public void paused(AudioPlayer player) {
				mediaPlay.setIcon(PLAY);
			}
			
			public void finished(AudioPlayer player) {
				setTitle(Configure.getInstance().title);
				slider.max = 0;
				slider.setRange(0);
				startDuration.setText("00:00");
				endDuration.setText("00:00");
				mediaPlay.setIcon(PLAY);
				next();
			}

			public void init(AudioPlayer player) {
				slider.max = player.getLength();
				endDuration.setText(Utils.toDuaration(player.getDuration()));
				setTitle(player.getPlayingInfo());
				if (player.isBuffered()) slider.setRange(player.getLength());
			}
		});
		
		player.setStreaming(new Streaming() {
			public void buffering(int length) {
				slider.setRange(player.getBuffering());
			}
		});
		zing = Zing.getInstance();
		history = new History<List<Song>>();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				player.release();
				configure.save();
			}
		});
		addComponentListener(new ComponentAdapter() {
			public void componentMoved(ComponentEvent e) {
				configure.location = getLocation();
			}
		});
//		Add menu bar
		menuBar = new JMenuBar();
		menuFile = new JMenu("File");
		menuSetup = new JMenu("Setup");
		menuFile.add(itemOpen = new JMenuItem("Open playlist..."));
		itemOpen.setAccelerator(KeyStroke.getKeyStroke('O', KeyEvent.CTRL_DOWN_MASK));
		itemOpen.setIcon(getImage("open.png"));
		menuFile.add(itemSave = new JMenuItem("Save playlist..."));
		itemSave.setAccelerator(KeyStroke.getKeyStroke('S', KeyEvent.CTRL_DOWN_MASK));
		itemSave.setIcon(getImage("save.png"));
		menuFile.addSeparator();
		menuFile.add(itemExit = new JMenuItem("Exit"));
		itemExit.setAccelerator(KeyStroke.getKeyStroke('X', KeyEvent.ALT_DOWN_MASK));
		itemExit.setIcon(getImage("exit.png"));
		menuSetup.add(itemTopStartup = new JCheckBoxMenuItem("Load top song at startup"));
		menuSetup.add(itemAlbumStartup = new JCheckBoxMenuItem("Load album at startup"));
		menuSetup.add(itemLoadFirstPlaylist = new JCheckBoxMenuItem("Load first album in search album"));
		menuSetup.add(itemIcludeAlbum = new JCheckBoxMenuItem("Search album in search song"));
		itemIcludeAlbum.setAccelerator(KeyStroke.getKeyStroke('I', KeyEvent.CTRL_DOWN_MASK));
		itemIcludeAlbum.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				configure.includeAlbum = itemIcludeAlbum.isSelected();
			}
		});
		menuSetup.add(itemSendStatus = new JCheckBoxMenuItem("Send song to Y!M"));
		itemSendStatus.setAccelerator(KeyStroke.getKeyStroke('Y', KeyEvent.CTRL_DOWN_MASK));
		menuSetup.add(itemUpdate = new JCheckBoxMenuItem("Update song in mouse wheel"));
		itemUpdate.setAccelerator(KeyStroke.getKeyStroke('U', KeyEvent.CTRL_DOWN_MASK));
		itemUpdate.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				configure.update = itemUpdate.isSelected();
			}
		});
		menuSetup.addSeparator();
		menuSetup.add(itemSetProxy = new JMenuItem("Set proxy..."));
		itemOpen.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				String file = showOpen("Open playlist", new File(configure.oldFolder), "Links.m3u");
				if(file == null) return;
				try {
					setSongs(Utils.m3uToSongs(file), true);
				} catch (IOException e1) {
					setTitle(e1.toString());
					e1.printStackTrace();
				}
			}
		});
		itemSave.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				String file = showSave("Save playlist (*.m3u)", new File(configure.oldFolder), "Links.m3u");
				if (file == null) return;
				try {
					Utils.songsToM3UFile(configure.songs, file);
				} catch (IOException e) {
					setTitle(e.toString());
					e.printStackTrace();
				}
			}
		});
		itemLoadFirstPlaylist.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				configure.loadFirstPlaylist = itemLoadFirstPlaylist.isSelected();
			}
		});
		itemSendStatus.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				configure.sendStatus = itemSendStatus.isSelected();
			}
		});
		itemAlbumStartup.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				configure.albumStartup = itemAlbumStartup.isSelected();
			}
		});
		itemTopStartup.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				configure.topStartup = itemTopStartup.isSelected();
			}
		});
		
		itemSetProxy.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				if (dialogProxy == null) dialogProxy = new ProxySelector();
				Point point = getLocation();
				point.x += 200;
				point.y += 100;
				dialogProxy.setHost(configure.host);
				dialogProxy.setPort(configure.port);
				dialogProxy.setUsername(configure.username);
				dialogProxy.setPassword(configure.password);
				dialogProxy.setSystemProxy(configure.systemProxy);
				dialogProxy.setUseProxy(configure.useProxy);
				dialogProxy.setLocation(point);
				dialogProxy.setVisible(true);
			}
		});
		itemExit.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				configure.save();
				System.exit(0);
			}
		});
		menuBar.add(menuFile);
		menuBar.add(initMenuAction());
		menuBar.add(menuSetup);
		menuBar.add(initMenuRadio());
		menuBar.add(initMenuSong());
		menuBar.add(menuTopSong = new JMenu("Top"));
		menuTopSong.add(itemTopVietNamese = new JMenuItem("Việt Nam"));
		itemTopVietNamese.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				new Thread(){
					public void run(){
						setTitle("Loading...");
						try {
							setSongs(zing.getTopVietnamese(), true);
							setTitle(configure.title);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}.start();
			}
		});
		menuTopSong.add(itemTopEnglish = new JMenuItem("English"));
		itemTopEnglish.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				new Thread(){
					public void run(){
						setTitle("Loading...");
						try {
							setSongs(zing.getTopEnglish(), true);
							setTitle(configure.title);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}.start();
			}
		});
		menuTopSong.add(itemTopKorea = new JMenuItem("Korea"));
		itemTopKorea.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				new Thread(){
					public void run(){
						setTitle("Loading...");
						try {
							setSongs(zing.getTopKorea(), true);
							setTitle(configure.title);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}.start();
			}
		});
		menuBar.add(initMenuAlbum());
		menuBar.add(initMenuMedia());
		menuBar.add(initMenuUserPlaylists());
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(mediaPrevious = new JMenu());
		mediaPrevious.setIcon(getImage("previous.png"));
		mediaPrevious.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent arg0) {
				mediaPrevious.setSelected(false);
			}
		});
		mediaPrevious.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent arg0) {
				mediaPrevious.setSelected(false);
				previous();
			}
		});
		menuBar.add(mediaPlay = new JMenu());
		mediaPlay.setIcon(getImage("play.png"));
		mediaPlay.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent arg0) {
				mediaPlay.setSelected(false);
			}
		});
		mediaPlay.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent arg0) {
				mediaPlay.setSelected(false);
				if (!player.isPaused()){
					player.pause();
				}else{
					player.resume();
				}
			}
		});
		menuBar.add(mediaNext = new JMenu());
		mediaNext.setIcon(getImage("next.png"));
		mediaNext.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent arg0) {
				mediaNext.setSelected(false);
			}
		});
		mediaNext.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent arg0) {
				mediaNext.setSelected(false);
				next();
			}
		});
		dimension = menuBar.getPreferredSize();
		dimension.height = 25;
		menuBar.setPreferredSize(dimension);
		final JPanel panelSlider = new JPanel();
		panelSlider.addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent arg0) {
				panelSlider.setFocusCycleRoot(false);
				panelSlider.setFocusable(false);
			}
		});
		dimension = panelSlider.getPreferredSize();
		dimension.height = 25;
		dimension.width = 298;
		panelSlider.setPreferredSize(dimension);
		panelSlider.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(204, 204, 204)));
		panelSlider.setLayout(new BoxLayout(panelSlider, BoxLayout.LINE_AXIS));
		panelSlider.setOpaque(false);
		panelSlider.add(startDuration = new JLabel("00:00", JLabel.RIGHT));
//		startDuration.setOpaque(true);
		dimension = new Dimension(34, 25);
		startDuration.setPreferredSize(dimension);
		startDuration.setSize(dimension);
		startDuration.setMaximumSize(dimension);
		startDuration.setMinimumSize(dimension);
		slider = new ColorSlider();
		slider.addMouseListener(new MouseAdapter() {
        	public void mouseReleased(final MouseEvent e){
//        		slider.dragging = false;
				if (!SwingUtilities.isLeftMouseButton(e)) return;
//				setValue(slider.valueAtPoint(e));
				player.seek(slider.getValue());
				slider.dragging = false;
			}
		});
		panelSlider.add(Box.createRigidArea(new Dimension(5, 25)));
		panelSlider.add(slider);
		panelSlider.add(Box.createRigidArea(new Dimension(5, 25)));
		panelSlider.add(endDuration = new JLabel("00:00", JLabel.LEFT));
		endDuration.setPreferredSize(dimension);
		endDuration.setSize(dimension);
		endDuration.setMaximumSize(dimension);
		endDuration.setMinimumSize(dimension);
		menuBar.add(panelSlider);
		setJMenuBar(menuBar);
		
		chooser = new JFileChooser();
		dialogProxy = new ProxySelector();
		dialogProxy.setMain(Main.this);
		dialogProxy.setConfigure(configure);
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(initPanelSearch(), BorderLayout.NORTH);
//		container.add(initMediaButton(), BorderLayout.SOUTH);
		songs = new JList(modelSongs = new DefaultListModel()){
			private JToolTip toolTip;
			private static final long serialVersionUID = -5872981870254289711L;

			@Override
			public Point getToolTipLocation(MouseEvent event) {
				if (toolTip == null) createToolTip();
				return new Point(event.getPoint().x - (toolTip.getWidth() / 2), event.getPoint().y + 20);
			}
			
			@Override
			public JToolTip createToolTip() {
				toolTip = super.createToolTip();
				return toolTip;
			}
		};
		songs.setCellRenderer(new SongRenderer());
		songs.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e){
				int index = songs.locationToIndex(e.getPoint());
				if (index < 0 || configure.songs.isEmpty() || index >= configure.songs.size()) return;
				songs.setToolTipText(configure.songs.get(index).toString());
			}
		});
//		links.setFixedCellHeight(50);
		popupMenu = new JPopupMenu();
		JMenuItem itemCopy = new JMenuItem("Copy direct links");
		itemCopy.setIcon(getImage("copy.png"));
		JMenuItem itemSaveLinks = new JMenuItem("Save direct links (*.m3u8)...");
		itemSaveLinks.setIcon(getImage("save.png"));
		JMenuItem itemSaveAsPlaylist = new JMenuItem("Save direct links (*.m3u)...");
		itemSaveAsPlaylist.setIcon(getImage("m3u.png"));
		itemSaveAsPlaylist.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				saveAsPlaylist();
			}
		});
		itemSaveLinks.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				saveLinks();
			}
		});
		JMenuItem itemAddPlayList = new JMenuItem("Decode album link");
		itemAddPlayList.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				final String link = JOptionPane.showInputDialog(Main.this,"Enter playlist link (HTML file)", "");
				if (link == null || link.equals("")) return;
				new Thread(){
					public void run(){
						try {
							setSongs(zing.getAlbum(link), true);
						} catch (IOException e1) {
							setTitle(e1.toString());
							e1.printStackTrace();
						}
					}
				}.start();
			}
		});
		JMenuItem itemAddXMLFile = new JMenuItem("Decode XML file");
		itemAddXMLFile.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				final String linkXML = JOptionPane.showInputDialog("Enter XML file (XML file)");
				if (linkXML == null || linkXML.equals("")) return;
				new Thread(){
					public void run(){
						try {
							setSongs(zing.xmlToSongs(linkXML), true);
						} catch (IOException e1) {
							setTitle(e1.toString());
							e1.printStackTrace();
						}
					}
				}.start();
			}
		});
		itemCopy.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				new Thread(){
					public void run(){
						int[] save = songs.getSelectedIndices();
						String saveString = "";
						setStatus("COPYING LINK: 0/" + save.length);
						for (int i = 0; i < save.length; i++){
							try {
								saveString += configure.songs.get(save[i]).getDirectLink(Configure.getInstance().format) + "\n";
							} catch (IOException e) {
								setTitle(e.toString());
								e.printStackTrace();
							}
							setStatus("COPYING LINK: " + (i + 1) + "/" + save.length);
						}
						clipboard.setContents(new StringSelection(saveString), null);
						setStatus("COMPLETE COPY: " + save.length + "/" + save.length);
					}
				}.start();
			}
		});
		JMenuItem itemRename = new JMenuItem("Rename title");
		itemRename.setIcon(getImage("rename.png"));
		itemRename.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				if (songs.getSelectedIndex() == -1) return;
				String title = JOptionPane.showInputDialog(Main.this, "Enter title for song", configure.songs.get(songs.getSelectedIndex()).getTitle());
				if (title != null){
					configure.songs.get(songs.getSelectedIndex()).setTitle(title);
					setSongs(configure.songs, true);
				}
			}
		});
		JMenuItem itemCopyLink = new JMenuItem("Copy links");
		itemCopyLink.setIcon(getImage("copy.png"));
		itemCopyLink.addActionListener(new ActionListener() {
			
			
			public void actionPerformed(ActionEvent arg0) {
				new Thread(){
					public void run(){
						int[] save = songs.getSelectedIndices();
						String saveString = "";
						setStatus("COPYING LINK: 0/" + save.length);
						for (int i = 0; i < save.length; i++){
							saveString += configure.songs.get(save[i]).getLink() + "\n";
							setStatus("COPYING LINK: " + (i + 1) + "/" + save.length);
						}
						clipboard.setContents(new StringSelection(saveString), null);
						setStatus("COMPLETE COPY: " + save.length + "/" + save.length);
					}
				}.start();
			}
		});
		itemShare = new JMenuItem("Share to facebook");
		itemShare.setIcon(getImage("facebook.png"));
		itemShare.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				shareFacebook(configure.songs.get(songs.getSelectedIndex()).getLink());
			}
		});
		JMenuItem itemDelete = new JMenuItem("Delete", getImage("delete.png"));
		itemDelete.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				new Thread(){
					public void run(){
						System.out.println(SwingUtilities.isEventDispatchThread());
						deleteLinks();
					}
				}.start();
			}
		});
		itemShowLyric = new JMenuItem("Show lyric");
		itemShowLyric.setIcon(getImage("lyrics16.png"));
		itemShowLyric.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				if (frameLyric == null) frameLyric = new FrameLyric();
				new Thread(){
					public void run(){
						Song song = configure.songs.get(songs.getSelectedIndex());
						frameLyric.setTitle("Lyric - " + song.getTitle());
						frameLyric.setLyric(song);
					}
				}.start();
				frameLyric.setVisible(true);
			}
		});
		itemOpenLink = new JMenuItem("Open in browser");
		itemOpenLink.setIcon(getImage("chrome.png"));
		itemOpenLink.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				openLink(configure.songs.get(songs.getSelectedIndex()).getLink());
			}
		});
		
		popupMenu.add(itemCopy);
		popupMenu.add(itemCopyLink);
		popupMenu.add(itemShowLyric);
		popupMenu.add(itemShare);
		popupMenu.add(itemOpenLink);
		popupMenu.add(itemSaveLinks);
		popupMenu.add(itemSaveAsPlaylist);
		popupMenu.add(itemAddPlayList);
		popupMenu.add(itemAddXMLFile);
		popupMenu.add(itemRename);
		popupMenu.add(itemDelete);
		popupMenu.addSeparator();
		popupMenu.add(menuAddToPlaylist = initMenuAddToPlaylist());
		popupMenu.addSeparator();
		JMenu menuView = new JMenu("View mode");
		menuView.setIcon(getImage("viewmode.png"));
		JMenuItem modeDetail = new JMenuItem("Detail");
		modeDetail.setIcon(getImage("viewmode_detail.png"));
		menuView.add(modeDetail);
		JMenuItem modeClassic = new JMenuItem("Classic");
		modeClassic.setIcon(getImage("viewmode_classic.png"));
		menuView.add(modeClassic);
		modeDetail.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				new Thread(){
					public void run(){
						configure.viewModeSong = SongRenderer.VIEW_MODE_DETAIL;
						modelSongs.clear();
						setSongs(configure.songs, true);
//						songs.invalidate();
//						songs.repaint();
					}
				}.start();
			}
		});
		modeClassic.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				new Thread(){
					public void run(){
						configure.viewModeSong = SongRenderer.VIEW_MODE_CLASSIC;
						modelSongs.clear();
						setSongs(configure.songs, true);
					}
				}.start();
			}
		});
		popupMenu.add(menuView);
		
		songs.addKeyListener(new KeyAdapter() {
			
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					play();
				}
				if (arg0.getKeyCode() == KeyEvent.VK_DELETE) {
					new Thread(){
						public void run(){
							deleteLinks();
						}
					}.start();
				}
			}
		});
		songs.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					if (songs.getSelectedIndices().length <= 1){
						if (configure.songs.isEmpty()){
							songs.clearSelection();
						}else{
							songs.setSelectedIndex(songs.locationToIndex(e.getPoint()));
						}
					}
					popupMenu.show(songs, e.getX(), e.getY());
				}
				if (e.getClickCount() == 2) {
					play();			
				}
			}
		});
		songs.setDragEnabled(true);
		songs.setDropTarget(new DropTarget(){
			
			private static final long serialVersionUID = 5159432887394239344L;
			
			public synchronized void drop(DropTargetDropEvent evt) {
				try {
					evt.acceptDrop(DnDConstants.ACTION_COPY);
					if (evt.isDataFlavorSupported(DRAG_DROP_URI)){
						final URL url = (URL) evt.getTransferable().getTransferData(DRAG_DROP_URI);
						new Thread(){
							public void run(){
								final Song song = new Song();
								song.title = url.toString();
								song.songInfo = song.title;
								song.setDirectLink(Format.MP3_128_KBPS, song.title);
								song.site = Site.INTERNET_URL;
								configure.songs.add(song);
					            SwingUtilities.invokeLater(new Runnable() {
									public void run() {
										modelSongs.addElement(song);
									}
								});
							}
						}.start();
					}
					if (evt.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
						final List<?> files = (List<?>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
						new Thread(){
							public void run(){
								final List<Song> songs = new ArrayList<Song>();
								for (Object object : files) {
									if (object instanceof File){
										File file = (File) object;
										if (!file.getName().toLowerCase().endsWith(".mp3") && !file.getName().toLowerCase().endsWith(".wav") && !file.getName().toLowerCase().endsWith(".flac")) continue;
										Song song = new Song();
										song.title = file.getName();
										song.songInfo = file.getAbsolutePath();
//										song.songInfo = file.getAbsolutePath();
										song.setDirectLink(Format.MP3_128_KBPS, "file:" + file.getAbsolutePath());
										song.site = Site.MY_COMPUTER;
					            		songs.add(song);
									}
								}
								configure.songs.addAll(songs);
					            SwingUtilities.invokeLater(new Runnable() {
									public void run() {
										for (Song song : songs){
											modelSongs.addElement(song);
										}
									}
								});
							}
						}.start();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		    }
		});
		JPanel panelList = new JPanel(new GridLayout(1, 2, 5, 0));
		JScrollPane scrollPaneSong, scrollPaneAlbum;
		panelList.add(scrollPaneSong = new JScrollPane(songs,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		scrollPaneSong.addMouseWheelListener(new MouseWheelListener() {
			
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				if (configure.update && !configure.lastValueSong.equals("") && updateSong == null && configure.songs.size() - 1 == songs.getLastVisibleIndex() && arg0.getUnitsToScroll() > 0){
					updateSong();
				}
			}
		});
		panelList.add(scrollPaneAlbum = new JScrollPane(albums = new JList(modelAlbums = new DefaultListModel()),
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		scrollPaneAlbum.addMouseWheelListener(new MouseWheelListener() {
			
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				if (configure.update && !configure.lastValueAlbum.equals("") && updateAlbum == null && configure.albums.size() - 1 == albums.getLastVisibleIndex() && arg0.getUnitsToScroll() > 0){
					updateAlbum();
				}
			}
		});
		container.add(panelList, BorderLayout.CENTER);
		
		albums.setCellRenderer(new AlbumRenderer());
		albums.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e){
				int index = albums.locationToIndex(e.getPoint());
				if (index < 0 || configure.albums.isEmpty() || index >= configure.albums.size()) return;
				albums.setToolTipText(configure.albums.get(index).toString());
			}
		});
		final JPopupMenu popupAlbum = new JPopupMenu();
		JMenu menuAlbumView = new JMenu("View mode");
		menuAlbumView.setIcon(getImage("viewmode.png"));
		JMenuItem modeAlbumDetail = new JMenuItem("Detail");
		modeAlbumDetail.setIcon(getImage("viewmode_detail.png"));
		menuAlbumView.add(modeAlbumDetail);
		JMenuItem modeAlbumClassic = new JMenuItem("Classic");
		modeAlbumClassic.setIcon(getImage("viewmode_classic.png"));
		menuAlbumView.add(modeAlbumClassic);
		modeAlbumDetail.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				new Thread(){
					public void run(){
						configure.viewModeAlbum = AlbumRenderer.VIEW_MODE_DETAIL;
						modelAlbums.clear();
						setAlbum(configure.albums, true);
					}
				}.start();
			}
		});
		modeAlbumClassic.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				new Thread(){
					public void run(){
						configure.viewModeAlbum = AlbumRenderer.VIEW_MODE_CLASSIC;
						modelAlbums.clear();
						setAlbum(configure.albums, true);
					}
				}.start();
			}
		});
		popupAlbum.add(menuAlbumView);
		albums.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)){
					popupAlbum.show(albums, e.getX(), e.getY());
				}
				if (e.getClickCount() == 2) {
					addSongsToLinks();
				}
			}
		});
		JMenuBar status = new JMenuBar();
		dimension = status.getPreferredSize();
		dimension.height = 30;
		status.setPreferredSize(dimension);
		status.setLayout(new BorderLayout());
		
		JPanel leftStatus = new JPanel();
		leftStatus.setOpaque(false);
		leftStatus.setLayout(new BoxLayout(leftStatus, BoxLayout.LINE_AXIS));
//		leftStatus.add(Box.createRigidArea(new Dimension(5, 0)));
		leftStatus.add(iconStatus = new JLabel(getImage("searching.gif")));
		iconStatus.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		iconStatus.setVisible(false);
		leftStatus.add(songInfo = new JLabel(configure.status));
		songInfo.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		status.add(leftStatus, BorderLayout.CENTER);
		
		JPanel panelActive = new JPanel();
		panelActive.setPreferredSize(new Dimension(200, 25));
		panelActive.add(info = new JLabel("[" + configure.title + "]", JLabel.CENTER));
		info.setPreferredSize(new Dimension(155, 14));
		panelActive.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(204, 204, 204)));
		panelActive.setOpaque(false);
		status.add(panelActive, BorderLayout.EAST);
		container.add(status, BorderLayout.SOUTH);
		container.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.EAST);
		container.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.WEST);
		if (configure.location == null){
			setLocationRelativeTo(null);
		}else{
			setLocation(configure.location);
		}
		setVisible(true);
		//Load configure;
		loadConfigure();
		toolkit.getSystemEventQueue().push(new MyEventQueue());
	}

	private void loadConfigure(){
		total.setText(configure.total);
		page.setText(configure.page);
		setSongs(configure.songs, true);
		setAlbum(configure.albums, true);
		itemAlbumStartup.setSelected(configure.albumStartup);
		itemLoadFirstPlaylist.setSelected(configure.loadFirstPlaylist);
		itemSendStatus.setSelected(configure.sendStatus);
		itemTopStartup.setSelected(configure.topStartup);
		itemIcludeAlbum.setSelected(configure.includeAlbum);
		selectQuality.setIcon(configure.format.getImage());
		itemUpdate.setSelected(configure.update);
		sites.setSelectedItem(configure.site);
		types.setSelectedItem(configure.type);
		filters.setSelectedItem(configure.filter);
		bys.setSelectedItem(configure.by);
		repeats.setSelectedItem(configure.repeat);
		value.setText(configure.value);
		startup();
		setProxy();
	}

	private void saveAsPlaylist() {
		final String str = showSave("Save as playlist", new File(configure.oldFolder), "Zing Links.m3u");
		if (str == null) return;
		new Thread() {
			public void run() {
				try {
					List<Song> listSongs = configure.songs;
					BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(str),"UTF-8"));
					int[] lists = songs.getSelectedIndices();
					setStatus("SAVING LINK: 0/" + lists.length);
					out.write("#EXTM3U");
					out.newLine();
					for (int i = 0; i < lists.length; i++) {
						Song song = listSongs.get(lists[i]);
						out.write("#EXTINF:-1," + song.getTitle());
						out.newLine();
						out.write(song.getDirectLink(configure.format));
						out.newLine();
						setStatus("SAVING LINK: " + (i + 1) + "/" + lists.length);
					}
					out.flush();
					out.close();
					setStatus("COMPLETE LINK: " + lists.length + "/" + lists.length);
					JOptionPane.showMessageDialog(Main.this, "All done");
				} catch (Exception e1) {
					setTitle(e1.toString());
					e1.printStackTrace();
				}
			}
		}.start();
	}

	private void saveLinks() {
		final String str = showSave("Save links", new File(configure.oldFolder), "Zing Links.m3u8");
		if (str == null) return;
		new Thread() {
			public void run() {
				try {
					List<Song> listSongs = configure.songs;
					BufferedWriter out = new BufferedWriter(new FileWriter(str));
					int[] lists = songs.getSelectedIndices();
					setStatus("SAVING LINK: 0/" + lists.length);
					for (int i = 0; i < lists.length; i++) {
						Song song = listSongs.get(lists[i]);
						out.write(song.getDirectLink(configure.format));
						out.newLine();
						setStatus("SAVING LINK: " + (i + 1) + "/" + lists.length);
					}
					out.flush();
					out.close();
					setStatus("COMPLETE LINK: " + lists.length + "/" + lists.length);
					JOptionPane.showMessageDialog(Main.this, "All done");
				} catch (Exception e1) {
					setTitle(e1.toString());
					e1.printStackTrace();
				}
			}
		}.start();
	}
	
	private void saveFiles() {
		chooser.setCurrentDirectory(new File(configure.oldFolder));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int ret = chooser.showSaveDialog(Main.this);
		if (ret != JFileChooser.APPROVE_OPTION) {
			return;
		}
		final String dir = chooser.getSelectedFile().getAbsolutePath();
		configure.oldFolder = dir;
		itemSaveMP3.setEnabled(false);
		new Thread() {
			public void run() {
				List<Song> listSongs = configure.songs;
				int i = 0;
				int[] lists = songs.getSelectedIndices();
				try {
					setStatus("SAVING FILE: 0/" + lists.length);
					allowSaveFiles = true;
					FileUtils.Streaming streaming = new FileUtils.Streaming() {
						
						@Override
						public void progressing(int length, int offset) {
							setStatus(offset * 100 / length + " %");
						}
					};
					for (i = 0; i < lists.length; i++) {
						Song song = listSongs.get(lists[i]);
						info.setToolTipText("Saving file " + song.toTitle() + ".mp3 to " + dir);
						String file = dir + File.separator + Utils.toANSI(song.toTitle()) + (song.getQuality().equals(Format.LOSSLESS) ? ".flac" : ".mp3");
						FileUtils.copyURLToFile(new URL(song.getDirectLink(configure.format)), new File(file), streaming);
						setStatus("SAVING FILE: " + (i + 1) + "/"
								+ lists.length);
						if (!allowSaveFiles) {
							i++;
							break;
						}
					}
					setStatus("COMPLETE SAVE: " + i + "/" + lists.length);
					info.setToolTipText("");
					JOptionPane.showMessageDialog(Main.this, "All done");
					itemSaveMP3.setEnabled(true);
				} catch (Exception e) {
					setTitle(e.toString());
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void setSongs(final List<Song> lists, boolean clear) {
		if (!clear){
			configure.songs.addAll(lists);
		}else{
			configure.songs = lists;
			modelSongs.clear();
			history.add(lists);
			configure.lastValueSong = "";
			configure.lastPageSong = 1;
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run(){
				for (Song song : lists) {
					modelSongs.addElement(song);
				}
				songs.setSelectedIndex(0);
				songs.requestFocus();
			}
		});
		if (history.undo > 0){
			itemUndo.setEnabled(true);
		}else{
			itemUndo.setEnabled(false);
		}
		if (history.redo > 0){
			itemRedo.setEnabled(true);
		}else{
			itemRedo.setEnabled(false);
		}
	}
	
	private void setAlbum(final List<Album> lists, boolean clear){
		if (!clear){
			configure.albums.addAll(lists);
		}else{
			configure.albums = lists;
			modelAlbums.clear();
			configure.lastValueAlbum = "";
			configure.lastPageAlbum = 1;
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				for (Album album : lists) {
					modelAlbums.addElement(album);
				}
				albums.setSelectedIndex(0);
				if (!configure.albums.isEmpty() && itemLoadFirstPlaylist.isSelected() && types.getSelectedItem().toString().equals("Album")) {
					new Thread(){
						public void run(){
							try {
								setSongs(configure.albums.get(0).getSongs(), true);
							} catch (IOException e) {
								setTitle(e.toString());
								e.printStackTrace();
							}
						}
					}.start();
				}
			}
		});
	}

	public void addSongsToLinks(){
		int index = albums.getSelectedIndex();
		if (configure.albums.isEmpty() || index >= configure.albums.size()) return;
		final Album currentPlaylist = configure.albums.get(index);
		setTitle("Loading '" + currentPlaylist.title + "'...");
		new Thread(){
			public void run(){
				try {
					setSongs(currentPlaylist.getSongs(), true);
				} catch (IOException e) {
					setTitle(e.toString());
					e.printStackTrace();
				}
				setTitle(configure.title);
			}
		}.start();
	}
	
	private void updateSong(){
		setIconStatus(true);
		songInfo.setText("Updating song...");
		updateSong = new Thread(){
			List<Song> lists = new ArrayList<Song>();
			public void run(){
				configure.lastPageSong++;
				ItemCombo itemFilter = (ItemCombo)filters.getSelectedItem();
				String filter = itemFilter.value;
				ItemCombo itemBy = (ItemCombo)bys.getSelectedItem();
				String by = itemBy.value;
				setTitle("Updating...");
				try {
					lists = musicSite.searchSong(configure.lastValueSong, configure.lastPageSong, filter + by);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				configure.songs.addAll(lists);
				SwingUtilities.invokeLater(new Runnable() {
					public void run(){
						for (Song song : lists) {
							modelSongs.addElement(song);
						}
					}
				});
//				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				updateSong = null;
				setIconStatus(false);
				songInfo.setText(configure.status);
				setTitle(configure.title);
			}
		};
		updateSong.start();
	}
	
	private void updateAlbum() {
		setIconStatus(true);
		songInfo.setText("Updating album...");
		updateAlbum = new Thread(){
			List<Album> albums = new ArrayList<Album>();
			public void run(){
				configure.lastPageAlbum++;
				ItemCombo itemFilter = (ItemCombo)filters.getSelectedItem();
				String filter = itemFilter.value;
				ItemCombo itemBy = (ItemCombo)bys.getSelectedItem();
				String by = itemBy.value;
				setTitle("Updating...");
				try {
					albums = musicSite.searchAlbum(configure.lastValueAlbum, configure.lastPageAlbum, filter + by);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				configure.albums.addAll(albums);
				SwingUtilities.invokeLater(new Runnable() {
					public void run(){
						for (Album album : albums) {
							modelAlbums.addElement(album);
						}
					}
				});
				updateAlbum = null;
				setIconStatus(false);
				songInfo.setText(configure.status);
				setTitle(configure.title);
			}
		};
		updateAlbum.start();
	}
	
	public void searchSong(final String valueSearch, final String filter, final String by){
		new Thread(){
			public void run(){
				for (int i = fromPage; i <= toPage; i++) {
					try {
						final List<Song> lists = musicSite.searchSong(valueSearch, i, filter + by);
						if (i == fromPage){
							setSongs(lists, true);
						}else{
							new Thread(){
								public void run(){
									setSongs(lists, false);
								}
							}.start();
						}
					} catch (UnsupportedEncodingException e) {
						setTitle(e.toString());
						e.printStackTrace();
					} catch (IOException e) {
						setTitle(e.toString());
						e.printStackTrace();
					}
				}
				if (configure.includeAlbum){
					setTitle("Searching album '" + valueSearch + "'...");
					searchAlbum(valueSearch);
				}
				configure.lastPageSong = toPage;
				configure.lastValueSong = valueSearch;
				setTitle(configure.title);
				labelSearch.setIcon(getImage("search24.png"));
				search.setEnabled(true);
			}
		}.start();
	}
	
	public void searchAlbum(final String valueSearch){
		new Thread(){
			public void run(){
				for (int i = fromPage; i <= toPage; i++) {
					try {
						final List<Album> lists = musicSite.searchAlbum(valueSearch, i, "");
						if (i == fromPage){
							setAlbum(lists, true);
						}else{
							new Thread(){
								public void run(){
									setAlbum(lists, false);
								}
							}.start();
						}
					} catch (UnsupportedEncodingException e) {
						setTitle(e.toString());
						e.printStackTrace();
					} catch (IOException e) {
						setTitle(e.toString());
						e.printStackTrace();
					}
				}
				configure.lastPageAlbum = toPage;
				configure.lastValueAlbum = valueSearch;
				setTitle(configure.title);
				labelSearch.setIcon(getImage("search24.png"));
				search.setEnabled(true);
			}
		}.start();
	}
	
	private void search(){
		String valueSearch = value.getText().trim();
		if (valueSearch.equals("")) return;
		labelSearch.setIcon(getImage("searching.gif"));
		search.setEnabled(false);
		configure.value = valueSearch;
		toPage = new Integer(total.getText().trim());
		fromPage = new Integer(page.getText().trim());
		toPage = (toPage == 0) ? 1 : toPage;
		fromPage = (toPage == 0) ? 1 : fromPage;
		String type = types.getSelectedItem().toString().trim();
		ItemCombo itemFilter = (ItemCombo)filters.getSelectedItem();
		String filter = itemFilter.value;
		ItemCombo itemBy = (ItemCombo)bys.getSelectedItem();
		String by = itemBy.value;
		if (type.equals("Song")){
			setTitle("Searching...");
			searchSong(valueSearch, filter, by);
		}else{
			setTitle("Searching...");
			searchAlbum(valueSearch);
		}
	}

	private void play() {
		play(songs.getSelectedIndex());
	}
	
	private void play(int index){
		if (configure.songs.isEmpty() || index >= configure.songs.size()) return;
		currentIndex = index;
		setCurrentSong(index);
		Song song = configure.songs.get(currentIndex);
		currentTitle = song.getTitle();
		try {
			if (configure.defaultMediaPlayer == null || configure.defaultMediaPlayer.equals("")
					|| !(new File(configure.defaultMediaPlayer).exists())) {
				setTitle("Getting: '" + currentTitle +"'...");
				player.play(song);
			} else {
				setTitle("Sending: '" + currentTitle + "'...");
				runtime.exec(configure.defaultMediaPlayer + " " + song.getDirectLink(configure.format));
				setTitle("Sended '" + song.getTitle() + "' - " + configure.title);
			}
		} catch (Exception e) {
			setTitle(e.toString());
			e.printStackTrace();
		}
	}
	
	private void next(){
		if (configure.songs.isEmpty()) return;
		currentIndex++;
		if (currentIndex >= configure.songs.size()){
			currentIndex = 0;
		}
		play(currentIndex);
	}
	
	private void previous(){
		if (configure.songs.isEmpty()) return;
		currentIndex--;
		if (currentIndex <= -1){
			currentIndex = configure.songs.size() - 1;
		}
		play(currentIndex);
	}

	public void setStatus(String str) {
		info.setText("[" + str + "]");
	}
	
	public void setCurrentSong(final int index){
		songs.setSelectedIndex(index);
		if (configure.update && !configure.lastValueSong.equals("") && index == songs.getLastVisibleIndex()){
			updateSong();
		}
		if (frameLyric != null && frameLyric.isVisible()){
			new Thread(){
				public void run(){
					Song song = configure.songs.get(index);
					frameLyric.setTitle("Lyric - " + song.getTitle());
					frameLyric.setLyric(song);
				}
			}.start();
		}
	}
	
	private void startup(){
		new Thread(){
			public void run(){
				try {
					if (itemAlbumStartup.isSelected()){
						setTitle("Loading...");
						setAlbum(zing.getDefaultAlbum(), true);
					}
					if (itemTopStartup.isSelected()){
						setTitle("Loading...");
						setSongs(zing.getTopVietnamese(), true);
					}
				} catch (UnsupportedEncodingException e) {
					setTitle(e.toString());
					e.printStackTrace();
				} catch (IOException e) {
					setTitle(e.toString());
					e.printStackTrace();
				}
				setTitle(configure.title);
			}
		}.start();
	}
	
	//Set proxy for application. Note restart application
	private void setProxy(String host, String port, final String username, final String password){
		if (configure.useProxy){
			if (configure.systemProxy){
				System.setProperty("java.net.useSystemProxies", "true");
				setStatus("SYSTEM PROXY");
			}else{
				System.setProperty("java.net.useSystemProxies", "false");
				System.setProperty("http.proxySet", "true");
				System.setProperty("http.proxyHost", host);
		        System.setProperty("http.proxyPort", port);
		        setStatus("MANUAL PROXY");
			}
			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
			        return new PasswordAuthentication(username, password.toCharArray());
			    }
			});
		}else{
			System.setProperty("java.net.useSystemProxies", "false");
			System.setProperty("http.proxySet", "false");
			System.setProperty("http.proxyHost", "");
	        System.setProperty("http.proxyPort", "");
	        setStatus("NO PROXY");
		}
	}
	
	public void setProxy(){
		setProxy(configure.host, configure.port, configure.username, configure.password);
	}
	
	private ImageIcon getImage(String name){
		return new ImageIcon(object.getResource("/images/" + name));
	}
	
	private JMenu initMenuAction(){
		JMenu menuAction = new JMenu("Action");
		itemUndo = new JMenuItem("Undo");
		itemUndo.setAccelerator(KeyStroke.getKeyStroke('Z', KeyEvent.CTRL_DOWN_MASK));
		itemUndo.setEnabled(false);
		itemUndo.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				setTitle("Undoing - " + configure.title);
				new Thread(){
					public void run(){
						if (history.undo()){
							setSongs(history.result, true);
							setTitle(configure.title);
						}
					}
				}.start();
			}
		});
		itemUndo.setIcon(getImage("undo.png"));
		menuAction.add(itemUndo);
		itemRedo = new JMenuItem("Redo");
		itemRedo.setAccelerator(KeyStroke.getKeyStroke('Y', KeyEvent.CTRL_DOWN_MASK));
		itemRedo.setEnabled(false);
		itemRedo.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				setTitle("Redoing - " + configure.title);
				new Thread(){
					public void run(){
						if (history.redo()){
							setSongs(history.result, true);
							setTitle(configure.title);
						}
					}
				}.start();
			}
		});
		itemRedo.setIcon(getImage("redo.png"));
		menuAction.add(itemRedo);
		menuAction.addSeparator();
		menuAction.add(itemSaveCurrentSong = new JMenuItem("Save current song", getImage("save.png")));
		itemSaveCurrentSong.setEnabled(false);
		itemSaveCurrentSong.setToolTipText("Current song can saved when completed buffer");
		itemSaveCurrentSong.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
//				new Thread(){
//					public void run(){
//						String file = showSave("Save current song", new File(configure.oldFolder), mediaPlayer.currentSong.toTitle(mediaPlayer.currentSong.getTitle()) + ".mp3");
//						if (file == null) return;
//						if (mediaPlayer.bufferringFlag){
//							JOptionPane.showInternalMessageDialog(Main.this, "New song is buffering. Can't save now, wait to completed buffer", "Save current song", JOptionPane.INFORMATION_MESSAGE);
//							return;
//						}
//						mediaPlayer.saveFile(file);
//					}
//				}.start();
			}
		});
		menuAction.add(itemSaveMP3 = new JMenuItem("Save selected songs", getImage("save.png")));
		itemSaveMP3.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				saveFiles();
			}
		});
		return menuAction;
	}
	
	private JMenu initMenuRadio(){
		JMenu menuRadio = new JMenu("Radio");
		for (int i = 0; i < radio.radioTypes.length; i++){
			JMenuItem item = new JMenuItem(radio.radioTypes[i]);
			item.setIcon(getImage("radio.png"));
			final String type = radio.radioTypes[i];
			item.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent arg0) {
					setTitle("Loading...");
					new Thread(){
						public void run(){
							List<Song> lists = new ArrayList<Song>();
							int toPage = new Integer(total.getText().trim());
							int fromPage = new Integer(page.getText().trim());
							toPage = (toPage == 0) ? 3 : toPage;
							fromPage = (toPage == 0) ? 1 : fromPage;
							for (int i = fromPage; i <= toPage; i++) {
								lists.addAll(radio.getRadio(i, type));
							}
							setSongs(lists, true);
							songs.requestFocus();
							setTitle(configure.title);
						}
					}.start();
				}
			});
			menuRadio.add(item);
			
		}
		return menuRadio;
	}
	
	private JMenu initMenuAlbum(){
		JMenu menuAlbum = new JMenu("Album");
		for (int i = 0; i < Zing.titlesAlbumType.length; i++){
			JMenuItem item = new JMenuItem(Zing.titlesAlbumType[i]);
			item.setIcon(getImage("album16.png"));
			final String type = Zing.titlesAlbumType[i];
			item.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent arg0) {
					setTitle("Loading...");
					new Thread(){
						public void run(){
							List<Album> lists = new ArrayList<Album>();
							int toPage = new Integer(total.getText().trim());
							int fromPage = new Integer(page.getText().trim());
							toPage = (toPage == 0) ? 1 : toPage;
							fromPage = (toPage == 0) ? 1 : fromPage;
							for (int i = fromPage; i <= toPage; i++) {
								try {
									lists.addAll(zing.getAlbumBy(type, i));
								} catch (IOException e) {
									setTitle(e.toString());
									e.printStackTrace();
								}
							}
							setAlbum(lists, true);
							setTitle(configure.title);
						}
					}.start();
				}
			});
			menuAlbum.add(item);
			
		}
		return menuAlbum;
	}
	
	private JMenu initMenuSong(){
		JMenu menuAlbum = new JMenu("Song");
		for (int i = 0; i < Zing.titlesSongType.length; i++){
			JMenuItem item = new JMenuItem(Zing.titlesSongType[i]);
			item.setIcon(getImage("song16.png"));
			final String type = Zing.titlesSongType[i];
			item.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent arg0) {
					setTitle("Loading...");
					new Thread(){
						public void run(){
							List<Song> lists = new ArrayList<Song>();
							int toPage = new Integer(total.getText().trim());
							int fromPage = new Integer(page.getText().trim());
							toPage = (toPage == 0) ? 1 : toPage;
							fromPage = (toPage == 0) ? 1 : fromPage;
							for (int i = fromPage; i <= toPage; i++) {
								try {
									lists.addAll(zing.getSongByType(type, i));
								} catch (IOException e) {
									setTitle(e.toString());
									e.printStackTrace();
								}
							}
							setSongs(lists, true);
							setTitle(configure.title);
						}
					}.start();
				}
			});
			menuAlbum.add(item);
			
		}
		return menuAlbum;
	}
	
	private JPanel initPanelSearch(){
		JPanel panelSearch = new JPanel(new FlowLayout(0, 5, 5));
		panelSearch.add(Box.createRigidArea(new Dimension(1,0)));
		panelSearch.add(labelSearch = new JLabel(getImage("search24.png"), JLabel.CENTER));
		panelSearch.add(value = new JTextField(25));
		value.addKeyListener(new KeyAdapter() {
			
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					search();
				}
			}
		});
		panelSearch.add(new JLabel("Site "));
		panelSearch.add(sites = new JComboBox(new String[]{"mp3.zing.vn", "nhaccuatui.com", "chiasenhac.com"}));
		sites.setFocusable(false);
		sites.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				configure.site = sites.getSelectedItem().toString();
				configure.lastPageSong = 0;
				configure.lastPageAlbum = 0;
				if (configure.site.equals("mp3.zing.vn")){
					musicSite = Zing.getInstance();
				}else if (configure.site.equals("nhaccuatui.com")){
					musicSite = NhacCuaTui.getInstance();
				}else if (configure.site.equals("chiasenhac.com")){
					musicSite = ChiaSeNhac.getInstance();
				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						setFilterAndBy();
					}
				});
			}
		});
		panelSearch.add(new JLabel("Type "));
		panelSearch.add(types = new JComboBox(new String[]{"Song", "Album"}));
		types.setFocusable(false);
		types.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				configure.type = types.getSelectedItem().toString();
				if (configure.type.equals("Song")){
					filters.setEnabled(true);
					bys.setEnabled(true);
				}else{
					filters.setEnabled(false);
					bys.setEnabled(false);
				}
			}
		});
		panelSearch.add(new JLabel("By "));
		panelSearch.add(bys = new JComboBox(modelBys = new DefaultComboBoxModel()));
		bys.setEditable(false);
		bys.setFocusable(false);
		bys.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				configure.by = (ItemCombo) bys.getSelectedItem();
			}
		});
		panelSearch.add(new JLabel("Filter "));
		panelSearch.add(filters = new JComboBox(modelFilters = new DefaultComboBoxModel()));
		filters.setEditable(false);
		filters.setFocusable(false);
		filters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				configure.filter = (ItemCombo) filters.getSelectedItem();
			}
		});
		panelSearch.add(new JLabel("Page "));
		panelSearch.add(page = new JTextField("1", 2));
		page.addKeyListener(new KeyAdapter() {
			
			public void keyReleased(KeyEvent arg0) {
				configure.page = page.getText();
				if(page.getText().compareTo(total.getText()) > 0){
					total.setText(page.getText());
					configure.total = page.getText();
				}
			}
		});
		panelSearch.add(new JLabel("to"));
		panelSearch.add(total = new JTextField("1", 2));
		total.addKeyListener(new KeyAdapter() {
			
			public void keyReleased(KeyEvent arg0) {
				configure.total = total.getText();
			}
		});
		panelSearch.add(search = new JButton("Go"));
		search.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				search();
//				configure.lastPage = Integer.parseInt(page.getText().trim());
//				configure.lastValue = value.getText().trim();
			}
		});
		panelSearch.add(new JLabel("Repeat "));
		panelSearch.add(repeats = new JComboBox(new String[]{"Default", "Random" , "One song", "All song",}));
		repeats.setEditable(false);
		repeats.setFocusable(false);
		repeats.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				configure.repeat = repeats.getSelectedItem().toString();
			}
		});
		panelSearch.add(Box.createGlue());
		panelSearch.add(selectQuality = new JLabel(configure.format.getImage()));
		selectQuality.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//		selectQuality.setPreferredSize(new Dimension(26, 16));
		selectQuality.setFocusable(false);
		selectQuality.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				if (configure.format.equals(Format.LOSSLESS)){
					configure.format = Format.MP3_128_KBPS;
				} else {
					for (Format format : Format.values()){
						if (format.compareTo(configure.format) > 0){
							configure.format = format;
							break;
						}
					}
				}
				selectQuality.setIcon(configure.format.getImage());
			}
		});

		panelSearch.add(Box.createRigidArea(new Dimension(0, 3)));
		return panelSearch;
	}
	
	private JMenu initMenuMedia(){
		menuMedia = new JMenu("Media");
		JMenuItem itemSelectMedia;
		menuMedia.add(itemSelectMedia = new JMenuItem("Select media player..."));
		itemSelectMedia.setIcon(getImage("zing16.png"));
		itemSelectMedia.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				String folder = System.getenv().get("ProgramFiles");
				folder = (folder == null)? "." : folder;
				final String str = showOpen("Chooser media player", new File(folder), "");
				if (str == null)
					return;
				boolean exist = false;
				if (configure.mediaPlayers.contains(str)) {
					configure.defaultMediaPlayer = str;
					exist = true;
				}
				if (!exist) {
					configure.mediaPlayers.add(str);
					configure.defaultMediaPlayer = str;
					JCheckBoxMenuItem item = new JCheckBoxMenuItem(str);
					menuMedia.add(item);
					group.add(item);
					item.setSelected(true);
					item.addActionListener(new ActionListener() {
						
						public void actionPerformed(ActionEvent arg0) {
							configure.defaultMediaPlayer = str;
						}
					});
				}

			}
		});
		menuMedia.addSeparator();
		group = new ButtonGroup();
		for (int i = 0; i < configure.mediaPlayers.size(); i++){
			final String media = configure.mediaPlayers.get(i);
			JCheckBoxMenuItem item = new JCheckBoxMenuItem(media);
			item.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent arg0) {
					configure.defaultMediaPlayer = media;
				}
			});
			if (media.equals(configure.defaultMediaPlayer)){
				item.setSelected(true);
			}
			group.add(item);
			menuMedia.add(item);
		}
		return menuMedia;
	}
	
	private JMenu initMenuUserPlaylists(){
		menuUserPlaylists = new JMenu("Playlists");
		JMenuItem itemAddPlaylist = new JMenuItem("Add playlist...");
		itemAddPlaylist.setIcon(getImage("add.png"));
		itemAddPlaylist.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				String title = JOptionPane.showInputDialog(Main.this, "Enter name for playlist");
				if (title == null || title.equals("")) return;
				Playlist playlist = new Playlist(title);
				if (!configure.userPlaylists.contains(playlist)){
					if (configure.userPlaylists.size() == 0) menuUserPlaylists.addSeparator();
					configure.userPlaylists.add(playlist);
					JMenuItem item = new JMenuItem(title);
					item.addActionListener(new ActionListener() {
						
						public void actionPerformed(ActionEvent arg0) {
							setSongs(configure.userPlaylists.get(configure.userPlaylists.size() - 1).lists, true);
						}
					});
					item.setIcon(getImage("list16.png"));
					menuUserPlaylists.add(item);
					popupMenu.remove(menuAddToPlaylist);
					menuAddToPlaylist = initMenuAddToPlaylist();
					popupMenu.add(initMenuAddToPlaylist());
				}
			}
		});
		menuUserPlaylists.add(itemAddPlaylist);
		if (configure.userPlaylists.size() > 0) menuUserPlaylists.addSeparator();
		for (int i = 0; i < configure.userPlaylists.size(); i++){
			final int index = i;
			final String title = configure.userPlaylists.get(i).title;
			JMenuItem item = new JMenuItem(title);
			item.setIcon(getImage("list16.png"));
			item.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent arg0) {
					setSongs(configure.userPlaylists.get(index).lists, true);
				}
			});
			menuUserPlaylists.add(item);
		}
		return menuUserPlaylists;
	}
	
	private JMenu initMenuAddToPlaylist(){
		menuAddToPlaylist = new JMenu("Add to playlist");
		menuAddToPlaylist.setIcon(getImage("add.png"));
		for (int i = 0; i < configure.userPlaylists.size(); i++){
			JMenuItem item = new JMenuItem(configure.userPlaylists.get(i).title);
			item.setIcon(getImage("list16.png"));
			final int index = i;
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					int[] selected = songs.getSelectedIndices();
					for (int i = 0; i < selected.length; i++){
						configure.userPlaylists.get(index).lists.add(configure.songs.get(selected[i]));
					}
				}
			});
			menuAddToPlaylist.add(item);
		}
		return menuAddToPlaylist;
	}

	private String showSave(String title, File currentFolder, String fileSelected){
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setDialogTitle(title);
		chooser.setCurrentDirectory(currentFolder);
		chooser.setSelectedFile(new File(fileSelected));
		int ret = chooser.showSaveDialog(Main.this);
		if(ret == JFileChooser.APPROVE_OPTION){
			configure.oldFolder = chooser.getCurrentDirectory().getAbsolutePath();
			return chooser.getSelectedFile().getAbsolutePath();
		}
		return null;
	}
	
	private String showOpen(String title, File currentFolder, String fileSelected){
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setDialogTitle(title);
		chooser.setCurrentDirectory(currentFolder);
		chooser.setSelectedFile(new File(fileSelected));
		int ret = chooser.showOpenDialog(Main.this);
		if(ret == JFileChooser.APPROVE_OPTION){
			configure.oldFolder = chooser.getCurrentDirectory().getAbsolutePath();
			return chooser.getSelectedFile().getAbsolutePath();
		}
		return null;
	}
	
	public void deleteLinks(){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final int[] remove = songs.getSelectedIndices();
				for (int i = remove.length - 1; i >= 0; i--) {
					modelSongs.remove(remove[i]);
					configure.songs.remove(remove[i]);
				}
			}
		});
	}
	
	public void setIconStatus(boolean set){
		iconStatus.setVisible(set);
	}
	
	private void shareFacebook(String link) {
		try {
			openLink("http://www.facebook.com/sharer.php?u=" + URLEncoder.encode(link, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private void openLink(String link) {
	    if (Desktop.isDesktopSupported()) {
	        try {
				Desktop.getDesktop().browse(new URI(link));
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	}
	
	public void setSongInfo(String str){
		songInfo.setText(str);
		int position = str.indexOf("/album/");
		if (position == -1){
			songInfo.setToolTipText(str);
			return;
		}
		str = str.substring(position);
		final String link = "http://mp3.zing.vn" + str.substring(0, str.indexOf("\""));
		String title = "";
		position = str.indexOf(">");
		str = str.substring(position + 1);
		title = str.substring(0, str.indexOf("<"));
		songInfo.setToolTipText("<html><b>Load album " + title + "</b></html>");
		songInfo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		songInfo.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e){
				if (SwingUtilities.isLeftMouseButton(e)){
					new Thread(){
						public void run(){
							try {
								setTitle("Loading...");
								setSongs(zing.getAlbum(link), true);
								setTitle(configure.title);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}.start();
				}
			}
		});
	}
	
	private void setFilterAndBy(){
		modelBys.removeAllElements();
		for (ItemCombo item : musicSite.getBys()){
			modelBys.addElement(item);
		}
		modelFilters.removeAllElements();
		for (ItemCombo item : musicSite.getFilters()){
			modelFilters.addElement(item);
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					new Main();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
