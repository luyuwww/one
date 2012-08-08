package ftpupload;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import ftpupload.util.CommonUtil;

import netscape.javascript.JSObject;

/**
 * 主应用程序
 * 
 * @author BruceXX
 * 
 */

public class MainApp extends JApplet {
	JSObject win;
	/**
	 * 主面板
	 */
	private JPanel mainpanel = null, centerPanel = null, northPanel = null;

	private JScrollPane jscrollPane = null;
	/**
	 * 图标
	 */
	private ImageIcon fileIcon, folderIcon, delIcon;
	/**
	 * 最大数量
	 */
	private long maxSize;
	/**
	 * 待上传的表单文件项
	 */
	private List<uploadItem> uploads;

	int removeItem;

	private JTextField textField;
	private JButton addButton = null, uploadButton = null;
	private JComboBox pzmComboBox = null;

	/**
	 * 上传的远程文件夹路径
	 */
	public String folderPath;
	private JLabel notice = null, remind = null;
	private JTable jtable = null;

	private JPopupMenu jpopmenu = null;
	private JMenuItem jmenuitem = null;

	/**
	 * 上传表格初始化模型
	 */
	private UploadTableModel uploadmodel = null;

	Thread processJob;
	/**
	 * HTTP接收servlet
	 */
	String ServletAddress;
	/**
	 * FTP接收servlet
	 */
	String FTPServletAddress;
	/**
	 * 当前传送所属文档id
	 */
	String docId;
	/**
	 * 当前传送所属的表名
	 */
	String tabName;
	/**
	 * 文档目录id
	 */
	String categoryId;

	String uploadWay;

	Properties p;

	private String hostName;
	private String ftpuser;
	private String ftppass;
	private int ftport;
	private String[] pzmArray;

	/**
	 * 0表示不支持文件拖曳,1表示支持
	 */
	private int isFolderDragSupport;

	public MainApp() {
		/**
		 * 256MB
		 */
		maxSize = 0x10000000L;
		uploads = new ArrayList<uploadItem>();

	}

	private JPanel getCenterPanel() {

		if (centerPanel == null) {
			centerPanel = new JPanel();
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.anchor = 18;
			gridBagConstraints4.gridy = 3;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = 2;
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.weightx = 0.0D;
			gridBagConstraints3.gridx = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.weighty = 1.0D;
			gridBagConstraints1.insets = new Insets(0, 10, 10, 10);
			gridBagConstraints1.gridwidth = 3;
			gridBagConstraints1.fill = 1;
			gridBagConstraints1.gridy = 2;

			notice = new JLabel();
			notice.setText("<html><center>拖曳到这里<br>或是点击添加按钮添加\"add\"</html>");
			notice.setHorizontalAlignment(0);
			notice.setFont(new Font("Tahoma", 0, 14));

			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = 1;
			gridBagConstraints6.gridy = 2;
			gridBagConstraints6.weightx = 1.0D;
			gridBagConstraints6.weighty = 1.0D;
			gridBagConstraints6.gridwidth = 3;
			gridBagConstraints6.insets = new Insets(0, 10, 0, 10);
			gridBagConstraints6.gridx = 0;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.insets = new Insets(10, 10, 10, 10);
			gridBagConstraints5.anchor = 13;
			gridBagConstraints5.gridwidth = 3;
			gridBagConstraints5.gridy = 3;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.insets = new Insets(10, 10, 10, 10);
			gridBagConstraints10.gridy = 1;
			gridBagConstraints10.weightx = 1.0D;
			gridBagConstraints10.fill = 1;
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.gridwidth=1;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 1;
			gridBagConstraints9.anchor = 13;
			gridBagConstraints9.insets = new Insets(10, 10, 10, 10);
			gridBagConstraints9.gridy = 1;

			remind = new JLabel();
			remind.setText("AMT源天软件");

			centerPanel.setLayout(new GridBagLayout());
			centerPanel.add(getAddButton(), gridBagConstraints9);
			//centerPanel.add(remind, gridBagConstraints10);
			centerPanel.add(getPzmComboBox(), gridBagConstraints10);
			centerPanel.add(getUploadButton(), gridBagConstraints5);
			centerPanel.add(getScrollPanel(), gridBagConstraints6);
			centerPanel.add(getNorthPanel(), gridBagConstraints1);
			centerPanel.add(getTextField(), gridBagConstraints3);
			// centerPanel.add(, gridBagConstraints4);
		}
		return centerPanel;
	}

	private JTextField getTextField() {

		if (textField == null) {
			textField = new JTextField();
			textField.setPreferredSize(new Dimension(30, 19));
			textField.setVisible(false);
		}
		return textField;
	}

	private JPanel getNorthPanel() {
		if (northPanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = 2;
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.gridx = -1;
			gridBagConstraints.gridy = -1;
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.weighty = 1.0D;
			gridBagConstraints.anchor = 10;
			gridBagConstraints.insets = new Insets(0, 30, 0, 30);

			northPanel = new JPanel();
			northPanel.setLayout(new GridLayout());
			northPanel.add(notice, gridBagConstraints);
		}
		return northPanel;
	}

	private JScrollPane getScrollPanel() {
		if (jscrollPane == null) {

			jscrollPane = new JScrollPane();
			jscrollPane.setVisible(false);
			jscrollPane.setViewportView(getTable());
		}
		return jscrollPane;

	}

	/**
	 * 弹出菜单的实现，弹出菜单是一个可弹出并显示一系列选项的小窗口。 JPopupMenu 用于用户在菜单栏上选择项时显示的菜单。
	 * 它还用于当用户选择菜单项并激活它时显示的 右拉式 (pull-right)”菜单。 最后，JPopupMenu
	 * 还可以在想让菜单显示的任何其他位置使用.
	 * 
	 * @return
	 */
	private JPopupMenu getPopMenu() {

		if (jpopmenu == null) {
			jpopmenu = new JPopupMenu();
			jpopmenu.add(getMenuItem());
		}
		return jpopmenu;
	}

	/**
	 * 菜单中的项的实现。菜单项本质上是位于列表中的按钮。当用户选择“按钮”时， 将执行与菜单项关联的操作。JPopupMenu 中包含的
	 * JMenuItem 正好执行该功能 右键出现菜单项
	 * 
	 * @return
	 */

	private JMenuItem getMenuItem() {

		if (jmenuitem == null)
			jmenuitem = new JMenuItem();
		jmenuitem.setText("删除该行");
		jmenuitem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				uploads.remove(removeItem);
				getTable().updateUI();
				if (uploads.size() == 0)
					deleteFileTrig();
			}
		});
		return jmenuitem;
	}

	private UploadTableModel getUploadModel() {

		if (uploadmodel == null) {

			uploadmodel = new UploadTableModel();
		}

		return uploadmodel;
	}

	private JTable getTable() {

		if (jtable == null) {
			jtable = new JTable();
			jtable.setModel(getUploadModel());
			/**
			 * 设置表是否绘制单元格之间的垂直线
			 */
			jtable.setShowVerticalLines(false);
			/**
			 * 设置表是否绘制单元格周围的网格线
			 */
			jtable.setShowGrid(false);

			jtable.setIntercellSpacing(new Dimension(5, 5));
			jtable.setDefaultRenderer(jtable.getColumnClass(0),
					new UploadTableCellRender());
			jtable.setRowHeight(32);
			jtable.addMouseListener(new MouseAdapter() {

				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub
					/**
					 * 点击鼠标左键
					 */
					if (e.getButton() == MouseEvent.BUTTON1) {
						if (getTable().getColumnName(
								getTable().columnAtPoint(e.getPoint())).equals(
								"操作")) {

							uploads.remove(getTable().rowAtPoint(e.getPoint()));
							getTable().updateUI();
							if (uploads.size() == 0)
								deleteFileTrig();
						}
					} else {
						removeItem = getTable().rowAtPoint(e.getPoint());
						getPopMenu().show(e.getComponent(), e.getX(), e.getY());

					}
				}
			});

			jtable.setRowMargin(5);

			/**
			 * 初始化各个列的单元格
			 */

			UploadNullTableCellEditor nte = new UploadNullTableCellEditor();
			jtable.getColumnModel().getColumn(0).setCellEditor(nte);
			jtable.getColumnModel().getColumn(1).setCellEditor(nte);
			jtable.getColumnModel().getColumn(2).setCellEditor(nte);
			jtable.getColumnModel().getColumn(3).setCellEditor(nte);
			jtable.getColumnModel().getColumn(5).setCellEditor(nte);

		}
		return jtable;
	}

	private JButton getAddButton() {

		addButton = new JButton();
		addButton.setText("添加");
		addButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser dlg = new JFileChooser();
				/**
				 * 允许文件和目录
				 */
				if (isFolderDragSupport == 1)
					dlg
							.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				else
					dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
				/**
				 * 允许多个文件选择
				 */
				dlg.setMultiSelectionEnabled(true);

				if (dlg.showOpenDialog(mainpanel) != JFileChooser.APPROVE_OPTION)
					return;
				File[] files = dlg.getSelectedFiles();
				for (int i = 0; i < files.length; i++) {
					File f = files[i];
					if (f.isFile()) {
						if (f.length() > maxSize)
							JOptionPane.showMessageDialog(null, "单个文件不能超过"
									+ maxSize / (1024 * 1024));
						else {
							uploadItem it = new uploadItem();
							it.setF(f);
							it.setUploadName(f.getName());
							uploads.add(it);

							// addFileTrig();
						}
					} else if (f.isDirectory()) {
						addFolder(f);

					}
				}
				addFileTrig();
				getTable().updateUI();
			}
		});

		return addButton;
	}

	private void addFolder(File f) {		
		File[] files = f.listFiles();
		for (int i = 0; i < files.length; i++) {
			addFile(files[i]);
		}
	}

	private void addFile(File f) {

		if (f.isFile()) {
			uploadItem it = new uploadItem();
			it.setF(f);
			it.setUploadName(f.getName());
			uploads.add(it);
		} else if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				addFile(files[i]);
			}
		}
	}

	private void ProcessJob() {

		processJob = new Thread(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub			

				final UploadProcessFrame frame = new UploadProcessFrame();
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

				frame.setLocation(dim.width / 2 - frame.getWidth() / 2,
						dim.height / 2 - frame.getHeight() / 2);

				while (uploads.size() != 0) {
					frame.setAlwaysOnTop(true);
					frame.setVisible(true);
					frame.cancel.setEnabled(true);
					frame.quit.setEnabled(true);
					UploadProcessJob job = null;

					uploadItem it = uploads.get(0);
					File f = it.getF();
					/**
					 * 显示文件名
					 */
					//String uploadName=it.getUploadName();
					String title = it.getUploadName();
					title=title.substring(0,title.lastIndexOf("."));
				
					String remoteFile=CommonUtil.createPrimaryKey();//随机生成上传文件名称
					String fileName = it.getF().getName();
					String ext = fileName.substring(fileName.lastIndexOf(".")+1);					
					remoteFile=remoteFile+"."+ext;
					


					try {
						//job = new UploadProcessJob(hostName, ftport, f,
								//folderPath, ftpuser, ftppass, uploadName);
						
						job = new UploadProcessJob(hostName, ftport, f,
								folderPath, ftpuser, ftppass, remoteFile);
					} catch (FTPException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					frame.job = job;
					job.setProcessControl(frame);
					job.start();
					//win.eval("aa()");uploadFile(title,ext,pzm,pathname,efilename)
					String pzm=getPzmComboBox().getSelectedItem().toString();
					win.eval("uploadFile('"+title+"','"+ext+"','"+pzm+"','"+folderPath+"','"+remoteFile+"')");
					

					try {

						Thread.sleep(200);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}

					while (!job.isfinish()) {
						try {
							Thread.sleep(500);
							if (job.ftpcom.isover()) {
								frame.label7.setVisible(true);
								frame.cancel.setEnabled(false);
								frame.quit.setEnabled(false);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					if (job.breakAll) {
						uploads.removeAll(uploads);
						deleteFileTrig();
						break;
					}

					job.Curstop();
					frame.label7.setVisible(false);
					uploads.remove(0);
					jtable.updateUI();
					// System.out.println("第"+(i+1)+"个上传完毕!");
				}

				if (0 == uploads.size()) {
					frame.setVisible(false);
					JOptionPane.showMessageDialog(null, "上传任务完毕");
					deleteFileTrig();
					processJob.interrupt();
				}
			}

		});

		processJob.start();

	}

	private JButton getUploadButton() {

		if (uploadButton == null) {

			uploadButton = new JButton();
			uploadButton.setText("开始上传");
			uploadButton.setVisible(false);
			uploadButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {					
					//win.eval("getPzmInfo(\""+getPzmComboBox().getSelectedItem()+"\")");
					ProcessJob();
				}

			});
		}
		return uploadButton;
	}

	private JComboBox getPzmComboBox() {
		if (pzmComboBox == null) {
			pzmComboBox = new JComboBox(pzmArray);
			pzmComboBox.setBorder(BorderFactory.createTitledBorder("挂接配制名:"));			
		    win.eval("getPzmInfo(\""+pzmComboBox.getSelectedItem()+"\")");
		    pzmComboBox.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {// ItemListener界面只有itemStateChanged()一个方法，在此实作它。
					if (e.getStateChange() == ItemEvent.SELECTED) {// 当用户的选择改变时
						int fontsize = 0;
						try {
							System.out.println(e.getItem());
							win.eval("getPzmInfo(\""+e.getItem()+"\")");
						} catch (Exception ex) {// 若所输入的值不是整数，则不作任何的操作.
                            ex.printStackTrace();
						}
					}
				}

			});

		}
		return pzmComboBox;
	}

	long CaculateFileSize(File f) {

		if (f.isFile()) {

			return f.length();
		}

		File[] files = f.listFiles();
		long s = 0L;
		for (int i = 0; i < files.length; i++) {
			s += CaculateFileSize(files[i]);
		}
		return s;

	}

	private String transSize(long s) {

		if (s < 1024L)
			return new StringBuilder(String.valueOf(s)).append("Bytes")
					.toString();
		DecimalFormat df = new DecimalFormat("0.00");
		if (s < 0x100000L)
			return new StringBuilder(String.valueOf(df
					.format((float) s / 1024F))).append("KB").toString();
		if (s < 0x40000000L)
			return new StringBuilder(String.valueOf(df.format((float) s
					/ (1024 * 1024F)))).append("MB").toString();

		return "";
	}

	public void addFileTrig() {

		getNorthPanel().setVisible(false);
		getScrollPanel().setVisible(true);
		getUploadButton().setVisible(true);
		getAddButton().setEnabled(false);

	}

	public void deleteFileTrig() {

		getNorthPanel().setVisible(true);
		getScrollPanel().setVisible(false);
		getUploadButton().setVisible(false);
		getAddButton().setEnabled(true);

	}

	public void setFtpFwqInfo(String ftpuser,String ftppass,String ftport,String hostName){
		this.ftpuser=ftpuser;
		this.ftppass=ftppass;
		this.ftport=Integer.parseInt(ftport);
		this.hostName=hostName;		
		

		
		
	}
	
	public void init() {
		win = JSObject.getWindow(this);
/*		p = new Properties();
		try {
			p.load(this.getClass().getResourceAsStream("upload.properties"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		hostName = this.getParameter("hostName");
		if (hostName == null)
			hostName = p.getProperty("hostName");
		ftpuser = this.getParameter("ftpuser");
		if (ftpuser == null)
			ftpuser = p.getProperty("ftpuser");
		ftppass = this.getParameter("ftppass");
		if (ftppass == null)
			ftppass = p.getProperty("ftppass");

		String port = this.getParameter("ftport");
		if (port == null)
			port = "21";
		ftport = Integer.parseInt(port);
		folderPath = this.getParameter("folderpath");
		if (folderPath == null)
			folderPath = p.getProperty("remoteFolder");*/
		folderPath = this.getParameter("folderpath");
		String folderDragSupport = this.getParameter("isFolderDragSupport");
		if (folderDragSupport == null)
			isFolderDragSupport = 1;
		else {
			if (folderDragSupport.equals("0"))
				isFolderDragSupport = 0;
			else
				isFolderDragSupport = 1;
		}
		String maxSize = this.getParameter("maxSize");
		if (maxSize != null && !maxSize.equals("")) {
			try {
				this.maxSize = Long.parseLong(maxSize);
			} catch (Exception e) {

			}
		}

		

		String pzms = this.getParameter("pzms");// ftp配制名
		pzmArray = pzms.split(",", -1);

		mainpanel = new JPanel();
		mainpanel.setLayout(new BorderLayout());
		mainpanel.add(getCenterPanel(), "Center");

		this.setContentPane(mainpanel);
		try {
			fileIcon = new ImageIcon(ImageIO.read(getClass()
					.getResourceAsStream("documents.png")));
			folderIcon = new ImageIcon(ImageIO.read(getClass()
					.getResourceAsStream("folderopen.png")));
			delIcon = new ImageIcon(ImageIO.read(getClass()
					.getResourceAsStream("cancel.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		new DropTarget(getCenterPanel(), new FileDropTargetListener());
		new DropTarget(getTable(), new FileDropTargetListener());
	}

	private class UploadNullTableCellEditor implements TableCellEditor {

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			// TODO Auto-generated method stub
			return null;
		}

		public void addCellEditorListener(CellEditorListener l) {
			// TODO Auto-generated method stub

		}

		public void cancelCellEditing() {
			// TODO Auto-generated method stub

		}

		public Object getCellEditorValue() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isCellEditable(EventObject anEvent) {
			// TODO Auto-generated method stub
			return false;
		}

		public void removeCellEditorListener(CellEditorListener l) {
			// TODO Auto-generated method stub

		}

		public boolean shouldSelectCell(EventObject anEvent) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean stopCellEditing() {
			// TODO Auto-generated method stub
			return false;
		}

	}

	private class UploadTableModel extends DefaultTableModel {

		public int getColumnCount() {
			return 6;
		}

		public int getRowCount() {
			return uploads.size();
		}

		public String getColumnName(int col) {

			switch (col) {
			case 0:
				return "";
			case 1:
				return "文件名称";
			case 2:
				return "文件路径";
			case 3:
				return "文件大小";
			case 4:
				return "上传名";
			case 5:
				return "操作";

			}
			return "";
		}

		public Object getValueAt(int row, int col) {

			File f = uploads.get(row).getF();
			String uploadName = uploads.get(row).uploadName;
			int pos = uploadName.lastIndexOf(".");
			if (pos != -1)
				uploadName = uploadName.substring(0, uploadName
						.lastIndexOf("."));

			if (col == 0) {
				if (uploads.get(row).getF().isDirectory())
					return folderIcon;
				if (uploads.get(row).getF().isFile())
					return fileIcon;
			}
			if (col == 1) {

				return uploads.get(row).getF().getName();
			}
			if (col == 2) {
				return uploads.get(row).getF().getPath();
			}
			if (col == 3)
				return transSize(CaculateFileSize(uploads.get(row).getF()));
			if (col == 4)
				return uploadName;
			if (col == 5)
				return delIcon;

			return null;
		}

		public void setValueAt(Object aValue, int row, int col) {

			if (col == 4) {
				uploadItem it = uploads.get(row);
				String fileName = it.getF().getName();
				String suffix = fileName.substring(fileName.lastIndexOf("."));

				uploads.get(row).setUploadName(aValue.toString() + suffix);
			}

		}

	}

	class FileDropTargetListener implements DropTargetListener {

		public void dragEnter(DropTargetDragEvent event) {

			if (!isDragAcceptable(event)) {
				event.rejectDrag();
				return;
			}
		}

		public void dragExit(DropTargetEvent event) {
		}

		public void dragOver(DropTargetDragEvent event) {
		}

		public void dropActionChanged(DropTargetDragEvent event) {
			if (!isDragAcceptable(event)) {
				event.rejectDrag();
				return;
			}
		}

		public void drop(DropTargetDropEvent event) {
			if (!isDropAcceptable(event)) {
				event.rejectDrop();
				return;
			}

			event.acceptDrop(DnDConstants.ACTION_COPY);

			Transferable transferable = event.getTransferable();

			DataFlavor[] flavors = transferable.getTransferDataFlavors();
			for (int i = 0; i < flavors.length; i++) {
				DataFlavor d = flavors[i];
				if (d.equals(DataFlavor.javaFileListFlavor)) {
					List fileList = new ArrayList();
					try {
						fileList = (List) transferable.getTransferData(d);
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					boolean containFolder = false;
					Iterator iterator = fileList.iterator();
					while (iterator.hasNext()) {
						File f = (File) iterator.next();

						if (f.isFile()) {
							if (f.length() > maxSize)
								JOptionPane.showMessageDialog(null, "单个文件不能超过"
										+ maxSize / (1024 * 1024));
							else {
								uploadItem it = new uploadItem();
								it.setF(f);
								it.setUploadName(f.getName());
								uploads.add(it);

							}
						} else if (f.isDirectory()) {
							if (isFolderDragSupport == 1)
								addFolder(f);
							else
								containFolder = true;
						}

					}
					if (containFolder) {
						JOptionPane.showMessageDialog(null, "当前用户不允许拖曳文件夹");
					}

					addFileTrig();
					jtable.updateUI();
				}
			}
			event.dropComplete(true);
		}

		public boolean isDragAcceptable(DropTargetDragEvent event) {
			return (event.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0;
		}

		public boolean isDropAcceptable(DropTargetDropEvent event) {
			return (event.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0;
		}

	}

	final class uploadItem {

		File f;
		String uploadName;

		public File getF() {
			
			return f;
		}

		public void setF(File f) {
			this.f = f;
		}

		public String getUploadName() {
			return uploadName;
		}

		public void setUploadName(String uploadName) {
			this.uploadName = uploadName;
		}

	}

}
