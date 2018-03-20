package vo;

public class ImpSrVO {
	
	private String seq;
	private String title;
	private String serviceType_c;
	private String partyNumber;
	private String partyId;
	private String status;
	private String assigneePerson;
	private String dliveCloseDt_c;
	private String problemDescription;
	private String trnsYN;
	private String importDate;
	private String trnsDate;
	
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
	public String getPartyId() {
		return partyId;
	}
	public void setPartyId(String partyId) {
		this.partyId = partyId;
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
	public String getProblemDescription() {
		return problemDescription;
	}
	public void setProblemDescription(String problemDescription) {
		this.problemDescription = problemDescription;
	}
	public String getTrnsYN() {
		return trnsYN;
	}
	public void setTrnsYN(String trnsYN) {
		this.trnsYN = trnsYN;
	}
	public String getImportDate() {
		return importDate;
	}
	public void setImportDate(String importDate) {
		this.importDate = importDate;
	}
	public String getTrnsDate() {
		return trnsDate;
	}
	public void setTrnsDate(String trnsDate) {
		this.trnsDate = trnsDate;
	}
	@Override
	public String toString() {
		return "ImpSrVO [seq=" + seq + ", title=" + title + ", serviceType_c=" + serviceType_c + ", partyNumber="
				+ partyNumber + ", partyId=" + partyId + ", status=" + status + ", assigneePerson=" + assigneePerson
				+ ", dliveCloseDt_c=" + dliveCloseDt_c + ", problemDescription=" + problemDescription + ", trnsYN="
				+ trnsYN + ", importDate=" + importDate + ", trnsDate=" + trnsDate + "]";
	}
}
