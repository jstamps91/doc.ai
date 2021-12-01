package  com.jstamps.docai.fhir_server.converter;

public interface EntityFHIRConverter<DBEntity, FHIREntity>  {

    DBEntity convertFHIRToDBEntity(FHIREntity fhirEntity);
    FHIREntity convertDBToFhirEntity(DBEntity dbEntity);
}
