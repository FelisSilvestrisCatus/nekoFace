package nekoFace;


import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
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

//        Imgproc.rectangle(image, new Point(rect.x, rect.y),
//                new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
        rect_cut.x = rect.x;
        rect_cut.y = rect.y;
        rect_cut.width = rect.width;
        rect_cut.height = rect.height;
        String dirpath = null;
        if (type.equalsIgnoreCase("0")) {

            dirpath = "C:\\vfiles\\photo\\" + uid;
        } else {
            dirpath = "C:\\vfiles\\photo_temp\\" + uid;
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
                String listitem = path_img  + ";" + uname;
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
//    //模型训练 用户上传完照片后自动调用模型训练  将生成后的模型保存 后期直接使用
//    public static void train() throws IOException {
//        //模型训练之前 生成csv文件
//        Face.getCsv();
//        String trainingDir = "C:\\vfiles\\a.text";
//
//
//        // 封装数据源
//        BufferedReader br = new BufferedReader(new FileReader(trainingDir));
//        // 封装目的地(创建集合对象)
//        ArrayList<String> array = new ArrayList<String>();
//
//        // 读取数据存储到集合中
//        String line = null;
//        while ((line = br.readLine()) != null) {
//            array.add(line);
//        }
//
//        // 释放资源
//        br.close();
//        MatVector images = new MatVector(array.size());
//
//        Mat labels = new Mat(array.size(), 1, CV_32SC1);
//        IntBuffer labelsBuf = labels.createBuffer();
//        int counter = 0;
//        System.out.println(array.size());
//        for (String s : array) {
//
//
//            Mat img = imread(s.split(";")[0], Imgcodecs.IMREAD_GRAYSCALE);
//
//            System.out.println("加载" + counter + "张" + "像素" + img.arrayHeight() * img.arrayWidth());
//            System.out.println(counter + "张" + "通道数目" + img.channels());
//            int label = Integer.parseInt(s.split(";")[1]);
//            System.out.println(label + "label");
//            images.put(counter, img);
//
//            labelsBuf.put(counter, label);
//
//            counter++;
//        }
//
//        FaceRecognizer faceRecognizer = FisherFaceRecognizer.create();
//        // FaceRecognizer faceRecognizer = EigenFaceRecognizer.create();
//        // FaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
//        System.out.println("模型标签的数目" + labels.size());
//        System.out.println("模型开始训练 ");
//        faceRecognizer.train(images, labels);
//
//        //   IntPointer label = new IntPointer(1);
//        //  DoublePointer confidence = new DoublePointer(1);
//        //faceRecognizer.predict(testImage, label, confidence);
//        //   int predictedLabel = label.get(0);
////
//        System.out.println("模型训练完毕 ");
//        faceRecognizer.save("C:\\vfiles\\Model.xml");
//        System.out.println("模型保存完毕 ");
//
//    }

//    //人脸识别
//    //学生用来登录或者点名时所需（）
//    public static Map<String, String> validate(String uid, String img) {
//
//        boolean flag = false;
//        //将照片字节码转为指定格式的照片  若通过返回值
//        String loginimgpath = Face.base64StrToImage(img, uid);
//        //识别人脸并裁剪
//        Map<String, String> map = new HashMap<>();
//        map = Face.facedetection(loginimgpath, uid, "1");// 1 表示该该方法用来登录存放临时的图片
//        if (!(map.get("flag").equalsIgnoreCase("1"))) {
//            map.put("isThisGuy", "no");//也同时拒绝他的登录  或者这个哔是冒名顶替的
//            return map;// 不具备人脸识别的条件
//        }
//        //开始人脸识别
//        //加载模型
//        FaceRecognizer faceRecognizer = FisherFaceRecognizer.create();
//
//        IntPointer label = new IntPointer(1);
//        DoublePointer confidence = new DoublePointer(1);
//        faceRecognizer.setThreshold(94.22);//设置阈值
//        faceRecognizer.read("C:\\vfiles\\Model.xml");
//        //图像灰度化
//        Mat Image = imread(map.get("path"), Imgcodecs.IMREAD_GRAYSCALE);
//        faceRecognizer.predict(Image, label, confidence);
//        int predictedLabel = label.get(0);
//        if (predictedLabel == Integer.valueOf(uid)) {
//            map.put("isThisGuy", "yes"); //是他就是他
//            return map;
//        }
//        map.put("isThisGuy", "no");//也同时拒绝他的登录  或者这个哔是冒名顶替的
//        return map;
//    }

}



