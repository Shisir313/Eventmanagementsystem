-- SQL script to create the College Event Management System database and tables
-- This script drops any existing `college_events` database and recreates it to avoid schema conflicts.
-- Database name: college_events
-- Run this script in your MySQL server (adjust user/host/charset as needed)

DROP DATABASE IF EXISTS `college_events`;
CREATE DATABASE IF NOT EXISTS `college_events` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `college_events`;

-- Student table (matches Java code: student, student_id, name, email, password, department)
CREATE TABLE IF NOT EXISTS `student` (
  `student_id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL,
  `email` VARCHAR(150),
  `password` VARCHAR(255),
  `department` VARCHAR(100) DEFAULT 'General'
) ENGINE=InnoDB;

-- Organizer table (organizer, organizer_id, name, email, password, contact)
CREATE TABLE IF NOT EXISTS `organizer` (
  `organizer_id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL,
  `email` VARCHAR(150),
  `password` VARCHAR(255),
  `contact` VARCHAR(100)
) ENGINE=InnoDB;

-- Admin table (admin, admin_id, name, email, password)
CREATE TABLE IF NOT EXISTS `admin` (
  `admin_id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL,
  `email` VARCHAR(150),
  `password` VARCHAR(255)
) ENGINE=InnoDB;

-- Event table (event, event_id, event_name, event_date, location, description, organizer_id, status)
CREATE TABLE IF NOT EXISTS `event` (
  `event_id` INT AUTO_INCREMENT PRIMARY KEY,
  `event_name` VARCHAR(200) NOT NULL,
  `event_date` DATE NOT NULL,
  `location` VARCHAR(255),
  `description` TEXT,
  `organizer_id` INT NOT NULL,
  `status` VARCHAR(50) DEFAULT 'Pending',
  FOREIGN KEY (`organizer_id`) REFERENCES `organizer`(`organizer_id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Registration table (registration, registration_id, student_id, event_id, registration_date, status)
CREATE TABLE IF NOT EXISTS `registration` (
  `registration_id` INT AUTO_INCREMENT PRIMARY KEY,
  `student_id` INT NOT NULL,
  `event_id` INT NOT NULL,
  `registration_date` DATE NOT NULL,
  `status` VARCHAR(50) DEFAULT 'Pending',
  FOREIGN KEY (`student_id`) REFERENCES `student`(`student_id`) ON DELETE CASCADE,
  FOREIGN KEY (`event_id`) REFERENCES `event`(`event_id`) ON DELETE CASCADE,
  UNIQUE KEY `ux_student_event` (`student_id`, `event_id`)
) ENGINE=InnoDB;

-- Approval table (approval, approval_id, registration_id, admin_id, status, created_at)
CREATE TABLE IF NOT EXISTS `approval` (
  `approval_id` INT AUTO_INCREMENT PRIMARY KEY,
  `registration_id` INT NOT NULL,
  `admin_id` INT NOT NULL,
  `status` ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`registration_id`) REFERENCES `registration`(`registration_id`) ON DELETE CASCADE,
  FOREIGN KEY (`admin_id`) REFERENCES `admin`(`admin_id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Sample data (optional) -- change as needed
INSERT INTO `student` (`name`, `email`, `password`) VALUES ('Alice Smith', 'alice@example.com', 'password123');
INSERT INTO `organizer` (`name`, `email`, `password`, `contact`) VALUES ('Student Council', 'sc@example.com', 'pass', 'sc@example.com');
INSERT INTO `admin` (`name`, `email`, `password`) VALUES ('Admin User', 'admin@example.com', 'adminpass');

-- Create a sample event (organizer_id = 1)
INSERT INTO `event` (`event_name`, `event_date`, `organizer_id`, `location`, `description`) VALUES ('Welcome Fair', CURDATE() + INTERVAL 7 DAY, 1, 'Main Hall', 'An event to welcome new students');

-- Create a sample registration (student_id = 1, event_id = 1)
INSERT INTO `registration` (`student_id`, `event_id`, `registration_date`) VALUES (1, 1, CURDATE());

-- Useful indexes to speed lookups (optional)
ALTER TABLE `event` ADD INDEX (`event_date`);
ALTER TABLE `registration` ADD INDEX (`student_id`);
ALTER TABLE `registration` ADD INDEX (`event_id`);

-- End of script