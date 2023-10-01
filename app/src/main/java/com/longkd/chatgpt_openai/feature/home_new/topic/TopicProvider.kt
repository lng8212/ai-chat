package com.longkd.chatgpt_openai.feature.home_new.topic

import android.content.Context
import android.os.Parcelable
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.model.DetailTopicData
import com.longkd.chatgpt_openai.base.model.TopicData
import kotlinx.parcelize.Parcelize

object TopicProvider {

    fun getListTopics(list: List<String>): List<TopicData> {
        val result: ArrayList<TopicData> = arrayListOf()
        list.forEachIndexed { index, it ->
            result.add(TopicData(index, it))
        }
        return result
    }

    val listSubHaveOption: ArrayList<OptionSubTopic> = arrayListOf()
    val listAllCountris = listOf<String>("United States", "Canada", "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua and/or Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory", "Brunei Darussalam", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Cape Verde", "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island", "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo", "Cook Islands", "Costa Rica", "Croatia (Hrvatska)", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "East Timor", "Ecudaor", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Falkland Islands (Malvinas)", "Faroe Islands", "Fiji", "Finland", "France", "France, Metropolitan", "French Guiana", "French Polynesia", "French Southern Territories", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Heard and Mc Donald Islands", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran (Islamic Republic of)", "Iraq", "Ireland", "Israel", "Italy", "Ivory Coast", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, Democratic People's Republic of", "Korea, Republic of", "Kosovo", "Kuwait", "Kyrgyzstan", "Lao People's Democratic Republic", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libyan Arab Jamahiriya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia, Federated States of", "Moldova, Republic of", "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfork Island", "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russian Federation", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Georgia South Sandwich Islands", "South Sudan", "Spain", "Sri Lanka", "St. Helena", "St. Pierre and Miquelon", "Sudan", "Suriname", "Svalbarn and Jan Mayen Islands", "Swaziland", "Sweden", "Switzerland", "Syrian Arab Republic", "Taiwan", "Tajikistan", "Tanzania, United Republic of", "Thailand", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States minor outlying islands", "Uruguay", "Uzbekistan", "Vanuatu", "Vatican City State", "Venezuela", "Vietnam", "Virigan Islands (British)", "Virgin Islands (U.S.)", "Wallis and Futuna Islands", "Western Sahara", "Yemen", "Yugoslavia", "Zaire", "Zambia", "Zimbabwe")
    val listAllLanguage = listOf<String>("Afrikaans", "Albanian", "Amharic", "Arabic", "Armenian", "Aymara", "Azerbaijani", "Bambara", "Basque", "Belarusian", "Bengali", "Bhojpuri", "Bosnian", "Bulgarian", "Catalan", "Cebuano", "Chinese (Simplified)", "Chinese (Traditional)", "Corsican", "Croatian", "Czech", "Danish", "Dhivehi", "Dogri", "Dutch", "English", "Esperanto", "Estonian", "Ewe", "Filipino (Tagalog)", "Finnish", "French", "Frisian", "Galician", "Georgian", "German", "Greek", "Guarani", "Gujarati", "Haitian Creole", "Hausa", "Hawaiian", "Hebrew", "Hindi", "Hmong", "Hungarian", "Icelandic", "Igbo", "Ilocano", "Indonesian", "Irish", "Italian", "Japanese", "Javanese", "Kannada", "Kazakh", "Khmer", "Kinyarwanda", "Konkani", "Korean", "Krio", "Kurdish", "Kurdish (Sorani)", "Kyrgyz", "Lao", "Latin", "Latvian", "Lingala", "Lithuanian", "Luganda", "Luxembourgish", "Macedonian", "Maithili", "Malagasy", "Malay", "Malayalam", "Maltese", "Maori", "Marathi", "Meiteilon (Manipuri)", "Mizo", "Mongolian", "Myanmar (Burmese)", "Nepali", "Norwegian", "Nyanja (Chichewa)", "Odia (Oriya)", "Oromo", "Pashto", "Persian", "Polish", "Portuguese (Portugal, Brazil)", "Punjabi", "Quechua", "Romanian", "Russian", "Hungary", "Samoan", "Sanskrit", "Scots Gaelic", "Sepedi", "Serbian", "Sesotho", "Shona", "Sindhi", "Sinhala (Sinhalese)", "Slovak", "Slovenian", "Somali", "Spanish", "Sundanese", "Swahili", "Swedish", "Tagalog (Filipino)", "Tajik", "Tamil", "Tatar", "Telugu", "Thai", "Tigrinya", "Tsonga", "Turkish", "Turkmen", "Twi (Akan)", "Ukrainian", "Urdu", "Uyghur", "Uzbek", "Vietnamese", "Yiddish", "Yoruba", "Zulu")
    fun listSubHaveOption(list: Array<DetailTopicData>, context: Context) {
        listSubHaveOption.clear()
        list.map { it1 -> it1.toString() }.forEach {
            when(DetailTopicData.fromString(it)) {
                DetailTopicData.MARKETING_PLAN -> {
                    addSubTopic(it, arrayListOf(
                        DataFieldTopic(context.getString(R.string.str_brand_name), DataTypeTopic.INPUT.name),
                        DataFieldTopic(context.getString(R.string.str_product_services), DataTypeTopic.INPUT.name),
                        DataFieldTopic(context.getString(R.string.str_goals), DataTypeTopic.INPUT.name),
                        DataFieldTopic(context.getString(R.string.str_context_strategy_type), DataTypeTopic.MULTI_SELECT.name, context.resources.getStringArray(R.array.arr_strategy_type).toList())
                    )
                    )
                }

                DetailTopicData.INTERVIEWER_SUPPORT -> {
                    addSubTopic(it, arrayListOf(
                        DataFieldTopic(context.getString(R.string.str_position), DataTypeTopic.INPUT.name, null, context.getString(R.string.str_context_position)),
                        DataFieldTopic(context.getString(R.string.str_details), DataTypeTopic.MULTI_SELECT.name, context.resources.getStringArray(R.array.arr_interviewer_detail).toList()),)
                    )
                }

                DetailTopicData.COMPETITORS_RESEARCH -> {
                    addSubTopic(it, arrayListOf(
                        DataFieldTopic(context.getString(R.string.str_context_name), DataTypeTopic.INPUT.name),
                        DataFieldTopic(context.getString(R.string.str_industry), DataTypeTopic.INPUT.name),
                        DataFieldTopic(context.getString(R.string.str_product_services), DataTypeTopic.INPUT.name),
                        DataFieldTopic(context.getString(R.string.str_key_segment), DataTypeTopic.MULTI_SELECT.name, context.resources.getStringArray(R.array.arr_key_segment).toList())
                    ))
                }

                DetailTopicData.CUSTOMERS_INSIGHTS_RESEARCH -> {
                    addSubTopic(it, arrayListOf(
                        DataFieldTopic(context.getString(R.string.str_product_services), DataTypeTopic.INPUT.name, null, context.getString(R.string.str_context_product_services)),
                        DataFieldTopic(context.getString(R.string.str_age), DataTypeTopic.INPUT.name),
                        DataFieldTopic(context.getString(R.string.str_gender), DataTypeTopic.SELECT.name, context.resources.getStringArray(R.array.arr_gender).toList()),
                        DataFieldTopic(context.getString(R.string.str_region), DataTypeTopic.INPUT.name)
                    ))
                }

                DetailTopicData.WRITE_PARAGRAPH -> {
                    addSubTopic(it, arrayListOf(
                        DataFieldTopic(context.getString(R.string.str_subject), DataTypeTopic.INPUT.name, null, context.getString(R.string.str_context_subject)),
                        DataFieldTopic(context.getString(R.string.str_tone_of_voice), DataTypeTopic.SELECT.name, context.resources.getStringArray(R.array.arr_tone_voice).toList()),
                        DataFieldTopic(context.getString(R.string.str_length), DataTypeTopic.INPUT.name),
                    ))
                }

                DetailTopicData.WRITE_SONG_LYRICS -> {
                    addSubTopic(it, arrayListOf(
                        DataFieldTopic(context.getString(R.string.str_context), DataTypeTopic.INPUT.name, null, context.getString(R.string.str_context_song)),
                        DataFieldTopic(context.getString(R.string.str_genre), DataTypeTopic.SELECT.name, context.resources.getStringArray(R.array.arr_genre_song).toList()),
                        DataFieldTopic(context.getString(R.string.str_mood), DataTypeTopic.SELECT.name, context.resources.getStringArray(R.array.arr_mood_song).toList())
                    ))
                }

                DetailTopicData.WRITE_MEDIA_POST -> {
                    addSubTopic(it, arrayListOf(
                        DataFieldTopic(context.getString(R.string.str_platform), DataTypeTopic.SELECT.name, context.resources.getStringArray(R.array.arr_media_plalform).toList()),
                        DataFieldTopic(context.getString(R.string.str_context), DataTypeTopic.INPUT.name, null, context.getString(R.string.str_context_post_media)),
                        DataFieldTopic(context.getString(R.string.str_goals), DataTypeTopic.MULTI_SELECT.name, context.resources.getStringArray(R.array.arr_media_goal).toList()),
                        DataFieldTopic(context.getString(R.string.str_tone_of_voice), DataTypeTopic.SELECT.name, context.resources.getStringArray(R.array.arr_tone_voice).toList())
                    ))
                }

                DetailTopicData.WRITE_EMAIL -> {
                    addSubTopic(it, arrayListOf(
                        DataFieldTopic(context.getString(R.string.str_context), DataTypeTopic.INPUT.name, null, context.getString(R.string.str_context_post_media)),
                        DataFieldTopic(context.getString(R.string.str_tone_of_voice), DataTypeTopic.SELECT.name, context.resources.getStringArray(R.array.arr_tone_voice).toList())
                    ))
                }

                DetailTopicData.OUTFIT_DESIGNER -> {
                    addSubTopic(it, arrayListOf(
                        DataFieldTopic(context.getString(R.string.str_gender), DataTypeTopic.SELECT.name, context.resources.getStringArray(R.array.arr_gender).toList()),
                        DataFieldTopic(context.getString(R.string.str_style), DataTypeTopic.SELECT.name, context.resources.getStringArray(R.array.arr_outfit_designer_style).toList(), context.getString(R.string.str_context_style))
                    ))
                }

                DetailTopicData.WRITE_PICKUP_LINE -> {
                    addSubTopic(it, arrayListOf(
                        DataFieldTopic(context.getString(R.string.str_language), DataTypeTopic.SELECT.name, listAllLanguage),
                        DataFieldTopic(context.getString(R.string.str_tone_of_voice), DataTypeTopic.INPUT.name)
                    ))
                }

                DetailTopicData.WRITE_JOKE -> {
                    addSubTopic(it, arrayListOf(
                        DataFieldTopic(context.getString(R.string.str_language), DataTypeTopic.SELECT.name, listAllLanguage),
                        DataFieldTopic(context.getString(R.string.str_context), DataTypeTopic.INPUT.name, null, context.getString(R.string.str_context_joke))
                    ))
                }

                DetailTopicData.HOROSCOPE_READER -> {
                    addSubTopic(it, arrayListOf(
                        DataFieldTopic(context.getString(R.string.str_birth_date), DataTypeTopic.ORTHER.name, null, context.getString(R.string.str_context_birth_date_time)),
                        DataFieldTopic(context.getString(R.string.str_information), DataTypeTopic.MULTI_SELECT.name, context.resources.getStringArray(R.array.arr_information_horoscope).toList(), context.getString(R.string.str_context_information))
                    ))
                }

                DetailTopicData.SOLVE_MATH_PROBLEM -> {
                    addSubTopic(it, arrayListOf(
                        DataFieldTopic(context.getString(R.string.str_context), DataTypeTopic.INPUT.name, null, context.getString(R.string.str_context_solve_math_problem))
                    ))
                }

                DetailTopicData.HISTORY_EVENTS -> {
                    addSubTopic(it, arrayListOf(
                        DataFieldTopic(context.getString(R.string.str_year), DataTypeTopic.INPUT.name),
                        DataFieldTopic(context.getString(R.string.str_country_region), DataTypeTopic.SELECT.name, listAllCountris)
                    ))
                }

                DetailTopicData.CODE_SUPPORTER -> {
                    addSubTopic(it, arrayListOf(
                        DataFieldTopic(context.getString(R.string.str_issue), DataTypeTopic.INPUT.name, null, context.getString(R.string.str_context_issue)),
                        DataFieldTopic(context.getString(R.string.str_language), DataTypeTopic.SELECT.name, context.resources.getStringArray(R.array.language_programing).toList())
                    ))
                }

                DetailTopicData.LATINO_DISHES -> {
                    addSubTopic(it, arrayListOf(
                        DataFieldTopic(context.getString(R.string.str_dishes), DataTypeTopic.INPUT.name, null, context.getString(R.string.str_context_dishes)),
                        DataFieldTopic(context.getString(R.string.str_techniques), DataTypeTopic.SELECT.name, context.resources.getStringArray(R.array.arr_techniques_cooking).toList(), context.getString(R.string.str_context_techniques)),
                        DataFieldTopic(context.getString(R.string.str_ingredients), DataTypeTopic.INPUT.name, null, context.getString(R.string.str_context_ingredients)),
                        DataFieldTopic(context.getString(R.string.str_position), DataTypeTopic.INPUT.name, null, context.getString(R.string.str_context_potion))
                    ))
                }

                DetailTopicData.AFRICAN_DISHES -> {
                    addSubTopic(it, arrayListOf(
                        DataFieldTopic(context.getString(R.string.str_dishes), DataTypeTopic.INPUT.name, null, context.getString(R.string.str_context_dishes)),
                        DataFieldTopic(context.getString(R.string.str_techniques), DataTypeTopic.SELECT.name, context.resources.getStringArray(R.array.arr_techniques_cooking).toList(), context.getString(R.string.str_context_techniques)),
                        DataFieldTopic(context.getString(R.string.str_ingredients), DataTypeTopic.INPUT.name, null, context.getString(R.string.str_context_ingredients)),
                        DataFieldTopic(context.getString(R.string.str_position), DataTypeTopic.INPUT.name, null, context.getString(R.string.str_context_potion))
                    ))
                }

                DetailTopicData.ASIAN_DISHES -> {
                    addSubTopic(it, arrayListOf(
                        DataFieldTopic(context.getString(R.string.str_dishes), DataTypeTopic.INPUT.name, null, context.getString(R.string.str_context_dishes)),
                        DataFieldTopic(context.getString(R.string.str_techniques), DataTypeTopic.SELECT.name, context.resources.getStringArray(R.array.arr_techniques_cooking).toList(), context.getString(R.string.str_context_techniques)),
                        DataFieldTopic(context.getString(R.string.str_ingredients), DataTypeTopic.INPUT.name, null, context.getString(R.string.str_context_ingredients)),
                        DataFieldTopic(context.getString(R.string.str_position), DataTypeTopic.INPUT.name, null, context.getString(R.string.str_context_potion))
                    ))
                }

                DetailTopicData.WESTERN_DISHES -> {
                    addSubTopic(it, arrayListOf(
                        DataFieldTopic(context.getString(R.string.str_dishes), DataTypeTopic.INPUT.name, null, context.getString(R.string.str_context_dishes)),
                        DataFieldTopic(context.getString(R.string.str_techniques), DataTypeTopic.SELECT.name, context.resources.getStringArray(R.array.arr_techniques_cooking).toList(), context.getString(R.string.str_context_techniques)),
                        DataFieldTopic(context.getString(R.string.str_ingredients), DataTypeTopic.INPUT.name, null, context.getString(R.string.str_context_ingredients)),
                        DataFieldTopic(context.getString(R.string.str_position), DataTypeTopic.INPUT.name, null, context.getString(R.string.str_context_potion))
                    ))
                }

                DetailTopicData.TRAVEL_DESTINATION -> {
                    addSubTopic(it, arrayListOf(
                        DataFieldTopic(context.getString(R.string.str_information), DataTypeTopic.MULTI_SELECT.name, context.resources.getStringArray(R.array.arr_information_travel).toList(), context.getString(R.string.str_context_information_travel)),
                        DataFieldTopic(context.getString(R.string.str_country_region), DataTypeTopic.SELECT.name, listAllCountris, context.getString(R.string.str_context_country))
                    ))
                }

                DetailTopicData.TRAVEL_EXPERIENCES -> {
                    addSubTopic(it, arrayListOf(
                        DataFieldTopic(context.getString(R.string.str_information), DataTypeTopic.MULTI_SELECT.name, context.resources.getStringArray(R.array.arr_information_travel).toList(), context.getString(R.string.str_context_information_travel)),
                        DataFieldTopic(context.getString(R.string.str_country_region), DataTypeTopic.SELECT.name, listAllCountris, context.getString(R.string.str_context_country))
                    ))
                }

                else -> {}
            }
        }
    }
    fun addSubTopic( subTopic: String ? = null, listOption: ArrayList<DataFieldTopic> ? = null) {
        listSubHaveOption.add(OptionSubTopic( subTopic, listOption))
    }
}

data class OptionSubTopic(
    val subTopic: String ? = null,
    val listOption: ArrayList<DataFieldTopic> ? = null
)

@Parcelize
data class DataFieldTopic(
    val field1: String,
    val dataType: String,
    val option: List<String> ? = null,
    val context: String ? =  null
):Parcelable