-- =====================================================================
-- FinanzIA — Datos de prueba: 3 usuarios con movimientos, una meta y
-- una conversación. Ejecutar DESPUÉS de 002_seguridad_y_auth.sql.
-- Se puede ejecutar varias veces: no duplica usuarios ni movimientos.
--
-- | Usuario        | Correo               | Contraseña   | Escenario                       |
-- |----------------|----------------------|--------------|---------------------------------|
-- | Ana Torres     | ana@finanzia.test    | Finanzia2026 | Independiente, ingreso diario   |
-- | Carlos Ruiz    | carlos@finanzia.test | Finanzia2026 | Estudiante, ingreso quincenal   |
-- | Lucía Martínez | lucia@finanzia.test  | Finanzia2026 | Empleada, ingreso mensual, meta |
--
-- Cuentas de prueba: no usar en producción.
-- =====================================================================

-- Crea el usuario en Supabase Auth (correo ya confirmado). El trigger de la
-- migración 002 crea su fila en `usuarios`, la cuenta "Efectivo" y las categorías.
create or replace function pg_temp.crear_usuario_prueba(p_email text, p_password text, p_nombre text)
returns bigint
language plpgsql
as $$
declare
  v_auth uuid;
begin
  select id into v_auth from auth.users where email = p_email;

  if v_auth is null then
    v_auth := gen_random_uuid();

    insert into auth.users (
      instance_id, id, aud, role, email, encrypted_password,
      email_confirmed_at, raw_app_meta_data, raw_user_meta_data,
      created_at, updated_at,
      confirmation_token, recovery_token, email_change, email_change_token_new
    ) values (
      '00000000-0000-0000-0000-000000000000', v_auth, 'authenticated', 'authenticated',
      p_email, extensions.crypt(p_password, extensions.gen_salt('bf')),
      now(),
      '{"provider":"email","providers":["email"]}'::jsonb,
      jsonb_build_object('full_name', p_nombre),
      now(), now(),
      '', '', '', ''                         -- Supabase exige '' (no null)
    );

    insert into auth.identities (
      id, user_id, provider_id, identity_data, provider,
      last_sign_in_at, created_at, updated_at
    ) values (
      gen_random_uuid(), v_auth, v_auth::text,
      jsonb_build_object('sub', v_auth::text, 'email', p_email, 'email_verified', true),
      'email', now(), now(), now()
    );
  end if;

  return (select id_usuario from public.usuarios where auth_id = v_auth);
end;
$$;

-- Atajos para buscar la cuenta y la categoría de un usuario por nombre.
create or replace function pg_temp.cuenta(p_usuario bigint) returns bigint language sql as $$
  select min(id_cuenta) from public.cuentas where id_usuario = p_usuario and nombre = 'Efectivo';
$$;
create or replace function pg_temp.cat(p_usuario bigint, p_nombre text) returns bigint language sql as $$
  select min(id_categoria) from public.categorias where id_usuario = p_usuario and nombre = p_nombre;
$$;

do $$
declare
  ana    bigint := pg_temp.crear_usuario_prueba('ana@finanzia.test',    'Finanzia2026', 'Ana Torres');
  carlos bigint := pg_temp.crear_usuario_prueba('carlos@finanzia.test', 'Finanzia2026', 'Carlos Ruiz');
  lucia  bigint := pg_temp.crear_usuario_prueba('lucia@finanzia.test',  'Finanzia2026', 'Lucía Martínez');
  meta_pc bigint;
  conv    bigint;
begin
  -- Limpia los datos de prueba anteriores para no duplicar.
  delete from public.movimientos    where id_usuario in (ana, carlos, lucia);
  delete from public.metas          where id_usuario in (ana, carlos, lucia);  -- borra sus aportes
  delete from public.conversaciones where id_usuario in (ana, carlos, lucia);  -- borra sus mensajes

  insert into public.movimientos (id_usuario, id_cuenta, id_categoria, tipo, monto, frecuencia, descripcion, fecha) values
    -- Ana: vende arepas, ingresos diarios
    (ana, pg_temp.cuenta(ana), pg_temp.cat(ana, 'Ventas'),       'ingreso',  85000, 'diario', 'Ventas del día',          now() - interval '2 days'),
    (ana, pg_temp.cuenta(ana), pg_temp.cat(ana, 'Ventas'),       'ingreso',  92000, 'diario', 'Ventas del día',          now() - interval '1 day'),
    (ana, pg_temp.cuenta(ana), pg_temp.cat(ana, 'Alimentación'), 'gasto',   120000, null,     'Insumos: harina y queso', now() - interval '2 days'),
    (ana, pg_temp.cuenta(ana), pg_temp.cat(ana, 'Vivienda'),     'gasto',   450000, null,     'Arriendo del local',      now() - interval '5 days'),
    (ana, pg_temp.cuenta(ana), pg_temp.cat(ana, 'Transporte'),   'gasto',    18000, null,     'Taxi al mercado',         now() - interval '1 day'),
    -- Carlos: estudiante que trabaja medio tiempo
    (carlos, pg_temp.cuenta(carlos), pg_temp.cat(carlos, 'Salario'),         'ingreso', 650000, 'quincenal', 'Pago quincena',        now() - interval '10 days'),
    (carlos, pg_temp.cuenta(carlos), pg_temp.cat(carlos, 'Educación'),       'gasto',   180000, null,        'Fotocopias y libros',  now() - interval '8 days'),
    (carlos, pg_temp.cuenta(carlos), pg_temp.cat(carlos, 'Entretenimiento'), 'gasto',    35000, null,        'Cine',                 now() - interval '3 days'),
    (carlos, pg_temp.cuenta(carlos), pg_temp.cat(carlos, 'Transporte'),      'gasto',    60000, null,        'Recarga TransMilenio', now() - interval '7 days'),
    -- Lucía: empleada con sueldo mensual
    (lucia, pg_temp.cuenta(lucia), pg_temp.cat(lucia, 'Salario'),      'ingreso', 2500000, 'mensual', 'Nómina',               now() - interval '15 days'),
    (lucia, pg_temp.cuenta(lucia), pg_temp.cat(lucia, 'Vivienda'),     'gasto',    900000, null,      'Arriendo apartamento', now() - interval '14 days'),
    (lucia, pg_temp.cuenta(lucia), pg_temp.cat(lucia, 'Servicios'),    'gasto',    220000, null,      'Luz, agua e internet', now() - interval '12 days'),
    (lucia, pg_temp.cuenta(lucia), pg_temp.cat(lucia, 'Alimentación'), 'gasto',    350000, null,      'Mercado del mes',      now() - interval '13 days'),
    (lucia, pg_temp.cuenta(lucia), pg_temp.cat(lucia, 'Compras'),      'gasto',     45000, null,      'Regalo de cumpleaños', now() - interval '4 days');

  -- Lucía ahorra para un computador.
  insert into public.metas (id_usuario, nombre, monto_objetivo, fecha_limite)
  values (lucia, 'Comprar computador', 4000000, now() + interval '6 months')
  returning id_meta into meta_pc;

  insert into public.aportes (id_meta, id_cuenta, monto, fecha) values
    (meta_pc, pg_temp.cuenta(lucia), 1000000, now() - interval '30 days'),
    (meta_pc, pg_temp.cuenta(lucia),  500000, now() - interval '10 days');

  -- Ana ya le preguntó algo a FinanzIA.
  insert into public.conversaciones (id_usuario, titulo)
  values (ana, '¿En qué gasto más?')
  returning id_conversacion into conv;

  insert into public.mensajes (id_conversacion, rol, contenido) values
    (conv, 'usuario',   '¿En qué estoy gastando más?'),
    (conv, 'asistente', 'Tu mayor gasto es Vivienda, con $450.000: el 77% de lo que has gastado.');
end $$;

-- Verificación: debe mostrar 3 usuarios con sus totales.
select u.nombre, u.correo,
       (select count(*) from public.categorias k where k.id_usuario = u.id_usuario) as categorias,
       count(m.id_movimiento) as movimientos,
       coalesce(sum(m.monto) filter (where m.tipo = 'ingreso'), 0) as ingresos,
       coalesce(sum(m.monto) filter (where m.tipo = 'gasto'),   0) as gastos
from public.usuarios u
left join public.movimientos m on m.id_usuario = u.id_usuario
where u.correo like '%@finanzia.test'
group by u.id_usuario, u.nombre, u.correo
order by u.nombre;
