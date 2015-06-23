package apps.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tb_users_query")
public class UsersQuery implements Serializable {
	private static final long serialVersionUID = -8768958995691885387L;
	
	
	@Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
	
	@ManyToOne
	@JoinColumn(name="user_id", nullable = true)
	private Users userData;
	
	@ManyToOne
	@JoinColumn(name="query_id", nullable = true)
	private QueryData queryData;
	
	public UsersQuery() {
		
	}
	
	public UsersQuery(Users userData1, QueryData queryData1) {
		userData = userData1;
		queryData = queryData1;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Users getUserData() {
		return userData;
	}

	public void setUserData(Users userData) {
		this.userData = userData;
	}

	public QueryData getQueryData() {
		return queryData;
	}

	public void setQueryData(QueryData queryData) {
		this.queryData = queryData;
	}
	
	
}
