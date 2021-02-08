package bluebase.in.pioneermusicgym;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    Context context = this;
    JsonObject jsonObject;
    EditText userNameEditText1;
    EditText passwordEditText;

    String urlLogin = CommonUtils.IP + "/PMG/pmg_android/login/login.php";
    String urlForgotPassword = CommonUtils.IP + "/PMG/pmg_android/login/forgotPasswordGenerator.php";

    ProgressDialog progressDialog;
    Dialog dialog;

    boolean isRememberPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        height = (int) (height / 1.7);

        ImageView background = findViewById(R.id.background);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 200, height);
        background.setLayoutParams(layoutParams);

        CommonUtils.startDatabaseHelper(context);

        userNameEditText1 = findViewById(R.id.userName);
        passwordEditText = findViewById(R.id.password);

        CheckBox rememberPassword = findViewById(R.id.rememberPassword);

        JSONArray loginCredentials = CommonUtils.dataBaseHelper.selectUserMaster();

        if(loginCredentials.length() > 0) {
            try {

                JSONObject jsonObject = (JSONObject) loginCredentials.get(0);

                userNameEditText1.setText(jsonObject.getString("userName"));
                passwordEditText.setText(jsonObject.getString("password"));

                rememberPassword.setChecked(true);

            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        userNameEditText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do Nothing!
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do Nothing!
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(rememberPassword.isChecked()) {
                    isRememberPassword = true;
                }else {
                    isRememberPassword = false;
                }
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do Nothing!
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do Nothing!
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(rememberPassword.isChecked()) {
                    isRememberPassword = true;
                }else {
                    isRememberPassword = false;
                }
            }
        });

        rememberPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            CommonUtils.dataBaseHelper.deleteUserMaster();
            if(isChecked) {
                if(userNameEditText1.getText().toString().equals("") || passwordEditText.getText().toString().equals("")) {
                    isRememberPassword = true;
                }else {
                    isRememberPassword = false;
                    CommonUtils.dataBaseHelper.insertUserMaster(userNameEditText1.getText().toString(), passwordEditText.getText().toString());
                }
            }else {
                isRememberPassword = false;
            }
        });

        Button login = findViewById(R.id.login);
        TextView signUp = findViewById(R.id.signUp);
        TextView forgotPassword = findViewById(R.id.forgotPassword);

        login.setOnClickListener(view -> {
            if(!userNameEditText1.getText().toString().equals("")){
                if(!passwordEditText.getText().toString().equals("")){
                    if(isRememberPassword){
                        CommonUtils.dataBaseHelper.deleteUserMaster();
                        CommonUtils.dataBaseHelper.insertUserMaster(userNameEditText1.getText().toString(), passwordEditText.getText().toString());
                        CommonUtils.closeDataBaseHelper();
                    }

                    progressDialog = new ProgressDialog(context);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();

                    jsonObject = new JsonObject();
                    jsonObject.addProperty("userName", userNameEditText1.getText().toString());

                    MD5 md5 = new MD5();
                    jsonObject.addProperty("password", md5.getMD5(passwordEditText.getText().toString()));

                    PostLogin postLogin = new PostLogin(context);
                    postLogin.checkServerAvailability(2);
                }else{
                    Toast.makeText(context, "Enter Password", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(context, "Enter Username", Toast.LENGTH_SHORT).show();
            }
        });

        signUp.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        forgotPassword.setOnClickListener(view -> {
            dialog = new Dialog(context);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.forgot_password);

            final EditText userNameEditText2 = dialog.findViewById(R.id.userName);
            Button submit = dialog.findViewById(R.id.submit);

            submit.setOnClickListener(v -> {
                if(userNameEditText2.getText().toString().length() > 0) {
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();

                    jsonObject = new JsonObject();
                    jsonObject.addProperty("userName", userNameEditText2.getText().toString());

                    PostForgotPassword postForgotPassword = new PostForgotPassword(context);
                    postForgotPassword.checkServerAvailability(2);
                }else{
                    Toast.makeText(context, "Enter Username", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.show();
        });

    }

    private class PostLogin extends PostRequest{
        public PostLogin(Context context){
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                super.postRequest(urlLogin, jsonObject);
            }else{
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }

        public void onFinish(JSONArray jsonArray){
            progressDialog.dismiss();

            try{
                JSONObject jsonObject = (JSONObject) jsonArray.get(0);

                if(jsonObject.getBoolean("status")){
                    CommonUtils.userId = jsonObject.getInt("userId");
                    CommonUtils.userName = userNameEditText1.getText().toString();
                    CommonUtils.email = jsonObject.getString("email");
                    CommonUtils.startDatabaseHelper(context);
                    CommonUtils.dataBaseHelper.insertUserIdIntoUserMaster(jsonObject.getInt("userId"), CommonUtils.userName);
                    CommonUtils.closeDataBaseHelper();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(),"Username or Password Incorrect",Toast.LENGTH_SHORT).show();
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        }

    }

    private class PostForgotPassword extends PostRequest{
        public PostForgotPassword(Context context){
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                super.postRequest(urlForgotPassword, jsonObject);
            }else{
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
                    Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"UserName not registered",Toast.LENGTH_SHORT).show();
                }

            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

}