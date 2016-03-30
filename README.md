# telekonsultacje-2
Prosty system konsultacyjny (grupa #2)

Autorzy: 
Bartosz Sławianowski 117205
Bartosz Wysocki 117239

1. Opis projektu
 - tworzymy program umożliwiający wielu ekspertom zdalną komunikację i manipulację zdjęć
 - przykład zastosowania: zdalna analiza graficznych wyników badań pacjenta przez grupę lekarzy, którzy nie mogą spotkać się w jednym miejscu
2. Zakładana funkcjonalność
 - grupowy czat oraz przeglądanie zdjęć
 - komunikacja tekstowa
 - prosta manipulacja zdjęciami (zaznaczanie, obracanie, zmiana rozmiaru)
 - system kont użytkowników
 - archiwum sesji – możliwość pobrania i odtworzenia przebiegu dowolnej sesji, w której braliśmy udział
3. Możliwe rozszerzenia
 - możliwość komunikacji głosowej (VoIP)
 - integracja z serwisami posiadającymi bazy danych chorób itp. w celu szybkiego dostępu do informacji
4. Planowana architektura systemu
 - Serwer systemu - uwierzytelnianie i autoryzacja użytkowników, obsuga sesji(wielu na raz), przechowywanie archiwum
   i obsługa dostępu do niego ...
 - Klient #1, Klient #2 ... Klient #N
 - Baza Danych
5. Wykorzystywane technologie
 - przez wszystkich znana i lubiana Java!
 - do stworzenia interfejsu graficznego klienta - wbudowana biblioteka JavaFX
 - framework do aplikacji sieciowych - Netty
 - format serializacji danych przesyłanych pomiędzy serwerem, a klientem - Google Protocol Buffers
6. Wykorzystywane narzędzia
 - IDE - IntelliJ IDEA
 - praca grupowa - Git
 - systemy operacyjne - Windows, MacOS X, Linux (serwer)
7. Harmonogram prac
 - 21.03 - opracowanie wstępnej wersji protokołu (w formacie Protocol Buffers)
 - 04.04 - prosty czat grupowy
 - 11.04 - system kont użytkowników
 - 09.05 - wrzucanie obrazków i manipulacja nimi
 - 30.05 - zapisywanie sesji do archiwum i ich odtwarzanie
8. Podział prac
 - protokół - wspólnie
 - klient - Bartosz Wysocki
 - serwer - Bartosz Sławianowski
9. Przewidywane trudności i problemy
 - wdrożenie dodatkowej funkcjonalności (jeśli wystarczy czasu)
   - komunikacja głosowa
    - zapoznanie się z kodekami dźwięku
    - znaczna rozbudowa serwera - strumieniowanie dźwięku poprzez TCP nie jest dobrym pomysłem




