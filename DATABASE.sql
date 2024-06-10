create table answer_message(
    id serial primary key,
    to_answer_message_id int
)
create table block_message(
    id serial primary key,
    chat_id int,
    text varchar(300)
)
create table bot(
    id serial primary key,
    bot_token varchar(100),
    username varchar(20)
)
create table bot_chat(
    id serial primary key
)
create table channel(
    id serial primary key,
    channel_name varchar(100)
)
create table chat(
    id serial primary key
)
create table chat_member(
    id serial primary key,
    chat_id int,
    member_type varchar(50),
    user_id int
)
create table complaint(
    id serial primary key,
    owner_id int,
    text varchar(300)
)
create table forward_message(
    id serial primary key,
    forward_message_type varchar(50),
    from_chat_id int,
    from_owner_id int,
    text_under_message
)
create table group_chat(
    id serial primary key,
    group_name varchar(300)
)
create table link_message(
    id serial primary key,
    link varchar(300)
)
create table message(
    id serial primary key
)
create table message_wrapper(
    id serial primary key,
    chat_id int,
    content varchar(500),
    has_been_read varchar(20),
    owner_id int,
    sending_time timestamp,
    type varchar(30)
)
create table messenger_user(
    id serial primary key,
    username varchar(100)
)
create table person(
    id serial primary key,
    email varchar(200),
    firstname varchar(100),
    lang varchar(20),
    last_online timestamp,
    lastname varchar(100),
    password varchar(200),
    phone varchar(75)
    role varchar(30)
)
create table photo_message(
    id serial primary key,
    expansion varchar(50),
    text_under_photo(300)
)
create table private_chat(
    id serial primary key,
)
create table user_chat(
    chat_id int,
    user_id int,
    primary key(chat_id, user_id)
)