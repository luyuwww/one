package ftpupload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * 
 * @author BruceXX
 * 
 */

public class FTPClient {

	private int ControlPort;
	private String controlEncoding;
	private boolean cancelTransfer;
	private boolean detectTransferMode;
	private long monitorInterval;
	private int transferBufferSize;
	private FTPControlSocket control;
	private int timeout;
	protected InetAddress remoteAddress;
	private FTPReply lastReply;
	protected FTPProcessListener listener;
	private FTPDataSocket data;
	private boolean isASCII;

	private long startSize;
	private volatile boolean handling;
	private volatile boolean over;

	public FTPClient() {

		this.ControlPort = 21;
		this.controlEncoding = "uTF-8";
		this.cancelTransfer = false;
		this.detectTransferMode = true;
		this.monitorInterval = 10 * 1024L;
		this.transferBufferSize = 16384;
		this.handling = true;
		this.over = false;
	}

	/**
	 * 建立连接
	 * 
	 * @throws FTPException
	 * @throws SocketException
	 * @throws IOException
	 */

	public void connect() throws FTPException, SocketException, IOException {

		checkConnection(false);
		initalize(new FTPControlSocket(remoteAddress, ControlPort, timeout,
				controlEncoding));
	}

	/**
	 * 登陆
	 * 
	 * @param user
	 * @param password
	 * @throws FTPException
	 * @throws IOException
	 */
	public void login(String user, String password) throws FTPException,
			IOException {

		user(user);
		pass(password);

	}

	public void user(String user) throws FTPException, IOException {

		sendCommand("USER", user, "230,331");
	}

	public void pass(String pass) throws FTPException, IOException {

		sendCommand("PASS", pass, "230,202,332");
	}

	/**
	 * 进入某个目录
	 * 
	 * @param dir
	 * @throws FTPException
	 * @throws IOException
	 */

	public void cd(String dir) throws FTPException, IOException {

		sendCommand("CWD", dir, "250");

	}

	/**
	 * 创建文件夹
	 * 
	 * @param dir
	 * @throws FTPException
	 * @throws IOException
	 */

	public void mkdir(String dir) throws FTPException, IOException {

		sendCommand("MKD", dir, "200,250,257");
	}

	/**
	 * 退出连接
	 * 
	 * @throws FTPException
	 * @throws IOException
	 */
	public void quit() throws FTPException, IOException {
		sendCommand("QUIT", null, "221,226");

	}

	/**
	 * 获取远程文件的大小
	 * 
	 * @param remoteFile
	 * @return
	 * @throws FTPException
	 * @throws IOException
	 */

	public long getSize(String remoteFile) throws FTPException, IOException {

		checkConnection(true);
		FTPReply replay = control.sendCommand("SIZE " + remoteFile);
		String replyText = replay.getReplyText();
		int pos = replyText.indexOf(' ');
		if (pos > 0)
			replyText = replyText.substring(0, pos);
		try {
			return Long.parseLong(replyText);
		} catch (NumberFormatException e) {
			return 0L;
		}
	}

	/**
	 * 设置文件传输的类型,FTP有两种，一种是BINARY,第二种是ASCII,我试验了一下，貌似binary可以传任何类型的，不像网上说的那些文本之类的需要用ASCII来传输
	 * 
	 * @param type
	 * @throws FTPException
	 * @throws IOException
	 */

	private void sendTransferType(String type) throws FTPException, IOException {

		sendCommand("TYPE", type, "200");
	}

	/**
	 * 根据文件名自动选择传输类型
	 * 
	 * @param filename
	 * @throws FTPException
	 * @throws IOException
	 */

	public void autoChooseType(String filename) throws FTPException,
			IOException {

		if (detectTransferMode) {

			if (FTPFileType.matchASCII(filename)) {
				isASCII = true;
				sendTransferType("A");
			} else {
				isASCII = false;
				sendTransferType("I");
			}

		}
	}

	/**
	 * 来检验是否传输完毕
	 * 
	 * @throws FTPException
	 * @throws IOException
	 */

	public void validateTransfer() throws FTPException, IOException {

		handling = true;
		checkConnection(true);
		String[] rightCode = { "225", "226", "250", "426", "450" };
		FTPReply reply = control.readReply();
		String code = reply.getReplyCode();
		if ((code.equals("426") || code.equals("450")) && !cancelTransfer) {
			handling = false;
			throw new FTPException(reply);
		} else {

			lastReply = control.validateReply(reply, rightCode);
			handling = false;
			return;
		}

	}

	/**
	 * 从某个断点开始续传
	 * 
	 * @param startSize
	 * @throws FTPException
	 * @throws IOException
	 */

	public void restart(long startSize) throws FTPException, IOException {

		sendCommand("REST", startSize + "", "350");
	}

	/**
	 * 向服务器传输文件
	 * 
	 * @param remoteFile
	 * @throws FTPException
	 * @throws IOException
	 */

	public void storefile(String remoteFile) throws FTPException, IOException {

		sendCommand("STOR", remoteFile, "125,150,350");
	}

	/**
	 * 从服务器已有文件开始附加文件，续传可以用这个，也可以用STOR,不过STOR要先指定start后才可以
	 * 
	 * @param remoteFile
	 * @throws FTPException
	 * @throws IOException
	 */

	public void appendfile(String remoteFile) throws FTPException, IOException {

		sendCommand("APPE", remoteFile, "125,150,350");
	}

	/**
	 * 关闭文件传输流
	 */
	private void closeSocket() {

		try {
			if (data != null) {
				data.close();
				data = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭文件传输流，以及相关的输出流
	 * 
	 * @param out
	 */

	private void closeSocket(OutputStream out) {

		try {
			if (out != null)
				out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		closeSocket();
	}

	/**
	 * 初始化传输条件,即传输之前的指令
	 * 
	 * @param remoteFile
	 * @throws FTPException
	 */

	private void initTransfer(String remoteFile) throws FTPException {

		checkConnection(true);
		cancelTransfer = false;
		data = null;
		try {
			data = control.createDataSocketPASV();
			data.setSoTimeOut(timeout);
			startSize = getStartSize(remoteFile);
			if (!isASCII) {
				restart(startSize);
				storefile(remoteFile);
			} else {
				appendfile(remoteFile);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 传输文件整体过程
	 * 
	 * @param localFile
	 * @param remoteFile
	 */

	public void transfer(String localFile, String remoteFile) {
		try {
			Subtransfer(localFile, remoteFile);
			over = true;
			validateTransfer();
		} catch (FTPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 传输文件写入过程
	 * @param localFile
	 * @param remoteFile
	 */
	public void Subtransfer(String localFile, String remoteFile) {

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		long size = 0L;

		try {
			bis = new BufferedInputStream(new FileInputStream(localFile));
			initTransfer(remoteFile);
			bos = new BufferedOutputStream(new DataOutputStream(data
					.getOutputStream()), transferBufferSize * 2);
			// if(!isASCII)

			bis.skip(startSize);
			byte[] buffer = new byte[transferBufferSize];
			byte prevBuf[] = null;
			long monitorCount = 0L;
			int count = 0;
			int separatorPos = 0;
			byte[] linseparator = System.getProperty("line.separator")
					.getBytes();

			while ((count = bis.read(buffer)) > 0 && !cancelTransfer) {
				if (isASCII) {
					for (int i = 0; i < count; i++) {
						boolean found = true;
						int skip = 0;
						for (; separatorPos < linseparator.length
								&& i + separatorPos < count; separatorPos++) {
							if (buffer[i + separatorPos] != linseparator[separatorPos]) {
								found = false;
								break;
							}
							skip++;
						}

						if (found) {
							if (separatorPos == linseparator.length) {
								bos.write(13);
								bos.write(10);
								size += 2L;
								monitorCount += 2L;
								separatorPos = 0;
								i += skip - 1;
								prevBuf = null;
							} else {
								prevBuf = new byte[skip];
								for (int k = 0; k < skip; k++)
									prevBuf[k] = buffer[i + k];

							}
						} else {
							if (prevBuf != null) {
								bos.write(prevBuf);
								size += prevBuf.length;
								monitorCount += prevBuf.length;
								prevBuf = null;
							}
							bos.write(buffer[i]);
							size++;
							monitorCount++;
							separatorPos = 0;
						}
					}

				} else {
					bos.write(buffer, 0, count);
					size += count;
					monitorCount += count;
				}
				if (prevBuf != null) {
					bos.write(prevBuf);
					size += prevBuf.length;
					monitorCount += prevBuf.length;
				}
				/**
				 * 监听器转换数值,一定间隔内刷新数值
				 */
				if (listener != null && monitorCount > monitorInterval) {
					listener.getUploadSize(size);
					monitorCount = 0L;
				}

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (FTPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				if (bis != null)
					bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			closeSocket(bos);
			if (listener != null) {
				listener.getUploadSize(size);
			}
		}

	}

	private void sendCommand(String command, String commandafter,
			String rightCodeSplit) throws FTPException, IOException {
		checkConnection(true);
		if (commandafter == null)
			commandafter = "";
		String[] rightCode = rightCodeSplit.split(",");
		FTPReply reply = control.sendCommand(command + " " + commandafter);
		lastReply = control.validateReply(reply, rightCode);
	}

	/**
	 * 检验控制流
	 * 
	 * @return
	 */

	public boolean connected() {
		return control != null;
	}

	/**
	 * 检验连接
	 * 
	 * @param shouldBeConnected
	 * @throws FTPException
	 */

	protected void checkConnection(boolean shouldBeConnected)
			throws FTPException {

		if (shouldBeConnected && !connected()) {

			throw new FTPException("客户端还没有连上服务端");
		}
		if (!shouldBeConnected && connected()) {

			throw new FTPException("客户端已经连上,请求的动作必须在建立连接之前进行");
		} else
			return;
	}

	protected void initalize(FTPControlSocket control) throws SocketException {
		this.control = control;
		control.setSoTimeOut(timeout);
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) throws SocketException {

		this.timeout = timeout;
		if (control != null)
			control.setSoTimeOut(timeout);
	}

	public int getControlPort() {
		return ControlPort;
	}

	public void setControlPort(int controlPort) {
		ControlPort = controlPort;
	}

	public String getControlEncoding() {
		return controlEncoding;
	}

	public void setControlEncoding(String controlEncoding) {
		this.controlEncoding = controlEncoding;
	}

	public boolean isDetectTransferMode() {
		return detectTransferMode;
	}

	public void setDetectTransferMode(boolean detectTransferMode) {
		this.detectTransferMode = detectTransferMode;
	}

	public long getMonitorInterval() {
		return monitorInterval;
	}

	public void setMonitorInterval(long monitorInterval) {
		this.monitorInterval = monitorInterval;
	}

	public long getTransferBufferSize() {
		return transferBufferSize;
	}

	public void setTransferBufferSize(int transferBufferSize) {
		this.transferBufferSize = transferBufferSize;
	}

	public String getRemoteAddress() {
		return remoteAddress.getHostName();
	}

	public void setRemoteAddress(InetAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public void setRemoteAddress(String remotehost) {
		try {
			this.remoteAddress = InetAddress.getByName(remotehost);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public FTPProcessListener getListener() {
		return listener;
	}

	public void setListener(FTPProcessListener listener) {
		this.listener = listener;
	}

	/**
	 * 获取远程的文件大小,如果没有的话返回0
	 * 
	 * @param remoteFile
	 * @return
	 * @throws FTPException
	 * @throws IOException
	 */

	public long getStartSize(String remoteFile) throws FTPException,
			IOException {

		if (startSize == 0)
			return getSize(remoteFile);
		else
			return startSize;
	}

	public static void main(String args[]) {

		Thread temp = null;
		String remoteFolder = "";
		FTPClient client = new FTPClient();
		client.setRemoteAddress("localhost");
		try {
			client.setTimeout(10000);
			client.connect();
			client.login("BruceXX", "183320433");
			client.cd(" /");
			if (!remoteFolder.equals("")) {
				String[] t = remoteFolder.split("/");
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < t.length; i++) {
					sb.append(t[i] + "/");
					client.mkdir(sb.toString());
				}
				client.cd(remoteFolder);
			}
			final long size = client.getStartSize("ttplayer.exe");
			final FTPProcessMointor m = new FTPProcessMointor();
			client.setListener(m);
			client.autoChooseType("ttplayer.exe");
			temp = new Thread(new Runnable() {

				public void run() {
					// TODO Auto-generated method stub
					while (true) {
						// System.out.println(monitor.uploadSize);
						System.out.println("现文件大小:" + (m.uploadSize + size));
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				}

			});
			temp.start();
			client.transfer("F:\\ttplayer.exe", "ttplayer.exe");

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (FTPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setCancelTransfer(boolean cancelTransfer) {
		this.cancelTransfer = cancelTransfer;
	}

	public boolean isHandling() {
		return handling;
	}

	public boolean isOver() {
		return over;
	}

}
