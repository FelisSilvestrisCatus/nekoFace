package nekoFace;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/")
public class api {


    @RequestMapping(value = "/facedetection")
    public Map<String, String> index(String temppath, String uid, String type) {
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


}
