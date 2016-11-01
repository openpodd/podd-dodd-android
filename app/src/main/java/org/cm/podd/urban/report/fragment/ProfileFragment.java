package org.cm.podd.urban.report.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.squareup.otto.Subscribe;

import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.activity.LoginActivity;
import org.cm.podd.urban.report.activity.UserLoginActivity;
import org.cm.podd.urban.report.adapter.ProfileAdapter;
import org.cm.podd.urban.report.api.ApiRequestor;
import org.cm.podd.urban.report.data.Report;
import org.cm.podd.urban.report.data.User;
import org.cm.podd.urban.report.databinding.FragmentProfileBinding;
import org.cm.podd.urban.report.delegate.ReportDelegate;
import org.cm.podd.urban.report.helper.ActivityResultEvent;
import org.cm.podd.urban.report.helper.AppHelper;
import org.cm.podd.urban.report.helper.BusProvider;
import org.cm.podd.urban.report.protocol.OnFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends BaseFeedFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = ProfileFragment.class.getSimpleName();

    //    Report.Adapter adapter;
    ProfileAdapter adapter;

    private OnFragmentInteractionListener mListener;
    private FragmentProfileBinding binding;

    TextView emptyText;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        try {
            Button btnLogin = (Button) getView().findViewById(R.id.login_button);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), LoginActivity.class);
                    startActivity(i);
                }
            });

        } catch (NullPointerException ex) {
            Log.e("Edit", "null");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        // Inflate the layout for this fragment
        mContext = getActivity();
        BusProvider.getInstance().register(this);

        if (AppHelper.isLoggedIn(mContext) == false) {
            View v = inflater.inflate(R.layout.fragment_profile_anonymous, container, false);
            mRootView = v;
            Button btnLogin = (Button) v.findViewById(R.id.login_button);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), UserLoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            });

            return v;
        }
        else {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
            mRootView = binding.getRoot();
            mRootView = super.onCreateView(inflater, container, savedInstanceState);

            emptyText = (TextView) mRootView.findViewById(R.id.emptyText);
            emptyText.setVisibility(View.INVISIBLE);
        }

        binding.setUser(AppHelper.getUserSerialized(mContext));

        return mRootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        BusProvider.getInstance().unregister(this);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void getReportsAndUpdateListView() {
        final List<ProfileAdapter.Item> data = new ArrayList<>();
        data.add(new ProfileAdapter.Item(ProfileAdapter.HEADER, new Report.Model()));

        if (emptyText != null)
            emptyText.setVisibility(View.INVISIBLE);

        apiRequestor.getMyReports(new Callback<ArrayList<Report.Model>>() {
            @Override
            public void success(ArrayList<Report.Model> models, Response response) {

                for (int i = models.size() - 1; i>=0; i--) {
                    ProfileAdapter.Item item = new ProfileAdapter.Item(ProfileAdapter.CHILD, models.get(i));
                    data.add(ProfileAdapter.CHILD, item);
                }

                adapter = new ProfileAdapter(data);
                adapter.setCallback(new ReportDelegate(getActivity(), mRootView));
                recyclerView.setAdapter(adapter);

                if (emptyText != null) {
                    if (models.size() == 0) {
                        emptyText.setVisibility(View.VISIBLE);
                    } else {
                        emptyText.setVisibility(View.INVISIBLE);
                    }
                }

                setSwipeViewRefreshing(false);
            }

            @Override
            public void failure(RetrofitError error) {
                setSwipeViewRefreshing(false);
                AppHelper.showSnackbarErrorInternetConnection(mContext, mRootView, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRefresh();
                    }
                });

                if (emptyText != null)
                    emptyText.setVisibility(View.VISIBLE);
            }
        });

    }

    public void fetchNewProfileDataFromServer() {
        // UPDATE USER
        apiRequestor = new ApiRequestor(mContext);
        apiRequestor.getMyProfile(new Callback<User.Model>() {

            @Override
            public void success(User.Model model, Response response) {
                AppHelper.setUserSerialized(mContext, model);
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    @Override
    public void onRefresh() {
        super.onRefresh();

        fetchNewProfileDataFromServer();
        getReportsAndUpdateListView();

//        adapter.clearDataSet();
    }

    @Override
    public void onSetupAdapter() {
        super.onSetupAdapter();
        apiRequestor = new ApiRequestor(mContext);
//        adapter = new ProfileAdapter()
//        adapter = new Report.Adapter(mContext);
        recyclerView.setAdapter(adapter);
        onRefresh();

    }

    @Subscribe
    public void onFragmentEvent(ActivityResultEvent event) {
        // Do something when any event on fragment was happened

        if (event.reportModel == null) return;

        adapter.updateReport(event.reportModel, event.position);

    }


    @Override
    public void onSetupEventListener() {
        super.onSetupEventListener();

//        adapter.setCallback(new ReportDelegate(getActivity(), mRootView, adapter, apiRequestor));
    }

    @Override
    public void onResume() {
        super.onResume();
//
//        pTracker.setScreenName("MyProfileView");
//        pTracker.send(new HitBuilders.AppViewBuilder().build());
    }
}
