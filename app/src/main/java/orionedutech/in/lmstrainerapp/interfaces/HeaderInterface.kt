package orionedutech.`in`.lmstrainerapp.interfaces

import android.view.View

interface HeaderInterface {
    fun getHeaderPositionForItem(itemPosition: Int?): Int?

    fun getHeaderLayout(headerPosition: Int?): Int?

    fun bindHeaderData(header: View?, headerPosition: Int?)

    fun isHeader(itemPosition: Int?): Boolean?
}