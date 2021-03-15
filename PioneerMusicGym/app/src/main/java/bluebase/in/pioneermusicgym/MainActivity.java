package bluebase.in.pioneermusicgym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Context context = this;
    private DrawerLayout drawer;

    public static NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(CommonUtils.email == null){
            CommonUtils.startDatabaseHelper(context);
            CommonUtils.dataBaseHelper.deleteNotifications();
            CommonUtils.closeDataBaseHelper();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }else {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            params.screenBrightness = 1;
            getWindow().setAttributes(params);

            setVolumeControlStream(AudioManager.STREAM_MUSIC);

            Toolbar toolBar = findViewById(R.id.toolBar);
            setSupportActionBar(toolBar);

            drawer = findViewById(R.id.patient_drawer_layout);

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerStateChanged(int newState) {
                    if (newState == DrawerLayout.STATE_SETTLING) {
                        if (!drawer.isDrawerOpen(GravityCompat.START)) {
                            getWindow().setStatusBarColor(Color.TRANSPARENT);
                        } else {
                            getWindow().setStatusBarColor(getResources().getColor(R.color.c2));
                        }
                    }
                }
            });

            navigationView = findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);
            navigationView.setNavigationItemSelectedListener(this);
            TextView emailTextView = headerView.findViewById(R.id.email);
            emailTextView.setText(CommonUtils.email);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            ImageView backButton = headerView.findViewById(R.id.backButton);
            backButton.setOnClickListener(v -> drawer.closeDrawer(GravityCompat.START));

            if (savedInstanceState == null) {
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack("homeFragment")
                        .replace(R.id.fragment_container, new HomeFragment(), "homeFragment")
                        .commit();
                navigationView.setCheckedItem(R.id.nav_home);
            }

        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case R.id.nav_home:
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack("homeFragment")
                        .replace(R.id.fragment_container, new HomeFragment(), "homeFragment")
                        .commit();
                break;

            case R.id.nav_library:
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack("libraryFragment")
                        .replace(R.id.fragment_container, new LibraryFragment(), "libraryFragment")
                        .commit();
                break;

            case R.id.nav_music_player:
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack("musicPlayerFragment")
                        .replace(R.id.fragment_container, new MusicPlayerFragment(), "musicPlayerFragment")
                        .commit();
                break;

            case R.id.nav_playlists:
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack("playlistsFragment")
                        .replace(R.id.fragment_container, new PlaylistsFragment(), "playlistsFragment")
                        .commit();
                break;

            case R.id.nav_favourites:
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack("favouritesFragment")
                        .replace(R.id.fragment_container, new FavouritesFragment(), "favouritesFragment")
                        .commit();
                break;

            case R.id.nav_profile:
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack("profileFragment")
                        .replace(R.id.fragment_container, new ProfileFragment(), "profileFragment")
                        .commit();
                break;

            case R.id.nav_change_password:
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack("changePassword")
                        .replace(R.id.fragment_container, new ChangePasswordFragment(), "changePassword")
                        .commit();
                break;

            case R.id.nav_settings:
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack("settingsFragment")
                        .replace(R.id.fragment_container, new SettingsFragment(), "settingsFragment")
                        .commit();
                break;

            case R.id.nav_rate:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + this.getPackageName())));
                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
                }
                break;

            case R.id.nav_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;


            default:
                // Do Nothing!

        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent != null){
            try{
                boolean isNotification = intent.getBooleanExtra("isNotification", false);
                if(isNotification){
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .addToBackStack("musicPlayerFragment")
                            .replace(R.id.fragment_container, new MusicPlayerFragment(), "musicPlayerFragment")
                            .commit();
                    navigationView.setCheckedItem(R.id.nav_music_player);

                    CommonUtils.fromNotification = true;
                }

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed(){
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            int fragmentsInStack = getSupportFragmentManager().getBackStackEntryCount();

            if (fragmentsInStack > 1) {
                getSupportFragmentManager().popBackStack();
            }else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setTitle("Pioneer Music Gym");
                alertDialogBuilder.setMessage("Do you want to logout?");
                alertDialogBuilder.setPositiveButton(android.R.string.ok,
                        (dialog, id) -> {
                            dialog.cancel();
                            CommonUtils.startDatabaseHelper(context);
                            CommonUtils.dataBaseHelper.deleteNotifications();
                            CommonUtils.closeDataBaseHelper();
                            if(CommonUtils.isBound){
                                MusicPlayerFragment.stopPlayer(MusicPlayerFragment.musicPlayerIntent);
                                CommonUtils.fromNotification = false;
                                CommonUtils.isPlaying = false;
                            }
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        });
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(CommonUtils.isBound) {
            MusicPlayerFragment.stopPlayer(MusicPlayerFragment.musicPlayerIntent);
            CommonUtils.fromNotification = false;
            CommonUtils.isPlaying = false;
        }
    }
}