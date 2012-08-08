package ftpupload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;

public class FTPControlSocket {

	private InetAddress remoteAddr;
	private Socket controlSocket;
	private String encoding;
	protected BufferedReader br;
	protected Writer writer;

	protected FTPControlSocket(InetAddress remoteAddr, int controlPort,
			int timeout, String encoding) throws IOException {
		this(remoteAddr, new Socket(remoteAddr, controlPort), timeout, encoding);
	}

	protected FTPControlSocket(InetAddress remoteAddr, Socket controlSock,
			int timeout, String encoding) throws IOException {
		this.controlSocket = controlSock;
		this.remoteAddr = remoteAddr;
		this.encoding = encoding;
		initStream();
		// setSoTimeOut(timeout);
		validateConnection();
	}
	
	

	private void validateConnection() throws IOException {

		FTPReply reply = readReply();
		String rightCode[] = { "220", "230" };
		validateReply(reply, rightCode);
	}

	private void initStream() {

		try {
			InputStream is = controlSocket.getInputStream();
			br = new BufferedReader(new InputStreamReader(is, encoding));
			writer = new OutputStreamWriter(controlSocket.getOutputStream(),
					encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setSoTimeOut(int millis) throws SocketException {
		controlSocket.setSoTimeout(millis);
	}

	public void close() throws IOException {

		controlSocket.close();
	}

	public void logout() {

		try {
			writer.close();
			br.close();
			controlSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FTPReply sendCommand(String command) throws IOException {
		writeCommand(command);
		return readReply();
	}

	void writeCommand(String command) throws IOException {

		writer.write(command + "\r\n");
		writer.flush();
	}

	public FTPReply readReply() throws IOException {

		String line;
		for (line = br.readLine(); line != null && line.length() == 0; line = br
				.readLine())
			;
		if (line == null)
			throw new IOException("Unexpected null reply received");

		if (line.length() < 3)
			throw new IOException("Short reply received");
		String replyCode = line.substring(0, 3);
		StringBuffer reply = new StringBuffer("");
		if (line.length() > 3)
			reply.append(line.substring(4));
		Vector dataLines = null;
		if (line.charAt(3) == '-') {
			dataLines = new Vector();
			for (boolean complete = false; !complete;) {
				line = br.readLine();
				if (line == null)
					throw new IOException("Unexpected null reply received");
				if (line.length() != 0) {

					if (line.length() > 3
							&& line.substring(0, 3).equals(replyCode)
							&& line.charAt(3) == ' ') {
						reply.append(line.substring(3));
						complete = true;
					} else {
						reply.append(" ").append(line);
						dataLines.addElement(line);
					}
				}
			}

		}
		if (dataLines != null) {
			String data[] = new String[dataLines.size()];
			dataLines.copyInto(data);
			return new FTPReply(replyCode, reply.toString(), data);
		} else {
			return new FTPReply(replyCode, reply.toString());
		}

	}

	public FTPReply validateReply(FTPReply reply, String[] rightCode) {

		String replyCode = reply.getReplyCode();
		for (int i = 0; i < rightCode.length; i++) {
			if (replyCode.equals(rightCode[i]))
				return reply;
		}

		return null;
	}

	protected FTPDataSocket createDataSocketPASV() throws Exception {

		FTPReply reply = sendCommand("PASV");
		String[] rightcode = { "227" };
		validateReply(reply, rightcode);
		String replyText = reply.getReplyText();
		int startIP = replyText.indexOf('(');
		int endIP = replyText.indexOf(')');

		if (startIP < 0) {
			for (startIP = 0; startIP < replyText.length()
					&& !Character.isDigit(replyText.charAt(startIP)); startIP++)
				;
			startIP--;
			// System.out.println("startIP:"+startIP);
		}
		if (endIP < 0) {
			for (endIP = replyText.length() - 1; endIP > 0
					&& !Character.isDigit(replyText.charAt(endIP)); endIP--)
				;
			if (++endIP >= replyText.length())
				replyText = replyText + ")";
			// System.out.println("endIP:"+endIP);
		}

		String ipData = replyText.substring(startIP + 1, endIP).trim();
		// System.out.println(ipData);
		int parts[] = new int[6];
		int len = ipData.length();
		int partCount = 0;
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < len && partCount <= 6; i++) {
			char ch = ipData.charAt(i);
			if (Character.isDigit(ch))
				buf.append(ch);
			else if (ch != ',' && ch != ' ')
				throw new Exception("Malformed PASV reply: " + replyText);
			if (ch == ',' || i + 1 == len)
				try {
					parts[partCount++] = Integer.parseInt(buf.toString());
					buf.setLength(0);
				} catch (NumberFormatException _ex) {
					throw new Exception("Malformed PASV reply: " + replyText);
				}
		}

		String ipAddress = parts[0] + "." + parts[1] + "." + parts[2] + "."
				+ parts[3];
		int tport = (parts[4] << 8) + parts[5];
		String hostIP = ipAddress;
		return new FTPDataSocket(new Socket(hostIP, tport));
	}

	public void stop() throws IOException {
		this.controlSocket.close();
	}

}
