package com.jstamps.docai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jstamps.docai.model.user.CommonUser;

public interface CommonUserRepository extends JpaRepository<CommonUser, String> {

    CommonUser findByUsername(String username);
}
