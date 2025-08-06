# Voice Todo - Sesli Todo Uygulaması

Bu uygulama, konuşmanızı yazıya çevirerek otomatik olarak todo listesine ekleyen bir Android uygulamasıdır.

## Özellikler

- 🎤 **Sesli Todo Ekleme**: Mikrofon butonuna basıp konuşarak todo ekleyin
- 📝 **Todo Yönetimi**: Todo'ları tamamlandı olarak işaretleyin veya silin
- 💾 **Yerel Depolama**: Veriler Room database ile cihazda güvenle saklanır
- 🕐 **Zaman Damgası**: Her todo'nun eklenme saati görüntülenir

## Kullanım

1. **Mikrofon İzni**: İlk açılışta mikrofon iznini verin
2. **Ses Kaydı**: Mavi mikrofon butonuna basın
3. **Konuşma**: "Alışveriş yap" veya "Toplantıya katıl" gibi konuşun
4. **Otomatik Ekleme**: Konuştukların otomatik olarak todo listesine eklenir
5. **Yönetim**: Checkbox ile tamamlandı işaretleyin, çöp kutusu ile silin

## Teknik Detaylar

- **Platform**: Android (API 21+)
- **Dil**: Java
- **Veritabanı**: Room Database
- **UI**: Material Design Components
- **Ses Tanıma**: Android SpeechRecognizer API

## Android Studio'da Çalıştırma

1. Bu projeyi Android Studio'da açın
2. Gradle sync işlemini bekleyin
3. Bir Android emulator çalıştırın veya gerçek cihaz bağlayın
4. "Run" butonuna basın

## Gereksinimler

- Android Studio 2022.1.1+
- Android SDK 34
- Minimum Android sürümü: 5.0 (API 21)
- Mikrofon erişimi olan cihaz

## Gelecek Geliştirmeler

- iOS sürümü (Mac/iPhone)
- Bulut senkronizasyonu
- Kategori bazlı todo grupları
- Sesli hatırlatıcılar

---

**Not**: Bu uygulama tamamen offline çalışır ve verileriniz sadece cihazınızda saklanır.