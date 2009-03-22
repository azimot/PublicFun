package edu.cs319.dataobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cs319.server.CoLabPrivledgeLevel;

/**
 * Class defining a room for people to meet.
 * 
 * @author The Squirrels
 * 
 */
public class CoLabRoom {

	private String roomname;
	private Map<String, CoLabRoomMember> members;
	private byte[] password;

	/**
	 * Constructs a new CoLabRoom with the supplied name.
	 * 
	 * @param roomname
	 */
	public CoLabRoom(String roomname, CoLabRoomMember theAdmin) {
		this(roomname, theAdmin, null);
	}

	/**
	 * Constructs a new CoLabRoom with the supplied name and password.
	 * 
	 * @param roomname
	 */
	public CoLabRoom(String roomname, CoLabRoomMember theAdmin, byte[] password) {
		this.roomname = roomname;
		theAdmin.setPrivLevel(CoLabPrivledgeLevel.ADMIN);
		members = Collections.synchronizedMap(new HashMap<String, CoLabRoomMember>());
		this.password = password;
	}

	/**
	 * Adds a new member to the CoLabRoom
	 * 
	 * @param member
	 *            the member-to-be-added's name
	 * @return whether or not the member already exists in a room.
	 */
	public boolean addMember(String member) {
		members.put(member, new CoLabRoomMember(member));
		return true;
	}

	/**
	 * checks if a member is in a colab room
	 * 
	 * @param name
	 *            the name to check
	 * @return whether or not the member is in a room
	 */
	public boolean containsMemberByName(String name) {
		return members.keySet().contains(name);
	}

	public CoLabRoomMember getMemberByName(String name) {
		return members.get(name);
	}

	public byte[] password() {
		return password;
	}

	public String roomName() {
		return roomname;
	}

	@Override
	public String toString() {
		return roomName();
	}
}