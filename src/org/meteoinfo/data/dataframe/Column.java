/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meteoinfo.data.dataframe;

import java.text.DecimalFormat;
import org.meteoinfo.data.ArrayMath;
import org.meteoinfo.ma.ObjectDataType;
import org.meteoinfo.global.DataConvert;
import ucar.ma2.Array;
import ucar.ma2.DataType;

/**
 *
 * @author Yaqiang Wang
 */
public class Column {
    // <editor-fold desc="Variables">
    private String name;
    private DataType dataType;
    private String format;
    private int formatLen;
    private ObjectDataType objDataType = ObjectDataType.NULL;
    // </editor-fold>
    // <editor-fold desc="Constructor">
    /**
     * Constructor
     * @param name Name
     * @param dataType Data type
     */
    public Column(String name, DataType dataType) {
        this.name = name;
        this.dataType = dataType;
        this.updateFormat();
    }
    // </editor-fold>
    // <editor-fold desc="Get Set Methods">
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
    
    /**
     * Get data type
     * @return Data type
     */
    public DataType getDataType(){
        return this.dataType;
    }
    
    /**
     * Set data type
     * @param value Data type
     */
    public void setDataType(DataType value) {
        this.dataType = value;
    }
    
    /**
     * Get format
     * @return Format
     */
    public String getFormat(){
        return this.format;
    }
    
    /**
     * Get Name format
     * @return 
     */
    public String getNameFormat() {
        return "%" + String.valueOf(this.formatLen) + "s";
    }
    
    /**
     * Set format
     * @param value Format 
     */
    public void setFormat(String value){
        this.format = value;
    }
    
    /**
     * Get format length
     * @return Format length
     */
    public int getFormatLen(){
        return this.formatLen;
    }
    
    /**
     * Set format length
     * @param value Format length
     */
    public void setFormatLen(int value) {
        this.formatLen = value;
    }
    
    /**
     * Get object data type
     * @return Object data type
     */
    public ObjectDataType getObjectDataType(){
        return this.objDataType;
    }
    
    /**
     * Set object data type
     * @param value Object data type
     */
    public void setObjectDataType(ObjectDataType value){
        this.objDataType = value;
    }
    // </editor-fold>
    // <editor-fold desc="Methods">
    /**
     * Update format
     */
    public void updateFormat(){
        this.format = null;
        switch (this.dataType){
            case FLOAT:
            case DOUBLE:
                this.format = "%f";                
                break;
        }
        this.formatLen = this.name.length();
    }
    
    /**
     * Update format
     * @param data Data array
     */
    public void updateFormat(Array data) {
        this.formatLen = this.name.length();
        switch(this.dataType) {
            case DOUBLE:
            case FLOAT:
                double dmax = ArrayMath.max(data);
                DecimalFormat df = new DecimalFormat("0.0");
                df.setMaximumFractionDigits(6);
                int nf = 1, ci, nn;
                String str;
                for (int i = 0; i < data.getSize(); i++){
                    str = df.format(data.getDouble(i));
                    ci = str.indexOf(".");
                    nn = str.length() - ci - 1;
                    if (nf < nn) {
                        nf = nn;
                        if (nf == 6)
                            break;
                    }
                }
                String smax = df.format(dmax);              
                ci = smax.indexOf(".");
                int len = ci + nf + 2;
                formatLen = Math.max(formatLen, len);
                this.format = "%" + String.valueOf(formatLen) + "." + String.valueOf(nf) + "f";
                break;
            case INT:
                int imax = (int)ArrayMath.max(data);
                smax = Integer.toString(imax);
                formatLen = Math.max(formatLen, smax.length());
                this.format = "%" + String.valueOf(formatLen) + "d";
                break;
            default:                
                String v;
                for (int i = 0; i < data.getSize(); i++){
                    v = data.getObject(i).toString();
                    if (formatLen < v.length())
                        formatLen = v.length();
                }
                this.format = "%" + String.valueOf(formatLen) + "s";
                break;
        }
    }        
    
    /**
     * Convert input data to current data type
     *
     * @param value Object value
     * @return Result object
     */
    public Object convertTo(Object value) {
        return DataConvert.convertTo(value, this.dataType, this.format);
    }
    
    /**
     * Convert input data to current data type
     * @param s Input string
     * @return Result object
     */
    public Object convertStringTo(String s) {
        return DataConvert.convertStringTo(s, dataType, format);
    }
    
    @Override
    public String toString(){
        return this.name;
    }
    
    /**
     *
     * @return Column
     */
    @Override
    public Object clone() {
        Column col = new Column(this.name, this.dataType);
        col.setFormat(this.format);
        col.setFormatLen(this.formatLen);
        return col;
    }
    // </editor-fold>
}
