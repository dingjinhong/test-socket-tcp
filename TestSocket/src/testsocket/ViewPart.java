package testsocket;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import testsocket.client.ChatRecordDao;
import testsocket.client.ClientReadThread.MessageListener;
import testsocket.client.ClientRegister;
import testsocket.client.IClient;
import testsocket.client.UserUtils;
import testsocket.dialog.ChatWindow;
import testsocket.dialog.GroupFriendDialog;
import testsocket.dialog.Notifier;
import testsocket.model.Category;
import testsocket.model.SeverRespone;
import testsocket.model.TextMessage;
import testsocket.model.TranObject;
import testsocket.model.TranObjectType;
import testsocket.model.User;

public class ViewPart extends org.eclipse.ui.part.ViewPart {
	TreeViewer tv;
	IClient client;
	MessageListener messageListenr;
	ChatRecordDao cr;
	List<String> friendsInChat=new ArrayList<String>();
	Gson gson=new Gson();
	public ViewPart() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		client=ClientRegister.getInstance().getCurrentClient();
		cr=ChatRecordDao.getInstance();
		User user=client.getUser();
		Composite composite =new Composite(parent,SWT.NONE);
		GridLayout gl=new GridLayout();
		composite.setLayout(gl);
		Text text=new Text(composite,SWT.NONE);
		text.setEditable(false);
		text.setText(user.getId()+" : "+user.getName());
		GridData gd=new GridData();
		gd.grabExcessHorizontalSpace=true;
		gd.horizontalAlignment=SWT.FILL;
		text.setLayoutData(gd);
		tv=new TreeViewer(composite,SWT.SINGLE|SWT.FULL_SELECTION);
		gd=new GridData(GridData.FILL_BOTH);
		tv.getTree().setLayoutData(gd);
		tv.setContentProvider(new TreeContentProvider());
		tv.setLabelProvider(new TreeLabelProvider());
		//Category category=new Category();
//		category.setKey("Friends");
//		List<Category> categorys=new ArrayList<Category>();
//		categorys.add(category);
		
		tv.setInput(user.getCategorys());
		tv.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				// TODO Auto-generated method stub
				if(event.getSelection()!=null&&!event.getSelection().isEmpty()){
					StructuredSelection ss=(StructuredSelection) event.getSelection();
					Object obj=ss.getFirstElement();
					if(obj instanceof User){
						User friend=(User) obj;
						int id=friend.getId();
						friendsInChat.add(id+"");
						refreshTreeViewer();
						ChatWindow dialog=new ChatWindow(Display.getDefault().getActiveShell(), friend);
						dialog.open();
						friendsInChat.remove(id+"");
						
						
					}
				}
			}
		});
		messageListenr=new MessageListener() {
			
			@Override
			public void Message(TranObject msg) {
				// TODO Auto-generated method stub
				if(msg.getType()==TranObjectType.SEVER_TO_CLIENT){
				if(msg.getRespone()==SeverRespone.USER_MESSAGE){
					if(msg.getObjStr()!=null){
						String objStr=msg.getObjStr();
						TextMessage textMessage=gson.fromJson(objStr, new TypeToken<TextMessage>(){}.getType());
						//if(!friendsInChat.contains(msg.getFromUser())){
							if(textMessage.getAuthor_id()!=0){
								if(friendsInChat.contains(msg.getFromUser()+"")){
									textMessage.setReaded(true);
	//								if(new_message_map.get(msg.getFromUser())!=null){
	//									new_message_map.get(msg.getFromUser()).add(textMessage);
	//								}else{
	//									List<TextMessage> list=new ArrayList<TextMessage>();
	//									list.add(textMessage);
	//									new_message_map.put(msg.getFromUser(), list);
	//								}
								}
								cr.add(msg.getFromUser(),textMessage);
							}else{
								Display.getDefault().syncExec(new Runnable() {
									
									@Override
									public void run() {
										// TODO Auto-generated method stub
										MessageDialog.openWarning(Display.getDefault().getActiveShell(), "wain", textMessage.getMessage());
									}
								});
							}
						//}
					}
					Display.getDefault().syncExec(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							refreshTreeViewer();
						}
					});
					
				}else if(msg.getRespone()==SeverRespone.USER_ADD_FRIEND){
					String objStr=msg.getObjStr();
					
					User u=gson.fromJson(objStr, new TypeToken<User>(){}.getType());
					Display.getDefault().syncExec(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Notifier notifier=new Notifier(Display.getDefault(),"add friend", u,null);
							notifier.setDelayClose(-1);
							notifier.open();
						}
					});
				
					
				}else if(msg.getRespone()==SeverRespone.USER_PROMISE_ADD_FRIEND){
					String objStr=msg.getObjStr();
					User u=gson.fromJson(objStr, new TypeToken<User>(){}.getType());
					if(u.getId()!=user.getId()){
						Display.getDefault().syncExec(new Runnable() {
							
							@Override
							public void run() {
								GroupFriendDialog dialog=new GroupFriendDialog(Display.getDefault().getActiveShell(), u);
								dialog.open();
							}
						});
					}
			}
			}
		}
		};
		client.getReadThread().addMessageListener(messageListenr);
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	public void refreshTreeViewer(){
		tv.refresh();
		tv.expandAll();
	}
	class TreeLabelProvider extends LabelProvider{
		Image im;
		TreeLabelProvider(){
			im=getCheckedImage();
		}
		@Override
		public String getText(Object element) {
			// TODO Auto-generated method stub
			if(element instanceof Category){
				return ((Category)element).getKey();
			}else if(element instanceof User){
				User f=(User)element;
			
				return f.getName();
			}else{
				return super.getText(element);
			}
		}

		
		public Image getImage(Object element) {
			if(element instanceof User){
				User f=(User)element;
				if(UserUtils.hasNewMessageToRead(f.getId())){
					return im;
				}
			}
			return null;
			
		}
		private Image getCheckedImage() {
		    Image image = new Image(Display.getCurrent(), 17, 17);
		    GC gc = new GC(image);
		    gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
		    gc.fillRectangle(3, 3, 10, 10);
		    gc.drawRectangle(3, 3,9,9);
		    gc.dispose();
		    return image;
		}
		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			if(im!=null&&!im.isDisposed())
			im.dispose();
			super.dispose();
		}
		
	}
	class TreeContentProvider implements ITreeContentProvider{

		@Override
		public Object[] getElements(Object inputElement) {
			// TODO Auto-generated method stub
			if(inputElement instanceof List){
				return ((List)inputElement).toArray();
			}
			return null;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			// TODO Auto-generated method stub
			if(parentElement instanceof Category){
				return ((Category)parentElement).getMembers().toArray();
			}
			return null;
		}

		@Override
		public Object getParent(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			// TODO Auto-generated method stub
			if(getChildren(element)==null)
				return false;
			return getChildren(element).length>0;
		}
		
	}
	
}
