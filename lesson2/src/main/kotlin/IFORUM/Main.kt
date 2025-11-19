package IFORUM

fun main() {
    println("=== ДЕМОНСТРАЦИЯ РАБОТЫ ФОРУМА 'МИР КОТИКОВ' ===\n")

    val myForum = Forum("Мир Котиков")

    val catLover = object : User("КотоМань", "catlover@mail.com", "meow123") {
        override fun getRole(): String = "Любитель котиков"
    }

    val vetDoctor = Moderator("Ветеринар", "vet@clinic.com", "vetPass", "Здоровье котиков")

    myForum.registerUser(catLover)
    myForum.registerUser(vetDoctor)

    val careTopic = Topic("Уход за кисулями", "Все о правильном уходе за пушистиками")
    val healthTopic = Topic("Здоровье кисуль", "Вопросы здоровья и лечения")
    val funnyTopic = Topic("Смешные фото котят", "Делимся забавными моментами")

    myForum.createTopic(careTopic)
    myForum.createTopic(healthTopic)
    myForum.createTopic(funnyTopic)

    val post1 = Post(1, catLover, "Мне купили британца, помогите!", "Сегодня взяли маленького британца! Как выжить?")
    val post2 = Post(2, vetDoctor, "Важность вакцинации", "Не забывайте делать прививки своим пушистикам!")
    val post3 = Post(3, catLover, "Смешные кисули", "Мой кот обмяукал сосиску!")
    val post4 = Post(4, catLover, "Чем кормить?", "Какой корм лучше для британца?")

    careTopic.addPost(post1)
    careTopic.addPost(post4)
    healthTopic.addPost(post2)
    funnyTopic.addPost(post3)

    println("\n--- Лайки ---")
    post1.like()
    post1.like()
    post1.like()
    post3.like()
    post3.like()
    println("Пост '${post1.title}' лайков: ${post1.likes}")
    println("Пост '${post3.title}' лайков: ${post3.likes}")

    println("\n--- Профили пользователей ---")
    catLover.displayProfile()
    println()
    vetDoctor.displayProfile()

    println("\n--- Поиск постов пользователя ---")
    val userPosts = careTopic.findPostsByAuthor(catLover)
    println("Посты ${catLover.username} в теме 'Уход за котиками':")
    userPosts.forEach { println("  - ${it.getPostInfo()}") }

    println("\n--- Работа модератора ---")
    println("Перед удалением: ${post4.getPostInfo()}")
    vetDoctor.deletePost(post4)
    println("После удаления: ${post4.getPostInfo()}")

    println("\n--- Статистика форума ---")
    myForum.displayForumStats()

    println("\n--- Содержимое тем ---")
    careTopic.displayTopic()
    healthTopic.displayTopic()
    funnyTopic.displayTopic()

    println("\n--- Смена пароля ---")
    catLover.changePassword("кот") //
    catLover.changePassword("МойКисик")

    println("\n--- Поиск темы ---")
    val foundTopic = myForum.findTopicByName("Здоровье котиков")
    if (foundTopic != null) {
        println("Найдена тема: ${foundTopic.name}")
        println("Описание: ${foundTopic.description}")
    }

    println("\n=== ДЕМОНСТРАЦИЯ ЗАВЕРШЕНА ===")
    println("Присоединяйтесь к нашему форуму 'Мир Котиков'!")
}