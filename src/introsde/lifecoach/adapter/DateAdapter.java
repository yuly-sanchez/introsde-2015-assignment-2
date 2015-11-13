package introsde.lifecoach.adapter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter extends XmlAdapter<String, Date>{

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public Date unmarshal(String datestr) throws Exception {
		System.out.println("--> Unmarshal");
		return getFormattedDateFromString(datestr);
	}

	@Override
	public String marshal(Date date) throws Exception {
		System.out.println("--> Marshal");
		return sdf.format(date);
	}
	
	private Date getFormattedDateFromString(String targetDate){
		String[] dateComponent;
		Date d;
		
		dateComponent = targetDate.split("-");
		Calendar gc = new GregorianCalendar();
		int year = Integer.parseInt(dateComponent[0]);
		int month = Integer.parseInt(dateComponent[1]);
		int day = Integer.parseInt(dateComponent[2]);
		gc.set(GregorianCalendar.YEAR, year);
		gc.set(GregorianCalendar.MONTH, month - 1);
		gc.set(GregorianCalendar.DAY_OF_MONTH, day);
		gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
		gc.set(GregorianCalendar.MINUTE, 0);
		gc.set(GregorianCalendar.SECOND, 0);
		d = gc.getTime();
		
		return d;
	}
}