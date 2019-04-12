package com.hanami.net.http;

import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Request;

/**
 * @author lidaisheng
 * @date 2019/4/7
 */
public abstract class ResultCallback<T> {

    Type mType;

    public ResultCallback() {
        mType = getSuperclassTypeParameter(getClass());
    }

    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    /**
     * 在请求之前的方法，一般用于加载框展示
     *
     * @param request
     */
    public void onBefore(Request request) {
    }

    /**
     * 在请求之后的方法，一般用于加载框隐藏
     */
    public void onAfter() {
    }

    /**
     * 请求失败的时候
     *
     * @param request
     * @param e
     */
    public abstract void onError(Request request, Exception e);

    /**
     * @param response
     */
    public abstract void onResponse(T response);
}
