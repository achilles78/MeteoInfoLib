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
import org.meteoinfo.global.util.DateUtil;

/**
 *
 * @author Yaqiang Wang
 */
public class DateTimeIndex extends Index {    
    // <editor-fold desc="Variables">
    ReadablePeriod period;
    String format = "yyyy-MM-dd";
    // </editor-fold>
    // <editor-fold desc="Constructor">
    /**
     * Constructor
     */
    public DateTimeIndex(){
        
    }
    
    /**
     * Constructor
     * @param data Data list
     */
    public DateTimeIndex(List data){
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
        DateTime sdt = DateUtil.getDateTime(start);
        DateTime edt = DateUtil.getDateTime(end);
        period = DateUtil.getPeriod(freq);
        this.format = DateUtil.getDateFormat(period);
        this.values = DateUtil.getDateTimes(sdt, edt, period);
    }
    
    /**
     * Constructor
     * @param start Start time
     * @param tNum Date time number
     * @param freq Frequent
     */
    public DateTimeIndex(String start, int tNum, String freq) {
        DateTime sdt = DateUtil.getDateTime(start);
        period = DateUtil.getPeriod(freq);
        this.format = DateUtil.getDateFormat(period);
        this.values = DateUtil.getDateTimes(sdt, tNum, period);
    }

    /**
     * Constructor
     * @param tNum Time number
     * @param end End time
     * @param freq Frequent
     */
    public DateTimeIndex(int tNum, String end, String freq) {
        DateTime edt = DateUtil.getDateTime(end);
        period = DateUtil.getPeriod(freq);
        this.format = DateUtil.getDateFormat(period);
        this.values = DateUtil.getDateTimes(tNum, edt, period);
    }
    // </editor-fold>
    // <editor-fold desc="Get Set Methods">
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
        return r;
    }
    
    /**
     * Sub index
     * @param idx Index list
     * @return Index
     */
    @Override
    public Index subIndex(List<Integer> idx){
        List rv = new ArrayList<>();
        for (int i : idx)
            rv.add(this.values.get(i));
        return new DateTimeIndex(rv);
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
        return new DateTimeIndex(rv);
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
        return ((DateTime)this.values.get(idx)).toString(this.format);
    }
    // </editor-fold>
}
