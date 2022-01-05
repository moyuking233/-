import com.baidu.translate.demo.TransApi;
import org.junit.Test;

public class TestBaidu {

    // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    private static final String APP_ID = "20210713000887310";
    private static final String SECURITY_KEY = "ljHzcM6ocHZDSKSmdJpH";
    @Test
    public static void main(String[] args) {
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
        String query = "高度600米";
        System.out.println(api.getTransResult(query, "auto", "en"));
    }

}
