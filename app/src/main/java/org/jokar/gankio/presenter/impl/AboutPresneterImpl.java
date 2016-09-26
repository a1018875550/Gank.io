package org.jokar.gankio.presenter.impl;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.Preference;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.jokar.gankio.presenter.event.AboutPresenter;

import java.io.File;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by JokAr on 2016/9/26.
 */

public class AboutPresneterImpl implements AboutPresenter {


    @Override
    public void showCache(Context context, File file, Preference tv_cache) {

        Observable.just(file)
                .subscribeOn(Schedulers.computation())
                .unsubscribeOn(Schedulers.computation())
                .map(file1 -> {return calculateSize(file);})
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(size -> {
                    String sizeText = android.text.format.Formatter.formatFileSize(context, size);
                    tv_cache.setSummary("当前缓存" + sizeText);
                });
    }

    @Override
    public void clearCache(Activity activity, File file, Preference tv_cache) {
        new AlertDialog.Builder(activity)
                .setTitle("提示")
                .setMessage("是否清空图片缓存?")
                .setNegativeButton("取消", null)
                .setPositiveButton("确认", (dialog, which) -> {
                    //清除图片缓存
                    clearImageCache(file, activity, tv_cache);
                }).show();
    }
    /**
     * 清除图片缓存
     *
     * @param file
     * @param activity
     * @param tv_cache
     */
    private void clearImageCache(File file, final Activity activity, final Preference tv_cache) {

        Observable.just(file)
                .doOnNext(new Action1<File>() {
                    @Override
                    public void call(File file) {
                        Glide.get(activity).clearDiskCache();
                    }
                })
                .map(file1 -> {
                    return calculateSize(file1);
                })
                .subscribeOn(Schedulers.computation())
                .unsubscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(size -> {
                    String sizeText = android.text.format.Formatter.formatFileSize(activity, size);
                    tv_cache.setSummary("当前缓存" + sizeText);
                });
    }
    /**
     * 算出当前目录下所有文件的大小
     *
     * @param dir
     * @return
     */
    private long calculateSize(File dir) {
        if (dir == null) return 0;
        if (!dir.isDirectory()) return dir.length();
        long result = 0;
        File[] children = dir.listFiles();
        if (children != null)
            for (File child : children)
                result += calculateSize(child);
        return result;
    }
}
