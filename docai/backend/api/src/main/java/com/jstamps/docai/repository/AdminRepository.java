package com.jstamps.docai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jstamps.docai.model.user.Admin;

public interface AdminRepository extends JpaRepository<Admin, String> {

    Admin findByUsername(String username);
}
