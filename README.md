# FinanzIA

App Android de finanzas personales con un asistente de IA que analiza los datos
**reales** del usuario. Registra ingresos y gastos, controla presupuestos y metas,
muestra estadísticas y da recomendaciones concretas:

> "Este mes has gastado $820.000 de tus $1.200.000 presupuestados. La categoría en
> la que más has gastado es alimentación."

Proyecto académico grupal (Universidad Jorge Tadeo Lozano).
Kotlin · Jetpack Compose · Supabase (Auth + PostgreSQL con RLS) · Edge Function para la IA.

## Qué hay en este repositorio

| Ruta | Para qué |
|---|---|
| `CLAUDE.md` | Arquitectura, sistema de diseño, tono de voz y reglas. Léelo antes de programar |
| `docs/SPEC.md` | Funcionalidades `F-00` a `F-20`, criterios de aceptación y decisiones abiertas |
| `CONTRIBUTING.md` | Ramas, commits y checklist de PR |
| `design/` | Capturas del prototipo, una carpeta por pantalla |
| `supabase/` | Esquema del equipo, migración de seguridad y usuarios de prueba |
| `.claude/agents/` | Agentes de Claude Code: `orquestador`, `desarrollador`, `qa`, `tester-android` |
| `.github/` | CI (lint, pruebas y compilación), plantilla de PR y de bugs |

## Módulos de la app

| Módulo | Qué hace | SPEC |
|---|---|---|
| Autenticación | Iniciar sesión, crear cuenta, recuperar contraseña | F-03 a F-06 |
| Onboarding | Primeros pasos, primer ingreso, primer gasto | F-07 a F-09 |
| Inicio | Saldo, ingresos vs. gastos, gastos por categoría, "FinanzIA dice" | F-10, F-11 |
| Movimientos | Lista y registro de ingresos y gastos | F-12 |
| Presupuestos | Límite por categoría y mes con alertas | F-13 |
| Reportes | Totales, promedio diario, distribución, comparación de meses | F-14 |
| Metas | Objetivo, aportes y progreso | F-15 |
| FinanzIA | Chat que responde con los datos reales del usuario | F-16 |
| Sin internet | Estado offline sin cierres ni errores técnicos | F-17 |
| Perfil | Datos del usuario y cerrar sesión | F-18 |

Flujo de la app: `Screen (Compose) → ViewModel → Repository → Supabase`.

## Puesta en marcha (una sola vez)

### 1. Proyecto Android
Android Studio → *New Project → Empty Activity*: nombre `FinanzIA`, paquete
`com.finanzia.app`, minSdk 26, Kotlin DSL. Ejecuta ▶ y verifica que abre en el emulador.

### 2. Este contenido en el proyecto
Copia **todo** el contenido de este repositorio en la raíz del proyecto, al lado
de `app/` y `settings.gradle.kts`. `.claude`, `.github` y `.gitignore` son ocultos:
actívalos para verlos antes de copiar.

### 3. GitHub
Crea un repositorio **vacío y privado** llamado `FinanzIA` (sin README ni
.gitignore) y súbelo:
```
git init
git add .
git status          # local.properties NO debe aparecer
git commit -m "chore: proyecto base y kit de trabajo (F-00)"
git branch -M main
git remote add origin https://github.com/<usuario>/FinanzIA.git
git push -u origin main
git switch -c develop && git push -u origin develop
```
En GitHub: *Settings → General → Default branch* = `develop`, y agrega a cada
integrante en *Settings → Collaborators*. El CI de *Actions* debe salir en verde.

### 4. Supabase
El esquema del equipo ya existe (`supabase/001_esquema_equipo.sql`, solo referencia).

1. **SQL Editor → New query**: pega `supabase/002_seguridad_y_auth.sql` y ejecuta.
   Activa RLS y conecta `usuarios` con Supabase Auth. No borra datos.
2. Nueva consulta: pega `supabase/003_datos_prueba.sql` y ejecuta.
3. **Authentication → Users**: deben aparecer los 3 correos de prueba.
4. **Table Editor**: ninguna tabla debe decir "Unrestricted".
5. **Project Settings → API**: copia la *Project URL* y la *anon public key* al
   final de `local.properties` (este archivo nunca se sube a Git; compártelo
   con el equipo por un canal privado):
   ```
   SUPABASE_URL=https://xxxx.supabase.co
   SUPABASE_ANON_KEY=eyJhbGci...
   ```

Cuentas de prueba (contraseña `Finanzia2026`): `ana@finanzia.test`,
`carlos@finanzia.test`, `lucia@finanzia.test`. Detalle en `docs/SPEC.md` sección 6.

Si `003_datos_prueba.sql` falla al crear usuarios, créalos en *Authentication →
Add user → Create new user* con *Auto Confirm User* y `{"full_name": "Ana Torres"}`
en *User Metadata*, y vuelve a ejecutar el script (no duplica nada).

## Trabajar con los agentes

1. Instala Claude Code y GitHub CLI (`gh auth login`).
2. Abre una terminal en la carpeta del proyecto y ejecuta `claude`.
3. Pide: *"Usa el agente orquestador para implementar F-01"*.
4. El orquestador muestra un plan, el desarrollador abre un PR contra `develop`,
   QA lo revisa y el tester lo prueba en el emulador (debe estar abierto).
   **Una persona del equipo revisa y hace el merge.**

Orden: hito 1 `F-00 → F-10`, luego `F-11 → F-20`.

## Principio de trabajo

Cada paso se construye, se ejecuta, se prueba, se entiende y se guarda en Git
antes de seguir. Nada de cientos de líneas que nadie entiende.

## Verificación

Antes de abrir un PR, los mismos comandos que el CI:
```
./gradlew lint testDebugUnitTest assembleDebug
```
