package com.protocloud.googlelocationdemo.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.protocloud.googlelocationdemo.R;

public class BaseActivity extends AppCompatActivity implements OnClickListener, View.OnTouchListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_main);


    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public View setTouchNClick(int id) {

        View v = findViewById(id);
        v.setOnClickListener(this);
        return v;
    }

    public View setClick(int id) {

        View v = findViewById(id);
        v.setOnClickListener(this);
        return v;
    }

    public View setTouch(int id) {
        View v = findViewById(id);
        v.setOnTouchListener(this);
        return v;
    }

    public void setViewSelected(int id, boolean flag) {
        View v = findViewById(id);
        v.setSelected(flag);
    }

    public void setViewVisibility(int id, int flag) {
        View v = findViewById(id);
        v.setVisibility(flag);
    }

    public void setTransfMethodPassword(int id) {
        EditText v = findViewById(id);
        v.setTransformationMethod(new PasswordTransformationMethod());
    }

    public void setTransfMethodText(int id) {
        EditText v = findViewById(id);
        v.setTransformationMethod(null);
    }

    public void setTextViewText(int id, String text) {
        ((TextView) findViewById(id)).setText(text);
    }

    public void setEditText(int id, String text) {
        ((EditText) findViewById(id)).setText(text);
    }

    public String getEditTextText(int id) {
        return ((EditText) findViewById(id)).getText().toString().trim();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)

    public void setCheckBox(int id, boolean checkFlag) {
        ((CheckBox) findViewById(id)).setChecked(checkFlag);
    }

    public String getTextViewText(int id) {
        return ((TextView) findViewById(id)).getText().toString().trim();
    }

    public String getButtonText(int id) {
        return ((Button) findViewById(id)).getText().toString();
    }

    public void setButtonText(int id, String text) {
        ((Button) findViewById(id)).setText(text);
    }

    public void replaceButtoImageWith(int replaceId, int drawable) {
        ((Button) findViewById(replaceId)).setBackgroundResource(drawable);
    }

    public void setButtonSelected(int id, boolean flag) {
        ((Button) findViewById(id)).setSelected(flag);
    }

    public boolean isButtonSelected(int id) {
        return ((Button) findViewById(id)).isSelected();
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub

    }

    public void startActivity(Activity activity, Class<?> targetActivity, boolean closeCurrent, boolean closeAll) {
        Intent intent = new Intent(activity, targetActivity);
        if (closeAll) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        activity.startActivity(intent);
        if (closeCurrent) {
            activity.finish();
        }
    }

    public void makeToast(String message) {
        Toast.makeText(this, "" + message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    /**
     * Check for google play services availability
     */
    public boolean isPlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            }
            return false;
        }
        return true;
    }

}
