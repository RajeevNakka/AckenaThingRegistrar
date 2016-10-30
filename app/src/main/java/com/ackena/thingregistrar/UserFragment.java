package com.ackena.thingregistrar;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ackena.thingregistrar.dao.DataChangeListenerAdapter;
import com.ackena.thingregistrar.dao.UserDao;
import com.ackena.thingregistrar.entities.User;

/**
 * A fragment representing a list of Friends/Users.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class UserFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";

    // TODO: Customize parameters
    private int mColumnCount = 1;

    private OnListFragmentInteractionListener mListener;
    private MyUserRecyclerViewAdapter mAdapter;

    private DataChangeListenerAdapter<User> userDataChangeListener = new DataChangeListenerAdapter<User>() {
        @Override
        public void itemChanged(Object source, Object dataSource, User object, int position) {
            mAdapter.notifyItemChanged(position);
        }

        @Override
        public void itemInserted(Object source, Object dataSource, User object, int position) {
            mAdapter.notifyItemInserted(position);
        }

        @Override
        public void itemRemoved(Object source, Object dataSource, User object, int position) {
            mAdapter.notifyItemRemoved(position);
        }

        @Override
        public void dataSetChanged(Object source, Object dataSource) {
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void dataSetLoaded(Object source, Object dataSource) {
            if (mListener != null) {
                mListener.onUserListFragmentInteraction_DataLoaded(source, dataSource);
            }
        }

        @Override
        public void dataSetLoading(Object source, Object dataSource) {
            if (mListener != null) {
                mListener.onUserListFragmentInteraction_DataLoading(source, dataSource);
            }
        }
    };
    private View mRecyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserFragment() {
    }

    @SuppressWarnings("unused")
    public static UserFragment newInstance(int columnCount) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mAdapter = new MyUserRecyclerViewAdapter(getActivity(), UserDao.ITEMS, mListener);
            UserDao.removeOnUserChangeListener(userDataChangeListener);
            UserDao.addOnUserChangeListener(userDataChangeListener);
            recyclerView.setAdapter(mAdapter);
            mRecyclerView = view;
        }
        return view;
    }

    public int getItemCount() {
        return mAdapter != null ? mAdapter.getItemCount() : 0;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onUserListFragmentInteraction_Click(User item);

        void onUserListFragmentInteraction_DataLoaded(Object source, Object dataSource);

        void onUserListFragmentInteraction_DataLoading(Object source, Object dataSource);
    }
}
