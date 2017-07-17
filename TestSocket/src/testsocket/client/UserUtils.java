package testsocket.client;

import java.util.List;

import org.eclipse.ui.PlatformUI;

import javafx.application.Platform;

import testsocket.ViewPart;
import testsocket.model.Category;
import testsocket.model.TextMessage;
import testsocket.model.User;



public class UserUtils {
	public static String getUserName(int id){
		IClient c=ClientRegister.getInstance().getCurrentClient();
		if(c.getUser().getId()==id){
			return c.getUser().getName();
		}
		for(Category cate:c.getUser().getCategorys()){
			for(User u:cate.getMembers()){
				if(u.getId()==id){
					return u.getName();
				}
			}
		}
		return null;
	}
	public static boolean hasNewMessageToRead(int id){
		ChatRecordDao cr=ChatRecordDao.getInstance();
		List<TextMessage> list = cr.getChat_record().get(id);
		if(list==null){
			return false;
		}else{
			for(TextMessage tm:list){
				if(!tm.isReaded()){
					return true;
				}
			}
			return false;
		}
	}
	public static void addToCategory(String category_name,User user){
		Category new_category=null;
		User me=ClientRegister.getInstance().getCurrentClient().getUser();
		for(Category category:me.getCategorys()){
			if(category.getKey().equals(category_name)){
				new_category=category;
				break;
			}
		}
		if(new_category==null){
			new_category=new Category();
			new_category.setKey(category_name);
			me.getCategorys().add(new_category);
		}
		if(!new_category.getMembers().contains(user)){
			new_category.getMembers().add(user);
		}
		ViewPart vp=(ViewPart) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("TestSocket.view");
		vp.refreshTreeViewer();
	}
}
