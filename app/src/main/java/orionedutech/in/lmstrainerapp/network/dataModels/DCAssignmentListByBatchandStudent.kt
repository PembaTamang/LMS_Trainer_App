package orionedutech.`in`.lmstrainerapp.network.dataModels

data class DCAssignmentListByBatchandStudent(
    val assigment_score_outof: String,
    val assignment_desc: String,
    val assignment_given_date: String,
    val assignment_id: String,
    val assignment_name: String,
    val assignment_score: Any,
    val is_marks_given: String,
    val is_submitted: String,
    val marks_given_by: String,
    val marks_given_date: Any,
    val user_assignment_id: String,
    val user_id: String
)