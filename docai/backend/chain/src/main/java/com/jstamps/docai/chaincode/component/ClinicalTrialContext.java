package com.jstamps.docai.chaincode.component;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;
import com.jstamps.docai.chaincode.dao.ClinicalTrialAccessRequestDAO;
import com.jstamps.docai.chaincode.dao.ClinicalTrialDAO;

public class ClinicalTrialContext extends Context {

    private ClinicalTrialDAO clinicalTrialDAO;
    private ClinicalTrialAccessRequestDAO clinicalTrialAccessRequestDAO;

    public ClinicalTrialContext(ChaincodeStub stub) {
        super(stub);
        clinicalTrialDAO = new ClinicalTrialDAO(this);
        clinicalTrialAccessRequestDAO = new ClinicalTrialAccessRequestDAO(this);
    }

    public ClinicalTrialDAO getClinicalTrialDAO() {
        return clinicalTrialDAO;
    }

    public ClinicalTrialAccessRequestDAO getClinicalTrialAccessRequestDAO() {
        return clinicalTrialAccessRequestDAO;
    }
}