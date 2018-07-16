/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meteoinfo.data.dataframe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.meteoinfo.data.ArrayMath;
import ucar.ma2.Array;

/**
 *
 * @author Yaqiang Wang
 */
public class Index implements Iterable{
    // <editor-fold desc="Variables">
    protected List values;
    // </editor-fold>
    // <editor-fold desc="Constructor">
    public Index(){
        values = new ArrayList<>();
    }
    
    /**
     * Constructor
     * @param array Index array
     */
    public Index(Array array) {
        values = ArrayMath.asList(array);
    }
    
    /**
     * Constructor
     * @param size Index size
     */
    public Index(int size) {
        values = new ArrayList<>();
        for (int i = 0; i < size; i++){
            values.add(i);
        }
    }
    
    /**
     * Constructor
     * @param values Index values
     */
    public Index(List values){
        this.values = values;
    }
    // </editor-fold>
    // <editor-fold desc="Get Set Methods">
    /**
     * Get values
     * @return Values
     */
    public List getValues(){
        return this.values;
    }
    
    /**
     * Set values
     * @param value Values
     */
    public void setValues(List value){
        this.values = value;
    }
    
    @Override
    public Iterator iterator() {
        return this.values.iterator();
    }
    
    /**
     * Get values size
     * @return Index size
     */
    public int size(){
        return values.size();
    }
    // </editor-fold>
    // <editor-fold desc="Methods">
    /**
     * Index of
     * @param v Value
     * @return Index
     */
    public int indexOf(Object v) {
        return this.values.indexOf(v);
    }
    
    /**
     * Index of
     * @param vs Value list
     * @return Index list
     */
    public List<Integer> indexOf(List vs) {
        List<Integer> r = new ArrayList<>();
        for (Object v : vs)
            r.add(indexOf(v));
        
        return r;
    }
    
    /**
     * Sub index
     * @return Index
     */
    public Index subIndex(){
        Index r = new Index(this.values);
        return r;
    }
    
    /**
     * Sub index
     * @param idx Index list
     * @return Index
     */
    public Index subIndex(List<Integer> idx){
        List rv = new ArrayList<>();
        for (int i : idx)
            rv.add(this.values.get(i));
        return new Index(rv);
    }
    
    /**
     * Sub index
     * @param start Start index
     * @param end End index
     * @param step Step
     * @return Index
     */
    public Index subIndex(int start, int end, int step) {
        List rv = new ArrayList<>();
        for (int i = start; i < end; i+=step){
            rv.add(this.values.get(i));
        }
        return new Index(rv);
    }
    
    /**
     * Get indices
     * @param labels Labels
     * @return Indices
     */
    public Object[] getIndices(Array labels) {
        return getIndices(ArrayMath.asList(labels));
    }
    
    /**
     * Get indices
     * @param labels Labels
     * @return Indices
     */
    public Object[] getIndices(List<Object> labels) {
        List<Integer> r = new ArrayList<>();
        List<Object> rIndex = new ArrayList<>();
        List<Integer> rData = new ArrayList<>();
        List<Object> rrIndex = new ArrayList<>();
        Object[] rr;
        List<Integer> r1;
        List<Object> rIndex1;
        for (Object l : labels){
            rr = getIndices(l);
            r1 = (ArrayList<Integer>)rr[0];
            rIndex1 = (ArrayList<Object>)rr[1];
            if (r1.isEmpty()){
                rData.add(0);
                rrIndex.add(l);
            } else {
                r.addAll(r1);
                rIndex.addAll(rIndex1);
                for (Iterator<Integer> it = r1.iterator(); it.hasNext();) {
                    it.next();
                    rData.add(1);
                    rrIndex.add(l);
                }
            }
        }
        
        return new Object[]{r, rIndex, rData, rrIndex};
    }
    
    /**
     * Get indices
     * @param label Label
     * @return Indices
     */
    public Object[] getIndices(Object label) {
        List<Integer> r = new ArrayList<>();
        List<Object> rIndex = new ArrayList<>();
        for (int i = 0; i < values.size(); i++){
            if (values.get(i).equals(label)){
                r.add(i);
                rIndex.add(values.get(i));
            }
        }
        
        return new Object[]{r, rIndex};
    }
    
    /**
     * Sub list by index
     * @param list The list
     * @param index The index
     * @return Result list
     */
    public static List subList(List list, List<Integer> index){
        List r = new ArrayList<>();
        for (int i : index){
            r.add(list.get(i));
        }
        
        return r;
    }
    
//    /**
//     * Fill key list
//     * @param data Valid data array
//     * @param rrdata Result data flags
//     * @return Result data array with same length as key list
//     */
//    public static Array fillKeyList(Array data, List<Integer> rrdata){
//        Array kdata = Array.factory(data.getDataType(), new int[]{rrdata.size()});
//        Object nanObj = null;
//        switch (data.getDataType()){
//            case FLOAT:
//                nanObj = Float.NaN;
//                break;
//            case DOUBLE:
//                nanObj = Double.NaN;
//                break;
//        } 
//        int idx = 0;
//        int i = 0;
//        for (int f : rrdata){
//            if (f == 0)
//                kdata.setObject(i, nanObj);
//            else {
//                kdata.setObject(i, data.getObject(idx));
//                idx += 1;
//            }
//            i += 1;
//        }
//        
//        return kdata;
//    }
    
    /**
     * Fill key list
     * @param data Valid data array
     * @param rrdata Result data flags
     * @return Result data array with same length as key list
     */
    public Array fillKeyList(Array data, List<Integer> rrdata){
        Array kdata = Array.factory(data.getDataType(), new int[]{rrdata.size()}); 
        int idx = 0;
        int i = 0;
        for (int f : rrdata){
            if (f == 0)
                kdata.setObject(i, Double.NaN);
            else {
                kdata.setObject(i, data.getObject(idx));
                idx += 1;
            }
            i += 1;
        }
        
        return kdata;
    }
    
    @Override
    public String toString(){
        if (this.size() < 100)
            return this.values.toString();
        else
            return this.values.subList(0, 98).toString() + ", ...";
    }
    
    /**
     * Convert i_th index to string
     * @param idx Index i
     * @return String
     */
    public String toString(int idx) {
        return this.values.get(idx).toString();
    }
    // </editor-fold>    
}
