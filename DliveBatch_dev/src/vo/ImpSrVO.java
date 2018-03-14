package vo;

public class ImpSrVO {
	
	private String seq;
	private String title;
	private String serviceType_c;
	private String partyNumber;
	private String status;
	private String assigneePerson;
	private String dliveCloseDt_c;
	private String targetYN;
	private String creationDate;
	private String lastUpdateDate;
	
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getServiceType_c() {
		return serviceType_c;
	}
	public void setServiceType_c(String serviceType_c) {
		this.serviceType_c = serviceType_c;
	}
	public String getPartyNumber() {
		return partyNumber;
	}
	public void setPartyNumber(String partyNumber) {
		this.partyNumber = partyNumber;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAssigneePerson() {
		return assigneePerson;
	}
	public void setAssigneePerson(String assigneePerson) {
		this.assigneePerson = assigneePerson;
	}
	public String getDliveCloseDt_c() {
		return dliveCloseDt_c;
	}
	public void setDliveCloseDt_c(String dliveCloseDt_c) {
		this.dliveCloseDt_c = dliveCloseDt_c;
	}
	public String getTargetYN() {
		return targetYN;
	}
	public void setTargetYN(String targetYN) {
		this.targetYN = targetYN;
	}
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	public String getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	
	@Override
	public String toString() {
		return "ImpSrVO [seq=" + seq + ", title=" + title + ", serviceType_c=" + serviceType_c + ", partyNumber="
				+ partyNumber + ", status=" + status + ", assigneePerson=" + assigneePerson + ", dliveCloseDt_c="
				+ dliveCloseDt_c + ", targetYN=" + targetYN + ", creationDate=" + creationDate + ", lastUpdateDate="
				+ lastUpdateDate + "]";
	}
}
