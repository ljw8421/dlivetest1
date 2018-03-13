package vo;

public class SalesVO {

	private String dnNo;
	private String dnSeq;
	private String itemCd;
	private String giQty;
	private String soNo;
	private String shipToParty;
	private String salesOrg;
	private String actualGiDt;
	private String bizGrp;
	private String giAmtLoc;
	private String insrtDt;
	private String updtDt;
	private String batchJobId;
	private String workDt;
	private String paramDt;
	private String trnsYn;
	
	public String getDnNo() {
		return dnNo;
	}
	public void setDnNo(String dnNo) {
		this.dnNo = dnNo;
	}
	public String getDnSeq() {
		return dnSeq;
	}
	public void setDnSeq(String dnSeq) {
		this.dnSeq = dnSeq;
	}
	public String getItemCd() {
		return itemCd;
	}
	public void setItemCd(String itemCd) {
		this.itemCd = itemCd;
	}
	public String getGiQty() {
		return giQty;
	}
	public void setGiQty(String giQty) {
		this.giQty = giQty;
	}
	public String getSoNo() {
		return soNo;
	}
	public void setSoNo(String soNo) {
		this.soNo = soNo;
	}
	public String getShipToParty() {
		return shipToParty;
	}
	public void setShipToParty(String shipToParty) {
		this.shipToParty = shipToParty;
	}
	public String getSalesOrg() {
		return salesOrg;
	}
	public void setSalesOrg(String salesOrg) {
		this.salesOrg = salesOrg;
	}
	public String getActualGiDt() {
		return actualGiDt;
	}
	public void setActualGiDt(String actualGiDt) {
		this.actualGiDt = actualGiDt;
	}
	public String getBizGrp() {
		return bizGrp;
	}
	public void setBizGrp(String bizGrp) {
		this.bizGrp = bizGrp;
	}
	public String getGiAmtLoc() {
		return giAmtLoc;
	}
	public void setGiAmtLoc(String giAmtLoc) {
		this.giAmtLoc = giAmtLoc;
	}
	public String getInsrtDt() {
		return insrtDt;
	}
	public void setInsrtDt(String insrtDt) {
		this.insrtDt = insrtDt;
	}
	public String getUpdtDt() {
		return updtDt;
	}
	public void setUpdtDt(String updtDt) {
		this.updtDt = updtDt;
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
	public String getParamDt() {
		return paramDt;
	}
	public void setParamDt(String paramDt) {
		this.paramDt = paramDt;
	}
	public String getTrnsYn() {
		return trnsYn;
	}
	public void setTrnsYn(String trnsYn) {
		this.trnsYn = trnsYn;
	}
	@Override
	public String toString() {
		return "SalesVO [dnNo=" + dnNo + ", dnSeq=" + dnSeq + ", itemCd=" + itemCd + ", giQty=" + giQty + ", soNo="
				+ soNo + ", shipToParty=" + shipToParty + ", salesOrg=" + salesOrg + ", actualGiDt=" + actualGiDt
				+ ", bizGrp=" + bizGrp + ", giAmtLoc=" + giAmtLoc + ", insrtDt=" + insrtDt + ", updtDt=" + updtDt
				+ ", batchJobId=" + batchJobId + ", workDt=" + workDt + ", paramDt=" + paramDt + ", trnsYn=" + trnsYn
				+ "]";
	}
	
}
