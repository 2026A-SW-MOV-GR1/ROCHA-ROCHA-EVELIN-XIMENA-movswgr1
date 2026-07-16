package com.example.examen2bmoviles.data

import com.example.examen2bmoviles.models.QuitoActivity

object QuitoActivities {
    val lista = listOf(
        QuitoActivity(
            id = "act_001",
            nombre = "Teleférico de Quito",
            descripcion = "Asciende a 4,100 msnm en teleférico con vistas panorámicas espectaculares de la ciudad y el volcán Pichincha. La actividad incluye senderismo guiado por la cima.",
            latitud = -0.2102,
            longitud = -78.5076,
            costoBase = 8.50,
            categoria = "Aventura",
            guia = "Carlos Mendoza"
        ),
        QuitoActivity(
            id = "act_002",
            nombre = "Ciudad Mitad del Mundo",
            descripcion = "Experiencia única en la línea ecuatorial exacta. Museos, planetario y demostraciones científicas a 22 km al norte de Quito.",
            latitud = -0.0022,
            longitud = -78.4559,
            costoBase = 5.00,
            categoria = "Cultural",
            guia = "María Suárez"
        ),
        QuitoActivity(
            id = "act_003",
            nombre = "Recorrido Centro Histórico",
            descripcion = "Explora el patrimonio de la humanidad más grande y mejor conservado de América Latina. Iglesias barrocas, plazas coloniales y museos de arte religioso.",
            latitud = -0.2201,
            longitud = -78.5124,
            costoBase = 12.00,
            categoria = "Histórico",
            guia = "Ana Torres"
        ),
        QuitoActivity(
            id = "act_004",
            nombre = "Aventura en Parque La Carolina",
            descripcion = "Kayak, ciclismo, patinaje y actividades deportivas en el pulmón verde de Quito. Ideal para familias y grupos de amigos.",
            latitud = -0.1763,
            longitud = -78.4865,
            costoBase = 6.00,
            categoria = "Deporte",
            guia = "Pedro Lara"
        ),
        QuitoActivity(
            id = "act_005",
            nombre = "Mercado Artesanal La Mariscal",
            descripcion = "El mercado de artesanías más completo de Ecuador. Tejidos, cerámica, joyería de plata y souvenirs únicos de las culturas indígenas.",
            latitud = -0.2072,
            longitud = -78.4936,
            costoBase = 3.00,
            categoria = "Cultural",
            guia = "Sofía Rojas"
        ),
        QuitoActivity(
            id = "act_006",
            nombre = "Parque Arqueológico Rumipamba",
            descripcion = "Descubre vestigios de las culturas precolombinas Quitu-Cara en plena ciudad. Senderos ecológicos y exposiciones arqueológicas al aire libre.",
            latitud = -0.1891,
            longitud = -78.5005,
            costoBase = 4.00,
            categoria = "Arqueología",
            guia = "Diego Arias"
        )
    )
}
