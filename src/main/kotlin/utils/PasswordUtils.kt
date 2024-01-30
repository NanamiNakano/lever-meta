package dev.thynanami.utils

import com.password4j.Argon2Function
import com.password4j.Password
import com.password4j.types.Argon2
import java.security.SecureRandom

val argon2Instance: Argon2Function = Argon2Function.getInstance(47104, 1, 1, 32, Argon2.ID)

fun hashPassword(plainTextPassword: String): String {
    return Password.hash(plainTextPassword).addRandomSalt().with(argon2Instance).result
}

fun verifyPassword(plainTextPassword: String, hash: String): Boolean {
    return Password.check(plainTextPassword, hash).with(argon2Instance)
}

fun generateSecurePassword(length: Int): String {
    val charset = ('A'..'Z') + ('a'..'z') + ('0'..'9') + listOf('!', '@', '#', '$', '%', '^', '*', '(', ')')
    val secureRandom = SecureRandom()
    return (1..length)
        .map { charset[secureRandom.nextInt(charset.size)] }
        .joinToString("")
}
