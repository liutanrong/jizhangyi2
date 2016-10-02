package com.liu.Account.module.request;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by tanrong on 16/10/3.
 */
public class GetBillRequest {
    private Long userId;
    private Set<String> havedBill=new HashSet<>();

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Set<String> getHavedBill() {
        return havedBill;
    }

    public void setHavedBill(Set<String> havedBill) {
        this.havedBill = havedBill;
    }
}
