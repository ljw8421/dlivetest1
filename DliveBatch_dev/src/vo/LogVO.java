package vo;

public class LogVO {
	
	private String batchJobId;
	private String workType;
	private String workJob;
	private String paramDt;
	private String batchDt;
	private String status;
	private String batchDesc;
	
	public String getBatchJobId() {
		return batchJobId;
	}
	public void setBatchJobId(String batchJobId) {
		this.batchJobId = batchJobId;
	}
	public String getWorkType() {
		return workType;
	}
	public void setWorkType(String workType) {
		this.workType = workType;
	}
	public String getWorkJob() {
		return workJob;
	}
	public void setWorkJob(String workJob) {
		this.workJob = workJob;
	}
	public String getParamDt() {
		return paramDt;
	}
	public void setParamDt(String paramDt) {
		this.paramDt = paramDt;
	}
	public String getBatchDt() {
		return batchDt;
	}
	public void setBatchDt(String batchDt) {
		this.batchDt = batchDt;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getBatchDesc() {
		return batchDesc;
	}
	public void setBatchDesc(String batchDesc) {
		this.batchDesc = batchDesc;
	}
	
	@Override
	public String toString() {
		return "LogVO [batchJobId=" + batchJobId + ", workType=" + workType + ", workJob=" + workJob + ", paramDt="
				+ paramDt + ", batchDt=" + batchDt + ", status=" + status + ", batchDesc=" + batchDesc + "]";
	}
	
}
