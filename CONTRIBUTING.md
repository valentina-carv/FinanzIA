# Contribuir a FinanzIA

## Estrategia de ramas
- `main`: versión estable (la que se entrega).
- `develop`: integración. Todos los PRs van aquí.
- `feature/f<XX>-<descripcion>`: una rama por funcionalidad del SPEC, ej. `feature/f03-login`.
- `fix/f<XX>-<descripcion>`: correcciones.

## Conventional Commits
`tipo(área): descripción corta (F-XX)`

Tipos: `feat`, `fix`, `refactor`, `docs`, `test`, `chore`, `style`.
Áreas: `auth`, `onboarding`, `theme`, `data`, `navigation`, `supabase`, `dashboard`,
`transactions`, `budget`, `reports`, `goals`, `ai`, `offline`, `profile`.
Ejemplo: `feat(auth): mostrar "La contraseña es incorrecta" en login (F-03)`

## Antes de abrir un PR
- [ ] `./gradlew lint testDebugUnitTest assembleDebug` pasa en verde.
- [ ] La UI no llama a Supabase directamente (`Screen → ViewModel → Repository`).
- [ ] Ningún color o espaciado está hardcodeado: todo sale de `ui/theme/`.
- [ ] Los ViewModel no importan `androidx.compose.*`.
- [ ] `local.properties` no está en el commit.
- [ ] La pantalla corresponde a su referencia en `/design`.
- [ ] Cada persona del equipo puede explicar lo que hace el código.

## Merge
Lo hace una persona del equipo después de que QA y la prueba en emulador pasen.
Nunca se hace push directo a `main` ni a `develop`.
