package com.fzu.facheck.utils;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.fzu.facheck.FacheckAPP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PhotoUtil {
    public static void takePicture(Activity activity,Uri imageUri, int requestCode) {
        //调用系统相机
        Intent intentCamera = new Intent();
        intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        activity.startActivityForResult(intentCamera, requestCode);
    }

    public static void openPic(Activity activity, int requestCode) {
        //打开系统相册
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        activity.startActivityForResult(photoPickerIntent, requestCode);
    }
    //g根据相册放回结果获取path
    public static String handleImageOnKitKat(final Context context,Intent data){
        String imagepath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(context,uri)){
            String docid=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docid.split(":")[1];
                String selection=MediaStore.Images.Media._ID+"="+id;
                imagepath=getImagePath(context,MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri=ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docid));
                imagepath=getImagePath(context,contentUri,null);}
            }else if("content".equalsIgnoreCase(uri.getScheme())){
                imagepath=getImagePath(context,uri,null);
            }else if("file".equalsIgnoreCase(uri.getScheme())){
                imagepath=uri.getPath();
            }
        return imagepath;
    }
    public static String getImagePath(final Context context,Uri uri,String selection){
        String path=null;
        Cursor cursor=context.getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    public static String getPath(){
        String path= FacheckAPP.getInstance().getFilesDir().getAbsolutePath()+"/photo.jpg";
        return path;
    }
    //压缩并将图片矫正
    public static Bitmap amendRotatePhoto(String originpath){
        int angel=readPictureDegree(originpath);
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inSampleSize=8;
        Bitmap bitmap=BitmapFactory.decodeFile(originpath,options);
        options=null;
        return bitmap;
    }
    //获取图片被旋转的角度
    private static int readPictureDegree(String path){
        int degree=0;
        try {
            ExifInterface exifInterface=new ExifInterface(path);
            int orientation=exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION
            ,ExifInterface.ORIENTATION_NORMAL);
            switch(orientation){
                case ExifInterface.ORIENTATION_ROTATE_90:
                {
                    degree=90;
                    break;
                }
                case ExifInterface.ORIENTATION_ROTATE_180:
                {
                    degree=180;
                    break;
                }
                case ExifInterface.ORIENTATION_ROTATE_270:
                {
                    degree=270;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
    //旋转图片
    private static Bitmap roatingImageView(int angel,Bitmap bitmap){
        Bitmap returnBm=null;
        Matrix matrix=new Matrix();
        matrix.postRotate(angel);
        try{
            returnBm=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth()
                    ,bitmap.getHeight(),matrix,true);
        }catch (OutOfMemoryError e){
            e.printStackTrace();
        }
        if(returnBm==null)
            returnBm=bitmap;
        if(bitmap!=returnBm)
            bitmap.recycle();
        return returnBm;
    }

    public static String base64(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}
