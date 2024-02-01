package mok.it.app.mokapp.model

import org.junit.Assert
import org.junit.Test
import java.util.Locale

class CategoryTest {

    fun replaceHungarianCharsToEnglishUpperCase(word: String): String {
        val retVal = StringBuilder()
        for (character in word.toCharArray()) {
            // Ellenőrizzük, hogy a karakter ékezetes-e.
            when (character) {
                'Ö', 'Ő', 'Ó' -> retVal.append('O')
                'Ü', 'Ű', 'Ú' -> retVal.append('U')
                'É' -> retVal.append('E')
                'Á' -> retVal.append('A')
                'Í' -> retVal.append('I')
                else -> retVal.append(character)
            }
        }
        return retVal.toString()
    }

    @Test
    fun testUniversalis() {
        val upperCaseWithoutSpace =
            Category.UNIVERZALIS.toString().uppercase(Locale.getDefault()).replace(" ", "")
        val result = replaceHungarianCharsToEnglishUpperCase(upperCaseWithoutSpace)
        Assert.assertEquals("UNIVERZALIS", result)
    }

    @Test
    fun testSzervezetFejlesztes() {
        val upperCaseWithoutSpace =
            Category.SZERVEZETFEJLESZTES.toString().uppercase(Locale.getDefault()).replace(" ", "")
        val result = replaceHungarianCharsToEnglishUpperCase(upperCaseWithoutSpace)
        Assert.assertEquals("SZERVEZETFEJLESZTES", result)
    }

    @Test
    fun testFeladatsor() {
        val upperCaseWithoutSpace =
            Category.FELADATSOR.toString().uppercase(Locale.getDefault()).replace(" ", "")
        val result = replaceHungarianCharsToEnglishUpperCase(upperCaseWithoutSpace)
        Assert.assertEquals("FELADATSOR", result)
    }

    @Test
    fun testMediaEsDIV() {
        val upperCaseWithoutSpace =
            Category.MEDIAESDIY.toString().uppercase(Locale.getDefault()).replace(" ", "")
        val result = replaceHungarianCharsToEnglishUpperCase(upperCaseWithoutSpace)
        Assert.assertEquals("MEDIAESDIY", result)
    }

    @Test
    fun testTaboriprogramEsElokeszites() {
        val upperCaseWithoutSpace =
            Category.TABORIPROGRAMESELOKESZITES.toString().uppercase(Locale.getDefault())
                .replace(" ", "")
        val result = replaceHungarianCharsToEnglishUpperCase(upperCaseWithoutSpace)
        Assert.assertEquals("TABORIPROGRAMESELOKESZITES", result)
    }

    @Test
    fun testIT() {
        val upperCaseWithoutSpace =
            Category.IT.toString().uppercase(Locale.getDefault()).replace(" ", "")
        val result = replaceHungarianCharsToEnglishUpperCase(upperCaseWithoutSpace)
        Assert.assertEquals("IT", result)
    }

    @Test
    fun testPedagogia() {
        val upperCaseWithoutSpace =
            Category.PEDAGOGIA.toString().uppercase(Locale.getDefault()).replace(" ", "")
        val result = replaceHungarianCharsToEnglishUpperCase(upperCaseWithoutSpace)
        Assert.assertEquals("PEDAGOGIA", result)
    }

    @Test
    fun testNyariTaborElokeszites() {
        val upperCaseWithoutSpace =
            Category.NYARITABORIELOKESZITES.toString().uppercase(Locale.getDefault())
                .replace(" ", "")
        val result = replaceHungarianCharsToEnglishUpperCase(upperCaseWithoutSpace)
        Assert.assertEquals("NYARITABORIELOKESZITES", result)
    }

    @Test
    fun testEvkoziTaborElokeszites() {
        val upperCaseWithoutSpace =
            Category.EVKOZITABORIELOKESZITES.toString().uppercase(Locale.getDefault())
                .replace(" ", "")
        val result = replaceHungarianCharsToEnglishUpperCase(upperCaseWithoutSpace)
        Assert.assertEquals("EVKOZITABORIELOKESZITES", result)
    }
}