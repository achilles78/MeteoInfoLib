/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meteoinfo.data.dataframe;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.meteoinfo.data.ArrayMath;
import ucar.ma2.Array;
import ucar.ma2.InvalidRangeException;
import ucar.ma2.Range;
import ucar.ma2.DataType;

/**
 *
 * @author Yaqiang Wang
 */
public class DataFrame {

    // <editor-fold desc="Variables">
    private Index index;
    private ColumnCollection columns;
    private Object data;    //Two dimension array or array list
    private boolean array2D = false;
    private int rowNum;
    private int colNum;
    private Range rowRange;
    private Range colRange;

    // </editor-fold>
    // <editor-fold desc="Constructor">
    /**
     * Constructor
     *
     * @param data Data array
     * @param columns Columns
     * @param index Index
     */
    public DataFrame(Array data, Index index, ColumnCollection columns) {
        int n;
        if (data.getRank() == 1) {    //One dimension array
            this.data = new ArrayList<>();
            ((List) this.data).add(data);
            n = 1;
        } else {   //Two dimension array
            this.data = data;
            this.array2D = true;
            n = data.getShape()[1];
        }
        this.columns = columns;

        this.index = index;
        this.rowNum = this.index.size();
        this.colNum = this.columns.size();
    }

    /**
     * Constructor
     *
     * @param data Data array
     * @param columns Columns
     * @param index Index
     */
    public DataFrame(Array data, Index index, List<String> columns) {
        int n;
        List<DataType> dtypes = new ArrayList<>();
        if (data.getRank() == 1) {    //One dimension array
            this.data = new ArrayList<>();
            ((List) this.data).add(data);
            n = 1;
            dtypes.add(data.getDataType());
        } else {   //Two dimension array
            this.data = data;
            this.array2D = true;
            n = data.getShape()[1];
            for (int i = 0; i < n; i++) {
                dtypes.add(data.getDataType());
            }
        }
        if (columns == null) {
            columns = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                columns.add("C_" + String.valueOf(i));
            }
        }
        this.columns = new ColumnCollection();
        for (int i = 0; i < n; i++) {
            this.columns.add(new Column(columns.get(i), dtypes.get(i)));
        }

        this.index = index;
        this.rowNum = this.index.size();
        this.colNum = this.columns.size();
    }

    /**
     * Constructor
     *
     * @param data Data array
     * @param columns Columns
     * @param index Index
     */
    public DataFrame(Array data, List index, List<String> columns) {
        this(data, new Index(index), columns);
    }

    /**
     * Constructor
     *
     * @param data Data array list
     * @param columns Columns
     * @param index Index
     */
    public DataFrame(List<Array> data, Index index, ColumnCollection columns) {
        this.data = data;
        int n = data.size();
        this.columns = columns;
        this.index = index;
        this.rowNum = this.index.size();
        this.colNum = this.columns.size();
    }

    /**
     * Constructor
     *
     * @param data Data array list
     * @param columns Columns
     * @param index Index
     */
    public DataFrame(List<Array> data, Index index, List<String> columns) {
        this.data = data;
        int n = data.size();
        if (columns == null) {
            columns = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                columns.add("C_" + String.valueOf(i));
            }
        }
        this.columns = new ColumnCollection();
        for (int i = 0; i < n; i++) {
            this.columns.add(new Column(columns.get(i), data.get(i).getDataType()));
        }
        this.index = index;
        this.rowNum = this.index.size();
        this.colNum = this.columns.size();
    }

    /**
     * Constructor
     *
     * @param data Data array list
     * @param columns Columns
     * @param index Index
     */
    public DataFrame(List<Array> data, List index, List<String> columns) {
        this(data, new Index(index), columns);
    }

    // </editor-fold>
    // <editor-fold desc="Get Set Methods">
    /**
     * Get data array
     *
     * @return Data array
     */
    public Object getData() {
        return this.data;
    }

    /**
     * Set data array
     *
     * @param value Data array
     */
    public void setData(Array value) {
        if (value.getRank() == 1) {    //One dimension array
            this.data = new ArrayList<>();
            ((List) this.data).add(value);
        } else {   //Two dimension array
            this.data = value;
            this.array2D = true;
        }
    }

    /**
     * Set data array
     *
     * @param value Data array
     */
    public void setData(List<Array> value) {
        this.data = value;
        this.array2D = false;
    }

    /**
     * Get index
     *
     * @return Index
     */
    public Index getIndex() {
        return this.index;
    }

    /**
     * Set index
     *
     * @param value Index
     */
    public void setIndex(Index value) {
        this.index = value;
        this.rowNum = this.index.size();
    }

    /**
     * Set index
     *
     * @param value Index value
     */
    public void setIndex(List value) {
        this.index = new Index(value);
    }

    /**
     * Get columns
     *
     * @return Columns
     */
    public ColumnCollection getColumns() {
        return this.columns;
    }

    /**
     * Get column names
     *
     * @return Column names
     */
    public List<String> getColumnNames() {
        return this.columns.getNames();
    }

    /**
     * Get column data types
     *
     * @return Column data types
     */
    public List<DataType> getColumnDataTypes() {
        return this.columns.getDataTypes();
    }

    /**
     * Set columns
     *
     * @param value Columns
     */
    public void setColumns(ColumnCollection value) {
        this.columns = value;
        this.colNum = this.columns.size();
    }

    /**
     * Get if is 2D array
     *
     * @return Boolean
     */
    public boolean isArray2D() {
        return this.array2D;
    }

    // </editor-fold>
    // <editor-fold desc="Methods">
    /**
     * Get shape
     *
     * @return Shape
     */
    public int[] getShape() {
        int[] shape = new int[2];
        shape[0] = this.index.size();
        shape[1] = this.columns.size();
        return shape;
    }

    /**
     * Get value
     *
     * @param row Row index
     * @param col Column index
     * @return Value
     */
    public Object getValue(int row, int col) {
        if (this.array2D) {
            return ((Array) this.data).getObject(row * this.colNum + col);
        } else {
            return ((Array) ((List) this.data).get(col)).getObject(row);
        }
    }

    /**
     * Get value
     *
     * @param row Row index
     * @param colName Column name
     * @return Value
     */
    public Object getValue(int row, String colName) {
        int col = this.columns.indexOf(colName);
        if (col >= 0) {
            return getValue(row, col);
        } else {
            System.out.println("Column not exists: " + colName + "!");
            return null;
        }
    }

    /**
     * Get column data array
     *
     * @param col Column index
     * @return Column data array
     * @throws InvalidRangeException
     */
    public Array getColumnData(int col) throws InvalidRangeException {
        Array r;
        if (this.array2D) {
            this.rowRange = new Range(0, this.rowNum - 1, 1);
            this.colRange = new Range(col, col, 1);
            List<Range> ranges = new ArrayList<>();
            ranges.add(rowRange);
            ranges.add(colRange);
            r = ArrayMath.section((Array) this.data, ranges);
        } else {
            r = (Array) ((List) this.data).get(col);
        }
        return r;
    }

    /**
     * Get column data array
     *
     * @param colName Column name
     * @return Column data array
     * @throws InvalidRangeException
     */
    public Array getColumnData(String colName) throws InvalidRangeException {
        int col = this.columns.getNames().indexOf(colName);
        if (col >= 0) {
            return getColumnData(col);
        } else {
            System.out.println("Column not exists: " + colName + "!");
            return null;
        }
    }

    /**
     * Select by row and column ranges
     *
     * @param rowRange Row range
     * @param colRange Column range
     * @return Selected data frame or series
     * @throws ucar.ma2.InvalidRangeException
     */
    public Object select(Range rowRange, Range colRange) throws InvalidRangeException {
        ColumnCollection cols = new ColumnCollection();
        for (int i = colRange.first(); i < colRange.last(); i += colRange.stride()) {
            cols.add(this.columns.get(i));
        }

        Object r;
        if (this.array2D) {
            List ranges = new ArrayList<>();
            ranges.add(new Range(rowRange.first(), rowRange.last() - 1, rowRange.stride()));
            ranges.add(new Range(colRange.first(), colRange.last() - 1, colRange.stride()));
            r = ArrayMath.section((Array) this.data, ranges);
        } else {
            r = new ArrayList<>();
            int rn = rowRange.length();
            for (int j = colRange.first(); j < colRange.last(); j++) {
                Array rr = Array.factory(this.columns.get(j).getDataType(), new int[]{rn});
                Array mr = ((List<Array>) this.data).get(j);
                for (int i = rowRange.first(); i < rowRange.last(); i += rowRange.stride()) {
                    rr.setObject(i, mr.getObject(i));
                }
                ((ArrayList) r).add(mr);
            }
            if (cols.size() == 1) {
                r = ((ArrayList) r).get(0);
            }
        }

        if (r == null) {
            return null;
        } else {
            Index rIndex = this.index.subIndex(rowRange.first(), rowRange.last(), rowRange.stride());
            if (cols.size() == 1) {
                Series s = new Series((Array) r, rIndex, cols.get(0).getName());
                return s;
            } else {
                DataFrame df;
                if (r instanceof Array) {
                    df = new DataFrame((Array) r, rIndex, cols);
                } else {
                    df = new DataFrame((ArrayList) r, rIndex, cols);
                }
                return df;
            }
        }
    }

    /**
     * Select by row and column ranges
     *
     * @param rowRange Row range
     * @param colRange Column range
     * @return Selected data frame or series
     * @throws ucar.ma2.InvalidRangeException
     */
    public Object select(Range rowRange, List<Integer> colRange) throws InvalidRangeException {
        ColumnCollection cols = new ColumnCollection();
        for (int i : colRange) {
            cols.add(this.columns.get(i));
        }

        Object r;
        if (this.array2D) {
            List ranges = new ArrayList<>();
            ranges.add(new Range(rowRange.first(), rowRange.last() - 1, rowRange.stride()));
            ranges.add(colRange);
            r = ArrayMath.take((Array) this.data, ranges);
        } else {
            r = new ArrayList<>();
            int rn = rowRange.length();
            for (int j : colRange) {
                Array rr = Array.factory(this.columns.get(j).getDataType(), new int[]{rn});
                Array mr = ((List<Array>) this.data).get(j);
                for (int i = rowRange.first(); i < rowRange.last(); i += rowRange.stride()) {
                    rr.setObject(i, mr.getObject(i));
                }
                ((ArrayList) r).add(mr);
            }
            if (cols.size() == 1) {
                r = ((ArrayList) r).get(0);
            }
        }

        if (r == null) {
            return null;
        } else {
            Index rIndex = this.index.subIndex(rowRange.first(), rowRange.last(), rowRange.stride());
            if (cols.size() == 1) {
                Series s = new Series((Array) r, rIndex, cols.get(0).getName());
                return s;
            } else {
                DataFrame df;
                if (r instanceof Array) {
                    df = new DataFrame((Array) r, rIndex, cols);
                } else {
                    df = new DataFrame((ArrayList) r, rIndex, cols);
                }
                return df;
            }
        }
    }

    /**
     * Select by row and column ranges
     *
     * @param rowRange Row range
     * @param colRange Column range
     * @return Selected data frame or series
     * @throws ucar.ma2.InvalidRangeException
     */
    public Object select(List<Integer> rowRange, Range colRange) throws InvalidRangeException {
        ColumnCollection cols = new ColumnCollection();
        for (int i = colRange.first(); i < colRange.last(); i += colRange.stride()) {
            cols.add(this.columns.get(i));
        }

        Object r;
        if (this.array2D) {
            List ranges = new ArrayList<>();
            ranges.add(rowRange);
            ranges.add(new Range(colRange.first(), colRange.last() - 1, colRange.stride()));
            r = ArrayMath.take((Array) this.data, ranges);
        } else {
            r = new ArrayList<>();
            int rn = rowRange.size();
            for (int j = colRange.first(); j < colRange.last(); j++) {
                Array rr = Array.factory(this.columns.get(j).getDataType(), new int[]{rn});
                Array mr = ((List<Array>) this.data).get(j);
                for (int i : rowRange) {
                    rr.setObject(i, mr.getObject(i));
                }
                ((ArrayList) r).add(mr);
            }
            if (cols.size() == 1) {
                r = ((ArrayList) r).get(0);
            }
        }

        if (r == null) {
            return null;
        } else {
            Index rIndex = this.index.subIndex(rowRange);
            if (cols.size() == 1) {
                Series s = new Series((Array) r, rIndex, cols.get(0).getName());
                return s;
            } else {
                DataFrame df;
                if (r instanceof Array) {
                    df = new DataFrame((Array) r, rIndex, cols);
                } else {
                    df = new DataFrame((ArrayList) r, rIndex, cols);
                }
                return df;
            }
        }
    }

    /**
     * Select by row and column ranges
     *
     * @param rowRange Row range
     * @param colRange Column range
     * @return Selected data frame or series
     */
    public Object select(List<Integer> rowRange, List<Integer> colRange) {
        ColumnCollection cols = new ColumnCollection();
        for (int i : colRange) {
            cols.add(this.columns.get(i));
        }

        Object r;
        if (this.array2D) {
            List ranges = new ArrayList<>();
            ranges.add(rowRange);
            ranges.add(colRange);
            r = ArrayMath.takeValues((Array) this.data, ranges);
        } else {
            r = new ArrayList<>();
            int rn = rowRange.size();
            for (int j : colRange) {
                Array rr = Array.factory(this.columns.get(j).getDataType(), new int[]{rn});
                Array mr = ((List<Array>) this.data).get(j);
                for (int i : rowRange) {
                    rr.setObject(i, mr.getObject(i));
                }
                ((ArrayList) r).add(mr);
            }
            if (cols.size() == 1) {
                r = ((ArrayList) r).get(0);
            }
        }

        if (r == null) {
            return null;
        } else {
            Index rIndex = this.index.subIndex(rowRange);
            if (cols.size() == 1) {
                Series s = new Series((Array) r, rIndex, cols.get(0).getName());
                return s;
            } else {
                DataFrame df;
                if (r instanceof Array) {
                    df = new DataFrame((Array) r, rIndex, cols);
                } else {
                    df = new DataFrame((ArrayList) r, rIndex, cols);
                }
                return df;
            }
        }
    }

    /**
     * Transpose
     *
     * @return Transposed data frame
     */
    public DataFrame transpose() {
        DataFrame df = null;
        if (this.array2D) {
            Array ta = ArrayMath.transpose((Array) this.data, 0, 1);
            List tIndex = new ArrayList<>();
            for (Column col : this.columns) {
                tIndex.add(col.getName());
            }
            List<String> tColumns = new ArrayList<>();
            for (int i = 0; i < this.index.size(); i++) {
                tColumns.add(this.index.toString(i));
            }
            df = new DataFrame(ta, tIndex, tColumns);
        }

        return df;
    }

    /**
     * Convert to string - head
     *
     * @param n Head row number
     * @return The string
     */
    public String head(int n) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ");
        for (String col : this.getColumnNames()) {
            sb.append("\t");
            sb.append(col);
        }
        sb.append("\n");

        int rn = this.index.size();
        if (n > rn) {
            n = rn;
        }
        List<String> formats = this.columns.getFormats();
        for (int r = 0; r < n; r++) {
            sb.append(this.index.toString(r));
            for (int i = 0; i < this.colNum; i++) {
                sb.append("\t");
                if (formats.get(i) == null) {
                    sb.append(this.getValue(r, i).toString());
                } else {
                    sb.append(String.format(formats.get(i), this.getValue(r, i)));
                }
            }
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
        sb.append(" ");
        for (String col : this.getColumnNames()) {
            sb.append("\t");
            sb.append(col);
        }
        sb.append("\n");

        int rn = this.index.size();
        if (n > rn) {
            n = rn;
        }
        List<String> formats = this.columns.getFormats();
        for (int r = rn - n; r < rn; r++) {
            sb.append(this.index.toString(r));
            for (int i = 0; i < this.colNum; i++) {
                sb.append("\t");
                if (formats.get(i) == null) {
                    sb.append(this.getValue(r, i).toString());
                } else {
                    sb.append(String.format(formats.get(i), this.getValue(r, i)));
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return head(100);
    }
    // </editor-fold>
}
