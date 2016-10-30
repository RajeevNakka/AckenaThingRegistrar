package com.ackena.thingregistrar.dao;

/**
 * Created by rajeev on 16-Jun-16.
 */
public class DataChangeListenerAdapter<T> implements IDataChangeListener<T> {

    @Override
    public void itemChanged(Object source, T object,int position) {

    }

    @Override
    public void itemChanged(Object source, Object dataSource, T object, int position) {

    }

    @Override
    public void itemChanged(Object source, T object) {

    }

    @Override
    public void itemInserted(Object source,Object dataSource, T object,int position) {

    }

    @Override
    public void itemRemoved(Object source,Object dataSource, T object,int position) {

    }

    @Override
    public void itemMoved(Object source, Object dataSource, T object, int fromPosition, int toPosition) {

    }

    @Override
    public void dataSetChanged(Object source, Object dataSource) {

    }

    @Override
    public void dataSetLoaded(Object source, Object dataSource) {

    }

    @Override
    public void dataSetLoading(Object source, Object dataSource) {

    }
}