import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.*;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class Main {

//    private static ConcurrentHashMap<String, String> cache = new ConcurrentHashMap();

//    static {
//        String regionId = "cn-hangzhou"; //必填固定值，必须为“cn-hanghou”
//        String accessKeyId = "accessKeyId"; // your accessKey
//        String accessKeySecret = "accessKeySecret";// your accessSecret
//        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
//        // 若报Can not find endpoint to access异常，请添加以下此行代码
//        // DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Alidns", "alidns.aliyuncs.com");
//        client = new DefaultAcsClient(profile);
//    }

    public static String getIP() {
        String ip = null;
        try {
            String urlBaidu = "https://api.ipify.org/?format=json";
            OkHttpClient okHttpClient = new OkHttpClient(); // 创建OkHttpClient对象
            Request request = new Request.Builder().url(urlBaidu).build(); // 创建一个请求
            Response response = okHttpClient.newCall(request).execute(); // 返回实体
            if (response.isSuccessful()) { // 判断是否成功
                /**获取返回的数据，可通过response.body().string()获取，默认返回的是utf-8格式；
                 * string()适用于获取小数据信息，如果返回的数据超过1M，建议使用stream()获取返回的数据，
                 * 因为string() 方法会将整个文档加载到内存中。*/

                JSONObject reponseJson = JSON.parseObject(response.body().string());
                ip = reponseJson.getString("ip");
            } else {
                System.out.println("失败"); // 链接失败
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip;
    }

    public static void main(String[] args) {

        Properties properties = readConf(args[0]);
        String accessKeyId = properties.getProperty("accessKeyId");
        String accessKeySecret = properties.getProperty("accessKeySecret");
        String domainName = properties.getProperty("domainName");
        String regionId = "cn-hangzhou"; //必填固定值，必须为“cn-hanghou”
        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        // 若报Can not find endpoint to access异常，请添加以下此行代码
        // DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Alidns", "alidns.aliyuncs.com");
        IAcsClient client = new DefaultAcsClient(profile);

//     DescribeDomainsRequest request = new DescribeDomainsRequest();
//        DescribeDomainsResponse response;
//        // describeRegionsRequest.setProtocol(ProtocolType.HTTPS); //指定访问协议
//        // describeRegionsRequest.setAcceptFormat(FormatType.JSON); //指定api返回格式
//        // describeRegionsRequest.setMethod(MethodType.POST); //指定请求方法
//        // describeRegionsRequest.setRegionId("cn-hangzhou");//指定要访问的Region,仅对当前请求生效，不改变client的默认设置。
//        try {
//            response = client.getAcsResponse(request);
//            List<DescribeDomainsResponse.Domain> list = response.getDomains();
//            for (DescribeDomainsResponse.Domain domain : list) {
//                System.out.println(domain.getDomainName());
//                System.out.println(domain.getDomainId());
//                System.out.println(domain.getInstanceId());
//                System.out.println("-----------------------------------------------------");
//            }
//        } catch (ServerException e) {
//            e.printStackTrace();
//        } catch (ClientException e) {
//            e.printStackTrace();
//        }
//
        try {

            DescribeDomainRecordsRequest describeDomainRecordsRequest = new DescribeDomainRecordsRequest();
            describeDomainRecordsRequest.setDomainName(domainName);
            describeDomainRecordsRequest.setActionName("DescribeDomainRecords");
            DescribeDomainRecordsResponse describeDomainRecordsResponse = client.getAcsResponse(describeDomainRecordsRequest);
            List<DescribeDomainRecordsResponse.Record> domainRecords = describeDomainRecordsResponse.getDomainRecords();
            String ip = getIP();
            if (domainRecords.size() > 0) {
                if (ip.equals(domainRecords.get(0).getValue())) {
                    System.out.println("IP duplicate ,exit!");
                    System.exit(0);
                }
                UpdateDomainRecordRequest updateDomainRecordRequest = new UpdateDomainRecordRequest();
                updateDomainRecordRequest.setActionName("UpdateDomainRecord");
                updateDomainRecordRequest.setRecordId(domainRecords.get(0).getRecordId());
                updateDomainRecordRequest.setRR("@");
                updateDomainRecordRequest.setType("A");
                updateDomainRecordRequest.setValue(ip);
                UpdateDomainRecordResponse updateDomainRecordResponse = client.getAcsResponse(updateDomainRecordRequest);
                System.out.println("ip update : " + ip);
            } else {
                AddDomainRecordRequest addDomainRecordRequest = new AddDomainRecordRequest();
                addDomainRecordRequest.setDomainName(domainName);
                addDomainRecordRequest.setActionName("AddDomainRecord");
                addDomainRecordRequest.setRR("@");
                addDomainRecordRequest.setType("A");
                addDomainRecordRequest.setValue(ip);
                AddDomainRecordResponse addDomainRecordResponse = client.getAcsResponse(addDomainRecordRequest);
                System.out.println("ip added : " + ip);
            }

        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    private static Properties readConf(String configFile) {
        Properties properties = new Properties();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(configFile));
            properties.load(bufferedReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}
