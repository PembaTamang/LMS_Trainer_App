package orionedutech.`in`.lmstrainerapp.model.activityAnswer

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ActivityData(
    val answer: String,
    val correctAnswer:String,
    val question: String,
    val status : String
):Parcelable