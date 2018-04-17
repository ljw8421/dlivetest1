package vo;

public class CodeVO {

	private String cd_id;
	private String type;
	private String type_name;
	private String cd_name;
	private String cd_val;
	private String seq;
	private String use_yn;
	
	public String getCd_id() {
		return cd_id;
	}
	public void setCd_id(String cd_id) {
		this.cd_id = cd_id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getType_name() {
		return type_name;
	}
	public void setType_name(String type_name) {
		this.type_name = type_name;
	}
	public String getCd_name() {
		return cd_name;
	}
	public void setCd_name(String cd_name) {
		this.cd_name = cd_name;
	}
	public String getCd_val() {
		return cd_val;
	}
	public void setCd_val(String cd_val) {
		this.cd_val = cd_val;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getUse_yn() {
		return use_yn;
	}
	public void setUse_yn(String use_yn) {
		this.use_yn = use_yn;
	}
	
	@Override
	public String toString() {
		return "CodeVO [cd_id=" + cd_id + ", type=" + type + ", type_name=" + type_name + ", cd_name=" + cd_name
				+ ", cd_val=" + cd_val + ", seq=" + seq + ", use_yn=" + use_yn + "]";
	}
}
