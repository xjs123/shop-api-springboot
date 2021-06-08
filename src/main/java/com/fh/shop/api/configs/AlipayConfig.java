package com.fh.shop.api.configs;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {

//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

	// 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
	public static String app_id = "2021000117659279";
    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQD7myoDqX3lY5K6wPbVAzlAPBHWIuaCfhpdYimWv3cwR37dFSQ8Ix4vSaXLkJfBvrV+duh94yfCgO0nM9UbZeVRd7bkXDGmjphF5cTWm2Lj1fCnBbmR9wJaUWvYi0Kv6bEKDWrNM2zHZ6LwCn2N7VbwChmCt9aWYr7hHoOkAMT3J9oBTTp+1jpD6J0ZRpFef0VBL0gOwAha9KNd/uLDI0vUHm1Qbo0HdB1Gk6CY98CA1FOv9VCvtyszb9SEFmuaqVrSdkMytlbIhjenwAHqAKb5Ce+sJ9kIH56olg0O8bip79QIzJ0S6UHOMSqKc+yeo0jhBb/SmmkpJl1+5ExugJZRAgMBAAECggEBAMuiz6RbH4vyxSTlXGEW1NVKSN1ZLdtbmJcQBbz+hcbLdsAUhlktyxZWsxtIkaq7YUVTk+OfX+SqWEasSsR7+dev3pSAkabOLiwURPW0mZrw4m8r0P90PbE/yNtgCr85kpIiPdiK0HWrSVKiCvA9sWlhozgK9mCM1xR4j+7CuKeXPKY/N/15Hl0xkUxh8FZL84AUqtvHIB0OF0xYbOSJM55mJK4hCuMEHNbRnkPaM+gSsR9yZLnBpfksghUzCSRzOwNCtXpfXvwjsrmKlfbZNKSIVBKFnkqSQiaJopf4VWmITlvzS5pAaivwQdSxvAAU4r8Zj83aI97c+2OvXf2Fp+ECgYEA/pAN5Zvai5xxark2YAZuOpRJN9tU2Y64d/QTigIz6ipft+RcacY1j3J5XfAhF2hkTkqAGz5/wbEDLrQO47dDCpKNOLd2N0vDJotBcs3GIxyww4KuwVFAIxd1gamrrNZCXH5pbGi5GpT4VTFZ18e0oTB3jet+BXC6ZXZ/hZZpPp0CgYEA/QbWGyZ9LKPbFyoZOWckiot/2w0AOyl9MsRbaK0PRfsg1UZb5iItUxjKCAWR7LOrfFsaUV3rOXbAfrEHc7J/RLPesI0pbrqdBXHgRqZkXywG48Wn2LXHkcOrevzk6e1Ydy6j07Eg2gJ4O86itmD9riquJpe1NAV8KjlH9IukrkUCgYEAxegq1bKYcuf6iRVbP8ZgFizIy6M6UTWTFrbsJeRWhhvRdL8WVamiZFaEItMYdwFS0kc7V3L8khJTPhEAbmtAtMyA/HItdwnwu1uSU3XFL/ETP7yurl7DtUR8t4S4klO1HgGtZnn4RsFAW8NVbNu3A4YRvIUz4XjlgRJiS/JG+kUCgYAQzmBwQzkzOtr1ie1M+8XYkfV3FkD6rQHM2m/73urRyiFfKSI3gfOivlXE/KG5qCFuzBpOZz+VfIbCo32MOMHtTUkwTt/+o9SEJRExgCHQlHsMq/c/FKvhya5dJ+kIhKRInkdxayV0dY+I4Wu7U2vmIp2YoKD/iJG7uDxISPIyRQKBgHGlYBG8tBJervz9X9lX7cfBXPnns5QZXvmHNuIyTD2MlnDAMEv7IHXllHVXCbhJOId14C7598b5NcEeAg2ceb3yYDNw7rUslVNgWNguaM9VlCZzeySdWlYJZDXEziWr2rbMKhjOOP2bH/WH57PUqIlniN2uQnNM29ZlUplsomlC";

	// 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhrS3dv9waJjPvq6P5VpD1sSKOnz4RIbzFk6Bc970rea4pLYpMqnqXpqk1huiEjWNtB5FBdQfiHf5q5J2VCVKHe6ywFUFpmTxTHvgQDQcut53X7zzEPZnKCXHl7/5ftt2d16ly25WHU20qdRzWDPyXeWz/wuLFN/RD7qaU80UKB2WnmsDlxhrsk1o1gAP+DPu0dpDz3rqZ9vqcmbJknRAhYqbb2Mu3TuMhHpGiwbMss11FshfU2Wc6SHEOr6GKRiv59+FQfsge2bpR6oOBNq+8CT2L3spJCG4h/4mCNOEKZmqfIXg1SVJhU1Fsh18wLCPA3JiBZr0CThIWYUJs4D3twIDAQAB";

	// 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String notify_url = "http://3ca7mh.natappfree.cc:/api/pay/aliNotify";

	// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String return_url = "http://localhost:8084/api/pay/returnUrl";

	// 签名方式
	public static String sign_type = "RSA2";


	// 字符编码格式
	public static String charset = "utf-8";
	
	// 支付宝网关
	public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
	
	// 支付宝网关
	public static String log_path = "C:\\";



}

