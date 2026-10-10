-- =====================================================================
-- FinanzIA — Esquema original del equipo (ya existe en Supabase).
-- Se guarda aquí solo como referencia y para reconstruir la base desde cero.
-- NO volver a ejecutarlo en el proyecto actual.
-- =====================================================================

create table usuarios (
  id_usuario bigint generated always as identity primary key,
  nombre varchar not null,
  correo varchar not null unique,
  contrasena varchar not null
);

create table colores (
  id_color bigint generated always as identity primary key,
  color varchar not null
);

create table categorias (
  id_categoria bigint generated always as identity primary key,
  id_usuario bigint not null references usuarios(id_usuario) on delete cascade,
  id_color bigint references colores(id_color),
  nombre varchar not null,
  tipo varchar not null,
  activo boolean not null default true
);

create table cuentas (
  id_cuenta bigint generated always as identity primary key,
  id_usuario bigint not null references usuarios(id_usuario) on delete cascade,
  id_color bigint references colores(id_color),
  nombre varchar not null,
  tipo varchar not null,
  saldo_inicial numeric(14,2) not null default 0,
  fecha_creacion timestamptz not null default now(),
  activo boolean not null default true
);

create table metas (
  id_meta bigint generated always as identity primary key,
  id_usuario bigint not null references usuarios(id_usuario) on delete cascade,
  nombre varchar not null,
  monto_objetivo numeric(14,2) not null,
  fecha_limite timestamptz,
  fecha_creacion timestamptz not null default now(),
  activo boolean not null default true
);

create table movimientos (
  id_movimiento bigint generated always as identity primary key,
  id_usuario bigint not null references usuarios(id_usuario) on delete cascade,
  id_cuenta bigint not null references cuentas(id_cuenta),
  id_categoria bigint references categorias(id_categoria),
  tipo varchar not null,
  monto numeric(14,2) not null,
  descripcion varchar,
  fecha timestamptz not null default now(),
  activo boolean not null default true
);

create table aportes (
  id_aporte bigint generated always as identity primary key,
  id_meta bigint not null references metas(id_meta) on delete cascade,
  id_cuenta bigint not null references cuentas(id_cuenta),
  monto numeric(14,2) not null,
  fecha timestamptz not null default now()
);

create table conversaciones (
  id_conversacion bigint generated always as identity primary key,
  id_usuario bigint not null references usuarios(id_usuario) on delete cascade,
  fecha_creacion timestamptz not null default now(),
  titulo varchar,
  activo boolean not null default true
);

create table mensajes (
  id_mensaje bigint generated always as identity primary key,
  id_conversacion bigint not null references conversaciones(id_conversacion) on delete cascade,
  rol varchar not null,
  contenido text not null,
  fecha timestamptz not null default now(),
  activo boolean not null default true
);

create index on movimientos (id_usuario, fecha desc);
create index on movimientos (id_cuenta);
create index on aportes (id_meta);
create index on mensajes (id_conversacion);
