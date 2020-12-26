package cugxg.com.onekeyalarm.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import cugxg.com.onekeyalarm.R;
import cugxg.com.onekeyalarm.base.BaseApplication;
import cugxg.com.onekeyalarm.mvp.Register.RegisterActivity;
import cugxg.com.onekeyalarm.mvp.MainActivity;


/**
 * Created by zhouyou on 2016/6/27.
 * Class desc: ui 操作相关封装
 */
public class UiUtils {

    /**
     * 获取上下文
     */
    public static Context getContext() {
        return BaseApplication.getContext();
    }

    /**
     * 获取资源操作类
     */
    public static Resources getResources() {
        return getContext().getResources();
    }

    /**
     * 获取字符串资源
     *
     * @param id 资源id
     * @return 字符串
     */
    public static String getString(int id) {
        return getResources().getString(id);
    }

    /**
     * 获取字符串数组资源
     *
     * @param id 资源id
     * @return 字符串数组
     */
    public static String[] getStringArray(int id) {
        return getResources().getStringArray(id);
    }

    /**
     * 获取颜色资源
     */
    public static int getColor(int id) {
        return ContextCompat.getColor(getContext(), id);
    }

    /**
     * 获取颜色资源
     *
     * @param id    资源id
     * @param theme 样式
     * @return
     */
    public static int getColor(int id, Resources.Theme theme) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getResources().getColor(id, theme);
        }
        return getResources().getColor(id);
    }

    /**
     * 获取drawable资源
     *
     * @param id 资源id
     * @return
     */
    public static Drawable getDrawable(int id) {
        return ContextCompat.getDrawable(getContext(), id);
    }

    /**
     * 通过图片名称获取图片资源 id
     *
     * @param imageName 图片名称
     * @return 图片资源 id
     */
    public static int getImageResIdByName(String imageName) {
        return getResources().getIdentifier(imageName, "mipmap"
                , AppUtils.getPackageName());
    }

    /**
     * 加载布局（使用View方式）
     *
     * @param resource 布局资源id
     * @return View
     */
    public static View inflate(int resource) {
        return View.inflate(getContext(), resource, null);
    }

    /**
     * 检查输入的内容是否为空
     */
    public static boolean checkEmpty(EditText editText) {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            ToastUtils.show(UiUtils.getContext(), UiUtils.getString(R.string.hint_empty));
            return true;
        }
        return false;
    }

    /**
     * 设置透明状态栏
     *
     * @param activity
     */
    public static void setStatusBar(Activity activity) {

        // 5.0及以上版本，设置透明状态栏
        Window window = activity.getWindow();

        // 添加标志位
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // 设置为透明
        window.setStatusBarColor(0);
    }



    /**
     * 进入首页
     */
    public static void enterHomePage(Context context) {
        ActivityManager.getInstance().popAllActivity();
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

    /**
     * 进入登录页
     *
     * @param context 上下文
     */
    public static void enterLoginPage(Context context) {
        enterLoginPage(context, false);
    }

    /**
     * 进入登录页
     *
     * @param context  上下文
     * @param isFinish 是否关闭当前 Activity
     */
    public static void enterLoginPage(Context context, boolean isFinish) {
        ActivityManager.getInstance().popAllActivity();
        Intent intent = new Intent(context, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        if (isFinish && context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

    public static ColorStateList getColorList(int resId) {
        return ContextCompat.getColorStateList(UiUtils.getContext(), resId);
    }

    public static void setCompoundDrawables(TextView textView, Drawable left, Drawable top, Drawable right, Drawable bottom) {
        textView.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

    /**
     * 显示不带 null 的字符
     */
    public static String show(String text) {
        return text != null ? text : "";
    }

    /**
     * 根据自定义属性获取对应颜色值.
     *
     * @param context  上下文
     * @param attrs    自定义属性
     * @param defColor 默认颜色
     * @return 颜色
     */
    public static int getColor(Context context, int attrs, int defColor) {
        int[] customAttrs = {attrs};
        TypedArray a = context.obtainStyledAttributes(customAttrs);
        int color = a.getColor(0, defColor);
        a.recycle();
        return color;
    }

    /**
     * 根据自定义属性获取对应资源 id.
     *
     * @param context 上下文
     * @param attrs   自定义属性
     * @param defId   默认 id
     * @return 资源 id
     */
    public static int getResourceId(Context context, int attrs, int defId) {
        int[] customAttrs = {attrs};
        TypedArray a = context.obtainStyledAttributes(customAttrs);
        int id = a.getResourceId(0, defId);
        a.recycle();
        return id;
    }
}
