package ftpupload;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public class FTPDataSocket {
	
protected Socket socket; 
	
	protected FTPDataSocket(Socket socket){
		
		this.socket=socket;
	}
	
	public int getLocalPort(){
		return socket.getLocalPort();
	}
	
	public void setSoTimeOut(int timeout) throws SocketException{
		socket.setSoTimeout(timeout);
	}

	public OutputStream getOutputStream() throws IOException{
		
		return socket.getOutputStream();
	}
	
	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	} 
	
	public void close() throws IOException{
		
		socket.close();
	}

}
