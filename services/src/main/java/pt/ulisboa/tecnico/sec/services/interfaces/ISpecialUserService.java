package pt.ulisboa.tecnico.sec.services.interfaces;

import java.util.List;

public interface ISpecialUserService extends IUserService {
	
	public List<String> obtainUsersAtLocation(String pos, int epoch);

}
