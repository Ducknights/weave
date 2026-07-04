package com.weave.minio.util;

public class MimeTypeUtil {

    private static final String IMAGE_PREFIX = "image/";
    private static final String VIDEO_PREFIX = "video/";

    /**
     * 判断 MIME 类型是否为图片
     * @param mimeType 文件的 MIME 类型，例如"image/jpeg"
     * @return true 表示是图片类型
     */
    public static boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith(IMAGE_PREFIX);
    }

    /**
     * 判断 MIME 类型是否为视频
     * @param mimeType 文件的 MIME 类型，例如"video/mp4"
     * @return true 表示是视频类型
     */
    public static boolean isVideo(String mimeType) {
        return mimeType != null && mimeType.startsWith(VIDEO_PREFIX);
    }
}
