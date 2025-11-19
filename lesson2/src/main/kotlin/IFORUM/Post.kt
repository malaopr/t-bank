package IFORUM

data class Post(
    val id: Int,
    val author: User,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    private var _likes: Int = 0
    var isDeleted: Boolean = false
        private set

    val likes: Int
        get() = _likes

    fun like() {
        if (!isDeleted) {
            _likes++
        }
    }

    fun getPostInfo(): String {
        return if (isDeleted) {
            "[УДАЛЕНО] $title"
        } else {
            "'$title' от ${author.username} (лайков: $likes)"
        }
    }

    fun markAsDeleted() {
        isDeleted = true
    }
}