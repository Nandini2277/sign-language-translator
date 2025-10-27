package com.signlanguage.app.controller;

import com.signlanguage.app.service.GestureRecognitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/camera")
@CrossOrigin(origins = "*")
public class CameraController {
    
    @Autowired
    private GestureRecognitionService recognitionService;
    
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startCamera() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = recognitionService.startCamera();
            
            if (success) {
                response.put("success", true);
                response.put("message", "Camera started successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to start camera. Check if camera is available.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/stop")
    public ResponseEntity<Map<String, Object>> stopCamera() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            recognitionService.stopCamera();
            response.put("success", true);
            response.put("message", "Camera stopped successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/recognize")
    public ResponseEntity<Map<String, Object>> recognizeGesture() {
        try {
            Map<String, Object> result = recognitionService.captureAndRecognize();
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Recognition error: " + e.getMessage());
            error.put("gesture", "ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("isRunning", recognitionService.isRunning());
        status.put("currentGesture", recognitionService.getCurrentGesture());
        return ResponseEntity.ok(status);
    }
}