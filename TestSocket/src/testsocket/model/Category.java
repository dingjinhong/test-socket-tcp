package testsocket.model;

import java.util.ArrayList;
import java.util.List;

public class Category {
	List<User> members=new ArrayList<User>();
	String key;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public List<User> getMembers() {
		return members;
	}
}
