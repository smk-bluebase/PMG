package bluebase.in.pioneermusicgym;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity {
    Context context = this;
    ProgressDialog progressDialog;
    JsonObject jsonObject;
    Dialog dialog;

    String urlSignUp = CommonUtils.IP + "/PMG/pmg_android/login/signup.php";
    String urlOTPVerifier = CommonUtils.IP + "/PMG/pmg_android/login/otpVerifier.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        height = (int) (height / 1.7);

        ImageView background = findViewById(R.id.background);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 200, height);
        background.setLayoutParams(layoutParams);

        final EditText userNameEditText = findViewById(R.id.userName);
        final EditText passwordEditText = findViewById(R.id.password);
        final EditText emailEditText = findViewById(R.id.email);

        TextView signUp = findViewById(R.id.signUp);

        signUp.setOnClickListener(view -> {
            if(!userNameEditText.getText().toString().equals("")){
                if(!emailEditText.getText().toString().equals("")){
                    if(CommonUtils.emailValidator(emailEditText.getText().toString())) {
                        if (!passwordEditText.getText().toString().equals("")) {
                            progressDialog = new ProgressDialog(context);
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage("Loading...");
                            progressDialog.show();

                            jsonObject = new JsonObject();
                            jsonObject.addProperty("userName", userNameEditText.getText().toString());

                            MD5 md5 = new MD5();
                            jsonObject.addProperty("password", md5.getMD5(passwordEditText.getText().toString()));

                            jsonObject.addProperty("email", emailEditText.getText().toString());

                            PostSignUp postSignUp = new PostSignUp(context);
                            postSignUp.checkServerAvailability(2);
                        } else {
                            Toast.makeText(context, "Enter Password", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(context, "Enter Valid Email", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, "Enter Email", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(context, "Enter UserName", Toast.LENGTH_SHORT).show();
            }
        });

        TextView goToLogin = findViewById(R.id.goToLogin);

        goToLogin.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });

    }

    private class PostSignUp extends PostRequest{
        public PostSignUp(Context context){
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                super.postRequest(urlSignUp, jsonObject);
            }else {
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }

        public void onFinish(JSONArray jsonArray){
            progressDialog.dismiss();

            try{
                JSONObject jsonObject1 = (JSONObject) jsonArray.get(0);

                if(jsonObject1.getBoolean("status")){
                    dialog = new Dialog(context);
                    dialog.setCancelable(true);
                    dialog.setContentView(R.layout.otp);

                    final EditText otp = dialog.findViewById(R.id.otp);
                    Button ok = dialog.findViewById(R.id.ok);

                    ok.setOnClickListener(v -> {
                        if(otp.getText().toString().length() > 0) {
                            progressDialog = new ProgressDialog(context);
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage("Loading...");
                            progressDialog.show();

                            jsonObject = new JsonObject();
                            jsonObject.addProperty("otp", otp.getText().toString());

                            PostOTP postOTP = new PostOTP(context);
                            postOTP.checkServerAvailability(2);
                        }else{
                            Toast.makeText(context, "Enter OTP!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    dialog.show();
                }else {
                    Toast.makeText(context, "SignUp not completed!", Toast.LENGTH_SHORT).show();
                }

            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    private class PostOTP extends PostRequest{
        public PostOTP(Context context){
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                super.postRequest(urlOTPVerifier, jsonObject);
            }else {
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                dialog.dismiss();
            }
        }

        public void onFinish(JSONArray jsonArray){
            progressDialog.dismiss();
            dialog.dismiss();

            try{
                JSONObject jsonObject = (JSONObject) jsonArray.get(0);

                if(jsonObject.getBoolean("status")){
                    Toast.makeText(context, "SignUp Successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(context, "SignUp Unsuccessful!", Toast.LENGTH_SHORT).show();
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
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}