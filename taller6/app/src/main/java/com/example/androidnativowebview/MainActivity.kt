package com.example.androidnativowebview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.androidnativowebview.databinding.ActivityMainBinding
import com.example.androidnativowebview.ui.home.HomeFragment
import com.example.androidnativowebview.ui.league.LeagueFragment
import com.example.androidnativowebview.ui.profile.ProfileFragment

/**
 * Taller 6 – Native UI Re-Engineering & UX Analysis
 * App: Duolingo Clone (Android Nativo – Kotlin)
 * Autora: Evelin Rocha
 *
 * Tres listas con RecyclerView + DiffUtil:
 *   1. HomeFragment     → Ruta de lecciones  (LessonAdapter)
 *   2. LeagueFragment   → Tabla de posiciones (LeaderboardAdapter)
 *   3. ProfileFragment  → Logros / Achievements (AchievementAdapter)
 *
 * Fase C – Mejora: barra de meta diaria siempre visible en el header.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cargar pantalla inicial
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            binding.bottomNav.selectedItemId = R.id.nav_home
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home    -> { loadFragment(HomeFragment());   true }
                R.id.nav_league  -> { loadFragment(LeagueFragment());  true }
                R.id.nav_profile -> { loadFragment(ProfileFragment()); true }
                else             -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
