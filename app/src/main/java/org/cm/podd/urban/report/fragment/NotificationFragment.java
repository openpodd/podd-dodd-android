package org.cm.podd.urban.report.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.analytics.HitBuilders;

import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.activity.LoginActivity;
import org.cm.podd.urban.report.data.Notification;
import org.cm.podd.urban.report.data.Report;
import org.cm.podd.urban.report.helper.AppHelper;
import org.cm.podd.urban.report.protocol.OnFragmentInteractionListener;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static org.cm.podd.urban.report.helper.NetworkHelper.hasNetworkConnection;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotificationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends BaseFeedFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Notification.Adapter adapter;

    public static final String TAG = NotificationFragment.class.getSimpleName();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    TextView emptyText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = getActivity();

        if (AppHelper.isLoggedIn(mContext) == false) {
            View v =  inflater.inflate(R.layout.empty, container, false);
            TextView textView = (TextView) v.findViewById(R.id.emptyText);
            textView.setText("ยังไม่มีข้อมูล");
            return v;
        }

        mRootView = inflater.inflate(R.layout.fragment_notification, container, false);
        super.onCreateView(inflater, container, savedInstanceState);


        emptyText = (TextView) mRootView.findViewById(R.id.emptyText);
        if (!hasNetworkConnection(getActivity())) {
            emptyText.setVisibility(View.VISIBLE);
        }

        return mRootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onSetupAdapter() {
        super.onSetupAdapter();

        adapter = new Notification.Adapter(mContext);
        recyclerView.setAdapter(adapter);

        onRefresh();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        adapter.clearDataSet();

        if (emptyText != null)
            emptyText.setVisibility(View.INVISIBLE);

        apiRequestor.getNotifications(new Callback<ArrayList<Notification.Model>>() {
            @Override
            public void success(ArrayList<Notification.Model> models, Response response) {

                for (int i = 0; i < models.size(); i++) {
                    Notification.Model notification = models.get(i);
                    adapter.add(notification, i);

                }

                if (emptyText != null) {
                    if (models.size() == 0) {
                        emptyText.setVisibility(View.VISIBLE);
                    } else {
                        emptyText.setVisibility(View.INVISIBLE);
                    }
                }

                AppHelper.setSwipeViewRefreshing(swipeLayout, false);
            }

            @Override
            public void failure(RetrofitError error) {
                if (emptyText != null)
                    emptyText.setVisibility(View.VISIBLE);

                AppHelper.setSwipeViewRefreshing(swipeLayout, false);
                AppHelper.showSnackbarErrorInternetConnection(mContext, mRootView, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRefresh();
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//
//        pTracker.setScreenName("NotificationView");
//        pTracker.send(new HitBuilders.AppViewBuilder().build());
    }
}
