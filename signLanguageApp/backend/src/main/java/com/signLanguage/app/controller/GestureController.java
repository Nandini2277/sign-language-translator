package com.signLanguage.app.controller;

import com.signLanguage.app.model.Gesture;
import com.signLanguage.app.repository.GestureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class GestureController {
    
    @Autowired
    private GestureRepository gestureRepository;
    
    @GetMapping("/gestures/{signType}")
    public List<Gesture> getGestures(@PathVariable String signType) {
        return gestureRepository.findBySignType(signType);
    }
    
    @GetMapping("/gestures")
    public List<Gesture> getAllGestures() {
        return gestureRepository.findAll();
    }
}
