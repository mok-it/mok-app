package mok.it.app.mokapp.utility

import mok.it.app.mokapp.utility.Utility.unaccent
import org.junit.Assert
import org.junit.Test

class UtilityTest {
    @Test
    fun testUnaccent() {
        val result = "árvíztűrő tükörfúrógép ÁRVÍZTŰRŐ TÜKÖRFÚRÓGÉP".unaccent()
        Assert.assertEquals("arvizturo tukorfurogep ARVIZTURO TUKORFUROGEP", result)
    }

    @Test
    fun testGetIconFileName() {
        val utility = Utility
        val result = utility.getIconFileName("testFileName")
        println(result)
        Assert.assertEquals("testFileName.png", result)
    }
}
