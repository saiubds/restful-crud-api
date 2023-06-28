package com.sai.app.restfulcrudapi.Repo;

import com.sai.app.restfulcrudapi.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
}
