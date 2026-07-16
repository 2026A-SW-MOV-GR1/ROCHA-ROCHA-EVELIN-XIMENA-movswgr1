"""
Generador de presentación PDF — Taller 6: Duolingo Clone
Autora: Evelin Rocha
"""
from reportlab.lib.pagesizes import landscape, A4
from reportlab.lib import colors
from reportlab.lib.units import cm
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, HRFlowable
)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.enums import TA_CENTER, TA_LEFT, TA_RIGHT
from reportlab.platypus import KeepTogether
import os

# ─── Paleta de colores Duolingo ───────────────────────────────────────────────
DUO_GREEN       = colors.HexColor("#58CC02")
DUO_GREEN_DARK  = colors.HexColor("#3E9501")
DUO_GREEN_LIGHT = colors.HexColor("#D7FFB8")
DUO_BLUE        = colors.HexColor("#1CB0F6")
DUO_BLUE_DARK   = colors.HexColor("#0D87BF")
DUO_ORANGE      = colors.HexColor("#FF9600")
DUO_RED         = colors.HexColor("#FF4B4B")
DUO_WHITE       = colors.HexColor("#FFFFFF")
DUO_BG          = colors.HexColor("#F7F7F7")
DUO_TEXT_DARK   = colors.HexColor("#3C3C3C")
DUO_TEXT_GRAY   = colors.HexColor("#AFAFAF")
DUO_PURPLE      = colors.HexColor("#CE82FF")

PAGE_W, PAGE_H = landscape(A4)
MARGIN = 1.5 * cm

OUTPUT = os.path.join(os.path.dirname(__file__), "Presentacion_Taller6_EvelinRocha.pdf")

doc = SimpleDocTemplate(
    OUTPUT,
    pagesize=landscape(A4),
    leftMargin=MARGIN, rightMargin=MARGIN,
    topMargin=MARGIN, bottomMargin=MARGIN,
)

# ─── Estilos ──────────────────────────────────────────────────────────────────
base = getSampleStyleSheet()

def style(name, parent="Normal", **kw):
    s = ParagraphStyle(name, parent=base[parent], **kw)
    return s

S = {
    "slide_title": style("SlideTitle",
        fontSize=32, textColor=DUO_GREEN_DARK, alignment=TA_CENTER,
        fontName="Helvetica-Bold", leading=38, spaceAfter=6),
    "slide_subtitle": style("SlideSubtitle",
        fontSize=16, textColor=DUO_BLUE_DARK, alignment=TA_CENTER,
        fontName="Helvetica", leading=22),
    "section_label": style("SectionLabel",
        fontSize=11, textColor=DUO_GREEN, fontName="Helvetica-Bold",
        spaceBefore=8, spaceAfter=2),
    "body": style("Body",
        fontSize=12, textColor=DUO_TEXT_DARK, fontName="Helvetica",
        leading=18, spaceBefore=3),
    "bullet": style("Bullet",
        fontSize=12, textColor=DUO_TEXT_DARK, fontName="Helvetica",
        leading=18, leftIndent=16, bulletIndent=4, spaceBefore=2),
    "code": style("Code",
        fontSize=10, textColor=colors.HexColor("#2E2E2E"),
        fontName="Courier", leading=14, leftIndent=12,
        backColor=colors.HexColor("#F0F0F0"), spaceBefore=4, spaceAfter=4),
    "caption": style("Caption",
        fontSize=9, textColor=DUO_TEXT_GRAY, fontName="Helvetica",
        alignment=TA_CENTER, spaceBefore=2),
    "cover_title": style("CoverTitle",
        fontSize=42, textColor=DUO_WHITE, alignment=TA_CENTER,
        fontName="Helvetica-Bold", leading=50, spaceAfter=10),
    "cover_sub": style("CoverSub",
        fontSize=18, textColor=DUO_GREEN_LIGHT, alignment=TA_CENTER,
        fontName="Helvetica", leading=24),
    "cover_author": style("CoverAuthor",
        fontSize=14, textColor=DUO_WHITE, alignment=TA_CENTER,
        fontName="Helvetica-Bold", leading=20),
    "tag": style("Tag",
        fontSize=11, textColor=DUO_WHITE, fontName="Helvetica-Bold",
        alignment=TA_CENTER),
    "h2": style("H2",
        fontSize=22, textColor=DUO_TEXT_DARK, fontName="Helvetica-Bold",
        alignment=TA_CENTER, leading=28, spaceBefore=4, spaceAfter=8),
    "phase_title": style("PhaseTitle",
        fontSize=26, textColor=DUO_WHITE, fontName="Helvetica-Bold",
        alignment=TA_CENTER, leading=32),
    "small": style("Small",
        fontSize=10, textColor=DUO_TEXT_GRAY, fontName="Helvetica",
        alignment=TA_CENTER),
}

# ─── Helpers ──────────────────────────────────────────────────────────────────
def hr(color=DUO_GREEN, thickness=2):
    return HRFlowable(width="100%", thickness=thickness, color=color, spaceAfter=8)

def sp(h=0.3):
    return Spacer(1, h * cm)

def badge_table(items, colors_list=None, col_widths=None):
    """Fila de badges de colores."""
    if colors_list is None:
        colors_list = [DUO_GREEN] * len(items)
    data = [[Paragraph(t, S["tag"]) for t in items]]
    w = col_widths or [(PAGE_W - 2*MARGIN) / len(items) - 0.3*cm] * len(items)
    t = Table(data, colWidths=w, rowHeights=[1*cm])
    cmds = [
        ("BACKGROUND", (i, 0), (i, 0), colors_list[i % len(colors_list)])
        for i in range(len(items))
    ]
    cmds += [
        ("ALIGN",    (0,0), (-1,-1), "CENTER"),
        ("VALIGN",   (0,0), (-1,-1), "MIDDLE"),
        ("ROUNDEDCORNERS", [6,6,6,6]),
        ("LEFTPADDING",  (0,0), (-1,-1), 8),
        ("RIGHTPADDING", (0,0), (-1,-1), 8),
    ]
    t.setStyle(TableStyle(cmds))
    return t

def colored_box(content_flowables, bg=DUO_BG, border=DUO_GREEN, padding=10):
    """Envuelve flowables en una tabla con fondo de color."""
    inner = Table([[f] for f in content_flowables], colWidths=[PAGE_W - 2*MARGIN - 1*cm])
    inner.setStyle(TableStyle([
        ("BACKGROUND", (0,0), (-1,-1), bg),
        ("LEFTPADDING",   (0,0), (-1,-1), padding),
        ("RIGHTPADDING",  (0,0), (-1,-1), padding),
        ("TOPPADDING",    (0,0), (-1,-1), 6),
        ("BOTTOMPADDING", (0,0), (-1,-1), 6),
        ("BOX", (0,0), (-1,-1), 1.5, border),
        ("ROUNDEDCORNERS", [8,8,8,8]),
    ]))
    return inner

def two_col(left_items, right_items, left_color=DUO_GREEN_LIGHT, right_color=colors.HexColor("#E8F4FD")):
    """Dos columnas lado a lado."""
    def make_cell(items, bg):
        rows = [[Paragraph(t, S["bullet"])] for t in items]
        t = Table(rows, colWidths=[(PAGE_W - 2*MARGIN)/2 - 0.8*cm])
        t.setStyle(TableStyle([
            ("BACKGROUND", (0,0), (-1,-1), bg),
            ("LEFTPADDING",  (0,0), (-1,-1), 10),
            ("TOPPADDING",   (0,0), (-1,-1), 6),
            ("BOTTOMPADDING",(0,0), (-1,-1), 6),
            ("ROUNDEDCORNERS", [6,6,6,6]),
        ]))
        return t
    left  = make_cell(left_items,  left_color)
    right = make_cell(right_items, right_color)
    outer = Table([[left, right]],
                  colWidths=[(PAGE_W-2*MARGIN)/2 - 0.2*cm,
                             (PAGE_W-2*MARGIN)/2 - 0.2*cm],
                  spaceBefore=4)
    outer.setStyle(TableStyle([("VALIGN",(0,0),(-1,-1),"TOP"),
                               ("LEFTPADDING",(0,0),(-1,-1),0),
                               ("RIGHTPADDING",(0,0),(-1,-1),0)]))
    return outer

# ─── Slides ───────────────────────────────────────────────────────────────────
story = []

# ─── PORTADA ──────────────────────────────────────────────────────────────────
cover_bg = Table(
    [[Paragraph("🦉  Duolingo Clone", S["cover_title"])],
     [Paragraph("Android Nativo · Kotlin · RecyclerView + DiffUtil", S["cover_sub"])],
     [sp(0.5)],
     [Paragraph("Taller 6 — Native UI Re-Engineering &amp; UX Analysis", S["cover_sub"])],
     [sp(0.8)],
     [Paragraph("Evelin Ximena Rocha Rocha", S["cover_author"])],
     [Paragraph("Aplicaciones Móviles · 2026", S["small"])],
    ],
    colWidths=[PAGE_W - 2*MARGIN]
)
cover_bg.setStyle(TableStyle([
    ("BACKGROUND", (0,0), (-1,-1), DUO_GREEN_DARK),
    ("TOPPADDING",    (0,0), (-1,-1), 20),
    ("BOTTOMPADDING", (0,0), (-1,-1), 12),
    ("LEFTPADDING",   (0,0), (-1,-1), 30),
    ("RIGHTPADDING",  (0,0), (-1,-1), 30),
    ("ALIGN",  (0,0), (-1,-1), "CENTER"),
    ("VALIGN", (0,0), (-1,-1), "MIDDLE"),
    ("ROUNDEDCORNERS", [12,12,12,12]),
]))
story.append(cover_bg)
story.append(sp(0.4))
story.append(badge_table(
    ["Fase A: Análisis UX", "Fase B: Implementación", "Fase C: Mejora"],
    [DUO_BLUE, DUO_ORANGE, DUO_PURPLE]
))

# ─── SLIDE 2: Objetivo ────────────────────────────────────────────────────────
story.append(sp())
story.append(hr())
story.append(Paragraph("¿Qué es este proyecto?", S["slide_title"]))
story.append(hr())
story.append(sp(0.3))
story.append(Paragraph(
    "Reverse-engineering de la app Duolingo, reconstruida desde cero "
    "en <b>Android nativo con Kotlin</b>, aplicando buenas prácticas "
    "de arquitectura, animaciones y UX.",
    S["body"]
))
story.append(sp(0.3))
story.append(two_col(
    ["🎯 Analizar patrones de UX de Duolingo",
     "🏗️ Diseñar arquitectura con Fragments + RecyclerView",
     "⚡ Implementar micro-animaciones nativas",
     "💡 Proponer una mejora propia (Fase C)"],
    ["📱 Pantalla Home — Ruta de lecciones",
     "🏆 Pantalla Liga — Tabla de posiciones",
     "👤 Pantalla Perfil — Logros desbloqueados",
     "🧭 Navegación con Bottom Navigation Bar"],
    left_color=DUO_GREEN_LIGHT,
    right_color=colors.HexColor("#E8F4FD")
))

# ─── SLIDE 3: Fase A - Análisis ───────────────────────────────────────────────
story.append(sp())
story.append(hr(DUO_BLUE))

phase_a = Table(
    [[Paragraph("FASE A", S["phase_title"]),
      Paragraph("Análisis UX de Duolingo", S["phase_title"])]],
    colWidths=[3*cm, PAGE_W - 2*MARGIN - 3.5*cm]
)
phase_a.setStyle(TableStyle([
    ("BACKGROUND", (0,0), (0,0), DUO_BLUE_DARK),
    ("BACKGROUND", (1,0), (1,0), DUO_BLUE),
    ("TOPPADDING",    (0,0),(-1,-1), 10),
    ("BOTTOMPADDING", (0,0),(-1,-1), 10),
    ("LEFTPADDING",   (0,0),(-1,-1), 14),
    ("VALIGN", (0,0),(-1,-1), "MIDDLE"),
    ("ROUNDEDCORNERS", [8,8,8,8]),
]))
story.append(phase_a)
story.append(sp(0.3))

analysis_data = [
    ["Patrón observado", "Implementación en el clon"],
    ["Ruta de lecciones con conectores verticales", "RecyclerView + item_lesson.xml con viewConnector"],
    ["Estados visuales: completado / activo / bloqueado", "applyCompletedState / applyActiveState / applyLockedState"],
    ["Tabla de clasificación con avatar de color", "LeaderboardAdapter con initials + color de avatar"],
    ["Logros con barra de progreso individual", "AchievementAdapter con progressAchievement visible/gone"],
    ["Navegación por pestañas inferior", "BottomNavigationView con 3 ítems"],
    ["Micro-animación al tocar lección", "ObjectAnimator (scale) con rebote press/release"],
]
t = Table(analysis_data, colWidths=[(PAGE_W-2*MARGIN)*0.42, (PAGE_W-2*MARGIN)*0.56])
t.setStyle(TableStyle([
    ("BACKGROUND",   (0,0), (-1,0),  DUO_BLUE),
    ("TEXTCOLOR",    (0,0), (-1,0),  DUO_WHITE),
    ("FONTNAME",     (0,0), (-1,0),  "Helvetica-Bold"),
    ("FONTSIZE",     (0,0), (-1,-1), 10),
    ("FONTNAME",     (0,1), (-1,-1), "Helvetica"),
    ("ROWBACKGROUNDS",(0,1), (-1,-1), [DUO_WHITE, DUO_BG]),
    ("TEXTCOLOR",    (0,1), (-1,-1), DUO_TEXT_DARK),
    ("GRID",         (0,0), (-1,-1), 0.5, DUO_TEXT_GRAY),
    ("LEFTPADDING",  (0,0), (-1,-1), 8),
    ("TOPPADDING",   (0,0), (-1,-1), 5),
    ("BOTTOMPADDING",(0,0), (-1,-1), 5),
    ("VALIGN",       (0,0), (-1,-1), "MIDDLE"),
]))
story.append(t)

# ─── SLIDE 4: Fase B - Arquitectura ───────────────────────────────────────────
story.append(sp())
story.append(hr(DUO_ORANGE))

phase_b = Table(
    [[Paragraph("FASE B", S["phase_title"]),
      Paragraph("Arquitectura &amp; Implementación", S["phase_title"])]],
    colWidths=[3*cm, PAGE_W - 2*MARGIN - 3.5*cm]
)
phase_b.setStyle(TableStyle([
    ("BACKGROUND", (0,0), (0,0), colors.HexColor("#CC7700")),
    ("BACKGROUND", (1,0), (1,0), DUO_ORANGE),
    ("TOPPADDING",    (0,0),(-1,-1), 10),
    ("BOTTOMPADDING", (0,0),(-1,-1), 10),
    ("LEFTPADDING",   (0,0),(-1,-1), 14),
    ("VALIGN", (0,0),(-1,-1), "MIDDLE"),
    ("ROUNDEDCORNERS", [8,8,8,8]),
]))
story.append(phase_b)
story.append(sp(0.3))

arch_left = [
    "<b>📂 Estructura de paquetes</b>",
    "• <font name='Courier'>com.example.duolingoclone</font>",
    "  ├─ <font name='Courier'>data/model/</font>  → Lesson, LeaderboardUser, Achievement",
    "  ├─ <font name='Courier'>data/</font>         → MockDataRepository (object singleton)",
    "  └─ <font name='Courier'>ui/</font>",
    "       ├─ home/   → HomeFragment + LessonAdapter",
    "       ├─ league/ → LeagueFragment + LeaderboardAdapter",
    "       └─ profile/→ ProfileFragment + AchievementAdapter",
]
arch_right = [
    "<b>🔧 Tecnologías clave</b>",
    "• <b>ViewBinding</b> — acceso tipado a vistas, sin findViewById",
    "• <b>ListAdapter + DiffUtil</b> — diff eficiente de listas",
    "• <b>ObjectAnimator</b> — animaciones sin librerías externas",
    "• <b>Fragment + FragmentManager</b> — navegación liviana",
    "• <b>BottomNavigationView</b> — navegación principal",
    "• <b>DividerItemDecoration</b> — separadores en la liga",
]

left_t = Table([[Paragraph(r, S["body"])] for r in arch_left],
               colWidths=[(PAGE_W-2*MARGIN)/2 - 0.6*cm])
left_t.setStyle(TableStyle([
    ("BACKGROUND", (0,0),(-1,-1), colors.HexColor("#FFF8E7")),
    ("LEFTPADDING",(0,0),(-1,-1), 10), ("TOPPADDING",(0,0),(-1,-1),4),
    ("BOTTOMPADDING",(0,0),(-1,-1),4), ("ROUNDEDCORNERS",[6,6,6,6]),
]))

right_t = Table([[Paragraph(r, S["body"])] for r in arch_right],
                colWidths=[(PAGE_W-2*MARGIN)/2 - 0.6*cm])
right_t.setStyle(TableStyle([
    ("BACKGROUND", (0,0),(-1,-1), colors.HexColor("#E8F4FD")),
    ("LEFTPADDING",(0,0),(-1,-1), 10), ("TOPPADDING",(0,0),(-1,-1),4),
    ("BOTTOMPADDING",(0,0),(-1,-1),4), ("ROUNDEDCORNERS",[6,6,6,6]),
]))

outer = Table([[left_t, right_t]],
              colWidths=[(PAGE_W-2*MARGIN)/2-0.1*cm, (PAGE_W-2*MARGIN)/2-0.1*cm])
outer.setStyle(TableStyle([("VALIGN",(0,0),(-1,-1),"TOP"),
                            ("LEFTPADDING",(0,0),(-1,-1),0),
                            ("RIGHTPADDING",(0,0),(-1,-1),0)]))
story.append(outer)

# ─── SLIDE 5: Pantallas ───────────────────────────────────────────────────────
story.append(sp())
story.append(hr())
story.append(Paragraph("Las 3 Pantallas Principales", S["slide_title"]))
story.append(hr())
story.append(sp(0.2))

screens = [
    ("🏠 HOME", DUO_GREEN,
     ["RecyclerView con 20 lecciones",
      "Conectores verticales entre ítems",
      "Estado: completado (verde) / activo / bloqueado",
      "Pulso suave en lección activa actual",
      "Barra de meta diaria (65/100 XP)"]),
    ("🏆 LIGA", DUO_BLUE,
     ["15 usuarios en tabla de posiciones",
      "Avatar con iniciales y color único",
      "Usuario propio resaltado en morado",
      "DividerItemDecoration entre filas",
      "Datos: nombre + XP + posición"]),
    ("👤 PERFIL", DUO_ORANGE,
     ["12 logros en grid/lista",
      "Estado: desbloqueado / bloqueado",
      "Barra de progreso por logro",
      "Emoji representativo por logro",
      "Descripción de condición de desbloqueo"]),
]

screen_cells = []
for (title, color, bullets) in screens:
    rows = [[Paragraph(f"• {b}", S["bullet"])] for b in bullets]
    inner = Table(rows, colWidths=[(PAGE_W-2*MARGIN)/3 - 0.8*cm])
    inner.setStyle(TableStyle([
        ("BACKGROUND",(0,0),(-1,-1),DUO_WHITE),
        ("LEFTPADDING",(0,0),(-1,-1),6),
        ("TOPPADDING",(0,0),(-1,-1),3),
        ("BOTTOMPADDING",(0,0),(-1,-1),3),
    ]))
    header = Table([[Paragraph(title, S["tag"])]], colWidths=[(PAGE_W-2*MARGIN)/3 - 0.8*cm])
    header.setStyle(TableStyle([
        ("BACKGROUND",(0,0),(-1,-1),color),
        ("TOPPADDING",(0,0),(-1,-1),8),
        ("BOTTOMPADDING",(0,0),(-1,-1),8),
        ("ROUNDEDCORNERS",[6,6,0,0]),
    ]))
    cell = Table([[header],[inner]], colWidths=[(PAGE_W-2*MARGIN)/3 - 0.8*cm])
    cell.setStyle(TableStyle([
        ("BOX",(0,0),(-1,-1),1,color),
        ("LEFTPADDING",(0,0),(-1,-1),0),
        ("RIGHTPADDING",(0,0),(-1,-1),0),
        ("TOPPADDING",(0,0),(-1,-1),0),
        ("BOTTOMPADDING",(0,0),(-1,-1),0),
        ("ROUNDEDCORNERS",[6,6,6,6]),
    ]))
    screen_cells.append(cell)

screens_table = Table(
    [screen_cells],
    colWidths=[(PAGE_W-2*MARGIN)/3-0.1*cm]*3,
)
screens_table.setStyle(TableStyle([
    ("VALIGN",(0,0),(-1,-1),"TOP"),
    ("LEFTPADDING",(0,0),(-1,-1),4),
    ("RIGHTPADDING",(0,0),(-1,-1),4),
]))
story.append(screens_table)

# ─── SLIDE 6: Micro-animaciones ───────────────────────────────────────────────
story.append(sp())
story.append(hr(DUO_PURPLE))
story.append(Paragraph("Micro-animaciones Nativas", S["slide_title"]))
story.append(hr(DUO_PURPLE))
story.append(sp(0.2))

anim_data = [
    ["Animación", "Código (Kotlin)", "Efecto UX"],
    ["Pulso en lección activa",
     "ObjectAnimator scaleX/Y 1f→1.04f→1f\nrepeatCount=INFINITE, duration=1400ms",
     "Guía visual: 'aquí debes continuar'"],
    ["Press / tap feedback",
     "AnimatorSet: down (scale→0.93f, 90ms)\n+ up (scale→1f, 90ms) playSequentially",
     "Respuesta táctil inmediata al toque"],
    ["Transición entre pantallas",
     "setCustomAnimations(\n  fade_in, fade_out\n)",
     "Cambio fluido sin saltos bruscos"],
    ["Estado de logro desbloqueado",
     "alpha = 0.35f (bloqueado)\nalpha = 1f   (disponible)",
     "Feedback visual de progreso claro"],
]
t2 = Table(anim_data,
           colWidths=[(PAGE_W-2*MARGIN)*0.22,
                      (PAGE_W-2*MARGIN)*0.42,
                      (PAGE_W-2*MARGIN)*0.34])
t2.setStyle(TableStyle([
    ("BACKGROUND",   (0,0), (-1,0),  DUO_PURPLE),
    ("TEXTCOLOR",    (0,0), (-1,0),  DUO_WHITE),
    ("FONTNAME",     (0,0), (-1,0),  "Helvetica-Bold"),
    ("FONTSIZE",     (0,0), (-1,-1), 10),
    ("FONTNAME",     (0,1), (-1,-1), "Helvetica"),
    ("FONTNAME",     (1,1), (1,-1),  "Courier"),
    ("ROWBACKGROUNDS",(0,1),(-1,-1), [DUO_WHITE, colors.HexColor("#F8F0FF")]),
    ("TEXTCOLOR",    (0,1), (-1,-1), DUO_TEXT_DARK),
    ("GRID",         (0,0), (-1,-1), 0.5, DUO_TEXT_GRAY),
    ("LEFTPADDING",  (0,0), (-1,-1), 8),
    ("TOPPADDING",   (0,0), (-1,-1), 5),
    ("BOTTOMPADDING",(0,0), (-1,-1), 5),
    ("VALIGN",       (0,0), (-1,-1), "MIDDLE"),
]))
story.append(t2)

# ─── SLIDE 7: Fase C - Mejora ─────────────────────────────────────────────────
story.append(sp())
story.append(hr(DUO_PURPLE))

phase_c = Table(
    [[Paragraph("FASE C", S["phase_title"]),
      Paragraph("Propuesta de Mejora Propia", S["phase_title"])]],
    colWidths=[3*cm, PAGE_W - 2*MARGIN - 3.5*cm]
)
phase_c.setStyle(TableStyle([
    ("BACKGROUND", (0,0), (0,0), colors.HexColor("#8B5CF6")),
    ("BACKGROUND", (1,0), (1,0), DUO_PURPLE),
    ("TOPPADDING",    (0,0),(-1,-1), 10),
    ("BOTTOMPADDING", (0,0),(-1,-1), 10),
    ("LEFTPADDING",   (0,0),(-1,-1), 14),
    ("VALIGN", (0,0),(-1,-1), "MIDDLE"),
    ("ROUNDEDCORNERS", [8,8,8,8]),
]))
story.append(phase_c)
story.append(sp(0.3))

story.append(Paragraph("💡 Barra de Meta Diaria Siempre Visible", S["h2"]))
story.append(sp(0.2))

improvement = colored_box([
    Paragraph("<b>Problema identificado en Duolingo original:</b>", S["section_label"]),
    Paragraph(
        "El progreso XP diario solo se muestra dentro de una pantalla específica, "
        "lo que obliga al usuario a navegar para saber cuánto le falta para su meta.",
        S["body"]),
    sp(0.2),
    Paragraph("<b>Solución implementada:</b>", S["section_label"]),
    Paragraph(
        "Se agregó una <b>ProgressBar horizontal + texto «65/100 XP»</b> fija en el header "
        "de HomeFragment, siempre visible sin importar la posición del scroll.",
        S["body"]),
    sp(0.2),
    Paragraph("<b>Código clave:</b>", S["section_label"]),
    Paragraph(
        "binding.progressDailyGoal.max = total<br/>"
        "binding.progressDailyGoal.progress = current<br/>"
        "binding.tvDailyGoalProgress.text = \"$current / $total XP\"",
        S["code"]),
], bg=colors.HexColor("#F8F0FF"), border=DUO_PURPLE)
story.append(improvement)

# ─── SLIDE 8: Stack técnico + conclusiones ────────────────────────────────────
story.append(sp())
story.append(hr())
story.append(Paragraph("Stack Técnico &amp; Conclusiones", S["slide_title"]))
story.append(hr())
story.append(sp(0.2))

story.append(badge_table(
    ["Kotlin", "Android SDK", "ViewBinding", "RecyclerView", "DiffUtil", "ObjectAnimator"],
    [DUO_GREEN_DARK, DUO_BLUE_DARK, DUO_ORANGE, DUO_PURPLE,
     colors.HexColor("#E02020"), DUO_GREEN],
))
story.append(sp(0.4))

story.append(two_col(
    ["✅ RecyclerView con ListAdapter + DiffUtil evita re-renders innecesarios",
     "✅ ViewBinding elimina NullPointerException en vistas",
     "✅ Arquitectura Fragment modular y escalable",
     "✅ Animaciones 100% nativas — sin dependencias externas",
     "✅ MockDataRepository centraliza todos los datos de prueba"],
    ["🎓 Aprendí a clonar UI complejas analizando patrones reales",
     "🎓 Comprendí el valor de DiffUtil para listas de aprendizaje",
     "🎓 Las micro-animaciones mejoran significativamente el UX",
     "🎓 Un buen análisis (Fase A) guía toda la implementación",
     "🎓 La mejora propia refuerza pensamiento crítico de producto"],
    left_color=DUO_GREEN_LIGHT,
    right_color=colors.HexColor("#E8F4FD")
))

story.append(sp(0.4))
footer = Table(
    [[Paragraph("Evelin Ximena Rocha Rocha  ·  Aplicaciones Móviles  ·  Taller 6  ·  2026", S["small"])]],
    colWidths=[PAGE_W - 2*MARGIN]
)
footer.setStyle(TableStyle([
    ("BACKGROUND", (0,0),(-1,-1), DUO_GREEN_DARK),
    ("TEXTCOLOR",  (0,0),(-1,-1), DUO_WHITE),
    ("TOPPADDING",    (0,0),(-1,-1), 6),
    ("BOTTOMPADDING", (0,0),(-1,-1), 6),
    ("ALIGN", (0,0),(-1,-1), "CENTER"),
    ("ROUNDEDCORNERS", [6,6,6,6]),
]))
story.append(footer)

# ─── Generar ──────────────────────────────────────────────────────────────────
doc.build(story)
print(f"PDF generado: {OUTPUT}")
