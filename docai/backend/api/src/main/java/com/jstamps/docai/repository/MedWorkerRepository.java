package com.jstamps.docai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jstamps.docai.model.medInstitution.MedInstitution;
import com.jstamps.docai.model.user.MedWorker;

import java.util.List;

public interface MedWorkerRepository extends JpaRepository<MedWorker, String> {

    MedWorker findByUsername(String username);

    List<MedWorker> findAllByRole(String role);
    List<MedWorker> findAllByMedInstitution(MedInstitution medInstitution);
    List<MedWorker> findAllByMedInstitutionAndRole(MedInstitution medInstitution, String role);
}
