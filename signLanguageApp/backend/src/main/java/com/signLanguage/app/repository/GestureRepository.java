package com.signLanguage.app.repository;

import com.signLanguage.app.model.Gesture;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GestureRepository extends JpaRepository<Gesture, Integer> {
    List<Gesture> findBySignType(String signType);
}
