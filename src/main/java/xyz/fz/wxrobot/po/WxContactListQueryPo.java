package xyz.fz.wxrobot.po;

import com.fasterxml.jackson.core.JsonProcessingException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.fz.wxrobot.util.BaseUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static xyz.fz.wxrobot.WxFun.getOKHttpClientInstance;
import static xyz.fz.wxrobot.WxRobotApp.splitFlag;

/**
 * Created by fz on 2016/5/30.
 */
public class WxContactListQueryPo extends WxQueryPo {

    private Logger logger = LoggerFactory.getLogger(WxContactListQueryPo.class);

    private String url = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxgetcontact";
    private String urlParams = "";

    public WxContactListQueryPo(Map<String, Object> bodyParams) {
        this.bodyParams = bodyParams;
    }

    @Override
    public Vector result() {

        Map<String, Object> result = new HashMap<>();
        Vector vector = new Vector();
        OkHttpClient client = getOKHttpClientInstance();
        RequestBody body = null;
        try {
            body = RequestBody.create(JSON, BaseUtil.toJson(bodyParams));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            logger.error(BaseUtil.getExceptionStackTrace(e));
        }
        Request request = new Request.Builder().url(url + urlParams).post(body).build();
        String responseBody = "";
        try {
            logger.debug("[POST] " + url + urlParams);
            Response response = client.newCall(request).execute();
            responseBody = response.body().string();
            logger.debug("[POST BACK] " + responseBody);
            result = BaseUtil.parseJson(responseBody, Map.class);
            List memberList = (List) result.get("MemberList");
            for (Object member : memberList) {
                Map m = (Map) member;
                vector.add(m.get("NickName").toString() + splitFlag + m.get("UserName").toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(BaseUtil.getExceptionStackTrace(e));
            result.put("error", responseBody);
        }
        return vector;
    }
}
