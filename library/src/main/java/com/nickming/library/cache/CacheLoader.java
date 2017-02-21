package com.nickming.library.cache;

import android.content.Context;


import rx.Observable;


/**
 * @author nickming
 * @description
 * @date 2017/2/21
 */

public class CacheLoader {

    private static final String TAG = "CacheLoader";

    private volatile static CacheLoader sInstance;

    private static Context mContext;

    private ICache mMemoryCache, mDiskCache;

    public static CacheLoader getInstance(Context context) {
        if (sInstance == null) {
            synchronized (CacheLoader.class) {
                if (sInstance == null) {
                    sInstance = new CacheLoader(context);
                }
            }
        }
        return sInstance;
    }

    private CacheLoader(Context context) {
        this.mContext = context.getApplicationContext();
        mMemoryCache = new MemoryCache();
        mDiskCache = new DiskCache();
    }

    public static Context getApplicationContext() {
        if (mContext == null)
            return null;
        return mContext;
    }

    public <T> Observable<T> asDataObservable(String key, Class<T> cls, NetworkCache<T> networkInfo) {
        return Observable.concat(getFromCache(key, cls), getFromDisk(key, cls), getFromNetWork(key, cls, networkInfo))
                .first(t -> {
                    return t != null;
                })
                .compose(RxUtil.applySchedulers());
    }

    private <T> Observable<T> getFromCache(String key, Class<T> cls) {
        return mMemoryCache.get(key, cls)
                .doOnNext(t -> {
                    LogUtil.i(TAG, "从内存获取数据");
                });
    }

    private <T> Observable<T> getFromDisk(String key, Class<T> cls) {
        return mDiskCache.get(key, cls)
                .doOnNext(t -> {
                    LogUtil.i(TAG, "从硬盘获取数据");
                    mMemoryCache.put(key, t);
                });
    }

    private <T> Observable<T> getFromNetWork(String key, Class<T> cls, NetworkCache<T> networkCache) {
        return networkCache.get(key, cls)
                .doOnNext(t -> {
                    LogUtil.i(TAG, "从硬盘获取数据");
                    mDiskCache.put(key, t);
                    mMemoryCache.put(key, t);
                });
    }
}
