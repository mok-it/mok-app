/*eslint-disable no-eval*/
const FieldValue = require("@google-cloud/firestore");
const functions = require("firebase-functions");
const admin = require("firebase-admin");
const { _refWithOptions } = require("firebase-functions/v1/database");
const { log } = require("firebase-functions/logger");
admin.initializeApp();

const db = admin.firestore();

// User létrehozásánál a userc collectionban létrehozzuk a neki megfelelő documentet a szükséges attribútumokkal
exports.createUser = functions.auth.user().onCreate((user) => {
  log("user created", user.email, user.uid);
  db.collection("users")
    .doc(user.uid)
    .set({
      email: user.email,
      name: user.displayName,
      isCreator: false,
      admin: false,
      photoURL: user.photoURL,
      joinedBadges: [],
      collectedBadges: [],
      categories: ["Univerzális"],
      phoneNumber: user.phoneNumber ? user.phoneNumber : "",
    });
  return null;
});

// Ha törlünk egy usert, a neki megfelelő dokumentum is törlődik a users collectionból
// Kérdés, hogy élesben kell-e ez nekünk, mert így véletlen törlésnél elvesznek az adatok
exports.deleteUser = functions.auth.user().onDelete((user) => {
  const user_to_delete = db.collection("users").where("id", "==", user.uid);
  user_to_delete.get().then(function (querySnapshot) {
    querySnapshot.forEach(function (doc) {
      doc.ref.delete();
      log("user deleted", user.email, user.uid);
    });
  });
  return null;
});

exports.uploadNewBadge = functions.https.onRequest((req, res) => {
  //adatok:
  //(ld. lent) minden mező, ami benne van a projects collection documentumaiban
  // + owner
  //ebből kötelező: name, description, deadline, creator
  //a többi opcionális
  const defaultIcon =
    "https://firebasestorage.googleapis.com/v0/b/mokapp-51f86.appspot.com/o/under_construction_badge.png?alt=media&token=3341868d-5aa8-4f1b-a8b6-f36f24317fef";
  db.collection("projects").add({
    name: req.body.name,
    description: req.body.description,
    deadline: new Date(
      req.body.deadline.year,
      req.body.deadline.month,
      req.body.deadline.day
    ),
    created: admin.firestore.Timestamp.now(),
    overall_progress: 0,
    icon: req.body.hasOwnProperty("icon") ? req.body.icon : defaultIcon,
    creator: req.body.creator,
    owner: req.body.hasOwnProperty("owner") ? req.body.owner : req.body.creator,
    editors: req.body.editors ? req.body.editors : [],
    members: req.body.members ? req.body.members : [],
    tasks: req.body.tasks ? req.body.tasks : [],
  });
  res.status(200).send('{"success":true}');
});

exports.updatePointsOnBadgeCollectionChange = functions.firestore
  .document("users/{userId}")
  .onUpdate(async (change, context) => {
    const userId = context.params.userId;

    // Get the old and new user document
    const oldUser = change.before.data();
    const newUser = change.after.data();

    // Only update points if the collectedBadges field has changed
    if (oldUser.collectedBadges === newUser.collectedBadges) {
      return null;
    }

    // Calculate the sum of all badge values
    const badgeIds = newUser.collectedBadges;
    let points = 0;
    for (const badgeId of badgeIds) {
      const projectDoc = await admin
        .firestore()
        .collection("projects")
        .doc(badgeId)
        .get();
      const projectValue = projectDoc.data().value;
      points += projectValue;
    }

    // Subtract the sum of requested rewards from the user's points

    const rewardIds = newUser.requestedRewards;
    for (const rewardId of rewardIds) {
      const rewardDoc = await admin
        .firestore()
        .collection("rewards")
        .doc(rewardId)
        .get();
      const rewardValue = rewardDoc.data().price;
      points -= rewardValue;
    }

    // Update the user's points field
    await admin.firestore().collection("users").doc(userId).update({ points });
  });
