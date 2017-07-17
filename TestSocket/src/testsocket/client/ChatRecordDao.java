package testsocket.client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import testsocket.filesystem.utils.UserHomeUtils;
import testsocket.model.Category;
import testsocket.model.TextMessage;



public class ChatRecordDao {
	// 私有构造器，防止被外面实例化改对像 
	private static ChatRecordDao instance;
	private Map<Integer,List<TextMessage>> map;
    private ChatRecordDao() {  
    	map=new HashMap<Integer,List<TextMessage>>();
    }  
  
    // 单例模式像外面提供该对象  
    public synchronized static ChatRecordDao getInstance() {  
        if (instance == null) {  
            instance = new ChatRecordDao();  
        }  
        return instance;  
    }
    public synchronized void add(Integer id, TextMessage tm) { 
    	List list=map.get(id);
    	if(list==null){
    		list=new ArrayList<TextMessage>();
    		map.put(id, list);
    	}
    	list.add(tm);
    
				
    }

	public Map<Integer,List<TextMessage>> getChat_record() {
		// TODO Auto-generated method stub
		return map;
	}

	


}
