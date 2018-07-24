/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meteoinfo.data.dataframe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.ReadablePeriod;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.meteoinfo.data.ArrayMath;
import org.meteoinfo.global.util.DateUtil;
import ucar.ma2.Array;

/**
 *
 * @author Yaqiang Wang
 */
public class DateTimeIndex extends Index<DateTime> {    
    // <editor-fold desc="Variables">
    ReadablePeriod period;
    ReadablePeriod resamplePeriod;
    DateTimeFormatter dtFormatter;
    // </editor-fold>
    // <editor-fold desc="Constructor">
    /**
     * Constructor
     */
    public DateTimeIndex(){
        this.setFormat("yyyy-MM-dd");
    }
    
    /**
     * Constructor
     * @param data Data
     */
    public DateTimeIndex(Array data){
        this(ArrayMath.asList(data));
    }
    
    /**
     * Constructor
     * @param data Data list
     */
    public DateTimeIndex(List data){
        this();
        if (data.get(0) instanceof Date) {
            this.values = new ArrayList<>();
            for (Date d : (List<Date>)data) {
                this.values.add(new DateTime(d));
            }
        } else {
            this.values = data;
        }
    }
    
    /**
     * Constructor
     * @param start Start time
     * @param end End time
     * @param freq Frequent
     */
    public DateTimeIndex(String start, String end, String freq) {
        this();
        DateTime sdt = DateUtil.getDateTime(start);
        DateTime edt = DateUtil.getDateTime(end);
        period = DateUtil.getPeriod(freq);
        this.setFormat(DateUtil.getDateFormat(period));
        this.values = DateUtil.getDateTimes(sdt, edt, period);
    }
    
    /**
     * Constructor
     * @param start Start time
     * @param tNum Date time number
     * @param freq Frequent
     */
    public DateTimeIndex(String start, int tNum, String freq) {
        this();
        DateTime sdt = DateUtil.getDateTime(start);
        period = DateUtil.getPeriod(freq);
        this.setFormat(DateUtil.getDateFormat(period));
        this.values = DateUtil.getDateTimes(sdt, tNum, period);
    }

    /**
     * Constructor
     * @param tNum Time number
     * @param end End time
     * @param freq Frequent
     */
    public DateTimeIndex(int tNum, String end, String freq) {
        this();
        DateTime edt = DateUtil.getDateTime(end);
        period = DateUtil.getPeriod(freq);
        this.setFormat(DateUtil.getDateFormat(period));
        this.values = DateUtil.getDateTimes(tNum, edt, period);
    }
    // </editor-fold>
    // <editor-fold desc="Get Set Methods">
    public ReadablePeriod getPeriod(){
        return this.period;
    }
    
    /**
     * Set period
     * @param value Period
     */
    public void setPeriod(ReadablePeriod value) {
        this.period = value;
        this.setFormat(DateUtil.getDateFormat(value));
    }
    
    /**
     * Get resample period
     * @return Resample period
     */
    public ReadablePeriod getResamplePeriod(){
        return this.resamplePeriod == null ? this.period : this.resamplePeriod;
    }
    
    /**
     * Set resample period
     * @param value Resample period
     */
    public void setResamplPeriod(ReadablePeriod value){
        this.resamplePeriod = value;
    }
    
    /**
     * Set string format
     * @param value String format
     */
    @Override
    public void setFormat(String value){
        super.setFormat(value);
        this.dtFormatter = DateTimeFormat.forPattern(format);
    }
    
    /**
     * Get Name format
     * @return 
     */
    @Override
    public String getNameFormat() {
        String str = ((DateTime)this.values.get(0)).toString(this.format);
        return "%" + String.valueOf(str.length()) + "s";
    }
    
    /**
     * Get date time formatter
     * @return Date time formatter
     */
    public DateTimeFormatter getDateTimeFormatter(){
        return this.dtFormatter;
    }
    
    /**
     * Set date time formatter
     * @param value Date time formatter
     */
    public void setDateTimeFormatter(DateTimeFormatter value){
        this.dtFormatter = value;
    }
    // </editor-fold>
    // <editor-fold desc="Methods">
    /**
     * Index of
     * @param d DateTime
     * @return Index
     */
    public int indexOf(DateTime d){
        return this.values.indexOf(d);
    }
    
    /**
     * Index of
     * @param d Date
     * @return Index
     */
    public int indexOf(Date d){
        DateTime dt = new DateTime(d);
        return this.values.indexOf(dt);
    }
    
    /**
     * Index of
     * @param d Date string
     * @return Index
     */
    public int indexOf(String d){
        DateTime dt = DateUtil.getDateTime(d);
        return this.values.indexOf(dt);
    }
    
    /**
     * Index of
     * @param ds Date list
     * @return Index list
     */
    @Override
    public List<Integer> indexOf(List ds){
        List<Integer> r = new ArrayList<>();
        if (ds.get(0) instanceof Date){
            for (Object d : ds){
                r.add(indexOf((Date)d));
            }
        } else if (ds.get(0) instanceof DateTime) {
            for (Object d : ds){
                r.add(indexOf((DateTime)d));
            }
        } else if (ds.get(0) instanceof String) {
            for (Object d : ds){
                r.add(indexOf((String)d));
            }
        }
        
        return r;
    }
    
    /**
     * Sub index
     * @return Index
     */
    @Override
    public DateTimeIndex subIndex(){
        DateTimeIndex r = new DateTimeIndex(this.values);
        r.setDateTimeFormatter(dtFormatter);
        return r;
    }
    
    /**
     * Sub index
     * @param idx Index list
     * @return Index
     */
    @Override
    public DateTimeIndex subIndex(List<Integer> idx){
        DateTimeIndex r = new DateTimeIndex();
        for (int i : idx)
            r.add(this.values.get(i));
        r.setDateTimeFormatter(dtFormatter);
        return r;
    }
    
    /**
     * Sub index
     * @param start Start index
     * @param end End index
     * @param step Step
     * @return Index
     */
    @Override
    public DateTimeIndex subIndex(int start, int end, int step) {
        List rv = new ArrayList<>();
        for (int i = start; i < end; i+=step){
            rv.add(this.values.get(i));
        }
        DateTimeIndex r = new DateTimeIndex(rv);
        r.setDateTimeFormatter(dtFormatter);
        return r;
    }
    
    /**
     * Get date values
     * @return Date values
     */
    public List<Date> getDateValues(){
        List<Date> vs = new ArrayList<>();
        for (DateTime dt : (List<DateTime>)this.values){
            vs.add(dt.toDate());
        }
        return vs;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("DateTimeIndex([");
        for (int i = 0; i < this.size(); i++){
            sb.append(toString(i));
            if (i < 100 && i < this.size() - 1) {
                sb.append(", ");
            } else {
                sb.append(", ...");
                break;
            }
        }
        sb.append("])");
        
        return sb.toString();
    }
    
    /**
     * Convert i_th index to string
     * @param idx Index i
     * @return String
     */
    @Override
    public String toString(int idx) {
        //return ((DateTime)this.values.get(idx)).toString(this.format);
        return this.dtFormatter.print((DateTime)this.values.get(idx));
    }
    // </editor-fold>
}
