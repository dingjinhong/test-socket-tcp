package testsocket.server.utils;

import java.util.ArrayList;
import java.util.List;

import testsocket.model.Category;
import testsocket.model.User;
import testsocket.model.UserDao;
import testsocket.model.UserDaoFactory;

public class ServerUtils {
	public static List<Integer> getAllFriendId(User user){
		List<Integer> ids=new ArrayList<Integer>();
		for(Category cate:user.getCategorys()){
			for(User u:cate.getMembers()){
				ids.add(u.getId());
			}
		}
		return ids;
	}
	public static List<Integer> getAllOnlineFriendId(User user){
		UserDao userDao=UserDaoFactory.getInstance();
		List<Integer> ids=new ArrayList<Integer>();
		for(Category cate:user.getCategorys()){
			for(User u:cate.getMembers()){
				if(userDao.isOnline(u)){
					ids.add(u.getId());
				}
			}
		}
		return ids;
	}
}
