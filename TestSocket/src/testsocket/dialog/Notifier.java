package testsocket.dialog;
 
import java.util.logging.Logger;

import org.eclipse.mylyn.commons.ui.dialogs.AbstractNotificationPopup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.MessageBox;

import testsocket.model.User;
 
public class Notifier extends AbstractNotificationPopup {
	
	private static Logger log = Logger.getLogger(Notifier.class.getName());
	
	private String title;
	private User user;
	private Image icon;
 
	public Notifier(Display display, String title, User user, Image icon) {
		super(display);
		this.title = title;
		this.user = user;
		this.icon = icon;
	}
 
	@Override
	protected void createContentArea(Composite composite) {
		composite.setLayout(new GridLayout(1, true));
		Link messageLabel = new Link(composite, SWT.WRAP);
		messageLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		messageLabel.setText("<a>"+user.getName()+" want to add you as friend</a>");
		messageLabel.setBackground(composite.getBackground());
		messageLabel.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				//Program.launch("http://www.smartbuilder.com");
				close();
				ConfirmAddFriendsDialog dialog=new ConfirmAddFriendsDialog(Display.getCurrent().getActiveShell(), user);
				dialog.open();
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
 
	@Override
	protected String getPopupShellTitle() {
		// Return a custom title
		return this.title;
	}
 
	@Override
	protected Image getPopupShellImage(int maximumHeight) {
		// Use createResource to use a shared Image instance of the ImageDescriptor
//		return (Image) Activator.getImageDescriptor("/icons/alt_about.gif")
//				.createResource(Display.getDefault());
		return icon;
	}
}