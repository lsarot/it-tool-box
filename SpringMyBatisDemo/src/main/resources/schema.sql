drop table if exists address;
drop table if exists contact;
--para evitar que falle al desplegar otra vez. Pq intenta crear esas tablas y ya existen.
--drop table if exists authorities;commit;
--drop table if exists users;commit;


create table address(
	id numeric not null,
	street varchar(30),
  	contact_id numeric,
	constraint pk_address primary key (id)
);

create table contact(
	id numeric not null,
	name varchar(20),
	email varchar(30),
	phone varchar(15),
	constraint pk_contact primary key (id)
);

commit;