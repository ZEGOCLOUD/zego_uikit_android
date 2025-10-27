package com.zegocloud.uikit.utils;

import android.net.Uri;
import android.webkit.MimeTypeMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {

    public static List<String> findFilesWithPrefix(String directoryPath, String prefix) {
        File directory = new File(directoryPath);
        List<String> fileList = new ArrayList<>();

        // 检查目录是否存在
        if (directory.exists() && directory.isDirectory()) {
            // 遍历目录下的所有文件和子目录
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    // 检查是否为文件且文件名以指定前缀开头
                    if (file.isFile() && file.getName().startsWith(prefix)) {
                        fileList.add(file.getAbsolutePath());
                    }
                    //                    // 如果是目录，递归查找
                    else if (file.isDirectory()) {
                        List<String> subFiles = findFilesWithPrefix(file.getAbsolutePath(), prefix);
                        if (!subFiles.isEmpty()) {
                            fileList.addAll(subFiles);
                        }
                    }
                }
            }
        }

        // 将列表转换为数组并返回
        return fileList;
    }

    public static void deleteFileOrDirectory(File fileOrDirectory) {
        // 检查是否是一个文件
        if (fileOrDirectory.isFile()) {
            // 如果是文件，直接删除
            boolean success = fileOrDirectory.delete();
            if (!success) {
                // 日志记录或异常处理：文件删除失败
                System.out.println("Failed to delete file: " + fileOrDirectory.getAbsolutePath());
            }
        } else if (fileOrDirectory.isDirectory()) {
            // 如果是目录，递归删除目录中的所有文件和子目录
            File[] files = fileOrDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteFileOrDirectory(file); // 递归调用
                }
            }
            // 删除目录本身
            boolean success = fileOrDirectory.delete();
            if (!success) {
                // 日志记录或异常处理：目录删除失败
                System.out.println("Failed to delete directory: " + fileOrDirectory.getAbsolutePath());
            }
        } else {
            // 日志记录或异常处理：给定的路径不存在
            System.out.println("The file or directory does not exist: " + fileOrDirectory.getAbsolutePath());
        }
    }

    // 使用 ContentResolver 获取文件的 MIME 类型
    public static String getMimeType(File file) {
        Uri fileUri = Uri.fromFile(file);
        String extension = MimeTypeMap.getFileExtensionFromUrl(fileUri.toString());
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
    }

    public static List<String> findDirsWithPrefix(String directoryPath, String prefix) {
        File directory = new File(directoryPath);
        List<File> dirList = new ArrayList<>();

        // 检查目录是否存在
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory() && file.getName().startsWith(prefix)) {
                        dirList.add(file);
                    }
                }
            }
        }

        // 按最后修改时间排序（从新到旧）
        Collections.sort(dirList, (f1, f2) -> {
            try {
                return Files.getLastModifiedTime(f2.toPath()).compareTo(Files.getLastModifiedTime(f1.toPath()));
            } catch (IOException e) {
                return 0;
            }
        });

        return dirList.stream().map(File::getAbsolutePath).collect(Collectors.toList());
    }
}
