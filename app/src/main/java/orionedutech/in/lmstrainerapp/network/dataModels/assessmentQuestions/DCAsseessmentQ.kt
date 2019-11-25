package orionedutech.`in`.lmstrainerapp.network.dataModels.assessmentQuestions

data class DCAsseessmentQ(
    val assesment_completed: String,
    val assesment_end_date: String,
    val assesment_id: String,
    val assesment_name: String,
    val assesment_questions: List<DCAssesmentQuestionList>,
    val assesment_start_date: String,
    val assesment_time: String,
    val response: String,
    val success: String
)