package com.lwj.bo;

import com.lwj.entity.EvaluationReport;
import com.lwj.entity.Unit;
import com.lwj.status.EvaluationStatus;
import com.lwj.status.ReportStatus;

/**
 * Created by liwj0 on 2017/7/25.
 */
public class UnitForEvaluation {
    private UnitBO unit;


    private Integer numberOnReport;

    private Double moneyOnReport;

    private ReportStatus status;

    public UnitForEvaluation() {
        this.numberOnReport = 0;
        this.moneyOnReport = 0.0;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public UnitBO getUnit() {
        return unit;
    }

    public void setUnit(UnitBO unit) {
        this.unit = unit;
    }


    public Integer getNumberOnReport() {
        return numberOnReport;
    }

    public void setNumberOnReport(Integer numberOnReport) {
        this.numberOnReport = numberOnReport;
    }

    public Double getMoneyOnReport() {
        return moneyOnReport;
    }

    public void setMoneyOnReport(Double moneyOnReport) {
        this.moneyOnReport = moneyOnReport;
    }
}
