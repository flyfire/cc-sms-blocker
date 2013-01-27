package zhs.betalee.ccSMSBlocker.database;

public class Sms {

	private long fromtime;
	private String formaddress;
	private String msgbody;
	
	public Sms(String add,String msg,long time) {
		this.formaddress = add;
		this.msgbody = msg;
		this.fromtime = time;
	}

	public final long getFromTime() {
		return fromtime;
	}

	public final void setFromTime(long time) {
		this.fromtime = time;
	}

	public final String getFormAddress() {
		return formaddress;
	}

	public final void setFormAddress(String add) {
		this.formaddress = add;
	}

	public final String getMsgbody() {
		return msgbody;
	}

	public final void setMsgbody(String msg) {
		this.msgbody = msg;
	}
	
}
