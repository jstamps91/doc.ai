package com.jstamps.docai.chaincode.dao;

import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import com.jstamps.docai.chaincode.dto.ClinicalTrialAccessRequestDto;
import com.jstamps.docai.chaincode.entity.ClinicalTrial;
import com.jstamps.docai.chaincode.entity.ClinicalTrialAccessRequest;

import java.util.List;

public class ClinicalTrialAccessRequestDAO {

    private ClinicalTrialAccessRequestCRUD clinicalTrialAccessRequestCRUD;
    private ClinicalTrialAccessRequestQuery clinicalTrialAccessRequestQuery;

    public ClinicalTrialAccessRequestDAO(Context context){
        clinicalTrialAccessRequestCRUD = new ClinicalTrialAccessRequestCRUD(context, ClinicalTrialAccessRequest.class.getSimpleName(), new Genson());
        clinicalTrialAccessRequestQuery = new ClinicalTrialAccessRequestQuery(context, ClinicalTrialAccessRequest.class.getSimpleName());
    }

    public ClinicalTrialAccessRequest addClinicalTrialAccessRequest(
            String patientId,
            String time,
            String requesterId,
            String decision,
            String accessAvailableFrom,
            String accessAvailableUntil,
            boolean anonymity,
            ClinicalTrial clinicalTrial
    ) {
        return clinicalTrialAccessRequestCRUD.addClinicalTrialAccessRequest(
          patientId,
          time,
          requesterId,
          decision,
          accessAvailableFrom,
          accessAvailableUntil,
          anonymity,
          clinicalTrial
        );
    }

    public ClinicalTrialAccessRequest addClinicalTrialAccessRequest(
            String patientId,
            String time,
            String requesterId,
            ClinicalTrial clinicalTrial
    ) {
        return clinicalTrialAccessRequestCRUD.addClinicalTrialAccessRequest(
                patientId,
                time,
                requesterId,
                clinicalTrial
        );
    }

    public ClinicalTrialAccessRequest defineClinicalTrialAccessRequest(
            String key,
            String decision,
            String accessAvailableFrom,
            String accessAvailableUntil,
            boolean anonymity,
            ClinicalTrialAccessRequest clinicalTrialAccessRequest
    ) {
        return clinicalTrialAccessRequestCRUD.defineClinicalTrialAccessRequest(key, decision, accessAvailableFrom, accessAvailableUntil, anonymity, clinicalTrialAccessRequest);
    }

    public ClinicalTrialAccessRequest getClinicalTrialAccessRequest(String key) {
        return clinicalTrialAccessRequestCRUD.getClinicalTrialAccessRequest(key);
    }

    public boolean clinicalTrialAccessRequestExist(final String key) {
        return clinicalTrialAccessRequestCRUD.clinicalTrialAccessRequestExist(key);
    }

    public List<ClinicalTrialAccessRequestDto> getRequestedTrialAccesses(String requester){
        return clinicalTrialAccessRequestQuery.getRequestedTrialAccesses(requester);
    }

    public List<ClinicalTrialAccessRequestDto> getTrialAccessRequestsHistory(String patient){
        return clinicalTrialAccessRequestQuery.getTrialAccessRequestsHistory(patient);
    }

    public List<ClinicalTrialAccessRequestDto> getTrialAccessRequests(String patient){
        return clinicalTrialAccessRequestQuery.getTrialAccessRequests(patient);
    }
}
