package orionedutech.`in`.lmstrainerapp.network.dataModels

data class DCScoreList(
    val assesment_question: String,
    val assesment_question_id: String,
    val correct_answers: String,
    val user_answer_id: Any,
    val user_answer_right_wrong: String,
    val user_answer_value: String
)