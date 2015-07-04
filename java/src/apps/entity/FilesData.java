package apps.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_file")
public class FilesData implements Serializable {
	private static final long serialVersionUID = 6764923961089284334L;
	
	@Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
	
	@Column(name = "filename", unique = true, nullable = false)
    private String filename;
	
	@Column(name = "filetype", nullable = false, length = 50)
	private String filetype;

	@Column(name = "isdeleted")
	private boolean isDeleted;
	
	@Column(name = "filesize", nullable = false)
	private double filesize;
	
	@Column(name = "filesize_show", nullable = false)
	private String filesizeToShow;
	
	@Column(name = "download_link", nullable = false, length = 50)
	private String downloadLink;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFiletype() {
		return filetype;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public double getFilesize() {
		return filesize;
	}

	public void setFilesize(double filesize) {
		this.filesize = filesize;
	}

	public String getFilesizeToShow() {
		return filesizeToShow;
	}

	public void setFilesizeToShow(String filesizeToShow) {
		this.filesizeToShow = filesizeToShow;
	}

	public String getDownloadLink() {
		return downloadLink;
	}

	public void setDownloadLink(String downloadLink) {
		this.downloadLink = downloadLink;
	}

}
