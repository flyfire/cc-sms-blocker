
```

import android.net.Uri;

public class AnyblockResolver {
	public static final String AUTHORITY = "zhs.betalee.ccsmsblocker.AnyBlockProvider";
	/*
	 * 规则 DB_TABLE = "rules"
	 */
	// The incoming URI matches the Notes URI pattern
	public static final Uri RULES_CONTENT_URI = Uri.parse("content://zhs.betalee.ccsmsblocker.AnyBlockProvider/rules");
	public static final String[] RULES_PROJECTION = new String[] { "_id", "rule", "type", "remark", "notif", "isenabled" };
	public static final String DB_TABLE = "rules";
	public static final String KEY_ID = "_id";
	public static final String KEY_NAME = "rule";// 规则
	public static final String KEY_TYPE = "type";// 规则类型
	public static final String KEY_REMARK = "remark";// 备注
	public static final String KEY_NOTIF = "notif";// 1是0否通知
	public static final String KEY_ISENABLED = "isenabled";// 1启用0停用

	public static final int COLUMN_INDEX_KEY_ID           = 0;	
	public static final int COLUMN_INDEX_RULES_KEY_NAME = 1;
	public static final int COLUMN_INDEX_RULES_KEY_TYPE = 2;
	public static final int COLUMN_INDEX_RULES_KEY_REMARK = 3;
	public static final int COLUMN_INDEX_RULES_KEY_NOTIF = 4;
	public static final int COLUMN_INDEX_RULES_KEY_ISENABLED = 5;

	/*
	 * 已拦截短信 BLOCKED_MESSAGES_DB_TABLE = "blockedmessages"
	 */
	// The incoming URI matches the Notes URI pattern
	public static final Uri BLOCKEDMSG_CONTENT_URI = Uri.parse("content://zhs.betalee.ccsmsblocker.AnyBlockProvider/blockedmessages");
	public static final String[] BLOCKEDMSG_PROJECTION = new String[] { "_id", "number", "msgbody", "timestamp", "blockedrule", "status", "date2", "thread_id" };
	public static final String BLOCKED_MESSAGES_DB_TABLE = "blockedmessages";
	public static final String _ID = "_id";
	public static final String FORMADDRESS = "number";// 拦截号码
	public static final String MSGBODY = "msgbody";// 短信内容
	public static final String FORMTIME = "timestamp";// 发送时间
	public static final String BLOCKEDRULE = "blockedrule";// 拦截对应规则
	public static final String STATUS="status";// 1已读，0未读
	public static final String DATE2 = "date2";// 接收时间
	public static final String THREAD_ID = "thread_id";// 序号，同一发信人的id相同

	public static final int COLUMN_INDEX_BLOCKEDMSG_ID = 0;
	public static final int COLUMN_INDEX_BLOCKEDMSG_FORMADDRESS = 1;
	public static final int COLUMN_INDEX_BLOCKEDMSG_MSGBODY = 2;
	public static final int COLUMN_INDEX_BLOCKEDMSG_FORMTIME = 3;
	public static final int COLUMN_INDEX_BLOCKEDMSG_BLOCKEDRULE = 4;
	public static final int COLUMN_INDEX_BLOCKEDMSG_STATUS     = 5;
	public static final int COLUMN_INDEX_BLOCKEDMSG_DATE2 = 6;
	public static final int COLUMN_INDEX_BLOCKEDMSG_THREAD_ID = 7;
}
```