package testsocket.server.utils;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserChannel {
	private static UserChannel instance=new UserChannel();
	private Map<Integer,SocketChannel> users=new HashMap<Integer,SocketChannel>();
	public static UserChannel getInstance(){
		return instance;
	}
	private UserChannel(){
		
	}
	public void addChannel(int id,SocketChannel channel){
		users.put(id, channel);
		
	}
	public void removeChannel(int id){
		users.remove(id);
	}
	public SocketChannel getChannel(int id){
		return users.get(id);
	}
	public List<SocketChannel> getAll(){
		return (List<SocketChannel>) users.values();
	}
}
