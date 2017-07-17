package testsocket.model;

import java.util.ArrayList;
import java.util.List;



public class UserDao {
	List<User> hasLoginList=new ArrayList<User>();
	List<User> allUsers=new ArrayList<User>();
	private static UserDao instance=new UserDao();
	private UserDao(){
		
	}
	public int register(User user){
		return 0;
	}
	public boolean isOnline(User user){
		return hasLoginList.contains(user.getId());
	}
	public boolean login(User user){
		for(User u:hasLoginList){
			if(u.getId()==user.getId()){
				return false;
			}
		}
		allUsers.add(user);
		hasLoginList.add(user);
		return true;
	}
	public List<Category> getCategroys(User user){
		return user.getCategorys();
	}
	public static UserDao getInstance(){
		return instance;
	}
	public void logout(int id){
		User deluser=null;
		for(User user:hasLoginList){
			if(user.getId()==id){
				deluser=user;
				break;
			}
		}
		if(deluser!=null){
			hasLoginList.remove(deluser);
		}
	}
	public List<User> refresh(int fromUser) {
		// TODO Auto-generated method stub
		return null;
	}
	public User getUser(int userId){
		for(User u:allUsers){
			if(u.getId()==userId){
				return u;
			}
		}
		return null;
	}
}
