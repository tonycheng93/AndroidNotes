package com.sky.androidnotes.fresco;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by tonycheng on 2017/10/9.
 */

public class FileUtils {

    private static final HashMap<String, String> FILE_TYPES = new HashMap<>();

    static {
        FILE_TYPES.put("474946", "gif");
    }

    private FileUtils() {
        //utility class,no instance
    }

    /**
     * get cache from disk cache
     *
     * @param url target image url
     * @return cache file
     */
    public static File getFileFromDiskCache(@NonNull String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(
                Uri.parse(url)).build();
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequest);
        final BinaryResource binaryResource = ImagePipelineFactory.getInstance().getMainDiskStorageCache().getResource(cacheKey);
        return ((FileBinaryResource) binaryResource).getFile();
    }

    public static String getFileRealType(@NonNull String filePath) {
        String fileType = "";
        if (!TextUtils.isEmpty(filePath)) {
            fileType = FILE_TYPES.get(getFileHeader(filePath));
        }
        return fileType;
    }

    private static String getFileHeader(@NonNull String filePath) {
        FileInputStream is = null;
        String mime = "";
        try {
            is = new FileInputStream(filePath);
            byte[] b = new byte[3];
            is.read(b, 0, b.length);
            mime = bytesToHexString(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return mime;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }
}