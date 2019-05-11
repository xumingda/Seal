package com.test.RongYun.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.android.volley.RequestQueue;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.test.RongYun.utils.Constants;
import com.test.RongYun.utils.FileUtils;
import com.test.RongYun.utils.IOUtils;
import com.test.RongYun.utils.LogUtils;
import com.test.RongYun.utils.MobileUtils;
import com.test.RongYun.utils.UIUtils;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @作者: 许明达
 * @创建时间: 2016-3-22上午10:56:36
 * @版权: 特速版权所有
 * @描述: 网络数据本地缓存 加载数据前 先从本地加载,如果有直接返回;如果没有,再从网络加载
 * @更新人:
 * @更新时间:
 * @更新内容: TODO
 */
public abstract class BaseProtocol<T> {

    private static final String DIR = "json";
    private static final long DURATION = 60 * 60 * 1000 * 24; // 24小时后过期
    private HttpUtils utils;
    SharedPreferences sp = UIUtils.getContext().getSharedPreferences("soda",
            Context.MODE_PRIVATE);

    /**
     * 解析json
     *
     * @param json
     * @return
     */
    protected abstract T parseJson(String json);

    public T loadData(Map<String, String> map) throws HttpException,
            IOException {
        String page = map.get("page");
        String cityId = map.get("city");

        // 1. 到缓存中去取
        // 第0页不从缓存中读取数据
        // 判断用户是否连接网络

        // 从本地取数据:
        // 从网络去数据:

        if (!MobileUtils.isHaveInternet(UIUtils.getContext())) {
            T data = getDataFromLocal(map);
            if (data != null) {
                LogUtils.d("网络连接已经断开,从缓存中取数据");
                return data;
            }
        } else {
            if (page != null && cityId != null) {
                T data = getDataFromLocal(map);
                if (data != null) {
                    LogUtils.d("从缓存中取数据");
                    return data;
                }
            }
            LogUtils.d("从网络中取数据");
            return getDataFromNet(map);
        }
        return null;
    }

    public T loadDataTwice(Map<String, String> map) throws HttpException,
            IOException {
        String page = map.get("page");
        String cityId = map.get("city");
        // 从本地取数据:
        // 从网络去数据:
        T data = null;
        if (page != null && cityId != null) {
            data = getDataFromLocal(map);
        }
        if (MobileUtils.isHaveInternet(UIUtils.getContext())) {
            data = getDataFromNet(map);
        }
        return data;
    }

    public T getDataFromLocal(Map<String, String> map) throws IOException {
        String page = (String) map.get("page");
        String cityId = map.get("city");
        String userId = map.get("id");

        // 存储缓存
        // 1.存储为文件 --->文件名的命名规范
        // ------> getKey() + "." + index
        // 2.文件内容的 --->文件内容中的规范
        // ------> 时间戳 + "\r\n" +json

        // 商场详情
        String plazaId = map.get("plazaId");
        // 帖子详情
        String postId = map.get("postId");

        String name = getApiFun() + "." + page + "." + cityId;
        // 用于缓存plazaActivity页面
        LogUtils.e("name:" + name);
        if (!TextUtils.isEmpty(plazaId)) {
            name += "." + plazaId;
        }
        if (!TextUtils.isEmpty(postId)) {
            name += "." + postId;
        }
        if (!TextUtils.isEmpty(userId)) {
            name += "." + userId;
        }

        File file = new File(FileUtils.getDir(DIR), name);

        // 如果文件不存在，说明本地没有缓存
        if (!file.exists()) {
            // LogUtils.e("文件不存在:" + name);
            return null;
        }

        // 文件存在
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));

            String timeString = reader.readLine();// 时间戳
            long time = Long.valueOf(timeString);

            if (time + DURATION < System.currentTimeMillis()) {
                // 过期了
                LogUtils.e("过期了:" + name);
                return null;
            }

            String json = reader.readLine();// 读json
            LogUtils.d("缓存中的json" + json);
            return parseJson(json);
        } finally {
            IOUtils.close(reader);
        }
    }

    private void write2Local(String page, String json, String cityId,
                             String plazaId, String postId, String userId) throws IOException {
        // 存储缓存
        // 1.存储为文件 --->文件名的命名规范
        // ------> getKey() + "." + index
        // 2.文件内容的 --->文件内容中的规范
        // ------> 时间戳 + "\r\n" +json

        String name = getApiFun() + "." + page + "." + cityId;

        // 用于缓存plazaActivity页面
        if (!TextUtils.isEmpty(plazaId)) {
            name += "." + plazaId;
        }

        if (!TextUtils.isEmpty(postId)) {
            name += "." + postId;
        }

        if (!TextUtils.isEmpty(userId)) {
            name += "." + userId;
        }

        File file = new File(FileUtils.getDir(DIR), name);

        // 往文件中写数据
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(System.currentTimeMillis() + "");// 写时间戳
            writer.write("\r\n");// 换行
            writer.write(json);
        } finally {
            IOUtils.close(writer);
        }
    }

    /**
     * 无需缓存的网络请求
     *
     * @param map
     * @return
     * @throws Exception
     */
    // public T loadDataNoCache(Map<String, String> map) throws Exception {
    // if (utils == null) {
    // utils = new HttpUtils();
    // utils.configTimeout(1000 * 10);
    // }
    // String url = Constants.SERVER_URL + getApiFun();
    //
    // Set<Entry<String, String>> entrySet = map.entrySet();
    // RequestParams params = new RequestParams();
    // ResponseStream stream;
    // String reqMethod = getReqMethod();
    // // post请求
    // if ("post".equals(reqMethod)) {
    // for (Entry<String, String> entry : entrySet) {
    // LogUtils.e("entry.getkey" + entry.getKey() + "----->value"
    // + entry.getValue());
    // if (entry.getKey().equals("img")) {
    //
    // params.addBodyParameter("img", new File(entry.getValue()));
    //
    // } else {
    // params.addBodyParameter(entry.getKey(), entry.getValue());
    // }
    // }
    // stream = utils.sendSync(HttpMethod.POST, url, params);
    //
    // } else {
    // for (Entry<String, String> entry : entrySet) {
    // LogUtils.e("key:" + entry.getKey() + "-->value:"
    // + entry.getValue());
    // params.addQueryStringParameter(entry.getKey(), entry.getValue());
    // }
    // // get请求
    // stream = utils.sendSync(HttpMethod.GET, url, params);
    // }
    // if (stream != null) {
    // String requestUrl = stream.getRequestUrl();
    // LogUtils.e("请求的url " + requestUrl);
    // }
    //
    // int statusCode = stream.getStatusCode();
    // if (200 == statusCode) {
    // // 正确返回
    // String json = stream.readString();
    // // 写入到文件
    // FileUtils.writeFile(json.getBytes(),
    // FileUtils.getExternalStoragePath() + "logcat.txt", false);
    // LogUtils.d(json);
    // // =存储到缓存中
    // String page = map.get("page");
    // // 解析json
    // return parseJson(json);
    // }
    // return null;
    // }

    /**
     * 请求网络数据
     *
     * @param map
     * @return
     * @throws Exception
     */
    public T getDataFromNet(Map<String, String> map) throws HttpException,
            IOException {
        if (utils == null) {
            utils = new HttpUtils();
            utils.configTimeout(1000 * 10);
            utils.configRequestRetryCount(1);
            utils.configDefaultHttpCacheExpiry(0);
        }

        String url = Constants.SERVER_URL + getApiFun();

        Set<Entry<String, String>> entrySet = map.entrySet();
        RequestParams params = new RequestParams();
        ResponseStream stream;
        String reqMethod = getReqMethod();
        // post请求
        if ("post".equals(reqMethod)) {
            for (Entry<String, String> entry : entrySet) {
                if (entry.getKey().equals("img")) {
                    params.addBodyParameter("img", new File(entry.getValue()));
                } else if (entry.getKey().equals("avatarimage")) {
                    params.addBodyParameter("avatar",
                            new File(entry.getValue()));
                } else if (entry.getKey().equals("bgimage")) {
                    params.addBodyParameter("bg", new File(entry.getValue()));
                } else {
                    params.addBodyParameter(entry.getKey(), entry.getValue());
                }
            }
            stream = utils.sendSync(HttpMethod.POST, url, params);
        } else {
            for (Entry<String, String> entry : entrySet) {
                params.addQueryStringParameter(entry.getKey(), entry.getValue());
            }
            // get请求
            stream = utils.sendSync(HttpMethod.GET, url, params);
        }
        if (stream != null) {
            String requestUrl = stream.getRequestUrl();
            LogUtils.e("请求的url " + requestUrl);
        }

        int statusCode = stream.getStatusCode();
        if (200 == statusCode) {
            // 正确返回
            String json = stream.readString();
            // 写入到文件
            FileUtils.writeFile(json.getBytes(),
                    FileUtils.getExternalStoragePath() + "logcat.txt", false);
            LogUtils.d(json);
            // =存储到缓存中
            String page = map.get("page");
            String cityId = map.get("city");
            String plazaId = map.get("plazaId");
            String postId = map.get("postId");
            String userId = map.get("id");


            if (!TextUtils.isEmpty(page)) {
                // Log.e("num", ""+page);
                write2Local(page, json, cityId, plazaId, postId, userId);
            }
            // 解析json
            return parseJson(json);
        }
        return null;
    }

    /**
     * @return 获得接口函数名
     */
    public abstract String getApiFun();

    /**
     * @return 获得请求方法
     */
    public abstract String getReqMethod();

    private RequestQueue mQueue;


}
