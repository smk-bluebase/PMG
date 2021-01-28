package bluebase.in.pioneermusicgym;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ChangePasswordFragment extends Fragment {
    Context context;
    ProgressDialog progressDialog;
    JsonObject jsonObject;

    EditText currentPasswordEditText;
    EditText newPasswordEditText;
    EditText retypePasswordEditText;

    String urlChangePassword = CommonUtils.IP + "PMG/pmg_android/changePassword.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        height = (int) (height / 1.53);

        ImageView background = view.findViewById(R.id.background);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 200, height);
        background.setLayoutParams(layoutParams);

        context = getActivity().getApplicationContext();

        currentPasswordEditText = view.findViewById(R.id.currentPassword);
        newPasswordEditText = view.findViewById(R.id.newPassword);
        retypePasswordEditText = view.findViewById(R.id.retypePassword);

        Button changePassword = view.findViewById(R.id.changePassword);

        changePassword.setOnClickListener(view1 -> {
            String currentPassword = currentPasswordEditText.getText().toString();
            String newPassword = newPasswordEditText.getText().toString();
            String retypePassword = retypePasswordEditText.getText().toString();

            if(!currentPassword.equals("")){
                if(!newPassword.equals("")){
                    if(!retypePassword.equals("")){
                        if(newPassword.equals(retypePassword)) {
                            progressDialog = new ProgressDialog(getContext());
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage("Loading...");
                            progressDialog.show();

                            jsonObject = new JsonObject();
                            jsonObject.addProperty("userName", CommonUtils.userName);

                            MD5 md5 = new MD5();
                            jsonObject.addProperty("currentPassword", md5.getMD5(currentPassword));
                            jsonObject.addProperty("newPassword", md5.getMD5(newPassword));

                            PostChangePassword postChangePassword = new PostChangePassword(context);
                            postChangePassword.checkServerAvailability(2);
                        }else {
                            Toast.makeText(context, "New Password and Retyped \nPassword are not same", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(context, "Please Retype the Password \nagain to confirm", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, "Please enter New Password", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(context, "Please enter Current Password", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private class PostChangePassword extends PostRequest{
        public PostChangePassword(Context context){
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                super.postRequest(urlChangePassword, jsonObject);
            }else {
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }

        public void onFinish(JSONArray jsonArray){
            progressDialog.dismiss();

            try{
                JSONObject jsonObject = (JSONObject) jsonArray.get(0);

                if(jsonObject.getBoolean("status")){
                    Toast.makeText(context, "Password Change Successful", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "Password Change Unsuccessful", Toast.LENGTH_SHORT).show();
                }

            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

}
