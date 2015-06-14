package apps.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_users")
public class Users implements Serializable {
	private static final long serialVersionUID = -6562694591463283825L;

	@Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
	
	@Column(name = "username", unique = true, nullable = false, length = 200)
    private String username;
	
	@Column(name = "pass", nullable = false, length = 200)
	private String pass;
	
	@Column(name = "last_login")
	private Timestamp last_login;
	
	@Column(name = "isdeleted")
	private boolean isdeleted;
	
	@Column(name = "divisi", nullable = false, length = 200)
	private String divisi;
	
	public Users() {
		
	}
	
	public Users(String username1, String pass1, String divisi1, boolean isdeleted1) {
		username = username1;
		pass = pass1;
		divisi = divisi1;
		isdeleted = isdeleted1;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public Timestamp getLast_login() {
		return last_login;
	}

	public void setLast_login(Timestamp last_login) {
		this.last_login = last_login;
	}
	
	public void setLast_loginAsDate(java.util.Date last_login) {
		this.last_login = new java.sql.Timestamp(last_login.getTime());
	}

	public boolean isIsdeleted() {
		return isdeleted;
	}

	public void setIsdeleted(boolean isdeleted) {
		this.isdeleted = isdeleted;
	}

	public String getDivisi() {
		return divisi;
	}

	public void setDivisi(String divisi) {
		this.divisi = divisi;
	}
}
