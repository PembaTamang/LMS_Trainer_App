package orionedutech.`in`.lmstrainerapp.network.dataModels.assessmentQuestions

data class DCAssesmentAns(
    val answer_right_wrong: String,
    val answer_value: String,
    val question: String?,
    val question_ans_id: String,
    val question_id: String
)