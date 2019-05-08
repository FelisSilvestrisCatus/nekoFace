package com.example.demo;


import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


//有关人脸识别的所有东西都在这
public class Face {
    //前端数据转换为图片
    static {
        System.load("C:\\vfiles\\opencv\\opencv_java401.dll");
    }

//    public Face() {
//        System.load("C:\\vfiles\\opencv\\opencv_java401.dll");
//    }

    public static int getPhotoNum(String uid) {
        File file = new File("C:\\vfiles\\photo\\" + uid);
        String files[];
        files = file.list();
        int num = files.length;

        return num;
    }

    /**
     * @param img      ：前端request 获得的图像数据
     * @param uid：用户id
     * @return
     */
    public static String base64StrToImage(String img, String uid) {
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
        String dirpath = "C:\\vfiles\\photo_login\\" + uid;
        File dirFile = new File(dirpath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        String imgStr = img.substring(22, img.length());

        if (imgStr == null)
            return "";
        BASE64Decoder decoder = new BASE64Decoder();
        String filename = dirpath + "\\" + f.format(new Date()) + ".png";
        try {
            // 解密
            byte[] b = decoder.decodeBuffer(imgStr);
            // 处理数据
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            // 文件夹不存在则自动创建

            File tempFile = new File(filename);
            if (!tempFile.getParentFile().exists()) {
                tempFile.getParentFile().mkdirs();
            }
            OutputStream out = new FileOutputStream(tempFile);
            out.write(b);
            out.flush();
            out.close();
            return filename;
        } catch (Exception e) {
            return filename;
        }
    }

    //检测人脸  //若检测到 进行裁剪 并返回值

    /**
     * @param path:base64 转换后的图片的地址
     *                    type:  0  默认保存到ui下的文件夹（用来上传照片时）
     *                    1  默认保存在临时文件夹中（用来登录时 所需）
     * @return 使用map   path 返回保存的地址  flag 返回检测到的人脸
     */

    public static Map<String, String> facedetection(String path, String uid, String type) {
        Map<String, String> map = new HashMap<>();
        int flag = 0;
        Rect rect_cut = new Rect();// 裁剪后的
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
        Size dsize = new Size(92, 112);
        CascadeClassifier faceDetector = new CascadeClassifier("C:\\vfiles\\opencv\\lbpcascade_frontalface.xml");
        File file = new File(path);
        System.out.println("file = " + file.exists());
        Mat image = Imgcodecs.imread(path);
        System.out.println("图片" + image.size());
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);
        flag = faceDetections.toArray().length;
        System.out.println("人脸有几个" + flag
        );
        if (flag != 1) {
            map.put("flag", "" + flag); //没检测到人脸（或者检测到多张人脸） 直接返回
            map.put("path", "");
            return map;
        }
        org.opencv.core.Rect rect = faceDetections.toArray()[0];
//            // 用绿色框匡助

        Imgproc.rectangle(image, new Point(rect.x, rect.y),
                new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
        rect_cut.x = rect.x;
        rect_cut.y = rect.y;
        rect_cut.width = rect.width;
        rect_cut.height = rect.height;
        String dirpath = null;
        if (type.equalsIgnoreCase("0")) {

            dirpath = "C:\\vfiles\\photo\\" + uid;
        } else {
            dirpath = "C:\\vfiles\\photo_login\\" + uid;
        }


        File dirFile = new File(dirpath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        String filename = dirpath + "\\" + f.format(new Date()) + ".png";
        System.out.println(filename);
        rect_cut = new Rect(rect_cut.x, rect_cut.y, rect_cut.width, rect_cut.height);
        //dst裁剪后的
        Mat dst = new Mat(image, rect_cut);
        Mat afterreasize = new Mat();
        Imgproc.resize(dst, afterreasize, new Size(92, 112), 0, 0, Imgproc.INTER_LINEAR);
        System.out.println("像素" + dst.height() + "dd" + dst.width());
        Imgcodecs.imwrite(filename, afterreasize);
        map.put("flag", "" + flag);
        map.put("path", filename);
        return map;
    }

    //获取训练图片的csv文件
    public static boolean getCsv() {
        boolean flag = false;
        //获取文件夹下的文件列表
        String basefile = "C:\\vfiles\\photo";
        File file = new File(basefile);
        // 获得该文件夹内的所有文件
        File[] array = file.listFiles();
        System.out.println("获取指定文件下文件夹的个数" + array.length);
        List<String> list = new ArrayList<>();
        // 循环遍历
        for (int i = 0; i < array.length; i++) {
            //此时能够获取已上传图片的学生相册
            File file_ = new File(array[i].getPath());
            File[] array_ = file_.listFiles();
            //获取相册文件夹名字 即学生学号
            String uname = array[i].getName();
            for (int a = 0; a < array_.length; a++) {
                File img_file = array_[a];
                String path_img = img_file.getPath();
                String imgname = img_file.getName();
                String listitem = path_img + imgname + ";" + uname;
                System.out.println("要写入的数据" + listitem);
                list.add(listitem);


            }


        }
        //sh创建文件 且将集合写入
        File filename = new File("C:\\vfiles\\a.text");

        if (!filename.exists()) {
            try {
                filename.createNewFile();
                System.out.println("文件一开始存在但被我删除" + "成功！");
                BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\vfiles\\a.text"));
                int count = 1;
                for (String l : list) {
                    if (count != list.size()) {
                        writer.write(l + "\r\n");
                    } else {
                        writer.write(l);
                    }

                    count++;
                }
                writer.close();
                flag = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {

            //删除该文件
            Boolean succeedDelete = filename.delete();
            if (succeedDelete) {
                System.out.println("删除单个文件" + filename.getName() + "成功！");

                try {
                    filename.createNewFile();
                    System.out.println("文件一开始存在但被我删除" + "成功！");
                    BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\vfiles\\a.text"));
                    int count = 1;
                    for (String l : list) {
                        if (count != list.size()) {
                            writer.write(l + "\r\n");
                        } else {
                            writer.write(l);
                        }

                        count++;
                    }
                    writer.close();
                    flag = true;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("删除单个文件" + filename.getName() + "失败！");

            }
        }

        return flag;
    }


}



