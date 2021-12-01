package com.jstamps.docai.converter;

import org.hl7.fhir.r4.model.ImagingStudy;
import org.springframework.stereotype.Component;
import com.jstamps.docai.converter.declaration.ConverterInterface;
import com.jstamps.docai.dto.form.ClinicalTrialForm;
import com.jstamps.docai.dto.medInstitution.ClinicalTrialDto;
import com.jstamps.docai.enums.AccessType;
import com.jstamps.docai.enums.ClinicalTrialType;
import com.jstamps.docai.util.StringUtil;

@Component
public class ClinicalTrialConverter implements ConverterInterface<ImagingStudy, ClinicalTrialDto, ClinicalTrialForm> {

    @Override
    public ClinicalTrialDto convertToDto(ImagingStudy object) {
        ClinicalTrialType clinicalTrialType = ClinicalTrialType.valueOf(object.getModality().get(0).getCode());
        String resource = null;
        if(object.getSeries().size() > 0){
            resource = object.getSeries().get(0).getBodySite().getCode();
            resource = StringUtil.getIdPart(resource);
        }
        String relevantParameters = "";
        if(object.getProcedureCode().size() > 0){
            relevantParameters = object.getProcedureCode().get(0).getText();
        }
        String introduction = object.getMeta().getSource();
        return ClinicalTrialDto
                .builder()
                .id(StringUtil.getIdPart(object.getId()))
                .introduction(introduction)
                .time(object.getStarted())
                .conclusion(object.getDescription())
                .relevantParameters(relevantParameters)
                .clinicalTrialType(clinicalTrialType)
                .patient(object.getSubject().getReference())
                .doctor(object.getLocation().getDisplay())
                .doctorId(object.getLocation().getReference())
                .accessType(convertAccessType(object.getStatus()))
                .resourcePath(resource)
                .build();
    }

    @Override
    public ImagingStudy convertFromDto(ClinicalTrialForm object, boolean update) {
        if(update){
            return null;
        } else {
            return new ImagingStudy();
        }
    }

    private AccessType convertAccessType(ImagingStudy.ImagingStudyStatus imagingStudyStatus){
        switch (imagingStudyStatus) {
            case REGISTERED: return AccessType.IDLE;
            case AVAILABLE: return AccessType.UNCONDITIONAL;
            case ENTEREDINERROR: return AccessType.ASK_FOR_ACCESS;
            case CANCELLED:
            default:
                return AccessType.FORBIDDEN;
        }
    }

    public ImagingStudy.ImagingStudyStatus convertAccessType(AccessType accessType){
        switch (accessType) {
            case IDLE: return ImagingStudy.ImagingStudyStatus.REGISTERED;
            case UNCONDITIONAL: return ImagingStudy.ImagingStudyStatus.AVAILABLE;
            case ASK_FOR_ACCESS: return ImagingStudy.ImagingStudyStatus.ENTEREDINERROR;
            case FORBIDDEN:
            default:
                return ImagingStudy.ImagingStudyStatus.CANCELLED;
        }
    }
}
