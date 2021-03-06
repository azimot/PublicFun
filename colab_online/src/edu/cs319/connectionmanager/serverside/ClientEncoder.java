package edu.cs319.connectionmanager.serverside;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.cs319.client.IClient;
import edu.cs319.connectionmanager.messaging.Message;
import edu.cs319.connectionmanager.messaging.MessageOutputStream;
import edu.cs319.connectionmanager.messaging.MessageType;
import edu.cs319.dataobjects.DocumentSubSection;
import edu.cs319.dataobjects.SectionizedDocument;
import edu.cs319.server.CoLabPrivilegeLevel;
import edu.cs319.util.Util;

/**
 * This class turns methods called on it by the Server into serialized byte strings and then sends
 * them down the network to the client.
 * 
 * @author Justin Nelson
 * 
 */
public class ClientEncoder implements IClient {
	private String username;
	private Socket connection;

	/**
	 * @param username
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public ClientEncoder(String username, Socket connection) throws UnknownHostException,
			IOException {
		if (Util.DEBUG) {
			System.out.println("Creating server side client connection");
		}
		this.username = username;
		this.connection = connection;
	}

	private boolean printMessageToStream(Message m) {
		MessageOutputStream out;
		try {
			out = new MessageOutputStream(connection.getOutputStream());
			out.printMessage(m);
		} catch (IOException e) {
			if (Util.DEBUG) {
				e.printStackTrace();
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean changeUserPrivilege(String username, CoLabPrivilegeLevel newPriv) {
		List<String> arg = new ArrayList<String>();
		arg.add(newPriv.toString());
		Message tosent = new Message(MessageType.CHANGE_USER_PRIV, username, arg);
		return printMessageToStream(tosent);
	}

	@Override
	public boolean coLabRoomMemberArrived(String username, final CoLabPrivilegeLevel priv) {
		Message tosend = new Message(MessageType.MEMBER_JOIN_ROOM, username,
				new ArrayList<String>() {
					{
						add(priv.toString());
					}
				});
		return printMessageToStream(tosend);
	}

	@Override
	public boolean coLabRoomMemberLeft(String username) {
		Message tosend = new Message(MessageType.MEMBER_LEAVE_ROOM, username,
				new ArrayList<String>());
		return printMessageToStream(tosend);
	}

	@Override
	public boolean newChatMessage(String usernameSender, String message) {
		if (Util.DEBUG) {
			System.out.println("Sending new chat message down the pipes.  Reciever: " + username);
		}
		List<String> args = new ArrayList<String>();
		args.add(message);
		Message m = new Message(MessageType.NEW_MESSAGE, usernameSender, args);
		return printMessageToStream(m);
	}

	@Override
	public boolean newChatMessage(String usernameSender, String message, String recipiant) {
		if (Util.DEBUG) {
			System.out.println("Sending new chat message down the pipes.  Reciever: " + username);
		}
		List<String> args = new ArrayList<String>();
		args.add(message);
		args.add(recipiant);
		Message m = new Message(MessageType.NEW_PRIVATE_MESSAGE, usernameSender, args);
		return printMessageToStream(m);
	}

	@Override
	public String toString() {
		return username;
	}

	@Override
	public String getUserName() {
		return username;
	}

	@Override
	public void setUserName(String un) {
		username = un;
	}

	@Override
	public boolean allCoLabRooms(Collection<String> roomNames) {
		List<String> args = new ArrayList<String>();
		for (String s : roomNames) {
			args.add(s);
		}
		Message m = new Message(MessageType.GET_ROOM_LIST, "Server Initiated", args);
		return printMessageToStream(m);
	}

	@Override
	public boolean allUsersInRoom(List<String> usernames, List<CoLabPrivilegeLevel> privs) {
		List<String> args = new ArrayList<String>();
		for (String s : usernames) {
			args.add(s);
		}
		args.add(null);
		for (CoLabPrivilegeLevel s : privs) {
			args.add(s.toString());
		}
		Message m = new Message(MessageType.MEMBERS_IN_ROOM, "Server Initiated", args);
		return printMessageToStream(m);
	}

	@Override
	public boolean newSubSection(String username, String documentName, String sectionID,
			DocumentSubSection section, int idx) {
		if (Util.DEBUG) {
			System.out.println("ClientEncoder.newSubSection: docname: " + documentName
					+ " sectID: " + sectionID);
		}
		List<String> args = new ArrayList<String>();
		args.add(documentName);
		args.add(sectionID);
		args.add(section.toDelimmitedString());
		args.add("" + idx);
		Message m = new Message(MessageType.NEW_SUBSECTION, username, args);
		return printMessageToStream(m);
	}

	@Override
	public boolean subsectionLocked(String usernameSender, String documentName, String sectionID) {
		if (Util.DEBUG) {
			System.out.println("ClientEncoder.subSectionLocked: docname: " + documentName
					+ " sectID: " + sectionID);
		}
		List<String> args = new ArrayList<String>();
		args.add(documentName);
		args.add(sectionID);
		Message m = new Message(MessageType.SUBSECTION_LOCKED, usernameSender, args);
		return printMessageToStream(m);
	}

	@Override
	public boolean subsectionUnLocked(String usernameSender, String documentName, String sectionID) {
		if (Util.DEBUG) {
			System.out.println("ClientEncoder.subSectionUnLocked: docname: " + documentName
					+ " sectID: " + sectionID);
		}
		List<String> args = new ArrayList<String>();
		args.add(documentName);
		args.add(sectionID);
		Message m = new Message(MessageType.SUBSECTION_UNLOCKED, usernameSender, args);
		return printMessageToStream(m);
	}

	@Override
	public boolean updateAllSubsections(String documentId, List<DocumentSubSection> allSections) {
		if (Util.DEBUG) {
			System.out.println("ClientEncoder.updateAllSubsections: docname: " + documentId);
		}
		List<String> args = new ArrayList<String>();
		args.add(documentId);
		for (DocumentSubSection doc : allSections) {
			args.add(doc.toDelimmitedString());
		}
		Message m = new Message(MessageType.UPDATE_ALL_SUBSECTIONS, "Server Initiated", args);
		return printMessageToStream(m);
	}

	@Override
	public boolean updateSubsectionAll(String usernameSender, String documentname,
			String sectionID, DocumentSubSection section) {
		List<String> args = new ArrayList<String>();
		args.add(documentname);
		args.add(sectionID);
		args.add(section.toDelimmitedString());
		if (Util.DEBUG) {
			System.out.println("_________________________________________________Delimmitde doc, check it out below");
			System.out.println(args.get(2));
		}
		Message m = new Message(MessageType.UPDATE_SUBSECTION, usernameSender, args);
		return printMessageToStream(m);
	}

	@Override
	public boolean updateSubsectionInsert(String usernameSender, String documentname,
			String sectionID, int start, String update) {
		if (Util.DEBUG) {
			System.out.println("ClientEncoder.updateSubsection: docname: " + documentname
					+ " sectID: " + sectionID);
		}
		List<String> args = new ArrayList<String>();
		args.add(documentname);
		args.add(sectionID);
		args.add(Integer.toString(start));
		args.add(update);
		Message m = new Message(MessageType.UPDATE_SUBSECTION_INSERT, usernameSender, args);
		return printMessageToStream(m);
	}

	@Override
	public boolean updateSubsectionRemove(String usernameSender, String documentname,
			String sectionID, int start, int end) {
		if (Util.DEBUG) {
			System.out.println("ClientEncoder.updateSubsection: docname: " + documentname
					+ " sectID: " + sectionID);
		}
		List<String> args = new ArrayList<String>();
		args.add(documentname);
		args.add(sectionID);
		args.add(Integer.toString(start));
		args.add(Integer.toString(end));
		Message m = new Message(MessageType.UPDATE_SUBSECTION_REMOVE, usernameSender, args);
		return printMessageToStream(m);
	}

	@Override
	public boolean subSectionRemoved(String username, String documentName, String sectionID) {
		if (Util.DEBUG) {
			System.out.println("ClientEncoder.subSectionRemoved: docname: " + documentName
					+ " sectID: " + sectionID);
		}
		List<String> args = new ArrayList<String>();
		args.add(documentName);
		args.add(sectionID);
		Message m = new Message(MessageType.REMOVE_SUBSECTION, username, args);
		return printMessageToStream(m);
	}

	@Override
	public boolean newDocument(String username, String documentName) {
		if (Util.DEBUG) {
			System.out.println("ClientEncoder.newDocument: docname: " + documentName);
		}
		List<String> args = new ArrayList<String>();
		args.add(documentName);
		Message m = new Message(MessageType.NEW_DOCUMENT, username, args);
		return printMessageToStream(m);
	}

	@Override
	public boolean removeDocument(String username, String documentName) {
		List<String> args = new ArrayList<String>();
		args.add(documentName);
		Message m = new Message(MessageType.REMOVE_DOCUMENT, username, args);
		return printMessageToStream(m);
	}

	@Override
	public boolean subsectionFlopped(String usernameSender, String documentName,
			String sectionIDMoveUp, String sectionIDMoveDown) {
		List<String> args = new ArrayList<String>();
		args.add(documentName);
		args.add(sectionIDMoveUp);
		args.add(sectionIDMoveDown);
		Message m = new Message(MessageType.SUBSECTION_FLOPPED, usernameSender, args);
		return printMessageToStream(m);
	}

	@Override
	public boolean subSectionSplit(String username, String documentName, String oldSecName,
			String newName1, String newName2, int index) {
		List<String> args = new ArrayList<String>();
		args.add(documentName);
		args.add(oldSecName);
		args.add(newName1);
		args.add(newName2);
		args.add(index + "");
		Message m = new Message(MessageType.SUBSECTION_SPLIT, username, args);
		return printMessageToStream(m);
	}

	public boolean subSectionCombined(String username, String documentName, String sectionA,
			String sectionB, String newSection) {

		List<String> args = new ArrayList<String>();
		args.add(documentName);
		args.add(sectionA);
		args.add(sectionB);
		args.add(newSection);
		Message m = new Message(MessageType.SUBSECTION_COMBINE, username, args);
		return printMessageToStream(m);
	}

	@Override
	public boolean listOfPersistedRooms(Collection<String> romnames) {
		List<String> args = new ArrayList<String>(romnames.size());
		args.addAll(romnames);
		Message m = new Message(MessageType.ALL_PERSISTED_ROOMS, "admin", args);
		return printMessageToStream(m);
	}

	@Override
	public boolean persistedCoLabRoom(List<SectionizedDocument> docs) {
		List<String> args = new ArrayList<String>(docs.size());
		for (SectionizedDocument doc : docs) {
			args.add(doc.toSerializedString());
		}
		Message m = new Message(MessageType.PERSISTED_ROOM, "admin", args);
		return printMessageToStream(m);
	}
	
	@Override
	public boolean userAuthenticatedResult(String username, boolean success) {
		List<String> args = new ArrayList<String>();
		args.add(Boolean.toString(success));
		if (Util.DEBUG) {
			System.out.println("~~~~~~~~~~~~~Client Encoder:  Success here was " + args.get(0));
		}
		Message m = new Message(MessageType.USER_AUTHENTICATE, username, args);
		return printMessageToStream(m);
	}

	@Override
	public boolean userCreateResult(String username, boolean success) {
		List<String> args = new ArrayList<String>();
		args.add(Boolean.toString(success));
		Message m = new Message(MessageType.USER_CREATE, username, args);
		return printMessageToStream(m);
	}

}
