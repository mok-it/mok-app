# MÖK App
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=mok-it_mok-app&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=mok-it_mok-app)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=mok-it_mok-app&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=mok-it_mok-app)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=mok-it_mok-app&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=mok-it_mok-app)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=mok-it_mok-app&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=mok-it_mok-app)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=mok-it_mok-app&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=mok-it_mok-app)

## Disztributálás

Jelenleg az app egyetlen terjesztési platformja a Google Play, ahol **Éles verzióként** tesszük közzé mindig a legfrissebb verziót. A csatornához _terv szerint_ két Google-csoport tagjai férhetnek hozzá; a mökösök és a premökösök. Jelenleg a bejelentkezés és regisztráció le van tiltva, mivel nincs még validálva, hogy milyen Google-fiókokkal lehet belépni.

## Új verzió kiadása

Az AppCircle jelenleg úgy van bekonfigurálva, hogy minden push (vagy PR) a master branchre automatikusan lebuildeli az appot és feltölti Google Play Console-ba. Fontos tudnivaló, hogy Gitflow-t használunk és tipikusan GitKraken klienst, a leírás most következő része is ennek figyelembevételével készült. Ha új kiadást szeretnél csinálni az appból, ehhez a következő lépéseket kell követni:

*Fontos megjegyzés: a folyamat ezen része mindig egy kicsit káoszos és ráadásul pont most változott, szóval nincs rá garancia, hogy az alábbiak pontosan működnek.*

0) Kitalálod, mi lesz az új verzió száma a [semantic versioning](https://semver.org/) szabályai alapján (röviden: minor.mayor.patch, pl. 3.0.1).
1) Elkészíted a kiadni kívánt verziót a **develop** branchre, megbizonyosodsz róla, hogy rendesen működik, majd készítesz egy új release-t a Gitflow szabályai alapján. Branchnévnek az új verziószámot add meg (pl. 3.0.1). 
   
   GitKrakenben:  *Gitflow/Start release*
2) Átírod (az újonnan készült **release** branchen állva) az appszintű build.gradle-ben a *versionName* változót az új verzióra (ha ez kimarad, ugyanúgy felmegy majd a frissítés, csak nehezebben lesz követhető, hogy kinek milyen verzió van telepítve). A *versionCode*-ot nem kell átírni, az automatikusan nő minden buildnél.
3) Commitolod a változtatásokat, majd befejezed a release-t. Tag message-nek add meg ugyanúgy az új verziószámot, majd pusholj. 

    GitKrakenben: *Gitflow/Finish release*, a "Delete branch" maradjon bepipálva
4) Az AppCircle automatikusan lebuildeli az appot és feltölti a Google Play Console-ba. Ez akár 15-20 perc is lehet a sikeres buildtől számítva, légy türelmes. Ha nem sikerült buildelnie, akkor nem jó az app jelenlegi állapota. Kezdd újra az egészet.
5) Innentől már csak meg kell várni, hogy a Google ellenőrizze az új verziót, ez általában max fél óra, de akár 1-2 nap is lehet. Ha ez lefutott, akkor az alkalmazás automatikusan elérhetővé válik és mindenki tud frissíteni rá, aki az adott csatornát használja.

### További potenciális terjesztési módszerek

A Firebase elvileg csak azokkal az alkalmazásokkal működik, amiknek megadtuk előzetesen az SHA fingerprintjét. A Google Playre feltöltött appoknak ez automatikusan generálódik, szóval egyszer kellett csak beírni a Firebase-be ezt, azóta működik a Playre feltöltött verziókkal. Viszont ha nem (csak) Playen terjesztjük az új verziót, akkor sanszos, hogy nem fog működni, hacsak be nem írjuk Firebase-be az új SHA fingerprintet. Ezért szerintem kicsit körülményes a lenti módszereket használni, plusz ha elfelejtődik a fingerprint beírása, akkor csodálkozni fogunk, hogy miért nem megy az app. Ha esetleg valakinek mégis szüksége lenne rá a továbbiakban, akkor hajrá.

Az [AppCircle](https://my.appcircle.io/dashboard) nevű CI/CD tool, amit használunk, lehetőséget nyújt két további terjesztési módszerre is; egyrészt el lehet küldeni automatikusan emailben előre meghatározott személyeknek a frissítést, másrészt létezik egy ún. [Enterprise App Store-ja](https://e49pp5xye21y.store.appcircle.io/) is, ahova fel lehet tölteni egy kattintással az appot. Ez esetleg azoknak lehet hasznos, akik emulátorról használják az appot.

Ezen kívül a Firebase-nak is van egy *Firebase app distribution* nevű szolgáltatása, bár ez kifejezetten a tesztelőkhöz való eljuttatásra való szerintük.


## Kérdések

Ha valami nem pont úgy működik, ahogy az a fentiekben írva van, szerkeszd nyugodtan a doksit.

Ha bármilyen kérés, kérdés felmerül benned, akkor keresd bármikor és bárhol engem (Olivér) vagy Zalánt. Ha hibát találtál, nyiss új issue-t, amit a megfelelő labellel ellátsz. :)
