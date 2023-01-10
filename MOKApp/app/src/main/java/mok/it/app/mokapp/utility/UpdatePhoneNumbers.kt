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
    result.forEach() {
        val name = it.Név.unaccent()
        if (name.contains(vezeteknev) && (name.contains(keresztnev) || (masodikkeresztnev != "" && name.contains(
                masodikkeresztnev
            )))
        )
            return it.Telefonszám
    }
    Log.d(ContentValues.TAG, "findPhoneNumber: no phone number found for $vezeteknev $keresztnev")
    return ""
}

fun updatePhoneNumbers(activity: Activity){
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
                    Log.d(ContentValues.TAG, "name length is not appropriate, skipping (name: $nev)")
                    continue
                }
                //if the phoneNumber field is not already phoneNumber, update it
                //Note that this doesn't work appropriately when there are people with the same name
                // (not including middle name), e.g. "John Doe" and "John Jack Doe"
                if (user["phoneNumber"] != phoneNumber) {
                    user.reference.update("phoneNumber", phoneNumber)
                    Log.d(ContentValues.TAG, "updated the phone number of ${user["name"]}: $phoneNumber")
                }
            }
        } else {
            // handle errors
        }
    }
}