package com.lwj.controller;

import com.lwj.annotation.UserRight;
import com.lwj.data.ResponseData;
import com.lwj.entity.*;
import com.lwj.exception.WSPException;
import com.lwj.repository.ApplicationRepository;
import com.lwj.repository.EvaluationRepository;
import com.lwj.repository.JobRepository;
import com.lwj.repository.UserRepository;
import com.lwj.service.IApplicationService;
import com.lwj.service.IUserService;
import com.lwj.status.*;
import com.lwj.utils.ExcelUtil;
import org.apache.poi.xwpf.usermodel.*;
import org.ebigdata.icp.pojo.ICPOAuth;
import org.ebigdata.icp.service.UserService;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.lwj.status.StaticParams.PAGE_SIZE;
import static org.ebigdata.icp.pojo.UserType.STUDENT_TYPE;

/**
 * Created by liwj0 on 2017/7/23.
 */
@RestController
@RequestMapping(value = "/applications")
public class ApplicationController {

    @Autowired
    private IApplicationService applicationService;

    @Autowired
    private IUserService userService;

    @UserRight(authorities = {Rights.STUDENT})
    @RequestMapping(value = "/working", method = RequestMethod.GET)
    public ResponseData getWorkingApplications(String token,
                                               @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        User user = userService.getUserByToken(token);
        Page<Application> res = applicationService.getStudentWorkingApplicationsForPage(user, pageable);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/status/{id}", method = RequestMethod.PUT)
    public ResponseData interviewStatusUpdate(@PathVariable Long id,
                                              @RequestParam(value = "status") InterviewStatus interviewStatus,
                                              @RequestParam(value = "date") String date,
                                              @RequestParam(value = "time") String time,
                                              @RequestParam(value = "position") String position) throws WSPException {
        if (id == null || interviewStatus == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }

        Date interviewDate = null;
        if (interviewStatus == InterviewStatus.SEND) {

            if ("".equals(date) &&  !"".equals(time)) {
                throw new WSPException(ErrorInfo.PARAMS_ERROR);
            }

            if ((date != null && !"".equals(date)) && (time == null || "".equals(time))) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    interviewDate = sdf.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (date != null && !"".equals(date) && time != null && !"".equals(time)) {
                String dateString = String.format("%s %s", date, time);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    interviewDate = sdf.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        applicationService.updateInterviewStatus(id, interviewStatus, interviewDate, position);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(null);
        return responseData;
    }

    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/result/{id}", method = RequestMethod.PUT)
    public ResponseData interviewResultUpdate(@PathVariable Long id, @RequestParam(value = "result") InterviewResult interviewResult,
                                              @RequestParam(value = "assessment", required = false) String interviewAssessment) throws WSPException {
        if (id == null || interviewResult == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        applicationService.updateInterviewResult(id, interviewResult, interviewAssessment);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(null);
        return responseData;
    }

    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseData getApplicationsOfUnit(String token,
                                              @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        User user = userService.getUserByToken(token);
        Page<Application> res = applicationService.getWorkingApplicationOfUnitForPage(user.getUnit(), pageable);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.STUDENT})
    @RequestMapping(value = "/myself", method = RequestMethod.GET)
    public ResponseData getMyApplyApplications(String token,
                                               @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        User user = userService.getUserByToken(token);
        Page<Application> res = applicationService.getApplyingApplicationForPage(user, pageable);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.STUDENT})
    @RequestMapping(value = "/myself/all", method = RequestMethod.GET)
    public ResponseData getMyAllApplications(String token,
                                             @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        User user = userService.getUserByToken(token);
        Page<Application> res = applicationService.getStudentAllApplicationsForPage(user, pageable);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public ResponseData getHistoryApplications(String token,
                                               @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        User user = userService.getUserByToken(token);
        Page<Application> res = applicationService.getHistoryApplicationsForPage(user.getUnit(), pageable);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.UNIT, Rights.SCHOOL})
    @RequestMapping(value = "/validate_student", method = RequestMethod.GET)
    public ResponseData validateStudent(String userName, Long jobId) throws WSPException {
        boolean res = applicationService.validateStudent(jobId, userName);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.STUDENT})
    @RequestMapping(value = "/check_applied", method = RequestMethod.GET)
    public ResponseData checkApplyStatus(String token) throws WSPException {
        User user = userService.getUserByToken(token);
        boolean res = applicationService.checkApplyStatus(user);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.STUDENT})
    @RequestMapping(value = "/evaluation_date", method = RequestMethod.GET)
    public ResponseData getMyEvaluationDate(String token) {
        User user = userService.getUserByToken(token);
        Date date = applicationService.getMyEvaluationDate(user);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(date);
        return responseData;
    }


    @UserRight(authorities = {Rights.STUDENT})
    @RequestMapping(value = "/my_history", method = RequestMethod.GET)
    public ResponseData getMyHistoryApplications(String token,
                                                 @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        User user = userService.getUserByToken(token);
        Page<Application> res = applicationService.getMyHistoryApplicationsForPage(user, pageable);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.UNIT, Rights.SCHOOL})
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseData addApplication(String token,
                                       @RequestParam(value = "jId") Long id,
                                       @RequestParam(value = "userNo") String userNo) throws WSPException {
        if (id == null || userNo == null || "".equals(userNo)) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        User user = userService.getUserByToken(token);
        ICPOAuth icpoAuth = new ICPOAuth();
        icpoAuth.setAccessToken(user.getApiToken());
        icpoAuth.setUrl(CommonVariable.oauthServer);
        boolean full = applicationService.addApplicationDirect(id, userNo, icpoAuth);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(full);
        return responseData;
    }

    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/fire", method = RequestMethod.PUT)
    public ResponseData fire(String token, @RequestParam(value = "id") Long id,
                             @RequestParam(value = "reason") FireReason fireReason,
                             @RequestParam(value = "remark", required = false) String remark) throws WSPException {
        if (id == null || fireReason == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        applicationService.fireStudent(id, fireReason, remark);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(null);
        return responseData;
    }

    @UserRight(authorities = {Rights.UNIT, Rights.STUDENT})
    @RequestMapping(value = "/print", method = RequestMethod.GET)
    public String printFiredApplication(String token, HttpServletResponse response, @RequestParam(value = "id") Long id) throws IOException {
        User user = userService.getUserByToken(token);
        Application application = applicationService.getApplicationById(id);

        XWPFDocument document = new XWPFDocument();

        //Write the Document in file system
//        FileOutputStream out = new FileOutputStream(new File("解雇详情.docx"));

        //添加标题
        XWPFParagraph titleParagraph = document.createParagraph();
        //设置段落居中
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleParagraphRun = titleParagraph.createRun();
        titleParagraphRun.setText("解聘详情");
        titleParagraphRun.setColor("000000");
        titleParagraphRun.setFontSize(20);

        XWPFParagraph paragraph1 = document.createParagraph();
        XWPFRun paragraphRun1 = paragraph1.createRun();
        paragraphRun1.setText("\r");

        //基本信息表格
        XWPFTable infoTable = document.createTable();
        //去表格边框
        infoTable.getCTTbl().getTblPr().unsetTblBorders();

        //列宽自动分割
        CTTblWidth infoTableWidth = infoTable.getCTTbl().addNewTblPr().addNewTblW();
        infoTableWidth.setType(STTblWidth.DXA);
        infoTableWidth.setW(BigInteger.valueOf(9072));

        //表格第一行
        XWPFTableRow infoTableRowOne = infoTable.getRow(0);
        infoTableRowOne.getCell(0).setText("姓名：");
        infoTableRowOne.addNewTableCell().setText(application.getApplicant().getRealName());

        //表格第二行
        XWPFTableRow infoTableRowTwo = infoTable.createRow();
        infoTableRowTwo.getCell(0).setText("学号：");
        infoTableRowTwo.getCell(1).setText(application.getApplicant().getUserName());

        //表格第四行
        XWPFTableRow infoTableRowFour = infoTable.createRow();
        infoTableRowFour.getCell(0).setText("学院:");
        infoTableRowFour.getCell(1).setText(application.getApplicant().getCollegeName());

        //表格第五行
        XWPFTableRow infoTableRowFive = infoTable.createRow();
        infoTableRowFive.getCell(0).setText("班级：");
        infoTableRowFive.getCell(1).setText(application.getApplicant().getGradeName());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        XWPFTableRow infoTableRowSix = infoTable.createRow();
        infoTableRowSix.getCell(0).setText("在岗时间：");
        infoTableRowSix.getCell(1).setText(formatter.format(application.getHiredDate()) + "~" + formatter.format(application.getFiredDate()));

        XWPFTableRow infoTableRow7 = infoTable.createRow();
        infoTableRow7.getCell(0).setText("解聘原因：");
        infoTableRow7.getCell(1).setText(application.getFireReason().desc);

        XWPFTableRow infoTableRow8 = infoTable.createRow();
        infoTableRow8.getCell(0).setText("备注：");
        infoTableRow8.getCell(1).setText(application.getFireRemark());

        if (user.getUserRight() == Rights.UNIT) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun paragraphRun = paragraph.createRun();
            paragraphRun.setText("\r");
            paragraphRun.setText("\r");

            XWPFTable ComTable = document.createTable();
            ComTable.getCTTbl().getTblPr().unsetTblBorders();
            CTTblWidth comTableWidth = ComTable.getCTTbl().addNewTblPr().addNewTblW();
            comTableWidth.setType(STTblWidth.DXA);
            comTableWidth.setW(BigInteger.valueOf(9072));

            XWPFTableRow comTableRowOne = ComTable.getRow(0);
            comTableRowOne.getCell(0).setText("签字");
            comTableRowOne.addNewTableCell().setText("");
            comTableRowOne.addNewTableCell().setText("盖章");
            comTableRowOne.addNewTableCell().setText("");

            XWPFTableRow comTableRowTwo = ComTable.createRow();
            comTableRowTwo.getCell(0).setText("解聘时间");
            comTableRowTwo.getCell(1).setText("");
            comTableRowTwo.getCell(2).setText("");
            comTableRowTwo.getCell(3).setText("");
        }


        String fileName = application.getApplicant().getRealName() + "解聘详情";
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            document.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] content = os.toByteArray();
        InputStream is = new ByteArrayInputStream(content);
        // 设置response参数，可以打开下载页面
        response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String((fileName + ".doc").getBytes("GB2312"), "iso-8859-1"));
        ServletOutputStream out = response.getOutputStream();
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];
            int bytesRead;
            // Simple read/write loop.
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (final IOException e) {
            throw e;
        } finally {
            if (bis != null)
                bis.close();
            if (bos != null)
                bos.close();
        }
        return null;

    }

    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/evaluation", method = RequestMethod.GET)
    public ResponseData getApplicationsForEvaluation(
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(value = "jobId") Long jid,
            @RequestParam(value = "year") Integer year,
            @RequestParam(value = "month") Integer month) throws WSPException, ParseException {
        if (jid == null || year == null || month == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        HashMap<String, Object> res = applicationService.getApplicationsForEvaluationForPage(jid, year, month, pageable);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    private void sendMessage() {
        // TODO: 2017/7/26  给学生发送站内信
    }
}
