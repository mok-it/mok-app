/*eslint-disable no-eval*/
const FieldValue = require("@google-cloud/firestore");
const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

const db = admin.firestore();
/*
users:
Zalán: 1cKA2ZV58q8oQAylZbRx
Lali: SnjeZQwtONUqQfwZhZIi
Ágo: 7MOtg0sRC7YjxMb0fkDh
Olivér: MzxeaKkzfSpgdcDoMLv3

tasks:
üdv ITt: uyX7lBxpiXip9jyuDpDx
Rajzolj Szummát: D1kRQGfQSe8fQ7iD8cVh
Szultán: HXsf2j6FaSdwqR02FHFN
*/

// mivel nem tudtam, hogy kene egy ertelmes triggert megadni az adatbazis
// populalasahoz, ezert akkor fut le ez a function, ha a "test" nevu
// collection - ben egy dokumentum "a" nevu fieldjet modositjuk/letrehozzuk
exports.populate = functions.firestore
  .document("/test/{a}")
  .onWrite((change, context) => {
    const db = admin.firestore();
    // ADD USERS
    /*db.collection("users").add({
      name: "Karcsi",
    });*/
    /// ADD PROJECTS

    db.collection("projects").add({
      name: "IT-s projekt",
      description: "Ez az első IT-s projekt",
      deadline: new Date(2022, 12, 20),
      created: admin.firestore.Timestamp.now(),
      overall_progress: 0,
      icon: "https://firebasestorage.googleapis.com/v0/b/mokapp-51f86.appspot.com/o/auth_google_y.png?alt=media&token=779a4d40-18bd-470b-81fe-ac02a54bcd16",
      creator: "1cKA2ZV58q8oQAylZbRx",
      editors: ["SnjeZQwtONUqQfwZhZIi"],
      members: ["7MOtg0sRC7YjxMb0fkDh", "MzxeaKkzfSpgdcDoMLv3"],
      tasks: ["uyX7lBxpiXip9jyuDpDx", "D1kRQGfQSe8fQ7iD8cVh"],
    });
    db.collection("projects").add({
      name: "Kreatív projekt",
      description: "Készítsünk el egy adventi naptárat az egyesületi tagoknak",
      deadline: new Date(2021, 12, 20),
      created: admin.firestore.Timestamp.now(),
      overall_progress: 0,
      icon: "https://firebasestorage.googleapis.com/v0/b/mokapp-51f86.appspot.com/o/taxi_y.png?alt=media&token=c216126c-13ab-484f-9488-20dc9a2714df",
      creator: "SnjeZQwtONUqQfwZhZIi",
      editors: ["7MOtg0sRC7YjxMb0fkDh"],
      members: ["MzxeaKkzfSpgdcDoMLv3"],
      tasks: [
        "HXsf2j6FaSdwqR02FHFN",
        "uyX7lBxpiXip9jyuDpDx",
        "D1kRQGfQSe8fQ7iD8cVh",
      ],
    });
    db.collection("projects").add({
      name: "Kifogytam az ötletekből",
      description:
        "...pedig még kéne vagy 3 projekt...na mindegy, majd kitalálok valamit",
      deadline: new Date(2021, 12, 10),
      created: admin.firestore.Timestamp.now(),
      overall_progress: 0,
      icon: "https://firebasestorage.googleapis.com/v0/b/mokapp-51f86.appspot.com/o/star_y.png?alt=media&token=75b73088-c623-4dfa-95b3-5ca457425385",
      creator: "SnjeZQwtONUqQfwZhZIi",
      editors: ["7MOtg0sRC7YjxMb0fkDh"],
      members: ["MzxeaKkzfSpgdcDoMLv3"],
      tasks: [
        "HXsf2j6FaSdwqR02FHFN",
        "uyX7lBxpiXip9jyuDpDx",
        "D1kRQGfQSe8fQ7iD8cVh",
        "uyX7lBxpiXip9jyuDpDx",
        "hXsf2j6FaSdwqR02FHFN",
      ],
    });
    db.collection("projects").add({
      name: "Utolsó előtti projekt",
      description:
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas quis vulputate odio. Nunc mollis sem dui, ac viverra metus viverra ac. Integer justo sapien, pulvinar nec nulla iaculis, mollis mattis diam. Phasellus dictum orci nec ligula interdum blandit. Sed vitae consequat est. Morbi dapibus, enim in commodo pretium",
      deadline: new Date(2021, 12, 10),
      created: admin.firestore.Timestamp.now(),
      overall_progress: 0,
      icon: "https://firebasestorage.googleapis.com/v0/b/mokapp-51f86.appspot.com/o/star_y.png?alt=media&token=75b73088-c623-4dfa-95b3-5ca457425385",
      creator: "SnjeZQwtONUqQfwZhZIi",
      editors: ["7MOtg0sRC7YjxMb0fkDh"],
      members: ["MzxeaKkzfSpgdcDoMLv3"],
      tasks: [
        "HXsf2j6FaSdwqR02FHFN",
        "D1kRQGfQSe8fQ7iD8cVh",
        "HXsf2j6FaSdwqR02FHFN",
        "D1kRQGfQSe8fQ7iD8cVh",
      ],
    });
    db.collection("projects").add({
      name: "Lapátolj napfényt",
      description:
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas quis vulputate odio. Nunc mollis sem dui, ac viverra metus viverra ac. Integer justo sapien, pulvinar nec nulla iaculis, mollis mattis diam. Phasellus dictum orci nec ligula interdum blandit. Sed vitae consequat est. Morbi dapibus, enim in commodo pretium",
      deadline: new Date(2021, 12, 15),
      created: admin.firestore.Timestamp.now(),
      overall_progress: 0,
      icon: "https://firebasestorage.googleapis.com/v0/b/mokapp-51f86.appspot.com/o/passenger_y.png?alt=media&token=6cb28546-8433-448a-ab47-eb5c4e06660f",
      creator: "MzxeaKkzfSpgdcDoMLv3",
      editors: ["7MOtg0sRC7YjxMb0fkDh"],
      members: [],
      tasks: ["HXsf2j6FaSdwqR02FHFN", "D1kRQGfQSe8fQ7iD8cVh"],
    });

    /// ADD TASKS
    /*db.collection("tasks").add({
      title: "Üdv Itt",
      description:
        "Ha ezt látod, sikeresen beléptél a munkacsoportba, ezt a korongot kipipálhatod. Hurrá!",
      icon: "https://firebasestorage.googleapis.com/v0/b/mokapp-51f86.appspot.com/o/auth_google_y.png?alt=media&token=779a4d40-18bd-470b-81fe-ac02a54bcd16",
    });
    db.collection("tasks").add({
      title: "Rajzolj egy jelmezes Szummát",
      description:
        "Rajzolj a projekthez egy Szummát tetszőleges jelmezben. Minél kreatívabb, annál jobb!",
      icon: "https://firebasestorage.googleapis.com/v0/b/mokapp-51f86.appspot.com/o/driver_y.png?alt=media&token=76d657ce-cc3e-42e7-bc4f-dc274e1123e8",
    });
    db.collection("tasks").add({
      title: "Masszírozd meg Szultánt",
      description:
        "Szultánt masszírozd meg, és a projekt készítéséhez hozzáadd a Szultánt! (by Github Copilot)",
      icon: "https://firebasestorage.googleapis.com/v0/b/mokapp-51f86.appspot.com/o/pet_y.png?alt=media&token=6f996b4e-6ce6-4a3f-8589-d6b0e3fc2c8a",
    });*/
    return null;
  });

  // User létrehozásánál a userc collectionban létrehozzuk a neki megfelelő documentet a szükséges attribútumokkal
  exports.createUser = functions.auth.user().onCreate((user) => {
    console.log('user created', user.email, user.uid)
    db.collection("users").doc(user.uid).set({
      uid: user.uid,
      email: user.email,
      name: user.displayName,
      isCreator: false,
      isOwner: false,
      photoURL: user.photoURL,
      joinedBadges: [],
      collectedBadges: [],
      // ide minden más attribute jöhet majd
    })
    return null;
  });

  // Ha törlünk egy usert, a neki megfelelő dokumentum is törlődik a users collectionból
  // Kérdés, hogy élesben kell-e ez nekünk, mert így véletlen törlésnél elvesznek az adatok
  exports.deleteUser = functions.auth.user().onDelete((user) => {
    var user_to_delete = db.collection('users').where('uid','==',user.uid);
    user_to_delete.get().then(function(querySnapshot) {
      querySnapshot.forEach(function(doc) {
        doc.ref.delete();
        console.log('user deleted', user.email, user.uid)
      });
    });
    return null;
  }); 

  exports.userLoggedIn = functions.https.onRequest((req, res) => {
    const newDocumentBody = {
      picture: req.pictureURL
    }
    var user = db.collection('users').where('uid','==',req.uid);
    user.get().then( querySnapshot => {
      let batch = firebase.firestore().batch()
      querySnapshot.forEach( doc => {
        const docRef = firebase.firestore().collection('users').doc(doc.id)
        batch.update(docRef, newDocumentBody)
      });
      batch.commit();
    });
    return null;
  });


  exports.joinBadge = functions.https.onCall((data, context) => {
    const uid = data.uid;
    const badgeid = data.badgeid;
    console.log('user wants to join', uid, badgeid)
    db.collection('projects').doc(badgeid).update({
      members: FieldValue.arrayUnion(uid)
    })
    db.collection('users').doc(uid).update({
      joinedBadges: FieldValue.arrayUnion(badgeid)
    })
  })

