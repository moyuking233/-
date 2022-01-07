import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSSClient;
import com.msun.admin.AdminApplication;
import com.msun.admin.common.utils.HttpUtils;
import com.msun.admin.config.MinIoUtil;
import com.msun.admin.entity.dto.TokenReq;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


@SpringBootTest(classes = AdminApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
@Data
public class TestDemo {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    OSSClient ossClient;

    @Test
    public void getEncryPassword(){
        System.out.println("test begin");
        String encode = passwordEncoder.encode("123");
        System.out.println(encode);

        Digester md5 = new Digester(DigestAlgorithm.MD5);
        System.out.println(md5.digestHex("123"));

        md5.setSalt("localhost".getBytes());
        String digestHex = md5.digestHex("123");



        System.out.println(digestHex);

    }
    @Test
    public void uploadPic() throws FileNotFoundException {
        // 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        InputStream inputStream = new FileInputStream("C:\\Users\\Lenovo\\Pictures\\Saved Pictures\\gitlab.jpg");
        // 依次填写Bucket名称（例如examplebucket）和Object完整路径（例如exampledir/exampleobject.txt）。Object完整路径中不能包含Bucket名称。
        ossClient.putObject("human-face-snapshot-bucket", "test.jpg", inputStream);

    }
    @Autowired
    MinIoUtil minIoUtil;

    @Test
    public void minioTest() throws FileNotFoundException {
        System.out.println("结果为真");
        System.out.println(minIoUtil.bucketExists("wido-pictures-bucket"));
    }
    String V5HOST = "http://api.jfgou.com";



    /**
     * 读取流
     *
     * @param inStream
     * @return 字节数组
     * @throws Exception
     */
    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        return outSteam.toByteArray();
    }



}
