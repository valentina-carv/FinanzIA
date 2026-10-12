# FinanzIA — Contexto del proyecto

## Qué es FinanzIA

App móvil Android de finanzas personales con IA. Ayuda a registrar ingresos y
gastos, clasificarlos, ver el saldo disponible, controlar presupuestos y metas,
y recibir recomendaciones de un asistente que analiza los datos **reales** del
usuario (no inventa cifras).

Usuaria objetivo: personas con ingresos informales o variables (por ejemplo,
una trabajadora independiente que necesita organizar sus finanzas).

Proyecto académico grupal (Universidad Jorge Tadeo Lozano). Todo el copy de la
UI está en **español**.

## Equipo

Paula Valentina Carvajal Velásquez, Juan y Tatiana
Mantilla. (Responsables por pantalla: ver `docs/SPEC.md`, decisión D-10.)

## Principio de trabajo (no negociable)

El equipo está aprendiendo Kotlin y Compose mientras construye la app. Por eso
cada funcionalidad se hace en pasos pequeños:

1. Construir → 2. Ejecutar → 3. Probar → 4. **Entender** → 5. Guardar en Git → 6. Seguir.

Nada de cientos de líneas que nadie entiende. Cada PR explica, en lenguaje
sencillo, qué hace el código nuevo y por qué.

## Sistema de diseño — "FinanzIA Clean" (tomado del prototipo en `/design/`)

- **Tema claro**, fondo casi blanco, tarjetas blancas con esquinas muy redondeadas
  y sombra suave. Amigable, juvenil, financiero sin parecer banco.
- Paleta base (tokens en `ui/theme/Color.kt`):

| Token | Hex | Uso |
|---|---|---|
| `Fondo` | `#F8F9FD` | Fondo de todas las pantallas |
| `Superficie` | `#FFFFFF` | Tarjetas y campos |
| `Tinta` | `#0F1B3A` | Títulos y montos |
| `TextoSecundario` | `#64748B` | Descripciones |
| `TextoTenue` | `#94A3B8` | Placeholders, "Omitir este paso" |
| `Borde` | `#E2E8F0` | Bordes de tarjetas y campos |
| `Verde` | `#10B981` | Ingresos, éxito, marca ("IA", "Tu asistente financiero con IA") |
| `VerdeSuave` | `#ECFDF5` | Fondo de tarjeta seleccionada de ingreso, consejos |
| `Azul` | `#0062F0` | Enlaces, botón de gasto, final del degradado |
| `AzulSuave` | `#EAF2FF` | Tarjetas informativas |
| `Coral` | `#E23D68` | Gastos, selección de categoría de gasto, errores |
| `CoralSuave` | `#FFF1F3` | Fondo de tarjeta seleccionada de gasto, "Dato de Finanzia" |
| `Violeta` | `#7C5CFF` | **Solo la IA:** pantalla FinanzIA, tarjeta "FinanzIA dice", burbujas del asistente |
| `VioletaSuave` | `#F1EDFF` | Fondo de tarjetas y burbujas de la IA |
| `Ambar` | `#FFB547` | Presupuesto cerca del límite (≥ 80 %) |

- **Botón principal:** degradado horizontal `Verde → Azul`, texto blanco, flecha →,
  altura 56dp, esquinas de 24dp. (En la pantalla de gasto el botón es `Azul` sólido.)
- **Regla de color:** verde = dinero que entra, coral = dinero que sale, azul =
  acciones y enlaces, ámbar = presupuesto cerca del límite, **violeta = solo
  cuando habla la IA** (así el usuario aprende a reconocerla). Ver D-16.
- Tipografía: Plus Jakarta Sans (títulos y montos en negrita) + Inter (texto).
  Mientras no se agreguen los `.ttf` a `res/font/`, `Type.kt` usa `FontFamily.Default`.
- Esquinas: 24dp tarjetas grandes, 16dp campos y opciones, píldoras completas para
  los indicadores de paso ("Paso 2 • Gastos").
- Iconos: emojis por categoría (🏠 🍔 🎉 🚗) e iconos Material *Rounded*.
- **Regla dura: cero colores, espaciados o tamaños de letra hardcodeados.** Todo
  sale de `ui/theme/` (`FinanziaColors`, `FinanziaSpacing`, `FinanziaShapes`,
  `FinanziaTypography`). Los componentes compartidos viven en `ui/components/`.

## Tono de voz

FinanzIA tutea, es cercana y **nunca regaña**: informa y propone. Sin jerga
bancaria ("débito", "cargo", "rubro").

| Sí | No |
|---|---|
| "Vas muy bien, te quedan $180.000 para comida" | "Ha consumido el 85% del rubro alimentación" |
| "Este mes gastaste más en salidas, ¿lo revisamos?" | "Exceso de gasto detectado" |

## Navegación principal (Fase 2)

Barra inferior con 5 destinos: 🏠 Inicio · 💳 Movimientos · 🤖 IA · 📊 Reportes · 👤 Perfil.

## Arquitectura

Un solo módulo Gradle (`app`), paquete `com.finanzia.app`, organizado por capas:

```
com.finanzia.app/
├── MainActivity.kt
├── FinanziaApp.kt            ← Application: crea el AppContainer
├── di/AppContainer.kt        ← "inyección manual": crea repositorios una sola vez
├── navigation/               ← NavHost y rutas
├── ui/
│   ├── theme/                ← Color, Type, Shape, Spacing, Theme
│   ├── components/           ← FinanziaButton, FinanziaTextField, FinanziaCard,
│   │                           FinanziaLogo, OptionCard, StepPill, TipCard
│   └── screens/
│       ├── auth/             ← login, register, welcome, forgot
│       └── onboarding/       ← start, income, expense
├── data/
│   ├── supabase/SupabaseClient.kt
│   ├── repository/           ← AuthRepository, TransactionRepository
│   └── model/                ← Transaction, Category, IncomeFrequency
└── domain/                   ← validaciones y reglas en Kotlin puro (EmailValidator,
                                PasswordPolicy, MoneyFormatter)
```

Flujo obligatorio: `Screen (Compose) → ViewModel → Repository → Supabase`.

Reglas duras:
- La UI **nunca** llama a Supabase directamente.
- Los ViewModels **nunca** importan `androidx.compose.*`. Exponen `StateFlow<UiState>`.
- Los repositorios devuelven `Result<T>` de Kotlin; ninguna excepción llega a la UI.
- Sin Hilt: las dependencias se crean en `AppContainer` (más fácil de entender).
- Las reglas de validación (correo, contraseña, montos) viven en `domain/` y
  tienen pruebas unitarias.
- Fechas con `java.time` (minSdk 26).
- Dinero: la base usa `numeric(14,2)`. En `data/model/` el monto llega como
  `Double` (así lo entrega Supabase en JSON) y el repositorio lo convierte a
  `Long` en pesos con `roundToLong()`. Dominio y UI trabajan solo con `Long`.

## Backend — Supabase

- Supabase Auth (registro, login, recuperar contraseña) + PostgreSQL con RLS.
- Librería: `supabase-kt` (módulos `auth-kt` y `postgrest-kt`) con Ktor.
- `SUPABASE_URL` y `SUPABASE_ANON_KEY` van en `local.properties` y se leen como
  `BuildConfig`. `local.properties` **nunca** se sube a Git.
- La anon key es pública por diseño: la seguridad la da RLS. Ninguna otra llave
  (service_role, API de IA) va dentro de la app; la IA se llamará desde una
  Edge Function de Supabase (fase posterior).
- Esquema del equipo: 9 tablas (`usuarios`, `colores`, `categorias`, `cuentas`,
  `metas`, `movimientos`, `aportes`, `conversaciones`, `mensajes`). Detalle en
  `docs/SPEC.md` sección 6 y en `supabase/`.
- **La contraseña nunca se guarda en `usuarios`.** Registro y login van por
  Supabase Auth; la fila de `usuarios` la crea un trigger.
- Cambios de esquema: un archivo nuevo numerado (`supabase/004_....sql`),
  nunca editar uno que ya se ejecutó.

## Asistente FinanzIA (IA)

- No es un chatbot genérico: responde con los datos **reales** del usuario y
  nunca inventa cifras. Si no hay datos suficientes, lo dice.
- Flujo: `FinanziaAIScreen → FinanziaAIViewModel → AIRepository → Edge Function
  de Supabase → modelo de IA`. La Edge Function arma el contexto financiero
  (ingresos, gastos por categoría, presupuestos, metas del mes) leyendo con la
  sesión del usuario, así RLS sigue aplicando.
- La llave de la IA vive solo como secreto de la Edge Function. Nunca en la app,
  en `local.properties` ni en el repo.
- Las conversaciones se guardan en `conversaciones` y `mensajes`
  (`rol` = `usuario`/`asistente`).

## Diseño de referencia

- `/design/<pantalla>/screen.jpg`: captura del prototipo, una carpeta por pantalla.
- Si una referencia contradice este archivo (un color fuera de la paleta, una
  promesa que la app no cumple), gana `CLAUDE.md` y se reporta como decisión en
  el SPEC.
- Los textos de ejemplo ("Paula Gómez", "$18,500.00") son datos de muestra.
- Lo de "cifrado AES-256" que aparece en el prototipo **no se usa**: ningún texto
  de la app lo menciona (decisión del equipo, D-4 y D-18).

## Cómo trabajar en este repo

- Funcionalidades, criterios de aceptación y decisiones abiertas: `docs/SPEC.md`.
- Ramas y commits: `CONTRIBUTING.md` (Conventional Commits con el ID `F-XX`).
- Antes de un PR: `./gradlew lint testDebugUnitTest assembleDebug` (igual que el CI)
  y el checklist de `.github/PULL_REQUEST_TEMPLATE.md`.
- Agentes de Claude Code en `.claude/agents/`: `orquestador` (coordina),
  `desarrollador` (implementa y abre PR), `qa` (revisa) y `tester-android`
  (prueba en emulador). El merge siempre lo hace una persona del equipo.
