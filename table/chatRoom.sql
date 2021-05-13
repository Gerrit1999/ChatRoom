/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2021/5/12 14:12:27                           */
/*==============================================================*/


drop table if exists inner_file_image;

drop table if exists inner_message_file;

drop table if exists inner_message_receiver;

drop table if exists inner_message_room;

drop table if exists inner_message_sender;

drop table if exists inner_room_user;

drop table if exists t_file;

drop table if exists t_image;

drop table if exists t_message;

drop table if exists t_room;

drop table if exists t_user;

/*==============================================================*/
/* Table: inner_file_image                                      */
/*==============================================================*/
create table inner_file_image
(
   id                   int not null auto_increment,
   file_id              int not null,
   image_id             int not null,
   primary key (id)
)
charset = UTF8;

/*==============================================================*/
/* Table: inner_message_file                                    */
/*==============================================================*/
create table inner_message_file
(
   id                   int not null auto_increment,
   message_id           int not null,
   file_id              int not null,
   primary key (id)
)
charset = UTF8;

/*==============================================================*/
/* Table: inner_message_receiver                                */
/*==============================================================*/
create table inner_message_receiver
(
   id                   int not null auto_increment,
   message_id           int not null,
   user_id              int not null,
   primary key (id)
)
charset = UTF8;

/*==============================================================*/
/* Table: inner_message_room                                    */
/*==============================================================*/
create table inner_message_room
(
   id                   int not null auto_increment,
   message_id           int not null,
   room_id              int not null,
   primary key (id)
)
charset = UTF8;

/*==============================================================*/
/* Table: inner_message_sender                                  */
/*==============================================================*/
create table inner_message_sender
(
   id                   int not null auto_increment,
   message_id           int not null,
   user_id              int not null,
   primary key (id)
)
charset = UTF8;

/*==============================================================*/
/* Table: inner_room_user                                       */
/*==============================================================*/
create table inner_room_user
(
   id                   int not null auto_increment,
   room_id              int not null,
   user_id              int not null,
   date                 timestamp default CURRENT_TIMESTAMP,
   unread               int not null,
   primary key (id)
)
charset = UTF8;

/*==============================================================*/
/* Table: t_file                                                */
/*==============================================================*/
create table t_file
(
   id                   int not null auto_increment,
   name                 varchar(128) not null,
   path                 varchar(256) not null,
   size                 bigint not null,
   primary key (id)
)
charset = utf8mb4;

/*==============================================================*/
/* Table: t_image                                               */
/*==============================================================*/
create table t_image
(
   id                   int not null auto_increment,
   url                  varchar(256) not null,
   width                int not null,
   height               int not null,
   proportion           double not null,
   primary key (id)
)
charset = utf8mb4;

/*==============================================================*/
/* Table: t_message                                             */
/*==============================================================*/
create table t_message
(
   id                   int not null auto_increment,
   date                 timestamp default CURRENT_TIMESTAMP,
   message              varchar(256),
   font_size            int,
   font_weight          int,
   font_style           char(16),
   primary key (id)
)
charset = utf8mb4;

/*==============================================================*/
/* Table: t_room                                                */
/*==============================================================*/
create table t_room
(
   id                   int not null auto_increment,
   name                 varchar(32) not null,
   password             char(100) not null,
   host_id              int not null,
   primary key (id)
)
charset = utf8mb4;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user
(
   id                   int not null auto_increment,
   username             varchar(32) not null,
   password             char(100) not null,
   email                char(32) not null,
   primary key (id),
   unique key AK_username (username)
)
charset = utf8mb4;

