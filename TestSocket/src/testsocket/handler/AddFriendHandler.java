package testsocket.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import testsocket.ViewPart;
import testsocket.dialog.SearchFriendDialog;

public class AddFriendHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		SearchFriendDialog dialog =new SearchFriendDialog(Display.getDefault().getActiveShell());
		dialog.open();
		//ViewPart vp=(ViewPart) HandlerUtil.getActivePart(event);
		//vp.refreshTreeViewer();
		return null;
	}

}
