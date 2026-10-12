# FinanzIA — Especificación del producto

> Fuente de verdad funcional para el equipo y los agentes de desarrollo.
> Cada funcionalidad tiene un ID (`F-XX`) y criterios de aceptación (`CA-X`)
> verificables. El desarrollador implementa contra estos criterios y QA aprueba
> o rechaza contra ellos.
>
> Las reglas de arquitectura y del sistema de diseño viven en `CLAUDE.md` y
> prevalecen sobre este documento. Las referencias visuales viven en `/design/`
> (una carpeta por pantalla con `screen.jpg`).
>
> Lo marcado como **(propuesta)** debe confirmarlo el equipo en la sección 9
> antes de implementarlo.

**Versión:** 0.4.1 · **Última actualización:** 2026-10-12

---

## 1. Visión

FinanzIA es una app Android de finanzas personales con un asistente de IA que
analiza los datos **reales** del usuario para darle recomendaciones concretas
("Este mes has gastado $820.000 de tus $1.200.000 presupuestados").

**Métricas de éxito:**
- El usuario registra su primer movimiento en menos de 2 minutos desde que crea la cuenta.
- El usuario conoce su "dinero libre" real (ingresos − gastos fijos).

## 2. Usuarios

| Usuario | Qué necesita lograr |
|---|---|
| Trabajadora independiente con ingresos variables | Saber cuánto entra y sale, aunque sus ingresos sean diarios o irregulares |
| Estudiante que trabaja | Controlar gastos pequeños y llegar a fin de mes |
| Empleado con sueldo fijo | Ver en qué se le va el dinero y ahorrar para una meta |

## 3. Alcance

**Hito 1 (este documento lo detalla):** base del proyecto, sistema de diseño,
base de datos con usuarios de prueba, autenticación y onboarding (7 pantallas).

**Hitos siguientes (sección 8, Fases 2 y 3):** dashboard, movimientos, presupuestos,
reportes, metas, asistente FinanzIA, modo sin internet, perfil, pruebas y APK final.

**Fuera de alcance:** iOS, web, tema oscuro, conexión con bancos, login social.

## 4. Requisitos no funcionales

| Área | Requisito |
|---|---|
| Plataforma | Android, minSdk 26, compileSdk/targetSdk 35, JDK 17 |
| UI | 100% Jetpack Compose, sin layouts XML de pantalla |
| Diseño | Tokens solo desde `ui/theme/` (ver `CLAUDE.md`) |
| Idioma | Todo el copy en español, en `res/values/strings.xml` |
| Accesibilidad | Áreas táctiles mínimas de 48dp y `contentDescription` en íconos interactivos |
| Datos | Supabase (Auth + PostgreSQL) con RLS en todas las tablas |
| Secretos | `SUPABASE_URL` y `SUPABASE_ANON_KEY` en `local.properties`; ninguna otra llave en la app |
| Dinero | `Long` en pesos colombianos, formato `es-CO` (D-6) |
| Errores | Los repositorios devuelven `Result<T>`; la UI nunca muestra mensajes técnicos en inglés |
| Calidad | Cada PR pasa `./gradlew lint testDebugUnitTest assembleDebug` (igual que el CI) |

## 5. Arquitectura

Un solo módulo `app`, paquete `com.finanzia.app`, capas `ui` → `data` → `domain`.
Detalle obligatorio en `CLAUDE.md`. Flujo: `Screen → ViewModel → Repository → Supabase`.

## 6. Modelo de datos

Base: el esquema del equipo que ya existe en Supabase (`supabase/001_esquema_equipo.sql`),
más la migración `supabase/002_seguridad_y_auth.sql` (probada) y los datos de
prueba `supabase/003_datos_prueba.sql`.

| Tabla | Campos clave | Notas |
|---|---|---|
| `auth.users` (Supabase) | correo, contraseña con hash bcrypt | La maneja Supabase Auth. **La contraseña solo vive aquí** |
| `usuarios` | `id_usuario`, `nombre`, `correo`, `auth_id` | Se crea sola al registrarse (trigger). `contrasena` queda obsoleta y vacía |
| `colores` | `id_color`, `color` (hex) | Catálogo compartido, solo lectura |
| `categorias` | `id_categoria`, `id_usuario`, `id_color`, `nombre`, `tipo` (`ingreso`/`gasto`), `activo` | Cada usuario recibe 11 por defecto al registrarse |
| `cuentas` | `id_cuenta`, `id_usuario`, `nombre`, `tipo`, `saldo_inicial`, `activo` | Cada usuario recibe la cuenta "Efectivo" al registrarse |
| `movimientos` | `id_movimiento`, `id_usuario`, `id_cuenta`, `id_categoria`, `tipo`, `monto`, `frecuencia`, `descripcion`, `fecha`, `activo` | `frecuencia` (nueva, solo ingresos): `mensual`/`quincenal`/`semanal`/`diario` |
| `metas` / `aportes` | objetivo, fecha límite / monto aportado desde una cuenta | Fase 2 (F-15) |
| `conversaciones` / `mensajes` | historial del asistente, `rol` = `usuario`/`asistente` | Fase 2 (F-16) |

Reglas que aplica la base de datos (no hace falta repetirlas en la app, pero
sí validarlas antes para mostrar mensajes claros):
- `id_usuario` se llena solo con el usuario de la sesión: la app no lo envía.
- RLS en las 9 tablas: cada usuario solo ve y modifica lo suyo; no puede usar
  la cuenta, categoría o meta de otro.
- `monto > 0`, `tipo` en minúsculas (`ingreso`/`gasto`), frecuencia solo en ingresos.
- `email_registrado(correo) → boolean`: distingue "correo no registrado" de
  "contraseña incorrecta" en el login (F-03).

### Usuarios de prueba (F-02)

| Nombre | Correo | Contraseña | Escenario |
|---|---|---|---|
| Ana Torres | `ana@finanzia.test` | `Finanzia2026` | Independiente, ingreso diario, gasta más de lo que gana, tiene una conversación con la IA |
| Carlos Ruiz | `carlos@finanzia.test` | `Finanzia2026` | Estudiante, ingreso quincenal |
| Lucía Martínez | `lucia@finanzia.test` | `Finanzia2026` | Empleada, ingreso mensual, meta "Comprar computador" con 2 aportes |

## 7. Pantallas y referencias de diseño

| Pantalla | Paquete | Referencia en `/design/` | Funcionalidad |
|---|---|---|---|
| Iniciar sesión | `ui/screens/auth/login` | `iniciar_sesion` | F-03 |
| Crear cuenta | `ui/screens/auth/register` | `crear_cuenta` | F-04 |
| Cuenta creada | `ui/screens/auth/welcome` | `cuenta_creada` | F-05 |
| Recuperar contraseña | `ui/screens/auth/forgot` | `recuperar_contrasena` | F-06 |
| Nueva contraseña (paso 2) | `ui/screens/auth/forgot` | `recuperar_contrasena_paso2`, `recuperar_contrasena_error` | F-06 (D-19) |
| Acceso autorizado | `ui/screens/auth/welcome` | `acceso_autorizado` | F-03, F-06 (D-18) |
| Primeros pasos | `ui/screens/onboarding/start` | `onboarding_primeros_pasos` | F-07 |
| Primer ingreso | `ui/screens/onboarding/income` | `onboarding_primer_ingreso` | F-08 |
| Primer gasto | `ui/screens/onboarding/expense` | `onboarding_primer_gasto` | F-09 |
| Inicio (dashboard) | `ui/screens/home` | `dashboard` (`screen.jpg` completo, `screen_detalle.jpg` parte superior) | F-11 (D-20) |

Flujo del hito 1:

```
App abre ─┬─ con sesión ──────────────────────────────► Dashboard provisional
          └─ sin sesión ─► Iniciar sesión ─────────────► Dashboard provisional
                              │        └─► Recuperar contraseña
                              └─► Crear cuenta ─► Cuenta creada ─► Primeros pasos
                                                                    ├─► Primer ingreso ─┐
                                                                    ├─► Primer gasto ───┤
                                                                    └─► Explorar ───────┴─► Dashboard provisional
```

## 8. Funcionalidades

Estados: `Pendiente` · `En progreso` · `En revisión` · `Hecho` · `Bloqueada`

### Fase 0 — Base

#### F-00 — Proyecto Android, Git y CI
- **Estado:** Pendiente · **Prioridad:** Alta · **Depende de:** ninguna · **PR:** —
- **CA-1:** El proyecto se crea en Android Studio (Empty Activity, Compose, Kotlin DSL, paquete `com.finanzia.app`, minSdk 26) y abre en el emulador.
- **CA-2:** El repositorio tiene ramas `main` y `develop`; los PRs van a `develop`.
- **CA-3:** El Gradle Wrapper está versionado y el CI (`.github/workflows/ci.yml`) pasa en verde.
- **CA-4:** `local.properties` está en `.gitignore`.

#### F-01 — Sistema de diseño y componentes
- **Estado:** Pendiente · **Prioridad:** Alta · **Depende de:** F-00 · **PR:** —
- **CA-1:** `ui/theme/` tiene `Color.kt`, `Type.kt`, `Shape.kt`, `Spacing.kt` y `Theme.kt` con los tokens de `CLAUDE.md`.
- **CA-2:** `ui/components/` tiene: `FinanziaButton` (degradado verde→azul, variante sólida y estado de carga), `FinanziaTextField` (ícono, etiqueta en mayúsculas, ojo para contraseña, estado de error en coral con mensaje), `FinanziaCard`, `FinanziaLogo` (texto "FINANZIA" con "IA" en verde y ✦), `OptionCard` (emoji, título, descripción, círculo de selección; color de acento configurable verde o coral), `StepPill` y `TipCard`.
- **CA-3:** Cada componente tiene un `@Preview`.

#### F-02 — Base de datos en Supabase con usuarios de prueba
- **Estado:** Pendiente · **Prioridad:** Alta · **Depende de:** F-00 · **PR:** —
- **CA-1:** Sobre el esquema existente se ejecutaron `supabase/002_seguridad_y_auth.sql` y `supabase/003_datos_prueba.sql`.
- **CA-2:** Hay **al menos 3 usuarios** con correo confirmado que pueden iniciar sesión (sección 6), cada uno con movimientos de ejemplo.
- **CA-3:** RLS activo en las 9 tablas: con la anon key y sin sesión no se lee nada; un usuario no puede leer ni escribir datos de otro.
- **CA-3b:** La columna `usuarios.contrasena` no se usa en ningún lugar de la app.
- **CA-4:** La app lee `SUPABASE_URL` y `SUPABASE_ANON_KEY` desde `BuildConfig` y crea un único cliente en `data/supabase/SupabaseClient.kt`.
- **CA-5:** Si `local.properties` no tiene las llaves (como en el CI), el proyecto compila igual con valores vacíos; solo falla al intentar conectarse.

### Fase 1 — Autenticación y onboarding (hito 1)

#### F-03 — Iniciar sesión
- **Estado:** Pendiente · **Prioridad:** Alta · **Depende de:** F-01, F-02 · **PR:** —
- **Referencia:** `design/iniciar_sesion`
- **CA-1 (estructura):** Logo, "Tu asistente financiero con IA", "¡Qué bueno verte! 👋", "Inicia sesión para continuar y seguir creciendo.", campos correo y contraseña (con ojo para mostrar/ocultar), enlace "¿Olvidaste tu contraseña?" y botón "Entrar →". También un enlace "¿No tienes cuenta? Crear cuenta" (el prototipo no lo muestra; se agrega para poder llegar al registro).
- **CA-2 (campos vacíos):** Con algún campo vacío, al tocar "Entrar" se muestra "Completa tu correo y contraseña" y no se llama a Supabase.
- **CA-3 (correo con formato inválido):** Si el correo no tiene formato válido (ej. `ana@finanzia`, `anafinanzia.test`), el campo se marca en coral con **"El correo no es válido. Revisa que esté bien escrito."** y no se llama a Supabase.
- **CA-4 (correo no registrado):** Si el formato es válido pero el correo no existe (ej. `ana@finanzia.tes`), el campo correo se marca en coral con **"Este correo no está registrado en FinanzIA."**
- **CA-5 (contraseña incorrecta):** Si el correo existe y la contraseña tiene aunque sea un carácter distinto (ej. `Finanzia2027`), el campo contraseña se marca en coral con **"La contraseña es incorrecta."** y el campo contraseña se vacía.
- **CA-6 (implementación de CA-4/CA-5):** Supabase responde el mismo error en ambos casos, así que tras un login fallido el repositorio llama a la función `email_registrado(correo)` para elegir el mensaje. El correo se compara sin importar mayúsculas ni espacios.
- **CA-7 (éxito):** Con `ana@finanzia.test` / `Finanzia2026` entra al dashboard provisional que muestra "Hola, Ana Torres" (nombre leído de `usuarios`). "Atrás" no regresa al login.
- **CA-8 (envío):** Mientras se envía, el botón muestra progreso y no permite doble toque.
- **CA-9 (sin internet):** Sin conexión se muestra "No hay conexión a internet. Intenta de nuevo." (no un mensaje en inglés).
- **CA-10 (sesión persistente):** Si hay sesión activa al abrir la app, entra directo sin pasar por el login.
- **CA-11 (pruebas):** Pruebas unitarias del ViewModel para CA-2 a CA-5 con un repositorio falso.

#### F-04 — Crear cuenta
- **Estado:** Pendiente · **Prioridad:** Alta · **Depende de:** F-03 · **PR:** —
- **Referencia:** `design/crear_cuenta`
- **CA-1 (estructura):** Logo, "¡Empieza tu viaje! 🚀", "Toma el control inteligente de tu dinero en minutos.", campos nombre completo, correo y contraseña; casilla "Acepto los Términos de Servicio y la Política de Privacidad"; botón "Crear cuenta →"; enlace "¿Ya tienes una cuenta? Iniciar sesión".
- **CA-2 (reglas de contraseña):** Indicadores "8+ carac.", "1 número" y "1 mayúscula" que se ponen verdes en vivo al cumplirse. (Confirmación de contraseña: ver D-2.)
- **CA-3 (validaciones):** Nombre vacío → "Escribe tu nombre". Correo inválido → mismo mensaje que F-03 CA-3. Contraseña que no cumple → "Tu contraseña aún no cumple los requisitos". Casilla sin marcar → botón deshabilitado.
- **CA-4 (correo repetido):** Si el correo ya existe → "Este correo ya tiene una cuenta. Inicia sesión."
- **CA-5 (éxito):** Crea el usuario con Supabase Auth (`signUp`) enviando `full_name` en los metadatos; el trigger crea su fila en `usuarios`, la cuenta "Efectivo" y sus categorías. La app **nunca** escribe la contraseña en `usuarios`. Navega a "Cuenta creada".

#### F-05 — Cuenta creada (bienvenida)
- **Estado:** Pendiente · **Prioridad:** Media · **Depende de:** F-04 · **PR:** —
- **Referencia:** `design/cuenta_creada`
- **CA-1:** Muestra "🚀 ¡CUENTA CREADA CON ÉXITO!", "¡Te damos la bienvenida a bordo!", el texto de bienvenida y la tarjeta "Todo listo para comenzar" con sus 3 ítems marcados (textos según D-4).
- **CA-2:** Tarjeta "Correo de confirmación enviado" con el correo real del usuario (truncado con "…" si no cabe). Solo se muestra si D-5 activa la confirmación por correo.
- **CA-3:** "Comenzar ahora →" lleva a "Primeros pasos".

#### F-06 — Recuperar contraseña
- **Estado:** Pendiente · **Prioridad:** Media · **Depende de:** F-03 · **PR:** —
- **Referencia:** `design/recuperar_contrasena`
- **CA-1:** Flecha atrás, indicador "PASO 1 DE 2", logo, "¿Olvidaste tu contraseña? 🔐", texto explicativo, campo correo, botón "Verificar y continuar →" y "← Volver a Iniciar sesión". (Campo "Nombre de usuario": ver D-3.)
- **CA-2:** Correo inválido → mensaje de F-03 CA-3. Correo no registrado → "Este correo no está registrado en FinanzIA."
- **CA-3:** Correo registrado → Supabase envía el correo de recuperación y se muestra el paso 2: "Te enviamos un enlace a <correo>. Revisa tu bandeja de entrada."
- **Nota:** el prototipo también trae la pantalla "Nueva contraseña" (`design/recuperar_contrasena_paso2` y su estado de error `design/recuperar_contrasena_error`). Cómo se usa depende de D-19.

#### F-07 — Onboarding: primeros pasos
- **Estado:** Pendiente · **Prioridad:** Alta · **Depende de:** F-05 · **PR:** —
- **Referencia:** `design/onboarding_primeros_pasos`
- **CA-1:** Indicador de paso, logo con etiqueta "IA", "¡Hola! Soy Finanzia 💸", "Tu asistente financiero inteligente", texto de presentación y tarjeta "¿Por dónde te gustaría empezar?".
- **CA-2:** "Quiero registrar mi primer ingreso" (botón degradado) → F-08. "Quiero registrar mi primer gasto" (tarjeta blanca con − coral) → F-09. "Explorar más tarde e ir al Dashboard" → dashboard provisional.
- **CA-3:** Pie con escudo verde: "Tus datos financieros viajan cifrados de punto a punto" (cierto: Supabase usa HTTPS).

#### F-08 — Onboarding: primer ingreso
- **Estado:** Pendiente · **Prioridad:** Alta · **Depende de:** F-07 · **PR:** —
- **Referencia:** `design/onboarding_primer_ingreso`
- **CA-1:** "Paso 2 • Ingresos" en píldora verde, "Registra tu primer ingreso", campo "MONTO DEL INGRESO (COP)" con $ verde, nota "Podrás modificar o agregar más fuentes de ingreso después.", 4 opciones (Mensual, Quincenal, Semanal, Diario) con su descripción, "Consejo de Finanzia", botón "Guardar y continuar →" y "Omitir este paso por ahora".
- **CA-2:** El monto solo acepta dígitos y se formatea en vivo como `$18.500` (D-6). Monto vacío o 0 → "Escribe un monto mayor a $0".
- **CA-3:** "Mensual" viene seleccionado; solo una opción a la vez.
- **CA-4:** Guardar inserta en `movimientos`: `tipo='ingreso'`, `monto`, `frecuencia` elegida, `id_cuenta` = cuenta "Efectivo" del usuario, `id_categoria` = su categoría "Salario" (o "Ventas" si eligió Diario). Al terminar va al dashboard provisional.
- **CA-5:** "Omitir" y la flecha atrás no guardan nada.

#### F-09 — Onboarding: primer gasto
- **Estado:** Pendiente · **Prioridad:** Alta · **Depende de:** F-07 · **PR:** —
- **Referencia:** `design/onboarding_primer_gasto`
- **CA-1:** "Paso 2 • Gastos" en píldora coral, "Registra tu primer gasto", campo "MONTO ESTIMADO DEL GASTO (COP)" con $ coral, nota de FinanzIA, sección "CATEGORÍA DE GASTO", 4 opciones (Gastos Fijos, Comida, Diversión, Transporte), "Dato de Finanzia", botón "Guardar gasto y finalizar →" y "Omitir este paso por ahora".
- **CA-2:** Mismo formato y validación de monto que F-08 CA-2.
- **CA-3:** Inserta en `movimientos`: `tipo='gasto'`, `monto`, `id_cuenta` = cuenta "Efectivo", `id_categoria` = la categoría del usuario mapeada según D-7, `frecuencia` vacía.
- **CA-4:** "Administrar categorías" no se implementa en este hito (D-9): se oculta.

#### F-10 — Dashboard provisional
- **Estado:** Pendiente · **Prioridad:** Media · **Depende de:** F-03 · **PR:** —
- **CA-1:** Muestra "Hola, <nombre>", total de ingresos, total de gastos y saldo del usuario leídos de Supabase, y un botón "Cerrar sesión" que vuelve al login limpiando el historial. Se reemplaza en Fase 2.

### Fase 2 — App completa (hitos 2 y 3)

Se detallan a partir del documento de contexto de FinanzIA. Antes de
implementar cada una, el equipo revisa los criterios y los marca como
confirmados. Orden recomendado: F-11 → F-12 → F-13 → F-14 → F-15 → F-16 → F-17 → F-18.

#### F-11 — Dashboard (Inicio) y barra inferior
- **Estado:** Pendiente · **Prioridad:** Alta · **Depende de:** F-10 · **PR:** —
- **CA-1:** Barra inferior con Inicio, Movimientos, IA, Reportes y Perfil; el destino activo se resalta y "Atrás" desde una pestaña raíz sale de la app.
- **CA-2:** Inicio muestra "Hola, <nombre> 👋", saldo disponible (saldo inicial de cuentas + ingresos − gastos), total de ingresos y total de gastos del mes actual, leídos de Supabase.
- **CA-3:** Lista de gastos del mes por categoría (emoji, nombre, monto), ordenada de mayor a menor.
- **CA-4:** Tarjeta "FinanzIA dice" en violeta con un consejo corto calculado en la app a partir de los datos (ej. categoría con más gasto). En Fase 2 no llama a la IA.
- **CA-5:** Botón flotante "+" que abre el registro de movimiento (F-12).
- **CA-6:** Sin movimientos, se muestra un estado vacío amable que invita a registrar el primero.

#### F-12 — Movimientos: lista y registro
- **Estado:** Pendiente · **Prioridad:** Alta · **Depende de:** F-11 · **PR:** —
- **CA-1:** `TransactionsScreen` lista los movimientos activos del usuario, del más reciente al más antiguo, agrupados por día, con ingresos en verde y gastos en coral.
- **CA-2:** `AddTransactionScreen` permite elegir Ingreso o Gasto, monto (mismo formato y validación que F-08 CA-2), categoría (solo las del tipo elegido), descripción opcional y fecha (por defecto hoy, no futura).
- **CA-3:** Guardar inserta en `movimientos` con la cuenta "Efectivo" (o la elegida) y vuelve a la lista; Inicio se actualiza sin reiniciar la app.
- **CA-4:** Eliminar un movimiento lo marca `activo = false` (no se borra) tras confirmar.
- **CA-5:** Filtros por tipo (Todos / Ingresos / Gastos) y por mes.

#### F-13 — Presupuestos por categoría
- **Estado:** Pendiente · **Prioridad:** Alta · **Depende de:** F-12, D-15 · **PR:** —
- **CA-1:** Migración `supabase/004_presupuestos.sql` crea `presupuestos` (`id_presupuesto`, `id_usuario`, `id_categoria`, `monto`, `mes`, `anio`, `activo`) con RLS y un único presupuesto por categoría y mes.
- **CA-2:** `BudgetScreen` permite crear o editar el límite mensual de una categoría de gasto.
- **CA-3:** Cada presupuesto muestra "gastado / límite" y una barra de progreso: verde por debajo del 80 %, ámbar desde el 80 %, coral al superarlo.
- **CA-4:** Al superar un presupuesto se muestra un aviso con el tono de `CLAUDE.md` (ej. "Este mes pasaste tu límite de Comida por $20.000, ¿lo revisamos?").

#### F-14 — Reportes y estadísticas
- **Estado:** Pendiente · **Prioridad:** Media · **Depende de:** F-12 · **PR:** —
- **CA-1:** Para el mes elegido: total gastado, promedio diario de gasto y categoría con mayor gasto.
- **CA-2:** Gráfica de distribución de gastos por categoría con porcentajes que suman 100 %.
- **CA-3:** Comparación con el mes anterior (más o menos que el mes pasado, en $ y %).
- **CA-4:** Evolución del ahorro (ingresos − gastos) de los últimos 6 meses.
- **CA-5:** Cumplimiento de presupuestos del mes (cuántos dentro del límite y cuántos superados), si existe F-13.
- **CA-6:** Los cálculos viven en `domain/` y tienen pruebas unitarias.

#### F-15 — Metas de ahorro
- **Estado:** Pendiente · **Prioridad:** Media · **Depende de:** F-12 · **PR:** —
- **CA-1:** Crear una meta con nombre, monto objetivo y fecha límite opcional (tabla `metas`).
- **CA-2:** Registrar aportes desde una cuenta (tabla `aportes`); el ahorrado es la suma de aportes.
- **CA-3:** Cada meta muestra objetivo, ahorrado y porcentaje con barra de progreso (ej. $1.500.000 de $4.000.000 → 37,5 %).
- **CA-4:** Si tiene fecha límite, muestra cuánto debería ahorrar por mes para cumplirla.

#### F-16 — Asistente FinanzIA
- **Estado:** Pendiente · **Prioridad:** Alta · **Depende de:** F-12, F-13, F-14 · **PR:** —
- **CA-1:** `FinanziaAIScreen` en violeta: "FinanzIA 🤖 · Tu asistente financiero", saludo y preguntas rápidas: "¿Cómo voy este mes?", "¿En qué gasto más?", "¿Puedo ahorrar $300.000?", "Analiza mis gastos"; campo "Escribe un mensaje...".
- **CA-2:** La pregunta va a una Edge Function de Supabase (`supabase/functions/finanzia-ia/`) que arma el contexto financiero del usuario autenticado y llama al modelo de IA. La llave de IA es un secreto de la función, nunca está en la app.
- **CA-3:** Las respuestas usan cifras reales del usuario (ej. "Tu categoría con mayor gasto este mes es alimentación, con $500.000, el 26 % de tus gastos") y nunca inventan datos; si faltan datos, lo dicen.
- **CA-4:** La conversación se guarda en `conversaciones` / `mensajes` y se recupera al volver a la pantalla.
- **CA-5:** Mientras responde se muestra "FinanzIA está pensando…"; si falla, un mensaje amable en español con opción de reintentar.

#### F-17 — Modo sin internet
- **Estado:** Pendiente · **Prioridad:** Media · **Depende de:** F-11 · **PR:** —
- **CA-1:** Sin conexión se muestra una pantalla o banner "Estás sin conexión. Lo que ves puede no estar actualizado." y un botón "Reintentar".
- **CA-2:** Ninguna pantalla se cierra ni muestra errores técnicos sin conexión.
- **CA-3:** (Opcional, según D-1) caché local con Room de los últimos movimientos para consultarlos sin internet.

#### F-18 — Perfil y cerrar sesión
- **Estado:** Pendiente · **Prioridad:** Media · **Depende de:** F-11 · **PR:** —
- **CA-1:** Muestra nombre y correo del usuario (de `usuarios`) y permite editar el nombre.
- **CA-2:** "Cerrar sesión" pide confirmación, cierra la sesión de Supabase y vuelve al login sin historial.

### Fase 3 — Pruebas y versión final

#### F-19 — Pruebas integrales
- **Estado:** Pendiente · **Prioridad:** Alta · **Depende de:** F-11 a F-18 · **PR:** —
- **CA-1:** Lista de pruebas en `docs/PRUEBAS.md` que cubre registro, login, logout, registro de ingresos y gastos, cálculos, presupuestos, seguridad (RLS), navegación, IA y estados sin conexión, con resultado de cada una.

#### F-20 — Versión final (APK)
- **Estado:** Pendiente · **Prioridad:** Alta · **Depende de:** F-19 · **PR:** —
- **CA-1:** Ícono y nombre de la app definitivos, pantallas finales y validaciones revisadas.
- **CA-2:** APK de release firmado generado y adjunto a un release de GitHub; `README.md` actualizado y repositorio organizado.

## 9. Decisiones abiertas

Los agentes no resuelven estas decisiones: si una tarea depende de una, la
reportan al orquestador.

| # | Pregunta | Afecta a | Decisión |
|---|---|---|---|
| D-1 | ¿Supabase o Room/SQLite local? El documento de Ingeniería decía Room | Todo | **Resuelta:** Supabase con el esquema del equipo (9 tablas). Contraseñas en Supabase Auth, no en `usuarios` (migración 002). Room solo si se exige modo offline real (F-17) |
| D-2 | El documento de contexto pide "confirmar contraseña", el prototipo no tiene ese campo | F-04 | Pendiente. Propuesta: agregar el campo debajo de contraseña |
| D-3 | Recuperar contraseña pide "Nombre de usuario", pero la app no tiene usuarios, solo correo | F-06 | **(propuesta)** quitar ese campo; Supabase solo necesita el correo |
| D-4 | "Cuenta creada" promete AES-256, "IA Copilot" con detección de gastos hormiga y un "bono gratis" que la app no tiene | F-05 | **Resuelta en parte:** lo de AES-256 no se usa en ningún texto de la app (decisión del equipo, 2026-10-12). **(propuesta)** para el resto, cambiar por hechos reales: "Cuenta protegida", "FinanzIA lista para aprender de tus movimientos", "Tu primer resumen te espera" |
| D-5 | ¿Supabase exige confirmar el correo antes de entrar? | F-04, F-05 | **(propuesta)** desactivado durante el desarrollo (el seed ya crea usuarios confirmados) |
| D-6 | El prototipo muestra `3,200.00`; en Colombia se escribe `$3.200` | F-08, F-09 | **(propuesta)** formato `es-CO`, sin decimales |
| D-7 | Categorías del onboarding (Gastos Fijos, Comida, Diversión, Transporte) vs. las 11 que se crean por usuario | F-09, F-12 | **(propuesta)** Gastos Fijos→Vivienda, Comida→Alimentación, Diversión→Entretenimiento, Transporte→Transporte |
| D-14 | ¿Se borra la columna `usuarios.contrasena`? | F-02 | **(propuesta)** borrarla cuando nadie la use: `alter table usuarios drop column contrasena;` |
| D-15 | No hay tabla de presupuestos y el documento de contexto los incluye | F-13 | Pendiente: crear `presupuestos` en una migración 004 cuando se llegue a Fase 2 |
| D-8 | Numeración del onboarding inconsistente ("Paso 1 de 2" vs. "Paso 2 • Gastos") | F-07 a F-09 | **(propuesta)** "Paso 1 de 2 • Primeros pasos" y "Paso 2 de 2 • Ingresos/Gastos" |
| D-9 | "Administrar categorías" y "Contactar a soporte de Finanzia" | F-06, F-09 | **(propuesta)** fuera del hito 1 |
| D-10 | ¿Quién es responsable de cada pantalla? | Todas | Pendiente |
| D-11 | Nombre: "FINANZIA", "FinanzIA" o "Finanzia" (el logo cambia entre pantallas) | F-01 | **(propuesta)** logo "FINANZIA" con "IA" en verde y ✦; en textos "FinanzIA" |
| D-12 | ¿Hay textos de Términos de Servicio y Política de Privacidad? | F-04 | Pendiente |
| D-16 | Dos paletas: la del prototipo (verde, azul, coral) y la del documento de contexto (sección 17, con violeta para la IA) | F-01, F-11, F-16 | **(propuesta)** paleta del prototipo + violeta `#7C5CFF` solo para la IA y ámbar para presupuestos cerca del límite (ver `CLAUDE.md`) |
| D-17 | ¿Qué modelo de IA usa la Edge Function y quién paga la llave? | F-16 | Pendiente |
| D-18 | El prototipo trae "Acceso autorizado" tras iniciar sesión o cambiar la contraseña, con saldo y "insight" de muestra, "Cifrado AES-256", "grado bancario" y "v3.4.1 (Build 2049)" | F-03, F-06 | **Resuelta en parte:** "Cifrado AES-256 Activo" no se muestra (decisión del equipo, 2026-10-12). **(propuesta)** no usarla en el hito 1 (F-03 va directo al dashboard). Si se usa, solo con datos reales y sin promesas que la app no cumple (igual que D-4) |
| D-19 | "Nueva contraseña" pide 4 requisitos (incluye símbolo) y "Cerrar sesión en los demás dispositivos"; el registro (F-04) pide 3. El cambio llega por el enlace de Supabase, que se abre fuera de la app | F-06 | **(propuesta)** mismas 3 reglas que F-04 (una sola `PasswordPolicy`); la pantalla se usa cuando el enlace de recuperación abra la app (deep link), en una fase posterior |
| D-20 | El dashboard del prototipo tiene barra inferior Inicio · Gráficos · Billetera · Metas · Asistente; `CLAUDE.md` define Inicio · Movimientos · IA · Reportes · Perfil. También muestra datos de muestra (Mercadona, `$12,450.00`, "Cuenta Nómina") y la tarjeta de la IA en verde | F-11 | **(propuesta)** usar la imagen como guía visual (tarjeta de saldo con degradado, tarjeta de la IA, meta, movimientos recientes, botón +) y mantener la barra y la regla del violeta de `CLAUDE.md` |
| D-21 | El repo de Tatiana usa el paquete `com.finazia.finanzia` (con typo) y su `CLAUDE.md` dice `com.finanzia.finanzia`; este SPEC dice `com.finanzia.app` | F-00 | **Resuelta:** `com.finanzia.app` (el proyecto Android traído del repo de Tatiana se renombró) |
| D-22 | El README de Tatiana incluye un módulo de **Notificaciones** que no está en este SPEC | Fase 2 | Pendiente: decidir si se agrega como funcionalidad nueva |
| D-13 | Distinguir "correo no registrado" de "contraseña incorrecta" revela qué correos tienen cuenta | F-03 | Aceptado por el equipo para el proyecto académico (requisito explícito). En producción se usaría un mensaje único |

### Responsables (D-10)

| Pantalla / funcionalidad | Responsable |
|---|---|
| F-00 a F-02 | Por asignar |
| F-03 a F-06 | Por asignar |
| F-07 a F-10 | Por asignar |
| F-11 a F-14 | Por asignar |
| F-15 a F-18 | Por asignar |
| F-19 y F-20 | Todo el equipo |

## 10. Registro de cambios

| Fecha | Cambio |
|---|---|
| 2026-10-12 | v0.4.1: lo de "cifrado AES-256" del prototipo no se usa (D-4, D-18). Equipo: Paula Valentina, Juan Pablo y Tatiana |
| 2026-10-12 | v0.4: se agrega el proyecto Android (F-00) tomado del repo de Tatiana con el paquete `com.finanzia.app`, 4 referencias nuevas en `/design/` (nueva contraseña, su error, acceso autorizado y dashboard) y las decisiones D-18 a D-22 |
| 2026-10-10 | v0.3: se detallan F-11 a F-18 con criterios de aceptación a partir del documento de contexto, se agregan F-19 (pruebas) y F-20 (APK), y las decisiones D-16 (paleta) y D-17 (modelo de IA) |
| 2026-10-09 | v0.2: se adopta el esquema existente del equipo (9 tablas). Migración 002: contraseñas en Supabase Auth, RLS en todas las tablas, trigger de registro, columna `frecuencia`, reglas de datos. 14 pruebas de seguridad pasadas |
| 2026-10-09 | v0.1: versión inicial a partir del documento de contexto, el prototipo (7 pantallas) y el flujo de trabajo de KRONO. Esquema y seed de Supabase probados: 3 usuarios, RLS y `email_registrado()` |
