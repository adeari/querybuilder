package apps.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tb_user_activity")
public class UserActivity implements Serializable {
	private static final long serialVersionUID = 3616636225156577759L;
	
	@Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
	
	@Column(name = "created_at", nullable = false)
	private Timestamp createdAt;
	
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private Users userCreated;
	
	@Column(name = "notes", nullable = false)
	private String notes;
	
	public UserActivity() {
		
	}
	
	public UserActivity(Users userCreated1, String notes1) {
		createdAt = new Timestamp((new Date()).getTime());
		userCreated = userCreated1;
		notes = notes1;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Users getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(Users userCreated) {
		this.userCreated = userCreated;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	
}
