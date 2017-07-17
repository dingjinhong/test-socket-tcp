package testsocket.model;

import java.io.Serializable;

public class TranObject<T> implements Serializable {
	private TranObjectType type;
	private int fromUser;
	private int toUser;
	private int groupId;
	private T obj;
	private String objStr;
	private ClientRequest request;
	private SeverRespone respone;
	
	public TranObject(TranObjectType type) {
		super();
		this.type = type;
	}
	public ClientRequest getRequest() {
		return request;
	}
	public void setRequest(ClientRequest request) {
		this.request = request;
	}
	public SeverRespone getRespone() {
		return respone;
	}
	public void setRespone(SeverRespone respone) {
		this.respone = respone;
	}
	public TranObjectType getType() {
		return type;
	}
	public void setType(TranObjectType type) {
		this.type = type;
	}
	public int getFromUser() {
		return fromUser;
	}
	public void setFromUser(int fromUser) {
		this.fromUser = fromUser;
	}
	public int getToUser() {
		return toUser;
	}
	public void setToUser(int toUser) {
		this.toUser = toUser;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public T getObj() {
		return obj;
	}
	public void setObj(T obj) {
		this.obj = obj;
	}
	public String getObjStr() {
		return objStr;
	}
	public void setObjStr(String objStr) {
		this.objStr = objStr;
	}
	
}
