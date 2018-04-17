package vo;

public class CsvFileVO {

	private String recordName;
	private String deptC;
	private String teamC;
	private String partC;
	private String type;
	private String memberC;
	private String recordNameM;
	private String targetMotherC;
	private String targerAmountC;
	
	public String getRecordName() {
		return recordName;
	}
	public void setRecordName(String recordName) {
		this.recordName = recordName;
	}
	public String getDeptC() {
		return deptC;
	}
	public void setDeptC(String deptC) {
		this.deptC = deptC;
	}
	public String getTeamC() {
		return teamC;
	}
	public void setTeamC(String teamC) {
		this.teamC = teamC;
	}
	public String getPartC() {
		return partC;
	}
	public void setPartC(String partC) {
		this.partC = partC;
	}
	public String getMemberC() {
		return memberC;
	}
	public void setMemberC(String memberC) {
		this.memberC = memberC;
	}
	public String getRecordNameM() {
		return recordNameM;
	}
	public void setRecordNameM(String recordNameM) {
		this.recordNameM = recordNameM;
	}
	public String getTargetMotherC() {
		return targetMotherC;
	}
	public void setTargetMotherC(String targetMotherC) {
		this.targetMotherC = targetMotherC;
	}
	public String getTargerAmountC() {
		return targerAmountC;
	}
	public void setTargerAmountC(String targerAmountC) {
		this.targerAmountC = targerAmountC;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return "CsvFileVO [recordName=" + recordName + ", deptC=" + deptC + ", teamC=" + teamC + ", partC=" + partC
				+ ", type=" + type + ", memberC=" + memberC + ", recordNameM=" + recordNameM + ", targetMotherC="
				+ targetMotherC + ", targerAmountC=" + targerAmountC + "]";
	}
	
}
