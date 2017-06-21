package com.etcxc.android.ui.activity;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.etcxc.android.utils.PermissionUtil;

/**
 * 申请权限Activity
 */
public class PermissionRequestingActivity extends Activity {
    private String[] mPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissions = getIntent().getStringArrayExtra("permissions");
        if (mPermissions == null || mPermissions.length == 0) {
            finish();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(mPermissions, 1);
            } else {
                int[] results = new int[mPermissions.length];
                for (int i = 0; i < mPermissions.length; i++) {
                    results[i] = PackageManager.PERMISSION_GRANTED;
                }
                PermissionUtil.setResults(this, mPermissions, results);
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            PermissionUtil.setResults(this, permissions, grantResults);
        }
        finish();
    }
}
