package bluebase.in.pioneermusicgym;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Animation zoom_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);

        ImageView logo = findViewById(R.id.splashScreenImage);

        logo.startAnimation(zoom_in);

        new Thread() {
            public void run() {
                try {
                    sleep(2 * 1000);

                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);

                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}