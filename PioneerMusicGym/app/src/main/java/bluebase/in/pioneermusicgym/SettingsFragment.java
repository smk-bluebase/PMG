package bluebase.in.pioneermusicgym;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.CollapsibleActionView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.concurrent.Callable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.content.Context.AUDIO_SERVICE;

public class SettingsFragment extends Fragment {
    Context context;

    SwitchMaterial getNotification;
    AutoCompleteTextView languagesPreferred;
    SeekBar volumeSeekBar;

    String[] languagesArr = {"Tamil", "Malayalam", "Hindi"};

    AudioManager audioManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        height = (int) (height / 1.53);

        ImageView background = view.findViewById(R.id.background);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 200, height);
        background.setLayoutParams(layoutParams);

        getNotification = view.findViewById(R.id.getNotificationSwitch);
        languagesPreferred = view.findViewById(R.id.languagePreferred);
        volumeSeekBar = view.findViewById(R.id.volumeSeekBar);

        audioManager = (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item, languagesArr);
        languagesPreferred.setAdapter(adapter);

        CommonUtils.startDatabaseHelper(context);
        getNotification.setChecked(CommonUtils.dataBaseHelper.getNotification() == 1);
        languagesPreferred.setText(languagesArr[CommonUtils.dataBaseHelper.getLanguageCode()], false);
        CommonUtils.closeDataBaseHelper();

        getNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            CommonUtils.startDatabaseHelper(context);
            CommonUtils.dataBaseHelper.setGetNotification(isChecked ? 1 : 0);
            CommonUtils.closeDataBaseHelper();
        });

        languagesPreferred.setOnItemClickListener((parent, view1, position, id) -> {
            CommonUtils.startDatabaseHelper(context);
            CommonUtils.dataBaseHelper.setLanguageCode(position);
            CommonUtils.closeDataBaseHelper();
        });

        volumeSeekBar.setMax(100);
        volumeSeekBar.setProgress((int) Math.round((double) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 6.7));

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Toast.makeText(context, "Volume : " + progress, Toast.LENGTH_SHORT).show();
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) Math.round(((double) progress / 6.7)), AudioManager.FLAG_SHOW_UI);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //  Do Nothing!
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //  Do Nothing!
            }
        });

    }

}
