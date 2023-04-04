package mok.it.app.mokapp.utility

import android.app.Activity
import android.content.ContentValues
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import com.beust.klaxon.Klaxon
import com.google.firebase.firestore.FirebaseFirestore
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.utility.Utility.unaccent


private var personList = listOf<Person>()

private class Person(val name: String, val phoneNumber: String, val email: String)

private fun findPhoneNumberInJson(
    vezeteknev: String,
    keresztnev: String,
    email: String = "",
    masodikkeresztnev: String = "",
): String {
    var found = false
    var phoneNumber = ""
    if (email != "") {
        personList.forEach { person ->
            if (person.email == email) {
                found = true
                phoneNumber = person.phoneNumber
            }
        }
    }
    if (!found)
        personList.forEach { person ->
            val name = person.name.unaccent()
            if (name.contains(vezeteknev)
                && (name.contains(keresztnev)
                        || (masodikkeresztnev != "" && name.contains(masodikkeresztnev)))
            )
                if (found) {
                    Log.d(
                        "UpdatePhoneNumbers",
                        "Too many results, there are (at least partly) matching names: $vezeteknev $keresztnev $masodikkeresztnev"
                    )
                    return ""
                } else {
                    found = true
                    phoneNumber = person.phoneNumber
                }
        }
    if (!found)
        Log.d(
            ContentValues.TAG,
            "findPhoneNumber: no phone number found for $vezeteknev $keresztnev"
        )
    return phoneNumber
}

fun updatePhoneNumbers(activity: Activity) {
    //open "phonenumbers.json" file from the phonenumbers folder
    personList = Klaxon().parseArray(activity.assets.open("phonenumbers.json"))!!

    val db = FirebaseFirestore.getInstance()
    val usersRef = db.collection(Collections.users)

    usersRef.get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            for (user in task.result!!) {
                val teljesNev = (user["name"] as String).split(" ")
                val keresztnev: String
                val vezeteknev: String
                val phoneNumber: String
                val email =
                    if (isValidEmail(user["email"] as String)) user["email"] as String else ""
                if (teljesNev.count() == 2) {
                    keresztnev = teljesNev[0].unaccent()
                    vezeteknev = teljesNev[1].unaccent()
                    phoneNumber = findPhoneNumberInJson(vezeteknev, keresztnev, email)
                } else if (teljesNev.count() == 3) {
                    keresztnev = teljesNev[0].unaccent()
                    val masodikkeresztnev = teljesNev[1].unaccent()
                    vezeteknev = teljesNev[2].unaccent()
                    phoneNumber = findPhoneNumberInJson(
                        vezeteknev,
                        keresztnev,
                        email,
                        masodikkeresztnev
                    )
                } else {
                    Log.d(
                        ContentValues.TAG,
                        "name length is not appropriate, skipping (name: $teljesNev)"
                    )
                    continue
                }
                //if the phoneNumber field is not already equal to phoneNumber, update it
                //if there are multiple people with the same name, the phone number will be empty
                //(unless their email is the same as on the website)
                if (user["phoneNumber"] != phoneNumber) {
                    user.reference.update("phoneNumber", phoneNumber)
                    Log.d(
                        ContentValues.TAG,
                        "updated the phone number of ${user["name"]}: $phoneNumber"
                    )
                }
            }
        } else {
            // handle errors
        }
    }
}

private fun isValidEmail(email: String): Boolean {
    return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

