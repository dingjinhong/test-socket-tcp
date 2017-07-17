package testsocket.client;

import testsocket.model.TranObject;

public abstract class ClientWriteThread extends Thread {
	  protected boolean isStart = true;
	  public void setStart(boolean isStart) {  
	        this.isStart = isStart;  
	    }
	  public abstract void setMsg(TranObject msg);
}
