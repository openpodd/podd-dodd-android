package org.cm.podd.urban.report.activity.report;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.cm.podd.urban.report.BuildConfig;
import org.cm.podd.urban.report.MyApplication;
import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.activity.BaseToolBarActivity;
import org.cm.podd.urban.report.activity.MainActivity;
import org.cm.podd.urban.report.adapter.ReportTypeAdapter;
import org.cm.podd.urban.report.api.ApiRequestor;
import org.cm.podd.urban.report.data.FormDataEntity;
import org.cm.podd.urban.report.data.PostModel;
import org.cm.podd.urban.report.data.PostModel.PostReportModel;
import org.cm.podd.urban.report.data.PostModel.ReportImagesModel;
import org.cm.podd.urban.report.data.Report;
import org.cm.podd.urban.report.databinding.ActivityFormBinding;
import org.cm.podd.urban.report.fragment.ReportFeedFragment;
import org.cm.podd.urban.report.helper.AppHelper;
import org.cm.podd.urban.report.helper.DateUtil;
import org.cm.podd.urban.report.helper.LocationHelper;
import org.cm.podd.urban.report.helper.NetworkHelper;
import org.cm.podd.urban.report.helper.NetworkHelper_;
import org.cm.podd.urban.report.helper.S3Helper;
import org.cm.podd.urban.report.widget.ImageWrapperLinearLayout;
import org.cm.podd.urban.report.widget.VolumnLinearLayout;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import bolts.Continuation;
import bolts.Task;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mylibrary.dialog.OkDialog;
import mylibrary.helper.CameraHelper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ReportFormActivity extends BaseToolBarActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    CameraHelper cameraHelper;
    @Bind(R.id.shape_linearlayout)
    LinearLayout mHorizontalLinearLayout;
    @Bind(R.id.time_textview)
    TextView mTimeTextView;
    @Bind(R.id.date_textview)
    TextView mDateTextView;
    @Bind(R.id.wrapper_linearlayout)
    LinearLayout mFormWrapper;
    @Bind(R.id.help_text)
    TextView helpText;

    @Bind(R.id.delete_button)
    Switch mDeleteButton;

    private DateTimeViewModel dateTimeViewModel;
    ActivityFormBinding binding;
    private ArrayList<String> s3ImagesFileUrl;
    private String reportCode;
    private int formTypeStrRes;
//    @Bind(R.id.submit_report_button) Button mSubmitReportButton;

    private double currentLatitude;
    private double currentLongitude;
    private String TAG = "BaseToolBarActivity";
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        binding = DataBindingUtil.setContentView(this, R.layout.activity_form);
        cameraHelper = new CameraHelper(this);


        LayoutInflater li = getLayoutInflater();
        Intent prevIntent = getIntent();


        mFormWrapper = binding.wrapperLinearlayout;
        formTypeStrRes = prevIntent.getIntExtra(ReportTypeAdapter.CAT_TYPE_STR_RES_KEY, 1);
        reportCode = prevIntent.getStringExtra(ReportTypeAdapter.CAT_CODE_STRING);

        Log.d("ReportType", "getMyReport (line 66): " + reportCode);

        if (toolbar != null) {
            getSupportActionBar().setTitle("รายงาน" + getResources().getString(formTypeStrRes));
        }

        switch (formTypeStrRes) {
            case R.string.IC_CAT_ANIMAL_DEAD:
                li.inflate(R.layout.cell_form_animal_die, mFormWrapper);
                break;
            case R.string.IC_CAT_ANIMAL_BITTEN:
                li.inflate(R.layout.cell_form_animal_bite, mFormWrapper);
                break;
            case R.string.IC_CAT_HUMAN_FOOD_REPEATED_USED_OIL:
                li.inflate(R.layout.cell_form_use_oil, mFormWrapper);
                break;
            case R.string.IC_CAT_HUMAN_FOOD_TOO_CHEAP_MEAT:
                li.inflate(R.layout.cell_form_cheapmeat, mFormWrapper);
                break;
            case R.string.IC_CAT_HUMAN_FOOD_POISON:
                li.inflate(R.layout.cell_form_food_poinsioning, mFormWrapper);
                break;
            case R.string.IC_CAT_HUMAN_FOOD_CONTAMINATED_SUSPECTED:
                li.inflate(R.layout.cell_form_food_contaminated, mFormWrapper);
                break;
            default:
                break;
        }


        li.inflate(R.layout.cell_form_moreinfo, mFormWrapper);
        li.inflate(R.layout.cell_form_anonymous, mFormWrapper);

        dateTimeViewModel = new DateTimeViewModel();
        binding.setDatetime(dateTimeViewModel);

        // SHOULD AFTER INFLATE THE UI
        ButterKnife.bind(this);

        int colourRes = prevIntent.getIntExtra(ReportTypeAdapter.CAT_COLOUR_INT, android.R.color.holo_red_dark);
        coordinatorLayout.setBackgroundColor(getResources().getColor(colourRes));

        buildGoogleApiClient();

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

                    @Override
                    public void onConnected(Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                    }
                })
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onSetupAdapter() {

    }

    @Override
    public void onSetupEventListener() {

    }


    @OnClick(R.id.time_textview)
    public void onClickTime(View v) {
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                ReportFormActivity.this,
                dateTimeViewModel.hourOfDay,
                dateTimeViewModel.minute,
                true
        );

        tpd.show(getFragmentManager(), "TimePickerDialog");
    }

    @OnClick(R.id.date_textview)
    public void onClickDate(View v) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                ReportFormActivity.this,
                dateTimeViewModel.year,
                dateTimeViewModel.monthOfYear,
                dateTimeViewModel.dayOfMonth
        );

        Calendar minCal = Calendar.getInstance();
        minCal.setTime(now.getTime());
        minCal.add(Calendar.DAY_OF_MONTH, -7);
        dpd.setMinDate(minCal);
        dpd.setMaxDate(now);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    ArrayList<Uri> uris = new ArrayList<Uri>();

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cameraHelper.handleActivityResult(requestCode, resultCode, data);

        if (requestCode == CameraHelper.RESULT_GET_CONTENT) {
            if (resultCode == RESULT_OK) {
                uris.clear();
                Log.d(TAG, "onActivityResult OK");

                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    int count = clipData.getItemCount();
                    ClipData.Item item;
                    Uri uri;
//                    uploadCount = clipData.getItemCount();
                    for (int i = 0; i < count; i++) {
                        item = clipData.getItemAt(i);
                        uri = item.getUri();
                        uris.add(uri);
                    }
                } else if (data.getData() != null) {
                    uris.add(data.getData());
                } else {
                    Log.e(TAG, "onActivityResult INTENT DATA == NULL");
                }

                for (int i = 0; i < uris.size(); i++) {
                    final ImageWrapperLinearLayout img = new ImageWrapperLinearLayout(mContext).setImageResource(uris.get(i));
                    img.setUri(uris.get(i));
                    img.setPadding(10, 10, 10, 10);

                    img.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mHorizontalLinearLayout.removeView(img);
                            if (mHorizontalLinearLayout.getChildCount() == 0) {
                                helpText.setVisibility(View.VISIBLE);
                            } else {
                                helpText.setVisibility(View.GONE);
                            }
                        }
                    });

                    mHorizontalLinearLayout.addView(img, i);
                    helpText.setVisibility(View.GONE);
                }
            }
        } else if (requestCode == CameraHelper.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "CAMERA PHOTO (line 203): ");
            }
        }
        Log.d(TAG, "onActivityResult ");

    }


    @OnClick(R.id.camera_imagebutton)
    public void onClickCamera(View v) {
        Intent intent = new Intent();
        cameraHelper.setListener(new CameraHelper.cameraHelperLister() {
            @Override
            public void onSuccess(Uri uri) {

            }

            @Override
            public void onCameraSuccess(Uri uri) {
                Log.d(TAG, "TAKE A PHOTO USING CAMERA SUCCESS (line ): ");
                final ImageWrapperLinearLayout img = new ImageWrapperLinearLayout(ReportFormActivity.this)
                        .setImageResource(uri);
                img.setUri(uri);
                img.setPadding(10, 10, 10, 10);

                img.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mHorizontalLinearLayout.removeView(img);
                        if (mHorizontalLinearLayout.getChildCount() == 0) {
                            helpText.setVisibility(View.VISIBLE);
                        } else {
                            helpText.setVisibility(View.GONE);
                        }
                    }
                });

                mHorizontalLinearLayout.addView(img);
                helpText.setVisibility(View.GONE);
            }
        });

        cameraHelper.showPopUp();
    }

    public String generateTextFromRadioButton(RadioGroup radioGroup) {
        int radioButtonTypeID = radioGroup.getCheckedRadioButtonId();
        RadioButton radioTypeButton = (RadioButton) radioGroup.findViewById(radioButtonTypeID);

        try {
            String text = radioTypeButton.getText().toString();
            text = text.replace("(ระบุ)", "").replace("ระบุ", "").replace("()", "");
            return text;
        } catch (NullPointerException ex) {
            return "";
        }
    }

    public String generateTextFromCheckbox(CheckBox[] checkBox) {
        String text = "";
        for (int i = 0; i < checkBox.length; i++) {
            if (checkBox[i].isChecked()) {
                text += checkBox[i].getText().toString();
                if (i != checkBox.length - 1)
                    text += ", ";
            }
        }
        return text;
    }

    //
    @OnClick(R.id.submit_report_button)
    public void onClickSubmit(View v) {
        submitReport();

    }

    public void submitReport() {
        final ApiRequestor apiRequestor = new ApiRequestor(mContext);
        final PostReportModel postReportModel = new PostReportModel();
        postReportModel.guid = AppHelper.getDeviceId(mContext) + "-" + Calendar.getInstance().getTimeInMillis();
        postReportModel.reportId = 1234;
        postReportModel.reportTypeCode = reportCode;

        // set incident date
        Log.d("FormData", dateTimeViewModel.getDateReportString());
        Log.d("FormData", dateTimeViewModel.getTimeString());
        if (!dateTimeViewModel.checkDateTimeBeforeNow()) {
            Toast.makeText(mContext, "เวลาที่คุณรายงานไม่ถูกต้อง", Toast.LENGTH_LONG).show();
            return;
        }
        postReportModel.incidentDate = dateTimeViewModel.getDateReportString();

        // Set date
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date today = Calendar.getInstance().getTime();
        String reportDate = df.format(today);
        postReportModel.date = reportDate;


        PostReportModel.ReportLocationEntity location = new PostReportModel.ReportLocationEntity();

        if (mHorizontalLinearLayout.getChildCount() == 0) {
            OkDialog okDialog = new OkDialog(mContext)
                    .setMessage(getString(R.string.one_image_required));
            okDialog.show();
            return;
        }

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        LocationHelper locationHelper = LocationHelper.getInstance_(mContext);
        if (mLastLocation != null) {
            locationHelper.setCurrentLocation(mLastLocation);
        }

//        LatLng sydney;
        boolean noLocation = true;
        ImageWrapperLinearLayout imageObject= (ImageWrapperLinearLayout) mHorizontalLinearLayout.getChildAt(0);

        try {
            File file = new File(imageObject.getPath());
            ExifInterface exifIf = new ExifInterface(file.getPath());
            float[] latLong = new float[2];
            Log.i("CameraMapsTest", exifIf.toString());
            Log.i("CameraMapsTest", "long" + exifIf.getAttribute(ExifInterface.TAG_DATETIME));
            boolean success = exifIf.getLatLong(latLong);

            if (success) {
//                sydney = new LatLng(latLong[0], latLong[1]);
                Location mLocation = new Location("provider");
                mLocation.setLatitude(latLong[0]);
                mLocation.setLongitude(latLong[1]);

                Log.i("CameraMapsTest", "CameraView::printExifData(): lat: " + latLong[0]);
                Log.i("CameraMapsTest", "CameraView::printExifData(): long: " + latLong[1]);

                currentLatitude = latLong[0];
                currentLongitude = latLong[1];

                locationHelper.setCurrentLocation(mLocation);
                noLocation = false;
            } else {

                Log.i("CameraMapsTest", "CameraView::printExifData(): no gps info on file!");

                if (mLastLocation != null) {
                    currentLatitude = mLastLocation.getLatitude();
                    currentLongitude = mLastLocation.getLongitude();
                    noLocation = false;
                }
            }

            Log.d(TAG, "latln: " + latLong);
        } catch (IOException e) {

            e.printStackTrace();

            if (mLastLocation != null) {
                currentLatitude = mLastLocation.getLatitude();
                currentLongitude = mLastLocation.getLongitude();
                noLocation = false;
            }
        } catch (NullPointerException ex) {
            if (mLastLocation != null) {
                currentLatitude = mLastLocation.getLatitude();
                currentLongitude = mLastLocation.getLongitude();
                noLocation = false;
            }
        }

        if (noLocation) {
            AppHelper.showSnackbarErrorLocation(mContext, findViewById(android.R.id.content), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitReport();
                }
            });
            return;
        }

        location.latitude = currentLatitude;
        location.longitude = currentLongitude;

        postReportModel.isAnonymous = mDeleteButton.isChecked();
        postReportModel.reportLocation = location;

        // FILL THE FORM
        FormDataEntity formData = new FormDataEntity();
        EditText remarkEditText = ButterKnife.findById(this, R.id.remark_edittext);
        EditText addressEditText = ButterKnife.findById(this, R.id.address_edittext);

        EditText editText1;
        EditText editText2;

        VolumnLinearLayout picker1;
        VolumnLinearLayout picker2;

        switch (formTypeStrRes) {
            case R.string.IC_CAT_ANIMAL_DEAD:
                FormDataEntity.AnimalSickDeadFormDataEntity child4 = new FormDataEntity.AnimalSickDeadFormDataEntity();
//                EditText suspectFoodEditText1 = ButterKnife.findById(this, R.id.food_suspect_edittext);
//                child4.foodSuspect = suspectFoodEditText1.getText().toString();
                picker1 = ButterKnife.findById(this, R.id.number_picker1);
                picker2 = ButterKnife.findById(this, R.id.number_picker2);

                String animalType = ((EditText) ButterKnife.findById(this, R.id.animal_type)).getText().toString();
                String symptom = ((EditText) ButterKnife.findById(this, R.id.symptom)).getText().toString();

                child4.animalType = animalType;
                child4.sickCount = picker1.getValue();
                child4.deathCount = picker2.getValue();
                child4.symptom = symptom;

                formData = child4;
                break;
            case R.string.IC_CAT_ANIMAL_BITTEN:
                FormDataEntity.AnimalBittenFormDataEntity child5 = new FormDataEntity.AnimalBittenFormDataEntity();

                RadioGroup radioType = ((RadioGroup) ButterKnife.findById(this, R.id.radio_type));
                RadioGroup radioStatus = ((RadioGroup) ButterKnife.findById(this, R.id.radio_status));

                CheckBox[] rebisSymptom = new CheckBox[]{
                        ((CheckBox) ButterKnife.findById(this, R.id.checkbox_rebis_1)),
                        ((CheckBox) ButterKnife.findById(this, R.id.checkbox_rebis_2)),
                        ((CheckBox) ButterKnife.findById(this, R.id.checkbox_rebis_3))
                };

                String otherAnimalType = ((EditText) ButterKnife.findById(this, R.id.other_animal_edittext)).getText().toString();

                String typeAnimalText = generateTextFromRadioButton(radioType) + " " + otherAnimalType;
                String statusAnimalText = generateTextFromRadioButton(radioStatus);
                String symptomsText = generateTextFromCheckbox(rebisSymptom);

                child5.animalStatus = statusAnimalText;
                child5.animalType = typeAnimalText;
                child5.symptom = symptomsText;

                formData = child5;
                break;
            case R.string.IC_CAT_HUMAN_FOOD_REPEATED_USED_OIL:
                FormDataEntity.HumanFoodRepeatedUsedOilFormDataEntity child3 = new FormDataEntity.HumanFoodRepeatedUsedOilFormDataEntity();

                editText1 = ButterKnife.findById(this, R.id.food_suspect_edittext);
                child3.foodSuspect = editText1.getText().toString();

                formData = child3;
                break;
            case R.string.IC_CAT_HUMAN_FOOD_TOO_CHEAP_MEAT:
                FormDataEntity.HumanFoodTooCheapMeatFormDataEntity child1 =
                        new FormDataEntity.HumanFoodTooCheapMeatFormDataEntity();
//                editText1 = ButterKnife.findById(this, R.id.price_kg_edittext);


//                child1.price = (Double.valueOf(editText1.getText().toString()));
                RadioGroup radioTypeMEAT = ((RadioGroup) ButterKnife.findById(this, R.id.radio_type_meat));

                String otherAnimalMeat = ((EditText) ButterKnife.findById(this, R.id.other_animal_edittext)).getText().toString();
                String price = ((EditText) ButterKnife.findById(this, R.id.price_kg_edittext)).getText().toString();

                child1.type = generateTextFromRadioButton(radioTypeMEAT) + " " + otherAnimalMeat;
                child1.price = price;

                formData = child1;
                break;
            case R.string.IC_CAT_HUMAN_FOOD_DIRTY_SHOP:
                //default form
                //default form
                //default form
                break;
            case R.string.IC_CAT_HUMAN_FOOD_POISON:
                FormDataEntity.HumanPoisonousFormDataEntity child2 = new FormDataEntity.HumanPoisonousFormDataEntity();
                picker1 = ButterKnife.findById(this, R.id.number_picker1);

                CheckBox[] poisonSymptom = new CheckBox[]{
                        ((CheckBox) ButterKnife.findById(this, R.id.checkbox_symptom_1)),
                        ((CheckBox) ButterKnife.findById(this, R.id.checkbox_symptom_2))
                };

                String foodSuspect = ((EditText) ButterKnife.findById(this, R.id.food_suspect_edittext)).getText().toString();
                String otherSymptom = ((EditText) ButterKnife.findById(this, R.id.other_symptom_edtitext)).getText().toString();
                String event = ((EditText) ButterKnife.findById(this, R.id.remark_edittext)).getText().toString();

                String foodSymptom = generateTextFromCheckbox(poisonSymptom);
                if ("".equalsIgnoreCase(foodSymptom) == false && "".equalsIgnoreCase(otherSymptom) == false)
                    foodSymptom += "," + otherSymptom;

                child2.otherSymptom = event;
                child2.foodSuspect = foodSuspect;
                child2.symptom = foodSymptom;
                child2.sickCount = picker1.getValue();

                formData = child2;

                break;
            default:
                break;
        }

        // Check upload images
        boolean canUploadImage = true;
        for (int i = 0; i < mHorizontalLinearLayout.getChildCount(); i++) {
            ImageWrapperLinearLayout view = (ImageWrapperLinearLayout) mHorizontalLinearLayout.getChildAt(i);
            if (view.getPath() == null) {
                canUploadImage = false;
                break;
            }
            Log.d(TAG, "(line ): NO - " + i + " : " + view.getPath());
        }

        if (!canUploadImage) {
            Toast.makeText(mContext, "ไม่สามารถอัพโหลดรูปภาพได้ กรุณาลองใหม่",
                    Toast.LENGTH_LONG).show();
            return;
        }
        // DUPP FORM DATA
        formData.address = addressEditText.getText().toString();
        formData.remark = remarkEditText.getText().toString();
        postReportModel.remark = formData.remark;

        postReportModel.formData = formData;


        postReport(apiRequestor, postReportModel).onSuccessTask(new Continuation<String, Task<ArrayList<String>>>() {
            @Override
            public Task<ArrayList<String>> then(Task<String> task) throws Exception {
                Log.d(TAG, "  (line ):" + task.getResult()); // GUID
                return uploadToS3(apiRequestor, task.getResult());
            }
        }).continueWithTask(new Continuation<ArrayList<String>, Task<Void>>() {
            @Override
            public Task<Void> then(Task<ArrayList<String>> task) throws Exception {
                ArrayList<Task<Void>> tasks = new ArrayList<Task<Void>>();
                for (int i = 0; i < task.getResult().size(); i++) {
                    NetworkHelper updateACL = NetworkHelper_.getInstance_(mContext);
                    final Task<Void>.TaskCompletionSource _task = Task.create();
                    tasks.add(_task.getTask());


                    updateACL.updateACL(task.getResult().get(i), new NetworkHelper.UpdateACLInterface() {
                        @Override
                        public void finish() {
                            _task.setResult(null);
                        }
                    });
                }

                return Task.whenAll(tasks);
            }
        }).continueWithTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(Task<Void> task) throws Exception {
                ArrayList<Task<Void>> tasks = new ArrayList<Task<Void>>();

                // ACCESS GLObAL SCOPE
                for (int i = 0; i < s3ImagesFileUrl.size(); i++) {
                    PostModel.ReportImagesModel imageModel = new PostModel.ReportImagesModel();
                    imageModel.guid = postReportModel.guid;
                    imageModel.reportGuid = postReportModel.guid;
                    imageModel.imageUrl = s3ImagesFileUrl.get(i);
                    imageModel.thumbnailUrl = s3ImagesFileUrl.get(i) + "-thumbnail";

                    final Task<Void>.TaskCompletionSource _task = Task.create();
                    tasks.add(_task.getTask());
                    apiRequestor.postImage(imageModel, new Callback<ReportImagesModel>() {
                        @Override
                        public void success(ReportImagesModel reportImagesModel, Response response) {
                            Log.d(TAG, "POST IMAGE SUCCES (line ): ");
                            _task.setResult(null);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d(TAG, "POST IMAGE FAILED (line ): ");
                            _task.setError(error);
                        }
                    });
                }
                return Task.whenAll(tasks);
            }
        }).onSuccess(new Continuation<Void, Object>() {
            @Override
            public Object then(Task<Void> task) throws Exception {
                Log.d(TAG, "FINALLY (line 295): ");
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(Report.INTENT_TARGET_TAG, ReportFeedFragment.class.getSimpleName());
                startActivity(intent);

                return null;
            }
        });

    }

    public Task<ArrayList<String>> uploadToS3(final ApiRequestor apiRequestor, final String guid) {
        final Task<ArrayList<String>>.TaskCompletionSource tcs = Task.create();
        final S3Helper s3Helper = new S3Helper(mContext).setCallback(new S3Helper.S3HelperCallback() {
            @Override
            public void onAllFilesUploaded(ArrayList<String> fileNames) {
                Log.d(TAG, "onAllFileUploaded (line ): ");
                s3ImagesFileUrl = new ArrayList<String>();

                ArrayList<String> updateKeys = new ArrayList<String>();
                for (int i = 0; i < fileNames.size(); i++) {
                    Log.d(TAG, "success: " + fileNames.get(i));

                    s3ImagesFileUrl.add(BuildConfig.S3_PREFIX + fileNames.get(i));

                    updateKeys.add(fileNames.get(i));
                    updateKeys.add(fileNames.get(i) + "-thumbnail");
                }
                tcs.setResult(updateKeys);
            }
        });

        for (int i = 0; i < mHorizontalLinearLayout.getChildCount(); i++) {
            ImageWrapperLinearLayout view = (ImageWrapperLinearLayout) mHorizontalLinearLayout.getChildAt(i);
            s3Helper.beginUpload(view.getPath(), view.getThumbnailImage());
            Log.d(TAG, "(line ): NO - " + i + " : " + view.getPath());
        }

        return tcs.getTask();

    }

    public Task<String> postReport(ApiRequestor apiRequestor, final PostModel.PostReportModel _postReportModel) {
        final ProgressDialog dialog = ProgressDialog.show(ReportFormActivity.this, "",
                "กรุณารอสักครู่...", true);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        final Task<String>.TaskCompletionSource successful = Task.create();
        apiRequestor.postReport(_postReportModel, new Callback<Report.Model>() {
            @Override
            public void success(Report.Model model, Response response) {
                successful.setResult(_postReportModel.guid);
            }

            @Override
            public void failure(RetrofitError error) {
//                Toast.makeText(mContext, "ไม่สามารถรายงานได้ ลองใหม่อีกครั้ง", Toast.LENGTH_LONG).show();
                successful.setError(error);

//                if (error.getKind().toString() == "NETWORK")
                AppHelper.showSnackbarErrorInternetConnection(mContext,
                        findViewById(android.R.id.content), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                   submitReport();
                            }
                        });
                dialog.hide();
            }
        });
        return successful.getTask();
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        dateTimeViewModel.year = year;
        dateTimeViewModel.monthOfYear = monthOfYear;
        dateTimeViewModel.dayOfMonth = dayOfMonth;

        binding.setDatetime(dateTimeViewModel);

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        dateTimeViewModel.hourOfDay = hourOfDay;
        dateTimeViewModel.minute = minute;
        binding.setDatetime(dateTimeViewModel);
    }

    public static class DateTimeViewModel {
        public String dateString;
        public String timeString;

        public DateTimeViewModel() {
            Calendar now = Calendar.getInstance();

            hourOfDay = now.get(Calendar.HOUR_OF_DAY);
            minute = now.get(Calendar.MINUTE);

            year = now.get(Calendar.YEAR);
            monthOfYear = now.get(Calendar.MONTH);
            dayOfMonth = now.get(Calendar.DAY_OF_MONTH);

        }

        public String getTimeString() {
            return String.format("%d:%02d น.", hourOfDay, minute);
        }

        public String getDateReportString() {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
            return calendar.get(Calendar.YEAR) + "-" + getValidMonth(calendar.get(Calendar.MONTH)) + "-" + calendar.get(Calendar.DATE);
        }

        public int getValidMonth(int monthOfYear) {
            int january = 1 - Calendar.JANUARY;
            return monthOfYear + january;
        }

        public String getDateString() {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);

            return DateUtil.convertToThaiDate(calendar.getTime());
        }

        public boolean checkDateTimeBeforeNow() {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);

            Calendar now = Calendar.getInstance();
            return !calendar.getTime().after(now.getTime());
        }

        public int hourOfDay;
        public int minute;

        public int year;
        public int monthOfYear;
        public int dayOfMonth;

    }

    @Override
    protected void onResume() {
        super.onResume();

        Tracker tracker = ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        tracker.setScreenName("ReportDetailActivity");
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }
}
