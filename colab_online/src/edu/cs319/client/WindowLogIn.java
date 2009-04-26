package edu.cs319.client;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import edu.cs319.connectionmanager.clientside.ConnectionFactory;
import edu.cs319.connectionmanager.clientside.Proxy;
import edu.cs319.util.Util;

/**
 * The window where the user logs in
 * 
 * @author Amelia Gee
 * @author Justin Nelson
 * 
 */
public class WindowLogIn extends JDialog {

	private JTextField hostField = new JTextField(20);
	private JTextField usernameField = new JTextField(20);
	private JPasswordField passwordField = new JPasswordField(20);
	private JButton logInButton = new JButton("Log In");
	private JButton cancelButton = new JButton("Cancel");
	private JButton createNewUserButton = new JButton("Create New User");

	private IClient client;
	private Proxy serverConnection;

	private WindowLogIn(JFrame parent, IClient client) {
		super(parent, "CoLab Log In");
		setLocation(new Point(parent.getLocation().x + 50, parent.getLocation().y + 50));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		if (Util.DEBUG) {
			hostField.setText("129.186.150.18");
			usernameField.setText((int) (Math.random() * 1000) + "");
		}
		this.client = client;
		Dimension minSize = new Dimension(380, 250);
		this.setSize(minSize);
		this.setResizable(false);
		setModal(true);
		setUpAppearance();
		setUpListeners();
	}

	private void setUpAppearance() {
		JLabel hostNameLabel = new JLabel("Host Name");
		JLabel usernameLabel = new JLabel("User Name");
		JLabel passwordLabel = new JLabel("Password");

		logInButton.setMnemonic(KeyEvent.VK_L);
		createNewUserButton.setMnemonic(KeyEvent.VK_C);
		cancelButton.setMnemonic(KeyEvent.VK_N);

		JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		Insets borderInsets = new Insets(5, 5, 5, 5);
		c.insets = borderInsets;

		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_END;
		mainPanel.add(hostNameLabel, c);

		c.gridx = 1;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_START;
		mainPanel.add(hostField, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LINE_END;
		mainPanel.add(usernameLabel, c);

		c.gridx = 1;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_START;
		mainPanel.add(usernameField, c);

		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LINE_END;
		mainPanel.add(passwordLabel, c);

		c.gridx = 1;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_START;
		mainPanel.add(passwordField, c);

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 4;
		c.anchor = GridBagConstraints.CENTER;
		mainPanel.add(logInButton, c);

		c.gridy = 4;
		mainPanel.add(createNewUserButton, c);
		
		c.gridy = 5;
		mainPanel.add(cancelButton, c);

		this.add(mainPanel);
	}

	public static Proxy showLoginWindow(WindowClient parent, IClient client) {
		WindowLogIn win = new WindowLogIn(parent, client);
		win.setVisible(true);
		if (win.serverConnection == null)
			return null;
		parent.setUserName(win.usernameField.getText());
		return win.serverConnection;
	}

	private void login(String host, String username) {
		serverConnection = ConnectionFactory.getNetworkedInstance().connect(host, 4444, client,
				username);
	}

	public static boolean isValidUserName(String usernme) {
		if (usernme.length() < 1)
			return false;
		return !(usernme.contains(" ") || usernme.startsWith("@"));
	}

	public static boolean isValidPassword(String pw) {
		if (pw.length() < 1)
			return false;
		return true;
	}

	private void setUpListeners() {
		hostField.addKeyListener(enterKey);
		usernameField.addKeyListener(enterKey);
		passwordField.addKeyListener(enterKey);
		logInButton.addKeyListener(enterKey);
		
		logInButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!isValidUserName(usernameField.getText())) {
					JOptionPane.showMessageDialog(WindowLogIn.this,
							"Your username may not contain a space or begin with an '@'.  "
									+ "It must also be at least one character long.");
					return;
				}
				login(hostField.getText(), usernameField.getText());
				dispose();
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				serverConnection = null;
				dispose();
			}
		});

		createNewUserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String username = JOptionPane.showInputDialog(WindowLogIn.this,
						"Please enter your new username:", "Enter Username",
						JOptionPane.QUESTION_MESSAGE);
				if (!isValidUserName(username)) {
					JOptionPane.showMessageDialog(WindowLogIn.this,
							"Your username may not contain a space or begin with an '@'.  "
									+ "It must also be at least one character long.");
					return;
				}
				String password = JOptionPane.showInputDialog(WindowLogIn.this,
						"Please enter your new password:", "Enter Password",
						JOptionPane.QUESTION_MESSAGE);
				if(!isValidPassword(password)) {
					JOptionPane.showMessageDialog(WindowLogIn.this, 
							"Your password must be at least one character long.");
					return;
				}
				//TODO once createNewUser method(s) written finish this
			}
		});
	}

	private KeyListener enterKey = new KeyListener() {
		@Override
		public void keyPressed(KeyEvent arg0) {
			if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
				logInButton.doClick();
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
		}
	};

}
