/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meteoinfo.data.dataframe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import org.joda.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.meteoinfo.data.ArrayMath;
import org.meteoinfo.data.ArrayUtil;
import org.meteoinfo.global.DataConvert;
import org.meteoinfo.global.util.GlobalUtil;
import org.meteoinfo.global.util.TypeUtils;
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
    //private Range rowRange;
    //private Range colRange;

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
        if (data.getRank() == 1) {    //One dimension array
            if (columns.size() == 1) {
                this.data = new ArrayList<>();
                ((List) this.data).add(data);
            } else {
                if (data.getSize() == columns.size()) {
                    this.data = data.reshape(new int[]{1, columns.size()});
                    this.array2D = true;
                }
            }
        } else {   //Two dimension array
            this.data = data;
            this.array2D = true;
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
     * Set column names
     *
     * @param colNames Column names
     */
    public void setColumns(List<String> colNames) {
        for (int i = 0; i < this.columns.size(); i++) {
            if (i < colNames.size()) {
                this.columns.get(i).setName(colNames.get(i));
            }
        }
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
     * Update columns formats
     */
    public void updateColumnFormats() {
        if (this.array2D) {
            Column col = this.columns.get(0);
            col.updateFormat((Array) data);
            for (int i = 1; i < this.columns.size(); i++) {
                this.columns.get(i).setFormat(col.getFormat());
                this.columns.get(i).setFormatLen(col.getFormatLen());
            }
        } else {
            for (int i = 0; i < this.columns.size(); i++) {
                this.columns.get(i).updateFormat(((List<Array>) data).get(i));
            }
        }
    }

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
     * Set value
     *
     * @param row Row index
     * @param col Column index
     * @param v Value
     */
    public void setValue(int row, int col, Object v) {
        if (this.array2D) {
            ((Array) this.data).setObject(row * this.colNum + col, v);
        } else {
            ((Array) ((List) this.data).get(col)).setObject(row, v);
        }
    }

    /**
     * Set value
     *
     * @param row Row index
     * @param colName Column name
     * @param v Value
     */
    public void setValue(int row, String colName, Object v) {
        int col = this.columns.indexOf(colName);
        if (col >= 0) {
            setValue(row, col, v);
        } else {
            System.out.println("Column not exists: " + colName + "!");
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
            Range rowRange = new Range(0, this.rowNum - 1, 1);
            Range colRange = new Range(col, col, 1);
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
     * Add column data
     *
     * @param colName Column name
     * @param a Column data array
     * @throws InvalidRangeException
     */
    public void addColumnData(String colName, Array a) throws InvalidRangeException {
        DataType dt = a.getDataType();
        if (this.array2D) {
            DataType dt1 = this.columns.get(0).getDataType();
            if (dt1 != dt) {
                if (dt1.isNumeric() && dt.isNumeric()) {
                    if (dt1 == DataType.DOUBLE) {
                        a = ArrayUtil.toDouble(a);
                        dt = a.getDataType();
                    } else if (dt1 == DataType.FLOAT) {
                        a = ArrayUtil.toFloat(a);
                        dt = a.getDataType();
                    }
                }
            }
            if (dt1 == dt) {
                Array ra = Array.factory(dt, new int[]{this.rowNum, this.colNum + 1});
                Range rowRange = new Range(0, this.rowNum - 1, 1);
                Range colRange = new Range(0, this.colNum - 1, 1);
                List<Range> ranges = Arrays.asList(rowRange, colRange);
                ArrayMath.setSection(ra, ranges, (Array) this.data);
                colRange = new Range(this.colNum, this.colNum, 1);
                ranges = Arrays.asList(rowRange, colRange);
                ArrayMath.setSection(ra, ranges, a);
                this.data = ra;
            } else {

            }
        }
        Column column = new Column(colName, dt);
        this.columns.add(column);
        this.colNum += 1;
    }

    /**
     * Set column data
     *
     * @param colName Column name
     * @param a Column data array
     * @throws InvalidRangeException
     */
    public void setColumnData(String colName, Array a) throws InvalidRangeException {
        int col = this.columns.getNames().indexOf(colName);
        if (col >= 0) {

        } else {
            this.addColumnData(colName, a);
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
            cols.add((Column) this.columns.get(i).clone());
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

    private String toString(int start, int end) {
        this.updateColumnFormats();

        StringBuilder sb = new StringBuilder();
        String format = this.index.getNameFormat();
        sb.append(String.format(format, " "));
        for (Column col : this.getColumns()) {
            sb.append(" ");
            sb.append(String.format(col.getNameFormat(), col.getName()));
        }
        sb.append("\n");

        List<String> formats = this.columns.getFormats();
        for (int r = start; r < end; r++) {
            sb.append(this.index.toString(r));
            for (int i = 0; i < this.colNum; i++) {
                sb.append(" ");
                if (formats.get(i) == null) {
                    sb.append(this.getValue(r, i).toString());
                } else {
                    sb.append(String.format(formats.get(i), this.getValue(r, i)));
                }
            }
            sb.append("\n");
        }
        if (end < this.index.size()) {
            sb.append("...");
        }

        return sb.toString();
    }

    /**
     * Convert to string - head
     *
     * @param n Head row number
     * @return The string
     */
    public String head(int n) {
        int rn = this.index.size();
        if (n > rn) {
            n = rn;
        }
        return toString(0, n);
    }

    /**
     * Convert to string - tail
     *
     * @param n Tail row number
     * @return The string
     */
    public String tail(int n) {
        int rn = this.index.size();
        if (n > rn) {
            n = rn;
        }
        return toString(rn - n, rn);
    }

    @Override
    public String toString() {
        return head(100);
    }

    /**
     * Read data frame from ASCII file
     *
     * @param fileName File name
     * @param delimiter Delimiter
     * @param headerLines Number of lines to skip at begining of the file
     * @param formatSpec Format specifiers string
     * @param encoding Fle encoding
     * @param indexCol Column to be used as index
     * @param indexFormat Index format
     * @return DataFrame object
     * @throws java.io.FileNotFoundException
     */
    public static DataFrame readTable(String fileName, String delimiter, int headerLines, String formatSpec, String encoding,
            int indexCol, String indexFormat) throws FileNotFoundException, IOException, Exception {
        BufferedReader sr = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), encoding));
        if (headerLines > 0) {
            for (int i = 0; i < headerLines; i++) {
                sr.readLine();
            }
        }

        String title = sr.readLine().trim();
        if (encoding.equals("UTF8")) {
            if (title.startsWith("\uFEFF")) {
                title = title.substring(1);
            }
        }
        String[] titleArray1 = GlobalUtil.split(title, delimiter);
        List<String> titleArray = new ArrayList(Arrays.asList(titleArray1));
        if (indexCol >= 0) {
            titleArray.remove(indexCol);
        }

        if (titleArray.isEmpty()) {
            System.out.println("File Format Error!");
            sr.close();
            return null;
        }

        int colNum = titleArray.size();
        if (headerLines == -1) {
            for (int i = 0; i < colNum; i++) {
                titleArray.set(i, "Col_" + String.valueOf(i));
            }
        }

        //Get fields
        ColumnCollection cols = new ColumnCollection();
        Column col;
        List<List> values = new ArrayList<>();
        String[] colFormats;
        if (formatSpec == null) {
            colFormats = new String[colNum];
            for (int i = 0; i < colNum; i++) {
                colFormats[i] = "C";
            }
        } else {
            colFormats = formatSpec.split("%");
        }

        int idx = 0;
        boolean isBreak = false;
        for (String colFormat : colFormats) {
            if (colFormat.isEmpty()) {
                continue;
            }

            int num = 1;
            if (colFormat.length() > 1 && !colFormat.substring(0, 1).equals("{")) {
                int index = colFormat.indexOf("{");
                if (index < 0) {
                    index = colFormat.length() - 1;
                }
                num = Integer.parseInt(colFormat.substring(0, index));
                colFormat = colFormat.substring(index);
            }
            for (int i = 0; i < num; i++) {
                String colName = titleArray.get(idx).trim();
                if (colFormat.equals("C") || colFormat.equals("s")) //String
                {
                    col = new Column(colName, DataType.STRING);
                } else if (colFormat.equals("i")) //Integer
                {
                    col = new Column(colName, DataType.INT);
                } else if (colFormat.equals("f")) //Float
                {
                    col = new Column(colName, DataType.FLOAT);
                } else if (colFormat.equals("d")) //Double
                {
                    col = new Column(colName, DataType.DOUBLE);
                } else if (colFormat.equals("B")) //Boolean
                {
                    col = new Column(colName, DataType.BOOLEAN);
                } else if (colFormat.substring(0, 1).equals("{")) {    //Date
                    int eidx = colFormat.indexOf("}");
                    String formatStr = colFormat.substring(1, eidx);
                    col = new Column(colName, DataType.OBJECT);
                    col.setFormat(formatStr);
                } else {
                    col = new Column(colName, DataType.STRING);
                }
                cols.add(col);
                values.add(new ArrayList<>());
                idx += 1;
                if (idx == colNum) {
                    isBreak = true;
                    break;
                }
            }
            if (isBreak) {
                break;
            }
        }

        if (idx < colNum) {
            for (int i = idx; i < colNum; i++) {
                cols.add(new Column(titleArray.get(i), DataType.STRING));
                values.add(new ArrayList<>());
            }
        }

        String[] dataArray;
        List<String> indexValues = new ArrayList<>();
        String line;
        if (headerLines == -1) {
            line = title;
        } else {
            line = sr.readLine();
        }
        while (line != null) {
            line = line.trim();
            if (line.isEmpty()) {
                line = sr.readLine();
                continue;
            }
            dataArray = GlobalUtil.split(line, delimiter);
            int cn = 0;
            for (int i = 0; i < dataArray.length; i++) {
                if (cn < colNum) {
                    if (i == indexCol) {
                        indexValues.add(dataArray[i]);
                    } else {
                        values.get(cn).add(dataArray[i]);
                        cn++;
                    }
                } else {
                    break;
                }
            }
            if (cn < colNum) {
                for (int i = cn; i < colNum; i++) {
                    values.get(i).add("");
                }
            }

            line = sr.readLine();
        }
        sr.close();

        int rn = values.get(0).size();
        Index index;
        if (indexCol >= 0) {
            DataType idxDT;
            DateTimeFormatter dtFormatter = ISODateTimeFormat.dateTime();
            if (indexFormat != null) {
                if (indexFormat.substring(0, 1).equals("%")) {
                    indexFormat = indexFormat.substring(1);
                }
                idxDT = DataConvert.getDataType(indexFormat);
                if (idxDT == DataType.OBJECT) {
                    String idxDateFormat = DataConvert.getDateFormat(indexFormat);
                    dtFormatter = DateTimeFormat.forPattern(idxDateFormat);
                }
            } else {
                idxDT = DataConvert.detectDataType(indexValues, 10, null);
                if (idxDT == DataType.OBJECT) {
                    dtFormatter = TypeUtils.getDateTimeFormatter(indexValues.get(0));
                }
            }

            List indexData = new ArrayList<>();
            if (idxDT == DataType.OBJECT) {
                for (String s : indexValues) {
                    indexData.add(dtFormatter.parseDateTime(s));
                }
                index = new DateTimeIndex(indexData);
                ((DateTimeIndex) index).setDateTimeFormatter(dtFormatter);
            } else {
                for (String s : indexValues) {
                    indexData.add(DataConvert.convertStringTo(s, idxDT, null));
                }
                index = new Index(indexData);
            }
        } else {
            index = new Index(rn);
        }

        List<Array> data = new ArrayList<>();
        Array a;
        List vv;
        for (int i = 0; i < colNum; i++) {
            vv = values.get(i);
            col = cols.get(i);
            DataType dt = col.getDataType();
            a = Array.factory(dt, new int[]{rn});
            String v;
            for (int j = 0; j < vv.size(); j++) {
                v = (String) vv.get(j);
                a.setObject(j, col.convertStringTo(v));
            }
            data.add(a);
        }

        DataFrame df = new DataFrame(data, index, cols);

        return df;
    }

    /**
     * Save as CSV file
     *
     * @param fileName File name
     * @param delimiter Delimiter
     * @param dateFormat Date format string
     * @param floatFormat Float format string
     * @param index If write index
     * @throws java.io.IOException
     */
    public void saveCSV(String fileName, String delimiter, String dateFormat, String floatFormat,
            boolean index) throws IOException {
        BufferedWriter sw = new BufferedWriter(new FileWriter(new File(fileName)));
        String str = "";
        String format = this.index.getNameFormat();
        if (index) {
            str = String.format(format, this.index.getName());
        }
        for (int i = 0; i < this.colNum; i++) {
            if (str.isEmpty()) {
                str = this.columns.get(i).getName();
            } else {
                str = str + delimiter + this.columns.get(i).getName();
            }
        };
        sw.write(str);

        String line, vstr;
        List<String> formats = new ArrayList<>();
        for (Column col : this.columns) {
            if (col.getDataType() == DataType.FLOAT || col.getDataType() == DataType.DOUBLE) {
                formats.add(floatFormat == null ? col.getFormat() : floatFormat);
            } else {
                formats.add(col.getFormat());
            }
        }
        for (int j = 0; j < this.rowNum; j++) {
            line = "";
            if (index) {
                line = this.index.toString(j);
            }
            for (int i = 0; i < this.colNum; i++) {
                if (formats.get(i) == null) {
                    vstr = this.getValue(j, i).toString();
                } else {
                    vstr = String.format(formats.get(i), this.getValue(j, i));
                }
                if (line.isEmpty()) {
                    line = vstr;
                } else {
                    line += delimiter + vstr;
                }
            }
            sw.newLine();
            sw.write(line);
        }
        sw.flush();
        sw.close();
    }
    // </editor-fold>
}
