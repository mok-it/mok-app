/*eslint-disable no-eval*/
const functions = require("firebase-functions");
const admin = require("firebase-admin");
const { log } = require("firebase-functions/logger");
const axios = require("axios");
admin.initializeApp();

const db = admin.firestore();

exports.sendNotification = functions.https.onCall(async (data, context) => {
  const message = data.message;
  const fcmToken = data.fcmToken;

  const payload = {
    notification: {
      title: message.title,
      body: message.body,
      icon: message.icon,
      click_action: message.click_action,
    },
  };

  await admin.messaging().sendToDevice(fcmToken, payload);
});

// User létrehozásánál a users collectionban létrehozzuk a neki megfelelő documentet a szükséges attribútumokkal
// Ez csak akkor fut le, ha a user emailjet validalni tudjuk, hogy egy mokoshoz tartozik
exports.createUser = functions.auth.user().onCreate((user) => {
  // Check if the user's email matches your desired criteria.
  GetMokMember(user.email).then((result) => {
    //mokapp71 is a test account required for Google Play
    if (
      user.email == "mokapp71@gmail.com" || result != null
    ) {
      // Email is valid, create the user.
      db.collection("users")
        .doc(user.uid)
        .set({
          email: user.email,
          name: user.displayName,
          mokStatus: result.mokStatus,
          photoURL: user.photoURL,
          phoneNumber: result.phone ? result.phone : "",
          allBadges: 0,
          remainingBadges: 0,
          role: "BASIC_USER",
          projectBadges: {},
        }).then(async () => {
          // Delete the user from the 'users_pending' collection
          const snapshot = await db.collection('users_pending').where('email', '==', user.email).get();
          if (!snapshot.empty) {
            const doc = snapshot.docs[0];
            await doc.ref.delete();
          }
          return(log("User created and Pending deleted:", user.email, user.uid));
        });
    } else {
      // Email is not valid, delete the user.
      admin.auth().deleteUser(user.uid);
      log("AUTH DELETED");
      return;
    }
  });
});

async function GetMokMember(email) {
  try {
    // Search for the email in the 'users' collection
    const usersSnapshot = await db.collection('users').where('email', '==', email).get();
    if (!usersSnapshot.empty) {
      const userDoc = usersSnapshot.docs[0];
      const userData = userDoc.data();
      return userData;
    }

    // If not found, search for the email in the 'users_pending' collection
    const pendingSnapshot = await db.collection('users_pending').where('email', '==', email).get();
    if (!pendingSnapshot.empty) {
      const pendingDoc = pendingSnapshot.docs[0];
      const pendingData = pendingDoc.data();
      if (pendingData.status === "allowed") {
        return {
          email: pendingData.email,
          mokStatus: pendingData.mokStatus
        };
      } else if (pendingData.status === "pending") {
        log("Login request is still pending:", email);
        return null;
      } else if (pendingData.status === "denied") {
        log("Login request is denied:", email);
        return null;
      }
    }

    // If not found in both collections, create a new document in 'users_pending' collection
    const newPendingUser = {
      email: email,
      status: "pending",
      mokStatus: "Inaktív",
      type: "login"
    };
    await db.collection('users_pending').doc(email).set(newPendingUser);
    log("Login request created:", email);
    return null;

  } catch (error) {
    // Handle any errors that may occur during the request.
    log("An error occurred:", error.message);
    return null;
  }
}

exports.updateOnBadgeCollectionChange = functions.firestore
  .document("users/{userId}")
  .onUpdate(async (change, context) => {
    const userId = context.params.userId;

    // Get the old and new user document
    const oldUser = change.before.data();
    const newUser = change.after.data();

    // Only update fields if the user got/lost some badges or requested a reward
    if (
      oldUser.projectBadges === newUser.projectBadges &&
      oldUser.requestedRewards === newUser.requestedRewards
    ) {
      return null;
    }

    const allBadges = Object.values(newUser.projectBadges).reduce(
      (acc, val) => acc + val,
      0
    );

    functions.logger.log("all badges: " + allBadges);

    // Subtract the sum of requested rewards from the user's badges

    let remainingBadges = allBadges;
    const rewardIds = newUser.requestedRewards;
    if (rewardIds != undefined) {
      for (const rewardId of rewardIds) {
        const rewardDoc = await admin
          .firestore()
          .collection("rewards")
          .doc(rewardId)
          .get();

        remainingBadges -= rewardDoc.data().price;
      }
      functions.logger.log(
        "price of all requested stuff: " + remainingBadges + " badges"
      );
    }

    // Update the user's fields
    await admin
      .firestore()
      .collection("users")
      .doc(userId)
      .update({ remainingBadges: remainingBadges, allBadges: allBadges });
  });
