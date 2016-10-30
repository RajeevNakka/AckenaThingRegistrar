package com.ackena.thingregistrar.dao;

import com.ackena.thingregistrar.entities.User;

import java.util.List;

/**
 * Created by rajeev on 16-Jun-16.
 */
public interface IDataChangeNotifier<T> {
    void notifyItemChanged(Object source, Object dataSource, T item, int position);

    void notifyItemChanged(Object source, T item, int position);

    void notifyItemChanged(Object source, T item);

    void notifyItemInserted(Object source, Object dataSource, T item, int position);

    void notifyItemDeleted(Object source, Object dataSource, T item, int position);

    void notifyItemMoved(Object source, Object dataSource, T item, int fromPosition, int toPosition);

    void notifyDataSetChanged(Object source, Object dataSource);

    void notifyDataSetLoaded(Object source, Object dataSource);

    void notifyDataSetLoading(Object o, Object items);
}
