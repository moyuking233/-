package com.msun.admin.config;

//import com.alipay.api.AlipayClient;
//import com.alipay.api.DefaultAlipayClient;

/**
 * 支付宝支付相关配置
 *
 * @author lwx
 */
public class AlipayConfig {

    //支付宝网关，这是沙箱的网关
    public static String URL = "https://openapi.alipaydev.com/gateway.do";
    //支付宝网关 - 正式环境
//    public static String URL = "https://openapi.alipay.com/gateway.do";

    //应用 APPID
    public static String ALIPAY_APPID = "";
    //应用私钥
    public static String APP_PRIVATE_KEY = "";
    //支付宝公钥
    public static String ALIPAY_PUBLIC_KEY = "";

    //签名算法类型(根据生成私钥的算法,RSA2或RSA)
    public static String SIGNTYPE = "RSA2";
    //请求数据格式
    public static final String FORMAT = "json";
    //编码集
    public static final String CHARSET = "utf-8";

    // 服务器异步通知页面路径,需 http://格式的完整路径，不能加自定义参数，必须外网可以正常访问
    public static String notify_url = "";
    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能自定义参数，支付成功后返回的页面
    public static String return_url = "http://www.uvstu.com:18000/demo/pay/returnUrl";

    // 统一收单交易创建接口
//    private static AlipayClient alipayClient = null;
//
//    /**获得初始化的AlipayClient
//     * @return 支付宝客户端
//     */
//    public static AlipayClient getAlipayClient() {
//        if (alipayClient == null) {
//            synchronized (AlipayConfig.class) {
//                if (null == alipayClient) {
//                    alipayClient = new DefaultAlipayClient(URL, ALIPAY_APPID, APP_PRIVATE_KEY, FORMAT, CHARSET,ALIPAY_PUBLIC_KEY,SIGNTYPE);
//                }
//            }
//        }
//        return alipayClient;
//    }

}