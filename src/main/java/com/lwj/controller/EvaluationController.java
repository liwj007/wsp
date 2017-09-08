package com.lwj.controller;

import com.lwj.annotation.UserRight;
import com.lwj.data.ResponseData;
import com.lwj.entity.Evaluation;
import com.lwj.entity.Unit;
import com.lwj.entity.User;
import com.lwj.exception.WSPException;
import com.lwj.service.IEvaluationService;
import com.lwj.service.IUserService;
import com.lwj.status.*;
import com.lwj.utils.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liwj0 on 2017/7/24.
 */
@RestController
@RequestMapping(value = "/evaluations")
public class EvaluationController {
    @Autowired
    private IEvaluationService evaluationService;

    @Autowired
    private IUserService userService;

    @UserRight(authorities = {Rights.SCHOOL})
    @RequestMapping(value = "/pass", method = RequestMethod.PUT)
    public ResponseData passEvaluation(@RequestParam(value = "id", required = true) Long id,
                                       @RequestParam(value = "money", required = true) Double money) throws WSPException {
        if (id == null || money == null || money < 0) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        evaluationService.passEvaluation(id, money);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(null);
        return responseData;
    }

    @UserRight(authorities = {Rights.SCHOOL})
    @RequestMapping(value = "/approve_all", method = RequestMethod.PUT)
    public ResponseData passAllEvaluationOfUnit(@RequestParam(value = "year", required = true) Integer year,
                                       @RequestParam(value = "month", required = true) Integer month,
                                       @RequestParam(value = "unitId", required = true) Long unitId) throws WSPException {
        evaluationService.passAllEvaluationOfUnit(unitId, year,month);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(null);
        return responseData;
    }

    @UserRight(authorities = {Rights.SCHOOL})
    @RequestMapping(value = "/reject_all", method = RequestMethod.PUT)
    public ResponseData rejectAllEvaluationofUnit(@RequestParam(value = "year", required = true) Integer year,
                                                @RequestParam(value = "month", required = true) Integer month,
                                                @RequestParam(value = "unitId", required = true) Long unitId) throws WSPException {
        evaluationService.rejectAllEvaluationofUnit(unitId, year,month);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(null);
        return responseData;
    }

    @UserRight(authorities = {Rights.SCHOOL})
    @RequestMapping(value = "/release", method = RequestMethod.POST)
    public ResponseData fundRelease(@RequestParam(value = "year", required = true) Integer year,
                                    @RequestParam(value = "month", required = true) Integer month,
                                    @RequestParam(value = "name", required = true) String name,
                                    @RequestParam(value = "remark", required = true) String remark) throws WSPException {
        evaluationService.fundRelease(name, remark, year, month);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(null);
        return responseData;
    }

    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/submit", method = RequestMethod.PUT)
    public ResponseData submitToShcool(String token,
                                       @RequestParam(value = "reportId", required = false) Long reportId,
                                       @RequestParam(value = "year", required = true) Integer year,
                                       @RequestParam(value = "month", required = true) Integer month,
                                       @RequestParam(value = "name", required = true) String name,
                                       @RequestParam(value = "remark", required = true) String remark) throws WSPException {
        User user = userService.getUserByToken(token);
        evaluationService.submitToSchool(reportId,user, name, remark, year, month);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(null);
        return responseData;
    }

    @UserRight(authorities = {Rights.SCHOOL})
    @RequestMapping(value = "/audit", method = RequestMethod.GET)
    public ResponseData getReportApplicationsForCheck(@RequestParam(value = "id", required = true) Long id,
                                                      @RequestParam(value = "year", required = true) Integer year,
                                                      @RequestParam(value = "month", required = true) Integer month,
                                                      @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) throws WSPException {
        if (id == null || year == null || month == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        HashMap<String, Object> result = evaluationService.getReportEvaluationsForCheckForPage(id, year, month, pageable);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(result);
        return responseData;
    }

    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/reported", method = RequestMethod.GET)
    public ResponseData getReportApplications(String token,
                                              @RequestParam(value = "year", required = true) Integer year,
                                              @RequestParam(value = "month", required = true) Integer month,
                                              @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) throws WSPException {
        User user = userService.getUserByToken(token);
        if (user.getUnit() == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        HashMap<String, Object> result = evaluationService.getReportEvaluationsForPage(user.getUnit(), year, month, pageable);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(result);
        return responseData;
    }

    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/check", method = RequestMethod.PUT)
    public ResponseData checkEvaluation(@RequestParam(value = "id", required = false) Long id,
                                        @RequestParam(value = "appId", required = true) Long appId,
                                        @RequestParam(value = "workload", required = true) Workload workload,
                                        @RequestParam(value = "workAttitude", required = true) WorkAttitude workAttitude,
                                        @RequestParam(value = "workResult", required = true) WorkResult workResult,
                                        @RequestParam(value = "money", required = true) Double money,
                                        @RequestParam(value = "year", required = false) Integer year,
                                        @RequestParam(value = "month", required = false) Integer month,
                                        @RequestParam(value = "assessment", required = true) String assessment) throws WSPException {
        evaluationService.checkEvaluation(id, appId,workload, workAttitude, workResult, assessment, money,year,month);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(null);
        return responseData;
    }

    @UserRight(authorities = {Rights.STUDENT})
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ResponseData createEvalution(@PathVariable long id,
                                        @RequestParam(value = "year", required = true) Integer year,
                                        @RequestParam(value = "month", required = true) Integer month,
                                        @RequestParam(value = "content", required = true) String content) throws WSPException {
        if (year == null || month == null || content == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        evaluationService.createEvaluation(id, year, month, content);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(null);
        return responseData;
    }

    @UserRight(authorities = {Rights.STUDENT})
    @RequestMapping(value = "/myself", method = RequestMethod.GET)
    public ResponseData getMyEvaluation(String token,
                                        @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        User user = userService.getUserByToken(token);
        Page<Evaluation> result = evaluationService.getMyEvaluationForPage(user, pageable);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(result);
        return responseData;
    }

    @UserRight(authorities = {Rights.STUDENT})
    @RequestMapping(value = "/myself_pass", method = RequestMethod.GET)
    public ResponseData getMyPassEvaluation(String token,
                                            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        User user = userService.getUserByToken(token);
        Page<Evaluation> result = evaluationService.getMyPassEvaluationForPage(user, pageable);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(result);
        return responseData;
    }

    @UserRight(authorities = {Rights.SCHOOL})
    @RequestMapping(value = "/fund_list", method = RequestMethod.GET)
    public ResponseData getFundList(@RequestParam(value = "year", required = true) Integer year,
                                    @RequestParam(value = "month", required = true) Integer month,
                                    @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) throws WSPException {
        if (year == null || month == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        HashMap<String, Object> res = evaluationService.getFundList(year, month, pageable);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.STUDENT})
    @RequestMapping(value = "/has_evaluation", method = RequestMethod.GET)
    public ResponseData hasEvaluation(String token,
                                      @RequestParam(value = "year") Integer year,
                                      @RequestParam(value = "month") Integer month,
                                      @RequestParam(value = "appId") Long appId) throws WSPException {
        String res = evaluationService.hasEvaluation(appId, year,month);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/can_submit_to_school", method = RequestMethod.GET)
    public ResponseData canSubmitToSchool(String token,
                                      @RequestParam(value = "year") Integer year,
                                      @RequestParam(value = "month") Integer month) throws WSPException {
        User user = userService.getUserByToken(token);
        Unit unit = user.getUnit();
        boolean res = evaluationService.canSubmitToSchool(unit, year,month);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/export_of_unit", method = RequestMethod.GET)
    public String exportEvaluationsOfUnit(HttpServletResponse response,
                                 String token,
                                 @RequestParam(value = "year", required = true) Integer year,
                                 @RequestParam(value = "month", required = true) Integer month) throws IOException, WSPException {
        String fileName = String.format("%d年%d月资金发放详情", year, month);
        User user = userService.getUserByToken(token);
        List<Evaluation> evaluations = evaluationService.getExportRecords(user.getUnit(),year, month, EvaluationStatus.REPORT);
        List<Map<String, Object>> list = createExcelRecord(evaluations);

        String columnNames[] = {"在岗单位", "岗位名称", "姓名", "学号", "银行账号", "工资"};//列名
        String keys[] = {"unitName", "jobName", "userName", "userNo", "bankCard", "money"};//map中的key
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ExcelUtil.createWorkBook(list, keys, columnNames).write(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] content = os.toByteArray();
        InputStream is = new ByteArrayInputStream(content);
        // 设置response参数，可以打开下载页面
        response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String((fileName + ".xls").getBytes("GB2312"), "iso-8859-1"));
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

    @UserRight(authorities = {Rights.SCHOOL})
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public String exportFundList(HttpServletResponse response,
                                 @RequestParam(value = "year", required = true) Integer year,
                                 @RequestParam(value = "month", required = true) Integer month) throws IOException, WSPException {
        String fileName = String.format("%d年%d月资金发放详情", year, month);

        List<Evaluation> evaluations = evaluationService.getExportRecords(year, month, EvaluationStatus.PASS);
        List<Map<String, Object>> list = createExcelRecord(evaluations);

//        String columnNames[] = {"在岗单位", "岗位名称", "姓名", "学号", "银行账号", "工资"};//列名
        String columnNames[] = {"学号", "姓名",  "工资"};
//        String keys[] = {"unitName", "jobName", "userName", "userNo", "bankCard", "money"};//map中的key
        String keys[] = { "userNo", "userName", "money"};
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ExcelUtil.createWorkBook(list, keys, columnNames).write(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] content = os.toByteArray();
        InputStream is = new ByteArrayInputStream(content);
        // 设置response参数，可以打开下载页面
        response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String((fileName + ".xls").getBytes("GB2312"), "iso-8859-1"));
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

    private List<Map<String, Object>> createExcelRecord(List<Evaluation> evaluations) {
        List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("sheetName", "sheet1");
        listmap.add(map);
        Evaluation evaluation = null;
        for (int j = 0; j < evaluations.size(); j++) {
            evaluation = evaluations.get(j);
            Map<String, Object> mapValue = new HashMap<String, Object>();
            mapValue.put("unitName", evaluation.getUnit().getName());
            mapValue.put("jobName", evaluation.getJob().getName());
            mapValue.put("userName", evaluation.getApplication().getApplicant().getRealName());
            mapValue.put("userNo", evaluation.getApplication().getApplicant().getUserName());
            mapValue.put("bankCard", evaluation.getApplication().getApplicant().getBankCard());
            mapValue.put("money", evaluation.getRealMoney());
            listmap.add(mapValue);
        }
        return listmap;
    }

}
