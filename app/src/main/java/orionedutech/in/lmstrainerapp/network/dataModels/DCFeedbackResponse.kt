package orionedutech.`in`.lmstrainerapp.network.dataModels

data class DCFeedbackResponse(
    val batch_id: String,
    val center_id: String,
    val course_id: String,
    val created_at: String,
    val created_by: String,
    val feedback_id: String,
    val feedback_question: String,
    val feedback_user_type: String,
    val status: String,
    val updated_at: Any,
    val updated_by: Any
)