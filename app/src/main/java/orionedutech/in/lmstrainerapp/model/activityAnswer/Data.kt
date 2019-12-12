package orionedutech.`in`.lmstrainerapp.model.activityAnswer

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Data(
    val activity_data: List<ActivityData>,
    val activity_name: String,
    val total_questions : String,
    val correct_answers : String,
    val incorrect_answers : String
):Parcelable