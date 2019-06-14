package com.fzu.facheck.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @date: 2018/12/25
 * @author: wyz
 * @version:
 * @description: 文件工具类
 */
public class FileUtils {

    private static final String SAVE_USER_PATH=Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)?
            Environment.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";//保存到SD卡
    private static final String SAVE_REAL_PATH = SAVE_USER_PATH+ "/onestep/";//保存的确切位置

    /**
     * 保存有文件名的文件
     * @param text
     * @param path
     * @param fileName
     * @throws IOException
     */
    public static void saveFile(String text, String path, String fileName) throws IOException {

        String subForder = SAVE_REAL_PATH + path;
        Log.d("文件存取",SAVE_REAL_PATH);
        File foder = new File(subForder);
        if (!foder.exists()) {
            Log.d("文件存在",SAVE_REAL_PATH);


            foder.mkdirs();
        }

        File myCaptureFile = new File(subForder, fileName);

        if (!myCaptureFile.exists()) {
            myCaptureFile.createNewFile();
        }

        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bos.write(text.getBytes());
        bos.flush();
        bos.close();

    }


    /**
     * 保存没有文件名的文件 文件名随机生成
     * @param text
     * @param path
     * @throws IOException
     */
    public static void saveFile(String text, String path) throws IOException {

        String subForder = SAVE_REAL_PATH + path;
        File foder = new File(subForder);
        if (!foder.exists()) {
            foder.mkdirs();
        }


        String fileName = FileUtils.createFileName(foder)+".txt";


        File myCaptureFile = new File(subForder, fileName);

        if (!myCaptureFile.exists()) {
            myCaptureFile.createNewFile();
        }

        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bos.write(text.getBytes());
        bos.flush();
        bos.close();

    }

    /**
     * 读去文件
     * @param strFilePath 文件路径
     * @return
     */
    public static String readFile(String strFilePath)  {


        String path = strFilePath;
        String result="";
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory())
        {
            Log.d("TestFile", "The File doesn't exist.");
        }
        else
        {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null)
                {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while (( line = buffreader.readLine()) != null) {
                        result+=line;
                    }
                    instream.close();
                }
            }
            catch (java.io.FileNotFoundException e)
            {
                Log.d("TestFile", "The File doesn't exist.");
            }
            catch (IOException e)
            {
                Log.d("TestFile", e.getMessage());
            }
        }
        return result;

    }


    /**
     * 获取保存的确切位置的路径
     * @return
     */
    public static String getRealPath()
    {
        return SAVE_REAL_PATH;
    }

    public static String generateSuffix() {
        // 获得当前时间
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        // 转换为字符串
        String formatDate = format.format(new Date());
        // 随机生成文件编号
        int random = new Random().nextInt(10000);
        return new StringBuffer().append(formatDate).append(
                random).toString();
    }

    public static String createFileName(File from) {
        String[] fileInfo = getFileInfo(from);
        String toPrefix = fileInfo[0] + generateSuffix();
        String toSuffix = fileInfo[1];
        return toPrefix + toSuffix;
    }

    /**
     * @param from fileInfo[0]: toPrefix;
     *             fileInfo[1]:toSuffix;
     * @return
     */
    public static String[] getFileInfo(File from) {
        String fileName = from.getName();
        int index = fileName.lastIndexOf(".");
        String toPrefix = "";
        String toSuffix = "";
        if (index == -1) {
            toPrefix = fileName;
        } else {
            toPrefix = fileName.substring(0, index);
            toSuffix = fileName.substring(index, fileName.length());
        }
        return new String[]{toPrefix, toSuffix};
    }




}
