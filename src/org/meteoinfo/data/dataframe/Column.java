/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meteoinfo.data.dataframe;

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
     * Set format
     * @param value Format 
     */
    public void setFormat(String value){
        this.format = value;
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
    }
    
    @Override
    public String toString(){
        return this.name;
    }
    // </editor-fold>
}
