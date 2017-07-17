package testsocket.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import testsocket.client.ClientReadThread.MessageListener;
import testsocket.client.ClientRegister;
import testsocket.client.IClient;
import testsocket.client.UserUtils;
import testsocket.model.ClientRequest;
import testsocket.model.SeverRespone;
import testsocket.model.TextMessage;
import testsocket.model.TranObject;
import testsocket.model.TranObjectType;
import testsocket.model.User;

public class ConfirmAddFriendsDialog extends Dialog {
	User user;
	IClient c;
	User me;
	Gson gson=new Gson();
	Combo combo;
	MessageListener listener;
	public ConfirmAddFriendsDialog(Shell parentShell,User user) {
		super(parentShell);
		this.user=user;
		c=ClientRegister.getInstance().getCurrentClient();
		me=c.getUser();
		listener=new MessageListener() {
			
			@Override
			public void Message(TranObject msg) {
				// TODO Auto-generated method stub
				if(msg.getType()==TranObjectType.SEVER_TO_CLIENT&&msg.getRespone()==SeverRespone.USER_NOT_ONLINE){
					String objStr=msg.getObjStr();
					TextMessage textMessage=gson.fromJson(objStr, new TypeToken<TextMessage>(){}.getType());
					//if(!friendsInChat.contains(msg.getFromUser())){
						if(textMessage.getAuthor_id()==0){
							Display.getDefault().syncExec(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									MessageDialog.openWarning(Display.getDefault().getActiveShell(), "wain", textMessage.getMessage());
								}
							});
						}else{
							Display.getDefault().syncExec(new Runnable() {
								@Override
								public void run() {
								String category_name=combo.getText();
								UserUtils.addToCategory(category_name, user);
								}
							});
						}
				}else{
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							String category_name=combo.getText();
							UserUtils.addToCategory(category_name, user);
						}
					});
				}
				
					
					
						// TODO Auto-generated method stub
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						close();
					}
				});
				
			
			
				
			}
		};
		c.getReadThread().addMessageListener(listener);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void configureShell(Shell newShell) {
		// TODO Auto-generated method stub
		newShell.setText(me.getName());
		super.configureShell(newShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// TODO Auto-generated method stub
		Composite composite= (Composite) super.createDialogArea(parent);
		combo=new Combo(composite, SWT.NONE);
		combo.setItems(new String[]{"Friends","Family"});
		combo.select(0);
		Composite table_com=new Composite(composite,SWT.NONE);
		GridData gd=new GridData(GridData.FILL_BOTH);
		table_com.setLayoutData(gd);
		table_com.setLayout(new FillLayout());
		TableViewer tv=new TableViewer(table_com,SWT.SINGLE|SWT.FULL_SELECTION);
		TableColumn id_tableColumn = new TableColumn(tv.getTable(), SWT.LEAD);
		id_tableColumn.setText("id");
		id_tableColumn.setWidth(100);
		TableColumn name_tableColumn = new TableColumn(tv.getTable(), SWT.LEAD);
		name_tableColumn.setText("name");
		name_tableColumn.setWidth(100);
		
		tv.setContentProvider(new TableContentPrvider());
		tv.setLabelProvider(new LabelProvider());
		List<User> l=new ArrayList<User>();
		l.add(user);
		tv.setInput(l);
		return composite;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// TODO Auto-generated method stub
		createButton(parent, IDialogConstants.OK_ID, "Add",
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}
	
	@Override
	protected void okPressed() {
		// TODO Auto-generated method stub
		TranObject<User> msg=new TranObject<User>(TranObjectType.CLIENT_TO_SEVER);
		msg.setFromUser(me.getId());
		msg.setToUser(user.getId());
		//msg.setObject(me);
		msg.setObjStr(gson.toJson(me));
		msg.setRequest(ClientRequest.PROMISE_ADD_FRIEND);
		c.getWriteThread().setMsg(msg);
		String category_name=combo.getText();
		UserUtils.addToCategory(category_name, user);
		//super.okPressed();
	}

	class TableContentPrvider implements IStructuredContentProvider{

		@Override
		public Object[] getElements(Object inputElement) {
			// TODO Auto-generated method stub
			try{
				return ((List)inputElement).toArray();
			}catch(Exception e){
				return null;
			}
		}
		
	}
	class LabelProvider extends StyledCellLabelProvider{

		@Override
		public void update(ViewerCell cell) {
			// TODO Auto-generated method stub
			User user=(User) cell.getElement();
			if(cell.getColumnIndex()==0){
				cell.setText(user.getId()+"");
			}else if(cell.getColumnIndex()==1){
				cell.setText(user.getName());
			}
			super.update(cell);
		}
		
	}
	public boolean close() {
		// TODO Auto-generated method stub
		boolean closed= super.close();
		if(closed){
			c.getReadThread().removeMessageListener(listener);
		}
		return closed;
	}
}
