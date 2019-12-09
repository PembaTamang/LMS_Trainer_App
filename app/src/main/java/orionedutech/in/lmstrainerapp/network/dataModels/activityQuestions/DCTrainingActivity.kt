package orionedutech.`in`.lmstrainerapp.network.dataModels.activityQuestions

data class DCTrainingActivity(
    val activity_data: List<DCActivityData>,
    val success: String,
    val error : String?
)