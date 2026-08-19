package com.example.temp_miau

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.temp_miau.security.CryptoManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CryptoManagerInstrumentedTest {

    private val cryptoManager = CryptoManager()

    @Test
    fun encryptAndDecrypt_devuelveElTextoOriginal() {
        val textoOriginal = "energia=2,bienestar=1,tiempo=0"

        val (cifrado, iv) = cryptoManager.encrypt(textoOriginal)
        val descifrado = cryptoManager.decrypt(cifrado, iv)

        assertEquals(textoOriginal, descifrado)
    }

    @Test
    fun encrypt_elTextoCifradoNoEsIgualAlOriginal() {
        val textoOriginal = "dato sensible de prueba"

        val (cifrado, _) = cryptoManager.encrypt(textoOriginal)

        assertNotEquals(textoOriginal, cifrado)
    }

    @Test
    fun encrypt_generaIvDistintoEnCadaLlamada() {
        val textoOriginal = "mismo texto, dos cifrados distintos"

        val (_, iv1) = cryptoManager.encrypt(textoOriginal)
        val (_, iv2) = cryptoManager.encrypt(textoOriginal)

        assertNotEquals(iv1, iv2)
    }
}