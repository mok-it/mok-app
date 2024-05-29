package mok.it.app.mokapp.model

data class ExportProject(
        val name: String = "",
        val category: String = "",
        val created: String = "",
        val creator: String = "",
        val projectLeader: String = "",
        val deadline: String = "",
        val description: String = "",
        val icon: String = "",
        val members: String = "",
        val overallProgress: Int = 0,
        val commentCount: Int = 0,
        val maxBadges: Int = 0,
        val totalEarnedBadges: Int = 0,
)


fun Project.toExportProject(members: List<User>): ExportProject {
    return ExportProject(
            name = name,
            category = categoryEnum.toString(),
            created = created.toString(),
            creator = creator,
            projectLeader = projectLeader,
            deadline = deadline.toString(),
            description = description,
            icon = icon,
            members = members.joinToString(";") { it.name },
            overallProgress = overallProgress,
            commentCount = comments.size,
            maxBadges = maxBadges,
            totalEarnedBadges = members.sumOf { it.projectBadges[id] ?: 0 }
    )
}


