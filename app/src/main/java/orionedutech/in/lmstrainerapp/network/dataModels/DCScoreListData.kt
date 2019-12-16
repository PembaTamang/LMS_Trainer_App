package orionedutech.`in`.lmstrainerapp.network.dataModels


data class DCScoreListData(
    val assesment_end_date: String,
    val assesment_name: String,
    val assesment_percentage: String,
    val assesment_start_date: String,
    val assesment_time: String,
    val assesment_total_questions: Int,
    val assesment_total_questions_right: Int,
    val batch_end_date: String,
    val batch_name: String,
    val batch_pay_mode: String,
    val batch_pay_mode_amount: String,
    val batch_start_date: String,
    val response: List<DCScoreList>,
    val success: String
)