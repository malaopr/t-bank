package IFORUM

abstract class User(
    val username: String,
    val email: String,
    protected var password: String
) {
    constructor(username: String, email: String) : this(username, email, "defaultPass")

    abstract fun getRole(): String

    val userInfo: String
        get() = "Пользователь: $username ($email), роль: ${getRole()}"

    fun changePassword(newPassword: String) {
        if (newPassword.length >= 6) {
            password = newPassword
            println("Пароль успешно изменен для пользователя $username")
        } else {
            println("Пароль должен содержать не менее 6 символов")
        }
    }

    open fun displayProfile() {
        println(userInfo)
    }
}