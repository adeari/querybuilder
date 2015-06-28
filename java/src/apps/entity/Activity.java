package apps.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tb_activity")
public class Activity implements Serializable {
	private static final long serialVersionUID = -1033939854585349974L;
	
	@Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
	
	@Column(name = "query", nullable = false)
    private String query;
	
	@ManyToOne
	@JoinColumn(name = "user_created_id", nullable = false)
	private Users userCreated;
	
	@Column(name = "created_at", nullable = false)
	private Timestamp createdAt;
	
	@ManyToOne
	@JoinColumn(name = "file_id")
	private FilesData fileData;

	@Column(name = "done_at")
	private Timestamp doneAt;
	
	@Column(name = "notes")
	private String notes;
	
	@Column(name = "query_name", nullable = false,  length = 200)
	private String queryName;
	
	@Column(name = "filetype", nullable = false,  length = 200)
	private String filetype;
	
	@Column(name = "driver", nullable = false,  length = 200)
	private String driver;
	
	@Column(name = "connection_string", nullable = false)
	private String connectionString;
	
	public Activity(String queryName1, String query1, Users userCreated1, String filetype1, String driver1, String connectionString1) {
		query = query1;
		queryName = queryName1;
		userCreated = userCreated1;
		filetype = filetype1;
		createdAt = new Timestamp((new java.util.Date()).getTime());
		driver = driver1;
		connectionString = connectionString1;
	}
	
	public Activity() {
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Users getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(Users userCreated) {
		this.userCreated = userCreated;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public FilesData getFileData() {
		return fileData;
	}

	public void setFileData(FilesData fileData) {
		this.fileData = fileData;
	}

	public Timestamp getDoneAt() {
		return doneAt;
	}

	public void setDoneAt(Timestamp doneAt) {
		this.doneAt = doneAt;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public String getFiletype() {
		return filetype;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}


	public String getConnectionString() {
		return connectionString;
	}

	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}

}
