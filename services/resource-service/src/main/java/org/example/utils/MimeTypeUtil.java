package org.example.utils;

public class MimeTypeUtil {

    private static final String IMAGE_PREFIX = "image/";
    private static final String VIDEO_PREFIX = "video/";

    public static final String IMAGE = "images";
    public static final String VIDEO = "videos";

    /**
     * 根据MIME类型获取对应的目录名称
     * @param mimeType 文件的MIME类型，例如"image/jpeg"、"video/mp4"等
     * @return 对应的目录名称，如果是图片类型则返回IMAGE，如果是视频类型则返回VIDEO
     * @throws IllegalArgumentException 当MIME类型为空或不支持时抛出此异常
     */
    public static String getDirectoryByMimeType(String mimeType) {
        // 检查MIME类型是否为空
        if (mimeType == null) {
            throw new IllegalArgumentException("文件类型不能为空");
        }
        // 判断是否为图片类型
        if (mimeType.startsWith(IMAGE_PREFIX)) {
            return IMAGE;
        } else if (mimeType.startsWith(VIDEO_PREFIX)) {
            return VIDEO;
        } else {
            throw new IllegalArgumentException("不支持的文件类型: " + mimeType);
        }
    }
}
