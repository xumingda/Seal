package com.test.RongYun.base;


import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;


import com.test.RongYun.R;
import com.test.RongYun.message.ContactNotificationMessageProvider;
import com.test.RongYun.message.TestMessage;
import com.test.RongYun.message.TestMessageProvider;
import com.test.RongYun.utils.NLog;
import com.test.RongYun.utils.SealUserInfoManager;
import com.test.RongYun.utils.SharedPrefrenceUtils;
import com.test.RongYun.widget.SealAppContext;

import io.rong.imageloader.core.DisplayImageOptions;
import io.rong.imageloader.core.display.FadeInBitmapDisplayer;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.RealTimeLocationMessageProvider;
import io.rong.imlib.RongIMClient;

import static io.rong.imkit.utils.SystemUtils.getCurProcessName;

public class BaseApplication extends Application {
    /** 全局Context，原理是因为Application类是应用最先运行的，所以在我们的代码调用时，该值已经被赋值过了 */
    private static BaseApplication mInstance;
    /** 主线程ID */
    private static int mMainThreadId = -1;
    /** 主线程 */
    private static Thread mMainThread;
    /** 主线程Handler */
    private static Handler mMainThreadHandler;
    /** 主线程Looper */
    private static Looper mMainLooper;
    public Vibrator mVibrator;
    private static DisplayImageOptions options;
    @Override
    public void onCreate() {
        super.onCreate();
        mMainThreadId = android.os.Process.myTid();
        mMainThread = Thread.currentThread();
        mMainThreadHandler = new Handler();
        mMainLooper = getMainLooper();
        mInstance = this;
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        RongIM.setServerInfo("nav.cn.ronghub.com", "up.qbox.me");
        RongIM.init(this);
        NLog.setDebug(true);//Seal Module Log 开关
        SealAppContext.init(this);
        /*初始化volley*/
        MyVolley.init(this);
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.de_default_portrait)
                .showImageOnFail(R.mipmap.de_default_portrait)
                .showImageOnLoading(R.mipmap.de_default_portrait)
                .displayer(new FadeInBitmapDisplayer(300))
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        try {
            RongIM.registerMessageTemplate(new ContactNotificationMessageProvider());
            RongIM.registerMessageTemplate(new RealTimeLocationMessageProvider());
            RongIM.registerMessageType(TestMessage.class);
            RongIM.registerMessageTemplate(new TestMessageProvider());


        } catch (Exception e) {
            e.printStackTrace();
        }
        openSealDBIfHasCachedToken();
        RongIM.setConnectionStatusListener(new RongIMClient.ConnectionStatusListener() {
            @Override
            public void onChanged(ConnectionStatus status) {
                if (status == ConnectionStatus.TOKEN_INCORRECT) {
                    final String cacheToken = SharedPrefrenceUtils.getString(BaseApplication.this,"token");
                    if (!TextUtils.isEmpty(cacheToken)) {
                        RongIM.connect(cacheToken, SealAppContext.getInstance().getConnectCallback());
                    } else {
                        Log.e("seal", "token is empty, can not reconnect");
                    }
                }
            }
        });
    }
    public static DisplayImageOptions getOptions() {
        return options;
    }
    //旋转适配,如果应用屏幕固定了某个方向不旋转的话(比如qq和微信),下面可不写.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    public static BaseApplication getApplication() {
        return mInstance;
    }

    /** 获取主线程ID */
    public static int getMainThreadId() {
        return mMainThreadId;
    }

    /** 获取主线程 */
    public static Thread getMainThread() {
        return mMainThread;
    }

    /** 获取主线程的handler */
    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    /** 获取主线程的looper */
    public static Looper getMainThreadLooper() {
        return mMainLooper;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    private void openSealDBIfHasCachedToken() {
        String cachedToken = SharedPrefrenceUtils.getString(BaseApplication.this,"token");
        if (!TextUtils.isEmpty(cachedToken)) {
            String current = getCurProcessName(this);
            String mainProcessName = getPackageName();
            if (mainProcessName.equals(current)) {
                SealUserInfoManager.getInstance().openDB();
            }
        }
    }
}