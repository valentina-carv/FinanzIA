-- =====================================================================
-- FinanzIA — Migración 002: seguridad y conexión con Supabase Auth
-- Se ejecuta SOBRE el esquema que ya existe (001_esquema_equipo.sql).
-- No borra tablas ni datos. Se puede ejecutar varias veces sin romper nada.
-- Supabase → SQL Editor → New query → pegar todo → Run.
--
-- Qué arregla:
--   1. La contraseña deja de guardarse en `usuarios`: la guarda Supabase Auth
--      con hash bcrypt. `usuarios` queda enlazada a auth.users con `auth_id`.
--   2. Activa Row Level Security (RLS) en las 9 tablas: cada usuario solo ve
--      y modifica sus propios datos. Sin esto, cualquiera con la anon key
--      podía leer TODAS las tablas.
--   3. Al registrarse, el usuario recibe automáticamente su fila en
--      `usuarios`, una cuenta "Efectivo" y sus categorías por defecto.
--   4. Reglas de datos: tipos válidos, montos mayores a 0, frecuencia de ingreso.
--   5. Función email_registrado() para los mensajes de error del login.
-- =====================================================================


-- ---------------------------------------------------------------------
-- 1. usuarios ↔ Supabase Auth
-- ---------------------------------------------------------------------
alter table public.usuarios
  add column if not exists auth_id uuid unique references auth.users(id) on delete cascade;

-- La contraseña ya no se guarda aquí. Se deja la columna (vacía) para no
-- romper nada; se puede borrar cuando el equipo lo confirme:
--   alter table public.usuarios drop column contrasena;
alter table public.usuarios alter column contrasena drop not null;
comment on column public.usuarios.contrasena is
  'OBSOLETA: la contraseña la guarda Supabase Auth (auth.users) con hash. No usar.';

-- El correo no se repite aunque cambien mayúsculas (Ana@ y ana@ son el mismo).
create unique index if not exists usuarios_correo_minusculas_idx
  on public.usuarios (lower(correo));


-- ---------------------------------------------------------------------
-- 2. ¿Quién es el usuario actual? Traduce el uuid de la sesión
--    (auth.uid()) al id_usuario numérico de nuestras tablas.
-- ---------------------------------------------------------------------
create or replace function public.id_usuario_actual()
returns bigint
language sql
stable
security definer set search_path = ''
as $$
  select id_usuario from public.usuarios where auth_id = auth.uid();
$$;

grant execute on function public.id_usuario_actual() to authenticated;

-- La app no necesita enviar id_usuario: se llena solo con el usuario de la sesión.
alter table public.categorias     alter column id_usuario set default public.id_usuario_actual();
alter table public.cuentas        alter column id_usuario set default public.id_usuario_actual();
alter table public.metas          alter column id_usuario set default public.id_usuario_actual();
alter table public.movimientos    alter column id_usuario set default public.id_usuario_actual();
alter table public.conversaciones alter column id_usuario set default public.id_usuario_actual();


-- ---------------------------------------------------------------------
-- 3. Reglas de datos. NOT VALID = solo se exigen a filas nuevas, así las
--    filas que ya existan no hacen fallar la migración.
-- ---------------------------------------------------------------------
alter table public.movimientos
  add column if not exists frecuencia varchar;   -- solo ingresos (pantalla "Primer ingreso")

alter table public.categorias  drop constraint if exists categorias_tipo_valido;
alter table public.categorias  add  constraint categorias_tipo_valido
  check (tipo in ('ingreso', 'gasto')) not valid;

alter table public.movimientos drop constraint if exists movimientos_tipo_valido;
alter table public.movimientos add  constraint movimientos_tipo_valido
  check (tipo in ('ingreso', 'gasto')) not valid;

alter table public.movimientos drop constraint if exists movimientos_monto_positivo;
alter table public.movimientos add  constraint movimientos_monto_positivo
  check (monto > 0) not valid;

alter table public.movimientos drop constraint if exists movimientos_frecuencia_valida;
alter table public.movimientos add  constraint movimientos_frecuencia_valida
  check (frecuencia is null
         or (tipo = 'ingreso' and frecuencia in ('mensual', 'quincenal', 'semanal', 'diario'))) not valid;

alter table public.metas   drop constraint if exists metas_objetivo_positivo;
alter table public.metas   add  constraint metas_objetivo_positivo check (monto_objetivo > 0) not valid;

alter table public.aportes drop constraint if exists aportes_monto_positivo;
alter table public.aportes add  constraint aportes_monto_positivo check (monto > 0) not valid;

alter table public.mensajes drop constraint if exists mensajes_rol_valido;
alter table public.mensajes add  constraint mensajes_rol_valido
  check (rol in ('usuario', 'asistente')) not valid;

-- Índices para las búsquedas que hará la app.
create index if not exists categorias_usuario_idx     on public.categorias (id_usuario);
create index if not exists cuentas_usuario_idx        on public.cuentas (id_usuario);
create index if not exists metas_usuario_idx          on public.metas (id_usuario);
create index if not exists conversaciones_usuario_idx on public.conversaciones (id_usuario);


-- ---------------------------------------------------------------------
-- 4. Colores base (para categorías y cuentas). Solo se insertan si faltan.
-- ---------------------------------------------------------------------
insert into public.colores (color)
select c from (values ('#10B981'), ('#0062F0'), ('#E23D68'), ('#F59E0B'),
                      ('#8B5CF6'), ('#14B8A6'), ('#EC4899'), ('#64748B')) as v(c)
where not exists (select 1 from public.colores where color = v.c);


-- ---------------------------------------------------------------------
-- 5. Al registrarse un usuario en Supabase Auth:
--    crea (o enlaza) su fila en `usuarios`, su cuenta "Efectivo" y sus
--    categorías por defecto.
-- ---------------------------------------------------------------------
create or replace function public.preparar_usuario_nuevo()
returns trigger
language plpgsql
security definer set search_path = ''
as $$
declare
  v_id bigint;
  v_color_por_hex jsonb;
begin
  -- Si ya existía en `usuarios` con ese correo (datos viejos), se enlaza.
  update public.usuarios
     set auth_id = new.id
   where lower(correo) = lower(new.email) and auth_id is null
  returning id_usuario into v_id;

  if v_id is null then
    insert into public.usuarios (nombre, correo, auth_id)
    values (coalesce(nullif(new.raw_user_meta_data ->> 'full_name', ''), split_part(new.email, '@', 1)),
            new.email, new.id)
    returning id_usuario into v_id;
  end if;

  -- Cuenta por defecto: los movimientos exigen una cuenta.
  if not exists (select 1 from public.cuentas where id_usuario = v_id) then
    insert into public.cuentas (id_usuario, id_color, nombre, tipo)
    values (v_id, (select min(id_color) from public.colores where color = '#10B981'),
            'Efectivo', 'efectivo');
  end if;

  -- Categorías por defecto (las del documento de contexto).
  if not exists (select 1 from public.categorias where id_usuario = v_id) then
    insert into public.categorias (id_usuario, id_color, nombre, tipo)
    select v_id, (select min(id_color) from public.colores where color = v.hex), v.nombre, v.tipo
    from (values
      ('Vivienda',        'gasto',   '#0062F0'),
      ('Alimentación',    'gasto',   '#F59E0B'),
      ('Transporte',      'gasto',   '#14B8A6'),
      ('Servicios',       'gasto',   '#64748B'),
      ('Educación',       'gasto',   '#8B5CF6'),
      ('Entretenimiento', 'gasto',   '#EC4899'),
      ('Compras',         'gasto',   '#E23D68'),
      ('Otros gastos',    'gasto',   '#64748B'),
      ('Salario',         'ingreso', '#10B981'),
      ('Ventas',          'ingreso', '#10B981'),
      ('Otros ingresos',  'ingreso', '#10B981')
    ) as v(nombre, tipo, hex);
  end if;

  return new;
end;
$$;

drop trigger if exists al_crear_usuario on auth.users;
create trigger al_crear_usuario
  after insert on auth.users
  for each row execute function public.preparar_usuario_nuevo();


-- ---------------------------------------------------------------------
-- 6. Row Level Security en las 9 tablas
-- ---------------------------------------------------------------------
alter table public.usuarios       enable row level security;
alter table public.colores        enable row level security;
alter table public.categorias     enable row level security;
alter table public.cuentas        enable row level security;
alter table public.metas          enable row level security;
alter table public.movimientos    enable row level security;
alter table public.aportes        enable row level security;
alter table public.conversaciones enable row level security;
alter table public.mensajes       enable row level security;

-- usuarios: solo ve y edita su propia fila (se crea sola con el trigger).
drop policy if exists "usuarios_propio_select" on public.usuarios;
create policy "usuarios_propio_select" on public.usuarios
  for select to authenticated using (auth_id = auth.uid());
drop policy if exists "usuarios_propio_update" on public.usuarios;
create policy "usuarios_propio_update" on public.usuarios
  for update to authenticated using (auth_id = auth.uid()) with check (auth_id = auth.uid());

-- colores: catálogo compartido, solo lectura.
drop policy if exists "colores_lectura" on public.colores;
create policy "colores_lectura" on public.colores
  for select to authenticated using (true);

-- Tablas con id_usuario: solo las filas propias.
drop policy if exists "categorias_propias" on public.categorias;
create policy "categorias_propias" on public.categorias
  for all to authenticated
  using (id_usuario = public.id_usuario_actual())
  with check (id_usuario = public.id_usuario_actual());

drop policy if exists "cuentas_propias" on public.cuentas;
create policy "cuentas_propias" on public.cuentas
  for all to authenticated
  using (id_usuario = public.id_usuario_actual())
  with check (id_usuario = public.id_usuario_actual());

drop policy if exists "metas_propias" on public.metas;
create policy "metas_propias" on public.metas
  for all to authenticated
  using (id_usuario = public.id_usuario_actual())
  with check (id_usuario = public.id_usuario_actual());

drop policy if exists "conversaciones_propias" on public.conversaciones;
create policy "conversaciones_propias" on public.conversaciones
  for all to authenticated
  using (id_usuario = public.id_usuario_actual())
  with check (id_usuario = public.id_usuario_actual());

-- movimientos: además, la cuenta y la categoría deben ser del mismo usuario
-- (evita registrar un gasto en la cuenta de otra persona).
drop policy if exists "movimientos_propios" on public.movimientos;
create policy "movimientos_propios" on public.movimientos
  for all to authenticated
  using (id_usuario = public.id_usuario_actual())
  with check (
    id_usuario = public.id_usuario_actual()
    and exists (select 1 from public.cuentas c
                where c.id_cuenta = movimientos.id_cuenta
                  and c.id_usuario = public.id_usuario_actual())
    and (id_categoria is null or exists (
                select 1 from public.categorias k
                where k.id_categoria = movimientos.id_categoria
                  and k.id_usuario = public.id_usuario_actual()))
  );

-- aportes: no tienen id_usuario; se protegen a través de su meta y su cuenta.
drop policy if exists "aportes_propios" on public.aportes;
create policy "aportes_propios" on public.aportes
  for all to authenticated
  using (exists (select 1 from public.metas m
                 where m.id_meta = aportes.id_meta
                   and m.id_usuario = public.id_usuario_actual()))
  with check (
    exists (select 1 from public.metas m
            where m.id_meta = aportes.id_meta
              and m.id_usuario = public.id_usuario_actual())
    and exists (select 1 from public.cuentas c
                where c.id_cuenta = aportes.id_cuenta
                  and c.id_usuario = public.id_usuario_actual())
  );

-- mensajes: se protegen a través de su conversación.
drop policy if exists "mensajes_propios" on public.mensajes;
create policy "mensajes_propios" on public.mensajes
  for all to authenticated
  using (exists (select 1 from public.conversaciones v
                 where v.id_conversacion = mensajes.id_conversacion
                   and v.id_usuario = public.id_usuario_actual()))
  with check (exists (select 1 from public.conversaciones v
                      where v.id_conversacion = mensajes.id_conversacion
                        and v.id_usuario = public.id_usuario_actual()));


-- ---------------------------------------------------------------------
-- 7. ¿El correo está registrado? (mensajes de error del login, F-03)
--    Supabase responde lo mismo si el correo no existe o si la contraseña
--    está mal; con esta función la app sabe cuál de los dos es.
--    Solo devuelve true/false, nunca datos del usuario.
-- ---------------------------------------------------------------------
create or replace function public.email_registrado(p_email text)
returns boolean
language sql
stable
security definer set search_path = ''
as $$
  select exists (select 1 from auth.users where lower(email) = lower(trim(p_email)));
$$;

revoke all on function public.email_registrado(text) from public;
grant execute on function public.email_registrado(text) to anon, authenticated;
