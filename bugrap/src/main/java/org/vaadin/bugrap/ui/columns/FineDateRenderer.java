package org.vaadin.bugrap.ui.columns;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import com.vaadin.ui.renderers.DateRenderer;

import elemental.json.JsonValue;

public class FineDateRenderer extends DateRenderer{

	@Override
	public JsonValue encode(Date value) {
		return super.encode(getFineTextFromDate(value), String.class);
		
	}
	
	private static String getFineTextFromDate(Date value) {
		long durationAmount = 0;
		String durationUnitTxt = "";
		
		Instant now = Instant.now();
		Instant from = value.toInstant();
		
		if (ChronoUnit.DAYS.between(from, now) > 0) {
			Period period = Period.between(LocalDate.ofInstant(from, ZoneOffset.UTC), LocalDate.ofInstant(now, ZoneOffset.UTC));
			if (period.getYears() > 0) {
				durationAmount = period.getYears();
				durationUnitTxt = "year";
			}else if (period.getMonths() > 0) {
				durationAmount = period.getMonths();
				durationUnitTxt = "month";
			}else if (period.getDays() > 0) {
				durationAmount = period.getDays();
				durationUnitTxt = "day";
			}
		}else if (ChronoUnit.HOURS.between(from, now) > 0) {
			durationAmount = ChronoUnit.HOURS.between(from, now);
			durationUnitTxt = "hour";
		}else if (ChronoUnit.MINUTES.between(from, now) > 0) {
			durationAmount = ChronoUnit.MINUTES.between(from, now);
			durationUnitTxt = "minute";
		}
		
		if (durationAmount > 1)
			durationUnitTxt+="s";
		
		if (durationAmount == 0)
			return "now";
		else
			return durationAmount + " " + durationUnitTxt + " ago";
	}
}