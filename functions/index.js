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

// User létrehozásánál a userc collectionban létrehozzuk a neki megfelelő documentet a szükséges attribútumokkal
// Ez csak akkor fut le, ha a user emailjet validalni tudjuk, hogy egy mokoshoz tartozik
exports.createUser = functions.auth.user().onCreate((user) => {
  // Check if the user's email matches your desired criteria.
  GetMokMember(user.email).then((result) => {
    //mokapp71 is a test account required for Google Play
    if (
      user.email == "mokapp71@gmail.com" ||
      (result && /MOK/.test(result.mok_status))
    ) {
      // Email is valid, create the user.
      db.collection("users")
        .doc(user.uid)
        .set({
          email: user.email,
          name: user.displayName,
          photoURL: user.photoURL,
          phoneNumber: result.phone ? result.phone : "",
          allBadges: 0,
          remainingBadges: 0,
          role: "BASIC_USER",
          projectBadges: {},
        });
      log("user created", user.email, user.uid);
    } else {
      // Email is not valid, delete the user.
      return admin.auth().deleteUser(user.uid);
    }
  });
});

async function GetMokMember(email) {
  try {
    const apiUrl = "https://nevezes.medvematek.hu/api/MOKAuthenticate/";
    const requestBody = { email: email };

    const response = await axios.post(apiUrl, requestBody);

    if (response.status === 200) {
      return response.data;
    } else {
      // Handle the case when the API request is not successful (e.g., it's not a 200 response).
      log("API request failed, response code is not 200:", response.statusText);
      return null;
    }
  } catch (error) {
    // Handle any other errors that may occur during the request.
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
