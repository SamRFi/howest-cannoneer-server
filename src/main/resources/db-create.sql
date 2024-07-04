create table quotes
(
    id    int auto_increment,
    quote varchar(255)
);

create table players
(
  id int auto_increment,
  name varchar(255)
);

create table games
(
    playerId int,
    location varchar(255),
    starttime varchar(255),
    gamemode varchar(255),
    durationinminutes int,
    shotsamount int,
    targetshit int,
    score int
)