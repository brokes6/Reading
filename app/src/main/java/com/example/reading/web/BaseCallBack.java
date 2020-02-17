package com.example.reading.web;

import com.google.gson.internal.$Gson$Types;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import lombok.Getter;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

@Getter
public abstract class BaseCallBack<T> {
    private Type mType;

    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    public BaseCallBack() {
        mType=getSuperclassTypeParameter(getClass());
    }

    protected abstract void OnRequestBefore(Request request);

    protected abstract void onFailure(Call call);

    protected abstract void onSuccess(Call call, Response response, T t);

    protected abstract void onResponse(Response response);

    protected abstract void onEror(Call call, int statusCode);

    protected abstract void inProgress(int progress, long total , int id);
}
