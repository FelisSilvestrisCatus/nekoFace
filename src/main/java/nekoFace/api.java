package nekoFace;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class api {

    @RequestMapping(value = "/")
    public String index(String temppath, String uid, String type) {
        return "helloworld";
    }


    @RequestMapping(value = "/facedetection")
    public Map<String, String> facedetection(String temppath, String uid, String type) {
        System.out.println("temppath = " + temppath);
        System.out.println("uid = " + uid);
        System.out.println("type = " + type);
        return Face.facedetection(temppath, uid, type);
    }

    //temppath 是用户base64转换后的地址
    @RequestMapping(value = "/faceRecognition")
    public Map<String, String> faceRecognition(String temppath, String uid) {
        System.out.println("temppath = " + temppath);
        System.out.println("uid = " + uid);

        return FaceTrainAndValidate.validate(uid, temppath);
    }

    //接受指令开始 训练
    @RequestMapping(value = "/startTrain")
    public Map<String, String> startTrain() throws IOException {
        Map<String, String> map = new HashMap<>();
        FaceTrainAndValidate.train();
        map.put("train","yes");
        return map;
    }
}
