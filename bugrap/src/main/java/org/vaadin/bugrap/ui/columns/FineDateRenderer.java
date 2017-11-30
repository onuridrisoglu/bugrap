package org.vaadin.bugrap.ui.columns;

import java.io.ObjectInputStream.GetField;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.vaadin.ui.renderers.DateRenderer;

import elemental.json.JsonValue;

public class FineDateRenderer extends DateRenderer{

	private final int[] units = {Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_YEAR, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND};
	private final String[] unitTextValues = {"year", "month", "day", "hour", "minute", "second"};
	
	
	@Override
	public JsonValue encode(Date value) {
		return super.encode(getFineTextFromDate(value), String.class);
	}
	
	private String getFineTextFromDate(Date value) {
		StringBuffer presentationText = new StringBuffer();
		if (value == null)
			return presentationText.toString();
		
		long diffInMillis = (new Date()).getTime() - value.getTime();
		
		Calendar diff = Calendar.getInstance();
		diff.setTimeInMillis(diffInMillis - diff.get(Calendar.ZONE_OFFSET));
		
		int index = findMaxUnitIndex(diff);
		int timeValue = getTimeValueInIndexUnit(diff, index);
		
		presentationText.append(timeValue + " " + unitTextValues[index]) ;
		if (timeValue > 1)
			presentationText.append("s");
		
		presentationText.append(" ago");
		
		return presentationText.toString();
	}

	private int getTimeValueInIndexUnit(Calendar cal, int i) {
		int val = cal.get(units[i]);
		if (Calendar.YEAR == units[i]) // 1970 means 0
			val-=1970;
		return val;
	}

	private int findMaxUnitIndex(Calendar diff) {
		int index = 0;
		while (diff.get(units[index]) == 0 
				|| (units[index] == Calendar.YEAR && diff.get(units[index]) == 1970) 		// first year is 1970
				|| (units[index] == Calendar.DAY_OF_YEAR && diff.get(units[index]) == 1)  // first day is 1
				) {
			index++;
		}
		return index;
	}
}