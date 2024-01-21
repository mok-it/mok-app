package mok.it.app.mokapp.service

interface IUserService {
    fun addBadges(userId: String, badgeId: String, badgeAmount: Int, onComplete: () -> Unit, onFailure: (Exception) -> Unit)
    fun getBadgeAmountSum(userId: String, onComplete: (Int) -> Unit, onFailure: (Exception) -> Unit)
    fun getProjectBadges(userId: String, onComplete: (Map<String, Int>) -> Unit, onFailure: (Exception) -> Unit)
    fun getProjectUsersAndBadges(projectId: String, onComplete: (Map<String, Int>) -> Unit, onFailure: (Exception) -> Unit)
    fun joinUsersToProject(projectId: String, userIds: List<String>, onComplete: () -> Unit, onFailure: (Exception) -> Unit)
}