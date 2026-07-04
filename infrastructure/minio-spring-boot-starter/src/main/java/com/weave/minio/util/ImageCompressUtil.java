package com.weave.minio.util;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片压缩工具类
 * <p>
 * 基于 Thumbnailator，支持按比例缩放、按尺寸缩放、质量压缩。
 * 使用示例：
 * <pre>
 *     byte[] compressed = ImageCompressUtil.compressByScale(file, 0.5f, 0.8f);
 *     byte[] compressed = ImageCompressUtil.compressBySize(file, 800, 600, 0.8f);
 * </pre>
 */
@Slf4j
public class ImageCompressUtil {

    /**
     * 按比例缩放并压缩图片
     *
     * @param file        原始文件
     * @param scale       缩放比例（0 < scale <= 1，如 0.5 表示缩小到 50%）
     * @param quality     输出质量（0 < quality <= 1，如 0.8 表示 80% 质量）
     * @return 压缩后的字节数组
     */
    public static byte[] compressByScale(MultipartFile file, float scale, float quality) {
        try (InputStream in = file.getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Thumbnails.of(in)
                    .scale(scale)
                    .outputQuality(quality)
                    .toOutputStream(out);

            log.debug("图片按比例压缩: scale={}, quality={}, 原始大小={}KB, 压缩后={}KB",
                    scale, quality, file.getSize() / 1024, out.size() / 1024);
            return out.toByteArray();
        } catch (IOException e) {
            log.error("图片压缩失败: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("图片压缩失败：" + e.getMessage(), e);
        }
    }

    /**
     * 按尺寸缩放并压缩图片（保持宽高比，不超过指定尺寸）
     *
     * @param file        原始文件
     * @param width       最大宽度（px）
     * @param height      最大高度（px）
     * @param quality     输出质量（0 < quality <= 1）
     * @return 压缩后的字节数组
     */
    public static byte[] compressBySize(MultipartFile file, int width, int height, float quality) {
        try (InputStream in = file.getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Thumbnails.of(in)
                    .size(width, height)
                    .outputQuality(quality)
                    .toOutputStream(out);

            log.debug("图片按尺寸压缩: width={}, height={}, quality={}, 原始大小={}KB, 压缩后={}KB",
                    width, height, quality, file.getSize() / 1024, out.size() / 1024);
            return out.toByteArray();
        } catch (IOException e) {
            log.error("图片压缩失败: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("图片压缩失败：" + e.getMessage(), e);
        }
    }

    /**
     * 仅质量压缩（不改变尺寸）
     *
     * @param file    原始文件
     * @param quality 输出质量（0 < quality <= 1）
     * @return 压缩后的字节数组
     */
    public static byte[] compressByQuality(MultipartFile file, float quality) {
        return compressByScale(file, 1.0f, quality);
    }

    /**
     * 按比例缩放并压缩图片（InputStream 版本）
     *
     * @param inputStream 输入流
     * @param scale       缩放比例
     * @param quality     输出质量
     * @return 压缩后的字节数组
     */
    public static byte[] compressByScale(InputStream inputStream, float scale, float quality) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Thumbnails.of(inputStream)
                    .scale(scale)
                    .outputQuality(quality)
                    .toOutputStream(out);

            return out.toByteArray();
        } catch (IOException e) {
            log.error("图片压缩失败", e);
            throw new RuntimeException("图片压缩失败：" + e.getMessage(), e);
        }
    }

    /**
     * 压缩并返回 ByteArrayInputStream，便于直接上传到 MinIO
     *
     * @param file    原始文件
     * @param width   最大宽度
     * @param height  最大高度
     * @param quality 输出质量
     * @return ByteArrayInputStream
     */
    public static ByteArrayInputStream compressToStream(MultipartFile file, int width, int height, float quality) {
        byte[] bytes = compressBySize(file, width, height, quality);
        return new ByteArrayInputStream(bytes);
    }
}
