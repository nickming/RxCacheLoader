package com.nickming.library.cache;

import com.google.gson.Gson;
import com.nickming.tcpudpdemo.rx.LogUtil;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;
import rx.Observable;


/**
 * @author nickming
 * @description
 * @date 2017/2/21
 */

public class DiskCache implements ICache {

    private static final String TAG = "DiskCache";

    private static final String NAME = ".db";

    public static long OTHER_CACHE_TIME = 10 * 60 * 1000;

    public static long WIFI_CACHE_TIME = 30 * 60 * 1000;

    private File mFileDir;

    public DiskCache() {
        mFileDir = CacheLoader.getApplicationContext().getExternalCacheDir();
    }

    @Override
    public <T> Observable<T> get(String key, Class<T> cls) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed()) {
                subscriber.onNext(null);
            }
            T t = new Gson().fromJson(getDiskData(key + NAME), cls);
            if (t != null) {
                subscriber.onNext(t);
            } else {
                subscriber.onNext(null);
            }
            subscriber.onCompleted();
        });
    }

    @Override
    public <T> void put(String key, T data) {
        Observable.create(subscriber -> {
            boolean isSuccess = isSave(key + NAME, data);
            if (!subscriber.isUnsubscribed() && isSuccess) {
                subscriber.onNext(data);
                subscriber.onCompleted();
            }
        })
                .compose(RxUtil.applySchedulers())
                .subscribe();
    }

    /**
     * 从文件中获取数据
     * @param filename
     * @return
     */
    private String getDiskData(String filename) {
        File file = new File(mFileDir, filename);
        if (!file.exists()) {
            return null;
        }
        if (isCacheDataFailure(file)) {
            LogUtil.w(TAG, "文件缓存失效");
            return null;
        }
        Source source = null;
        String result = null;
        try {
            source = Okio.source(file);
            BufferedSource buffer = Okio.buffer(source);
            result = buffer.readUtf8();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(source);
        }
        return result;
    }

    /**
     * 进行数据的存储
     * @param fileName
     * @param t
     * @param <T>
     * @return
     */
    private <T> boolean isSave(String fileName, T t) {
        File file = new File(mFileDir, fileName);
        Sink sink = null;
        boolean isSuccess = false;
        try {
            sink = Okio.sink(file);
            BufferedSink bufferedSink = Okio.buffer(sink);
            String content = new Gson().toJson(t);
            bufferedSink.writeUtf8(content);
            isSuccess = true;
        } catch (Exception e) {
            isSuccess = false;
            e.printStackTrace();
        } finally {
            closeSilently(sink);
        }
        return isSuccess;
    }

    /**
     * 关闭io的stream
     * @param closeable
     */
    private void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 检测是否缓存失效
     *
     * @param datFile
     * @return
     */
    private boolean isCacheDataFailure(File datFile) {
        if (!datFile.exists()) {
            return false;
        }
        long existTime = System.currentTimeMillis() - datFile.lastModified();
        boolean failure = false;
        if (NetWorkUtil.isWifiConnected(CacheLoader.getApplicationContext())) {
            failure = existTime > WIFI_CACHE_TIME ? true : false;
        } else {
            failure = existTime > OTHER_CACHE_TIME ? true : false;
        }
        return failure;
    }
}
