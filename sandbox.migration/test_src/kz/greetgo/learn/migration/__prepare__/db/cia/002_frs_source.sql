create table transition_transactions (
  number bigserial primary key,
  inserted_at timestamp not null default current_timestamp,
  status varchar(30) not null default 'JUST_INSERTED',
  error varchar(300),
  record_data text not null
);

create table transition_accounts (
  number bigserial primary key,
  inserted_at timestamp not null default current_timestamp,
  status varchar(30) not null default 'JUST_INSERTED',
  error varchar(300),
  record_data text not null
);
