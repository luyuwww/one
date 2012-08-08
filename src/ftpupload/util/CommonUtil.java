package ftpupload.util;

import java.util.UUID;

/**
 * 陆宇
 */
public class CommonUtil {
	public static synchronized String createPrimaryKey() {
		return String.valueOf(System.currentTimeMillis()+UUID.randomUUID().toString().replaceAll("-", ""));
	}



}