package com.longkd.chatgpt_openai.base.util

import android.os.Parcel
import android.os.Parcelable

class CommonActionWithValue(var title: String? = null, var action: ((value:String) -> Unit)? = null) : Parcelable {
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

    companion object CREATOR : Parcelable.Creator<CommonActionWithValue> {
        override fun createFromParcel(parcel: Parcel): CommonActionWithValue {
            return CommonActionWithValue(parcel)
        }

        override fun newArray(size: Int): Array<CommonActionWithValue?> {
            return arrayOfNulls(size)
        }
    }
}