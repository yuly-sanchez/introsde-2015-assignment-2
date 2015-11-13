package introsde.lifecoach.converter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply=true)
public class DateConverter implements AttributeConverter<Date, String> {

	@Override
	public String convertToDatabaseColumn(Date toConvert) {
		System.out.println("--> Convert to database column <--");
		Calendar gc = new GregorianCalendar();
		gc.setTime(toConvert);
		return twoDigits(gc.get(Calendar.YEAR))+"-"+twoDigits(gc.get(Calendar.MONTH)+1)+"-"+twoDigits(gc.get(Calendar.DAY_OF_MONTH));
	}

	@Override
	public Date convertToEntityAttribute(String toParse) {
		System.out.println("--> Convert to entity attribute <--");
		try{
			return new SimpleDateFormat("yyyy-MM-dd").parse(toParse);
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	private String twoDigits(int num){
		int temp = num;
		int count = 0;
		do{
			temp /= 10;
			count++;
		}while(temp != 0);
		
		if(count == 1)
			return "0"+num;
		
		return ""+num;
	}

}
