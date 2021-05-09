package pt.ulisboa.tecnico.sec.services.dto;

import java.util.List;
import java.util.Objects;

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

	@Override
	public String toString() {
		return "Request User Proofs sent by " + userIdSender + " for user " + userIdRequested + " at epochs " + epochs.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RequestUserProofsDTO that = (RequestUserProofsDTO) o;
		return userIdSender.equals(that.userIdSender) && userIdRequested.equals(that.userIdRequested) && epochs.equals(that.epochs);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userIdSender, userIdRequested, epochs);
	}
}
