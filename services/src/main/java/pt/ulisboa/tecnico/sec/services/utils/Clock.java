package pt.ulisboa.tecnico.sec.services.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Clock {
	
	private Clock() {}

	public static LocalDate getDate() { return LocalDate.now(); }
	public static LocalTime getTime() { return LocalTime.now(); }
	public static LocalDateTime getDateTime() { return LocalDateTime.now(); }

}
