package testsocket.dialog;

import java.util.List;
import java.util.Random;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import testsocket.client.ClientFactory;
import testsocket.client.ClientReadThread.MessageListener;
import testsocket.client.ClientRegister;
import testsocket.client.IClient;
import testsocket.model.Category;
import testsocket.model.ClientRequest;
import testsocket.model.TranObject;
import testsocket.model.TranObjectType;
import testsocket.model.User;

public class LoginDialog extends Dialog {
	int id;
	Text name_text;
	Text log_text;
	Gson gson=new Gson();
	boolean state;
	IClient c;
	MessageListener messageListener;
	public LoginDialog(Shell parentShell,boolean useNIO) {
		super(parentShell);
		c=ClientFactory.getClient(useNIO);
		ClientRegister.getInstance().setCurrentClient(c);
		state=c.start();
		
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// TODO Auto-generated method stub
		Composite composite=(Composite) super.createDialogArea(parent);
		Composite com=new Composite(composite,SWT.NONE);
		com.setLayout(new GridLayout(3,false));
		Label id_label=new Label(com,SWT.NONE);
		id_label.setText("id:");
		Text id_text=new Text(com,SWT.NONE);
		GridData  gd=new GridData();
		gd.widthHint=150;
		id_text.setLayoutData(gd);
		id_text.setEditable(false);
		id=getRandomNum(9999,1);
		id_text.setText(id+"");
		Button refresh=new Button(com,SWT.PUSH);
		refresh.setText("refresh");
		refresh.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				id=getRandomNum(9999,1);
				id_text.setText(id+"");
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		Label name_label=new Label(com,SWT.NONE);
		name_label.setText("name:");
		name_text=new Text(com,SWT.NONE);
		gd=new GridData();
		gd.horizontalSpan=2;
		gd.horizontalAlignment=SWT.FILL;
		name_text.setLayoutData(gd);
		log_text=new Text(com,SWT.NONE);
		log_text.setEditable(false);
		gd=new GridData();
		gd.horizontalSpan=3;
		gd.horizontalAlignment=SWT.FILL;
		log_text.setLayoutData(gd);
		return composite;
	}
	private int getRandomNum(int max ,int min){
		Random random=new Random();
		int s = random.nextInt(max)%(max-min+1) + min;
		return s;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// TODO Auto-generated method stub
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void okPressed() {
		// TODO Auto-generated method stub
		
		if(state){
		User user=new User();
		user.setId(id);
		user.setName(name_text.getText());
		
		TranObject<User> loginObject = new TranObject<User>(  
                  TranObjectType.CLIENT_TO_SEVER);  
		//loginObject.setObject(user);
		loginObject.setObjStr(gson.toJson(user));
		loginObject.setRequest(ClientRequest.LOGIN);
		messageListener=new MessageListener() {
			
			@Override
			public void Message(TranObject msg) {
				// TODO Auto-generated method stub
				Display.getDefault().syncExec(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(msg.getObjStr()!=null){
							List<Category> categorys = gson.fromJson(msg.getObjStr(), new TypeToken<List<Category>>(){}.getType());
							user.getCategorys().addAll(categorys);
							c.setUser(user);
							LoginDialog.super.okPressed();
						}else{
							log_text.setText("login failed");
						}
					}
				});
				
				
			}
		};
		c.getReadThread().addMessageListener(messageListener);
		c.getWriteThread().setMsg(loginObject);
		System.out.println("hahaha");
		}
//		try {
//			cm.getOut().writeUTF(id);
//			String s=cm.getIn().readUTF();
//			System.out.println(s);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	
	}
	@Override
	public boolean close() {
		// TODO Auto-generated method stub
		boolean closed= super.close();
		if(closed){
			if(messageListener!=null){
				c.getReadThread().removeMessageListener(messageListener);
			}
		}
		return closed;
	}
	
}
