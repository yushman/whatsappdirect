package ru.tomindapps.main

import org.junit.Test
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

    private fun String.isPhoneNumber(): Boolean = PhoneValidator.isPhoneNumber(this)
}