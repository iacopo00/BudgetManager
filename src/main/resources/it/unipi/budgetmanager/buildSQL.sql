DROP SCHEMA IF EXISTS `603217`;
CREATE DATABASE `603217`;
USE `603217`;

DROP TABLE IF EXISTS `gara`;
CREATE TABLE `gara` (
  `n_gara` int NOT NULL,
  `data` date NOT NULL,
  `campionato` varchar(45) NOT NULL,
  `luogo` varchar(45) NOT NULL,
  `rimborso` float NOT NULL,
  `username` varchar(45) NOT NULL,
  PRIMARY KEY (`n_gara`,`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `spesa`;
CREATE TABLE `spesa` (
  `id` int NOT NULL AUTO_INCREMENT,
  `causale` varchar(45) NOT NULL,
  `data` date NOT NULL,
  `costo` float NOT NULL,
  `username` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
