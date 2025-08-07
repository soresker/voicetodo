package com.voicetodo.app;

public enum Category {
    PERSONAL("Kişisel", "#2196F3", "🏠"),      // Blue
    WORK("İş", "#FF9800", "💼"),              // Orange  
    URGENT("Acil", "#F44336", "🚨"),          // Red
    SHOPPING("Alışveriş", "#4CAF50", "🛒"),   // Green
    HEALTH("Sağlık", "#E91E63", "🏥"),        // Pink
    STUDY("Eğitim", "#9C27B0", "📚"),         // Purple
    OTHER("Diğer", "#607D8B", "📋");          // Blue Grey

    private final String displayName;
    private final String colorHex;
    private final String emoji;

    Category(String displayName, String colorHex, String emoji) {
        this.displayName = displayName;
        this.colorHex = colorHex;
        this.emoji = emoji;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColorHex() {
        return colorHex;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getDisplayWithEmoji() {
        return emoji + " " + displayName;
    }

    // Voice command'den kategori tespit etme
    public static Category fromVoiceCommand(String text) {
        String lowerText = text.toLowerCase().trim();
        
        if (lowerText.contains("iş") || lowerText.contains("çalışma") || 
            lowerText.contains("toplantı") || lowerText.contains("ofis") ||
            lowerText.contains("proje") || lowerText.contains("rapor")) {
            return WORK;
        }
        
        if (lowerText.contains("acil") || lowerText.contains("önemli") || 
            lowerText.contains("hemen") || lowerText.contains("bugün")) {
            return URGENT;
        }
        
        if (lowerText.contains("alışveriş") || lowerText.contains("market") || 
            lowerText.contains("satın") || lowerText.contains("al ")) {
            return SHOPPING;
        }
        
        if (lowerText.contains("doktor") || lowerText.contains("hastane") || 
            lowerText.contains("sağlık") || lowerText.contains("ilaç") ||
            lowerText.contains("muayene")) {
            return HEALTH;
        }
        
        if (lowerText.contains("ders") || lowerText.contains("ödev") || 
            lowerText.contains("sınav") || lowerText.contains("eğitim") ||
            lowerText.contains("öğren") || lowerText.contains("okul")) {
            return STUDY;
        }
        
        return PERSONAL; // Default kategori
    }
} 