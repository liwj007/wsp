package com.lwj.bo;

/**
 * Created by liwj0 on 2017/8/2.
 */
public class JobBO {
    private Long id;
    private String name;
    private boolean full;
    private Integer hiredNum;

    public JobBO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getHiredNum() {
        return hiredNum;
    }

    public void setHiredNum(Integer hiredNum) {
        this.hiredNum = hiredNum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }
}
