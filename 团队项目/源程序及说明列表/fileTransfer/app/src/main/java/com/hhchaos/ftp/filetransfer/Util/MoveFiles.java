package com.hhchaos.ftp.filetransfer.Util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

/**
 * Created by Mr.Wang on 2016/5/24 0024.
 */
public class MoveFiles {
    /**
     * 移动文件
     *
     * @param srcFileName 源文件完整路径
     * @param destDirName 目的目录完整路径
     * @return 文件移动成功返回true，否则返回false
     */
    public boolean moveFiles(String srcFileName, String destDirName) {

        File srcFile = new File(srcFileName);
        if (!srcFile.exists() || !srcFile.isFile()) {
            return false;
        }

        File destDir = new File(destDirName);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return srcFile.renameTo(new File(destDirName + File.separator + srcFile.getName()));
//        File from = new File(Environment.getExternalStorage().getAbsolutePath()+"/kaic1/imagem.jpg");
//        File to = new File(Environment.getExternalStorage().getAbsolutePath()+"/kaic2/imagem.jpg");
//        from.renameTo(to);
    }

    /**
     * 移动目录
     *
     * @param srcDirName  源目录完整路径
     * @param destDirName 目的目录完整路径
     * @return 目录移动成功返回true，否则返回false
     */
    public boolean moveDirctory(String srcDirName, String destDirName) {
        File srcDir = new File(srcDirName);
        if (!srcDir.exists() || !srcDir.isDirectory()) {
            return false;
        }
        File destDir = new File(destDirName);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        /**
         * 如果是文件则移动，否则递归移动文件夹。删除最终的空源文件夹
         * 注意移动文件夹时保持文件夹的树状结构
         */
        File[] sourceFiles = srcDir.listFiles();
        for (File sourceFile : sourceFiles) {
            if (sourceFile.isFile())
                moveFiles(sourceFile.getAbsolutePath(), destDir.getAbsolutePath());
            else if (sourceFile.isDirectory())
                moveDirctory(sourceFile.getAbsolutePath(), destDir.getAbsolutePath() + File.separator + sourceFile.getName());
        }
        return srcDir.delete();
    }

    private void moveFile2(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputPath + inputFile).delete();


        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

}