# 🎤 Voice Todo - Akıllı Sesli Todo Uygulaması

**Sesli komutlar ve modern arayüz ile todo yönetiminde yeni nesil deneyim!**

Voice Todo, sesli komutlar, swipe actions ve akıllı arayüz özellikleri ile todo yönetimini tamamen yeniden tanımlayan modern bir Android uygulamasıdır.

![Voice Todo](https://img.shields.io/badge/Android-21%2B-green) ![Material Design](https://img.shields.io/badge/UI-Material%20Design-blue) ![Offline](https://img.shields.io/badge/Mode-Offline-orange)

## ✨ Yeni Özellikler (v1.2.0)

### 🗣️ **Akıllı Sesli Komutlar**
- **"tamamla"** → Son todo'yu tamamla
- **"sil"** → Son todo'yu sil  
- **"temizle"** → Tamamlanan todo'ları temizle
- **"kaç tane todo var"** → İstatistikleri söyle
- **"oku"** → Todo'ları sesli listele

### 👆 **Swipe Actions (Kaydırma İşlemleri)**
- **Sağa kaydır** → Tamamla/Tamamlanmadı işaretle (Yeşil)
- **Sola kaydır** → Sil (Kırmızı)
- **Görsel geri bildirim** → Renkli arkaplan + ikonlar

### 🌙 **Dark/Light Mode**
- **Otomatik tema değişimi** → Sistem ayarlarına göre
- **Batarya dostu** → Koyu tema ile tasarruf
- **Material Design 3** → Modern görünüm

### ✨ **Animasyonlar & Efektler**
- **Pulse animasyon** → Mikrofon dinlerken
- **Slide-in/out** → Todo ekleme/silme
- **Haptic feedback** → Dokunsal geri bildirim
- **Smooth transitions** → Akıcı geçişler

## 🎯 Temel Özellikler

- 🎤 **Offline Sesli Todo Ekleme** → İnternet gerektirmez
- 📝 **Akıllı Todo Yönetimi** → Checkbox, swipe ve sesli kontrol
- 💾 **Güvenli Yerel Depolama** → Room database ile
- 🕐 **Zaman Damgası** → Her todo'nun ekleme saati
- 📱 **Modern Material UI** → Kullanıcı dostu arayüz
- 🔄 **Crash Koruması** → Güvenilir çalışma

## 🚀 Hızlı Başlangıç

### 📱 Temel Kullanım
1. **Mikrofon İzni** → İlk açılışta izin verin
2. **🎤 Ses Butonu** → Mikrofon butonuna basın
3. **🗣️ Konuşun** → "Bugün alışverişe gideceğim"
4. **✅ Otomatik Ekleme** → Todo listeye eklenir
5. **👆 Swipe/Tap** → Yönetim işlemleri

### 🎤 Sesli Komut Örnekleri
```bash
👤 "Yarın toplantıya katılacağım"     → ✅ Todo eklendi
👤 "tamamla"                          → ✅ Son todo tamamlandı  
👤 "Alışverişe çıkacağım"             → ✅ Yeni todo eklendi
👤 "sil"                              → ✅ Son todo silindi
👤 "kaç tane todo var"                → 📊 "Toplam 1 todo var..."
```

## 📋 Detaylı Kullanım Kılavuzu

Tüm özellikler ve kullanım detayları için: **[📖 KULLANIM_KILAVUZU.md](./KULLANIM_KILAVUZU.md)**

## 🛠️ Teknik Detaylar

### 📊 Proje Bilgileri
- **Platform**: Android (API 21+)
- **Dil**: Java
- **Veritabanı**: Room Database
- **UI Framework**: Material Design Components
- **Ses Tanıma**: Android SpeechRecognizer API (Offline)
- **Build System**: Gradle (AGP 8.11.0)

### 🏗️ Mimari
```
📱 UI Layer (Activity/Adapter)
    ↓
🎤 Voice Recognition (SpeechRecognizer)
    ↓  
🧠 Command Processing (MainActivity)
    ↓
💾 Data Layer (Room Database)
```

### 📦 Dependencies
```gradle
- androidx.appcompat:appcompat:1.6.1
- com.google.android.material:material:1.11.0
- androidx.room:room-runtime:2.5.0
- androidx.recyclerview:recyclerview:1.3.1
```

## 🚀 Android Studio'da Çalıştırma

### 📋 Ön Gereksinimler
- **Android Studio** 2022.1.1+
- **Android SDK** 34
- **Gradle** 8.13+
- **Java** 8+

### ⚡ Kurulum Adımları
```bash
1. git clone [repo-url]
2. Android Studio'da projeyi aç
3. Gradle sync işlemini bekle
4. Emulator veya gerçek cihaz bağla
5. Run butonuna bas → 🚀
```

### 🔧 Build Komutları
```bash
# Clean build
./gradlew clean
./gradlew build

# Debug APK
./gradlew assembleDebug

# Release APK  
./gradlew assembleRelease
```

## 📱 Sistem Gereksinimleri

### ✅ Minimum Gereksinimler
- **Android 5.0** (API 21) ve üzeri
- **RAM**: 2GB+
- **Depolama**: 50MB
- **Mikrofon** erişimi
- **Ses tanıma** desteği

### 🎯 Önerilen Gereksinimler
- **Android 10.0** (API 29) ve üzeri
- **RAM**: 4GB+
- **Dark mode** desteği
- **Haptic feedback** desteği

## 🔮 Roadmap & Gelecek Özellikler

### 🚧 v1.3.0 (Orta Vadeli)
- 📅 **Tarih ve saat parsing** → "Yarın 14:00'da toplantı"
- 🏷️ **Kategori sistemi** → "İş: Rapor hazırla"
- 🔔 **Smart notifications** → Zamanında hatırlatma
- 📊 **Analytics dashboard** → İlerleme takibi

### 🎯 v1.4.0 (Uzun Vadeli)
- ☁️ **Cloud sync** → Cihazlar arası senkronizasyon
- 🤖 **AI suggestions** → Akıllı öneriler
- 📱 **Widget support** → Ana ekran widget'ı
- 🌍 **Multi-language** → Çoklu dil desteği

### 💭 v2.0.0 (Vizyon)
- 🏠 **Smart home integration** → Google Assistant
- ⌚ **Wearable support** → Smartwatch uyumlu
- 👥 **Collaboration** → Paylaşımlı listeler
- 🎯 **Habit tracking** → Alışkanlık takibi

## 🤝 Katkıda Bulunma

### 🛠️ Development
```bash
1. Fork the repository
2. Create feature branch: git checkout -b feature-name
3. Commit changes: git commit -m "Add feature"
4. Push branch: git push origin feature-name
5. Create Pull Request
```

### 🐛 Bug Report
- **GitHub Issues** kullanın
- **Detaylı açıklama** ekleyin
- **Log çıktıları** paylaşın
- **Ekran görüntüleri** ekleyin

### 💡 Feature Request
- **Clear description** → Net açıklama
- **Use cases** → Kullanım senaryoları
- **Mockups** → Tasarım örnekleri

## 📞 Destek & İletişim

### 🆘 Teknik Destek
- **GitHub Issues**: Bug report ve feature request
- **Discussions**: Genel sorular ve öneriler
- **Email**: destek@voicetodo.app

### 📱 Sosyal Medya
- **Twitter**: @VoiceTodoApp
- **LinkedIn**: Voice Todo Official

## 📜 Lisans

Bu proje **MIT Lisansı** altında lisanslanmıştır. Detaylar için [LICENSE](./LICENSE) dosyasına bakın.

## 🏆 Özel Teşekkürler

- **Android Team** → SpeechRecognizer API
- **Material Design** → UI/UX Guidelines  
- **Room Database** → Güvenilir veri saklama
- **Community** → Geri bildirimler ve öneriler

---

## 📊 Proje İstatistikleri

![GitHub stars](https://img.shields.io/github/stars/username/voicetodo)
![GitHub forks](https://img.shields.io/github/forks/username/voicetodo)
![GitHub issues](https://img.shields.io/github/issues/username/voicetodo)
![GitHub license](https://img.shields.io/github/license/username/voicetodo)

**🎉 Voice Todo ile verimliliğinizi artırın!**

*Son güncelleme: 2025-01-06*