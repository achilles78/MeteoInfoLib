/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meteoinfo.data.dataframe;

import java.util.ArrayList;
import java.util.List;
import ucar.ma2.DataType;

/**
 *
 * @author Yaqiang Wang
 */
public class ColumnCollection extends ArrayList<Column> {
    // <editor-fold desc="Variables">
    // </editor-fold>
    // <editor-fold desc="Constructor">
    // </editor-fold>
    // <editor-fold desc="Get Set Methods">
    // </editor-fold>
    // <editor-fold desc="Methods">
    /**
     * Get column names
     * @return Column names
     */
    public List<String> getNames(){
        List<String> colNames = new ArrayList<>();
        for (Column col : this){
            colNames.add(col.getName());
        }
        
        return colNames;
    }
    
    /**
     * Get Column data types
     * @return Column data types
     */
    public List<DataType> getDataTypes(){
        List<DataType> dTypes = new ArrayList<>();
        for (Column col : this){
            dTypes.add(col.getDataType());
        }
        
        return dTypes;
    }
    
    /**
     * Get column data formats
     * @return Column data formats
     */
    public List<String> getFormats(){
        List<String> formats = new ArrayList<>();
        for (Column col : this){
            formats.add(col.getFormat());
        }
        
        return formats;
    }
    
    /**
     * Index of column name
     * @param colName Column name
     * @return Index value
     */
    public int indexOf(String colName) {
        return this.getNames().indexOf(colName);
    }
    
    /**
     * Index of column names
     * @param colNames Column names
     * @return Index list
     */
    public List<Integer> indexOf(List<String> colNames) {
        List<Integer> r = new ArrayList<>();
        for (String colName : colNames){
            r.add(indexOf(colName));
        }
        return r;
    }
    // </editor-fold>
}
