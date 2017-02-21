package com.nickming.library.cache;

import android.text.TextUtils;
import android.util.LruCache;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

import rx.Observable;


/**
 * @author nickming
 * @description
 * @date 2017/2/21
 */

public class MemoryCache implements ICache {

    private LruCache<String, String> mCache;

    public MemoryCache() {
        final int maxMemory = (int) Runtime.getRuntime().maxMemory();
        final int cacheSize = maxMemory / 8;
        mCache = new LruCache<String, String>(cacheSize) {
            @Override
            protected int sizeOf(String key, String value) {
                try {
                    return value.getBytes("UTF-8").length;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return value.getBytes().length;
                }
            }
        };
    }

    @Override
    public <T> Observable<T> get(String key, Class<T> cls) {
        return Observable.create(subscriber -> {
            String result = mCache.get(key);
            if (subscriber.isUnsubscribed()) {
                return;
            }
            if (TextUtils.isEmpty(result)) {
                subscriber.onNext(null);
            } else {
                T t = new Gson().fromJson(result, cls);
                subscriber.onNext(t);
            }
            subscriber.onCompleted();
        });
    }

    @Override
    public <T> void put(String key, T data) {
        if (data != null) {
            String content = new Gson().toJson(data);
            mCache.put(key, content);
        }
    }


}
