-- phpMyAdmin SQL Dump
-- version 4.1.14
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1:3306
-- Generation Time: 07 Jul 2015 pada 18.33
-- Versi Server: 5.6.17-log
-- PHP Version: 5.5.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `querybuilder`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_activity`
--

CREATE TABLE IF NOT EXISTS `tb_activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `query` text NOT NULL,
  `user_created_id` int(11) NOT NULL,
  `created_at` datetime NOT NULL,
  `file_id` bigint(20) DEFAULT NULL,
  `done_at` datetime DEFAULT NULL,
  `notes` text,
  `query_name` varchar(200) NOT NULL,
  `filetype` varchar(50) NOT NULL,
  `driver` varchar(200) NOT NULL,
  `connection_string` text NOT NULL,
  `start_at` datetime DEFAULT NULL,
  `memory_used` bigint(20) DEFAULT NULL,
  `memory_max` bigint(20) DEFAULT NULL,
  `show_memory_used` varchar(150) DEFAULT NULL,
  `show_memory_max` varchar(150) DEFAULT NULL,
  `show_duration` varchar(100) DEFAULT NULL,
  `duration_time` time DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=14 ;

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_file`
--

CREATE TABLE IF NOT EXISTS `tb_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `filename` text NOT NULL,
  `filetype` varchar(50) NOT NULL,
  `isdeleted` tinyint(1) NOT NULL DEFAULT '1',
  `filesize` double NOT NULL,
  `filesize_show` varchar(200) DEFAULT NULL,
  `download_link` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=135 ;

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_filesize_used`
--

CREATE TABLE IF NOT EXISTS `tb_filesize_used` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `filesize` double NOT NULL DEFAULT '0',
  `filesize_show` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=9 ;

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_filetotal`
--

CREATE TABLE IF NOT EXISTS `tb_filetotal` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `filesize` double NOT NULL,
  `filesize_show` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_query`
--

CREATE TABLE IF NOT EXISTS `tb_query` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `driver` varchar(200) NOT NULL,
  `sql_query` text NOT NULL,
  `created_by` int(11) NOT NULL,
  `modified_by` int(11) NOT NULL,
  `created_at` datetime NOT NULL,
  `modified_at` datetime NOT NULL,
  `named` varchar(200) NOT NULL,
  `connection_string` text NOT NULL,
  `isdeleted` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `named` (`named`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=25 ;

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_users`
--

CREATE TABLE IF NOT EXISTS `tb_users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(200) NOT NULL,
  `pass` varchar(200) NOT NULL,
  `last_login` datetime DEFAULT NULL,
  `isdeleted` tinyint(1) DEFAULT '1',
  `divisi` varchar(50) NOT NULL,
  `theme` varchar(50) DEFAULT NULL,
  `email` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=39 ;

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_users_query`
--

CREATE TABLE IF NOT EXISTS `tb_users_query` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `query_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=79 ;

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_user_activity`
--

CREATE TABLE IF NOT EXISTS `tb_user_activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL,
  `user_id` int(11) NOT NULL,
  `notes` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=160 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
