package bluebase.in.pioneermusicgym;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    Context context;
    ProgressDialog progressDialog;
    JsonObject jsonObject;

    TextView name;
    TextView userName;
    TextView email;
    TextView title;
    TextView gender;
    TextView dateOfBirth;
    TextView mobileNo;
    TextView experience;
    TextView languagesKnown;

    String urlGetProfile = CommonUtils.IP + "PMG/pmg_android/getProfileDetails.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        height = (int) (height / 1.53);

        ImageView background = view.findViewById(R.id.background);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 200, height);
        background.setLayoutParams(layoutParams);

        name = view.findViewById(R.id.nameValue);
        userName = view.findViewById(R.id.userNameValue);
        email = view.findViewById(R.id.emailValue);
        title = view.findViewById(R.id.titleValue);
        gender = view.findViewById(R.id.genderValue);
        dateOfBirth = view.findViewById(R.id.dateOfBirthValue);
        mobileNo = view.findViewById(R.id.mobileNoValue);
        experience = view.findViewById(R.id.experienceValue);
        languagesKnown = view.findViewById(R.id.languagesKnownValue);

        ImageView editButton = view.findViewById(R.id.editButton);
        editButton.setOnClickListener(v ->
                getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("profileEditFragment")
                .replace(R.id.fragment_container, new ProfileEditFragment(), "profileEditFragment")
                .commit());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();

        progressDialog = new ProgressDialog(context);
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
        public void onFinish(JSONArray jsonArray) {
            progressDialog.dismiss();
            String languages = "";

            try{
                JSONObject jsonObject = (JSONObject) jsonArray.get(0);

                if(jsonObject.getBoolean("status")){
                    name.setText(jsonObject.getString("name"));
                    userName.setText(jsonObject.getString("userName"));
                    email.setText(jsonObject.getString("email"));
                    title.setText(jsonObject.getString("title"));
                    gender.setText(jsonObject.getString("gender"));
                    dateOfBirth.setText(jsonObject.getString("dateOfBirth"));
                    mobileNo.setText(jsonObject.getString("mobileNo"));
                    if(!jsonObject.getString("experience").equals("")) experience.setText(jsonObject.getString("experience") + " years");
                    else experience.setText("0 years");
                    if(jsonObject.getInt("english") == 1) languages = " English ";
                    if(jsonObject.getInt("tamil") == 1) languages += " Tamil ";
                    if(jsonObject.getInt("hindi") == 1) languages += " Hindi ";

                    languagesKnown.setText(languages);
                }else{
                    Toast.makeText(context, "Data Fetch Error", Toast.LENGTH_SHORT).show();
                }

            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

}
