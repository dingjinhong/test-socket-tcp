package testsocket.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import testsocket.client.ChatRecordDao;
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



public class ChatWindow extends Dialog {
	User friend;
	Text input_text;
	StyledText display_text;
	IClient c;
	User me;
	Gson gson=new Gson();
	ChatRecordDao cr;
	MessageListener messageListener;
	public ChatWindow(Shell parentShell,User friend) {
		super(parentShell);
		this.friend=friend;
		c=ClientRegister.getInstance().getCurrentClient();
		me=c.getUser();
		cr=ChatRecordDao.getInstance();
		messageListener=new MessageListener(){

			@Override
			public void Message(TranObject msg) {
				// TODO Auto-generated method stub
				if(msg.getType()==TranObjectType.SEVER_TO_CLIENT&&msg.getRespone()==SeverRespone.USER_MESSAGE){
					if(msg.getObjStr()!=null){
						String objStr=msg.getObjStr();
						TextMessage textMessage=gson.fromJson(objStr, new TypeToken<TextMessage>(){}.getType());
						//cr.add(msg.getFromUser(),textMessage);
						Display.getDefault().syncExec(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								display_text.append(friend.getName()+" :\n"+textMessage.getMessage()+"\n");
							}
						});
						
					}
				}
			}
			
		};
		c.getReadThread().addMessageListener(messageListener );
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void configureShell(Shell newShell) {
		// TODO Auto-generated method stub
		super.configureShell(newShell);
		newShell.setText(friend.getId()+" : "+friend.getName());
	}
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(newShellStyle | SWT.RESIZE|SWT.SHELL_TRIM);
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		// TODO Auto-generated method stub
		Composite composite= (Composite) super.createDialogArea(parent);
		Composite com=new Composite(composite,SWT.NONE);
		com.setLayout(new GridLayout());
		com.setLayoutData(new GridData(GridData.FILL_BOTH));
		SashForm sashForm=new SashForm(com, SWT.VERTICAL);
		GridData gd=new GridData(GridData.FILL_BOTH);
		gd.heightHint=450;
		gd.widthHint=550;
		sashForm.setLayoutData(gd);
	
		display_text=new StyledText(sashForm,SWT.MULTI|SWT.V_SCROLL|SWT.WRAP|SWT.BORDER);
		display_text.setEditable(false);
		Map<Integer,List<TextMessage>> map=cr.getChat_record();
		List<TextMessage> record=map.get(friend.getId());
		if(record!=null){
			//for(TextMessage text:messages){
			List<TextMessage> hasReadedMessages=new ArrayList<TextMessage>();
			List<TextMessage> noReadedMessages=new ArrayList<TextMessage>();
			for(TextMessage tm:record){
				if(tm.isReaded()){
					hasReadedMessages.add(tm);
				}else{
					noReadedMessages.add(tm);
				}
			}
			//}
		
			if(!hasReadedMessages.isEmpty()){
				for(TextMessage tm:hasReadedMessages){
					display_text.append(UserUtils.getUserName(tm.getAuthor_id())+" :\n"+tm.getMessage()+"\n");
				}
				display_text.append("\n");
				CLabel label=new CLabel(display_text,SWT.NONE);
				label.setAlignment(SWT.CENTER);
				label.setText("以上是历史消息");
				label.setLocation(0,display_text.getSelection().y);
				display_text.addControlListener(new ControlListener() {
					
					@Override
					public void controlResized(ControlEvent e) {
						// TODO Auto-generated method stub
						
						label.setSize(label.computeSize(display_text.getSize().x, -1));
					}
					
					@Override
					public void controlMoved(ControlEvent e) {
						// TODO Auto-generated method stub
						
					}
				});
			}
			if(!noReadedMessages.isEmpty()){
				for(TextMessage tm:noReadedMessages){
					display_text.append(UserUtils.getUserName(tm.getAuthor_id())+" :\n"+tm.getMessage()+"\n");
				}
			}
		}
		
		input_text=new Text(sashForm,SWT.MULTI|SWT.V_SCROLL|SWT.WRAP|SWT.BORDER);
		sashForm.setWeights(new int[]{70,30});
		return composite;
	}
	@Override
	protected void okPressed() {
		// TODO Auto-generated method stub
		String message=input_text.getText();
		if(!StringUtils.isBlank(message)){
			display_text.append(me.getName()+" :\n"+message+"\n");
			input_text.setText("");
			
			TranObject<TextMessage> msg=new TranObject<TextMessage>(TranObjectType.CLIENT_TO_SEVER);
			TextMessage text=new TextMessage(me.getId());
			//text.setAuthor_id(me.getId());
			text.setMessage(message);
			msg.setFromUser(me.getId());
			msg.setToUser(friend.getId());
			msg.setObjStr(gson.toJson(text));
			msg.setRequest(ClientRequest.MESSAGE);
			cr.add(me.getId(), text);
			c.getWriteThread().setMsg(msg);
			
		}else{
			MessageDialog.openWarning(Display.getDefault().getActiveShell(),"wain", "send message can not be null");
		}
		//super.okPressed();
		
	}
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// TODO Auto-generated method stub
		createButton(parent, IDialogConstants.OK_ID, "send",
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
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
