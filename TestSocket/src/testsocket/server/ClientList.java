package testsocket.server;

import java.util.ArrayList;
import java.util.List;

public class ClientList {
	private List<ClientNode> nodes=new ArrayList<ClientNode>();
	public List<ClientNode> getNodes() {
		return nodes;
	}
	public ClientNode getNodeById(String id){
		for(ClientNode cn:nodes){
			if(cn.getId().equals(id)){
				return cn;
			}
		}
		return null;
	}
	public void addNode(ServerThread thread,String id){
		ClientNode node=new ClientNode();
		node.setId(id);
		node.setThread(thread);
		nodes.add(node);
	}
	
}
