package com.bidcrm.data.vo;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;




public class IcalUnit {

	
	public static enum RecurrenceUnit {
        DAY,
        WEEK,
        MONTH,
        YEAR;
    }

    public static enum RecurrenceInWeek {
        MONDAY(1, Calendar.MONDAY),
        TUESDAY(2, Calendar.TUESDAY),
        WEDNESDAY(4, Calendar.WEDNESDAY),
        THURSDAY(8, Calendar.THURSDAY),
        FRIDAY(16, Calendar.FRIDAY),
        SATURDAY(32, Calendar.SATURDAY),
        SUNDAY(64, Calendar.SUNDAY);

        private int value;
        
        private int calendarValue;

        private RecurrenceInWeek(int value, int calendarValue)
        {
            this.value = value;
            this.calendarValue = calendarValue;
        }
        
        public static RecurrenceInWeek getForDate(Date date)
        {
            RecurrenceInWeek inWeek = null;
                        
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(date);
            
            for(RecurrenceInWeek value : RecurrenceInWeek.values())
            {
                if(cal.get(Calendar.DAY_OF_WEEK) == value.calendarValue)
                {
                    inWeek = value;
                    break;
                }
            }
            
            return inWeek;
        }
        
        public int getValue() 
        {
            return value;
        }

        public int getCalendarValue() 
        {
            return calendarValue;
        }
    }

    public static enum RecurrenceInMonth {
        DAY_OF_WEEK,
        DAY_OF_MONTH;
    }

    public static enum RecurrenceType
    {
        NONE,
        FOREVER,
        NUMBER_OF_TIMES,
        UNTIL_DATE;
    }
}
