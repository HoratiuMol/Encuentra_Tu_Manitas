package com.example.encuentratumanitas

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

// ⚠️ Reemplaza estos valores con los de tu proyecto en supabase.com
//    Dashboard → Settings → API
const val SUPABASE_URL = "https://rmmrkhptvzcnnohsnchq.supabase.co"
const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJtbXJraHB0dnpjbm5vaHNuY2hxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzMwNjA2NTEsImV4cCI6MjA4ODYzNjY1MX0.m-EmHwlhD2vZUv91wVdFm6gCQkZHsIzhacyKPfvBd-U"

val supabaseClient = createSupabaseClient(
    supabaseUrl = SUPABASE_URL,
    supabaseKey = SUPABASE_ANON_KEY
) {
    install(Auth)        // autenticación (reemplaza a GoTrue)
    install(Postgrest)   // base de datos
    install(Storage)     // archivos / fotos
}