package com.equisoft.function.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.microsoft.azure.functions.ExecutionContext;

public class FunctionUtils {

    public static String getCurrentlyDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(new Date());
    }

    public static String getCurrentlyDateTimeWithPattern(String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(new Date());
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    public static String getUUIDWithName(String name) {
        return UUID.fromString(name).toString();
    }

    public static Map<Integer, String> callAPIMWithXML(String xmlbody, final ExecutionContext context)
            throws IOException {
        Map<Integer, String> resultMap = new HashMap<>();
        try {
            context.getLogger().info("FunctionUtils.callAPIMWithXML() started");
            CloseableHttpClient client = HttpClients.createDefault();
            String endpoint = System.getenv(CommonConstants.APIM_OIPAFileReceived_ENDPOINT);

            String uri = endpoint;
            HttpPost httpPost = new HttpPost(uri);

            StringEntity entity = new StringEntity(xmlbody);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/xml");
            httpPost.setHeader("Content-type", "application/xml");

            String apiSecurityToken = System.getenv(CommonConstants.OCP_APIM_SUBSCRIPTION_KEY);
            httpPost.setHeader(CommonConstants.OCP_APIM_SUBSCRIPTION_KEY_HEADER, apiSecurityToken);

            CloseableHttpResponse response = client.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity());
            int responseCode = response.getStatusLine().getStatusCode();
            resultMap.put(responseCode, responseBody);
            context.getLogger().info("FunctionUtils.callAPIM() File :  POST Response Code :  " + responseCode
                    + " - Description : " + responseBody);

            client.close();

        } catch (IOException e) {
            context.getLogger().warning("FunctionUtils.callAPIM(): " + " Error : " + e.toString());
            throw e;
        } finally {
            context.getLogger().info("FunctionUtils.callAPIM() ended");
        }
        return resultMap;
    }

}
