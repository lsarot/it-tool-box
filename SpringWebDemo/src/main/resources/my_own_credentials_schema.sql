--PODEMOS USAR LOS FICHEROS QUE UTILIZA SPRINGBOOT SECURITY POR DEFECTO
--PARA EL BOOTSTRAPING DEL SCHEMA Y DATOS
--schema.sql y data.sql


create table if not exists accounts (
  name varchar(50) not NULL,
  email varchar(50) not NULL,
  password varchar(100) not NULL,
  enabled numeric not NULL default 1,
  constraint pk_acc primary key (email)
);
   
create table if not exists authorities (
  email varchar(50) not NULL,
  authority varchar(50) not NULL,
  --constraint pk_auth primary key (email),
  constraint fk_email foreign key (email) references accounts (email)
);
 
--CREATE UNIQUE IF NOT EXISTS INDEX ix_auth_email on authorities (email,authority);
  
commit;
  
-- User user/pass
--INSERT INTO accounts (name, email, password, enabled) values ('nombre','user','$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a',1);
--INSERT INTO authorities (email, authority) values ('user', 'ROLE_USER');
--commit;

