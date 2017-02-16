package org.cm.podd.urban.report.fragment;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.Gson;

import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.activity.MainActivity;
import org.cm.podd.urban.report.activity.report.ReportDetailActivity;
import org.cm.podd.urban.report.activity.report.ReportNewActivity;
import org.cm.podd.urban.report.api.ApiRequestor;
import org.cm.podd.urban.report.data.AdministrationArea;
import org.cm.podd.urban.report.data.Report;
import org.cm.podd.urban.report.helper.AppHelper;
import org.cm.podd.urban.report.protocol.ActivityInterface;
import org.cm.podd.urban.report.protocol.OnFragmentInteractionListener;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static org.cm.podd.urban.report.helper.NetworkHelper.hasNetworkConnection;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyMapFragment extends BaseFragment implements ActivityInterface {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GoogleMap mMap;
    private View mRootView;
    private Context mContext;


    public static final String TAG = MyMapFragment.class.getSimpleName();
    private Report.Adapter adapter;

    ApiRequestor apiRequestor;
    private OnFragmentInteractionListener mListener;
    private SupportMapFragment mapFragment;
    private Gson gson;


    private ProgressDialog progressDialog;

    private int areaId;

    // TODO: Rename and change types and number of parameters
    public static MyMapFragment newInstance() {
        MyMapFragment fragment = new MyMapFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MyMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);

//        setRetainInstance(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        gson = new Gson();
        mContext = getActivity();

        apiRequestor = new ApiRequestor(mContext);

//        getReportMarker();

        mRootView = v;

        ButterKnife.bind(this, v);

        return v;
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
        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (Exception e) {
//            throw new RuntimeException(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        pTracker.setScreenName("MapView");
        pTracker.send(new HitBuilders.AppViewBuilder().build());


        mMap = mapFragment.getMap();

        // in case old play services
        if (mMap == null) return;

        mMap.setMyLocationEnabled(true);

        int areaId = ((MainActivity) getActivity()).getAdminAreaId();
        if (areaId == -99999 || mMap == null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(18.7847952, 98.9850526), 10));
            getReportMarker();

        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (!hasNetworkConnection(getActivity())) {
                    Toast.makeText(mContext, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    return false;
                }

                Report.Model model = gson.fromJson(marker.getSnippet(), Report.Model.class);

                Intent i = new Intent(mContext, ReportDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Report.REPORT_MODEL_KEY, marker.getSnippet());
                i.putExtras(bundle);
                getActivity().startActivity(i);

                return false;
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        try {
            SupportMapFragment fm = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_container));
            if (mMap != null && fm.isResumed()) {
                getFragmentManager().beginTransaction()
                        .remove(fm).commit();
                mMap = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, mapFragment).commit();

//            mMap = mapFragment.getMap();
//            Log.d ("map ", (mMap == null) + "");

        }

    }

    @Override
    public void onBindView() {

    }

    @Override
    public void onSetupAdapter() {

    }

    @Override
    public void onSetupEventListener() {

    }


    @OnClick(R.id.report_buttonview)
    public void reportAction(View v) {

        AppHelper.anonymousCheckAction(getActivity(), new AppHelper.AnonymousCheck() {
            @Override
            public void denied() {

            }

            @Override
            public void next() {
                Intent i = new Intent(mContext, ReportNewActivity.class);
                startActivity(i);
            }
        });

    }

    public void clearMap() {
        if (mMap != null)
            mMap.clear();
    }
    public void loadingMap(boolean show) {
        if (show)
            progressDialog = ProgressDialog.show(getActivity(), "", "กรุณารอสักครู่...");
        else
            progressDialog = null;
    }

    public void dismissLoadingMap() {
        if (progressDialog == null) return;
        progressDialog.dismiss();

    }

    public void moveCamera(LatLng latLng, int areaId) {

        try {
            int zoom = 10;
            if (areaId == -1) {
                zoom = 7;
            } else if (areaId == 311) {
                zoom = 12;
            }

            if (areaId == -1 || areaId == 311)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom), 2200, null);

        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public void drawBoundary (List<List<Double>> points) {

        if (points == null) return;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        ArrayList<LatLng> arrayPoints = new ArrayList<LatLng>();
        for (int i = 0; i < points.size(); i++) {
            LatLng latlng = new LatLng(points.get(i).get(1), points.get(i).get(0));
            arrayPoints.add(latlng);
            builder.include(latlng);
        }

        if (arrayPoints.size() > 0) {

            PolygonOptions polygonOptions = new PolygonOptions();
            polygonOptions.addAll(arrayPoints);
            polygonOptions.strokeColor(Color.argb(100, 0, 0, 0));
            polygonOptions.strokeWidth(7);
            polygonOptions.fillColor(Color.argb(30, 0, 0, 0));
            mMap.addPolygon(polygonOptions);

            LatLngBounds mBound = builder.build();
            int padding = 100;

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(mBound, padding);
            mMap.animateCamera(cu, 1000, null);
        }

        ((MainActivity) getActivity()).setChooseButton(true);
    }

    public void getReportMarker() {

        areaId = -9999;
        try {
            areaId = ((MainActivity) getActivity()).getAdminAreaId();
        } catch (Exception ex) {}

        if (areaId == -99999 || mMap == null) {
            return;
        }

        // hot fix: map show limit 100;
        int pageSize = 100;
        int page = 1;
        apiRequestor.getReportsByArea(areaId, page, pageSize, new Callback<ArrayList<Report.Model>>() {
            @Override
            public void success(ArrayList<Report.Model> models, Response response) {

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (int i = 0; i < models.size(); i++) {
                    Double kLat = ((50.0 - (1.0 * (new Random()).nextInt() % 100)) / 10000000.0);
                    Double kLng = ((50.0 - (1.0 * (new Random()).nextInt() % 100)) / 10000000.0);
                    try {
                        Double lat = (models.get(i).reportLocation.coordinates.get(1) + kLat);
                        Double lng = (models.get(i).reportLocation.coordinates.get(0) + kLng);
                        LatLng location = new LatLng(lat, lng);

                        if (mMap != null) {
                            mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .snippet(gson.toJson(models.get(i)))
                                    .icon(models.get(i).getReportMakerColor()));
                        }

                        builder.include(location);
                    } catch (NullPointerException ex) {

                    }

                }

                dismissLoadingMap();

                if (areaId == 311) {
                    int padding = 100;
                    LatLngBounds mBound = null;

                    try {
                        mBound = builder.build();

                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(mBound, padding);
                        mMap.animateCamera(cu, 1500, null);
                    } catch (IllegalStateException ex) {
                        LatLng location = new LatLng(13.808277, 100.552206);
                        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(location, 11);
                        mMap.animateCamera(cu, 1500, null);
                    }
                } else {
                    ((MainActivity) getActivity()).moveCamera();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                AppHelper.showSnackbarErrorInternetConnection(mContext, mRootView, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onResume();
                    }
                });

                dismissLoadingMap();
                ((MainActivity) getActivity()).moveCamera();
            }
        });
    }
}
