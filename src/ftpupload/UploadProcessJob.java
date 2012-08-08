package ftpupload;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;


/**
 * 
 * @author BruceXX
 *	上传进度计时作业
 */

public class UploadProcessJob {
	
	  long totalbytes;

	 long starttime;

	/**
	 * 被更新的组件，这里为uploadProcessFrame
	 */
	Component control;
	/**
	 * 更新线程
	 */
	Thread updateThread;
	
	Thread uploadThread;
	
	Thread processThread;


	FTPCOM ftpcom;

	
	FTPProcessMointor monitor;

	
	File localFile;
	
	String remoteFolder;
	

	
	String uploadName;
	
	boolean breakAll=false;

	boolean breakCur=false;
	
	volatile long startSize;
	
	UploadProcessJob(String remoteHost,int ftpport,File localFile,String remoteFolder,String username,String pass,String uploadName) throws  IOException, FTPException{
		
//		this.way=2;
		totalbytes=localFile.length();		
		starttime=System.currentTimeMillis();
		this.remoteFolder=remoteFolder;
		this.localFile=localFile;
		this.uploadName=uploadName;
		monitor=new FTPProcessMointor();
		//System.out.println("monitor.uploadSize==="+monitor.uploadSize);
		ftpcom=new FTPCOM(remoteHost,ftpport,username,pass,100*1000);
		
	}
	
//	UploadProcessJob(File localFile,String remoteFolder,String ServletAddress,String uploadName){
//		this.way=1;		
//		totalbytes=localFile.length();
//		starttime=System.currentTimeMillis();
//		this.remoteFolder=remoteFolder;
//		this.localFile=localFile;
//		this.uploadName=uploadName;
//		httpcom=new HTTPCOM(ServletAddress);
//		
//	}
	
	public void start(){
		
		backgroundFlush();
		reUpload();
		
	}
	

	
	void transferData(){
		uploadThread = new Thread(new Runnable() {

			public void run() {

				// TODO Auto-generated method stub

					try {
						startSize=ftpcom.getStartSize(uploadName);
						ftpcom.transferData(remoteFolder, uploadName, localFile.getPath(), monitor);
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (FTPException e) {
						e.printStackTrace();
					}
				
			}
		});
		uploadThread.start();

	}
	
	void backgroundFlush(){
		
		updateThread=new Thread(
			new Runnable(){
				public void run(){

					while(!ftpcom.isfinish()){
						UpdateUI();
						try{
							Thread.sleep(500);
						}catch(InterruptedException e){}
						
					}
					
				}
			}
		);
		updateThread.start();		
	}
	
	
	
	/**
	 * 停止FTP上传
	 */
	public void stop(){

			ftpcom.stop();
	}
	

	/**
	 * 重新开始上传
	 */
	public void reUpload(){

			ftpcom.recover();
		transferData();
	}
	
	
	public void UpdateUI(){
		
		
		UploadProcessFrame dlg= (UploadProcessFrame)control;
		
		/**
		 * 更新uploadprocessFrame中的内容
		 */
		long endSize=0;
		long donebytes=0;

		endSize=startSize+monitor.uploadSize;
		//System.out.println("startSize=="+startSize);
		//System.out.println("monitor.uploadSize=="+monitor.uploadSize);
		donebytes=monitor.uploadSize;
		dlg.percent.setText((new StringBuilder(String.valueOf((int)((float)(100L * (endSize)) / (float)totalbytes)))).append("%").toString());
		String filepath=localFile.getPath();
		int len=filepath.length();
		filepath="当前上传:"+(filepath.length()>30?filepath.substring(0,10)+"..."+filepath.substring(len-10, len):filepath);
		dlg.curFilepath.setText(filepath);
		dlg.getProcessBar().setValue((int)((float)(10000L * endSize) / (float)totalbytes));
		dlg.totalBytes.setText(shortSize(totalbytes));
		dlg.doneBytes.setText(shortSize(endSize));
		
		long timepast=System.currentTimeMillis()-starttime;
		dlg.timePast.setText(getCurTime(timepast));

			
			if(timepast!=0L)
				dlg.avgSpeed.setText(new StringBuilder(String.valueOf(shortSize((long)((float)1000*donebytes/(float)(timepast))))).append("/秒").toString());
			if(donebytes!=0L){
				/**
				 * 估计剩余的时间
				 */
				if(timepast!=0){
				long speed=donebytes/timepast;
				if(speed!=0){
				long timeleft=(totalbytes-endSize)/speed;
				dlg.timeLeft.setText(getCurTime(timeleft));
				}
				}
			}				
		
		
	}
	
	protected String shortSize(long s){
		if(s<1024L)
			return new StringBuilder(String.valueOf(s)).append("Bytes").toString();
		DecimalFormat df=new DecimalFormat("0.00");
		if(s<0x100000L)
			return new StringBuilder(String.valueOf(df.format((float)s/1024F))).append("KB").toString();
		else 
			return new StringBuilder(String.valueOf(df.format((float)s/(1024*1024F)))).append("MB").toString();
		
	}
	
	private String getCurTime(long psec){
		long sec=psec/1000L;
		if(sec<60)
			return new StringBuilder(String.valueOf(sec)).append("秒").toString();
		else if(sec<60*60 && sec>=60)
			return new StringBuilder(String.valueOf(sec/60)).append("分").append(String.valueOf(sec%60)).append("秒").toString();
		else{
			int hour=(int)sec/(60*60);
			int temp=(int)sec%(60*60);
			int min=temp/60;
			int second=temp%60;
			return new StringBuilder(String.valueOf(hour)).append("小时").append(String.valueOf(min)).append("分").append(String.valueOf(second)).append("秒").toString();
		}
	}
	
	

	public void Curstop(){

		stop();
		if(uploadThread!=null)
			uploadThread.interrupt();
		if(updateThread!=null)
			updateThread.interrupt();		
		UpdateUI();
		
	}
	
	public void quitAll(){
		
		stop();
		breakAll=true;
	}
	
	
	
	public boolean isfinish(){
		
		return ftpcom.isfinish();
	}
	
	void setProcessControl(Component d){
		
		control=d;
		UpdateUI();
	}
	
	void setProcessThread(Thread t){
		this.processThread=t;	
	}
	

}
