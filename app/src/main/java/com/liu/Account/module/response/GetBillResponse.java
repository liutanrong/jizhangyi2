package com.liu.Account.module.response;

import com.liu.Account.database.Bill;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanrong on 16/10/3.
 */
public class GetBillResponse  extends BaseResponse {
    private List<Bill> data=new ArrayList<>();

    public List<Bill> getData() {
        return data;
    }

    public void setData(List<Bill> data) {
        this.data = data;
    }
}