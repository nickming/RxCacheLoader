package com.nickming.library.cache;


import rx.Observable;

/**
 * @author nickming
 * @description
 * @date 2017/2/21
 */

public interface ICache {

    <T> Observable<T> get(String key, Class<T> cls);

    <T> void put(String key, T data);
}
