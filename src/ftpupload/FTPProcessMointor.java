package ftpupload;

public class FTPProcessMointor implements FTPProcessListener{
	
	volatile long uploadSize;

	public void getUploadSize(long size) {
		// TODO Auto-generated method stub
		this.uploadSize=size;
	}

}
