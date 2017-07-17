package testsocket.dialog;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.IntegerValidator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import testsocket.client.ClientReadThread.MessageListener;
import testsocket.client.ClientRegister;
import testsocket.client.IClient;
import testsocket.model.Category;
import testsocket.model.ClientRequest;
import testsocket.model.TranObject;
import testsocket.model.TranObjectType;
import testsocket.model.User;

public class SearchFriendDialog extends Dialog {
	TableViewer tv;
	IClient c;
	//Combo combo;
	User user;
	Text text;
	Gson gson=new Gson();
	MessageListener messageListener;
	public SearchFriendDialog(Shell parentShell) {
		super(parentShell);
		c=ClientRegister.getInstance().getCurrentClient();
		user=c.getUser();
		messageListener=new MessageListener() {
			
			@Override
			public void Message(TranObject msg) {
				// TODO Auto-generated method stub
				if(msg.getObjStr()!=null){
					String objStr=msg.getObjStr();
					User addUser=gson.fromJson(objStr,new TypeToken<User>(){}.getType());
					boolean hasUser=false;
					for(Category cate:user.getCategorys()){
						for(User u:cate.getMembers()){
							if(u.getId()==addUser.getId()){
								hasUser=true;
								break;
							}
						}
					}
					if(hasUser){
						Display.getDefault().syncExec(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								List<User> l=new ArrayList<User>();
								tv.setInput(l);
								MessageDialog.openWarning(Display.getDefault().getActiveShell(), "wain", "you already has this friend");
							}
						});
					}else{
						Display.getDefault().syncExec(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								List<User> l=new ArrayList<User>();
								l.add(addUser);
								tv.setInput(l);
							}
						});	
					}
					
				}else{
					Display.getDefault().syncExec(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							List<User> l=new ArrayList<User>();
							tv.setInput(l);
							MessageDialog.openWarning(Display.getDefault().getActiveShell(), "wain", "Without this user ");
						}
					});
				}
				
			}
		};
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void configureShell(Shell newShell) {
		// TODO Auto-generated method stub
		newShell.setText(user.getName());
		super.configureShell(newShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// TODO Auto-generated method stub
		Composite composite= (Composite) super.createDialogArea(parent);
		Composite com=new Composite(composite,SWT.NONE);
		com.setLayout(new GridLayout(3,false));
		Label label=new Label(com,SWT.NONE);
		label.setText("id:");
		text=new Text(com,SWT.NONE);
		text.addVerifyListener(new VerifyListener() {
			
			@Override
			public void verifyText(VerifyEvent e) {
				// TODO Auto-generated method stub
				Text t=(Text) e.widget;
				String s=e.text;
				String ct=t.getText();
				String temps=StringUtils.substring(ct, 0,e.start)+s+StringUtils.substring(ct,e.end);
				if(!IntegerValidator.getInstance().isValid(temps)){
					e.doit=false;
				}
			}
		});
		GridData gd=new GridData();
		gd.widthHint=150;
		text.setLayoutData(gd);
		Button search_btn=new Button(com,SWT.NONE);
		search_btn.setText("search");
//		combo=new Combo(com, SWT.NONE);
//		combo.setItems(new String[]{"Friends","Family"});
//		combo.select(0);
		Composite table_com=new Composite(parent,SWT.NONE);
		gd=new GridData(GridData.FILL_BOTH);
		table_com.setLayoutData(gd);
		table_com.setLayout(new FillLayout());
		tv=new TableViewer(table_com,SWT.SINGLE|SWT.FULL_SELECTION);
		TableColumn id_tableColumn = new TableColumn(tv.getTable(), SWT.LEAD);
		id_tableColumn.setText("id");
		id_tableColumn.setWidth(100);
		TableColumn name_tableColumn = new TableColumn(tv.getTable(), SWT.LEAD);
		name_tableColumn.setText("name");
		name_tableColumn.setWidth(100);
		
		tv.setContentProvider(new TableContentPrvider());
		tv.setLabelProvider(new LabelProvider());
		search_btn.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				String search_id=text.getText();
				if(!StringUtils.isBlank(search_id)){
					c.getReadThread().addMessageListener(messageListener);
					TranObject to=new TranObject(TranObjectType.CLIENT_TO_SEVER);
					to.setFromUser(user.getId());
					to.setToUser(Integer.parseInt(text.getText()));
					to.setRequest(ClientRequest.SEARCH_FRIEND);
					c.getWriteThread().setMsg(to);
				}
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});		
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
	@Override
	protected void okPressed() {
		// TODO Auto-generated method stub
		
		for(TableItem item:tv.getTable().getItems()){
			User friend=(User) item.getData();
			TranObject<User> msg=new TranObject<User>(TranObjectType.CLIENT_TO_SEVER);
			msg.setFromUser(user.getId());
			msg.setToUser(friend.getId());
			//msg.setObject(user);
			msg.setObjStr(gson.toJson(user));
			msg.setRequest(ClientRequest.ADD_FRIEND);
			c.getWriteThread().setMsg(msg);
//			String category_name=combo.getText();
//			Category new_category=null;
//			for(Category category:user.getCategorys()){
//				if(category.getKey().equals(category_name)){
//					new_category=category;
//					break;
//				}
//			}
//			if(new_category==null){
//				new_category=new Category();
//				new_category.setKey(category_name);
//				user.getCategorys().add(new_category);
//			}
//			new_category.getMembers().add(friend);
			
		}
		
		super.okPressed();
	}

	@Override
	public boolean close() {
		// TODO Auto-generated method stub
		boolean closed= super.close();
		if(closed){
			c.getReadThread().removeMessageListener(messageListener);
		}
		return closed;
	}
	
}
