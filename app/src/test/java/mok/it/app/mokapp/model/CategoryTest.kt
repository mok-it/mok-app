package mok.it.app.mokapp.model

import mok.it.app.mokapp.model.enums.Category
import mok.it.app.mokapp.utility.Utility.unaccent
import org.junit.Assert
import org.junit.Test
import java.util.Locale

class CategoryTest {

    @Test
    fun testUniversalis() {
        val upperCaseWithoutSpace =
                Category.UNIVERZALIS.toString().uppercase(Locale.getDefault()).replace(" ", "")
        val result = upperCaseWithoutSpace.unaccent()
        Assert.assertEquals("UNIVERZALIS", result)
    }

    @Test
    fun testSzervezetFejlesztes() {
        val upperCaseWithoutSpace =
                Category.SZERVEZETFEJLESZTES.toString().uppercase(Locale.getDefault()).replace(" ", "")
        val result = upperCaseWithoutSpace.unaccent()
        Assert.assertEquals("SZERVEZETFEJLESZTES", result)
    }

    @Test
    fun testFeladatsor() {
        val upperCaseWithoutSpace =
                Category.FELADATSOR.toString().uppercase(Locale.getDefault()).replace(" ", "")
        val result = upperCaseWithoutSpace.unaccent()
        Assert.assertEquals("FELADATSOR", result)
    }

    @Test
    fun testMediaEsDIV() {
        val upperCaseWithoutSpace =
                Category.MEDIAESDIY.toString().uppercase(Locale.getDefault()).replace(" ", "")
        val result = upperCaseWithoutSpace.unaccent()
        Assert.assertEquals("MEDIAESDIY", result)
    }

    @Test
    fun testIT() {
        val upperCaseWithoutSpace =
                Category.IT.toString().uppercase(Locale.getDefault()).replace(" ", "")
        val result = upperCaseWithoutSpace.unaccent()
        Assert.assertEquals("IT", result)
    }

    @Test
    fun testPedagogia() {
        val upperCaseWithoutSpace =
                Category.PEDAGOGIA.toString().uppercase(Locale.getDefault()).replace(" ", "")
        val result = upperCaseWithoutSpace.unaccent()
        Assert.assertEquals("PEDAGOGIA", result)
    }

    @Test
    fun testNyariTaborElokeszites() {
        val upperCaseWithoutSpace =
                Category.NYARITABORIELOKESZITES.toString().uppercase(Locale.getDefault())
                        .replace(" ", "")
        val result = upperCaseWithoutSpace.unaccent()
        Assert.assertEquals("NYARITABORIELOKESZITES", result)
    }

    @Test
    fun testEvkoziTaborElokeszites() {
        val upperCaseWithoutSpace =
                Category.EVKOZITABORIELOKESZITES.toString().uppercase(Locale.getDefault())
                        .replace(" ", "")
        val result = upperCaseWithoutSpace.unaccent()
        Assert.assertEquals("EVKOZITABORIELOKESZITES", result)
    }
}