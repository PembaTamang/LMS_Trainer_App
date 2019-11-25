package orionedutech.`in`.lmstrainerapp.network.dataModels.assessmentQuestions

import orionedutech.`in`.lmstrainerapp.database.entities.AssesmentAnswers

data class DCAssesmentQuestionList(
    val assesment_ans: List<AssesmentAnswers>,
    val assesment_question: String,
    val assesment_question_id: String,
    val assesment_id: String,
    val question_type: String
)