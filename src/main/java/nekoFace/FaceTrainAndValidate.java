package nekoFace;//package neko.utils.face;


import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_face.FisherFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

public class FaceTrainAndValidate {

    //模型训练 用户上传完照片后自动调用模型训练  将生成后的模型保存 后期直接使用
    public static void train() throws IOException {
        //模型训练之前 生成csv文件
        Face.getCsv();
        String trainingDir = "C:\\vfiles\\a.text";


        // 封装数据源
        BufferedReader br = new BufferedReader(new FileReader(trainingDir));
        // 封装目的地(创建集合对象)
        ArrayList<String> array = new ArrayList<String>();

        // 读取数据存储到集合中
        String line = null;
        while ((line = br.readLine()) != null) {
            array.add(line);
        }

        // 释放资源
        br.close();
        MatVector images = new MatVector(array.size());

        Mat labels = new Mat(array.size(), 1, CV_32SC1);
        IntBuffer labelsBuf = labels.createBuffer();
        int counter = 0;
        System.out.println(array.size());
        for (String s : array) {


            Mat img = imread(s.split(";")[0], Imgcodecs.IMREAD_GRAYSCALE);

            System.out.println("加载" + counter + "张" + "像素" + img.arrayHeight() * img.arrayWidth());
            System.out.println(counter + "张" + "通道数目" + img.channels());
            int label = Integer.parseInt(s.split(";")[1]);
            System.out.println(label + "label");
            images.put(counter, img);

            labelsBuf.put(counter, label);

            counter++;
        }

        FaceRecognizer faceRecognizer = FisherFaceRecognizer.create();
        // FaceRecognizer faceRecognizer = EigenFaceRecognizer.create();
        // FaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
        System.out.println("模型标签的数目" + labels.size());
        System.out.println("模型开始训练 ");
        faceRecognizer.train(images, labels);

        //   IntPointer label = new IntPointer(1);
        //  DoublePointer confidence = new DoublePointer(1);
        //faceRecognizer.predict(testImage, label, confidence);
        //   int predictedLabel = label.get(0);
//
        System.out.println("模型训练完毕 ");
        faceRecognizer.save("C:\\vfiles\\Model.xml");
        System.out.println("模型保存完毕 ");

    }

    //人脸识别
    //学生用来登录或者点名时所需（）

    /**
     * @param uid
     * @param loginimgpath base64 转换后的地址
     * @return
     */
    public static Map<String, String> validate(String uid, String loginimgpath) {


        try {
            boolean flag = false;
            //识别人脸并裁剪
            Map<String, String> map = new HashMap<>();
            map = Face.facedetection(loginimgpath, uid, "1");// 1 表示该该方法用来登录存放临时的图片
            if (!(map.get("flag").equalsIgnoreCase("1"))) {
                map.put("isThisGuy", "no");//也同时拒绝他的登录  或者这个哔是冒名顶替的
                return map;// 不具备人脸识别的条件
            }
            //开始人脸识别
            //加载模型
            FaceRecognizer faceRecognizer = FisherFaceRecognizer.create();

            IntPointer label = new IntPointer(1);
            DoublePointer confidence = new DoublePointer(1);
            faceRecognizer.setThreshold(10.00);//设置阈值
            faceRecognizer.read("C:\\vfiles\\Model.xml");
            //图像灰度化
            //先去噪声 再灰度化
             Mat Image = imread(map.get("path"), Imgcodecs.IMREAD_GRAYSCALE);
            faceRecognizer.predict(Image, label, confidence);
            int predictedLabel = label.get(0);
            if (predictedLabel == Integer.valueOf(uid)) {
                map.put("isThisGuy", "yes");
                return map;
            }
            map.put("isThisGuy", "no");
            return map;
        } catch (Exception e) {
            Map<String, String> map = new HashMap<>();
            map.put("isThisGuy", "no");
            return map;
        }
    }
}
