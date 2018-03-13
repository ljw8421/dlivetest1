package vo;

public class ResourcesVO {
	
	private String resourceProfileId;
	private String partyId;
	private String partyName;
	private String partyNumber;
	private String roles;
	private String emailAddress;
	private String manager;
	private String userName;
	private String organizations;
	private String managerId;
	private String dliveBranchCode;

	private String selectLv1;
	private String lv1;
	private String lv2;
	private String lv3;
	private String lv4;
	private String lv5;
	private String jobId;
	private String jobMdt;
	
	private String batchJobId;
	private String workDt;
	
	public String getResourceProfileId() {
		return resourceProfileId;
	}
	public void setResourceProfileId(String resourceProfileId) {
		this.resourceProfileId = resourceProfileId;
	}
	public String getPartyId() {
		return partyId;
	}
	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}
	public String getPartyName() {
		return partyName;
	}
	public void setPartyName(String partyName) {
		this.partyName = partyName;
	}
	public String getPartyNumber() {
		return partyNumber;
	}
	public void setPartyNumber(String partyNumber) {
		this.partyNumber = partyNumber;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getOrganizations() {
		return organizations;
	}
	public void setOrganizations(String organizations) {
		this.organizations = organizations;
	}
	public String getManagerId() {
		return managerId;
	}
	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}
	public String getDliveBranchCode() {
		return dliveBranchCode;
	}
	public void setDliveBranchCode(String dliveBranchCode) {
		this.dliveBranchCode = dliveBranchCode;
	}
	public String getSelectLv1() {
		return selectLv1;
	}
	public void setSelectLv1(String selectLv1) {
		this.selectLv1 = selectLv1;
	}
	public String getLv1() {
		return lv1;
	}
	public void setLv1(String lv1) {
		this.lv1 = lv1;
	}
	public String getLv2() {
		return lv2;
	}
	public void setLv2(String lv2) {
		this.lv2 = lv2;
	}
	public String getLv3() {
		return lv3;
	}
	public void setLv3(String lv3) {
		this.lv3 = lv3;
	}
	public String getLv4() {
		return lv4;
	}
	public void setLv4(String lv4) {
		this.lv4 = lv4;
	}
	public String getLv5() {
		return lv5;
	}
	public void setLv5(String lv5) {
		this.lv5 = lv5;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getJobMdt() {
		return jobMdt;
	}
	public void setJobMdt(String jobMdt) {
		this.jobMdt = jobMdt;
	}
	public String getBatchJobId() {
		return batchJobId;
	}
	public void setBatchJobId(String batchJobId) {
		this.batchJobId = batchJobId;
	}
	public String getWorkDt() {
		return workDt;
	}
	public void setWorkDt(String workDt) {
		this.workDt = workDt;
	}
	
	@Override
	public String toString() {
		return "ResourcesVO [resourceProfileId=" + resourceProfileId + ", partyId=" + partyId + ", partyName="
				+ partyName + ", partyNumber=" + partyNumber + ", roles=" + roles + ", emailAddress=" + emailAddress
				+ ", manager=" + manager + ", userName=" + userName + ", organizations=" + organizations
				+ ", managerId=" + managerId + ", dliveBranchCode=" + dliveBranchCode + ", selectLv1=" + selectLv1
				+ ", lv1=" + lv1 + ", lv2=" + lv2 + ", lv3=" + lv3 + ", lv4=" + lv4 + ", lv5=" + lv5 + ", jobId="
				+ jobId + ", jobMdt=" + jobMdt + ", batchJobId=" + batchJobId + ", workDt=" + workDt + "]";
	}
	
}
