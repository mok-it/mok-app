package mok.it.app.mokapp.utility

import android.app.Activity
import android.content.ContentValues
import android.util.Log
import com.beust.klaxon.Klaxon
import com.google.firebase.firestore.FirebaseFirestore
import java.text.Normalizer


private var result = listOf<Person>()

private class Person(val Név: String, val Telefonszám: String)

private fun CharSequence.unaccent(): String {
    val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
    return "\\p{InCombiningDiacriticalMarks}+".toRegex().replace(temp, "")
}

private fun findPhoneNumber(
    vezeteknev: String,
    keresztnev: String,
    masodikkeresztnev: String = ""
): String {
    var count = 0
    var phoneNumber = ""
    result.forEach {
        val name = it.Név.unaccent()
        if (name.contains(vezeteknev) && (name.contains(keresztnev) || (masodikkeresztnev != "" && name.contains(
                masodikkeresztnev
            )))
        )
            if (count >= 1) {
                Log.d(
                    "UpdatePhoneNumbers",
                    "Too many results, there are (at least partly) matching names: $vezeteknev $keresztnev $masodikkeresztnev"
                )
                return ""
            } else {
                count++
                phoneNumber = it.Telefonszám
            }
    }
    return if (count == 0) {
        Log.d(
            ContentValues.TAG,
            "findPhoneNumber: no phone number found for $vezeteknev $keresztnev"
        )
        ""
    } else phoneNumber
}

fun updatePhoneNumbers(activity: Activity) {
    //phonenumbers.json file has to be in assets folder
    result = Klaxon().parseArray(activity.assets.open("phonenumbers.json"))!!

    val db = FirebaseFirestore.getInstance()
    val usersRef = db.collection("users")

    usersRef.get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            for (user in task.result!!) {
                val nev = (user["name"] as String).split(" ")
                val keresztnev: String
                val vezeteknev: String
                val phoneNumber: String
                if (nev.count() == 2) {
                    keresztnev = nev[0].unaccent()
                    vezeteknev = nev[1].unaccent()
                    phoneNumber = findPhoneNumber(vezeteknev, keresztnev)
                } else if (nev.count() == 3) {
                    keresztnev = nev[0].unaccent()
                    val masodikkeresztnev = nev[1].unaccent()
                    vezeteknev = nev[2].unaccent()
                    phoneNumber = findPhoneNumber(vezeteknev, keresztnev, masodikkeresztnev)
                } else {
                    Log.d(
                        ContentValues.TAG,
                        "name length is not appropriate, skipping (name: $nev)"
                    )
                    continue
                }
                //if the phoneNumber field is not already phoneNumber, update it
                //if there are multiple people with the same name, the phone number will be empty
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