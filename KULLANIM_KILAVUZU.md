# 🎤 Voice Todo - Kullanım Kılavuzu

**Sesli komutlarla todo yönetiminde yeni çağ!**

Voice Todo, sesli komutlar ve akıllı arayüz ile todo yönetimini kolaylaştıran modern bir Android uygulamasıdır.

---

## 📱 Uygulama Genel Bakış

### ✨ Ana Özellikler
- 🎤 **Offline Sesli Todo Ekleme** - İnternet gerektirmez
- 🗣️ **Akıllı Sesli Komutlar** - Todo işlemleri için sesli kontrol
- 👆 **Swipe Actions** - Hızlı todo yönetimi
- 🌙 **Dark/Light Mode** - Otomatik tema değişimi
- ✨ **Görsel Animasyonlar** - Modern kullanıcı deneyimi
- 📳 **Haptic Feedback** - Dokunsal geri bildirim
- 💾 **Offline Depolama** - Veriler cihazda güvenle saklanır

---

## 🎯 Temel Kullanım

### 🎤 Todo Ekleme
1. **Mikrofon butonuna** basın (pulse animasyon başlayacak)
2. **Konuşun**: "Bugün alışverişe gideceğim"
3. **Otomatik ekleme**: Todo listeye slide-in animasyon ile eklenir
4. **Haptic feedback**: Telefon titreyerek onay verir

### ✅ Todo Tamamlama
**Manuel:** Checkbox'a tıklayın
**Swipe:** Todo'ya **sağa kaydırın** (yeşil arkaplan)
**Sesli:** "tamamla" komutu

### 🗑️ Todo Silme
**Manuel:** Çöp kutusu butonuna tıklayın
**Swipe:** Todo'ya **sola kaydırın** (kırmızı arkaplan)
**Sesli:** "sil" komutu

---

## 🗣️ Sesli Komutlar Rehberi

### 📝 Todo İşlemleri
```
🎤 "Yarın toplantıya katılacağım"     → Yeni todo ekler
🎤 "tamamla" / "bitir" / "tamam"      → Son todo'yu tamamlar
🎤 "sil" / "silme"                    → Son todo'yu siler
🎤 "temizle" / "temizlik"             → Tamamlanan todo'ları temizler
```

### 📊 Bilgi Komutları
```
🎤 "say" / "kaç tane" / "adet"        → Todo istatistiklerini söyler
🎤 "oku" / "listele"                  → Todo'ları sesli okur
```

### 💡 Komut Örnekleri
```
👤 "Pazartesi doktora gideceğim"      → ✅ Todo eklendi
👤 "tamamla"                          → ✅ "Pazartesi doktora gideceğim" tamamlandı
👤 "Alışverişe çıkacağım"             → ✅ Yeni todo eklendi  
👤 "sil"                              → ✅ "Alışverişe çıkacağım" silindi
👤 "kaç tane todo var"                → ✅ "Toplam 1 todo var. 1 tamamlandı, 0 bekliyor"
```

---

## 👆 Swipe Actions (Kaydırma İşlemleri)

### ➡️ Sağa Kaydırma (Tamamla)
- **Görsel**: Yeşil arkaplan + ✅ ikon + "Tamamla" yazısı
- **İşlem**: Todo'yu tamamlandı/tamamlanmadı olarak işaretler
- **Feedback**: Başarı mesajı + haptic feedback

### ⬅️ Sola Kaydırma (Sil)
- **Görsel**: Kırmızı arkaplan + 🗑️ ikon + "Sil" yazısı
- **İşlem**: Todo'yu kalıcı olarak siler
- **Animasyon**: Slide-out animasyon ile todo kaybolur

### 🎯 Kullanım İpuçları
- **%30 kaydırma** yeterli (tam kaydırmaya gerek yok)
- **Yavaş kaydırma** → Önizleme görürsünüz
- **Hızlı kaydırma** → Anında işlem yapılır

---

## 🌙 Dark/Light Mode

### 🔄 Tema Değiştirme
**Android 10+:**
1. Ayarlar → Ekran → Koyu tema (aç/kapat)
2. Hızlı Ayarlar → Koyu tema butonuna bas

**Android 9 ve altı:**
1. Ayarlar → Ekran → Tema → Koyu seç

### 🎨 Otomatik Tema
- **Sistem ayarlarınıza** göre otomatik değişir
- **Gece saatlerinde** koyu tema
- **Gündüz saatlerinde** açık tema
- **Batarya tasarrufu** için koyu tema önerilir

---

## ✨ Animasyonlar ve Efektler

### 🎭 Görsel Animasyonlar
- **Pulse Animasyon**: Mikrofon dinlerken
- **Slide-in**: Yeni todo ekleme
- **Slide-out**: Todo silme
- **Bounce**: Onay işlemleri

### 📳 Haptic Feedback
- **Todo ekleme**: Kısa titreşim (100ms)
- **İşlem tamamlama**: Kısa titreşim (50ms)
- **Mikrofon aktif**: Kısa titreşim (100ms)

### 🎵 Sesli Geri Bildirim
- **İşlem onayları**: Toast mesajları
- **Hata durumları**: Uyarı mesajları
- **İstatistik bilgileri**: Uzun toast mesajları

---

## 📱 Uygulama Arayüzü

### 🏠 Ana Ekran Bileşenleri
```
┌─────────────────────────┐
│      Voice Todo         │  ← Başlık
├─────────────────────────┤
│          🎤             │  ← Mikrofon Butonu (FAB)
│                         │
│ Todo eklemek için ses   │  ← Durum Metni
│    butonu kullan        │
├─────────────────────────┤
│ ☐ Todo item 1    12:30  │  ← Todo Listesi
│ ☑ Todo item 2    11:45  │  
│ ☐ Todo item 3    10:15  │
└─────────────────────────┘
```

### 🃏 Todo Card Bileşenleri
```
┌─────────────────────────────────┐
│ ☐  Todo metni buraya yazılır  12:30  🗑️ │
│    ↑         ↑                ↑    ↑   │
│ Checkbox   Metin           Saat  Sil    │
└─────────────────────────────────┘
        ↑                    ↑
   Sağa kaydır           Sola kaydır
   (Tamamla)              (Sil)
```

---

## 🔧 Gelişmiş Özellikler

### 🧠 Akıllı Komut Tanıma
- **Büyük/küçük harf duyarsız**: "SİL" = "sil" = "Sil"
- **Kısmi eşleşme**: "tamamla" komutunda "tamam" da çalışır
- **Türkçe destek**: Tamamen Türkçe komutlar
- **Hata toleransı**: Benzer komutları anlayabilir

### 📊 İstatistik Sistemi
```
🎤 "kaç tane todo var" komutu ile:
📈 Toplam todo sayısı
✅ Tamamlanan todo sayısı  
⏳ Bekleyen todo sayısı
📋 Detaylı bilgi raporu
```

### 🔄 Veri Yönetimi
- **Otomatik kaydetme**: Her işlem anında kaydedilir
- **Offline çalışma**: İnternet gerektirmez
- **Room Database**: Güvenli ve hızlı veri saklama
- **Crash koruması**: Uygulama kapansa veri kaybolmaz

---

## 🚀 Performans İpuçları

### ⚡ Hızlı Kullanım
1. **Kısa komutlar** kullanın: "sil" > "son todo'yu sil"
2. **Swipe actions** manual butonlardan daha hızlı
3. **Sesli komutlar** sık kullanılan işlemler için ideal
4. **Dark mode** batarya tasarrufu sağlar

### 🔧 Optimizasyon
- **Temizlik**: Düzenli olarak "temizle" komutu kullanın
- **Sıralama**: Yeni todo'lar listenin altında görünür
- **Hafıza**: Uygulama minimum RAM kullanır
- **Depolama**: Todo'lar çok az yer kaplar

---

## ❓ Sık Sorulan Sorular

### ❔ **Mikrofon çalışmıyor**
✅ **Çözüm**: Ayarlar → Uygulamalar → Voice Todo → İzinler → Mikrofon (Açık)

### ❔ **Sesli komutlar tanınmıyor**
✅ **Çözüm**: Net ve yavaş konuşun, gürültülü ortamdan kaçının

### ❔ **Dark mode çalışmıyor**
✅ **Çözüm**: Telefon ayarlarından sistem temasını değiştirin

### ❔ **Animasyonlar görünmüyor**
✅ **Çözüm**: Telefon ayarlarında animasyonların açık olduğunu kontrol edin

### ❔ **Todo'lar kayboluyor**
✅ **Çözüm**: Veriler cihazda saklanır, uygulama güncellemelerinde korunur

---

## 🏆 Kullanım Senaryoları

### 📅 Günlük Planlama
```
👤 "Bugün toplantıya hazırlanacağım"
👤 "Öğleden sonra spor yapacağım"  
👤 "Akşam alışverişe çıkacağım"
👤 "oku" → Tüm planı dinle
```

### ✅ Hızlı Yönetim
```
👤 "tamamla" → Son görevi bitir
👤 "sil" → Gereksiz görevi sil
👤 "temizle" → Tamamlananları temizle
```

### 📊 Takip ve Analiz
```
👤 "kaç tane todo var" → İlerleme kontrolü
👤 "oku" → Mevcut durumu gözden geçir
```

---

## 🔮 Gelecek Özellikler

### 🚧 Planlanır Özellikler
- 📅 **Tarih ve saat ekleme**: "Yarın 14:00'da toplantı"
- 🏷️ **Kategori sistemi**: "İş: Rapor hazırla"
- 🔔 **Hatırlatıcılar**: Zamanında bildirim
- 🔄 **Bulut senkronizasyon**: Cihazlar arası paylaşım
- 🎯 **Öncelik seviyeleri**: Önemli/Normal/Düşük
- 📱 **Widget desteği**: Ana ekran widget'ı

---

## 📞 Destek ve Geri Bildirim

### 🆘 Yardım Gerekiyor?
- **Uygulama içi**: Ayarlar → Yardım
- **GitHub**: Issues bölümünde sorun bildirin
- **E-posta**: destek@voicetodo.app

### 💡 Öneriniz Var mı?
- **Yeni özellik**: GitHub'da feature request oluşturun
- **Bug raporu**: Detaylı açıklama ile bildirin
- **İyileştirme**: Pull request gönderin

---

## 📜 Sürüm Geçmişi

### 🆕 v1.2.0 (Mevcut)
- ✨ Sesli komutlar sistemi
- 👆 Swipe actions
- 🌙 Dark mode desteği  
- ✨ Animasyonlar ve haptic feedback
- 🎨 Modern Material Design arayüz

### 📦 v1.1.0 
- 🎤 Offline sesli todo ekleme
- 💾 Room database entegrasyonu
- 📱 Temel Material Design arayüz

### 🎯 v1.0.0
- 📝 Temel todo yönetimi
- ✅ Checkbox işlemleri
- 🗑️ Manuel silme

---

**🎉 Voice Todo ile verimliliğinizi artırın!**

*Son güncelleme: 2025-01-06*