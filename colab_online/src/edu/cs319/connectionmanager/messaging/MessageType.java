package edu.cs319.connectionmanager.messaging;

/**
 * The different message types the CoLab Encoder/Decoders support
 **/
public enum MessageType {
	
	UPDATE_ALL_SUBSECTIONS((byte) 0x00, "Update All Subsections"), 
	UPDATE_SUBSECTION((byte) 0x01, "Update Subsection"), 
	SUBSECTION_LOCKED((byte) 0x02, "Subsection Locked"), 
	SUBSECTION_UNLOCKED((byte) 0x03, "Subsection Unlocked"), 
	NEW_SUBSECTION((byte) 0x04, "New Subsection"),
	NEW_MESSAGE((byte) 0x05, "New Message"), 
	NEW_PRIVATE_MESSAGE((byte) 0x06, "New Private Message"), 
	NEW_CLIENT((byte) 0x07, "New Client"), 
	MEMBER_JOIN_ROOM((byte) 0x08, "Member Joined Room"), 
	MEMBER_LEAVE_ROOM((byte) 0x09, "Member Left Room"), 
	MEMBERS_IN_ROOM((byte) 0x0a, "Members In Room"),
	CHANGE_USER_PRIV((byte) 0x0b, "Changed User Priv"), 
	GET_ROOM_LIST((byte) 0x0c, "Get Room List"), 
	NEW_COLAB_ROOM(	(byte) 0x0d, "New CoLab Room"), 
	COMMUNICATION_FAIL((byte) 0x0e,	"A previous communication attempt failed"),
	REMOVE_SUBSECTION((byte) 0x0f, "Remove A Subsection"),
	NEW_DOCUMENT((byte) 0x10, "New Document"),
	REMOVE_DOCUMENT((byte) 0x11, "Remove Document"),
	LOG_OUT((byte)0x12, "Log Out"),
	SUBSECTION_FLOPPED((byte) 0x13, "Flop subsections"),
	SUBSECTION_SPLIT((byte) 0x14, "Split SubSections"),
	SUBSECTION_COMBINE((byte) 0x15, "Combint SubSections"),
	UPDATE_SUBSECTION_INSERT((byte) 0x16, "Insert into SubSection"),
	UPDATE_SUBSECTION_REMOVE((byte) 0x17, "Remove from SubSection"),
	ALL_PERSISTED_ROOMS((byte)0x18, "All persisted rooms"),
	PERSISTED_ROOM((byte)0x19, "Persisted Room"),
	SAVE_ROOM((byte)0x1a, "Save Room"),
	USER_AUTHENTICATE((byte) 0x1b, "Authenticate User"),
	USER_CREATE((byte) 0x1c, "Create User"),
	USER_DELETE((byte) 0x1d, "Delete User");

	private byte code;
	private String name;

	private MessageType(byte code, String name) {
		this.code = code;
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
	/**
	 * Gets the byte code for this message type
	 * 
	 * @return The byte code for this message
	 **/
	public byte getCode() {
		return code;
	}

	/**
	 * Given a byte code, returns the MessageType associated with that code
	 * 
	 * @param code The byte code
	 * 
	 * @return MessageType The MessageType corresponding to that byte code
	 **/
	public static MessageType getMessageTypeByCode(byte code) {
		switch (code) {
		case 0x0:
			return UPDATE_ALL_SUBSECTIONS;
		case 0x1:
			return UPDATE_SUBSECTION;
		case 0x2:
			return SUBSECTION_LOCKED;
		case 0x3:
			return SUBSECTION_UNLOCKED;
		case 0x4:
			return NEW_SUBSECTION;
		case 0x5:
			return NEW_MESSAGE;
		case 0x6:
			return NEW_PRIVATE_MESSAGE;
		case 0x7:
			return NEW_CLIENT;
		case 0x8:
			return MEMBER_JOIN_ROOM;
		case 0x9:
			return MEMBER_LEAVE_ROOM;
		case 0xa:
			return MEMBERS_IN_ROOM;
		case 0xb:
			return CHANGE_USER_PRIV;
		case 0xc:
			return GET_ROOM_LIST;
		case 0xd:
			return NEW_COLAB_ROOM;
		case 0xe:
			return COMMUNICATION_FAIL;
		case 0xf:
			return REMOVE_SUBSECTION;
		case 0x10:
			return NEW_DOCUMENT;
		case 0x11:
			return REMOVE_DOCUMENT;
		case 0x12:
			return LOG_OUT;
		case 0x13:
			return SUBSECTION_FLOPPED;
		case 0x14:
			return SUBSECTION_SPLIT;
		case 0x15:
			return SUBSECTION_COMBINE;
		case 0x16:
			return UPDATE_SUBSECTION_INSERT;
		case 0x17:
			return UPDATE_SUBSECTION_REMOVE;
		case 0x18:
			return ALL_PERSISTED_ROOMS;
		case 0x19:
			return PERSISTED_ROOM;
		case 0x1a:
			return SAVE_ROOM;
		case 0x1b:
			return USER_AUTHENTICATE;
		case 0x1c:
			return USER_CREATE;
		case 0x1d:
			return USER_DELETE;
		default:
			return null;
		}
	}
}
