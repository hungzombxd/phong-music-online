package zing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ProxySelector extends JDialog{
	private static final long serialVersionUID = 1L;
	private JTextField host, port, password, username;
	private JCheckBox systemProxy, useProxy;
	private JPanel panelProxy;
	private JButton ok, cancel;
	private Main main;
	private Configure configure;
	
	public ProxySelector(){
		setTitle("Set proxy...");
		setSize(260, 243);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setModal(true);
		setResizable(false);
		JPanel panelMain = new JPanel(new BorderLayout());
		panelProxy = new JPanel(new GridLayout(5,2,0,5));
		panelProxy.add(systemProxy = new JCheckBox("Use system proxy"));
		panelProxy.add(new JLabel(""));
		panelProxy.add(new JLabel("Host: "));
		panelProxy.add(host = new JTextField(10));
		panelProxy.add(new JLabel("Port: "));
		panelProxy.add(port = new JTextField(10));
//		panelProxy.add(authentication = new JCheckBox("Authentication"));
//		panelProxy.add(new JLabel(""));
		panelProxy.add(new JLabel("Username: "));
		panelProxy.add(username = new JTextField(10));
		panelProxy.add(new JLabel("Password: "));
		panelProxy.add(password = new JTextField(10));
		panelProxy.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Proxy setting:"));
		panelMain.add(panelProxy, BorderLayout.CENTER);
		panelMain.add(useProxy = new JCheckBox("Use proxy (Note: Restart application is require)"), BorderLayout.NORTH);
		JPanel panelButton = new JPanel(new GridLayout(1,2,10,10));
		panelButton.add(ok = new JButton("OK"));
		panelButton.add(cancel = new JButton("Cancel"));
		panelMain.add(panelButton, BorderLayout.SOUTH);
		Container container = getContentPane();
		JPanel panel = new JPanel();
		panel.add(panelMain);
		container.add(panel);
		useProxy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setUseProxy(useProxy.isSelected());
			}
		});
		systemProxy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setSystemProxy(systemProxy.isSelected());
			}
		});
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String strHost = host.getText();
				String strPort = port.getText();
				String strUsername = username.getText();
				String strPassword = password.getText();
				configure.host = (strHost == null)? "" : strHost.trim();
				configure.port = (strPort == null)? "" : strPort.trim();
				configure.username = (strUsername == null)? "" : strUsername.trim();
				configure.password = (strPassword == null)? "" : strPassword.trim();
				configure.useProxy = useProxy.isSelected();
				configure.systemProxy = systemProxy.isSelected();
				main.setProxy();
				setVisible(false);
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
	}
	public void setUseProxy(boolean useProxy){
		Component[] components = panelProxy.getComponents();
		this.useProxy.setSelected(useProxy);
		for(Component component:components){
			component.setEnabled(useProxy);
		}
		if (useProxy && systemProxy.isSelected()){
			host.setEnabled(false);
			port.setEnabled(false);
		}
	}
	
	public void setSystemProxy(boolean systemProxy){
		this.systemProxy.setSelected(systemProxy);
		host.setEnabled(!systemProxy);
		port.setEnabled(!systemProxy);
	}
	
	public String getHost(){
		return host.getText().trim();
	}
	
	public String getPort(){
		return port.getText().trim();
	}
	
	public String getUsername(){
		return username.getText().trim();
	}
	
	public String getPassword(){
		return password.getText().trim();
	}
	
	public boolean isUseProxy(){
		return useProxy.isSelected();
	}
	
	public boolean isSystemProxy(){
		return systemProxy.isSelected();
	}
	
	public void setHost(String host){
		if (host == null || host.equals("")) return;
		this.host.setText(host);
	}
	
	public void setPort(String port){
		if (port == null || port.equals("")) return;
		this.port.setText(port);
	}
	
	public void setUsername(String username){
		if (username == null || username.equals("")) return;
		this.username.setText(username);
	}
	
	public void setPassword(String password){
		if (password == null || password.equals("")) return;
		this.password.setText(password);
	}
	
	public void setMain(Main main){
		this.main = main;
	}
	
	public void setConfigure(Configure configure){
		this.configure = configure;
	}
}
