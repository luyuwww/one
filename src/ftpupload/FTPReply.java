package ftpupload;

public class FTPReply {

	private String replyCode;
	private String replyText;
	private String data[];

	FTPReply(String replyCode, String replyText) {
		this.replyCode = replyCode;
		this.replyText = replyText;
	}

	FTPReply(String replyCode, String replyText, String data[]) {
		this.replyCode = replyCode;
		this.replyText = replyText;
		this.data = data;
	}

	FTPReply(String rawReply) {
		rawReply = rawReply.trim();
		replyCode = rawReply.substring(0, 3);
		if (rawReply.length() > 3)
			replyText = rawReply.substring(4);
		else
			replyText = "";
	}

	public String getReplyCode() {
		return replyCode;
	}

	public String getReplyText() {
		return replyText;
	}

	public String[] getReplyData() {
		return data;
	}

}
