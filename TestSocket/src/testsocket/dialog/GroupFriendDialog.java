package testsocket.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import testsocket.client.Client;
import testsocket.client.ClientRegister;
import testsocket.client.IClient;
import testsocket.client.UserUtils;
import testsocket.model.Category;
import testsocket.model.User;

public class GroupFriendDialog extends Dialog {
	IClient c;
	User me;
	User user;
	Combo combo;
	public GroupFriendDialog(Shell parentShell,User user) {
		super(parentShell);
		this.user=user;
		c=ClientRegister.getInstance().getCurrentClient();
		me=c.getUser();
		// TODO Auto-generated constructor stub
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		// TODO Auto-generated method stub
		Composite composite=(Composite) super.createDialogArea(parent);
		combo=new Combo(composite, SWT.NONE);
		combo.setItems(new String[]{"Friends","Family"});
		combo.select(0);
		return composite;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		// TODO Auto-generated method stub
		newShell.setText(me.getName());
		super.configureShell(newShell);
	}
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// TODO Auto-generated method stub
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}
	@Override
	public boolean close() {
		// TODO Auto-generated method stub
		String category_name=combo.getText();
		UserUtils.addToCategory(category_name, user);
		boolean closed= super.close();
		return closed;
	}
}
