package com.hanami.net.download;

import java.io.File;

/**
 * @author lidaisheng
 * @date 2019/3/20
 */
public interface DownloadListener {

    void onUpdate(long count, long current);

    void onSuccess(File file);

    void onFailure(Throwable t, int errorNo, String strMsg);

}
