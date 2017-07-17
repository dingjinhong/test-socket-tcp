package testsocket.nio.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import testsocket.model.Category;
import testsocket.model.SeverRespone;
import testsocket.model.TextMessage;
import testsocket.model.TranObject;
import testsocket.model.TranObjectType;
import testsocket.model.User;
import testsocket.model.UserDao;
import testsocket.model.UserDaoFactory;
import testsocket.server.OutputThread;
import testsocket.server.utils.ServerUtils;
import testsocket.server.utils.UserChannel;

public class ReadThread implements Runnable {
	private SelectionKey key;
	private Gson gson=new Gson();
	private ByteBuffer rBuffer = ByteBuffer.allocate(1024);
	private ByteBuffer sBuffer = ByteBuffer.allocate(1024);  
	 private Charset cs = Charset.forName("utf-8");  
	public ReadThread(SelectionKey key) {
		super();
		this.key = key;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		SocketChannel client = (SocketChannel)key.channel();
		rBuffer.clear();
		try{
			int count=client.read(rBuffer);
			System.out.println("count:"+count);
			if(count>0){
				//rBuffer.clear();
				String readstr=new String(rBuffer.array(), 0, count);
				rBuffer.flip();
		          TranObject read_tranObject = gson.fromJson(readstr, new TypeToken<TranObject>(){}.getType());
		            UserDao dao = UserDaoFactory.getInstance();// 通过dao模式管理后台  
		            if (read_tranObject != null) {
		            	if(read_tranObject.getType()==TranObjectType.CLIENT_TO_SEVER){
		                //TranObject read_tranObject = (TranObject) readObject;// 转换成传输对象  
		                switch (read_tranObject.getRequest()) {  
		                case REGISTER:// 如果用户是注册  
		                	String registerUserStr=read_tranObject.getObjStr();
		                    User registerUser = gson.fromJson(registerUserStr, new TypeToken<User>(){}.getType());
		                    int registerResult = dao.register(registerUser);  
		                    System.out.println(" 新用户注册:"  
		                            + registerResult);  
		                    // 给用户回复消息  
		                    TranObject<User> register2TranObject = new TranObject<User>(  
		                            TranObjectType.SEVER_TO_CLIENT);  
		                    User register2user = new User();  
		                    register2user.setId(registerResult);  
		                  //  register2TranObject.setObject(register2user);  
		                    register2TranObject.setObjStr(gson.toJson(register2user));
		                    register2TranObject.setRespone(SeverRespone.REGISTER_SUCCESS);
		                	 writeBuffer(client, register2TranObject);
		                    break;  
		                case LOGIN:  
		                	String loginUserStr=read_tranObject.getObjStr();
		                    User loginUser = gson.fromJson(loginUserStr, new TypeToken<User>(){}.getType()); 
		                    boolean login = dao.login(loginUser);  
		                    TranObject<List<Category>> login2Object = new TranObject<List<Category>>(  
		                            TranObjectType.SEVER_TO_CLIENT);  
		                    if(login){
		                    	List<Category> list = dao.getCategroys(loginUser);
			                    if (!list.isEmpty()) {// 如果登录成功  
			                        TranObject<User> onObject = new TranObject<User>(  
			                                TranObjectType.SEVER_TO_CLIENT);  
			                        User login2User = new User();  
			                        login2User.setId(loginUser.getId());
			                        login2User.setName(loginUser.getName());
			                       // onObject.setObject(login2User);  
			                        onObject.setObjStr(gson.toJson(login2User));
			                        onObject.setRespone(SeverRespone.USER_LOGIN);
			                        List<Integer> friends=ServerUtils.getAllOnlineFriendId(loginUser);
			                        for(int id:friends){
			                        	SocketChannel channel = UserChannel.getInstance().getChannel(id);
			                        	writeBuffer(channel, onObject);// 广播一下用户上线
			                        	
			                        }
			                    } 
			                    UserChannel.getInstance().addChannel(loginUser.getId(), client); // 先广播，再把对应用户id的写线程存入map中，以便转发消息时调用 
			                    //login2Object.setObject(list);// 把好友列表加入回复的对象中 
			                    login2Object.setObjStr(gson.toJson(list));
			                    System.out.println(" 用户："  
			                            + loginUser.getId() + " 上线了"); 
		                    }else{
		                    	//login2Object.setObject(null);
		                    	login2Object.setObjStr(null);
		                    }
		                    login2Object.setRespone(SeverRespone.USER_LOGIN);
		                    writeBuffer(client, login2Object);// 同时把登录信息回复给用户
		                    break;  
		                case LOGOUT:// 如果是退出，更新数据库在线状态，同时群发告诉所有在线用户  
		                   	String logoutUserStr=read_tranObject.getObjStr();
		                    User logoutUser = gson.fromJson(logoutUserStr, new TypeToken<User>(){}.getType()); 
		                    int offId = logoutUser.getId();  
		                    System.out  
		                            .println(" 用户：" + offId + " 下线了");  
		                    dao.logout(offId);  
		                   // isStart = false;// 结束自己的读循环  
		                    UserChannel.getInstance().removeChannel(offId);//map.remove(offId);// 从缓存的线程中移除  
		                    //out.setMessage(null);// 先要设置一个空消息去唤醒写线程  
		                    //out.setStart(false);// 再结束写线程循环  
		      
		                    TranObject<User> offObject = new TranObject<User>(  
		                            TranObjectType.SEVER_TO_CLIENT);  
		                    User logout2User = new User();  
		                    logout2User.setId(logoutUser.getId());  
		                   // offObject.setObject(logout2User);
		                    offObject.setRespone(SeverRespone.USER_LOGOUT);
		                    offObject.setObjStr(gson.toJson(logout2User));
		                    List<Integer> friends=ServerUtils.getAllOnlineFriendId(logoutUser);
	                        for(int id:friends){
	                        	SocketChannel channel = UserChannel.getInstance().getChannel(id);
	                        	  writeBuffer(channel, offObject);// 广播用户下线消息
	                        }
		                    client.close();
		                    //key.selector().close();
		                    break;  
		                case MESSAGE:// 如果是转发消息（可添加群发）  
		                    // 获取消息中要转发的对象id，然后获取缓存的该对象的写线程  
		                    int id2 = read_tranObject.getToUser();  
		                    SocketChannel toOut =  UserChannel.getInstance().getChannel(id2);  
		                    if (toOut != null) {// 如果用户在线 
		                    	read_tranObject.setType(TranObjectType.SEVER_TO_CLIENT);
		                    	read_tranObject.setRequest(null);
		                    	read_tranObject.setRespone(SeverRespone.USER_MESSAGE);
		                        writeBuffer(toOut, read_tranObject);
		                    } else {// 如果为空，说明用户已经下线,回复用户  
		                        TextMessage text = new TextMessage(0);  
		                        text.setMessage("亲！对方不在线哦，您的消息将暂时保存在服务器");  
		                        TranObject<TextMessage> offText = new TranObject<TextMessage>(  
		                                TranObjectType.SEVER_TO_CLIENT);  
		                       // offText.setObject(text);
		                        offText.setObjStr(gson.toJson(text));
		                        offText.setFromUser(0);  
		                        offText.setRespone(SeverRespone.USER_NOT_ONLINE);
		                        writeBuffer(client, offText);
		                    }  
		                    break;  
		                case REFRESH:  
		                    List<User> refreshList = dao.refresh(read_tranObject  
		                            .getFromUser());  
		                    TranObject<List<User>> refreshO = new TranObject<List<User>>(  
		                            TranObjectType.SEVER_TO_CLIENT);  
		                    refreshO.setRespone(SeverRespone.USER_REFRESH);
		                   // refreshO.setObject(refreshList);  
		                    refreshO.setObjStr(gson.toJson(refreshList));
		                    writeBuffer(client, refreshO);
		                    break; 
		                case SEARCH_FRIEND:
		                	User searchUser=dao.getUser(read_tranObject.getToUser());
		                	TranObject<User> searchO = new TranObject<User>(  
		                            TranObjectType.SEVER_TO_CLIENT);
		                	//searchO.setObject(searchUser);
		                	searchO.setRespone(SeverRespone.USER_SEARCH_FRIEND_RESOULT);
		                	if(searchUser!=null){
		                		searchO.setObjStr(gson.toJson(searchUser));
		                	}else{
		                		searchO.setObjStr(null);
		                	}
		                    writeBuffer(client, searchO);
		                	break;
		                case ADD_FRIEND:
		                	User toUser=dao.getUser(read_tranObject.getToUser());
		                	User fromUser=dao.getUser(read_tranObject.getFromUser());
		                	if(toUser!=null){
		                		TranObject<User> fromAddO = new TranObject<User>(  
			                            TranObjectType.SEVER_TO_CLIENT);
		                	//	fromAddO.setObject(fromUser);
		                		fromAddO.setRespone(SeverRespone.USER_ADD_FRIEND);
		                		fromAddO.setObjStr(gson.toJson(fromUser));
		                        writeBuffer(UserChannel.getInstance().getChannel(toUser.getId()), fromAddO);
		                		//map.getById(toUser.getId()).setMessage(fromAddO);
		                		
		                	}else{
		                		  TextMessage text = new TextMessage(0);  
			                        text.setMessage("亲！对方不在线哦");  
			                        TranObject<TextMessage> offText = new TranObject<TextMessage>(  
			                                TranObjectType.SEVER_TO_CLIENT);  
			                     //   offText.setObject(text);
			                        offText.setObjStr(gson.toJson(text));
			                        offText.setRespone(SeverRespone.USER_NOT_ONLINE);
			                        offText.setFromUser(0);
			                        writeBuffer(client, offText);
		                	}

		                	break;
		                case PROMISE_ADD_FRIEND:
		                	User promiseAddToUser=dao.getUser(read_tranObject.getToUser());
		                	User promiseAddFromUser=dao.getUser(read_tranObject.getFromUser());
		                	if(promiseAddToUser!=null){
		                		TranObject<User> fromAddO = new TranObject<User>(  
			                            TranObjectType.SEVER_TO_CLIENT);
		                		//fromAddO.setObject(promiseAddFromUser);
		                		fromAddO.setObjStr(gson.toJson(promiseAddFromUser));
		                		fromAddO.setRespone(SeverRespone.USER_PROMISE_ADD_FRIEND);
		                		
		                		 writeBuffer(UserChannel.getInstance().getChannel(promiseAddToUser.getId()), fromAddO);
		                         writeBuffer(client, fromAddO);
		                   
		                	}else{
		                		  TextMessage text = new TextMessage(0);  
			                        text.setMessage("亲！对方已经不在线哦");  
			                        TranObject<TextMessage> offText = new TranObject<TextMessage>(  
			                                TranObjectType.SEVER_TO_CLIENT);  
			                        //offText.setObject(text);
			                        offText.setObjStr(gson.toJson(text));
			                        offText.setFromUser(0);
			                        offText.setRespone(SeverRespone.USER_NOT_ONLINE);
			                        writeBuffer(client, offText);
		                	}
		                	break;
		                default:  
		                    break;  
		                }  
		            }
		            }
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(client.isOpen()){
				key.interestOps(key.interestOps()|SelectionKey.OP_READ);
				key.selector().wakeup();
			}
		}
		
	}
	private void writeBuffer(SocketChannel channel,Object obj) throws IOException{
		 String objStr=gson.toJson(obj);
		 sBuffer.clear();  
         sBuffer.put(objStr.getBytes(cs));  
         sBuffer.flip();
         channel.write(sBuffer);  
	}

}
