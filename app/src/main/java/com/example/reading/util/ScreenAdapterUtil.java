
package com.example.reading.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ScreenAdapterUtil
{
    // 对外接口 /////////////////////////////////////////////////////////////////////////////
    // 判断是否是刘海屏
    public static boolean hasNotchScreen(Activity activity)
    {
        if (getInt("ro.miui.notch",activity) == 1 || // 小米刘海屏判断
                hasNotchAtHuawei(activity) || // 华为刘海屏判断
                hasNotchAtOPPO(activity) || // OPPO刘海屏判断
                hasNotchAtVivo(activity) || // Vivo刘海屏判断
                isAndroidP(activity) != null || // Android P版本刘海屏判断
                getBarHeight(activity) >= 80 // 一般状态栏高度超过80可以认为就是刘海屏
            //TODO 各种品牌
        )
        {
            return true;
        }

        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    // Android P 刘海屏判断
    @TargetApi(28)
    @SuppressWarnings({"unchecked","deprecation"})
    private static DisplayCutout isAndroidP(Activity activity){
        View decorView = activity.getWindow().getDecorView();
        if (decorView != null && Build.VERSION.SDK_INT >= 28){
            WindowInsets windowInsets = decorView.getRootWindowInsets();
            if (windowInsets != null)
                return windowInsets.getDisplayCutout();
        }
        return null;
    }

    // 获取状态栏高度
    @SuppressWarnings({"unchecked","deprecation"})
    private static int getBarHeight(Activity activity)
    {
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(resourceId > 0)
        {
            return activity.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private static boolean isXiaomi()
    {
        return "Xiaomi".equals(Build.MANUFACTURER);
    }

    // 小米刘海屏判断
    @SuppressWarnings({"unchecked","deprecation"})
    private static int getInt(String key, Activity activity) {
        int result = 0;
        if (isXiaomi()){
            try {
                ClassLoader classLoader = activity.getClassLoader();
                @SuppressWarnings("rawtypes")
                Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
                //参数类型
                @SuppressWarnings("rawtypes")
                Class[] paramTypes = new Class[2];
                paramTypes[0] = String.class;
                paramTypes[1] = int.class;
                Method getInt = SystemProperties.getMethod("getInt", paramTypes);
                //参数
                Object[] params = new Object[2];
                params[0] = new String(key);
                params[1] = new Integer(0);
                result = (Integer) getInt.invoke(SystemProperties, params);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // 华为刘海屏判断
    @SuppressWarnings({"finally","unchecked","deprecation"})
    private static boolean hasNotchAtHuawei(Context context)
    {
        boolean ret = false;
        try
        {
            ClassLoader classLoader = context.getClassLoader();
            Class HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (Boolean) get.invoke(HwNotchSizeUtil);
        }
        catch (ClassNotFoundException e)
        {
        }
        catch (NoSuchMethodException e)
        {
        }
        catch (Exception e)
        {
        }
        finally
        {
            return ret;
        }
    }

    private static final int VIVO_NOTCH = 0x00000020;//是否有刘海
    private static final int VIVO_FILLET = 0x00000008;//是否有圆角

    // VIVO刘海屏判断
    @SuppressWarnings({"finally","unchecked","deprecation"})
    private static boolean hasNotchAtVivo(Context context)
    {
        boolean ret = false;
        try
        {
            ClassLoader classLoader = context.getClassLoader();
            Class FtFeature = classLoader.loadClass("android.util.FtFeature");
            Method method = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (Boolean) method.invoke(FtFeature, VIVO_NOTCH);
        }
        catch (ClassNotFoundException e)
        {
        }
        catch (NoSuchMethodException e)
        {
        }
        catch (Exception e)
        {
        }
        finally
        {
            return ret;
        }
    }

    // OPPO刘海屏判断
    private static boolean hasNotchAtOPPO(Context context)
    {
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }
}
