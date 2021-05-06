package pt.ulisboa.tecnico.sec.services.dto;

import java.util.List;

public class RequestUserProofsDTO {

	private String userIdSender;
	private String userIdRequested;
	private List<Integer> epochs;
	
	/**
	 * @return the userIdSender
	 */
	public String getUserIdSender() {
		return userIdSender;
	}
	/**
	 * @param userIdSender the userIdSender to set
	 */
	public void setUserIdSender(String userIdSender) {
		this.userIdSender = userIdSender;
	}
	/**
	 * @return the userIdRequested
	 */
	public String getUserIdRequested() {
		return userIdRequested;
	}
	/**
	 * @param userIdRequested the userIdRequested to set
	 */
	public void setUserIdRequested(String userIdRequested) {
		this.userIdRequested = userIdRequested;
	}
	/**
	 * @return the epochs
	 */
	public List<Integer> getEpochs() {
		return epochs;
	}
	/**
	 * @param epochs the epochs to set
	 */
	public void setEpochs(List<Integer> epochs) {
		this.epochs = epochs;
	}
	
}
