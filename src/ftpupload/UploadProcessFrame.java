package ftpupload;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * 
 * @author BruceXX
 * 上传进度条窗口
 */

public class UploadProcessFrame extends JFrame {
	
	
	
	 JPanel mainPanel=null,centerPanel=null;
	 JProgressBar processbar=null;
	 JLabel label1=null,label2=null,label3=null,label4=null,label5=null,label6=null,label7=null;
	
	 JLabel done=null,totalBytes=null,doneBytes=null,avgSpeed=null,timePast=null,timeLeft=null,percent=null,curFilepath=null;
	 JButton cancel=null,pause=null,quit=null;
	 UploadProcessJob job;
	 Thread t=null;
	
	public UploadProcessFrame(){
		
		init();
	}
	
	private void init(){
		
        setSize(600, 240);
        setMinimumSize(new Dimension(600, 240));
//        setPreferredSize(new Dimension(600, 240));
        setTitle("上传进度条");
        setDefaultCloseOperation(2);
        setContentPane(getMainPanel());
		
	}
	
	private JPanel getMainPanel(){
		
		if(mainPanel==null){
			
			/**
			 * 主界面标签初始化
			 */
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.gridx = 0;
            gridBagConstraints14.gridwidth = 4;
            gridBagConstraints14.weightx = 0.0D;
            gridBagConstraints14.fill = 2;
            gridBagConstraints14.gridy = 5;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.gridx = 1;
            gridBagConstraints9.anchor = 16;
            gridBagConstraints9.insets = new Insets(0, 10, 0, 0);
            gridBagConstraints9.fill = 0;
            gridBagConstraints9.weightx = 0.0D;
            gridBagConstraints9.gridy = 0;
            percent = new JLabel();
            percent.setText("0%");
            curFilepath=new JLabel();
            
             
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 1;
            gridBagConstraints8.anchor = 17;
            gridBagConstraints8.insets = new Insets(0, 10, 5, 20);
            gridBagConstraints8.fill = 0;
            gridBagConstraints8.weightx = 0.5D;
            gridBagConstraints8.gridy = 4;
            timeLeft = new JLabel();
            timeLeft.setText("N/A");
            timeLeft.setMinimumSize(new Dimension(90, 14));
            timeLeft.setMaximumSize(new Dimension(90, 14));
            timeLeft.setPreferredSize(new Dimension(70, 14));
            
            GridBagConstraints gridBagConstraints71 = new GridBagConstraints();
            gridBagConstraints71.gridx = 0;
            gridBagConstraints71.anchor = 13;
            gridBagConstraints71.insets = new Insets(0, 20, 5, 0);
            gridBagConstraints71.weightx = 0.0D;
            gridBagConstraints71.gridy = 4;
            label1 = new JLabel();
            label1.setText("剩余时间:");
            GridBagConstraints gridBagConstraints61 = new GridBagConstraints();
            gridBagConstraints61.gridx = 1;
            gridBagConstraints61.anchor = 16;
            gridBagConstraints61.insets = new Insets(0, 10, 5, 20);
            gridBagConstraints61.fill = 0;
            gridBagConstraints61.ipadx = 0;
            gridBagConstraints61.weightx = 0.5D;
            gridBagConstraints61.gridy = 3;
            timePast = new JLabel();
            timePast.setText("0 小时 0 分 0秒");
            timePast.setMinimumSize(new Dimension(90, 14));
            timePast.setMaximumSize(new Dimension(90, 14));
            timePast.setPreferredSize(new Dimension(70, 14));
            
            
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.anchor = 13;
            gridBagConstraints5.insets = new Insets(0, 20, 5, 0);
            gridBagConstraints5.weightx = 0.0D;
            gridBagConstraints5.gridy = 3;
            label2 = new JLabel();
            label2.setText("已用时间:");
            GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
            gridBagConstraints31.gridx = 3;
            gridBagConstraints31.insets = new Insets(0, 10, 5, 20);
            gridBagConstraints31.anchor = 16;
            gridBagConstraints31.weightx = 0.5D;
            gridBagConstraints31.fill = 0;
            gridBagConstraints31.gridy = 3;
            avgSpeed = new JLabel();
            avgSpeed.setText("0 B/秒");
            avgSpeed.setMinimumSize(new Dimension(90, 14));
            avgSpeed.setMaximumSize(new Dimension(90, 14));
            avgSpeed.setPreferredSize(new Dimension(70, 14));
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.gridx = 2;
            gridBagConstraints12.insets = new Insets(0, 0, 5, 0);
            gridBagConstraints12.anchor = 14;
            gridBagConstraints12.weightx = 0.0D;
            gridBagConstraints12.gridy = 3;
            label3 = new JLabel();
            label3.setText("上传平均速度:");
            
            
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 3;
            gridBagConstraints7.insets = new Insets(0, 10, 5, 20);
            gridBagConstraints7.anchor = 16;
            gridBagConstraints7.weightx = 0.5D;
            gridBagConstraints7.fill = 0;
            gridBagConstraints7.gridy = 2;
            doneBytes = new JLabel();
            doneBytes.setText("0B");
            doneBytes.setMinimumSize(new Dimension(90, 14));
            doneBytes.setMaximumSize(new Dimension(90, 14));
            doneBytes.setPreferredSize(new Dimension(70, 14));
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 2;
            gridBagConstraints6.anchor = 14;
            gridBagConstraints6.insets = new Insets(20, 0, 5, 0);
            gridBagConstraints6.weightx = 0.0D;
            gridBagConstraints6.gridy = 2;
            label4 = new JLabel();
            label4.setText("已上传大小:");
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 1;
            gridBagConstraints3.anchor = 16;
            gridBagConstraints3.insets = new Insets(20, 10, 5, 20);
            gridBagConstraints3.weightx = 0.5D;
            gridBagConstraints3.fill = 0;
            gridBagConstraints3.gridy = 2;
            totalBytes = new JLabel();
            totalBytes.setText("0B");
            totalBytes.setMinimumSize(new Dimension(90, 14));
            totalBytes.setMaximumSize(new Dimension(90, 14));
            totalBytes.setPreferredSize(new Dimension(70, 14));
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.insets = new Insets(20, 29, 5, 0);
            gridBagConstraints2.anchor = 13;
            gridBagConstraints2.weightx = 0.0D;
            gridBagConstraints2.gridy = 2;
            label5 = new JLabel();
            label5.setText("总共大小:");
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.anchor = 17;
            gridBagConstraints1.insets = new Insets(20, 20, 0, 0);
            gridBagConstraints1.gridwidth = 1;
            gridBagConstraints1.gridy = 0;
            label6 = new JLabel();
            label6.setText("当前进度:");
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.weightx = 0.0D;
            gridBagConstraints.fill = 2;
            gridBagConstraints.insets = new Insets(5, 20, 0, 20);
            gridBagConstraints.anchor = 11;
            gridBagConstraints.weighty = 0.0D;
            gridBagConstraints.gridwidth = 5;
            gridBagConstraints.ipady = 0;
            gridBagConstraints.gridy = 1;
            label7=new JLabel();
            label7.setText("正在等待服务器传送完毕....");
            label7.setVisible(false);
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getProcessBar(), gridBagConstraints);
            mainPanel.add(label6, gridBagConstraints1);
            mainPanel.add(label5, gridBagConstraints2);
            mainPanel.add(totalBytes, gridBagConstraints3);
            mainPanel.add(label4, gridBagConstraints6);
            mainPanel.add(doneBytes, gridBagConstraints7);
            mainPanel.add(label3, gridBagConstraints12);
            mainPanel.add(avgSpeed, gridBagConstraints31);
            mainPanel.add(label2, gridBagConstraints5);
            mainPanel.add(timePast, gridBagConstraints61);
            mainPanel.add(label1, gridBagConstraints71);
            mainPanel.add(timeLeft, gridBagConstraints8);
            mainPanel.add(percent, gridBagConstraints9);
            mainPanel.add(curFilepath);
            mainPanel.add(label7);
            mainPanel.add(getCenterPanel(), gridBagConstraints14);
		}
		return mainPanel;
	}
	
	 JProgressBar getProcessBar(){
		
		if(processbar==null){
			
			processbar=new JProgressBar();
			processbar.setPreferredSize((new Dimension(200, 20)));
			processbar.setMaximum(10000);
		}
		return processbar;
	}
	
	private JButton getCancelButton(){
		
		if(cancel==null){
			cancel=new JButton();
			cancel.setText("取消当前");
			cancel.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					job.breakCur=true;
					job.Curstop();
					label7.setVisible(true);
					cancel.setEnabled(false);
					quit.setEnabled(false);
					waitover();

				}
			});
		}
		return cancel;
	}
	
	
	private void waitover(){		 
		t=new Thread(new Runnable(){

			public void run() {
				// TODO Auto-generated method stub

				while(job.ftpcom.ishandling()){
					
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
				label7.setVisible(false);				
				setVisible(false);
				t.interrupt();
			}			
		});	
		t.start();
		
	}
	
	
	private JButton getQuitButton(){
		
		if(quit==null){
			quit=new JButton();
			quit.setText("取消全部");
			quit.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					job.quitAll();
					label7.setVisible(true);
					cancel.setEnabled(false);
					quit.setEnabled(false);
					waitover();
				}
			});
		}
		return quit;
		
	}
	

	
	
	private JPanel getCenterPanel(){
		if(centerPanel==null){
			
			 GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
	            gridBagConstraints10.fill = 0;
	            gridBagConstraints10.gridwidth = 1;
	            gridBagConstraints10.gridx = -1;
	            gridBagConstraints10.gridy = -1;
	            gridBagConstraints10.weightx = 0.5D;
	            gridBagConstraints10.insets = new Insets(10, 0, 10, 0);
	            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
	            gridBagConstraints4.insets = new Insets(10, 0, 10, 0);
	            gridBagConstraints4.gridx = -1;
	            gridBagConstraints4.gridy = -1;
	            gridBagConstraints4.weightx = 0.5D;
	            gridBagConstraints4.gridwidth = 1;
	            centerPanel = new JPanel();
	            centerPanel.setLayout(new GridBagLayout());
	            centerPanel.add(getCancelButton(), gridBagConstraints4);
	            centerPanel.add(getQuitButton(),gridBagConstraints10);
//	            centerPanel.add(getPauseButton(), gridBagConstraints10);

		}
		return centerPanel;
	}
	
	final class ServerWaitingFrame extends JFrame{
		
		JPanel panel;
		JLabel jlabel;
		
		public ServerWaitingFrame(){
			
			 setSize(200, 70);
		     setContentPane(getSelfPanel());
		}
		
		private JPanel getSelfPanel(){
			if(panel==null){
				panel=new JPanel();
				jlabel=new JLabel();
				jlabel.setText("正在等待服务器传送完毕...");
				
			}
			return panel;
		}
	
	}
	

}
