package com.daejong.seoulpharm.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.daejong.seoulpharm.R;
import com.daejong.seoulpharm.db.DBHelper;
import com.daejong.seoulpharm.model.PharmItem;
import com.daejong.seoulpharm.widget.NotoTextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    /** 두 가지 경우를 체크
     * 1) 필요한 Permission들이 Runtime으로 허가되었는가? (API >= 23)
     * 2) DB가 Initialize 되었는가?
     */

    private static final int MESSAGE_PERMISSION_CHECKED = 1;
    private static final int MESSAGE_DB_INITIALIZED = 2;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_PERMISSION_CHECKED :
                    checkDBInitialized();
                    break;
                case MESSAGE_DB_INITIALIZED :
                    mHandler.postDelayed(goToMainRunnable, 2000);
                    break;
            }
        }
    };

    Runnable goToMainRunnable = new Runnable() {
        @Override
        public void run() {
            if (isFirstLaunched) {
                startActivity(new Intent(SplashActivity.this, TutorialActivity.class));
                finish();
            } else {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
            mHandler.removeCallbacks(goToMainRunnable);
        }
    };

    DBHelper db;

    NotoTextView noticeTextView;

    public static final String IS_FIRST_LAUNCHED = "IS_FIRST_LAUNCHED";
    boolean isFirstLaunched;

    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
        setContentView(R.layout.activity_splash);

        db = new DBHelper(SplashActivity.this);
        if (checkAndRequestPermissions()) {
            mHandler.sendEmptyMessage(MESSAGE_PERMISSION_CHECKED);
        }

        // Check First Launched
        mPrefs = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
        isFirstLaunched = mPrefs.getBoolean("IS_FIRST_LAUNCHED", true);

        noticeTextView = (NotoTextView) findViewById(R.id.notice_text);
        noticeTextView.setText("");
    }

    /** 1. DB Initialize
     *   현재 단말기 DB의 data 수가 550개인지 확인하고
     *   seoul_pharm_data_complete.csv 파일을 읽어서 Database에 추가
     */
    private static final int DB_ROW_COUNT = 550;
    private boolean checkDBInitialized() {
        if (db.getPharmRowCount() != DB_ROW_COUNT) {
            noticeTextView.setText("초기 DB를 설정중입니다.");
            InputStream inputStream = getResources().openRawResource(R.raw.seoul_pharm_data_complete);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            try {
                String csvLine;
                while ((csvLine = reader.readLine()) != null) {
                    String[] row = csvLine.split(",");
                    PharmItem item = new PharmItem();
                    item.setMainKey(row[0]);

                    item.setNameKor(row[1]);
                    item.setNameEng(row[2]);
                    item.setNameChi(row[3]);

                    item.setAddressKor(row[4]);
                    item.setAddressEng(row[5]);

                    item.sethKorCity(row[6]);
                    item.sethKorGu(row[7]);
                    item.sethKorDong(row[8]);
                    item.setTel(row[9]);

                    item.setAvailLanKor(row[10]);
                    item.setAvailLanEng(row[11]);
                    item.setAvailLanChi(row[12]);

                    item.setLongtitude(row[13]);
                    item.setLatitude(row[14]);

                    db.addPharmItem(item);
//                    Log.d("!!! DB Initializing !!!", "DATA : " + row[1]);
                }
                mHandler.sendEmptyMessage(MESSAGE_DB_INITIALIZED);
                noticeTextView.setText("");
                return true;
            } catch (IOException ex) {
                throw new RuntimeException("Error iv reading CSV file:" + ex);
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException("Error while closing input strem:" + e);
                }
            }
        } else {
            mHandler.sendEmptyMessage(MESSAGE_DB_INITIALIZED);
            return true;
        }
    }



    /** 2. Request permissions at Runtime
     *   ACCESS_FINE_LOCATION
     *   ACCESS_COARSE_LOCATION
     *   CAMERA
     *   permission들에 대한 허가 요청 수행
     */

    String[] permissions= new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA };


    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 100;
    private boolean checkAndRequestPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();

        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(SplashActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 허가
                    // 해당 권한을 사용해서 작업을 진행할 수 있음
                    mHandler.sendEmptyMessage(MESSAGE_PERMISSION_CHECKED);

                } else {
                    // 권한 거부
                    // 사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행.
                    Toast.makeText(SplashActivity.this, "App 실행 시 필요한 권한을 허가해주세요", Toast.LENGTH_SHORT).show();
                    mHandler.removeMessages(MESSAGE_DB_INITIALIZED);
                    mHandler.removeMessages(MESSAGE_PERMISSION_CHECKED);
                    finish(); // 종료

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    }

    @Override
    protected void onDestroy() {
        mHandler.removeMessages(MESSAGE_PERMISSION_CHECKED);
        mHandler.removeMessages(MESSAGE_DB_INITIALIZED);
        super.onDestroy();
    }
}
