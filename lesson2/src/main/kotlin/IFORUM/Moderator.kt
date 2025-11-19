package IFORUM

class Moderator(
    username: String,
    email: String,
    password: String,
    val forumSection: String
) : User(username, email, password) {


    override fun getRole(): String = "Модератор раздела '$forumSection'"


    override fun displayProfile() {
        super.displayProfile()
        println("Модератор контролирует раздел: $forumSection")
    }

    fun deletePost(post: Post) {
        println("Модератор $username удалил пост '${post.title}'")
        post.markAsDeleted()
    }
}