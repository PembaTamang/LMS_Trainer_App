package orionedutech.`in`.lmstrainerapp.network.dataModels

data class DCCourse(
    val batch_id : String,
    val batch_name : String,
    val course_id : String,
    val course_links_to : String,
    val course_sector_id : String,
    val course_sector_code : String,
    val course_code : String,
    val course_abbr : String,
    val course_name : String,
    val course_description : String,
    val course_type : String,
    val course_status : Int,
    val created_at : String,
    val updated_at : String,
    val created_by : String,
    val created_by_type : String,
    val is_deleted : String,
    val deleted_by : String,
    val deleted_date : String
)
