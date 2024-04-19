# MÖK App

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=mok-it_mok-app&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=mok-it_mok-app)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=mok-it_mok-app&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=mok-it_mok-app)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=mok-it_mok-app&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=mok-it_mok-app)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=mok-it_mok-app&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=mok-it_mok-app)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=mok-it_mok-app&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=mok-it_mok-app)

## Mental Model

- Mancs = badge. Mancsnak mondjuk a Mökön belül, de a kódban badge-et használunk (mert a hunglish
  kód csúnya).
- A mancs csak egy szám lényegében.
- A projektekre jelentkeznek az emberek. Minden projekt rendelkezik egy N maximum mancsértékkel.
  Minden projekt teljesítésekor a résztvevők [0-N] mancsot kapnak. Az app kezdőoldalán látszó dolgok
  is projektek. Különleges esetben lehet N-nél is többet kapni.
- (Minden userhez a projektjeit tároljuk, azon belül azt, hogy azokért hány mancsot kapott. )
- A mancsokból ajándékokat lehet venni.
- Van mindenkinek egy mancsegyenlege, amit az adott szezonban megszerzett, és egy olyan, amit még
  nem használt fel. Ez utóbbira lehet elköltetlen/felhasználatlan mancsok számaként hivatkozni.
- A mancsok resetelődnek évente, amit nem költ valaki el az egyikben, az attól még nem lesz
  elkölthető a következőben.
- Vannak kritikus/zöld és nem kritikus/kék projektek. A kritikus projektekre zöld mancs jár, a nem
  kritikusakra pedig kék.
- Külön követelmény van arra, hogy egy időszakban mennyi zöld mancsot kell szerezni, és arra, hogy
  mennyi mancsot kell szerezni összesen (zöld+kék). Mindkét fajta mancs ugyanúgy elkölthető
  jutalmakra, ilyen téren nincs köztük különbség.

### Jogosultságkezelés

Jelenleg az alábbi jogosultságok léteznek az appban:

- **Admin**: Az elmökség és néhány fő appfejlesztő jogosultsága ez. Mindent csinálhat, bármit
  szerkeszthet, még az ajándékokat is. Küldhet értesítéseket a tagoknak
- **Területvezető**: A terület összes projektjét szerkesztheti, hozhat létre mancsokat
- **Projektvezető**: A saját projektjeit szerkesztheti
- **Tag**: Ez az alap jogosultsága mindenkinek. A premökösöket és a mökösöket nem különböztetjük
  meg, mindkettő csoport tagjainak ezt jelenítjük meg

## Disztributálás

Jelenleg az app egyetlen terjesztési platformja a Google Play, ahol **Éles verzióként** tesszük
közzé mindig a legfrissebb verziót. Ez azt jelenti, hogy bárki letöltheti az appot, aki rákeres
Google Playen, bejelentkezni viszont már csak (pre)Mökösök tudnak

## Új verzió kiadása

Az AppCircle jelenleg úgy van bekonfigurálva, hogy minden push (vagy PR) a main branchre
automatikusan lebuildeli az appot és feltölti Google Play Console-ba. Fontos tudnivaló, hogy
Gitflow-t használunk (nagyjából) és tipikusan GitKraken klienst, a leírás most következő része is
ennek
figyelembevételével készült. Ha új kiadást szeretnél csinálni az appból, ehhez a következő lépéseket
kell követni:

1) Kicheckoutolod a **develop** branchet, meggyőződsz róla, hogy be van ide mergelve minden feature,
   amit ki szeretnél adni, valamint hogy ez az állapot le is fordul.
2) Kitalálod, mi lesz az új verzió száma a [semantic versioning](https://semver.org/) szabályai
   alapján (röviden: major.minor.patch, pl. 3.0.1), majd átírod az appszintű build.gradle-ben a
   *versionName* változót az új verzióra.

   Ha ez a lépés kimarad, ugyanúgy felmegy majd a frissítés, csak
   nehezebben lesz követhető, hogy kinek milyen verzió van telepítve.
3) Commitolod a változtatásokat, mergeled a **developot** a **mainbe**, majd pusholsz.

Innentől az AppCircle automatikusan lebuildeli az appot és feltölti a Google Play Console-ba. Ez
akár
15-20 perc is lehet a sikeres buildtől számítva, légy türelmes. Ha nem sikerült buildelnie, akkor
nem jó az app jelenlegi állapota. Kezdd újra az egészet. Ha jó, akkor már csak meg kell várni, hogy
a Google ellenőrizze az új verziót, ez általában max fél
óra, de akár 1-2 nap is lehet. Ha ez lefutott, akkor az alkalmazás automatikusan elérhetővé válik,
és mindenkinél meg fog jelenni a frissítés Google Playen.

## Issue-k létrehozása

Ha ötleted van, vagy hibát találtál, akkor nyugodtan nyiss egy Githubos issue-t. Az issue-knál a
következő paramétereket add meg feltétlenül:

- Leírás: Minél részletesebben írd le, milyen hibát találtál/milyen feature-t javasolsz. Ha kell,
  csinálj hozzá képernyőképet, vagy írj task listet, hogy milyen lépésekből áll a feladat várhatóan.
- Label(s): Minden issue-hoz adj meg legalább egy labelt, ami jelzi, hogy az issue milyen típusú,
  pl. bug, design, refactor, stb. Ha több is ráillik, akkor mindet add meg.
- Milestone: Az aktív Milestone-ok közül bármelyikhez adhatod, attól függően, hogy mennyire nagy
  feladat/sürgős a dolog.

Ha te szeretnéd majd megcsinálni az issue-t, akkor nyomj rá az "assign yourself" gombra is.

## Az app fejlesztése

Általános fejlesztési guideline-okért lásd a [Mindenttudó doksit](https://bit.ly/itmindenttudo),
azon belül is a szakmai mindenttudót. A fejlesztés ezen a projekten a Gitflow egyszerűsített
verziója szerint dolgozunk, ami így néz ki:

- **main**: Ez a stabil verzió, amit a felhasználók használnak. Ebből a branchből készülnek a
  kiadások (automatikusan).
- **develop**: Ezen a branchen dolgozunk, és ide mergeljük be a feature brancheket. Ha valami kész
  van, akkor innen megy a mainbe.
- **feature/...**: Minden új feature-höz készíts egy új branchet a developból, és amikor kész van,
  akkor csinálj egy PR-t a developba. A branch nevére példa: `feature/#103_add_loading_animations`,
  ahol a
  #103 a hozzá tartozó issue száma.

## Kérdések

Ha valami nem pont úgy működik, ahogy az a fentiekben írva van, szerkeszd nyugodtan a doksit.

Ha bármilyen kérés, kérdés felmerül benned, akkor keresd bármikor és bárhol engem (Olivér) vagy
Zalánt.
