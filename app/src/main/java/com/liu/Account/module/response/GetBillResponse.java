package com.liu.Account.module.response;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.liu.Account.database.Bill;

import java.util.List;

/**
 * Created by tanrong on 16/10/3.
 */
public class GetBillResponse  extends BaseResponse {
    private JSONArray data=new JSONArray();

    public JSONArray getData() {
        return data;
    }
    public List<Bill> getDataBill() {
        return JSON.parseArray(JSON.toJSONString(data),Bill.class);
    }
    public void setData(JSONArray data) {
        this.data = data;
    }
}