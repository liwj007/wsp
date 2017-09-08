package com.lwj.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.ebigdata.icp.pojo.Address;
import org.ebigdata.icp.pojo.SchoolUser;
import org.ebigdata.icp.pojo.Student;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by liwj0 on 2017/7/16.
 */
@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = true)
    private String email;


    @Column(nullable = true)
    private int gender;

    @Column(nullable = false)
    private int userType;

    @Column(nullable = false)
    private String realName;

    @Column(nullable = true)
    private String mobile;

    @Transient
    private Address address;

    @Column(nullable = true)
    private int peopleId;

    @Column(nullable = true)
    private String peopleName;

    @Column(nullable = true)
    private int politicalId;

    @Column(nullable = true)
    private String politicalName;

    @Column(nullable = true)
    private String bankCard;

    @Column(nullable = true)
    private String IDCard;

    @Column(nullable = true)
    private Date birthday;

    @Column(nullable = true)
    private String incumbent;

    @Column(nullable = true)
    private Date entranceTime;

    @Column(nullable = true)
    private int residenceType;

    @Column(nullable = true)
    private int schoolLength;

    @Column(nullable = true)
    private String postcode;

    @Column(nullable = true)
    private int entranceScore;

    @Column(nullable = false)
    private int userRight;

    @Column(nullable = true)
    private String token;

    @Column(nullable = true)
    private String apiToken;

    @Column(nullable = true)
    private Long expire;

    @Column
    private String className;

    @Column
    private String majorName;

    @Column
    private String collegeName;

    @Column
    private String gradeName;

    @Transient
    private Address origin;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "applicant")
    private List<Application> applications;

    @JsonBackReference
    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "unitId")
    @JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
    private Unit unit;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public Address getOrigin() {
        return origin;
    }

    public void setOrigin(Address origin) {
        this.origin = origin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getPeopleId() {
        return peopleId;
    }

    public void setPeopleId(int peopleId) {
        this.peopleId = peopleId;
    }

    public String getPeopleName() {
        return peopleName;
    }

    public void setPeopleName(String peopleName) {
        this.peopleName = peopleName;
    }

    public int getPoliticalId() {
        return politicalId;
    }

    public void setPoliticalId(int politicalId) {
        this.politicalId = politicalId;
    }

    public String getPoliticalName() {
        return politicalName;
    }

    public void setPoliticalName(String politicalName) {
        this.politicalName = politicalName;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getIDCard() {
        return IDCard;
    }

    public void setIDCard(String IDCard) {
        this.IDCard = IDCard;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getIncumbent() {
        return incumbent;
    }

    public void setIncumbent(String incumbent) {
        this.incumbent = incumbent;
    }

    public Date getEntranceTime() {
        return entranceTime;
    }

    public void setEntranceTime(Date entranceTime) {
        this.entranceTime = entranceTime;
    }

    public int getResidenceType() {
        return residenceType;
    }

    public void setResidenceType(int residenceType) {
        this.residenceType = residenceType;
    }

    public int getSchoolLength() {
        return schoolLength;
    }

    public void setSchoolLength(int schoolLength) {
        this.schoolLength = schoolLength;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public int getEntranceScore() {
        return entranceScore;
    }

    public void setEntranceScore(int entranceScore) {
        this.entranceScore = entranceScore;
    }

    public int getUserRight() {
        return userRight;
    }

    public void setUserRight(int userRight) {
        this.userRight = userRight;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getExpire() {
        return expire;
    }

    public void setExpire(Long expire) {
        this.expire = expire;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public User (){

    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public User (Student icpUser){
        this.userName=icpUser.getUserName();
        this.realName=icpUser.getRealName();
        this.bankCard=icpUser.getBankCard();
        this.birthday=icpUser.getBirthday();
        this.email=icpUser.getEmail();
        this.entranceScore=icpUser.getEntranceScore();
        this.entranceTime=icpUser.getEntranceTime();
        this.gender=icpUser.getGender();
        this.userType=icpUser.getUserType();
        this.mobile=icpUser.getMobile();
        this.peopleId=icpUser.getPeopleId();
        this.peopleName=icpUser.getPeopleName();
        this.politicalId=icpUser.getPoliticalId();
        this.politicalName=icpUser.getPoliticalName();
        this.IDCard=icpUser.getIDCard();
        this.incumbent = icpUser.getIncumbent();
        this.residenceType=icpUser.getResidenceType();
        this.postcode=icpUser.getPostcode();
        if (icpUser.getClasses()!=null){
            this.className = icpUser.getClasses().getClassName();
            this.majorName = icpUser.getClasses().getMajorName();
            this.collegeName = icpUser.getClasses().getCollegeName();
            this.gradeName = icpUser.getClasses().getGradeName();
        }
    }

    public User (org.ebigdata.icp.pojo.User icpUser){
        this.userName=icpUser.getUserName();
        this.realName=icpUser.getRealName();
        this.bankCard=icpUser.getBankCard();
        this.birthday=icpUser.getBirthday();
        this.email=icpUser.getEmail();
        this.entranceScore=icpUser.getEntranceScore();
        this.entranceTime=icpUser.getEntranceTime();
        this.gender=icpUser.getGender();
        this.userType=icpUser.getUserType();
        this.mobile=icpUser.getMobile();
        this.peopleId=icpUser.getPeopleId();
        this.peopleName=icpUser.getPeopleName();
        this.politicalId=icpUser.getPoliticalId();
        this.politicalName=icpUser.getPoliticalName();
        this.IDCard=icpUser.getIDCard();
        this.incumbent = icpUser.getIncumbent();
        this.residenceType=icpUser.getResidenceType();
        this.postcode=icpUser.getPostcode();
    }
}
