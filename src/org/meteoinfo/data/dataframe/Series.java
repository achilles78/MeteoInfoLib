/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meteoinfo.data.dataframe;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.joda.time.ReadablePeriod;
import org.meteoinfo.data.dataframe.impl.Aggregation;
import org.meteoinfo.data.dataframe.impl.Grouping;
import org.meteoinfo.data.dataframe.impl.KeyFunction;
import org.meteoinfo.data.dataframe.impl.Views;
import org.meteoinfo.data.dataframe.impl.WindowFunction;
import org.meteoinfo.global.util.DateUtil;
import ucar.ma2.Array;

/**
 *
 * @author Yaqiang Wang
 */
public class Series implements Iterable{
    // <editor-fold desc="Variables">
    private Index index; 
    private Array data;    //One dimension array
    private String name;
    private Grouping groups;
    // </editor-fold>
    // <editor-fold desc="Constructor">
    /**
     * Constructor
     * @param data Data array
     * @param index Index
     * @param name Name
     * @param groups Groups
     */
    public Series(Array data, Index index, String name, Grouping groups) {
        this.data = data;
        this.index = index;
        this.name = name;
        this.groups = groups;
    }
    
    /**
     * Constructor
     * @param data Data array
     * @param index Index
     * @param name Name
     */
    public Series(Array data, Index index, String name) {
        this.data = data;
        this.index = index;
        this.name = name;
        this.groups = new Grouping();
    }
    
    /**
     * Constructor
     * @param data Data array
     * @param idxValue Index value
     * @param name Name
     */
    public Series(Array data, List idxValue, String name) {
        this(data, Index.factory(idxValue), name);
    }
    
    /**
     * Constructor
     * @param data Data array
     * @param name name
     */
    public Series(Array data, String name) {        
        this(data, new Index((int)data.getSize()), name);
    }
    // </editor-fold>
    // <editor-fold desc="Get Set Methods">
    /**
     * Get data array
     * @return Data array
     */
    public Array getData(){
        return this.data;
    }
    
    /**
     * Set data array
     * @param value Data array
     */
    public void setData(Array value){
        this.data = value;
    }
    
    /**
     * Get index
     * @return Index
     */
    public Index getIndex(){
        return this.index;
    }
    
    /**
     * Set index
     * @param value Index
     */
    public void setIndex(Index value){
        this.index = value;
    }
    
    /**
     * Set index
     * @param value Index value
     */
    public void setIndex(List value) {
        this.index = new Index(value);
    }
    
    /**
     * Get name
     * @return Name
     */
    public String getName(){
        return this.name;
    }
    
    /**
     * Set name
     * @param value Name
     */
    public void setName(String value){
        this.name = value;
    }
    // </editor-fold>
    // <editor-fold desc="Methods">
    /**
     * Get if the series contains no data
     * @return Boolean
     */
    public boolean isEmpty(){
        return this.size() == 0;
    }
    
    /**
     * Get a data value
     * @param i Index
     * @return Data value
     */
    public Object getValue(int i) {
        return this.data.getObject(i);
    }
    
    /**
     * Get a index value
     * @param i Index
     * @return Index value
     */
    public Object getIndexValue(int i) {
        return this.index.get(i);
    }
    
    @Override
    public Iterator iterator() {
        return iterrows();
    }
    
    public ListIterator<List<Object>> iterrows() {
        return new Views.ListView<>(this).listIterator();
    }
    
    /**
     * Get size
     * @return Size
     */
    public int size(){
        return this.index.size();
    }
    
    /**
     * Group the series rows using the specified key function.
     *
     * @param function the function to reduce rows to grouping keys
     * @return the grouping
     */
    public Series groupBy(final KeyFunction function) {
        return new Series(                
                data,
                index,
                name,
                new Grouping(this, function)
            );
    }
    
    /**
     * Group the series rows using the specified key function.
     *
     * @return the grouping
     */
    public Series groupBy() {
        return new Series(
            data,
            index,
            name,
            new Grouping(this)
        );
    }
    
    /**
     * Group the data frame rows using the specified key function.
     *
     * @param function the function to reduce rows to grouping keys
     * @return the grouping
     */
    public Series groupByIndex(final WindowFunction function) {
        ((DateTimeIndex)index).setResamplPeriod(function.getPeriod());
        return new Series(
                data,
                index,
                name,
                new Grouping(this, function)
            );
    }
    
    /**
     * Group the data frame rows using the specified key function.
     *
     * @param pStr Period string
     * @return the grouping
     */
    public Series groupByIndex(final String pStr) {
        ReadablePeriod period = DateUtil.getPeriod(pStr);
        WindowFunction function = new WindowFunction(period);
        return groupByIndex(function);
    }
    
     /**
     * Compute the mean of the numeric columns for each group
     * or the entire data frame if the data is not grouped.
     *
     * @return the new series
     */
    public Series mean() {
        Series r = groups.apply(this, new Aggregation.Mean());
        if (this.index instanceof DateTimeIndex)
            ((DateTimeIndex)r.getIndex()).setPeriod(((DateTimeIndex)this.index).getResamplePeriod());
        return r;
    }
    
    /**
     * Convert to string - head
     *
     * @param n Head row number
     * @return The string
     */
    public String head(int n) {
        StringBuilder sb = new StringBuilder();
        int rn = this.index.size();
        if (n > rn) {
            n = rn;
        }
        for (int r = 0; r < n; r++) {
            sb.append(this.index.toString(r));
            sb.append("\t");
            sb.append(this.data.getObject(r).toString());
            sb.append("\n");
        }
        if (n < rn) {
            sb.append("...");
        }

        return sb.toString();
    }
    
    /**
     * Convert to string - tail
     *
     * @param n Tail row number
     * @return The string
     */
    public String tail(int n) {
        StringBuilder sb = new StringBuilder();
        int rn = this.index.size();
        if (n > rn) {
            n = rn;
        }
        for (int r = rn - n; r < rn; r++) {
            sb.append(this.index.toString(r));
            sb.append("\t");
            sb.append(this.data.getObject(r).toString());
            sb.append("\n");
        }

        return sb.toString();
    }
    
    @Override
    public String toString(){
        return head(100);
    }
    // </editor-fold>
}
