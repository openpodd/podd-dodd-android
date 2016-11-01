package org.cm.podd.urban.report.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;

import org.cm.podd.urban.report.BuildConfig;
import org.cm.podd.urban.report.activity.AboutWebviewActivity;
import org.cm.podd.urban.report.activity.EditPasswordActivity;
import org.cm.podd.urban.report.activity.PolicyWebviewActivity;
import org.cm.podd.urban.report.activity.UserLoginActivity;
import org.cm.podd.urban.report.activity.profile.EditProfileActivity;
import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.helper.AppHelper;
import org.cm.podd.urban.report.protocol.OnFragmentInteractionListener;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends BaseFragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment() {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = getActivity();
        mActivity = getActivity();
        mRootView =  inflater.inflate(R.layout.fragment_settings, container, false);


        if (AppHelper.isLoggedIn(mContext)) {
            ButterKnife.findById(mRootView, R.id.logout_textview).setVisibility(View.VISIBLE);
            ButterKnife.findById(mRootView, R.id.profile_linearlayout_wrapper).setVisibility(View.VISIBLE);
        }
        else {
            ButterKnife.findById(mRootView, R.id.logout_textview).setVisibility(View.GONE);
            ButterKnife.findById(mRootView, R.id.profile_linearlayout_wrapper).setVisibility(View.GONE);
        }


        TextView versionName1 = ButterKnife.findById(mRootView, R.id.version_build_text);
        TextView versionCode = ButterKnife.findById(mRootView, R.id.version_name_text);
        versionName1.setText(String.valueOf(BuildConfig.VERSION_NAME));
        versionCode.setText(String.valueOf(BuildConfig.VERSION_CODE));

        ButterKnife.bind(this, mRootView);
        return mRootView;
    }

    // TODO: Rename method, utdate argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick(R.id.edit_profile_textview)
    public void onClickEditProfile(View v) {
        AppHelper.anonymousCheckAction(getActivity(), new AppHelper.AnonymousCheck() {
            @Override
            public void denied() {

            }

            @Override
            public void next() {
                Intent i = new Intent(mContext, EditProfileActivity.class);
                startActivity(i);
            }
        });
    }

    @OnClick(R.id.edit_password_textview)
    public void onClickEditPassword(View v) {
        AppHelper.anonymousCheckAction(getActivity(), new AppHelper.AnonymousCheck() {
            @Override
            public void denied() {

            }

            @Override
            public void next() {
                Intent i = new Intent(mContext, EditPasswordActivity.class);
                startActivity(i);
            }
        });
    }

    @OnClick(R.id.term_and_condition_textview)
    public void onClickTerm(View v) {
        Intent i = new Intent(mContext, PolicyWebviewActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.about_textview)
    public void onClickAbout(View v) {
        Intent i = new Intent(mContext, AboutWebviewActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.logout_textview)
    public void onClickLogout(View v) {
        AppHelper.setIsLoggedIn(v.getContext(), false);
        AppHelper.setToken(v.getContext(), "x");
        AppHelper.setDeviceId(v.getContext());

        Intent i = new Intent(mContext, UserLoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
//
//        pTracker.setScreenName("SettingView");
//        pTracker.send(new HitBuilders.AppViewBuilder().build());
    }
}
