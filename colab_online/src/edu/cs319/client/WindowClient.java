package edu.cs319.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import edu.cs319.client.customcomponents.JChatPanel;
import edu.cs319.client.customcomponents.JDocTabPanel;
import edu.cs319.client.customcomponents.JRoomList;
import edu.cs319.connectionmanager.clientside.Proxy;
import edu.cs319.dataobjects.DocumentSubSection;
import edu.cs319.server.CoLabPrivilegeLevel;

/**
 * 
 * @author Amelia Gee
 * @author Justin Nelson
 * 
 */
public class WindowClient extends JFrame implements IClient {

	private Proxy proxy;

	private WindowLogIn loginFrame;
	private WindowJoinCoLab colabRoomFrame;
	
	private JTabbedPane documentPane;
	private JRoomList roomMemberList;
	private JChatPanel chatPanel;

	private JMenuItem openDocument;
	private JMenuItem logIn;
	private JMenuItem joinCoLabRoom;
	private JMenuItem disconnect;
	private JMenuItem exitCoLab;
	
	public WindowClient() {
		setTitle("CoLab");
		setSize(new Dimension(900, 500));
		setJMenuBar(createMenuBar());

		roomMemberList = new JRoomList();
		documentPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		documentPane.addTab("panel1", new JDocTabPanel());
		documentPane.addTab("panel2", new JDocTabPanel());
		chatPanel = new JChatPanel();

		JPanel roomPanel = new JPanel();
		roomPanel.add(roomMemberList);

		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.add(roomPanel, BorderLayout.WEST);
		panel.add(documentPane, BorderLayout.CENTER);
		panel.add(chatPanel, BorderLayout.EAST);
		add(panel);
	}

	private JMenuBar createMenuBar() {
		JMenuBar mainMenu = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenu view = new JMenu("View");
		JMenu help = new JMenu("Help");
		openDocument = new JMenuItem("Add New Document");
		logIn = new JMenuItem("Log In");
		joinCoLabRoom = new JMenuItem("Join CoLab Room");
		disconnect = new JMenuItem("Disconnect");
		exitCoLab = new JMenuItem("Exit CoLab");
		final JCheckBox showChat = new JCheckBox("Display Chat Window");
		showChat.setSelected(true);
		ActionListener chatChecked = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chatPanel.setVisible(showChat.isSelected());
			}
		};
		showChat.addActionListener(chatChecked);
		view.add(showChat);
		file.setMnemonic(KeyEvent.VK_F);
		help.setMnemonic(KeyEvent.VK_H);
		openDocument.setMnemonic(KeyEvent.VK_O);
		logIn.setMnemonic(KeyEvent.VK_L);
		joinCoLabRoom.setMnemonic(KeyEvent.VK_J);
		disconnect.setMnemonic(KeyEvent.VK_D);
		exitCoLab.setMnemonic(KeyEvent.VK_X);

		file.add(openDocument);
		file.add(logIn);
		file.add(joinCoLabRoom);
		file.add(disconnect);
		file.add(exitCoLab);
		
		openDocument.setEnabled(false);
		joinCoLabRoom.setEnabled(false);
		disconnect.setEnabled(false);
		
		mainMenu.add(file);
		mainMenu.add(view);
		mainMenu.add(help);
		return mainMenu;
	}
	
	private void setLogIn() {
		joinCoLabRoom.setEnabled(true);
		disconnect.setEnabled(true);
	}
	
	private void setJoinedRoom() {
		openDocument.setEnabled(true);
	}
	
	private void setDisconnected() {
		openDocument.setEnabled(false);
		joinCoLabRoom.setEnabled(false);
		disconnect.setEnabled(false);
	}

	@Override
	public boolean allCoLabRooms(Collection<String> roomNames) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean allUsersInRoom(Collection<String> usernames) {
		roomMemberList.updateList(usernames);
		return true;
	}

	@Override
	public boolean changeUserPrivilege(String username, CoLabPrivilegeLevel newPriv) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean coLabRoomMemberArrived(String username) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean coLabRoomMemberLeft(String username) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean newChatMessage(String usernameSender, String message) {
		chatPanel.newChatMessage(usernameSender, message);
		return false;
	}

	@Override
	public boolean newChatMessage(String usernameSender, String message, String recipiant) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean newSubSection(String username, String sectionID, String documentName,
			DocumentSubSection section, int idx) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean subsectionLocked(String usernameSender, String documentName, String sectionID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean subsectionUnLocked(String usernameSender, String documentName, String sectionID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean subSectionRemoved(String username, String sectionID, String documentName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateAllSubsections(String documentId, List<DocumentSubSection> allSections) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateSubsection(String usernameSender, String documentname,
			DocumentSubSection section, String sectionID) {
		// TODO Auto-generated method stub
		return false;
	}

}
