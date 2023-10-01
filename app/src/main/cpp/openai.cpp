#include <jni.h>
#include <string>
#include "include/PtkScan.h"

using namespace ptk_scan;

std::string input[66] =
        {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
         "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
         "u", "v", "w", "x", "y", "z", "1", "2", "3", "4",
         "5", "6", "7", "8", "9", "0", ",", ".", "?",
         "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
         "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
         "U", "V", "W", "X", "Y", "Z", "-"};

std::string output[66] =
        {".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---",
         "-.-", ".-..", "--", "-.", "---", ".---.", "--.-", ".-.", "...", "-",
         "..-", "...-", ".--", "-..-", "-.--", "--..", ".----", "..---", "...--",
         "....-",
         ".....", "-....", "--...", "---..", "----.", "-----", "--..--", ".-.-.-",
         "..--..",
         ".-_", "-..._", "-.-._", "-.._", "._", "..-._", "--._", "...._", ".._", ".---_",
         "-.-_", ".-.._", "--_", "-._", "---_", ".---._", "--.-_", ".-._", "..._", "-_",
         "....._", "-...._", "--..._", "---.._", "----._", "-----_", ".__."};

char arrayNum[4] = {'1', '3', '5', '7'};
char arrayNum2[4] = {'2', '4', '6', '8'};

const char *name = "com.longkd.chatgpt_openai";

const char *classPath = "com/longkd/chatgpt_openai/base/OpenAIHolder";
const char *tKey = "12440803720630770375018460699408106315806553806994070717041538061117";
const char *cKey = "58480805540210310737014880646013304771303380274801510788402104582";
const char *vPrivate = "KEYG4UvGzTEelwojeTZbrpE0OD7es4Te7fRskdzBI2EsIfQV152ztJvPw9O50Odk";

PtkScan::~PtkScan() = default;

static jboolean auth = JNI_FALSE;

jstring getStringData(JNIEnv *pEnv, jstring pJstring);

std::string stringToNumber(const char i);

std::string reStringToNumber(const char i);

jstring convertKeyToString(JNIEnv *pEnv, jstring pJstring);

jobject getCipher(JNIEnv *pEnv, jstring encrypt, jint mode);

jstring createKey(JNIEnv *env, jstring encpt);

jobject getApplicationContext(JNIEnv *env) {
    jclass activityThread = env->FindClass("android/app/ActivityThread");
    jmethodID currentActivityThread = env->GetStaticMethodID(activityThread,
                                                             "currentActivityThread",
                                                             "()Landroid/app/ActivityThread;");
    jobject at = env->CallStaticObjectMethod(activityThread, currentActivityThread);
    jmethodID getApplication = env->GetMethodID(activityThread, "getApplication",
                                                "()Landroid/app/Application;");
    env->DeleteLocalRef(activityThread);
    return env->CallObjectMethod(at, getApplication);
}

jstring getStringData(JNIEnv *env, jstring data) {
    jclass stringClass = env->FindClass("java/lang/String");
    jmethodID stringClassMId = env->GetMethodID(stringClass, "substring",
                                                "(II)Ljava/lang/String;");
    jstring result = env->NewStringUTF("");
    jsize length = env->GetStringLength(data);
    if (length >= 16) {
        result = (jstring) env->CallObjectMethod(data, stringClassMId, length - 16, length);
    } else if (length >= 32) {
        result = (jstring) env->CallObjectMethod(data, stringClassMId, length - 32, length);
    }
    env->DeleteLocalRef(stringClass);
    return result;
}

std::string convertCharToKey(char data) {
    std::string sData = "";
    sData += data;
    int sFind = std::distance(input, std::find(input, input + 66, sData));
    return output[sFind];
}

std::string convertKeyToChar(char data) {
    std::string sData = "";
    sData += data;
    int sFind = std::distance(output, std::find(output, input + 66, sData));
    return input[sFind];
}

std::string convertKeyToChar1(std::string data) {
    int sFind = std::distance(output, std::find(output, output + 66, data));
    return input[sFind];
}

jstring convertStringToKey(JNIEnv *env, jstring data) {
    std::string result = "";
    std::string resultNumber = "";

    const char *mystring = env->GetStringUTFChars(data, 0);
    size_t strLen = strlen(mystring);
    for (size_t i = 0; i < strLen; i++) {
        result += convertCharToKey(mystring[i]);
        if (i < strLen - 1)
            result += "0";
    }
    const char *res = result.c_str();
    size_t resLen = strlen(res);
    for (size_t i = 0; i < resLen; i++) {
        resultNumber = resultNumber + stringToNumber(res[i]);
    }
    res = resultNumber.c_str();


//    return convertKeyToString(env, env->NewStringUTF(res));
    return env->NewStringUTF(res);
}

jstring convertKeyToString(JNIEnv *env, jstring data) {
    std::string result = "";
    std::string resultString = "";

    const char *mystring = env->GetStringUTFChars(data, 0);

    size_t strLen = strlen(mystring);
    for (size_t i = 0; i < strLen; i++) {
        result += reStringToNumber(mystring[i]);
    }
    char *res = strdup(result.c_str());
    char *token = strtok(res, "0");

    std::string del = "0";
    int start, end = -1 * del.size();
    do {
        start = end + del.size();
        end = result.find(del, start);
        resultString += convertKeyToChar1(result.substr(start, end - start));
    } while (end != -1);

    return env->NewStringUTF(resultString.c_str());
}

std::string stringToNumber(const char condition) {
    std::string ss = "0";
    int raIn = rand() % 4;

    switch (condition) {
        case '.':
            ss = arrayNum2[raIn];
            break;
        case '-':
            ss = arrayNum[raIn];
            break;
        case '_':
            ss = "9";
            break;
    }
    return ss;
}

std::string reStringToNumber(const char condition) {
    std::string ss = "0";
    if (condition == '0')
        return "0";
    int num = condition - '0';
    if (num == 9) {
        ss = "_";
    } else if (num % 2 == 0) {
        ss = ".";
    } else {
        ss = "-";
    }

    return ss;
}

jobject getCipher(JNIEnv *env, jstring encrypt, jint mode) {
    jclass cipherClass = env->FindClass("javax/crypto/Cipher");
    jmethodID getInstanceMId = env->GetStaticMethodID(cipherClass, "getInstance",
                                                      "(Ljava/lang/String;)Ljavax/crypto/Cipher;");
    jstring transformation = env->NewStringUTF("AES/ECB/PKCS5Padding");
    jobject cipher = env->CallStaticObjectMethod(cipherClass, getInstanceMId, transformation);
    jmethodID initMId = env->GetMethodID(cipherClass, "init",
                                         "(ILjava/security/Key;)V");
    jclass secretKeySpecClass = env->FindClass("javax/crypto/spec/SecretKeySpec");
    jmethodID secretKeySpecConstructor = env->GetMethodID(secretKeySpecClass, "<init>",
                                                          "([BLjava/lang/String;)V");
    jstring algorithm = env->NewStringUTF("AES");

    jclass stringClass = env->FindClass("java/lang/String");

    jstring data = getStringData(env, convertKeyToString(env, encrypt));
    //
    jmethodID getBytesMId = env->GetMethodID(stringClass, "getBytes", "()[B");
    jbyteArray keyBytes = (jbyteArray) env->CallObjectMethod(data, getBytesMId);
    jobject secretKeySpec = env->NewObject(secretKeySpecClass, secretKeySpecConstructor, keyBytes,
                                           algorithm);
    env->CallVoidMethod(cipher, initMId, mode, secretKeySpec);
    env->DeleteLocalRef(cipherClass);
    env->DeleteLocalRef(secretKeySpecClass);
    env->DeleteLocalRef(stringClass);
    return cipher;
}

jint baseFlag() {
    try {
        std::string const &str = "0";
        char const *digits = "0123456789";
        std::size_t const n = str.find_first_of(digits);
        if (n != std::string::npos) {
            std::size_t const m = str.find_first_not_of(digits, n);
        }
    } catch (std::exception e) {

    }
    return 2;
}

jstring getKey(JNIEnv *env, jstring encpt, jstring key) {
    try {

        jclass base64Class = env->FindClass("android/util/Base64");
        jmethodID decodeMId = env->GetStaticMethodID(base64Class, "decode",
                                                     "(Ljava/lang/String;I)[B");
        jbyteArray encryptedBase64Bytes = (jbyteArray) env->CallStaticObjectMethod(base64Class,
                                                                                   decodeMId,
                                                                                   encpt,
                                                                                   baseFlag());

        jobject cir = getCipher(env, key, 2);
        jclass cipherClass = env->FindClass("javax/crypto/Cipher");
        jmethodID doFinalMId = env->GetMethodID(cipherClass, "doFinal", "([B)[B");

        jobject decryptedBytes = env->CallObjectMethod(
                cir, doFinalMId, encryptedBase64Bytes);

        jclass stringCl = env->FindClass("java/lang/String");
        jclass charsetCl = env->FindClass("java/nio/charset/Charset");
        jmethodID charsetMID = env->GetStaticMethodID(charsetCl, "forName",
                                                      "(Ljava/lang/String;)Ljava/nio/charset/Charset;");
        jmethodID stringMID = env->GetMethodID(stringCl, "<init>",
                                               "([BLjava/nio/charset/Charset;)V");

        jobject charsetValue = (jstring) env->CallStaticObjectMethod(charsetCl,
                                                                     charsetMID,
                                                                     env->NewStringUTF("UTF-8"));
        jstring dt = (jstring) env->NewObject(stringCl, stringMID, decryptedBytes,
                                              charsetValue);

        env->DeleteLocalRef(base64Class);
        env->DeleteLocalRef(cipherClass);
        env->DeleteLocalRef(stringCl);
        env->DeleteLocalRef(charsetCl);
        return dt;
    } catch (std::exception e) {
        return env->NewStringUTF("");
    }

}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_getResponse(JNIEnv *env, jobject thiz,
                                                               jobject chain, jstring token,
                                                               jint type) {
    if (auth != JNI_TRUE) {
        PtkScan::blk();
    }
    jclass requestClass = env->FindClass("okhttp3/Request");
    jclass chainClass = env->FindClass("okhttp3/Interceptor$Chain");
    jmethodID chainFuncId = env->GetMethodID(chainClass, "request",
                                             "()Lokhttp3/Request;");
    jobject request = env->CallObjectMethod(chain, chainFuncId);

    jmethodID requestBuilderID = env->GetMethodID(requestClass, "newBuilder",
                                                  "()Lokhttp3/Request$Builder;");
    jobject requestBuilder = env->CallObjectMethod(request, requestBuilderID);

    //
    jclass requestBuilderClass = env->FindClass("okhttp3/Request$Builder");
    jmethodID headerID = env->GetMethodID(requestBuilderClass, "header",
                                          "(Ljava/lang/String;Ljava/lang/String;)Lokhttp3/Request$Builder;");
    std::string key1 = "longkd";

    std::string timeValue = env->GetStringUTFChars(token, 0);

    std::string a = "Bearer ";
    std::string tokenValue = key1 + env->GetStringUTFChars(getKey(env,
                                                                  env->NewStringUTF(vPrivate),
                                                                  env->NewStringUTF(cKey)), 0)
                             + key1 + timeValue + key1;
    jstring key = createKey(env, env->NewStringUTF(tokenValue.c_str()));
    jclass serviceClass = env->FindClass("com/longkd/chatgpt_openai/open/client/OpenAiUtils");
    jmethodID methodID = env->GetStaticMethodID(serviceClass, "padStart",
                                                "(Ljava/lang/String;IC)Ljava/lang/String;");
    jstring endValue = (jstring) env->CallStaticObjectMethod(serviceClass, methodID, key, 32, '0');
    std::string kk = a + env->GetStringUTFChars(endValue, 0);


    env->CallObjectMethod(requestBuilder,
                          headerID, env->NewStringUTF("Authorization"),
                          env->NewStringUTF(kk.c_str()));
//
    jmethodID buildID = env->GetMethodID(requestBuilderClass, "build",
                                         "()Lokhttp3/Request;");
    jobject rq = env->CallObjectMethod(requestBuilder,
                                       buildID);
    jmethodID proceedID = env->GetMethodID(chainClass, "proceed",
                                           "(Lokhttp3/Request;)Lokhttp3/Response;");
    env->DeleteLocalRef(requestClass);
    env->DeleteLocalRef(chainClass);
    env->DeleteLocalRef(requestBuilderClass);

    return env->CallObjectMethod(chain, proceedID, rq);
}

bool PtkScan::blk() {
    throw std::runtime_error("Not valid");
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_initLib(JNIEnv *env, jobject thiz) {

    jclass binderClass = env->FindClass("android/os/Binder");
    jclass contextClass = env->FindClass("android/content/Context");
    jclass signatureClass = env->FindClass("android/content/pm/Signature");
    jclass packageNameClass = env->FindClass("android/content/pm/PackageManager");
    jclass packageInfoClass = env->FindClass("android/content/pm/PackageInfo");

    jmethodID packageManager = env->GetMethodID(contextClass, "getPackageManager",
                                                "()Landroid/content/pm/PackageManager;");
    jmethodID packageName = env->GetMethodID(contextClass, "getPackageName",
                                             "()Ljava/lang/String;");
    jmethodID toCharsString = env->GetMethodID(signatureClass, "toCharsString",
                                               "()Ljava/lang/String;");
    jmethodID packageInfo = env->GetMethodID(packageNameClass, "getPackageInfo",
                                             "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    jmethodID nameForUid = env->GetMethodID(packageNameClass, "getNameForUid",
                                            "(I)Ljava/lang/String;");
    jmethodID callingUid = env->GetStaticMethodID(binderClass, "getCallingUid", "()I");

    jint uid = env->CallStaticIntMethod(binderClass, callingUid);
    jobject context = getApplicationContext(env);

    jobject packageManagerObject = env->CallObjectMethod(context, packageManager);
    jstring packNameString = (jstring) env->CallObjectMethod(context, packageName);
    jobject packageInfoObject = env->CallObjectMethod(packageManagerObject, packageInfo,
                                                      packNameString, 64);
    jfieldID signaturefieldID = env->GetFieldID(packageInfoClass, "signatures",
                                                "[Landroid/content/pm/Signature;");
    jobjectArray signatureArray = (jobjectArray) env->GetObjectField(packageInfoObject,
                                                                     signaturefieldID);
    jobject signatureObject = env->GetObjectArrayElement(signatureArray, 0);
    jstring runningPackageName = (jstring) env->CallObjectMethod(packageManagerObject, nameForUid,
                                                                 uid);
    env->DeleteLocalRef(binderClass);
    env->DeleteLocalRef(contextClass);
    env->DeleteLocalRef(signatureClass);
    env->DeleteLocalRef(packageNameClass);
    env->DeleteLocalRef(packageInfoClass);
    if (runningPackageName) {
        const char *charPackageName = env->GetStringUTFChars(runningPackageName, 0);
        if (strcmp(charPackageName, name) != 0) {
            PtkScan::blk();
            auth = JNI_FALSE;
            return JNI_FALSE;
        }
        env->ReleaseStringUTFChars(runningPackageName, charPackageName);
        auth = JNI_TRUE;
        return JNI_TRUE;
    } else {
        PtkScan::blk();
        auth = JNI_FALSE;
        return JNI_FALSE;
    }

    jstring signatureStr = (jstring) env->CallObjectMethod(signatureObject, toCharsString);
    const char *signature = env->GetStringUTFChars(
            (jstring) env->CallObjectMethod(signatureObject, toCharsString), NULL);

    env->DeleteLocalRef(binderClass);
    env->DeleteLocalRef(contextClass);
    env->DeleteLocalRef(signatureClass);
    env->DeleteLocalRef(packageNameClass);
    env->DeleteLocalRef(packageInfoClass);

//    if (strcmp(signature, rhs) == 0) {
//        env->ReleaseStringUTFChars(signatureStr, signature);
//        auth = JNI_TRUE;
//        return JNI_TRUE;
//    } else {
//        auth = JNI_FALSE;
//        return JNI_FALSE;
//    }
}

static JNINativeMethod registerMethods[] = {
        {"initLib", "()Z", (jboolean *) Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_initLib},
};


static int registerNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *gMethods,
                                 int numMethods) {
    jclass clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        env->DeleteLocalRef(clazz);
        return JNI_FALSE;
    }
    env->DeleteLocalRef(clazz);
    return JNI_TRUE;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {

    JNIEnv *env = NULL;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }

    if (!registerNativeMethods(env, classPath, registerMethods,
                               sizeof(registerMethods) / sizeof(registerMethods[0]))) {
        PtkScan::blk();
        return -1;
    }
    return JNI_VERSION_1_6;
}

jint getNumberFreeChat(JNIEnv *env,
                       jobject shared_preferences) {
    jclass jcSharedPreferences = env->FindClass("android/content/SharedPreferences");

    jmethodID getStringID = env->GetMethodID(jcSharedPreferences, "getInt",
                                             "(Ljava/lang/String;I)I");
    jint value = env->CallIntMethod(shared_preferences, getStringID,
                                    env->NewStringUTF("number_free_chat"), 5);
    env->DeleteLocalRef(jcSharedPreferences);
    return value;
}

jint getNumberFreeGenerate(JNIEnv *env,
                       jobject shared_preferences) {
    jclass jcSharedPreferences = env->FindClass("android/content/SharedPreferences");

    jmethodID getStringID = env->GetMethodID(jcSharedPreferences, "getInt",
                                             "(Ljava/lang/String;I)I");
    jint value = env->CallIntMethod(shared_preferences, getStringID,
                                    env->NewStringUTF("number_free_generate_art"), 2);
    env->DeleteLocalRef(jcSharedPreferences);
    return value;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_getKey(JNIEnv *env, jobject thiz, jstring key) {
    try {

        jclass mdClass = env->FindClass("java/security/MessageDigest");
        jmethodID instanceMId = env->GetStaticMethodID(mdClass, "getInstance",
                                                       "(Ljava/lang/String;)Ljava/security/MessageDigest;");
        jmethodID mdDigestMId = env->GetMethodID(mdClass, "digest",
                                                 "([B)[B");
        jobject mdObj = env->CallStaticObjectMethod(mdClass, instanceMId, env->NewStringUTF("MD5"));

        std::string key1 = "longkd703DF45C-8EBB-4EE6-AB2D-321805677690longkd";
        std::string key2 = "longkd";
        std::string timeValue = env->GetStringUTFChars(key, 0);

        std::string tokenValue = key1 + timeValue + key2;
        jstring key = env->NewStringUTF(tokenValue.c_str());

        jclass stringCl = env->FindClass("java/lang/String");
        jclass charsetCl = env->FindClass("java/nio/charset/Charset");
        jmethodID charsetMID = env->GetStaticMethodID(charsetCl, "forName",
                                                      "(Ljava/lang/String;)Ljava/nio/charset/Charset;");
        jmethodID stringMID = env->GetMethodID(stringCl, "getBytes",
                                               "(Ljava/nio/charset/Charset;)[B");

        jobject charsetValue = (jstring) env->CallStaticObjectMethod(charsetCl,
                                                                     charsetMID,
                                                                     env->NewStringUTF("UTF-8"));
        jbyteArray dt = (jbyteArray) env->CallObjectMethod(key, stringMID,
                                                           charsetValue);
        jbyteArray var10004 = (jbyteArray) env->CallObjectMethod(mdObj, mdDigestMId, dt);


        jclass bigClass = env->FindClass("java/math/BigInteger");

        jmethodID bigClassMId = env->GetMethodID(bigClass, "<init>",
                                                 "(I[B)V");
        jobject bigObj = env->NewObject(bigClass, bigClassMId, 1, var10004);


        jmethodID bigStringMId = env->GetMethodID(bigClass, "toString",
                                                  "(I)Ljava/lang/String;");

        jstring var10000 = (jstring) env->CallObjectMethod(bigObj, bigStringMId, 16);
        jclass serviceClass = env->FindClass("com/longkd/chatgpt_openai/open/client/OpenAiUtils");
        jmethodID methodID = env->GetStaticMethodID(serviceClass, "padStart",
                                                    "(Ljava/lang/String;IC)Ljava/lang/String;");
        jstring endValue = (jstring) env->CallStaticObjectMethod(serviceClass, methodID, var10000,
                                                                 32, '0');
        env->DeleteLocalRef(mdClass);
        env->DeleteLocalRef(stringCl);
        env->DeleteLocalRef(bigClass);
        env->DeleteLocalRef(charsetCl);
        return endValue;
    } catch (std::exception e) {
        return env->NewStringUTF("");
    }
}

jint getNumberChatReset(JNIEnv *env,
                        jobject shared_preferences) {
    jclass jcSharedPreferences = env->FindClass("android/content/SharedPreferences");

    jmethodID getStringID = env->GetMethodID(jcSharedPreferences, "getInt",
                                             "(Ljava/lang/String;I)I");
    jint value = env->CallIntMethod(shared_preferences, getStringID,
                                    env->NewStringUTF("number_chat_reset"), 2);
    env->DeleteLocalRef(jcSharedPreferences);
    return value;
}

jint getNumberRewarded(JNIEnv *env,
                       jint numberReward,
                       jobject shared_preferences) {
    jclass jcSharedPreferences = env->FindClass("android/content/SharedPreferences");

    jmethodID getStringID = env->GetMethodID(jcSharedPreferences, "getInt",
                                             "(Ljava/lang/String;I)I");
    jint value = env->CallIntMethod(shared_preferences, getStringID,
                                    env->NewStringUTF("number_rewarded"), numberReward);
    env->DeleteLocalRef(jcSharedPreferences);
    return value;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_getChatFreeMessage(JNIEnv *env, jobject thiz,
                                                                      jint type,
                                                                      jobject shared_preferences) {
    jint numberChatReset = getNumberFreeChat(env, shared_preferences);
    jclass jcSharedPreferences = env->FindClass("android/content/SharedPreferences");
    jmethodID getStringID = env->GetMethodID(jcSharedPreferences, "getInt",
                                             "(Ljava/lang/String;I)I");
    jint value = env->CallIntMethod(shared_preferences, getStringID,
                                    env->NewStringUTF("free_mess_normal"), numberChatReset);
    jint valuePro = env->CallIntMethod(shared_preferences, getStringID,
                                       env->NewStringUTF("free_mess_pro"), numberChatReset);
    env->DeleteLocalRef(jcSharedPreferences);
    if (type == 0)
        return value;
    else return valuePro;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_getFreeNumberGenerate(JNIEnv *env, jobject thiz,
                                                                         jobject shared_preferences) {
    jclass jcSharedPreferences = env->FindClass("android/content/SharedPreferences");
    jmethodID getStringID = env->GetMethodID(jcSharedPreferences, "getInt",
                                             "(Ljava/lang/String;I)I");
    jint numberFreeGenerate = getNumberFreeGenerate(env, shared_preferences);
    jint value = env->CallIntMethod(shared_preferences, getStringID,
                                    env->NewStringUTF("free_number_generate"), numberFreeGenerate);
    env->DeleteLocalRef(jcSharedPreferences);
    return value;
}

void putFreeMessage(JNIEnv *env, jstring key, jint value, jobject shared_preference) {
    jclass spcls = env->FindClass("android/content/SharedPreferences");
    jclass speditorcls = env->FindClass("android/content/SharedPreferences$Editor");

    jmethodID midedit = env->GetMethodID(spcls, "edit",
                                         "()Landroid/content/SharedPreferences$Editor;");

    jmethodID midputbool = env->GetMethodID(speditorcls, "putInt",
                                            "(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;");

    jmethodID midapply = env->GetMethodID(speditorcls, "apply",
                                          "()V");

    jobject jobjectsharededit = env->CallObjectMethod(shared_preference, midedit);
    env->CallVoidMethod(env->CallObjectMethod(jobjectsharededit, midputbool, key, value),
                        midapply);

    env->DeleteLocalRef(spcls);
    env->DeleteLocalRef(speditorcls);
}

jboolean getVMem(JNIEnv *env,
                 jobject shared_preferences) {
    jclass jcSharedPreferences = env->FindClass("android/content/SharedPreferences");

    jmethodID getStringID = env->GetMethodID(jcSharedPreferences, "getBoolean",
                                             "(Ljava/lang/String;Z)Z");
    jboolean value = env->CallBooleanMethod(shared_preferences, getStringID,
                                            env->NewStringUTF("KEY_APP_PURCHASE"), false);
    env->DeleteLocalRef(jcSharedPreferences);
    return value;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_callCompletion(JNIEnv *env, jobject thiz,
                                                                  jint type,
                                                                  jstring message, jobject service,
                                                                  jobject request,
                                                                  jobject shared_preference,
                                                                  jobject vip_shared_preference) {
    jint mesNum = Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_getChatFreeMessage(env, thiz,
                                                                                        type,
                                                                                        shared_preference);
    if (auth != JNI_TRUE) {
        return nullptr;
    }
    if (getVMem(env, vip_shared_preference) != true) {
        if (mesNum <= 0)
            return nullptr;
    }
    jclass serviceClass = env->FindClass("com/longkd/chatgpt_openai/open/client/OpenAiService");
    jmethodID methodID = env->GetMethodID(serviceClass, "getCompletionsBm",
                                          "(Lcom/longkd/chatgpt_openai/open/dto/completion/CompletionRequest;)Lcom/longkd/chatgpt_openai/open/dto/completion/CompletionResult;");

    env->DeleteLocalRef(serviceClass);

    if (getVMem(env, vip_shared_preference) != true) {
        jstring key;
        if (type == 0) key = env->NewStringUTF("free_mess_normal");
        else key = env->NewStringUTF("free_mess_pro");
        putFreeMessage(env, key, mesNum - 1, shared_preference);
    }
    return env->CallObjectMethod(service, methodID, request);

}


jstring createKey(JNIEnv *env, jstring encpt) {
    try {

        jclass mdClass = env->FindClass("java/security/MessageDigest");
        jmethodID instanceMId = env->GetStaticMethodID(mdClass, "getInstance",
                                                       "(Ljava/lang/String;)Ljava/security/MessageDigest;");
        jmethodID mdDigestMId = env->GetMethodID(mdClass, "digest",
                                                 "([B)[B");
        jobject mdObj = env->CallStaticObjectMethod(mdClass, instanceMId, env->NewStringUTF("MD5"));


        jclass stringCl = env->FindClass("java/lang/String");
        jclass charsetCl = env->FindClass("java/nio/charset/Charset");
        jmethodID charsetMID = env->GetStaticMethodID(charsetCl, "forName",
                                                      "(Ljava/lang/String;)Ljava/nio/charset/Charset;");
        jmethodID stringMID = env->GetMethodID(stringCl, "getBytes",
                                               "(Ljava/nio/charset/Charset;)[B");

        jobject charsetValue = (jstring) env->CallStaticObjectMethod(charsetCl,
                                                                     charsetMID,
                                                                     env->NewStringUTF("UTF-8"));
        jbyteArray dt = (jbyteArray) env->CallObjectMethod(encpt, stringMID,
                                                           charsetValue);
        jbyteArray var10004 = (jbyteArray) env->CallObjectMethod(mdObj, mdDigestMId, dt);


        jclass bigClass = env->FindClass("java/math/BigInteger");

        jmethodID bigClassMId = env->GetMethodID(bigClass, "<init>",
                                                 "(I[B)V");
        jobject bigObj = env->NewObject(bigClass, bigClassMId, 1, var10004);


        jmethodID bigStringMId = env->GetMethodID(bigClass, "toString",
                                                  "(I)Ljava/lang/String;");
        jstring var10000 = (jstring) env->CallObjectMethod(bigObj, bigStringMId, 16);


        env->DeleteLocalRef(mdClass);
        env->DeleteLocalRef(stringCl);
        env->DeleteLocalRef(bigClass);
        env->DeleteLocalRef(charsetCl);
        return var10000;
    } catch (std::exception e) {
        return env->NewStringUTF("");
    }

}


jstring createKeyA(JNIEnv *env, jstring encpt) {
    try {

        jclass mdClass = env->FindClass("java/security/MessageDigest");
        jmethodID instanceMId = env->GetStaticMethodID(mdClass, "getInstance",
                                                       "(Ljava/lang/String;)Ljava/security/MessageDigest;");
        jmethodID mdDigestMId = env->GetMethodID(mdClass, "digest",
                                                 "([B)[B");
        jobject mdObj = env->CallStaticObjectMethod(mdClass, instanceMId, env->NewStringUTF("MD5"));

        std::string key1 = "longkd703DF45C-8EBB-4EE6-AB2D-321805677690longkd";
        std::string key2 = "longkd";
        std::string timeValue = env->GetStringUTFChars(encpt, 0);

        std::string tokenValue = key1 + timeValue + key2;
        jstring key = env->NewStringUTF(tokenValue.c_str());

        jclass stringCl = env->FindClass("java/lang/String");
        jclass charsetCl = env->FindClass("java/nio/charset/Charset");
        jmethodID charsetMID = env->GetStaticMethodID(charsetCl, "forName",
                                                      "(Ljava/lang/String;)Ljava/nio/charset/Charset;");
        jmethodID stringMID = env->GetMethodID(stringCl, "getBytes",
                                               "(Ljava/nio/charset/Charset;)[B");

        jobject charsetValue = (jstring) env->CallStaticObjectMethod(charsetCl,
                                                                     charsetMID,
                                                                     env->NewStringUTF("UTF-8"));
        jbyteArray dt = (jbyteArray) env->CallObjectMethod(key, stringMID,
                                                           charsetValue);
        jbyteArray var10004 = (jbyteArray) env->CallObjectMethod(mdObj, mdDigestMId, dt);


        jclass bigClass = env->FindClass("java/math/BigInteger");

        jmethodID bigClassMId = env->GetMethodID(bigClass, "<init>",
                                                 "(I[B)V");
        jobject bigObj = env->NewObject(bigClass, bigClassMId, 1, var10004);


        jmethodID bigStringMId = env->GetMethodID(bigClass, "toString",
                                                  "(I)Ljava/lang/String;");

        jstring var10000 = (jstring) env->CallObjectMethod(bigObj, bigStringMId, 16);
        jclass serviceClass = env->FindClass("com/longkd/chatgpt_openai/open/client/OpenAiUtils");
        jmethodID methodID = env->GetStaticMethodID(serviceClass, "padStart",
                                                    "(Ljava/lang/String;IC)Ljava/lang/String;");
        jstring endValue = (jstring) env->CallStaticObjectMethod(serviceClass, methodID, var10000,
                                                                 32, '0');
        env->DeleteLocalRef(mdClass);
        env->DeleteLocalRef(stringCl);
        env->DeleteLocalRef(bigClass);
        env->DeleteLocalRef(charsetCl);
        return endValue;
    } catch (std::exception e) {
        return env->NewStringUTF("");
    }

}

extern "C"
JNIEXPORT void JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_verifyRewarded(JNIEnv *env, jobject thiz,
                                                                  jint type,
                                                                  jint numberReward,
                                                                  jobject shared_preferences) {
    jstring typeKey = env->NewStringUTF("free_mess");
    jint typeNum = getNumberRewarded(env, numberReward, shared_preferences);
    if (type == 0) {
        typeKey = env->NewStringUTF("free_mess_normal");
    }
    if (type == 1) {
        typeKey = env->NewStringUTF("free_mess_pro");
    }
    putFreeMessage(env, typeKey, typeNum, shared_preferences);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_resetFreeChat(JNIEnv *env, jobject thiz,

                                                                 jobject shared_preferences) {
    jint numberChatReset = getNumberChatReset(env, shared_preferences);
    jstring typeKey = env->NewStringUTF("free_mess");
    jint typeNum = 0;
    typeKey = env->NewStringUTF("free_mess_normal");
    typeNum = numberChatReset;
    putFreeMessage(env, typeKey, typeNum, shared_preferences);

    typeKey = env->NewStringUTF("free_mess_pro");
    typeNum = numberChatReset;
    putFreeMessage(env, typeKey, typeNum, shared_preferences);

}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_callCompletionOld(JNIEnv *env, jobject thiz,
                                                                     jint type,
                                                                     jstring message,
                                                                     jobject service,
                                                                     jobject request,
                                                                     jobject shared_preference) {
    jint mesNum = Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_getChatFreeMessage(env, thiz,
                                                                                        type,
                                                                                        shared_preference);
    if (auth != JNI_TRUE) {
        return nullptr;
    }
    if (getVMem(env, shared_preference) != true) {
        if (mesNum <= 0)
            return nullptr;
    }
    jclass serviceClass = env->FindClass("com/longkd/chatgpt_openai/open/client/OpenAiService");
    jmethodID methodID = env->GetMethodID(serviceClass, "createCompletion",
                                          "(Lcom/longkd/chatgpt_openai/open/dto/completion/CompletionRequest;)Lcom/longkd/chatgpt_openai/open/dto/completion/CompletionResult;");

    env->DeleteLocalRef(serviceClass);
    return env->CallObjectMethod(service, methodID, request);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_decreaseNumberFreeChat(JNIEnv *env, jobject thiz,
                                                                          jint type,
                                                                          jobject shared_preference,
                                                                          jobject vip_shared_preference) {
    jint mesNum = Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_getChatFreeMessage(env, thiz,
                                                                                        type,
                                                                                        shared_preference);
    if (auth != JNI_TRUE) {
        return;
    }
    if (getVMem(env, vip_shared_preference) != true) {
        if (mesNum <= 0)
            return;
    }
    jstring key;
    if (type == 0) key = env->NewStringUTF("free_mess_normal");
    else key = env->NewStringUTF("free_mess_pro");
    putFreeMessage(env, key, mesNum - 1, shared_preference);

}
extern "C"
JNIEXPORT void JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_decreaseNumberGenerate(JNIEnv *env, jobject thiz,
                                                                          jobject shared_preference,
                                                                          jobject vip_shared_preference) {
    jint mesNum = Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_getFreeNumberGenerate(env,
                                                                                           thiz,
                                                                                           shared_preference);
    if (auth != JNI_TRUE) {
        return;
    }
    if (getVMem(env, vip_shared_preference) != true) {
        if (mesNum <= 0)
            return;
    }
    jstring key = env->NewStringUTF("free_number_generate");;
    putFreeMessage(env, key, mesNum - 1, shared_preference);

}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_callGetTime(JNIEnv *env, jobject thiz,
                                                               jobject service) {
    if (auth != JNI_TRUE) {
        return nullptr;
    }
    jclass serviceClass = env->FindClass("com/longkd/chatgpt_openai/open/client/TimeStampService");
    jmethodID methodID = env->GetMethodID(serviceClass, "getTimeStamp",
                                          "()Lcom/longkd/chatgpt_openai/open/dto/completion/TokenDto;");

    env->DeleteLocalRef(serviceClass);
    return env->CallObjectMethod(service, methodID);
}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_getResponseOld(JNIEnv *env, jobject thiz,
                                                                  jobject chain, jstring token,
                                                                  jstring userAgent,
                                                                  jstring time_stamp,
                                                                  jint type) {
    if (auth != JNI_TRUE) {
        PtkScan::blk();
    }
    jclass requestClass = env->FindClass("okhttp3/Request");
    jclass chainClass = env->FindClass("okhttp3/Interceptor$Chain");
    jmethodID chainFuncId = env->GetMethodID(chainClass, "request",
                                             "()Lokhttp3/Request;");
    jobject request = env->CallObjectMethod(chain, chainFuncId);

    jmethodID requestBuilderID = env->GetMethodID(requestClass, "newBuilder",
                                                  "()Lokhttp3/Request$Builder;");
    jobject requestBuilder = env->CallObjectMethod(request, requestBuilderID);

    //
    jclass requestBuilderClass = env->FindClass("okhttp3/Request$Builder");
    jmethodID headerID = env->GetMethodID(requestBuilderClass, "header",
                                          "(Ljava/lang/String;Ljava/lang/String;)Lokhttp3/Request$Builder;");
    std::string a = "Bearer ";
    std::string b = (env)->GetStringUTFChars(
            createKeyA(env, time_stamp),
            0);
    std::string kk = a + b;

    jstring okk = getKey(env, env->NewStringUTF("Sc7P6bs4PC4Mid7Tx4ZRDNk1o9sGkyahSNTXT3HdRDY="),
                         env->NewStringUTF(
                                 "4203810110384606017206701107750526203160832801170748406508524"));

    env->CallObjectMethod(requestBuilder,
                          headerID, env->NewStringUTF("Authorization"),
                          env->NewStringUTF(kk.c_str()));
    env->CallObjectMethod(requestBuilder,
                          headerID, env->NewStringUTF("User-Agent"), userAgent);
    env->CallObjectMethod(requestBuilder,
                          headerID, env->NewStringUTF("timestamp"), time_stamp);
    env->CallObjectMethod(requestBuilder,
                          headerID, env->NewStringUTF("OpenAI-Organization"),
                          okk);
//
    jmethodID buildID = env->GetMethodID(requestBuilderClass, "build",
                                         "()Lokhttp3/Request;");
    jobject rq = env->CallObjectMethod(requestBuilder,
                                       buildID);
    jmethodID proceedID = env->GetMethodID(chainClass, "proceed",
                                           "(Lokhttp3/Request;)Lokhttp3/Response;");
    env->DeleteLocalRef(requestClass);
    env->DeleteLocalRef(chainClass);
    env->DeleteLocalRef(requestBuilderClass);

    return env->CallObjectMethod(chain, proceedID, rq);
}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_getTimeStampResponse(JNIEnv *env, jobject thiz,
                                                                        jobject chain,
                                                                        jstring userAgent) {
    if (auth != JNI_TRUE) {
        PtkScan::blk();
    }
    jclass requestClass = env->FindClass("okhttp3/Request");
    jclass chainClass = env->FindClass("okhttp3/Interceptor$Chain");
    jmethodID chainFuncId = env->GetMethodID(chainClass, "request",
                                             "()Lokhttp3/Request;");
    jobject request = env->CallObjectMethod(chain, chainFuncId);

    jmethodID requestBuilderID = env->GetMethodID(requestClass, "newBuilder",
                                                  "()Lokhttp3/Request$Builder;");
    jobject requestBuilder = env->CallObjectMethod(request, requestBuilderID);

    //
    jclass requestBuilderClass = env->FindClass("okhttp3/Request$Builder");
    jmethodID headerID = env->GetMethodID(requestBuilderClass, "header",
                                          "(Ljava/lang/String;Ljava/lang/String;)Lokhttp3/Request$Builder;");
    env->CallObjectMethod(requestBuilder,
                          headerID, env->NewStringUTF("User-Agent"), userAgent);
    jmethodID buildID = env->GetMethodID(requestBuilderClass, "build",
                                         "()Lokhttp3/Request;");
    jobject rq = env->CallObjectMethod(requestBuilder,
                                       buildID);
    jmethodID proceedID = env->GetMethodID(chainClass, "proceed",
                                           "(Lokhttp3/Request;)Lokhttp3/Response;");
    env->DeleteLocalRef(requestClass);
    env->DeleteLocalRef(chainClass);
    env->DeleteLocalRef(requestBuilderClass);

    return env->CallObjectMethod(chain, proceedID, rq);
}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_callCompletion35(JNIEnv *env, jobject thiz,
                                                                    jint type, jstring message,
                                                                    jobject service,
                                                                    jobject request,
                                                                    jobject shared_preferences) {
    jint mesNum = Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_getChatFreeMessage(env, thiz,
                                                                                        type,
                                                                                        shared_preferences);
    if (auth != JNI_TRUE) {
        return nullptr;
    }
    if (getVMem(env, shared_preferences) != true) {
        if (mesNum <= 0)
            return nullptr;
    }
    jclass serviceClass = env->FindClass("com/longkd/chatgpt_openai/open/client/OpenAiService");
    jmethodID methodID = env->GetMethodID(serviceClass, "createCompletion35",
                                          "(Lcom/longkd/chatgpt_openai/open/dto/completion/Completion35Request;)Lcom/longkd/chatgpt_openai/open/dto/completion/Completion35Result;");

    env->DeleteLocalRef(serviceClass);
    return env->CallObjectMethod(service, methodID, request);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_uploadSummaryFile(
        JNIEnv *env, jobject thiz,
        jobject service,
        jstring request,
        jobject shared_preferences
) {
    jint mesNum = Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_getChatFreeMessage(env, thiz,1, shared_preferences);
    if (auth != JNI_TRUE) {
        return nullptr;
    }
    if (getVMem(env, shared_preferences) != true) {
        if (mesNum <= 0)
            return nullptr;
    }

    jclass serviceClass = env->FindClass("com/longkd/chatgpt_openai/open/client/OpenAiService");
    jmethodID methodID = env->GetMethodID(serviceClass, "uploadSummaryFile",
                                          "(Ljava/lang/String;)Lcom/longkd/chatgpt_openai/base/model/SummaryFileResponse;");

    env->DeleteLocalRef(serviceClass);
    return env->CallObjectMethod(service, methodID, request);


}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_callCompletionMore(JNIEnv *env, jobject thiz,
                                                                      jint type, jstring message,
                                                                      jobject service,
                                                                      jobject request) {
    if (auth != JNI_TRUE) {
        return nullptr;
    }

    jclass serviceClass = env->FindClass("com/longkd/chatgpt_openai/open/client/OpenAiService");
    jmethodID methodID = env->GetMethodID(serviceClass, "createCompletion",
                                          "(Lcom/longkd/chatgpt_openai/open/dto/completion/CompletionRequest;)Lcom/longkd/chatgpt_openai/open/dto/completion/CompletionResult;");


    env->DeleteLocalRef(serviceClass);
    return env->CallObjectMethod(service, methodID, request);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_generateAiArt(JNIEnv *env, jobject thiz,
                                                                 jstring message,
                                                                 jobject service,
                                                                 jobject request,
                                                                 jobject shared_preferences) {
    jint mesNum = Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_getFreeNumberGenerate(env,
                                                                                           thiz,
                                                                                           shared_preferences);
    if (auth != JNI_TRUE) {
        return nullptr;
    }
    if (getVMem(env, shared_preferences) != true) {
        if (mesNum <= 0)
            return nullptr;
    }
    jclass serviceClass = env->FindClass("com/longkd/chatgpt_openai/open/client/OpenAiService");
    jmethodID methodID = env->GetMethodID(serviceClass, "generateImageArt",
                                          "(Lcom/longkd/chatgpt_openai/open/dto/generate/GenerateArtRequest;)Lcom/longkd/chatgpt_openai/open/dto/generate/GenerateArtResult;");

    env->DeleteLocalRef(serviceClass);
    return env->CallObjectMethod(service, methodID, request);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_getImageAiArt(JNIEnv *env, jobject thiz,
                                                                 jobject service,
                                                                 jobject request,
                                                                 jobject shared_preferences) {
    jint mesNum = Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_getFreeNumberGenerate(env,
                                                                                           thiz,
                                                                                           shared_preferences);
    if (mesNum <= 0)
        return nullptr;
    jclass serviceClass = env->FindClass("com/longkd/chatgpt_openai/open/client/OpenAiService");
    jmethodID methodID = env->GetMethodID(serviceClass, "createImageArt",
                                          "(Lcom/longkd/chatgpt_openai/open/dto/image_art/ImageArtRequest;)Lcom/longkd/chatgpt_openai/open/dto/image_art/ImageArtResult;");

    env->DeleteLocalRef(serviceClass);
    return env->CallObjectMethod(service, methodID, request);
}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_uploadSummaryText(JNIEnv *env, jobject thiz,
                                                                     jint type,
                                                                     jobject service,
                                                                     jobject request,
                                                                     jobject shared_preferences) {

    jclass serviceClass = env->FindClass("com/longkd/chatgpt_openai/open/client/OpenAiService");
    jmethodID methodID = env->GetMethodID(serviceClass, "uploadSummaryText",
                                          "(Lcom/longkd/chatgpt_openai/open/dto/completion/Completion35Request;)Lcom/longkd/chatgpt_openai/base/model/SummaryFileResponse;");

    env->DeleteLocalRef(serviceClass);
    return env->CallObjectMethod(service, methodID, request);
}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_completeSummaryChat(JNIEnv *env, jobject thiz,
                                                                       jint type, jstring message,
                                                                       jobject service,
                                                                       jobject request,
                                                                       jobject shared_preferences) {
    jint mesNum = Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_getChatFreeMessage(env, thiz,
                                                                                        type,
                                                                                        shared_preferences);
    if (auth != JNI_TRUE) {
        return nullptr;
    }
    if (getVMem(env, shared_preferences) != true) {
        if (mesNum <= 0)
            return nullptr;
    }
    jclass serviceClass = env->FindClass("com/longkd/chatgpt_openai/open/client/OpenAiService");
    jmethodID methodID = env->GetMethodID(serviceClass, "completeSummaryChat",
                                          "(Lcom/longkd/chatgpt_openai/open/dto/completion/Completion35Request;)Lcom/longkd/chatgpt_openai/open/dto/completion/Completion35Result;");

    env->DeleteLocalRef(serviceClass);
    return env->CallObjectMethod(service, methodID, request);
}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_completeTopicChat(JNIEnv *env, jobject thiz,
                                                                     jint type, jobject service,
                                                                     jobject request,
                                                                     jobject shared_preferences) {
    jint mesNum = Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_getChatFreeMessage(env, thiz,
                                                                                        type,
                                                                                        shared_preferences);
    if (auth != JNI_TRUE) {
        return nullptr;
    }
    if (getVMem(env, shared_preferences) != true) {
        if (mesNum <= 0)
            return nullptr;
    }
    jclass serviceClass = env->FindClass("com/longkd/chatgpt_openai/open/client/OpenAiService");
    jmethodID methodID = env->GetMethodID(serviceClass, "completeTopicChat",
                                          "(Lcom/longkd/chatgpt_openai/open/dto/completion/Completion35Request;)Lcom/longkd/chatgpt_openai/base/model/TopicResponse;");

    env->DeleteLocalRef(serviceClass);
    return env->CallObjectMethod(service, methodID, request);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_putNumberGenerate(JNIEnv *env, jobject thiz,
                                                                     jobject shared_preferences,
                                                                     jint number_generate) {
    jint mesNum = Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_getFreeNumberGenerate(env,
                                                                                           thiz,
                                                                                           shared_preferences);
    jstring typeKey = env->NewStringUTF("free_number_generate");
    jint typeNum =  mesNum + number_generate;
    putFreeMessage(env, typeKey, typeNum, shared_preferences);
}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_getImageStyle(JNIEnv *env, jobject thiz,
                                                                 jobject service, jstring folder) {
    jclass serviceClass = env->FindClass("com/longkd/chatgpt_openai/open/client/OpenAiService");
    jmethodID methodID = env->GetMethodID(serviceClass, "getListImageStyle",
                                          "(Ljava/lang/String;)Lcom/longkd/chatgpt_openai/base/model/ImageStyleResponse;");

    env->DeleteLocalRef(serviceClass);
    return env->CallObjectMethod(service, methodID, folder);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_longkd_chatgpt_1openai_base_OpenAIHolder_generateArtByVyro(JNIEnv *env, jobject thiz,
                                                                     jobject service,
                                                                     jobject request,
                                                                     jobject shared_preferences) {
    jclass serviceClass = env->FindClass("com/longkd/chatgpt_openai/open/client/OpenAiService");
    jmethodID methodID = env->GetMethodID(serviceClass, "generateArtByVyro",
                                          "(Lcom/longkd/chatgpt_openai/base/model/GenerateArtByVyroRequest;)Lcom/longkd/chatgpt_openai/base/model/GenerateArtResponse;");

    env->DeleteLocalRef(serviceClass);
    return env->CallObjectMethod(service, methodID, request);
}