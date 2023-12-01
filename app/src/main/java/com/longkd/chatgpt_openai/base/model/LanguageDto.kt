package com.longkd.chatgpt_openai.base.model

import android.os.Parcel
import android.os.Parcelable

data class LanguageDto(val data: LanguageItem, var isSelected: Boolean) : Parcelable {
    constructor(parcel: Parcel) : this(
        LanguageItem.ENGLISH,
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LanguageDto> {
        override fun createFromParcel(parcel: Parcel): LanguageDto {
            return LanguageDto(parcel)
        }

        override fun newArray(size: Int): Array<LanguageDto?> {
            return arrayOfNulls(size)
        }
    }
}