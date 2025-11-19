package IFORUM

sealed class ForumAction {
    data class CreatePost(val user: User, val topic: Topic, val title: String, val content: String) : ForumAction()
    data class DeletePost(val moderator: Moderator, val post: Post) : ForumAction()
    object ViewForum : ForumAction()
    data class LikePost(val post: Post) : ForumAction()
}