package com.ackena.thingregistrar;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ackena.thingregistrar.UserFragment.OnListFragmentInteractionListener;
import com.ackena.thingregistrar.entities.User;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link User} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyUserRecyclerViewAdapter extends RecyclerView.Adapter<MyUserRecyclerViewAdapter.ViewHolder> {

    private final List<User> mValues;
    private final OnListFragmentInteractionListener mListener;

    private final Context mContext;
    private View.OnClickListener clickListener;

    public MyUserRecyclerViewAdapter(Context context,List<User> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);

        setStatus(holder);

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onUserListFragmentInteraction_Click(mValues.get(holder.getAdapterPosition()));
                }
            }
        };
        holder.mView.setOnClickListener(clickListener);
        holder.mIdView.setOnClickListener(clickListener);
    }

    private void setStatus(ViewHolder holder) {
        int resourceId;
        switch ( holder.mItem.status.toUpperCase())
        {
            case "AVAILABLE" : resourceId = R.drawable.status_available;
                break;
            case "AWAY" : resourceId = R.drawable.status_away;
                break;
            case "DND" : resourceId = R.drawable.status_dnd;
                break;
            case "UNAVAILABLE" : resourceId = R.drawable.status_not_available;
                break;
            case "XA" : resourceId = R.drawable.status_extended_away;
                break;
            default:
                resourceId = holder.mItem.isAvailable?R.drawable.status_available:R.drawable.status_not_available;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            holder.mAvailabilityIcon.setBackground(ContextCompat.getDrawable(mContext, resourceId));
        }else{
            holder.mAvailabilityIcon.setBackgroundDrawable(ContextCompat.getDrawable(mContext, resourceId));
        }

        holder.mStatusView.setText(getStatusText(holder));
    }

    private String getStatusText(ViewHolder holder) {
        String status = holder.mItem.status;
        if (status.equalsIgnoreCase("DND"))
            return "Do not disturb";
        else if(status.equalsIgnoreCase("XA"))
            return "Extended away";
        else if(status.equalsIgnoreCase("UNAVAILABLE"))
            return "Offline";
        else return status;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mStatusView;
        public final View mAvailabilityIcon;
        public User mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mStatusView = (TextView) view.findViewById(R.id.status);
            mAvailabilityIcon = (View) view.findViewById(R.id.availability);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mStatusView.getText() + "'";
        }
    }
}
