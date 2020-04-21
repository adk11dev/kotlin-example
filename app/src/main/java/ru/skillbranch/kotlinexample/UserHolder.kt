package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting

object UserHolder {
    private val map = mutableMapOf<String, User>()

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): User = User.makeUser(fullName, email = email, password = password)
        .also { user -> require(!map.containsKey(user.login)) { "A user with this email already exists" } }
        .also { user -> map[user.login] = user }

    fun registerUserByPhone(
        fullName: String,
        rawPhone: String
    ): User = User.makeUser(fullName, phone = rawPhone)
        .also { user -> require(!map.containsKey(user.login)) { throw IllegalArgumentException("A user with this phone already exists") } }
        .also { user -> map[user.login] = user }

    fun loginUser(login: String, password: String): String? {

        val phone = login.replace("""[^+\d]""".toRegex(), "")

        return if (Regex("""^\+\d{11}""").matches(phone)) {
            map[phone.trim()]?.let {
                if (it.checkPassword(password)) it.userInfo
                else null
            }
        } else {
            map[login.trim()]?.let {
                if (it.checkPassword(password)) it.userInfo
                else null
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }

    fun requestAccessCode(login: String): Unit {
        val phone = login.replace("""[^+\d]""".toRegex(), "")

        if (Regex("""^\+\d{11}""").matches(phone)) {
            map[phone.trim()]?.let {
                it.generateAndSetAccessCode(login)
            }
        }
    }
}