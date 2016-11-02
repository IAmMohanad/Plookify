CREATE TABLE if not exists artist(
  idartist INTEGER PRIMARY KEY,
  artistName VARCHAR(45) NULL,
  artistGenre VARCHAR(45) NULL
);

CREATE TABLE if not exists track(
  idtrack INTEGER PRIMARY KEY,
  artistID INT NULL,
  trackGenre VARCHAR(45) NULL,
  trackName VARCHAR(45) NULL,
  trackLength VARCHAR(45) NULL,
  trackPath VARCHAR(45) NULL,
  CONSTRAINT idartist
    FOREIGN KEY (artistID)
    REFERENCES artist(idartist)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);

CREATE TABLE if not exists user(
  iduser INT PRIMARY KEY,
  datejoined DATETIME NOT NULL,
  email VARCHAR(45) NOT NULL,
  firstName VARCHAR(45) NOT NULL,
  lastName VARCHAR(45) NOT NULL,
  phone VARCHAR(45) NOT NULL,
  adress VARCHAR(45) NOT NULL
);


CREATE TABLE if not exists playlist(
  idplaylist INT PRIMARY KEY,
  iduser INT NULL,
  playlistVisibility TINYINT(1) NULL,
  playlistName VARCHAR(45) NULL,
  CONSTRAINT iduser
    FOREIGN KEY (iduser)
    REFERENCES user(iduser)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);


CREATE TABLE if not exists track_playlist(
  idtrack INT NOT NULL,
  idplaylist INT NOT NULL,
  CONSTRAINT tp_fk_track
    FOREIGN KEY (idtrack)
    REFERENCES track(idtrack)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT tp_fk_playlist
    FOREIGN KEY (idplaylist)
    REFERENCES playlist(idplaylist)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE if not exists device(
  iddevice INT NOT NULL,
  deviceType VARCHAR(45) NOT NULL,
  idDevice_User INT NOT NULL,
  dateAdded DATETIME NOT NULL,
  PRIMARY KEY (iddevice),
  CONSTRAINT iduser
    FOREIGN KEY (idDevice_User)
    REFERENCES user(iduser)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);


CREATE TABLE if not exists userFriend(
  iduser1 INT NOT NULL,
  iduser2 INT NOT NULL,
  status INT NULL,
  PRIMARY KEY (iduser1),
  CONSTRAINT iduser
    FOREIGN KEY (iduser1)
    REFERENCES user(iduser)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT iduser
    FOREIGN KEY (iduser1 , iduser2)
    REFERENCES user(iduser , iduser)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);

CREATE TABLE if not exists userLogin(
	iduserLogin INT,
	username VARCHAR(45) NOT NULL,
	password VARCHAR(45) NOT NULL,
	PRIMARY KEY (iduserLogin),
	CONSTRAINT iduser
    FOREIGN KEY (iduserLogin)
    REFERENCES user(iduser)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);

 CREATE TABLE if not exists subscription(
  iduserSub INT NOT NULL,
  subscribed TINYINT(1) NOT NULL,
  paymentType TINYINT(1) NOT NULL,
  lastPaid DATETIME NOT NULL,
  discoverable TINYINT(1) NULL,
  PRIMARY KEY (iduserSub),
  CONSTRAINT iduser
    FOREIGN KEY (iduserSub)
    REFERENCES user(iduser)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);
