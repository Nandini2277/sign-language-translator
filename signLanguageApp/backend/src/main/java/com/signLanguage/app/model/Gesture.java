package com.signLanguage.app.model;

import javax.persistence.*;

@Entity
@Table(name = "gestures")
public class Gesture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "sign_type")
    private String signType;
    
    @Column(name = "gesture_name")
    private String gestureName;
    
    @Column(name = "text_translation")
    private String textTranslation;
    
    private String description;
    
    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getSignType() { return signType; }
    public void setSignType(String signType) { this.signType = signType; }
    public String getGestureName() { return gestureName; }
    public void setGestureName(String gestureName) { this.gestureName = gestureName; }
    public String getTextTranslation() { return textTranslation; }
    public void setTextTranslation(String textTranslation) { this.textTranslation = textTranslation; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
