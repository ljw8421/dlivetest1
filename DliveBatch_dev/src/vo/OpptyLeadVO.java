package vo;

public class OpptyLeadVO {
	private String optyId;
	private String optyLeadId;
	private String leadId;
	private String batchJobId;
	
	public String getLeadId() {
		return leadId;
	}
	public void setLeadId(String leadId) {
		this.leadId = leadId;
	}
	public String getOptyId() {
		return optyId;
	}
	public void setOptyId(String optyId) {
		this.optyId = optyId;
	}
	public String getOptyLeadId() {
		return optyLeadId;
	}
	public void setOptyLeadId(String optyLeadId) {
		this.optyLeadId = optyLeadId;
	}
	public String getBatchJobId() {
		return batchJobId;
	}
	public void setBatchJobId(String batchJobId) {
		this.batchJobId = batchJobId;
	}
	
	@Override
	public String toString() {
		return "OpptyLeadVO [optyId=" + optyId + ", optyLeadId=" + optyLeadId + ", leadId=" + leadId + ", batchJobId="
				+ batchJobId + "]";
	}
	
}
