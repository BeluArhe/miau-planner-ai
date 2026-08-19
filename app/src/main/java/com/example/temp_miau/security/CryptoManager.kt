package com.example.temp_miau.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class CryptoManager {

    private val keyAlias = "miau_wellness_key"
    private val androidKeyStore = "AndroidKeyStore"
    private val transformation = "AES/GCM/NoPadding"

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(androidKeyStore).apply { load(null) }

        (keyStore.getEntry(keyAlias, null) as? KeyStore.SecretKeyEntry)?.let {
            return it.secretKey
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, androidKeyStore
        )
        val spec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    fun encrypt(plainText: String): Pair<String, String> {
        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())

        val cipherBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val ivBytes = cipher.iv

        val cipherTextBase64 = Base64.encodeToString(cipherBytes, Base64.NO_WRAP)
        val ivBase64 = Base64.encodeToString(ivBytes, Base64.NO_WRAP)

        return Pair(cipherTextBase64, ivBase64)
    }

    fun decrypt(cipherTextBase64: String, ivBase64: String): String {
        val cipherBytes = Base64.decode(cipherTextBase64, Base64.NO_WRAP)
        val ivBytes = Base64.decode(ivBase64, Base64.NO_WRAP)

        val cipher = Cipher.getInstance(transformation)
        val spec = GCMParameterSpec(128, ivBytes)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), spec)

        val plainBytes = cipher.doFinal(cipherBytes)
        return String(plainBytes, Charsets.UTF_8)
    }
}