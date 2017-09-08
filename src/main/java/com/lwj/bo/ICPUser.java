package com.lwj.bo;

import com.lwj.entity.User;

/**
 * Created by liwj0 on 2017/7/20.
 */
public class ICPUser {
    private String userName;
    private Long id;
    private Long unitId;
    private String unitName;

    public ICPUser() {

    }

    public ICPUser(User user) {
        this.userName = user.getUserName();
        this.id = user.getId();
        this.unitId = user.getUnit() == null ? null : user.getUnit().getId();
        this.unitName = user.getUnit() == null ? null : user.getUnit().getName();
    }

    public ICPUser(org.ebigdata.icp.pojo.User user) {
        this.userName = user.getUserName();
        this.id = null;
        this.unitId = null;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}
