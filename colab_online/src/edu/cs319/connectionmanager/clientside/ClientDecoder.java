package edu.cs319.connectionmanager.clientside;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.cs319.client.IClient;
import edu.cs319.connectionmanager.messaging.Message;
import edu.cs319.connectionmanager.messaging.MessageInputStream;
import edu.cs319.dataobjects.DocumentSubSection;
import edu.cs319.server.CoLabPrivilegeLevel;
import edu.cs319.util.NotYetImplementedException;
import edu.cs319.util.Util;

/**
 * This class needs to be instantiated with some implementation of IClient. It listens on port 3333
 * for messages from the server.
 * 
 * It calls the proper methods on the Client passed into the constructor which will properly display
 * the changes in some way to the user.
 * 
 * @author Justin Nelson
 * @author Wayne Rowcliffe
 * 
 */
public class ClientDecoder implements Runnable {
	public static final int DEFAULT_PORT = 3333;

	private IClient actualClient;
	private MessageInputStream mIn;

	public ClientDecoder(IClient actualClient, InputStream in) {
		this.actualClient = actualClient;
		this.mIn = new MessageInputStream(in);
	}

	@Override
	public void run() {
		while (true) {
			try {
				decodeMessage(mIn.readMessage());
			} catch (IOException e) {
				if (Util.DEBUG) {
					e.printStackTrace();
				}
			}
		}
	}

	private void decodeMessage(Message message) throws IOException {
		String username = message.getSentByClientName();
		List<String> arg = message.getArgumentList();
		switch (message.getMessageType()) {
		case NEW_MESSAGE:
			actualClient.newChatMessage(username, arg.get(0));
			break;
		case NEW_PRIVATE_MESSAGE:
			actualClient.newChatMessage(username, arg.get(0), arg.get(1));
			break;
		case MEMBER_JOIN_ROOM:
			actualClient.coLabRoomMemberArrived(username);
			break;
		case UPDATE_ALL_SUBSECTIONS:
			List<DocumentSubSection> args = new ArrayList<DocumentSubSection>();
			for (int i = 1; i < arg.size(); i++) {
				args.add(DocumentSubSection.getFromDelimmitedString(arg.get(i)));
			}
			actualClient.updateAllSubsections(arg.get(0), args);
		case UPDATE_SUBSECTION:
			actualClient.updateSubsection(username, arg.get(0), DocumentSubSection
					.getFromDelimmitedString(arg.get(1)), arg.get(2));
		case SUBSECTION_LOCKED:
			actualClient.subsectionLocked(username, arg.get(0), arg.get(1));
		case SUBSECTION_UNLOCKED:
			actualClient.subsectionUnLocked(username, arg.get(0), arg.get(1));
		case NEW_SUBSECTION:
			actualClient.newSubSection(username, arg.get(0), arg.get(1), DocumentSubSection
					.getFromDelimmitedString(arg.get(2)), Integer.parseInt(arg.get(3)));
		case MEMBER_LEAVE_ROOM:
			actualClient.coLabRoomMemberLeft(username);
		case MEMBERS_IN_ROOM:
			actualClient.allUsersInRoom(arg);
		case CHANGE_USER_PRIV:
			actualClient.changeUserPrivilege(username, CoLabPrivilegeLevel
					.getPrivilegeLevelFromString(arg.get(0)));
		case GET_ROOM_LIST:
			actualClient.allCoLabRooms(arg);
		case REMOVE_SUBSECTION:
			actualClient.subSectionRemoved(username, arg.get(0), arg.get(1));
		default:
			throw new NotYetImplementedException();
		}
	}
}
