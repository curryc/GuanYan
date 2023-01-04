package com.scu.guanyan.utils.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: GuanYan
 * @author: 陈博文
 * @create: 2022-11-06 17:04
 * @description: 权限工具
 **/
public class PermissionUtils {
    /**
     * 第一次检查权限，用在打开应用的时候请求应用需要的所有权限
     *
     * @param context
     * @param requestCode 请求码
     * @param permission  权限数组
     * @return
     */
    public static boolean checkPermissionFirst(Activity context, int requestCode, String[] permission) {
        List<String> permissions = new ArrayList<String>();
        for (String per : permission) {
            int permissionCode = ActivityCompat.checkSelfPermission(context, per);
            if (permissionCode != PackageManager.PERMISSION_GRANTED) {
                permissions.add(per);
            }
        }
        if (!permissions.isEmpty()) {
            // 在这个Activity中申请权限,通过requestCode回调
            ActivityCompat.requestPermissions(context, permissions.toArray(new String[permissions.size()]), requestCode);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 第二次检查权限，用在某个操作需要某个权限的时候调用
     *
     * @param context
     * @param requestCode 请求码
     * @param permission  权限数组
     * @return
     */
    public static boolean checkPermissionSecond(Context context, int requestCode, String[] permission) {
        List<String> permissions = new ArrayList<String>();
        for (String per : permission) {
            int permissionCode = ActivityCompat.checkSelfPermission(context, per);
            if (permissionCode != PackageManager.PERMISSION_GRANTED) {
                permissions.add(per);
            }
        }
        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) context, permissions.toArray(new String[permissions.size()]), requestCode);

            /*跳转到应用详情，让用户去打开权限*/
            Intent localIntent = new Intent();
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 9) {
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
            } else if (Build.VERSION.SDK_INT <= 8) {
                localIntent.setAction(Intent.ACTION_VIEW);
                localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
            }
            context.startActivity(localIntent);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 第二次检查权限，用在某个操作需要某个权限的时候调用
     *
     * @param context
     * @param requestCode 请求码
     * @param permission  权限
     * @return
     */
    public static boolean checkPermissionSecond(Context context, int requestCode, String permission, Intent intent) {
        int permissionCode = ActivityCompat.checkSelfPermission(context, permission);
        if (permissionCode != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, requestCode);
            context.startActivity(intent);
            return false;
        } else {
            return true;
        }

    }
}
