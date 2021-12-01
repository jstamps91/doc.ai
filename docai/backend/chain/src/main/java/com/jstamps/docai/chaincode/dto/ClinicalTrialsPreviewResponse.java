package com.jstamps.docai.chaincode.dto;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.List;

@DataType()
public class ClinicalTrialsPreviewResponse {

    @Property()
    private int total;

    @Property()
    private List<ClinicalTrialDto> clinicalTrialDtoList;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ClinicalTrialDto> getClinicalTrialDtoList() {
        return clinicalTrialDtoList;
    }

    public void setClinicalTrialDtoList(List<ClinicalTrialDto> clinicalTrialDtoList) {
        this.clinicalTrialDtoList = clinicalTrialDtoList;
    }

    public ClinicalTrialsPreviewResponse(int total, List<ClinicalTrialDto> clinicalTrialDtoList) {
        this.total = total;
        this.clinicalTrialDtoList = clinicalTrialDtoList;
    }
}
