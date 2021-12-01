package com.jstamps.docai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jstamps.docai.model.medInstitution.MedInstitution;

public interface MedInstitutionRepository extends JpaRepository<MedInstitution, String> {

}
