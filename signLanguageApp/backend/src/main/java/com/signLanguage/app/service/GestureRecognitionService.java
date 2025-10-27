package com.signlanguage.app.service;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;

@Service
public class GestureRecognitionService {
    
    private VideoCapture camera;
    private boolean isRunning = false;
    private String currentGesture = "NO_HAND";
    private double currentConfidence = 0.0;
    private Mat lastFrame;
    private String lastStableGesture = "NO_HAND";
    private int gestureStabilityCount = 0;
    private static final int STABILITY_THRESHOLD = 3;
    
    static {
        // Load OpenCV native library with absolute path
        System.load("D:\\signLanguageApp\\backend\\lib\\opencv_java4120.dll");
        System.out.println("OpenCV loaded successfully: " + Core.VERSION);
    }

    
    @PostConstruct
    public void init() {
        System.out.println("Gesture Recognition Service initialized");
    }
    
    @PreDestroy
    public void cleanup() {
        stopCamera();
    }
    
    public synchronized boolean startCamera() {
        if (isRunning) {
            return true;
        }
        
        try {
            camera = new VideoCapture(0);
            
            if (!camera.isOpened()) {
                System.err.println("Failed to open camera");
                return false;
            }
            
            camera.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
            camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);
            camera.set(Videoio.CAP_PROP_FPS, 30);
            
            isRunning = true;
            System.out.println("Camera started successfully");
            return true;
            
        } catch (Exception e) {
            System.err.println("Error starting camera: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public synchronized void stopCamera() {
        if (camera != null && camera.isOpened()) {
            camera.release();
            isRunning = false;
            currentGesture = "NO_HAND";
            System.out.println("Camera stopped");
        }
    }
    
    public synchronized Map<String, Object> captureAndRecognize() {
        Map<String, Object> result = new HashMap<>();
        
        if (camera == null || !camera.isOpened()) {
            result.put("success", false);
            result.put("message", "Camera not initialized");
            result.put("gesture", "NO_HAND");
            return result;
        }
        
        try {
            Mat frame = new Mat();
            
            if (camera.read(frame) && !frame.empty()) {
                lastFrame = frame.clone();
                
                // Detect gesture
                GestureResult gestureResult = detectGesture(frame);
                
                currentGesture = gestureResult.gestureName;
                currentConfidence = gestureResult.confidence;
                
                result.put("success", true);
                result.put("gesture", gestureResult.gestureName);
                result.put("translation", gestureResult.translation);
                result.put("confidence", gestureResult.confidence);
                result.put("fingerCount", gestureResult.fingerCount);
                
            } else {
                result.put("success", false);
                result.put("message", "Failed to capture frame");
                result.put("gesture", "NO_HAND");
            }
            
        } catch (Exception e) {
            System.err.println("Error during recognition: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", e.getMessage());
            result.put("gesture", "NO_HAND");
        }
        
        return result;
    }
    
    private GestureResult detectGesture(Mat frame) {
        // Convert to HSV for better skin detection
        Mat hsv = new Mat();
        Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_BGR2HSV);
        
        // Define skin color range in HSV
        Scalar lowerSkin = new Scalar(0, 20, 70);
        Scalar upperSkin = new Scalar(20, 255, 255);
        
        // Threshold to get skin regions
        Mat mask = new Mat();
        Core.inRange(hsv, lowerSkin, upperSkin, mask);
        
        // Apply morphological operations to remove noise
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, kernel);
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel);
        
        // Apply Gaussian blur
        Imgproc.GaussianBlur(mask, mask, new Size(5, 5), 0);
        
        // Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, 
                           Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        
        if (contours.isEmpty()) {
            return new GestureResult("NO_HAND", "No hand detected", 0.0, 0);
        }
        
        // Find largest contour (hand)
        MatOfPoint handContour = findLargestContour(contours);
        
        if (handContour == null || Imgproc.contourArea(handContour) < 5000) {
            return new GestureResult("NO_HAND", "Hand too small or far", 0.0, 0);
        }
        
        // Analyze hand shape FIRST
        GestureResult result = analyzeHandShape(handContour, mask);
        
        // THEN apply gesture stability check
        if (result.gestureName.equals(lastStableGesture)) {
            gestureStabilityCount++;
        } else {
            gestureStabilityCount = 0;
            lastStableGesture = result.gestureName;
        }

        // Only return stable gesture after threshold
        if (gestureStabilityCount < STABILITY_THRESHOLD) {
            return new GestureResult("NO_HAND", "Stabilizing...", 0.0, 0);
        }
        
        return result;
    }
    
    private MatOfPoint findLargestContour(List<MatOfPoint> contours) {
        MatOfPoint maxContour = null;
        double maxArea = 0;
        
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area > maxArea) {
                maxArea = area;
                maxContour = contour;
            }
        }
        
        return maxContour;
    }
    
    private GestureResult analyzeHandShape(MatOfPoint contour, Mat mask) {
        try {
            // Get convex hull
            MatOfInt hullIndices = new MatOfInt();
            Imgproc.convexHull(contour, hullIndices);
            
            // Convert hull indices to points
            List<Point> contourPoints = contour.toList();
            List<Point> hullPoints = new ArrayList<>();
            int[] hullIndicesArray = hullIndices.toArray();
            
            for (int idx : hullIndicesArray) {
                if (idx < contourPoints.size()) {
                    hullPoints.add(contourPoints.get(idx));
                }
            }
            
            MatOfPoint hullContour = new MatOfPoint();
            hullContour.fromList(hullPoints);
            
            // Find convexity defects
            MatOfInt4 defects = new MatOfInt4();
            if (hullIndices.rows() > 3) {
                Imgproc.convexityDefects(contour, hullIndices, defects);
            }
            
            // Count fingers based on defects
            int fingerCount = 0;
            
            if (!defects.empty()) {
                fingerCount = countFingers(defects, contourPoints);
            }
            
            // Calculate confidence based on contour area and circularity
            double area = Imgproc.contourArea(contour);
            double perimeter = Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true);
            double circularity = 4 * Math.PI * area / (perimeter * perimeter);
            double confidence = Math.min(0.7 + circularity * 0.3, 0.98);
            
            // Map finger count to gestures
            return mapFingersToGesture(fingerCount, confidence);
            
        } catch (Exception e) {
            System.err.println("Error analyzing hand shape: " + e.getMessage());
            return new GestureResult("UNKNOWN", "Analysis failed", 0.5, 0);
        }
    }
    
    private int countFingers(MatOfInt4 defects, List<Point> contourPoints) {
        int fingerCount = 0;
        int[] defectArray = defects.toArray();
        
        for (int i = 0; i < defectArray.length; i += 4) {
            int startIdx = defectArray[i];
            int endIdx = defectArray[i + 1];
            int farIdx = defectArray[i + 2];
            double depth = defectArray[i + 3] / 256.0;
            
            if (startIdx >= contourPoints.size() || 
                endIdx >= contourPoints.size() || 
                farIdx >= contourPoints.size()) {
                continue;
            }
            
            Point start = contourPoints.get(startIdx);
            Point end = contourPoints.get(endIdx);
            Point far = contourPoints.get(farIdx);
            
            // Calculate angle at the defect point
            double a = distance(start, far);
            double b = distance(end, far);
            double c = distance(start, end);
            
            double angle = Math.acos((a * a + b * b - c * c) / (2 * a * b));
            angle = Math.toDegrees(angle);
            
            // Filter defects based on angle and depth
            if (angle <= 90 && depth > 20) {
                fingerCount++;
            }
        }
        
        // Add 1 because defects count gaps between fingers
        return fingerCount + 1;
    }
    
    private double distance(Point p1, Point p2) {
        double dx = p1.x - p2.x;
        double dy = p1.y - p2.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    private GestureResult mapFingersToGesture(int fingerCount, double confidence) {
        switch (fingerCount) {
            case 0:
            case 1:
                return new GestureResult("YES", "Yes", confidence * 0.9, fingerCount);
            case 2:
                return new GestureResult("NO", "No", confidence * 0.88, fingerCount);
            case 3:
                return new GestureResult("LOVE", "I love you", confidence * 0.85, fingerCount);
            case 4:
                return new GestureResult("THANK_YOU", "Thank you", confidence * 0.92, fingerCount);
            case 5:
                return new GestureResult("HELLO", "Hello", confidence * 0.95, fingerCount);
            default:
                return new GestureResult("UNKNOWN", "Unknown gesture", confidence * 0.6, fingerCount);
        }
    }
    
    public boolean isRunning() {
        return isRunning;
    }
    
    public String getCurrentGesture() {
        return currentGesture;
    }
    
    // Inner class for gesture results
    private static class GestureResult {
        String gestureName;
        String translation;
        double confidence;
        int fingerCount;
        
        GestureResult(String gestureName, String translation, double confidence, int fingerCount) {
            this.gestureName = gestureName;
            this.translation = translation;
            this.confidence = confidence;
            this.fingerCount = fingerCount;
        }
    }
}