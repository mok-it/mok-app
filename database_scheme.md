# Adatbázisséma

Az alkalmazás Firebase Firestore-os adatbázist használ, ugyanazt, amit
a [Mancsok projekt](https://github.com/mok-it/mancsok/) is. és
mivel a Firestore egy nosql adatbázis, azaz nem rendelkezik sémával,
ezért a sémát itt írjuk le.

A documentId-kat nem írjuk ki külön.

A *szezonális* jelöléssel ellátott collectionökből minden szezonra külön collection létezik, hogy ne keveredjenek a tanévek adatai. A séma mindig csak a legújabb szezonra vonatkozik, de ha valamiért kellenének a régebbi szezonok adatai, akkor azok is megtalálhatóak a Firestore-ban. A szezonokat a `season` kulcs alapján lehet megkülönböztetni, ami Firebase Remote Configban van tárolva. Jelenleg a 2024-es szezon az aktuális.

## rewardrequests (*szezonális*)

Ebben a collectionben a jutalmakat kérő felhasználók kéréseit tároljuk.

- `user` (String): A jutalmat kérő felhasználó id-je.
- `reward` (String): A kért jutalom id-je.
- `price` (Int): A kért jutalom ára.
- `created` (Date): A kérés létrehozásának dátuma.

## users

Ebben a collectionben a felhasználók vannak tárolva. Regisztrációkor automatikusan létrejön mindenkinek egy dokumentum.

- `documentId` (String): A felhasználó azonosítója.
- `email` (String): A felhasználó email címe.
- `name` (String): A felhasználó neve.
- `photoURL` (String): A felhasználó fényképének URL-je.
- `phoneNumber` (String): A felhasználó telefonszáma.
- `requestedRewards` (List(String)): A felhasználó által kért jutalmak listája (id-k).
- `allBadges` (Int): A felhasználó által megszerzett összes mancs száma az aktuális szezonban.
- `remainingBadges` (Int): A felhasználó által megszerzett és még el nem költött mancs száma az aktuális szezonban.
- `fcmToken` (String): A felhasználó Firebase Cloud Messaging tokenje. Ezzel lehet értesítéseket küldeni neki az appba.
- `nickname` (String): A felhasználó beceneve.
- `projectBadges` (MutableMap(String, Int)): Azok a projektek, amikhez a user csatlakozott. A kulcs a projekt id-je, az érték az eddig megszerzett mancsok száma.
- `stamps` (MutableMap(String, Int)): Azok a pecsétek, amiket a user megszerzett. A kulcs a pecsét id-je, az érték pedig azt írja le, hogy hányszor szerezte meg.
- `role` (String): A felhasználó szerepe. Értékkészlete a [Role](#role) enum értékei.
- `notifications` (Collection([Notification](#notifications))): A felhasználónak küldött appon belüli értesítések.

## rewards

Ebben a collectionben a jutalmak vannak tárolva. Jelenleg nem szezonális, de akár az is lehetne, ha meg akarjuk őrizni a régebbi jutalmakat.

- `documentId` (String): A jutalom azonosítója.
- `name` (String): A jutalom neve.
- `price` (Int): A jutalom ára.
- `icon` (String): A jutalom ikonjának URL-je.
- `quantity` (Int): Hány darab van még a jutalomból raktáron.

## projects (*szezonális*)

Ebben a collectionben a projektek vannak tárolva.

- `category` (String): A projekt kategóriája. Értékkészlete a [Category](#category) enum értékei.
- `created` (Date): A projekt létrehozásának dátuma.
- `deadline` (Date): A projekt határideje.
- `description` (String): A projekt leírása.
- `icon` (String): A projekt ikonjának URL-je. A Cloud Storage-re mutat.
- `members` (List(String)): A projekt tagjainak listája (id-k).
- `name` (String): A projekt neve.
- `comments` (Collection([Comment](#comment))): A projekt kommentjeinek subcollectionje.
- `maxBadges` (Int): A projektben maximális megszerezhető mancsérték. Ezt speciális esetben túl lehet lépni, ha valaki nagyon sok effortot tett bele.
- `projectLeader` (String): A projektvezető id-je.
- `start` (Date): A projekt indulása.
- `end` (Date): A projekt vége.

Értékek, amik jelenleg nincsenek az appban használva, de a db-ben szerepelnek:
- `overall_progress` (Int): A projekt összesített előrehaladása 0-tól 100-ig. 
- `creator` (String): A projekt létrehozója. Háthajóleszvalamire-alapon tároljuk.

## stamps (*szezonális*)

Ebben a collectionben a pecsétek vannak tárolva.

- `name` (String): A pecsét neve.
- `multiplier` (number): A pecsét szorzó értéke (pl. 0 => nem ér mancsot, 1 => 5db ér egy mancsot, 2 => 2.5db ér egy mancsot).
- `maximum` (number): Egy user legfeljebb hányat szerezhet a mancsból.
- `responsible` (String): A felelős felhasználó id-ja. Ez a user is osztogathatja a pecsétet.
- `stampsPerBadge` (number): Megadja, hogy hány pecsét ér egy mancsot

## links

Ebben a collectionben fontos dokumentumokra mutató linkek vannak.

- `title` (String): A link címe.
- `url` (String): A link URL-je.
- `category` (String): A link kategóriája. Nincs enumként kezelve jelenleg.

## Receipts

Ebben a collectionben a területek pénzköltéseiből keletkező számlákat adminisztráljuk.

- `category` (String): A terület. Értékkészlete a [Category](#category) enum értékei.
- `text` (String): Lerírás, hogy mire költöttünk
- `date` (Date): A költés időpontja.
- `amount` (number): Az elköltött pénz forintban.
- `createdBy` (String): A létrehozó user id-ja.
- `isArrived` (Boolean): Beérkezett-e a számla.

## Comment

- `time` (Timestamp): A komment időbélyege.
- `userName` (String): A kommentelő felhasználó neve. Ez a mező az egyszerűség végett lett ide berakva. 
- `uid` (String): A kommentelő felhasználó azonosítója.
- `text` (String): A komment szövege.

## achievmentsTest

Ebben a collectionben ún. "acsik" vannak tárolva. A koncepció még kiforróban van, úgyhogy élesben nincs használva egyelőre

- `name` (String): Az acsi neve.
- `description` (String): Az acsi leírása.
- `icon` (String): Az acsi ikonjának URL-je.
- `mandatory` (Boolean): Az acsi kötelező-e.

## Notifications

- `title` (String): Az értesítés címe.
- `description` (String): Az értesítés leírása.
- `timestamp` (Date): A keletkezés időpontja.
- `isSeen` (Boolean): A felhasználó látta-e az értesítést.

# Enumok
A Firestore-ban nem lehet sajnos enumokat tárolni, viszont vannak use case-ek, ahol csak egy megadott halmaz elemeit szeretnénk megengedni, mint érték. Ezért az app az alábbi enumokat használja.

## Category

Ez az enum a lehetséges projektkategóriákat reprezentálja.

- `UNIVERZALIS`: Univerzális
- `SZERVEZETFEJLESZTES`: Szervezetfejlesztés
- `FELADATSOR`: Feladatsor
- `MEDIAESDIY`: Média és DIY
- `IT`: IT
- `PEDAGOGIA`: Pedagógia
- `NYARITABORIELOKESZITES`: Nyári tábor előkészítés
- `EVKOZITABORIELOKESZITES`: Évközi tábor előkészítés

## Role

Ez az enum a lehetséges felhasználói szerepeket reprezentálja.

- `BASIC_USER`: Alapfelhasználó, ide tartoznak a premökösök és a mökösök.
- `AREA_MANAGER`: Területvezető
- `ADMIN`: Adminisztrátor, ide tartoznak az elmökségi tagok és az alkalmazás fő fejlesztői.

Projektvezető rang direkt nincs, hiszen nincs értelme általánosságban arról beszélni, hogy valaki projektvezető-e; az számít (más rang híján), hogy az adott projektnek az adott user projektvezetője-e, amikor szeretne rajta módosítani.
