/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meteoinfo.data.dataframe;

import java.util.List;
import ucar.ma2.Array;

/**
 *
 * @author Yaqiang Wang
 */
public class Series {
    // <editor-fold desc="Variables">
    private Index index; 
    private Array data;    //One dimension array
    private String name;
    // </editor-fold>
    // <editor-fold desc="Constructor">
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
    }
    
    /**
     * Constructor
     * @param data Data array
     * @param idxValue Index value
     * @param name Name
     */
    public Series(Array data, List idxValue, String name) {
        this(data, new Index(idxValue), name);
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
     * Get size
     * @return Size
     */
    public int size(){
        return this.index.size();
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
