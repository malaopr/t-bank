package IFORUM

class Topic(
    val name: String,
    val description: String
) {
    private val posts: MutableList<Post> = mutableListOf()

    val allPosts: List<Post>
        get() = posts.toList()

    fun addPost(post: Post) {
        if (!post.isDeleted) {
            posts.add(post)
            println("Пост '${post.title}' добавлен в тему '$name'")
        }
    }

    fun displayTopic() {
        println("=== Тема: $name ===")
        println("Описание: $description")
        println("Количество постов: ${posts.size}")
        println("Посты:")
        posts.forEach { post ->
            println("  - ${post.getPostInfo()}")
        }
        println()
    }

    fun findPostsByAuthor(author: User): List<Post> {
        return posts.filter { it.author == author && !it.isDeleted }
    }
}