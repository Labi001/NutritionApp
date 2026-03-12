package com.labinot.bajrami.nutritionapp.domain.helpers


import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage

//class SupabaseStorageUtils (val context: Context){
//
//    val supabase = createSupabaseClient(
//        "https://uvzzqkmlmogynxenlwds.supabase.co",
//        "sb_publishable_M_QSce0RY9WpC7fmg-Yn4w_QfeEa_Gd"
//    ) {
//
//
//        install(Storage)
//
//    }
//
//    suspend fun uploadImage(uri: Uri): String?{
//
//        try {
//
//            val extension = uri.path?.substringAfter(".") ?: "jpg"
//            val fileName = "${UUID.randomUUID()}.$extension"
//            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
//
//            supabase.storage.from(BUCKET_NAME).upload(fileName, inputStream.readBytes())
//
//            val publicUrl = supabase.storage.from(BUCKET_NAME).publicUrl(fileName)
//            return publicUrl
//
//        }catch (e: Exception) {
//
//            e.printStackTrace()
//            return null
//
//        }
//
//
//    }
//
//    companion object {
//
//        const val BUCKET_NAME = "NutrionAppImages"
//    }
//
//}