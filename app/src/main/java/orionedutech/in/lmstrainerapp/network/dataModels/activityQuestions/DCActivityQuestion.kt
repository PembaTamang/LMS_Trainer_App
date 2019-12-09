package orionedutech.`in`.lmstrainerapp.network.dataModels.activityQuestions

data class DCActivityQuestion(
    val activity_ans: List<DCActivityAns>,
    val activity_qns_id: String,
    val activity_qns_language: String,
    val activity_qns_type: String,
    val activity_qns_value: String
)