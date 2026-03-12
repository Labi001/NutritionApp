package com.labinot.bajrami.nutritionapp.domain.helpers

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage

object SupabaseFactory {

    val client = createSupabaseClient(
        supabaseUrl = "https://uvzzqkmlmogynxenlwds.supabase.co",
        supabaseKey = "sb_publishable_M_QSce0RY9WpC7fmg-Yn4w_QfeEa_Gd"
    ) {
        install(Storage)
    }


}