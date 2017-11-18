package com.etcxc.android.utils;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.etcxc.android.BuildConfig;
import com.etcxc.android.R;
import com.etcxc.android.base.App;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.etcxc.android.utils.UIUtils.getString;

/**
 * 系统级工具类
 * Created by xwpeng on 2017/6/15.
 */

public class SystemUtil {
    private static String TAG = "SystemUtil";
    public static Pattern phonePattern = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); //手机号正则

    public static String verifyPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            ToastUtils.showToast(getString(R.string.phone_number_notallow_empty));
            return "";
        }
        Matcher m = SystemUtil.phonePattern.matcher(phoneNumber);
        if (m.matches()) {
            return phoneNumber;
        } else ToastUtils.showToast(R.string.please_input_correct_phone_number);
        return "";
    }

    public static String getVersionName() {
        String name = BuildConfig.VERSION_NAME;
        if (TextUtils.isEmpty(name)) {
            LogUtil.w(TAG, "BuildConfig.VERSION_NAME is null or empty. So, get it from PackageInfo.");
            try {
                PackageInfo info = App.get().getPackageManager().getPackageInfo(App.get().getPackageName(), 0);
                name = info.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                LogUtil.e(TAG, "getVersionName", e);
            }
        }
        return name == null ? "" : name;
    }

    public static String getVersionName4CheckUpdate() {
        return getVersionNameByLength(4);
    }

    private static String getVersionNameByLength(int length) {
        String name = getVersionName();
        if (TextUtils.isEmpty(name)) return "";
        String[] names = name.split("[.]");
        if (names.length >= length) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(names[i]);
                if (i < length - 1) {
                    builder.append(".");
                }
            }
            name = builder.toString();
        }
        return name;
    }

    /**
     * 判断系统中是否存在可以启动的相机应用
     *
     * @return 存在返回true，不存在返回false
     */
    public static boolean hasCamera() {
        PackageManager packageManager = App.get().getPackageManager();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 安装apk
     */
    public static void installApk(Context context, File file) {
        if (file == null) {
            LogUtil.w(TAG, "installApk: file is null.");
            return;
        }
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri uri = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                    ? FileProvider.getUriForFile(App.get(), BuildConfig.APPLICATION_ID + ".fileprovider", file)
                    : Uri.fromFile(file);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            context.startActivity(intent);
        } else {
            LogUtil.w(TAG, "installApk: failed, path=" + file.getAbsolutePath());
        }
    }

    public static final File downloadDir() {
        File f1 = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            f1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (f1.exists()) {
                f1 = new File(f1, "XC");
                if (!f1.exists()) {
                    f1.mkdir();
                }
            }
        }
        return f1;
    }
    /**
     * 网络请求模板，复制粘贴提高效率
     */
    private void netModel() {
/*        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {

            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this)).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                LogUtil.e(TAG, "net", throwable);
                ToastUtils.showToast(R.string.request_failed);
            }
        });*/
    }

    /**
     * 拨打电话（直接拨打电话）
     * @param phoneNum 电话号码
     */
    public static void callPhone(Context mContext,String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        if (ActivityCompat.checkSelfPermission(App.get(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        mContext.startActivity(intent);
    }

    /**
     * 拨打电话（跳转到拨号界面，用户手动点击拨打）
     * @param phoneNum 电话号码
     */
    public static void dialPhone(Context mContext,String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        mContext.startActivity(intent);
    }


    public static void showCallDialog(Context context,String[] phones){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("请选择手机号"); //设置标题
        //builder.setMessage("是否确认退出?"); //设置内容
//        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        //设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(phones,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dialPhone(context,phones[which]);
            }
        });

        builder.setPositiveButton("取消",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    //判断微信是否可用
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        // 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        // 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断qq是否可用
     *
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否在主线程
     */
    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    public static void logTid(String attach) {
        LogUtil.e(TAG, attach + " ,on main: " + SystemUtil.isMainThread() + " ,tid: " + android.os.Process.myTid() );
    }
}
