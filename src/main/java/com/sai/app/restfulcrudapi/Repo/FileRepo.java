package com.sai.app.restfulcrudapi.Repo;

import com.sai.app.restfulcrudapi.Models.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepo extends JpaRepository<File, String> {
}
