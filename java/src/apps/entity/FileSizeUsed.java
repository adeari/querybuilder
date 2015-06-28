package apps.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_filesize_used")
public class FileSizeUsed implements Serializable {
	private static final long serialVersionUID = -6089635856765624147L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "user_id", unique = true, nullable = false)
	private Users userOwner;
	
	@Column(name = "filesize", nullable = false)
	private double filesize;
	
	@Column(name = "filesize_show", nullable = false)
	private String filesizeShow;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public Users getUserOwner() {
		return userOwner;
	}

	public void setUserOwner(Users userOwner) {
		this.userOwner = userOwner;
	}

	public double getFilesize() {
		return filesize;
	}

	public void setFilesize(double filesize) {
		this.filesize = filesize;
	}

	public String getFilesizeShow() {
		return filesizeShow;
	}

	public void setFilesizeShow(String filesizeShow) {
		this.filesizeShow = filesizeShow;
	}
	
	
}
