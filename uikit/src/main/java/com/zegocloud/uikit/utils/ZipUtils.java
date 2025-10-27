package com.zegocloud.uikit.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import timber.log.Timber;

public class ZipUtils {

    public static void zipFiles(List<String> filePaths, String outputFilePath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputFilePath))) {
            for (String filePath : filePaths) {
                File file = new File(filePath);
                if (file.exists()) {
                    addFileToZip(zos, file, "");
                }
            }
        }
    }

    private static void addFileToZip(ZipOutputStream zos, File file, String baseDir) throws IOException {
        if (file.isDirectory()) {
            String dirPath = file.getPath().length() <= baseDir.length() ? baseDir : baseDir + file.getName() + "/";
            ZipEntry zipEntry = new ZipEntry(dirPath);
            zos.putNextEntry(zipEntry);
            zos.closeEntry();

            File[] childFiles = file.listFiles();
            if (childFiles != null) {
                for (File childFile : childFiles) {
                    addFileToZip(zos, childFile, dirPath);
                }
            }
        } else {
            ZipEntry zipEntry = new ZipEntry(baseDir + file.getName());
            zos.putNextEntry(zipEntry);
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zos.write(bytes, 0, length);
                }
            }
            zos.closeEntry();
        }
    }

    public static void createZipFile(List<String> filePaths, String outputZipFilePath) {
        long startTime = System.currentTimeMillis();
        Timber.d("Starting createZipFile with: files = %s, zipFile = %s", filePaths, outputZipFilePath);

        try {
            zipFiles(filePaths, outputZipFilePath);
            Timber.d("createZipFile completed in %.2f s", (System.currentTimeMillis() - startTime)/1000f);
        } catch (IOException e) {
            e.printStackTrace();
            Timber.e("Error occurred while creating zip file.");
        }
    }
}
