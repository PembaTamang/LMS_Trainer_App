package orionedutech.`in`.lmstrainerapp.network.dataModels

data class DCBatchFragmentItem(
    val batch_admin: String,
    val batch_center_id: String,
    val batch_center_name: String,
    val batch_code: String,
    val batch_end_date: String,
    val batch_id: String,
    val batch_name: String,
    val batch_pay_mode: String,
    val batch_pay_mode_amount: String,
    val batch_sector_id: String,
    val batch_sector_name: String,
    val batch_start_date: String,
    val batch_status: String,
    val batch_total_students: Int,
    val created_by: String
)