-- SQL script to create the College Event Management System database and tables
-- Database name: college_events
-- Run this script in your MySQL server (adjust user/host/charset as needed)

CREATE DATABASE IF NOT EXISTS `college_events` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `college_events`;

-- Student table
CREATE TABLE IF NOT EXISTS `Student` (
  `StudentID` INT AUTO_INCREMENT PRIMARY KEY,
  `StudentName` VARCHAR(100) NOT NULL,
  `Email` VARCHAR(150)
) ENGINE=InnoDB;

-- Organizer table
CREATE TABLE IF NOT EXISTS `Organizer` (
  `OrganizerID` INT AUTO_INCREMENT PRIMARY KEY,
  `OrganizerName` VARCHAR(100) NOT NULL,
  `Contact` VARCHAR(100)
) ENGINE=InnoDB;

-- Admin table
CREATE TABLE IF NOT EXISTS `Admin` (
  `AdminID` INT AUTO_INCREMENT PRIMARY KEY,
  `AdminName` VARCHAR(100) NOT NULL,
  `Email` VARCHAR(150)
) ENGINE=InnoDB;

-- Event table
CREATE TABLE IF NOT EXISTS `Event` (
  `EventID` INT AUTO_INCREMENT PRIMARY KEY,
  `EventName` VARCHAR(200) NOT NULL,
  `EventDate` DATE NOT NULL,
  `OrganizerID` INT NOT NULL,
  FOREIGN KEY (`OrganizerID`) REFERENCES `Organizer`(`OrganizerID`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Registration table
CREATE TABLE IF NOT EXISTS `Registration` (
  `RegistrationID` INT AUTO_INCREMENT PRIMARY KEY,
  `StudentID` INT NOT NULL,
  `EventID` INT NOT NULL,
  `RegistrationDate` DATE NOT NULL,
  FOREIGN KEY (`StudentID`) REFERENCES `Student`(`StudentID`) ON DELETE CASCADE,
  FOREIGN KEY (`EventID`) REFERENCES `Event`(`EventID`) ON DELETE CASCADE,
  UNIQUE KEY `ux_student_event` (`StudentID`, `EventID`)
) ENGINE=InnoDB;

-- Approval table
CREATE TABLE IF NOT EXISTS `Approval` (
  `ApprovalID` INT AUTO_INCREMENT PRIMARY KEY,
  `RegistrationID` INT NOT NULL,
  `AdminID` INT NOT NULL,
  `Status` ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
  `CreatedAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`RegistrationID`) REFERENCES `Registration`(`RegistrationID`) ON DELETE CASCADE,
  FOREIGN KEY (`AdminID`) REFERENCES `Admin`(`AdminID`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Sample data (optional) -- change as needed
INSERT INTO `Student` (`StudentName`, `Email`) VALUES ('Alice Smith', 'alice@example.com');
INSERT INTO `Organizer` (`OrganizerName`, `Contact`) VALUES ('Student Council', 'sc@example.com');
INSERT INTO `Admin` (`AdminName`, `Email`) VALUES ('Admin User', 'admin@example.com');

-- Create a sample event (OrganizerID = 1)
INSERT INTO `Event` (`EventName`, `EventDate`, `OrganizerID`) VALUES ('Welcome Fair', CURDATE() + INTERVAL 7 DAY, 1);

-- Create a sample registration (StudentID = 1, EventID = 1)
INSERT INTO `Registration` (`StudentID`, `EventID`, `RegistrationDate`) VALUES (1, 1, CURDATE());

-- At this point the registration is pending because there is no Approval row for it.

-- Useful indexes to speed lookups (optional)
ALTER TABLE `Event` ADD INDEX (`EventDate`);
ALTER TABLE `Registration` ADD INDEX (`StudentID`);
ALTER TABLE `Registration` ADD INDEX (`EventID`);

-- End of script
