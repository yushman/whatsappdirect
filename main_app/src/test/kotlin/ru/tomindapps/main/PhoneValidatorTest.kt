package ru.tomindapps.main

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class PhoneValidatorTest {

    @Test
    fun isPhoneNumber() {
        assertTrue("+79994442211".isPhoneNumber())
        assertTrue("79994442211".isPhoneNumber())
        assertTrue("179994442211".isPhoneNumber())
        assertFalse("asd79994442211".isPhoneNumber())
    }

    @Test
    fun clearyfyTest() {
        val expected = "79994442211"

        assertEquals(expected, "+79994442211".clearify())
        assertEquals(expected, "+7(999) 444 22 11".clearify())
        assertEquals(expected, "+7999-444-2211".clearify())
        assertEquals(expected, "7+999+444()2211".clearify())
    }

    private fun String.clearify(): String {
        val reg = "^(\\+)|[^\\d\\n]".toRegex()
        return this.replace(reg, "")
    }

    private fun String.isPhoneNumber(): Boolean = PhoneValidator.isPhoneNumber(this)
}