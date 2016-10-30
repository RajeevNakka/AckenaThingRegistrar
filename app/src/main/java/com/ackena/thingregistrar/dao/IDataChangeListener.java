package com.ackena.thingregistrar.dao;

/**
 * Created by Rajeev on 15-06-2016.
 */
public interface IDataChangeListener<T> {
    public void itemChanged(Object source, T object, int position);
    public void itemChanged(Object source, Object dataSource, T object, int position);
    public void itemChanged(Object source, T object);
    public void itemInserted(Object source, Object dataSource, T object, int position);
    public void itemRemoved(Object source, Object dataSource, T object, int position);
    public void itemMoved(Object source, Object dataSource, T object, int fromPosition, int toPosition);
    void dataSetChanged(Object source, Object dataSource);
    void dataSetLoaded(Object source, Object dataSource);
    void dataSetLoading(Object source, Object dataSource);
}