package vo;

public class ImpOpptyVO {

	private String Dseq;
	
	// oppty
	private String seq;
	private String Name;
	private String EffectiveDate;
	private String SalesStage;
	private String EmpNumber;
	private String PartyNumber;
	private String OpptyType_c;
	private String OptyBranch_c;
	private String OpptyTrnsYn;

	// account
	private String OrganizationName;
	private String Country;
	private String Province;
	private String State;
	private String City;
	private String AddressLine1;
	private String AddressLine2;
	private String BranchNm_c;
	private String ConaId_c;
	private String OpptyAccountTrnsYn;
	
	public String getDseq() {
		return Dseq;
	}

	public void setDseq(String dseq) {
		Dseq = dseq;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getEffectiveDate() {
		return EffectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		EffectiveDate = effectiveDate;
	}

	public String getSalesStage() {
		return SalesStage;
	}

	public void setSalesStage(String salesStage) {
		SalesStage = salesStage;
	}

	public String getEmpNumber() {
		return EmpNumber;
	}

	public void setEmpNumber(String empNumber) {
		EmpNumber = empNumber;
	}

	public String getPartyNumber() {
		return PartyNumber;
	}

	public void setPartyNumber(String partyNumber) {
		PartyNumber = partyNumber;
	}

	public String getOpptyType_c() {
		return OpptyType_c;
	}

	public void setOpptyType_c(String opptyType_c) {
		OpptyType_c = opptyType_c;
	}

	public String getOptyBranch_c() {
		return OptyBranch_c;
	}

	public void setOptyBranch_c(String optyBranch_c) {
		OptyBranch_c = optyBranch_c;
	}

	public String getOrganizationName() {
		return OrganizationName;
	}

	public void setOrganizationName(String organizationName) {
		OrganizationName = organizationName;
	}

	public String getCountry() {
		return Country;
	}

	public void setCountry(String country) {
		Country = country;
	}

	public String getProvince() {
		return Province;
	}

	public void setProvince(String province) {
		Province = province;
	}

	public String getState() {
		return State;
	}

	public void setState(String state) {
		State = state;
	}

	public String getCity() {
		return City;
	}

	public void setCity(String city) {
		City = city;
	}

	public String getAddressLine1() {
		return AddressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		AddressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return AddressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		AddressLine2 = addressLine2;
	}

	public String getBranchNm_c() {
		return BranchNm_c;
	}

	public void setBranchNm_c(String branchNm_c) {
		BranchNm_c = branchNm_c;
	}

	public String getConaId_c() {
		return ConaId_c;
	}

	public void setConaId_c(String conaId_c) {
		ConaId_c = conaId_c;
	}

	public String getOpptyTrnsYn() {
		return OpptyTrnsYn;
	}

	public void setOpptyTrnsYn(String opptyTrnsYn) {
		OpptyTrnsYn = opptyTrnsYn;
	}

	public String getOpptyAccountTrnsYn() {
		return OpptyAccountTrnsYn;
	}

	public void setOpptyAccountTrnsYn(String opptyAccountTrnsYn) {
		OpptyAccountTrnsYn = opptyAccountTrnsYn;
	}

	@Override
	public String toString() {
		return "ImpOpptyVO [Dseq=" + Dseq + ", seq=" + seq + ", Name=" + Name + ", EffectiveDate=" + EffectiveDate
				+ ", SalesStage=" + SalesStage + ", EmpNumber=" + EmpNumber + ", PartyNumber=" + PartyNumber
				+ ", OpptyType_c=" + OpptyType_c + ", OptyBranch_c=" + OptyBranch_c + ", OpptyTrnsYn=" + OpptyTrnsYn
				+ ", OrganizationName=" + OrganizationName + ", Country=" + Country + ", Province=" + Province
				+ ", State=" + State + ", City=" + City + ", AddressLine1=" + AddressLine1 + ", AddressLine2="
				+ AddressLine2 + ", BranchNm_c=" + BranchNm_c + ", ConaId_c=" + ConaId_c + ", OpptyAccountTrnsYn="
				+ OpptyAccountTrnsYn + "]";
	}

}
