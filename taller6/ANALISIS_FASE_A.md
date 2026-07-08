# Taller 6 – Native UI Re-Engineering & UX Analysis
## Fase A: Selección y Análisis

**Estudiante:** Evelin Ximena Rocha Rocha  
**Aplicación seleccionada:** Duolingo  
**Tecnología de implementación:** Android Nativo – Kotlin (RecyclerView + DiffUtil + ViewBinding)  
**Sin uso de WebViews:** Toda la interfaz usa componentes nativos de Android.

---

## 1. Definición de Mercado

### ¿A quién está dirigida Duolingo?

| Dimensión | Descripción |
|-----------|-------------|
| **Rango de edad** | 13 – 40 años, con núcleo fuerte entre 18 – 30 años (estudiantes universitarios y jóvenes profesionales) |
| **Intereses** | Aprendizaje de idiomas, superación personal, viajes, juegos casuales, productividad |
| **Nivel socioeconómico** | Medio y medio-alto. Tiene capa gratuita (amplia base) y suscripción Duolingo Plus (~$7/mes) orientada al segmento con ingresos disponibles |
| **Perfil psicográfico** | Personas automotivadas pero que necesitan estructura gamificada para mantener el hábito. Buscan aprendizaje "en los ratos libres" (transporte, espera, descanso) |
| **Geografía** | Global. Mayor penetración en EE.UU., América Latina y Europa. En Ecuador/Latinoamérica muy popular entre estudiantes que buscan inglés para oportunidades laborales |

### Motivación de uso
Duolingo se posiciona como la "píldora diaria" del idioma: sesiones cortas (5-10 min), recompensas inmediatas (XP, rachas, ligas) y presión social positiva (tabla de clasificación). No compite con academias — compite con el tiempo libre del usuario.

---

## 2. Psicología del Color

### Paleta Principal Identificada

| Color | Hex | Rol en la UI | Justificación (Teoría del Color) |
|-------|-----|--------------|----------------------------------|
| **Verde primario** | `#58CC02` | Botones de acción, respuestas correctas, progreso | El verde es el color del **crecimiento, la naturaleza y el éxito**. En psicología del color transmite "sigue adelante" (semáforo verde). Duolingo lo usa para reforzar cada acierto → respuesta emocional positiva inmediata |
| **Verde oscuro** | `#46A302` | Sombra de botones, estados activos | Variación para dar profundidad y efecto 3D a los botones. Refuerza la solidez de la acción |
| **Amarillo** | `#FFD900` | XP, logros, monedas (lingots) | El amarillo activa **energía, euforia y recompensa**. Asociado al oro → el cerebro lo interpreta como "premio". Cada vez que ves amarillo en Duolingo, tu cerebro espera una recompensa |
| **Naranja** | `#FF9600` | Racha (streak fire 🔥) | El naranja combina la **urgencia del rojo con la calidez del amarillo**. La racha naranja crea ansiedad positiva: *"no puedo romperla hoy"* — el mecanismo psicológico de la pérdida supera al del logro |
| **Rojo** | `#FF4B4B` | Corazones (vidas), respuestas incorrectas | Rojo clásico de **peligro, error y urgencia**. Se usa con moderación para no generar frustración excesiva, pero lo suficiente para que perder un corazón duela |
| **Azul** | `#1CB0F6` | Progreso de logros, estados de carga | Azul de **confianza e inteligencia**. Se asocia a "información neutral". Duolingo lo usa en métricas de avance para que se sienta objetivo y confiable |
| **Blanco** | `#FFFFFF` | Fondo principal, tarjetas | **Limpieza cognitiva**. El fondo blanco elimina distracciones y dirige la atención al contenido. También reduce el agotamiento visual en sesiones largas |
| **Gris claro** | `#F7F7F7` | Fondo de pantallas | Suaviza la pantalla completa blanca. Separa secciones sin líneas visibles (diseño "flat") |
| **Gris texto** | `#AFAFAF` | Textos secundarios, elementos bloqueados | **Desactivación visual**: el gris indica "aún no disponible" sin generar frustración activa |

### Conclusión de Paleta
Duolingo aplica la teoría del **Color Hedónico**: verde para el placer del logro, naranja para la urgencia de la racha, amarillo para la recompensa, rojo como señal de alerta controlada. Es una paleta diseñada para **maximizar el engagement** y reducir el abandono de la sesión.

---

## 3. Auditoría de Componentes — Las 3 Listas a Clonar

### Lista 1: Ruta de Lecciones (Skill Path) — Pantalla Home
**Tipo de iterable:** Lista vertical con estados diferenciados  
**Descripción:** Secuencia lineal de lecciones representadas como burbujas/botones en una ruta. Cada elemento puede estar en estado `COMPLETADO` (verde), `ACTIVO` (resaltado con pulso) o `BLOQUEADO` (gris con candado).  
**Implementación:** `RecyclerView` + `LinearLayoutManager` + `LessonAdapter` con `DiffUtil.ItemCallback<Lesson>`  
**Datos clave:** título, emoji, tipo (LESSON/CHECKPOINT/PRACTICE), XP, progreso parcial, estado

### Lista 2: Tabla de Clasificación (Leaderboard) — Pantalla Liga
**Tipo de iterable:** Lista de ranking con destacado contextual  
**Descripción:** Ranking semanal de 15+ usuarios ordenados por XP. Los primeros 3 tienen medallas (🥇🥈🥉). El usuario actual se resalta con fondo verde.  
**Implementación:** `RecyclerView` + `LinearLayoutManager` + `LeaderboardAdapter` con `DiffUtil.ItemCallback<LeaderboardUser>`  
**Datos clave:** posición, nombre, iniciales, XP, color de avatar, isCurrentUser

### Lista 3: Logros / Achievements — Pantalla Perfil
**Tipo de iterable:** Lista mixta de elementos desbloqueados y bloqueados  
**Descripción:** Colección de 12 logros. Los desbloqueados muestran el emoji a full opacity; los bloqueados están desaturados con candado y, si hay progreso parcial, muestran una barra de avance azul.  
**Implementación:** `RecyclerView` + `LinearLayoutManager` + `AchievementAdapter` con `DiffUtil.ItemCallback<Achievement>` dentro de un `NestedScrollView`  
**Datos clave:** título, descripción, emoji, isUnlocked, progress, total

---

## 4. Arquitectura del Proyecto

```
com.example.androidnativowebview/
├── data/
│   ├── model/
│   │   ├── Lesson.kt          ← data class + enum LessonType
│   │   ├── LeaderboardUser.kt ← data class
│   │   └── Achievement.kt     ← data class
│   └── MockDataRepository.kt  ← objeto singleton con listas simuladas
├── ui/
│   ├── home/
│   │   ├── HomeFragment.kt    ← Lista 1
│   │   └── LessonAdapter.kt   ← ListAdapter + DiffUtil
│   ├── league/
│   │   ├── LeagueFragment.kt  ← Lista 2
│   │   └── LeaderboardAdapter.kt
│   └── profile/
│       ├── ProfileFragment.kt ← Lista 3
│       └── AchievementAdapter.kt
└── MainActivity.kt            ← BottomNavigationView + Fragment host
```

**Principios aplicados:**
- **Single Responsibility**: cada adapter gestiona únicamente su tipo de ítem
- **Separation of Concerns**: datos en `data/`, lógica de UI en `ui/`, repositorio independiente
- **DRY**: estados de UI (completado/activo/bloqueado) encapsulados en funciones privadas del adapter

---

## 5. Fase C — Análisis Crítico y Propuesta de Mejora

### Problema Detectado en Duolingo Original
**Falla:** La barra de progreso de la "Meta Diaria" de XP está oculta. Para verla, el usuario debe:  
1. Cerrar la lección actual  
2. Ir al perfil  
3. Navegar a estadísticas  

Esto genera que muchos usuarios **no saben cuánto XP les falta** para completar su meta diaria, y pierden su racha sin haberlo intentado conscientemente.

**Evidencia de la falla:** Duolingo genera millones de notificaciones push diarias tipo *"¡No pierdas tu racha!"* — si el progreso fuera visible, el usuario se auto-regularía sin depender de notificaciones.

### Solución Implementada
Se agregó una **barra de progreso persistente** en el header de la pantalla principal (HomeFragment), siempre visible sin navegación adicional:

```
⚡ Meta Diaria                          65 / 100 XP
[████████████░░░░░░░░░░░░░░░░░░░░░░░░░]
```

**Impacto esperado:**
- Reduce la necesidad de notificaciones push
- El usuario sabe en todo momento cuánto falta para su meta
- Aumenta la retención diaria al hacer el progreso tangible e inmediato
- Principio UX: **visibilidad del estado del sistema** (Nielsen Heuristic #1)
