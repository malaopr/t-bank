package IFORUM

class Forum(val name: String) {
    private val topics: MutableList<Topic> = mutableListOf()
    private val users: MutableList<User> = mutableListOf()

    fun registerUser(user: User) {
        users.add(user)
        println("Пользователь ${user.username} зарегистрирован на форуме '$name'")
    }

    fun createTopic(topic: Topic) {
        topics.add(topic)
        println("Создана новая тема: '${topic.name}'")
    }

    fun displayForumStats() {
        println("=== ФОРУМ: $name ===")
        println("Всего пользователей: ${users.size}")
        println("Всего тем: ${topics.size}")
        println("Темы на форуме:")
        topics.forEach { topic ->
            println("  - ${topic.name} (постов: ${topic.allPosts.size})")
        }
        println()
    }

    fun findTopicByName(name: String): Topic? {
        return topics.find { it.name == name }
    }
}