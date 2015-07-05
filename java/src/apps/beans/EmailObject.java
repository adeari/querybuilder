package apps.beans;

public class EmailObject {
	private String emaiForm;
	private String emaiTo;
	private String subject;
	private String description;
	
	public EmailObject() {
		
	}
	
	public EmailObject(String emaiForm1, String emaiTo1, String subject1, String description1) {
		emaiForm = emaiForm1;
		emaiTo = emaiTo1;
		subject = subject1;
		description = description1;
	}
	
	public String getEmaiForm() {
		return emaiForm;
	}
	public void setEmaiForm(String emaiForm) {
		this.emaiForm = emaiForm;
	}
	public String getEmaiTo() {
		return emaiTo;
	}
	public void setEmaiTo(String emaiTo) {
		this.emaiTo = emaiTo;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
}
