package pt.ulisboa.tecnico.sec.secureserver.business.domain.users;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class UserCatalog {
	
	private Map<String, User> users = new HashMap<>();
	
	public UserCatalog() {
		// dummy implementation for test purposes
		users.put("1", new User("1"));
		users.put("2", new User("2"));
		users.put("3", new User("3"));
		users.put("4", new User("4"));
		User special = new User("5");
		special.setAsSpecialUser();
		users.put("5", special);
		
	}
	
	public User getUserById(String userId) {
		return users.get(userId);
	}

}
