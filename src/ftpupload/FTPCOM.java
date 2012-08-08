package ftpupload;

import java.io.IOException;

public class FTPCOM {
	
	private FTPClient client;
	private volatile boolean finish;
	private volatile boolean over;
	

	public FTPCOM(String remotehost,int ftphost,String username,String password,int timeout) throws FTPException, IOException{
		
		client=new FTPClient();
		client.setRemoteAddress(remotehost);
		client.setTimeout(timeout);
		client.connect();
		client.login(username, password);
		finish=false;
	}
	
	private void inFolder(String remoteFolder) throws FTPException, IOException{
		client.cd(" /");
		if(!remoteFolder.equals("")){
			String[] t = remoteFolder.split("[\\\\/]");
			for(int i=0;i<t.length;i++){
				if(t[i].length() == 0){
					continue;
				}
				client.mkdir(t[i]);
				client.cd(t[i]);
			}
		}
	}
	
	public void transferData(String remoteFolder,String uploadName,String localFile) throws FTPException, IOException{
		
		inFolder(remoteFolder);
		client.autoChooseType(uploadName);
		client.transfer(localFile, uploadName);
		client.quit();
		finish=true;
	}
	
	public void transferData(String remoteFolder,String uploadName,String localFile,FTPProcessListener listener) throws FTPException, IOException{
		
		inFolder(remoteFolder);
		client.setListener(listener);
		client.autoChooseType(uploadName);
		client.transfer(localFile, uploadName);
		client.quit();
		finish=true;
	}
	
	public void quit() throws FTPException, IOException{
		client.quit();
	}
	
	public void stop(){
		
		client.setCancelTransfer(true);
	}
	
	public void recover(){
		client.setCancelTransfer(false);
	}
	
	public boolean isexist(String remoteFile,long rfSize) throws FTPException, IOException{
		
		long size=client.getSize(remoteFile);
		if(size==rfSize)
			return true;
		return false;
	}
	
	public long getStartSize(String remoteFile) throws FTPException, IOException{
		client.autoChooseType(remoteFile);
		return client.getStartSize(remoteFile);
	}
	
	public boolean ishandling(){
		return client.isHandling();
	}
	
	public boolean isover(){
		return client.isOver();
	}
	
	public boolean isfinish(){
		return finish;
	}
}
