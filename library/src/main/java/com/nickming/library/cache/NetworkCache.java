package com.nickming.library.cache;

import rx.Observable;

/**
 * @author nickming
 * @description
 * @date 2017/2/21
 */

public abstract class NetworkCache<T> {
    /**
     * 需要实现这个方法来获取网络数据
     * @param key
     * @param cls
     * @return
     */
    public abstract Observable<T> get(String key,final Class<T> cls);
}
