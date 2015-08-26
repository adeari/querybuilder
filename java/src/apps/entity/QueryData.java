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
@Table(name = "tb_query")
public class QueryData implements Serializable  {
	private static final long serialVersionUID = 8396662600168051326L;

	@Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
	
	@Column(name = "driver", nullable = false, length = 200)
    private String driver;
	
	@Column(name = "connection_string", nullable = false)
	private String connectionString;
	
	@Column(name = "sql_query", unique = true, nullable = false)
	private String sql;
	
	@ManyToOne
	@JoinColumn(name="created_by", nullable = true)
	private Users createdBy;
	
	@ManyToOne
	@JoinColumn(name="modified_by", nullable = true)
	private Users modifiedBy;
	
	@Column(name = "created_at")
	private Timestamp createdAt;
	
	@Column(name = "modified_at")
	private Timestamp modifiedAt;
	
	@Column(name = "isdeleted")
	private boolean isDeleted;
	

	@Column(name = "named", unique = true, nullable = false, length = 200)
	private String named;
	
	public QueryData(String driver1, String connectionString1, String named1, String sql1,
			Users createdBy1, Users modifiedBy1, java.util.Date createdAt1
			, java.util.Date modifiedAt1) {
		driver = driver1;
		connectionString = connectionString1;
		named = named1;
		sql = sql1;
		createdBy = createdBy1;
		modifiedBy = modifiedBy1;
		createdAt = new java.sql.Timestamp(createdAt1.getTime());
		modifiedAt = new java.sql.Timestamp(modifiedAt1.getTime());
		isDeleted = true;
	}
	
	public QueryData() {
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public Users getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Users createdBy) {
		this.createdBy = createdBy;
	}

	public Users getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Users modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(Timestamp modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public String getNamed() {
		return named;
	}

	public void setNamed(String named) {
		this.named = named;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	
}
