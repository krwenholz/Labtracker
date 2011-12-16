# --------------------------------------------------------
# Host:                         localhost
# Server version:               5.5.16
# Server OS:                    Win32
# HeidiSQL version:             6.0.0.3603
# Date/time:                    2011-12-16 14:08:29
# --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

# Dumping database structure for labtracker
CREATE DATABASE IF NOT EXISTS `labtracker` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `labtracker`;


# Dumping structure for table labtracker.activity_logs
CREATE TABLE IF NOT EXISTS `activity_logs` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `MethodName` varchar(50) NOT NULL,
  `UserID` varchar(50) NOT NULL DEFAULT 'GUEST',
  `SessionID` varchar(50) NOT NULL DEFAULT 'Lab 9',
  `TimeStamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

# Data exporting was unselected.


# Dumping structure for table labtracker.compile_errors
CREATE TABLE IF NOT EXISTS `compile_errors` (
  `ID` int(10) NOT NULL AUTO_INCREMENT,
  `MethodName` varchar(255) NOT NULL,
  `ErrorType` varchar(255) NOT NULL,
  `TimeStamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UserID` varchar(50) NOT NULL DEFAULT 'GUEST',
  `SessionID` varchar(50) NOT NULL DEFAULT 'Lab 9',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

# Data exporting was unselected.
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
