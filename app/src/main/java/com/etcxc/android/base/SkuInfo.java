package com.etcxc.android.base;

import java.util.ArrayList;
import java.util.List;

public class SkuInfo {
    public int id;
    public int proNum;
    public List<Integer> productProperty = new ArrayList<>();

    @Override
    public String toString() {
        return "SkuInfo{" +
                "id=" + id +
                ", proNum=" + proNum +
                ", productProperty=" + productProperty +
                '}';
    }
}
