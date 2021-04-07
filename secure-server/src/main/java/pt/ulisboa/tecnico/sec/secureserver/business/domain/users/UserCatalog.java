package pt.ulisboa.tecnico.sec.secureserver.business.domain.users;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Repository
public class UserCatalog {

	@PersistenceContext
	private EntityManager em;

	public UserCatalog() { }
	
	public User getUserById(String userId) throws ApplicationException {
		try {
			TypedQuery<User> query = em.createNamedQuery(User.FIND_BY_USER_ID, User.class);
			query.setParameter(User.FIND_BY_USER_ID_USERID, userId);
			return query.getSingleResult();
		} catch(Exception e) {
			e.printStackTrace();
			throw new ApplicationException("Error on getUserById class UserCatalog");
		}
	}

}
