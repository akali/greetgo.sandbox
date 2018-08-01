create table charm (
  id bigint not null primary key,
  name varchar(300),
  description varchar(300),
  energy numeric
);;
create table client (
  id bigint not null primary key,

  surname varchar(300),
  name varchar(300),
  patronymic varchar(300),
  birth_date date,

  actual smallint not null default 0,
  cia_id varchar(100),
  charm bigint references charm on delete cascade
);;
create table ClientAddress (
    client bigint references Client on delete cascade,
    type varchar(64) not null,

    street varchar(64),
    house varchar(64),
    flat varchar(64),
    primary key(client, type)
);;
create table ClientPhone (
    client bigint references Client on delete cascade,
    number varchar(64) not null,
    type varchar(64) not null,
    primary key(client, number)
);;

create table TransactionType (
  id bigserial primary key,
  name varchar(300),
  description varchar(300),
  energy numeric
);;

create table ClientAccount (
  id bigserial primary key,
  client bigint references Client on delete cascade,
  money numeric default 0,
  number varchar(300),
  registered_at timestamp
);;

create table ClientAccountTransaction (
  id bigserial primary key,
  account bigint references ClientAccount on delete cascade,
  money numeric,
  finished_at timestamp,
  type bigint references TransactionType on delete cascade
);;

create sequence s_client start with 1000000;;
create sequence s_charm start with 1000000;;
create sequence s_address start with 1000000;;
create sequence s_phone start with 1000000;;
create sequence s_Transaction_Type start with 1000000;;
create sequence s_client_Account_Transaction start with 1000000;;
create sequence s_client_Account start with 1000000;
