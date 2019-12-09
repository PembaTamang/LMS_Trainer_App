package orionedutech.`in`.lmstrainerapp.network.dataModels.activityQuestions

data class DCActivityData(
    val activity_chapter_id: String,
    val activity_description: Any,
    val activity_id: String,
    val activity_name: String,
    val activity_questions: List<DCActivityQuestion>
)