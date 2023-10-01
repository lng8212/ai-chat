package com.longkd.chatgpt_openai.base.util

import android.os.Parcel
import android.os.Parcelable

class CommonAction(var title: String? = null, var action: (() -> Unit)? = null) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        {}
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CommonAction> {
        override fun createFromParcel(parcel: Parcel): CommonAction {
            return CommonAction(parcel)
        }

        override fun newArray(size: Int): Array<CommonAction?> {
            return arrayOfNulls(size)
        }
    }
}