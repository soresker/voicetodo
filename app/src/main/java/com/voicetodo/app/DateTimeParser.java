package com.voicetodo.app;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateTimeParser {
    
    public static class ParsedDateTime {
        private Date date;
        private String cleanText;
        private boolean hasTime;
        private boolean hasDate;
        
        public ParsedDateTime(Date date, String cleanText, boolean hasTime, boolean hasDate) {
            this.date = date;
            this.cleanText = cleanText;
            this.hasTime = hasTime;
            this.hasDate = hasDate;
        }
        
        public Date getDate() { return date; }
        public String getCleanText() { return cleanText; }
        public boolean hasTime() { return hasTime; }
        public boolean hasDate() { return hasDate; }
        public boolean hasDateTime() { return hasTime || hasDate; }
    }
    
    public static ParsedDateTime parseDateTime(String text) {
        String originalText = text;
        String cleanText = text;
        Calendar calendar = Calendar.getInstance();
        boolean hasTime = false;
        boolean hasDate = false;
        
        // Zaman parsing patterns
        Pattern timePattern = Pattern.compile("(\\d{1,2})[:\\.]?(\\d{2})(?:\\s*(?:da|de|ta|te))?", Pattern.CASE_INSENSITIVE);
        Pattern hourOnlyPattern = Pattern.compile("(\\d{1,2})\\s*(?:da|de|ta|te)", Pattern.CASE_INSENSITIVE);
        
        // Saat parsing
        Matcher timeMatcher = timePattern.matcher(text);
        if (timeMatcher.find()) {
            try {
                int hour = Integer.parseInt(timeMatcher.group(1));
                int minute = Integer.parseInt(timeMatcher.group(2));
                
                if (hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59) {
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);
                    hasTime = true;
                    cleanText = cleanText.replaceFirst(timeMatcher.group(0), "").trim();
                }
            } catch (NumberFormatException e) {
                // Ignore invalid time
            }
        } else {
            // Sadece saat (dakika yok)
            Matcher hourMatcher = hourOnlyPattern.matcher(text);
            if (hourMatcher.find()) {
                try {
                    int hour = Integer.parseInt(hourMatcher.group(1));
                    if (hour >= 0 && hour <= 23) {
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        hasTime = true;
                        cleanText = cleanText.replaceFirst(hourMatcher.group(0), "").trim();
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid hour
                }
            }
        }
        
        // Tarih parsing
        String lowerText = text.toLowerCase();
        
        if (lowerText.contains("bugün")) {
            // Bugün - tarih değişmez
            hasDate = true;
            cleanText = cleanText.replaceAll("(?i)bugün", "").trim();
        } else if (lowerText.contains("yarın")) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            hasDate = true;
            cleanText = cleanText.replaceAll("(?i)yarın", "").trim();
        } else if (lowerText.contains("öbür gün") || lowerText.contains("öbür günü")) {
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            hasDate = true;
            cleanText = cleanText.replaceAll("(?i)öbür gün[ü]?", "").trim();
        } else if (lowerText.contains("gelecek hafta")) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            hasDate = true;
            cleanText = cleanText.replaceAll("(?i)gelecek hafta", "").trim();
        }
        
        // Haftanın günleri
        String[] weekDays = {"pazartesi", "salı", "çarşamba", "perşembe", "cuma", "cumartesi", "pazar"};
        int[] weekDayValues = {Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, 
                              Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};
        
        for (int i = 0; i < weekDays.length; i++) {
            if (lowerText.contains(weekDays[i])) {
                int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
                int targetDay = weekDayValues[i];
                
                // Gelecek hafta aynı gün
                int daysToAdd = (targetDay - currentDay + 7) % 7;
                if (daysToAdd == 0 && !lowerText.contains("bugün")) {
                    daysToAdd = 7; // Gelecek hafta aynı gün
                }
                
                calendar.add(Calendar.DAY_OF_MONTH, daysToAdd);
                hasDate = true;
                cleanText = cleanText.replaceAll("(?i)" + weekDays[i], "").trim();
                break;
            }
        }
        
        // Ay isimleri ile tarih parsing
        String[] months = {"ocak", "şubat", "mart", "nisan", "mayıs", "haziran",
                          "temmuz", "ağustos", "eylül", "ekim", "kasım", "aralık"};
        
        for (int i = 0; i < months.length; i++) {
            Pattern monthPattern = Pattern.compile("(\\d{1,2})\\s+" + months[i], Pattern.CASE_INSENSITIVE);
            Matcher monthMatcher = monthPattern.matcher(lowerText);
            if (monthMatcher.find()) {
                try {
                    int day = Integer.parseInt(monthMatcher.group(1));
                    calendar.set(Calendar.MONTH, i);
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    
                    // Eğer tarih geçmişte kalıyorsa, gelecek yıla al
                    if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                        calendar.add(Calendar.YEAR, 1);
                    }
                    
                    hasDate = true;
                    cleanText = cleanText.replaceFirst(monthMatcher.group(0), "").trim();
                } catch (NumberFormatException e) {
                    // Ignore invalid day
                }
                break;
            }
        }
        
        // Temizlik: gereksiz kelimeleri kaldır
        cleanText = cleanText.replaceAll("\\s+", " ").trim();
        cleanText = cleanText.replaceAll("(?i)\\b(da|de|ta|te|için|saat|saate)\\b", "").trim();
        cleanText = cleanText.replaceAll("\\s+", " ").trim();
        
        Date parsedDate = null;
        if (hasTime || hasDate) {
            parsedDate = calendar.getTime();
        }
        
        return new ParsedDateTime(parsedDate, cleanText, hasTime, hasDate);
    }
    
    // Tarih formatı helper
    public static String formatDateTime(Date date, boolean hasTime, boolean hasDate) {
        if (date == null) return "";
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        Calendar now = Calendar.getInstance();
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        
        StringBuilder result = new StringBuilder();
        
        // Tarih kısmı
        if (hasDate) {
            if (isSameDay(cal, now)) {
                result.append("Bugün");
            } else if (isSameDay(cal, tomorrow)) {
                result.append("Yarın");
            } else {
                // Hafta içi gün adı + tarih
                String[] dayNames = {"", "Pazar", "Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi"};
                result.append(dayNames[cal.get(Calendar.DAY_OF_WEEK)]);
                result.append(" ").append(cal.get(Calendar.DAY_OF_MONTH));
                result.append("/").append(cal.get(Calendar.MONTH) + 1);
            }
        }
        
        // Saat kısmı
        if (hasTime) {
            if (result.length() > 0) result.append(" ");
            result.append(String.format("%02d:%02d", 
                         cal.get(Calendar.HOUR_OF_DAY), 
                         cal.get(Calendar.MINUTE)));
        }
        
        return result.toString();
    }
    
    private static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
} 