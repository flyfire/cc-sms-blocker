package zhs.betalee.ccSMSBlocker.database;

public class SmsData {
	
	public int id;
	private String formaddress;
	private String msgbody;
	private long fromtime;
	public String blockedrule;
	public int status;
	
	public SmsData(int id, String formaddress, String msgbody, long fromtime,
			String blockedrule, int status) {
		super();
		this.id = id;
		this.formaddress = formaddress;
		this.msgbody = msgbody;
		this.fromtime = fromtime;
		this.blockedrule = blockedrule;
		this.status = status;
	}
	
	public SmsData(String formaddress, String msgbody, long fromtime) {
		super();
		this.formaddress = formaddress;
		this.msgbody = msgbody;
		this.fromtime = fromtime;
	}

	/**
	 * @return the id
	 */
	public final int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public final void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the formaddress
	 */
	public final String getFormaddress() {
		return formaddress;
	}

	/**
	 * @param formaddress the formaddress to set
	 */
	public final void setFormaddress(String formaddress) {
		this.formaddress = formaddress;
	}

	/**
	 * @return the msgbody
	 */
	public final String getMsgbody() {
		return msgbody;
	}

	/**
	 * @param msgbody the msgbody to set
	 */
	public final void setMsgbody(String msgbody) {
		this.msgbody = msgbody;
	}

	/**
	 * @return the fromtime
	 */
	public final long getFromtime() {
		return fromtime;
	}

	/**
	 * @param fromtime the fromtime to set
	 */
	public final void setFromtime(long fromtime) {
		this.fromtime = fromtime;
	}

	/**
	 * @return the blockedrule
	 */
	public final String getBlockedrule() {
		return blockedrule;
	}

	/**
	 * @param blockedrule the blockedrule to set
	 */
	public final void setBlockedrule(String blockedrule) {
		this.blockedrule = blockedrule;
	}

	/**
	 * @return the status
	 */
	public final int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public final void setStatus(int status) {
		this.status = status;
	}
	


	
}
