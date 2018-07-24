package org.meteoinfo.data.dataframe.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.meteoinfo.data.ArrayUtil;

import org.meteoinfo.data.dataframe.DataFrame;
import org.meteoinfo.data.dataframe.impl.Transforms.CumulativeFunction;
import ucar.ma2.Array;

public class SeriesGrouping
        implements Iterable<Map.Entry<Object, SparseBitSet>> {

    private final Map<Object, SparseBitSet> groups = new LinkedHashMap<>();
    private final Set<Integer> columns = new LinkedHashSet<>();

    public SeriesGrouping() {
    }

    public <V> SeriesGrouping(final DataFrame df, final KeyFunction<V> function, final Integer... columns) {
        final Iterator<List<V>> iter = df.iterator();
        for (int r = 0; iter.hasNext(); r++) {
            final List<V> row = iter.next();
            final Object key = function.apply(row);
            SparseBitSet group = groups.get(key);
            if (group == null) {
                group = new SparseBitSet();
                groups.put(key, group);
            }
            group.set(r);
        }

        for (final int column : columns) {
            this.columns.add(column);
        }
    }

    public <V> SeriesGrouping(final DataFrame df, final Integer... columns) {
        this(
                df,
                columns.length == 1
                        ? new KeyFunction<V>() {
                    @Override
                    public Object apply(final List<V> value) {
                        return value.get(columns[0]);
                    }

                }
                        : new KeyFunction<V>() {
                    @Override
                    public Object apply(final List<V> value) {
                        final List<Object> key = new ArrayList<>(columns.length);
                        for (final int column : columns) {
                            key.add(value.get(column));
                        }
                        return Collections.unmodifiableList(key);
                    }
                },
                columns
        );
    }

    @SuppressWarnings("unchecked")
    public <V> DataFrame apply(final DataFrame df, final Function<?, ?> function) {
        if (df.isEmpty()) {
            return df;
        }

        final List<Array> grouped = new ArrayList<>();
        final List<String> names = df.getColumns().getNames();
        final List<Object> newcols = new ArrayList<>();
        final List<Object> index = new ArrayList<>();

        // construct new row index
        if (function instanceof Aggregate && !groups.isEmpty()) {
            for (final Object key : groups.keySet()) {
                index.add(key);
            }
        }

//        // add key columns
//        for (final int c : columns) {
//            if (function instanceof Aggregate && !groups.isEmpty()) {
//                final List<V> column = new ArrayList<>();
//                for (final Map.Entry<Object, SparseBitSet> entry : groups.entrySet()) {
//                    final SparseBitSet rows = entry.getValue();
//                    final int r = rows.nextSetBit(0);
//                    column.add((V)df.getValue(r, c));
//                }
//                grouped.add(ArrayUtil.array(column));
//                newcols.add(names.get(c));
//            } else {
//                try {
//                    grouped.add(df.getColumnData(c));
//                } catch (InvalidRangeException ex) {
//                    Logger.getLogger(Grouping.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                newcols.add(names.get(c));
//            }
//        }
        // add aggregated data columns
        for (int c = 0; c < df.size(); c++) {
            if (!columns.contains(c)) {
                final List<V> column = new ArrayList<>();
                if (groups.isEmpty()) {
                    try {
                        if (function instanceof Aggregate) {
                            column.add((V) Aggregate.class.cast(function).apply(df.col(c)));
                        } else {
                            for (int r = 0; r < df.length(); r++) {
                                column.add((V) Function.class.cast(function).apply(df.getValue(r, c)));
                            }
                        }
                    } catch (final ClassCastException ignored) {
                    }

                    if (function instanceof CumulativeFunction) {
                        CumulativeFunction.class.cast(function).reset();
                    }
                } else {
                    for (final Map.Entry<Object, SparseBitSet> entry : groups.entrySet()) {
                        final SparseBitSet rows = entry.getValue();
                        try {
                            if (function instanceof Aggregate) {
                                final List<V> values = new ArrayList<>(rows.cardinality());
                                for (int r = rows.nextSetBit(0); r >= 0; r = rows.nextSetBit(r + 1)) {
                                    values.add((V) df.getValue(r, c));
                                }
                                column.add((V) Aggregate.class.cast(function).apply(values));
                            } else {
                                for (int r = rows.nextSetBit(0); r >= 0; r = rows.nextSetBit(r + 1)) {
                                    column.add((V) Function.class.cast(function).apply(df.getValue(r, c)));
                                }
                            }
                        } catch (final ClassCastException ignored) {
                        }

                        if (function instanceof CumulativeFunction) {
                            CumulativeFunction.class.cast(function).reset();
                        }
                    }
                }

                if (!column.isEmpty()) {
                    grouped.add(ArrayUtil.array(column));
                    newcols.add(names.get(c));
                }
            }
        }

//        if (newcols.size() <= columns.size()) {
//            throw new IllegalArgumentException(
//                    "no results for aggregate function "
//                    + function.getClass().getSimpleName()
//            );
//        }

        return new DataFrame(grouped, index, newcols);
    }

    public Set<Object> keys() {
        return groups.keySet();
    }

    public Set<Integer> columns() {
        return columns;
    }

    @Override
    public Iterator<Map.Entry<Object, SparseBitSet>> iterator() {
        return groups.entrySet().iterator();
    }
}
