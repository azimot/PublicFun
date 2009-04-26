package edu.cs319.database;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import edu.cs319.dataobjects.database.CS319User;

public class UsersUtil {

	/**
	 * Will return true if the username exists and the password
	 * matches for that user
	 * @param user
	 * @param pass
	 * @return
	 */
	public static boolean authenticateUser(String username, byte[] password){
		Session session = HibernateUtil.getSession();
		Criteria criteria = session.createCriteria(CS319User.class);
		
		criteria.add(Restrictions.eq("username", username));
		criteria.add(Restrictions.eq("password", password));
		
		CS319User dbUser = (CS319User) criteria.uniqueResult();
		
		if(dbUser == null){
			return false;
		}
		
		return true;
	}
	
	/**
	 * Will return true if the user has been succesfully created and the username
	 * did not exist. Will return false if the username already exists
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public static boolean createUser(String username, byte[] password){
		Session session = HibernateUtil.getSession();
		
		CS319User newUser = new CS319User();
		newUser.setUsername(username);
		newUser.setPassword(password);
		
		try{
			session.save(newUser);
		}
		catch(ConstraintViolationException e){
			return false;
		}
		
		session.flush();
		session.close();
		
		return true;		
	}
	
	public static boolean deleteUser(String username){
		Session session = HibernateUtil.getSession();
		Criteria criteria = session.createCriteria(CS319User.class);
		
		criteria.add(Restrictions.eq("username", username));
		
		CS319User user = (CS319User) criteria.uniqueResult();
		
		if(user != null){
			session.delete(user);
		}
		
		session.flush();
		session.close();
		
		return true;
	}
}
