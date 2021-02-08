package bluebase.in.pioneermusicgym;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends AppCompatActivity {
    Context context = this;
    JsonObject jsonObject;

    String urlForgotPasswordVerifier = CommonUtils.IP + "/PMG/pmg_android/login/forgotPasswordVerifier.php";

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        height = (int) (height / 1.7);

        ImageView background = findViewById(R.id.background);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 200, height);
        background.setLayoutParams(layoutParams);

        final EditText otpEditText = findViewById(R.id.otp);
        final EditText newPasswordEditText = findViewById(R.id.newPassword);
        final EditText confirmPasswordEditText = findViewById(R.id.confirmPassword);

        Button changePassword = findViewById(R.id.changePassword);

        changePassword.setOnClickListener(view -> {
            if(otpEditText.getText().toString().length() > 0) {
                if(newPasswordEditText.getText().toString().length() > 0) {
                    if(confirmPasswordEditText.getText().toString().length() > 0) {
                        if (newPasswordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
                            progressDialog = new ProgressDialog(context);
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage("Loading...");
                            progressDialog.show();

                            jsonObject = new JsonObject();
                            jsonObject.addProperty("otp", otpEditText.getText().toString());

                            MD5 md5 = new MD5();
                            jsonObject.addProperty("password", md5.getMD5(newPasswordEditText.getText().toString()));

                            PostForgotPassword postForgotPassword = new PostForgotPassword(context);
                            postForgotPassword.checkServerAvailability(2);
                        } else {
                            Toast.makeText(context, "Passwords don't match!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(context, "Enter Confirm Password", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context, "Enter New Password", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(context, "Enter OTP", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private class PostForgotPassword extends PostRequest{
        public PostForgotPassword(Context context){
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                super.postRequest(urlForgotPasswordVerifier, jsonObject);
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
                    Toast.makeText(context, "Password Changed Successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(context, "Password Change Unsuccessful \n Try Again", Toast.LENGTH_SHORT).show();
                }

            }catch(JSONException e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.basic_menu, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}