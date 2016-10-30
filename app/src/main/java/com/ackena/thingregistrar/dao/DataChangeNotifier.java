package com.ackena.thingregistrar.dao;

import java.util.List;

/**
 * Created by rajeev on 16-Jun-16.
 */
public class DataChangeNotifier<T> implements IDataChangeNotifier<T> {

    private final List<IDataChangeListener<T>> listeners;

    public DataChangeNotifier(List<IDataChangeListener<T>> listeners) {
        this.listeners = listeners;
    }

    @Override
    public void notifyItemChanged(final Object source, final Object dataSource, final T item, final int position) {
        for (final IDataChangeListener listener : listeners) {
            listener.itemChanged(source, dataSource, item, position);
        }
    }

    @Override
    public void notifyItemChanged(final Object source, final T item, final int position) {
        for (final IDataChangeListener listener : listeners) {
            listener.itemChanged(source, item, position);
        }
    }

    @Override
    public void notifyItemChanged(final Object source, final T item) {
        for (final IDataChangeListener listener : listeners) {
            listener.itemChanged(source, item);
        }
    }

    @Override
    public void notifyItemInserted(final Object source, final Object dataSource, final T item, final int position) {
        for (final IDataChangeListener listener : listeners) {
            listener.itemInserted(source, dataSource, item, position);
        }
    }

    @Override
    public void notifyItemDeleted(final Object source, final Object dataSource, final T item, final int position) {
        for (final IDataChangeListener listener : listeners) {
            listener.itemRemoved(source, dataSource, item, position);
        }
    }

    @Override
    public void notifyItemMoved(final Object source, final Object dataSource, final T item, final int fromPosition, final int toPosition) {
        for (final IDataChangeListener listener : listeners) {
            listener.itemMoved(source, dataSource, item, fromPosition, toPosition);
        }
    }

    @Override
    public void notifyDataSetChanged(Object source, Object dataSource) {
        for (final IDataChangeListener listener : listeners) {
            listener.dataSetChanged(source, dataSource);
        }
    }

    @Override
    public void notifyDataSetLoaded(Object source, Object dataSource) {
        for (final IDataChangeListener listener : listeners) {
            listener.dataSetLoaded(source, dataSource);
        }
    }

    @Override
    public void notifyDataSetLoading(Object source, Object dataSource) {
        for (final IDataChangeListener listener : listeners) {
            listener.dataSetLoading(source, dataSource);
        }
    }
}