package br.project_advhevogoober_final

import org.junit.Assert.assertThat
import org.junit.Test
import org.hamcrest.CoreMatchers.`is` as Is

private val regexCpf : Regex = Regex( "^[0-9]{3}\\.?[0-9]{3}\\.?[0-9]{3}-?[0-9]{2}$")
private val regexCelTel : Regex = Regex("^([1-9]{2}) (?:[2-8]|9[1-9])[0-9]{3}-[0-9]{4}$")
private val regexCnpj : Regex = Regex( "^[0-9]{2}\\.?[0-9]{3}\\.?[0-9]{3}\\/?[0-9]{4}\\-?[0-9]{2}$")

class RegexValidatorTest {

    @Test
    fun regexValidator_CorrectCPF_ReturnsTrue() {
        val validCPF: String = "107.532.908-10"
        assertThat(regexCpf.matches(validCPF), Is(true))
    }

    @Test
    fun regexValidator_IncorrectCPF_HyphenRatherThanDot_ReturnsFalse() {
        val invalidCPF: String = "107.532-908-10"
        assertThat(regexCpf.matches(invalidCPF), Is(false))
    }

    @Test
    fun regexValidator_IncorrectCPF_TooManyNumbers_ReturnsFalse() {
        val invalidCPF: String = "107.532.908-101"
        assertThat(regexCpf.matches(invalidCPF), Is(false))
    }

    @Test
    fun regexValidator_IncorrectCPF_TooLittleNumbers_ReturnsFalse() {
        val invalidCPF: String = "107.532.908-1"
        assertThat(regexCpf.matches(invalidCPF), Is(false))
    }

    @Test
    fun regexValidator_CorrectPhone_ReturnsTrue() {
        val validPhone: String = "21 98259-9048"
        assertThat(regexCelTel.matches(validPhone), Is(true))
    }

    @Test
    fun regexValidator_IncorrectPhone_NoHyphen_ReturnsFalse() {
        val invalidPhone: String = "21 982599048"
        assertThat(regexCelTel.matches(invalidPhone), Is(false))
    }

    @Test
    fun regexValidator_IncorrectPhone_TooManyNumbers_ReturnsFalse() {
        val invalidPhone: String = "21 95679-95643"
        assertThat(regexCelTel.matches(invalidPhone), Is(false))
    }

    @Test
    fun regexValidator_IncorrectPhone_TooLittleNumbers_ReturnsFalse() {
        val invalidPhone: String = "21 9999-922"
        assertThat(regexCelTel.matches(invalidPhone), Is(false))
    }

    @Test
    fun regexValidator_CorrectCNPJ_ReturnsTrue() {
        val validCNPJ: String = "22.333.333/4444-22"
        assertThat(regexCnpj.matches(validCNPJ), Is(true))
    }

    @Test
    fun regexValidator_IncorrectCNPJ_WrongSpecialCharacters_ReturnsFalse() {
        val invalidCNPJ: String = "22-333-333.4444/22"
        assertThat(regexCnpj.matches(invalidCNPJ), Is(false))
    }

    @Test
    fun regexValidator_IncorrectCNPJ_TooManyNumbers_ReturnsFalse() {
        val invalidCNPJ: String = "22.3333.333/4444-222"
        assertThat(regexCnpj.matches(invalidCNPJ), Is(false))
    }

    @Test
    fun regexValidator_IncorrectCNPJ_TooLittleNumbers_ReturnsFalse() {
        val invalidCNPJ: String = "22.33.333/4444-2"
        assertThat(regexCnpj.matches(invalidCNPJ), Is(false))
    }


}