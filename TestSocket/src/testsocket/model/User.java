package testsocket.model;

import java.util.ArrayList;
import java.util.List;

public class User {
	int id;
	String name;
	List<Category> categorys=new ArrayList<Category>();
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Category> getCategorys() {
		return categorys;
	}
	
}
