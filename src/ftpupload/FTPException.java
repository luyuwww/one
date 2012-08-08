package ftpupload;

public class FTPException extends Exception {

	private int replyCode;

	public FTPException(String msg) {
		super(msg);
		replyCode = -1;
	}

	public FTPException(String msg, String replyCode) {
		super(msg);
		this.replyCode = -1;
		try {
			this.replyCode = Integer.parseInt(replyCode);
		} catch (NumberFormatException _ex) {
			this.replyCode = -1;
		}
	}

	public FTPException(FTPReply reply) {
		super(reply.getReplyText());
		replyCode = -1;
		try {
			replyCode = Integer.parseInt(reply.getReplyCode());
		} catch (NumberFormatException _ex) {
			replyCode = -1;
		}
	}

	public int getReplyCode() {
		return replyCode;
	}

}
