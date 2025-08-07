package com.voicetodo.app;

public enum Priority {
    LOW("Düşük", "#4CAF50", "📗", 1),       // Green
    NORMAL("Normal", "#2196F3", "📘", 2),   // Blue  
    HIGH("Yüksek", "#FF9800", "📙", 3),     // Orange
    URGENT("Acil", "#F44336", "📕", 4);     // Red

    private final String displayName;
    private final String colorHex;
    private final String emoji;
    private final int level;

    Priority(String displayName, String colorHex, String emoji, int level) {
        this.displayName = displayName;
        this.colorHex = colorHex;
        this.emoji = emoji;
        this.level = level;
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

    public int getLevel() {
        return level;
    }

    public String getDisplayWithEmoji() {
        return emoji + " " + displayName;
    }

    // Voice command'den öncelik tespit etme
    public static Priority fromVoiceCommand(String text) {
        String lowerText = text.toLowerCase().trim();
        
        // URGENT kelimeler
        if (lowerText.contains("acil") || lowerText.contains("hemen") || 
            lowerText.contains("kritik") || lowerText.contains("çok önemli") ||
            lowerText.contains("ivedi") || lowerText.contains("derhal")) {
            return URGENT;
        }
        
        // HIGH kelimeler  
        if (lowerText.contains("önemli") || lowerText.contains("yüksek") ||
            lowerText.contains("öncelik") || lowerText.contains("mutlaka") ||
            lowerText.contains("gerekli") || lowerText.contains("şart")) {
            return HIGH;
        }
        
        // LOW kelimeler
        if (lowerText.contains("düşük") || lowerText.contains("basit") ||
            lowerText.contains("kolay") || lowerText.contains("vakit olursa") ||
            lowerText.contains("acele yok") || lowerText.contains("sonra")) {
            return LOW;
        }
        
        return NORMAL; // Default öncelik
    }

    // Kategori ile kombineli akıllı tespit
    public static Priority fromVoiceCommandWithCategory(String text, Category category) {
        Priority basePriority = fromVoiceCommand(text);
        
        // Kategori bazlı öncelik ayarlaması
        if (category == Category.URGENT) {
            return basePriority.getLevel() < URGENT.getLevel() ? URGENT : basePriority;
        } else if (category == Category.WORK) {
            // İş kategorisi genelde normal ya da yüksek öncelik
            return basePriority == LOW ? NORMAL : basePriority;
        } else if (category == Category.HEALTH) {
            // Sağlık genelde yüksek öncelik
            return basePriority.getLevel() < HIGH.getLevel() ? HIGH : basePriority;
        }
        
        return basePriority;
    }
} 