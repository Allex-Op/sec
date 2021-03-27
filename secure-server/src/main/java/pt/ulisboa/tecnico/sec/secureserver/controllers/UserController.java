package pt.ulisboa.tecnico.sec.secureserver.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pt.ulisboa.tecnico.sec.services.dto.ReportDTO;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;
import pt.ulisboa.tecnico.sec.services.interfaces.ISpecialUserService;

@RestController
public class UserController {
	
	private ISpecialUserService userService;

	@Autowired
	public UserController(ISpecialUserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/locations/{userID}/{epoch}")
	public ReportDTO obtainLocationReport(@PathVariable String userID, @PathVariable int epoch) {
		return this.userService.obtainLocationReport(userID, epoch);
	}
	
	@PostMapping("/locations/{userID}")
	public void submitLocationReport(@PathVariable String userID, @RequestBody int epoch, @RequestBody String report) throws ApplicationException {
		this.userService.submitLocationReport(userID, epoch, report);
	}
	
	@GetMapping("/locations/management/{x}/{y}/{epoch}")
	public List<String> obtainUsersAtLocation(@PathVariable int x, @PathVariable int y, @PathVariable int epoch) {
		return this.userService.obtainUsersAtLocation(x + "," + y, epoch);
	}
	
}
