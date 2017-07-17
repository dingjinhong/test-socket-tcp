package testsocket.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class FormSelectDialog extends Dialog {
	boolean useNIO=true;
	public FormSelectDialog(Shell parentShell) {
		super(parentShell);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// TODO Auto-generated method stub
		Composite composite=(Composite) super.createDialogArea(parent);
		Button  useNIO_btn=new Button(composite,SWT.CHECK);
		useNIO_btn.setSelection(useNIO);
		useNIO_btn.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				useNIO=useNIO_btn.getSelection();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		return composite;
	}
	@Override
	protected void handleShellCloseEvent() {
		// TODO Auto-generated method stub
		okPressed();
		//super.handleShellCloseEvent();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// TODO Auto-generated method stub
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}

	public boolean isUseNIO() {
		return useNIO;
	}
	

}
