package com.jstamps.docai.fhir_server.entity.imagingStudy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Coding {

    private String display;
    private String system;
    private String code;
}
