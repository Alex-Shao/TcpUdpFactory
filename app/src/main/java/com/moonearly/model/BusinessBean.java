package com.moonearly.model;

import java.io.Serializable;

/**
 * Created by Zun on 2016/7/26.
 * Bean基类
 */

public class BusinessBean implements Serializable, Cloneable {
    public BusinessBean() {
    }

    public BusinessBean clone() throws CloneNotSupportedException {
        return (BusinessBean) super.clone();
    }
}
