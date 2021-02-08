package bluebase.in.pioneermusicgym;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileEditFragment extends Fragment {
    Context context;
    ProgressDialog progressDialog;
    JsonObject jsonObject;

    EditText name;
    EditText userName;
    EditText email;
    EditText title;
    AutoCompleteTextView gender;
    EditText dateOfBirth;
    EditText mobileNo;
    EditText experience;
    CheckBox englishCheckBox;
    CheckBox tamilCheckBox;
    CheckBox hindiCheckBox;

    String originalUserName;

    String urlGetProfile = CommonUtils.IP + "/PMG/pmg_android/user_settings/getProfileDetails.php";
    String urlEditProfile = CommonUtils.IP + "/PMG/pmg_android/user_settings/editProfileDetails.php";

    String[] genderArr = {"Male", "Female", "Other"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        height = (int) (height / 1.53);

        ImageView background = view.findViewById(R.id.background);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 200, height);
        background.setLayoutParams(layoutParams);

        name = view.findViewById(R.id.name);
        userName = view.findViewById(R.id.userName);
        email = view.findViewById(R.id.email);
        title = view.findViewById(R.id.title);
        gender = view.findViewById(R.id.gender);
        dateOfBirth = view.findViewById(R.id.dateOfBirth);
        mobileNo = view.findViewById(R.id.mobileNo);
        experience = view.findViewById(R.id.experience);
        englishCheckBox = view.findViewById(R.id.englishCheckBox);
        tamilCheckBox = view.findViewById(R.id.tamilCheckBox);
        hindiCheckBox = view.findViewById(R.id.hindiCheckBox);

        Button update = view.findViewById(R.id.update);

        update.setOnClickListener(view1 -> {
            if(!name.getText().toString().equals("")) {
                if(!userName.getText().toString().equals("")) {
                    if(!email.getText().toString().equals("")) {
                        if(CommonUtils.emailValidator(email.getText().toString())) {
                            if (!title.getText().toString().equals("")) {
                                if(!gender.getText().toString().equals("")) {
                                    if(!dateOfBirth.getText().toString().equals("")){
                                        if (!mobileNo.getText().toString().equals("")) {
                                            if (!experience.getText().toString().equals("")) {
                                                if(englishCheckBox.isChecked() || tamilCheckBox.isChecked() || hindiCheckBox.isChecked()) {
                                                    progressDialog = new ProgressDialog(getContext());
                                                    progressDialog.setCancelable(false);
                                                    progressDialog.setMessage("Loading...");
                                                    progressDialog.show();

                                                    jsonObject = new JsonObject();
                                                    jsonObject.addProperty("name", name.getText().toString());
                                                    jsonObject.addProperty("userName", userName.getText().toString());
                                                    jsonObject.addProperty("originalUserName", originalUserName);
                                                    jsonObject.addProperty("email", email.getText().toString());
                                                    jsonObject.addProperty("title", title.getText().toString());
                                                    jsonObject.addProperty("gender", gender.getText().toString());
                                                    jsonObject.addProperty("dateOfBirth", dateOfBirth.getText().toString());
                                                    jsonObject.addProperty("mobileNo", mobileNo.getText().toString());
                                                    jsonObject.addProperty("experience", experience.getText().toString());
                                                    if (englishCheckBox.isChecked())
                                                        jsonObject.addProperty("english", 1);
                                                    else jsonObject.addProperty("english", 0);
                                                    if (tamilCheckBox.isChecked())
                                                        jsonObject.addProperty("tamil", 1);
                                                    else jsonObject.addProperty("tamil", 0);
                                                    if (hindiCheckBox.isChecked())
                                                        jsonObject.addProperty("hindi", 1);
                                                    else jsonObject.addProperty("hindi", 0);

                                                    PostEditProfile postEditProfile = new PostEditProfile(context);
                                                    postEditProfile.checkServerAvailability(2);
                                                }else {
                                                    Toast.makeText(context, "Enter Languages Known", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(context, "Enter Experience", Toast.LENGTH_SHORT).show();
                                            }
                                        }else {
                                            Toast.makeText(context, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
                                        }
                                    }else {
                                        Toast.makeText(context, "Enter Date of Birth", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(context, "Select Gender", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(context, "Enter Title", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(context, "Enter Valid Email", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(context, "Enter Email", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, "Enter UserName", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(context, "Enter Name", Toast.LENGTH_SHORT).show();
            }
        });

        dateOfBirth.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                    (DatePickerDialog.OnDateSetListener) (view12, year, monthOfYear, dayOfMonth) -> {
                        String month;
                        if(monthOfYear + 1 < 10){
                            month = "0" + (monthOfYear + 1);
                        }else{
                            month = String.valueOf(monthOfYear + 1);
                        }

                        String dateValue = year + "-" + month + "-" + dayOfMonth;
                        dateOfBirth.setText(dateValue);
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item, genderArr);
        gender.setAdapter(adapter);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        jsonObject = new JsonObject();
        jsonObject.addProperty("userId", CommonUtils.userId);

        PostProfile postProfile = new PostProfile(context);
        postProfile.checkServerAvailability(2);
    }

    private class PostProfile extends PostRequest{
        public PostProfile(Context context){
            super(context);
        }

        @Override
        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                super.postRequest(urlGetProfile, jsonObject);
            }else {
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }

        @Override
        public void onFinish(JSONArray jsonArray){
            progressDialog.dismiss();

            try{
                JSONObject jsonObject = (JSONObject) jsonArray.get(0);

                if(jsonObject.getBoolean("status")){
                    name.setText(jsonObject.getString("name"));
                    originalUserName = jsonObject.getString("userName");
                    userName.setText(jsonObject.getString("userName"));
                    email.setText(jsonObject.getString("email"));
                    title.setText(jsonObject.getString("title"));
                    if(!jsonObject.getString("gender").equals("")) gender.setText(jsonObject.getString("gender"), false);
                    else gender.setText("Male", false);
                    dateOfBirth.setText(jsonObject.getString("dateOfBirth"));
                    mobileNo.setText(jsonObject.getString("mobileNo"));
                    experience.setText(jsonObject.getString("experience"));
                    if(jsonObject.getInt("english") == 1) englishCheckBox.setChecked(true);
                    if(jsonObject.getInt("tamil") == 1) tamilCheckBox.setChecked(true);
                    if(jsonObject.getInt("hindi") == 1) hindiCheckBox.setChecked(true);
                }else{
                    Toast.makeText(context, "Data Fetch Error", Toast.LENGTH_SHORT).show();
                }

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private class PostEditProfile extends PostRequest{
        public PostEditProfile(Context context){
            super(context);
        }

        @Override
        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                super.postRequest(urlEditProfile, jsonObject);
            }else{
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }

        @Override
        public void onFinish(JSONArray jsonArray){
            progressDialog.dismiss();

            try{
                JSONObject jsonObject = (JSONObject) jsonArray.get(0);

                if(jsonObject.getBoolean("status")){
                    Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    CommonUtils.userName = userName.getText().toString();
                    getActivity().getSupportFragmentManager().popBackStack();
                }else {
                    if(!jsonObject.getString("message").equals("")){
                        Toast.makeText(context, "Update unsuccessful " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context, "Update unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                }

            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

}