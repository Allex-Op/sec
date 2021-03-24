package pt.ulisboa.tecnico.sec.secureserver.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;
import pt.ulisboa.tecnico.sec.services.interfaces.ISpecialUserService;

@RestController
public class UserController {
	
	private ISpecialUserService userService;

	@Autowired
	public UserController(ISpecialUserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/locations/{id}")
	public String obtainLocationReport(@PathVariable String userID, @RequestBody int epoch) {
		return this.userService.obtainLocationReport(userID, epoch);
	}
	
	@PostMapping("/locations/{id}")
	public void submitLocationReport(@PathVariable String userID, @RequestBody int epoch, @RequestBody String report) throws ApplicationException {
		this.userService.submitLocationReport(userID, epoch, report);
	}
	
	@GetMapping("/locations")
	public List<String> obtainUsersAtLocation(@RequestBody String pos, @RequestBody int epoch) {
		return this.userService.obtainUsersAtLocation(pos, epoch);
	}
	
}
