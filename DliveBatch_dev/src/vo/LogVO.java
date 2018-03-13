package vo;

public class LogVO {
	
	private String batchJobId;
	private String workJob;
	private String batchDt;
	private String status;
	private String batchDesc;
	
	public String getBatchJobId() {
		return batchJobId;
	}
	public void setBatchJobId(String batchJobId) {
		this.batchJobId = batchJobId;
	}
	public String getWorkJob() {
		return workJob;
	}
	public void setWorkJob(String workJob) {
		this.workJob = workJob;
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
		return "LogVO [batchJobId=" + batchJobId + ", workJob=" + workJob + ", batchDt=" + batchDt + ", status="
				+ status + ", batchDesc=" + batchDesc + "]";
	}
}
