CREATE TABLE AlbumDB (
	albumID text primary key,
	title text not null,
	releaseDate date
);

CREATE TABLE artistDB (
    artistID text primary key,
    name text,
    surname text,
    nickname text
);

CREATE TABLE SongDB (
    idSong text primary key,
    name text not null, 
    duration int,
    albumID text REFERENCES AlbumDB (albumID)
);

CREATE TABLE BandDB (
    bandID text primary key,
    name text not null,
    genre text not null
);

CREATE TABLE BandMemberDB (
    artistID text REFERENCES artistDB (artistID),
    bandID text REFERENCES BandDB (bandID)
);

CREATE TABLE Users (
    name text primary key,
    password text not null
);

INSERT INTO Users VALUES('admin', '8C6976E5B5410415BDE908BD4DEE15DFB167A9C873FC4BB8A81F6F2AB448A918');

GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO rock_db_owner;
