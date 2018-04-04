package vo;

public class ActivityVO {
	
	private String activityId;
	private String activityNumber;
	private String accountId;
	private String accountName;
	private String activityFunctioncode;
	private String activityTypeCode;
	private String activityStartDate;
	private String activityEndDate;
	private String ownerId;
	private String ownerName;
	private String ownerEmailAddress;
	private String subject;
	private String activityDescription;
	private String leadId;
	private String leadName;
	private String opportunityId;
	private String opportunityName;
	private String primaryContactId;
	private String primaryContactName;
	private String priorityCode;
	private String visitResult_c;
	private String visitDate_c;
	private String initialDate_c;
	private String initialDateF_c;
	private String initialDateTextF_c;
	private String createdBy;
	private String creationDate;
	private String lastUpdateDate;
	private String lastUpdatedBy;
	private String actBranch_c;
	private String privateFlag;
	private String batchJobId;
	
	
	public String getPrivateFlag() {
		return privateFlag;
	}
	public void setPrivateFlag(String privateFlag) {
		this.privateFlag = privateFlag;
	}
	public String getActBranch_c() {
		return actBranch_c;
	}
	public void setActBranch_c(String actBranch_c) {
		this.actBranch_c = actBranch_c;
	}
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	public String getActivityNumber() {
		return activityNumber;
	}
	public void setActivityNumber(String activityNumber) {
		this.activityNumber = activityNumber;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getActivityFunctioncode() {
		return activityFunctioncode;
	}
	public void setActivityFunctioncode(String activityFunctioncode) {
		this.activityFunctioncode = activityFunctioncode;
	}
	public String getActivityTypeCode() {
		return activityTypeCode;
	}
	public void setActivityTypeCode(String activityTypeCode) {
		this.activityTypeCode = activityTypeCode;
	}
	public String getActivityStartDate() {
		return activityStartDate;
	}
	public void setActivityStartDate(String activityStartDate) {
		this.activityStartDate = activityStartDate;
	}
	public String getActivityEndDate() {
		return activityEndDate;
	}
	public void setActivityEndDate(String activityEndDate) {
		this.activityEndDate = activityEndDate;
	}
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public String getOwnerEmailAddress() {
		return ownerEmailAddress;
	}
	public void setOwnerEmailAddress(String ownerEmailAddress) {
		this.ownerEmailAddress = ownerEmailAddress;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getActivityDescription() {
		return activityDescription;
	}
	public void setActivityDescription(String activityDescription) {
		this.activityDescription = activityDescription;
	}
	public String getLeadId() {
		return leadId;
	}
	public void setLeadId(String leadId) {
		this.leadId = leadId;
	}
	public String getLeadName() {
		return leadName;
	}
	public void setLeadName(String leadName) {
		this.leadName = leadName;
	}
	public String getOpportunityId() {
		return opportunityId;
	}
	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}
	public String getOpportunityName() {
		return opportunityName;
	}
	public void setOpportunityName(String opportunityName) {
		this.opportunityName = opportunityName;
	}
	public String getPrimaryContactId() {
		return primaryContactId;
	}
	public void setPrimaryContactId(String primaryContactId) {
		this.primaryContactId = primaryContactId;
	}
	public String getPrimaryContactName() {
		return primaryContactName;
	}
	public void setPrimaryContactName(String primaryContactName) {
		this.primaryContactName = primaryContactName;
	}
	public String getPriorityCode() {
		return priorityCode;
	}
	public void setPriorityCode(String priorityCode) {
		this.priorityCode = priorityCode;
	}
	public String getVisitResult_c() {
		return visitResult_c;
	}
	public void setVisitResult_c(String visitResult_c) {
		this.visitResult_c = visitResult_c;
	}
	public String getVisitDate_c() {
		return visitDate_c;
	}
	public void setVisitDate_c(String visitDate_c) {
		this.visitDate_c = visitDate_c;
	}
	public String getInitialDate_c() {
		return initialDate_c;
	}
	public void setInitialDate_c(String initialDate_c) {
		this.initialDate_c = initialDate_c;
	}
	public String getInitialDateF_c() {
		return initialDateF_c;
	}
	public void setInitialDateF_c(String initialDateF_c) {
		this.initialDateF_c = initialDateF_c;
	}
	public String getInitialDateTextF_c() {
		return initialDateTextF_c;
	}
	public void setInitialDateTextF_c(String initialDateTextF_c) {
		this.initialDateTextF_c = initialDateTextF_c;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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
	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}
	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}
	public String getBatchJobId() {
		return batchJobId;
	}
	public void setBatchJobId(String batchJobId) {
		this.batchJobId = batchJobId;
	}
	
	@Override
	public String toString() {
		return "ActivityVO [activityId=" + activityId + ", activityNumber=" + activityNumber + ", accountId="
				+ accountId + ", accountName=" + accountName + ", activityFunctioncode=" + activityFunctioncode
				+ ", activityTypeCode=" + activityTypeCode + ", activityStartDate=" + activityStartDate
				+ ", activityEndDate=" + activityEndDate + ", ownerId=" + ownerId + ", ownerName=" + ownerName
				+ ", ownerEmailAddress=" + ownerEmailAddress + ", subject=" + subject + ", activityDescription="
				+ activityDescription + ", leadId=" + leadId + ", leadName=" + leadName + ", opportunityId="
				+ opportunityId + ", opportunityName=" + opportunityName + ", primaryContactId=" + primaryContactId
				+ ", primaryContactName=" + primaryContactName + ", priorityCode=" + priorityCode + ", visitResult_c="
				+ visitResult_c + ", visitDate_c=" + visitDate_c + ", initialDate_c=" + initialDate_c
				+ ", initialDateF_c=" + initialDateF_c + ", initialDateTextF_c=" + initialDateTextF_c + ", createdBy="
				+ createdBy + ", creationDate=" + creationDate + ", lastUpdateDate=" + lastUpdateDate
				+ ", lastUpdatedBy=" + lastUpdatedBy + ", actBranch_c=" + actBranch_c + ", privateFlag=" + privateFlag
				+ ", batchJobId=" + batchJobId + "]";
	}
	
}
