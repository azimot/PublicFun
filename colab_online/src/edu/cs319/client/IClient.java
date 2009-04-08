package edu.cs319.client;

import java.util.List;

import edu.cs319.dataobjects.DocumentSubSection;
import edu.cs319.server.CoLabPrivilegeLevel;

public interface IClient {

	public boolean coLabRoomMemberArrived(String username);

	public boolean coLabRoomMemberLeft(String username);

	public boolean updateAllSubsections(List<DocumentSubSection> allSections);

	public boolean updateSubsection(String usernameSender, DocumentSubSection section,
			String sectionID);

	public boolean subsectionLocked(String usernameSender, String sectionID);

	public boolean subsectionUnLocked(String usernameSender, String sectionID);

	public boolean newSubSection(String username, String sectionID, DocumentSubSection section);
	
	public boolean newChatMessage(String usernameSender, String message);

	public boolean newChatMessage(String usernameSender, String message, String recipiant);

	public boolean changeUserPrivilege(String username, CoLabPrivilegeLevel newPriv);

	public boolean allUsersInRoom(List<String> usernames);

	public boolean allCoLabRooms(List<String> roomNames);

	public String getName();
}
