
-- получение версии postgresql
select version();

-- получение текущего подключения к базе данных
select current_database() as database_name, session_user as login_user, current_user as active_role;

-- получение количества строк в исходной таблице
select 
    (select count(*) from public.author) as authors,
    (select count(*) from public.manuscript) as manuscripts,
    (select count(*) from public.sale) as sales,
    (select count(*) from public.publication) as publications,
    (select count(*) from public.partner) as partners;

-- сохранение результатов исходного authors_summary
select * from public.authors_summary order by 1 limit 10;

-- сохранение результатов представления magazine
select * from public.magazine order by 1 limit 10;

-- версия программы pg_dump
\cd 'C:/Program Files/PostgreSQL/18/bin'
\! pg_dump --version
\echo Exit code: :SHELL_EXIT_CODE

-- создание дампа исходной базы
\cd 'C:/Program Files/PostgreSQL/18/bin'
\setenv PGHOST :HOST
\setenv PGPORT :PORT
\setenv PGUSER :USER
\setenv PGDATABASE :DBNAME
\! pg_dump -Fc -f "C:/Dump/Library/practice.dump" -w
\echo Exit code: :SHELL_EXIT_CODE

-- содержимое созданного архива
\cd 'C:/Program Files/PostgreSQL/18/bin'
\! pg_restore -l "C:/Dump/Library/practice.dump"
\echo Exit code: :SHELL_EXIT_CODE

-- создание владельца новой базы данных
create role library_owner login nosuperuser nocreatedb nocreaterole password 'Rbd2026_Owner!42';

-- создание новой базы данных для восстановления
create database library_access owner library_owner;

-- проверка владельца новой базы
select datname as database_name, pg_get_userbyid(datdba) as owner
from pg_database
where datname = 'library_access';

-- проверка подключения к новой базе
select current_database() as database_name, session_user as login_user, current_user as active_role;

-- восстановление дампа в новую базу
\cd 'C:/Program Files/PostgreSQL/18/bin'
\setenv PGHOST :HOST
\setenv PGPORT :PORT
\setenv PGUSER :USER
\setenv PGDATABASE :DBNAME
\! pg_restore -d "%PGDATABASE%" --no-owner --no-acl --exit-on-error "C:/Dump/Library/practice.dump" -w
\echo Exit code: :SHELL_EXIT_CODE

-- количество строк в восстановленной базе
select 
    (select count(*) from public.author) as authors,
    (select count(*) from public.manuscript) as manuscripts,
    (select count(*) from public.sale) as sales,
    (select count(*) from public.publication) as publications,
    (select count(*) from public.partner) as partners;

-- проверка представления authors_summary после восстановления
select * from public.authors_summary order by 1 limit 10;

-- проверка представления magazine после восстановления
select * from public.magazine order by 1 limit 10;

-- ==========================================
-- ЗАДАНИЕ 2. ПЕРЕНОС ОБЪЕКТОВ В СВОЮ СХЕМУ
-- ==========================================

-- создание новой схемы для объектов
create schema library authorization library_owner;

-- перенос таблиц в новую схему
alter table public.author set schema library;
alter table public.manuscript set schema library;
alter table public.sale set schema library;
alter table public.publication set schema library;
alter table public.partner set schema library;

-- перенос представлений в новую схему
alter view public.authors_summary set schema library;
alter view public.magazine set schema library;

-- перенос триггерной функции в новую схему
alter function public.update_aggregates_after_sale_change() set schema library;

-- перенос второй триггерной функции в новую схему
alter function public.validate_sale_item_insert() set schema library;

-- перенос оставшихся связанных таблиц в новую схему
alter table public.sale_item set schema library;
alter table public.employee set schema library;

-- настройка порядка поиска схем для владельца базы
alter role library_owner in database library_access set search_path = library, public;
set search_path = library, public;

-- проверка текущего порядка поиска схем
show search_path;

-- список объектов в новой схеме и их владельцы
select n.nspname as schema_name, c.relname as object_name,
       case c.relkind
           when 'r' then 'таблица'
           when 'v' then 'представление'
           when 'S' then 'последовательность'
       end as object_type,
       pg_get_userbyid(c.relowner) as owner
from pg_class as c
join pg_namespace as n on n.oid = c.relnamespace
where n.nspname = 'library'
  and c.relkind in ('r', 'v', 'S')
order by object_type, object_name;

-- проверка последовательности для id рукописи
select pg_get_serial_sequence('library.manuscript', 'id') as manuscript_sequence;

-- проверка триггера и функции таблицы sale_item после переноса
select t.tgname as trigger_name, 
       c.relname as table_name, 
       n.nspname as function_schema, 
       p.proname as function_name, 
       p.proconfig as function_settings
from pg_trigger as t
join pg_class as c on c.oid = t.tgrelid
join pg_proc as p on p.oid = t.tgfoid
join pg_namespace as n on n.oid = p.pronamespace
where t.tgrelid = 'library.sale_item'::regclass
  and not t.tgisinternal;

-- проверка внешних ключей после переноса
select conrelid::regclass as source_table,
       confrelid::regclass as target_table,
       pg_get_constraintdef(oid) as definition
from pg_constraint
where contype = 'f'
  and connamespace = 'library'::regnamespace
  and conrelid in (
      'library.manuscript'::regclass,
      'library.sale'::regclass,
      'library.publication'::regclass
  )
order by conrelid::regclass::text, conname;

-- проверка количества строк в новой схеме после переноса
select 
    (select count(*) from library.author) as authors,
    (select count(*) from library.manuscript) as manuscripts,
    (select count(*) from library.sale) as sales,
    (select count(*) from library.publication) as publications,
    (select count(*) from library.partner) as partners;

-- проверка представления authors_summary в новой схеме
select * from library.authors_summary order by 1 limit 10;

-- проверка представления magazine в новой схеме
select * from library.magazine order by 1 limit 10;

-- создание групповых ролей для разграничения прав
create role library_read nologin nosuperuser nocreatedb nocreaterole inherit;
create role library_write nologin nosuperuser nocreatedb nocreaterole inherit;
create role library_report nologin nosuperuser nocreatedb nocreaterole inherit;

-- создание пользователей
create role library_reader login nosuperuser nocreatedb nocreaterole inherit password 'Rbd2026_Reader!42';
create role library_operator login nosuperuser nocreatedb nocreaterole inherit password 'Rbd2026_Operator!42';
create role library_analyst login nosuperuser nocreatedb nocreaterole inherit password 'Rbd2026_Analyst!42';

-- включение пользователей в групповые роли
grant library_read to library_reader;
grant library_read to library_write;
grant library_write to library_operator;
grant library_report to library_analyst;

-- настройка схемы по умолчанию для новых пользователей
alter role library_reader in database library_access set search_path = library, public;
alter role library_operator in database library_access set search_path = library, public;
alter role library_analyst in database library_access set search_path = library, public;

-- проверка атрибутов созданных ролей
select rolname, rolcanlogin, rolsuper, rolcreatedb, rolcreaterole, rolinherit
from pg_roles
where rolname in (
    'library_owner', 'library_read', 'library_write',
    'library_report', 'library_reader', 'library_operator', 'library_analyst'
)
order by rolname;

-- проверка членства в групповых ролях
select parent.rolname as group_role, child.rolname as member_role
from pg_auth_members as m
join pg_roles as parent on parent.oid = m.roleid
join pg_roles as child on child.oid = m.member
where parent.rolname in ('library_read', 'library_write', 'library_report')
order by group_role, member_role;

-- удаление общих прав в учебной копии
revoke all privileges on database library_access from public;
revoke all privileges on schema public from public;
revoke all privileges on schema library from public;
revoke all privileges on all tables in schema library from public;
revoke all privileges on all sequences in schema library from public;

-- доступ групп к базе и схеме
grant connect on database library_access to library_read, library_report;
grant usage on schema library to library_read, library_report;

-- чтение пяти исходных таблиц
grant select on table library.author, library.manuscript, library.sale, library.publication, library.partner to library_read;

-- добавление и изменение рукописей
grant insert, update on table library.manuscript to library_write;
grant usage on sequence library.manuscript_id_seq to library_write;

-- доступ аналитика к отчёту authors_summary
grant select on table library.authors_summary to library_report;

-- проверка владельца и настроек отчёта
select c.relname as view_name, pg_get_userbyid(c.relowner) as owner, c.reloptions as options
from pg_class as c 
where c.oid = 'library.authors_summary'::regclass;

-- проверка выданных прав на таблицы и представление
select grantee, table_name, privilege_type
from information_schema.role_table_grants
where table_schema = 'library'
  and grantee in ('library_read', 'library_write', 'library_report')
order by grantee, table_name, privilege_type;

-- проверка эффективных прав пользователей
select u as user_name,
       has_database_privilege(u, 'library_access', 'connect') as can_connect,
       has_schema_privilege(u, 'library', 'usage') as schema_use,
       has_table_privilege(u, 'library.manuscript', 'select') as manuscript_read,
       has_table_privilege(u, 'library.manuscript', 'update') as manuscript_update,
       has_table_privilege(u, 'library.authors_summary', 'select') as report_read
from unnest(array['library_reader', 'library_operator', 'library_analyst']) as users(u);

-- проверка права оператора на последовательность
select has_sequence_privilege('library_operator', 'library.manuscript_id_seq', 'usage') as can_generate_manuscript_id;

-- проверка подключения читателя (под library_access (reader))
select current_database() as database_name, session_user as login_user, current_user as active_role;

-- чтение списка рукописей читателем
select id, title, submission_date, status_id 
from library.manuscript 
order by id limit 10;

-- начало проверки запрета для читателя
begin;

-- попытка изменения рукописи читателем
update library.manuscript set title = title || ' (изменено)' where id = 1;

-- завершение проверки читателя
rollback;

-- проверка подключения оператора (под library_access (operator))
select current_database() as database_name, session_user as login_user, current_user as active_role;

-- выравнивание последовательности id для рукописей (под владельцем)
-- select setval('library.manuscript_id_seq', (select max(id) from library.manuscript));

-- начало пробного добавления рукописи
begin;

-- добавление рукописи оператором
insert into library.manuscript (title, author_id, submission_date, status_id, comment)
values ('Учебная рукопись', 1, date '2026-11-14', 1, 'Проверка прав')
returning id, title, submission_date;

-- отмена пробной вставки
rollback;

-- проверка отсутствия пробной строки
select count(*) as test_rows 
from library.manuscript 
where title = 'Учебная рукопись';

-- начало проверки удаления
begin;

-- попытка удаления рукописи оператором
delete from library.manuscript where id = 1;

-- завершение проверки удаления
rollback;

-- начало проверки создания таблицы
begin;

-- попытка создания таблицы оператором
create table library.test_table (id int);

-- завершение проверки создания таблицы
rollback;

-- проверка подключения аналитика (под library_access (analyst))
select current_database() as database_name, session_user as login_user, current_user as active_role;

-- отчёт, доступный аналитику
select * from library.authors_summary order by 1 limit 10;

-- начало проверки ограничения аналитика
begin;

-- попытка чтения исходной таблицы аналитиком
select * from library.manuscript;

-- завершение проверки аналитика
rollback;

-- отзыв права update у группы library_write (под library_owner)
revoke update on table library.manuscript from library_write;

-- проверка права update после отзыва (под library_owner)
select has_table_privilege('library_operator', 'library.manuscript', 'update') as can_update;

-- начало проверки отозванного права (под library_operator)
begin;

-- попытка обновления рукописи после отзыва
update library.manuscript set title = 'Новое название' where id = 1;

-- завершение проверки отозванного права
rollback;

-- возврат права update (под library_owner)
grant update on table library.manuscript to library_write;

-- начало проверки возвращённого права (под library_operator)
begin;

-- успешное изменение после возврата права
update library.manuscript set title = title || ' (тест)' where id = 1
returning id, title;

-- отмена повторной проверки update
rollback;

-- отзыв членства читателя (под postgres)
revoke library_read from library_reader;

-- проверка прав читателя после исключения из группы (под postgres)
select 
    pg_has_role('library_reader', 'library_read', 'member') as is_member,
    has_database_privilege('library_reader', 'library_access', 'connect') as can_connect;

-- возврат членства читателю (под postgres)
grant library_read to library_reader;

-- чтение после возврата членства (под library_reader)
select current_user as user_name, count(*) as manuscripts_count
from library.manuscript;

-- правило чтения новых таблиц владельца
alter default privileges for role library_owner in schema library
grant select on tables to library_read;

-- создание новой таблицы для проверки правила
create table library.audit_note (note_text text not null);
insert into library.audit_note (note_text) values ('Проверка прав на новую таблицу');

-- проверка права, назначенного новой таблице автоматически
select has_table_privilege('library_reader', 'library.audit_note', 'select') as can_read_new_table;

-- чтение новой таблицы читателем (под library_reader)
select note_text from library.audit_note;

-- отмена правила для будущих таблиц
alter default privileges for role library_owner in schema library
revoke select on tables from library_read;

-- проверка, что право на существующую таблицу сохраняется
select has_table_privilege('library_reader', 'library.audit_note', 'select') as existing_table_read;

-- отзыв права у уже созданной таблицы
revoke select on table library.audit_note from library_read;

-- проверка результата отдельного отзыва права
select has_table_privilege('library_reader', 'library.audit_note', 'select') as existing_table_read;

-- распределение продаж по годам
select extract(year from sale_date)::integer as sale_year, count(*) as sales_count
from library.sale
group by sale_year
order by sale_year;

-- создание секционированной таблицы продаж
create table library.sale_by_year (
    doc_number integer not null,
    partner_id integer,
    employee_id integer,
    sale_date date not null,
    total_amount numeric(10, 2),
    primary key (doc_number, sale_date)
) partition by range (sale_date);

-- создание секций на 2024 и 2026 годы
create table library.sale_2024 partition of library.sale_by_year for values from ('2024-01-01') to ('2025-01-01');
create table library.sale_2026 partition of library.sale_by_year for values from ('2026-01-01') to ('2027-01-01');

-- проверка созданных секций и их границ
select c.relname as partition_name,
       pg_get_expr(c.relpartbound, c.oid) as bounds
from pg_inherits as i
join pg_class as c on c.oid = i.inhrelid
where i.inhparent = 'library.sale_by_year'::regclass
order by c.relname;

-- копирование продаж через родительскую таблицу
insert into library.sale_by_year (doc_number, partner_id, employee_id, sale_date, total_amount)
select doc_number, partner_id, employee_id, sale_date, total_amount
from library.sale;

-- фактическое распределение по секциям
select tableoid::regclass as partition_name, 
       count(*) as sales_count, 
       min(sale_date) as first_date, 
       max(sale_date) as last_date 
from library.sale_by_year 
group by tableoid 
order by tableoid::regclass::text;

-- строки исходной таблицы, отсутствующие в копии
select doc_number, partner_id, employee_id, sale_date, total_amount
from library.sale
except all
select doc_number, partner_id, employee_id, sale_date, total_amount
from library.sale_by_year;

-- лишние строки в секционированной копии
select doc_number, partner_id, employee_id, sale_date, total_amount
from library.sale_by_year
except all
select doc_number, partner_id, employee_id, sale_date, total_amount
from library.sale;

-- проверка отсутствия секции для 2027 года
begin;

-- попытка вставки за 2027 год
insert into library.sale_by_year (doc_number, partner_id, employee_id, sale_date, total_amount)
values (999999, 1, 1, date '2027-02-01', 100.00);

-- отмена неудачной вставки
rollback;

-- подготовка проверки планов
analyze library.sale_by_year;
set enable_partition_pruning = on;

-- проверка включённого отсечения секций
show enable_partition_pruning;

-- план запроса только за 2025 год
explain (costs off)
select * from library.sale_by_year
where sale_date >= date '2025-01-01' 
  and sale_date < date '2026-01-01';

-- план запроса без ограничения по году
explain (costs off)
select * from library.sale_by_year;

-- добавление секции на 2027 год
create table library.sale_2027 partition of library.sale_by_year for values from ('2027-01-01') to ('2028-01-01');

-- начало повторной вставки
begin;

-- вставка данных за 2027 год
insert into library.sale_by_year (doc_number, partner_id, employee_id, sale_date, total_amount)
values (999999, 1, 1, date '2027-02-01', 100.00)
returning tableoid::regclass as partition_name, doc_number, sale_date;

-- отмена пробной строки в новой секции
rollback;

-- проверка новой секции после отмены
select count(*) as rows_in_2027 from library.sale_2027;

-- итоговое количество строк в секционированной таблице
select count(*) as total_sales from library.sale_by_year;