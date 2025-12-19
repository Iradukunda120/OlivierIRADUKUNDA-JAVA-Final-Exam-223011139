-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Dec 16, 2025 at 01:28 PM
-- Server version: 8.3.0
-- PHP Version: 8.2.18

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `govtechsolutions`
--

-- --------------------------------------------------------

--
-- Table structure for table `casetable`
--

DROP TABLE IF EXISTS `casetable`;
CREATE TABLE IF NOT EXISTS `casetable` (
  `CaseID` bigint NOT NULL AUTO_INCREMENT,
  `CaseType` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Description` text COLLATE utf8mb4_unicode_ci,
  `Priority` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CreatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`CaseID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `casetable`
--

INSERT INTO `casetable` (`CaseID`, `CaseType`, `Description`, `Priority`, `CreatedAt`) VALUES
(1, 'Health Claim', 'Citizen submitted health insurance claim.', 'High', '2025-10-15 15:49:49'),
(2, 'School Certificate', 'Request for student certificate verification.', 'Medium', '2025-10-15 15:49:49'),
(3, 'Driver License Renewal', 'Renewal of expired driver license.', 'High', '2025-10-15 15:49:49'),
(4, 'Farming Subsidy', 'Request for maize and beans subsidy.', 'Medium', '2025-10-15 15:49:49'),
(5, 'Tax Payment', 'Assistance in annual tax filing.', 'High', '2025-10-15 15:49:49'),
(6, 'Trade License', 'Application for new trade license.', 'Medium', '2025-10-15 15:49:49');

-- --------------------------------------------------------

--
-- Table structure for table `citizen`
--

DROP TABLE IF EXISTS `citizen`;
CREATE TABLE IF NOT EXISTS `citizen` (
  `CitizenID` bigint NOT NULL AUTO_INCREMENT,
  `FullName` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `NationalID` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Contact` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CreatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`CitizenID`),
  UNIQUE KEY `NationalID` (`NationalID`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `citizen`
--

INSERT INTO `citizen` (`CitizenID`, `FullName`, `NationalID`, `Address`, `Contact`, `CreatedAt`) VALUES
(1, 'Olivier Iradukunda', '1198765432109876', 'Kigali, Rwanda', '+250788000000', '2025-10-15 14:59:46'),
(2, 'Die donne Iradukunda', '120058001234', 'Karongi, Rwanda', '+250788000123', '2025-10-15 15:08:47'),
(3, 'Ericksen Niyigena', '120058001111', 'Huye, Rwanda', '+250789349949', '2025-10-15 15:12:01'),
(4, 'Jean Bosco Habimana', '120058002345', 'Ruhango, Rwanda', '+250788334455', '2025-10-15 15:28:30'),
(5, 'Alice Mukamana', '120058003456', 'Musanze, Rwanda', '+250789445566', '2025-10-15 15:28:30'),
(6, 'Patrick Uwitonze', '120058004567', 'Gisenyi, Rwanda', '+250788556677', '2025-10-15 15:28:30'),
(7, 'DID MAN', '120000002020', 'KIMIHURURA', '07828282', '2025-11-29 15:07:56'),
(8, 'dfg', '1234', 'dfghjkl', '34567', '2025-12-16 13:26:20');

-- --------------------------------------------------------

--
-- Table structure for table `department`
--

DROP TABLE IF EXISTS `department`;
CREATE TABLE IF NOT EXISTS `department` (
  `DepartmentID` bigint NOT NULL AUTO_INCREMENT,
  `Name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Capacity` int DEFAULT NULL,
  `Manager` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Contact` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`DepartmentID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `department`
--

INSERT INTO `department` (`DepartmentID`, `Name`, `Address`, `Capacity`, `Manager`, `Contact`) VALUES
(1, 'Health Department', 'Kigali, Rwanda', 50, 'Dr. Jean Mutware', '+250788111222'),
(2, 'Education Department', 'Musanze, Rwanda', 40, 'Mrs. Alice Uwase', '+250788333444'),
(3, 'Transport Department', 'Huye, Rwanda', 35, 'Mr. Eric Niyonzima', '+250788555666'),
(4, 'Agriculture Department', 'Karongi, Rwanda', 30, 'Mr. Patrick Uwitonze', '+250788777888'),
(5, 'Finance Department', 'Ruhango, Rwanda', 25, 'Mrs. Grace Mukamana', '+250788999000'),
(6, 'Trade Department', 'Gisenyi, Rwanda', 20, 'Mr. Jean Bosco Habimana', '+250788112233');

-- --------------------------------------------------------

--
-- Table structure for table `document`
--

DROP TABLE IF EXISTS `document`;
CREATE TABLE IF NOT EXISTS `document` (
  `DocumentID` bigint NOT NULL AUTO_INCREMENT,
  `CaseID` bigint DEFAULT NULL,
  `CitizenID` bigint DEFAULT NULL,
  `FileName` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `FilePath` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `FileType` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CreatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`DocumentID`),
  KEY `idx_doc_case` (`CaseID`),
  KEY `idx_doc_citizen` (`CitizenID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `document`
--

INSERT INTO `document` (`DocumentID`, `CaseID`, `CitizenID`, `FileName`, `FilePath`, `FileType`, `CreatedAt`) VALUES
(1, 1, 1, 'HealthClaim.pdf', '/files/healthclaim1.pdf', 'PDF', '2025-10-15 15:51:43'),
(2, 2, 2, 'SchoolCert.pdf', '/files/schoolcert2.pdf', 'PDF', '2025-10-15 15:51:43'),
(3, 3, 3, 'DriverLicense.jpg', '/files/driverlicense3.jpg', 'JPG', '2025-10-15 15:51:43'),
(4, 4, 4, 'FarmSubsidy.docx', '/files/farmsubsidy4.docx', 'DOCX', '2025-10-15 15:51:43'),
(5, 5, 5, 'TaxForm.pdf', '/files/taxform5.pdf', 'PDF', '2025-10-15 15:51:43'),
(6, 6, 6, 'TradeLicense.pdf', '/files/tradelicense6.pdf', 'PDF', '2025-10-15 15:51:43');

-- --------------------------------------------------------

--
-- Table structure for table `download`
--

DROP TABLE IF EXISTS `download`;
CREATE TABLE IF NOT EXISTS `download` (
  `DownloadID` int NOT NULL AUTO_INCREMENT,
  `DocumentID` int NOT NULL,
  `CitizenID` int NOT NULL,
  `FileName` varchar(255) NOT NULL,
  `FilePath` varchar(500) NOT NULL,
  `FileType` varchar(10) NOT NULL,
  `Status` enum('Pending','Approved','Rejected') DEFAULT 'Pending',
  `DownloadedAt` timestamp NULL DEFAULT NULL,
  `CreatedAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`DownloadID`),
  KEY `CitizenID` (`CitizenID`),
  KEY `idx_dl_doc` (`DocumentID`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `download`
--

INSERT INTO `download` (`DownloadID`, `DocumentID`, `CitizenID`, `FileName`, `FilePath`, `FileType`, `Status`, `DownloadedAt`, `CreatedAt`) VALUES
(1, 1, 1, 'HealthClaim.pdf', '/files/healthclaim1.pdf', 'PDF', 'Approved', NULL, '2025-10-24 14:14:03'),
(2, 2, 2, 'SchoolCert.pdf', '/files/schoolcert2.pdf', 'PDF', 'Approved', NULL, '2025-10-24 14:14:03'),
(3, 3, 3, 'DriverLicense.jpg', '/files/driverlicense3.jpg', 'JPG', 'Approved', NULL, '2025-10-24 14:14:03'),
(4, 4, 4, 'FarmSubsidy.docx', '/files/farmsubsidy4.docx', 'DOCX', 'Approved', NULL, '2025-10-24 14:14:03'),
(5, 5, 5, 'TaxForm.pdf', '/files/taxform5.pdf', 'PDF', 'Approved', NULL, '2025-10-24 14:14:03'),
(6, 6, 6, 'TradeLicense.pdf', '/files/tradelicense6.pdf', 'PDF', 'Approved', NULL, '2025-10-24 14:14:03');

-- --------------------------------------------------------

--
-- Table structure for table `officer`
--

DROP TABLE IF EXISTS `officer`;
CREATE TABLE IF NOT EXISTS `officer` (
  `OfficerID` bigint NOT NULL AUTO_INCREMENT,
  `DepartmentID` bigint NOT NULL,
  `Name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Identifier` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'Active',
  `Location` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Contact` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `AssignedSince` date DEFAULT NULL,
  `CreatedAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`OfficerID`),
  UNIQUE KEY `Identifier` (`Identifier`),
  KEY `fk_officer_department` (`DepartmentID`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `officer`
--

INSERT INTO `officer` (`OfficerID`, `DepartmentID`, `Name`, `Identifier`, `Status`, `Location`, `Contact`, `AssignedSince`, `CreatedAt`) VALUES
(1, 1, 'Dr. Jean Mutware', 'OFF001', 'Active', 'Kigali', '+250788111222', '2023-01-15', '2025-12-01 15:24:58'),
(2, 2, 'Mrs. Alice Uwase', 'OFF002', 'Active', 'Musanze', '+250788333444', '2023-02-10', '2025-12-01 15:24:58'),
(3, 3, 'Mr. Eric Niyonzima', 'OFF003', 'Active', 'Huye', '+250788555666', '2023-03-05', '2025-12-01 15:24:58'),
(4, 4, 'Mr. Patrick Uwitonze', 'OFF004', 'Active', 'Karongi', '+250788777888', '2023-04-12', '2025-12-01 15:24:58'),
(5, 5, 'Mrs. Grace Mukamana', 'OFF005', 'Active', 'Ruhango', '+250788999000', '2023-05-20', '2025-12-01 15:24:58'),
(6, 6, 'Mr. Jean Bosco Habimana', 'OFF006', 'Active', 'Gisenyi', '+250788112233', '2023-06-18', '2025-12-01 15:24:58'),
(7, 1, 'rtyu', 'werty', 'Active', 'dfghj', '2345678', '2025-12-16', '2025-12-16 13:26:35');

-- --------------------------------------------------------

--
-- Table structure for table `officercase`
--

DROP TABLE IF EXISTS `officercase`;
CREATE TABLE IF NOT EXISTS `officercase` (
  `OfficerID` bigint NOT NULL,
  `CaseID` bigint NOT NULL,
  `AssignedDate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`OfficerID`,`CaseID`),
  KEY `fk_officercase_case` (`CaseID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `officercase`
--

INSERT INTO `officercase` (`OfficerID`, `CaseID`, `AssignedDate`) VALUES
(1, 1, '2025-10-15 15:50:41'),
(2, 2, '2025-10-15 15:50:41'),
(3, 3, '2025-10-15 15:50:41'),
(4, 4, '2025-10-15 15:50:41'),
(5, 5, '2025-10-15 15:50:41'),
(6, 6, '2025-10-15 15:50:41');

-- --------------------------------------------------------

--
-- Table structure for table `servicerequest`
--

DROP TABLE IF EXISTS `servicerequest`;
CREATE TABLE IF NOT EXISTS `servicerequest` (
  `ServiceRequestID` bigint NOT NULL AUTO_INCREMENT,
  `CitizenID` bigint NOT NULL,
  `DepartmentID` bigint NOT NULL,
  `RequestType` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Description` text COLLATE utf8mb4_unicode_ci,
  `Status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'Pending',
  `CreatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ServiceRequestID`),
  KEY `fk_service_department` (`DepartmentID`),
  KEY `idx_sr_citizen` (`CitizenID`),
  KEY `idx_sr_type` (`RequestType`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `servicerequest`
--

INSERT INTO `servicerequest` (`ServiceRequestID`, `CitizenID`, `DepartmentID`, `RequestType`, `Description`, `Status`, `CreatedAt`) VALUES
(1, 1, 1, 'Health Insurance Registration', 'Request to enroll in national health insurance.', 'Pending', '2025-10-15 15:37:44'),
(2, 2, 2, 'School Admission Certificate', 'Request for school admission certificate for child.', 'Pending', '2025-10-15 15:37:44'),
(3, 3, 3, 'Driver License Renewal', 'Renew driver license that expired last month.', 'Rejected', '2025-10-15 15:37:44'),
(4, 4, 4, 'Farming Subsidy Request', 'Request subsidy for maize and beans crops.', 'Approved', '2025-10-15 15:37:44'),
(5, 5, 5, 'Tax Payment Assistance', 'Assistance to pay annual business taxes.', 'Approved', '2025-10-15 15:37:44'),
(6, 6, 6, 'Trade License Application', 'Apply for a new trade license for small business.', 'Approved', '2025-10-15 15:37:44');

-- --------------------------------------------------------

--
-- Table structure for table `system_logs`
--

DROP TABLE IF EXISTS `system_logs`;
CREATE TABLE IF NOT EXISTS `system_logs` (
  `LogID` bigint NOT NULL AUTO_INCREMENT,
  `Timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `Level` varchar(20) DEFAULT 'INFO',
  `User` varchar(100) DEFAULT NULL,
  `Action` varchar(255) DEFAULT NULL,
  `IPAddress` varchar(45) DEFAULT NULL,
  `Details` text,
  PRIMARY KEY (`LogID`)
) ENGINE=MyISAM AUTO_INCREMENT=103 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `system_logs`
--

INSERT INTO `system_logs` (`LogID`, `Timestamp`, `Level`, `User`, `Action`, `IPAddress`, `Details`) VALUES
(1, '2025-11-29 12:14:28', 'INFO', 'admin_olivier', 'Login', NULL, 'User logged into system'),
(2, '2025-11-29 12:14:28', 'INFO', 'admin_olivier', 'View Dashboard', NULL, 'Accessed dashboard overview'),
(3, '2025-11-29 12:14:28', 'INFO', 'admin_olivier', 'View Users', NULL, 'Viewed user management panel'),
(4, '2025-11-29 12:14:28', 'INFO', 'system', 'System Start', NULL, 'Admin dashboard initialized'),
(5, '2025-11-29 12:14:28', 'INFO', 'admin_olivier', 'Theme Change', NULL, 'Switched to light theme'),
(6, '2025-11-29 12:34:32', 'INFO', 'admin_olivier', 'Theme Change', NULL, 'Switched to dark theme'),
(7, '2025-11-29 12:34:39', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to USER_MGMT'),
(8, '2025-11-29 12:34:43', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to CITIZEN'),
(9, '2025-11-29 12:34:44', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to OFFICER'),
(10, '2025-11-29 12:34:46', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to CASE'),
(11, '2025-11-29 12:34:47', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to DEPARTMENT'),
(12, '2025-11-29 12:34:48', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to LOGS'),
(13, '2025-11-29 12:34:49', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to SETTINGS'),
(14, '2025-11-29 12:34:51', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to CITIZEN'),
(15, '2025-11-29 12:34:53', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to LOGS'),
(16, '2025-11-29 12:35:09', 'INFO', 'admin_olivier', 'Profile Update', NULL, 'User updated their profile information'),
(17, '2025-11-29 12:35:13', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to HOME'),
(18, '2025-11-29 12:35:18', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to HOME'),
(19, '2025-11-29 12:36:43', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to LOGS'),
(20, '2025-11-29 12:36:44', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to OFFICER'),
(21, '2025-11-29 12:36:44', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to USER_MGMT'),
(22, '2025-11-29 12:36:45', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to HOME'),
(23, '2025-11-29 12:36:51', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to USER_MGMT'),
(24, '2025-11-29 12:36:52', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to HOME'),
(25, '2025-11-29 12:37:02', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to LOGS'),
(26, '2025-11-29 12:37:10', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to SETTINGS'),
(27, '2025-11-29 12:37:13', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to CITIZEN'),
(28, '2025-11-29 12:38:13', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to HOME'),
(29, '2025-11-29 12:38:43', 'INFO', 'admin_olivier', 'Theme Change', NULL, 'Switched to dark theme'),
(30, '2025-11-29 12:38:45', 'INFO', 'admin_olivier', 'Theme Change', NULL, 'Switched to light theme'),
(31, '2025-11-29 12:38:45', 'INFO', 'admin_olivier', 'Theme Change', NULL, 'Switched to dark theme'),
(32, '2025-11-29 12:38:46', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to USER_MGMT'),
(33, '2025-11-29 12:38:48', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to CITIZEN'),
(34, '2025-11-29 12:38:49', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to OFFICER'),
(35, '2025-11-29 12:38:49', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to CASE'),
(36, '2025-11-29 12:38:51', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to LOGS'),
(37, '2025-11-29 12:38:51', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to DEPARTMENT'),
(38, '2025-11-29 12:48:26', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to OFFICER'),
(39, '2025-11-29 12:48:26', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to OFFICER'),
(40, '2025-11-29 12:48:29', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to CITIZEN'),
(41, '2025-11-29 12:48:30', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to CASE'),
(42, '2025-11-29 12:48:33', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to DEPARTMENT'),
(43, '2025-11-29 12:48:49', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to LOGS'),
(44, '2025-11-29 12:48:50', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to DEPARTMENT'),
(45, '2025-11-29 12:48:53', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to CASE'),
(46, '2025-11-29 12:48:54', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to OFFICER'),
(47, '2025-11-29 12:53:11', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to OFFICER'),
(48, '2025-11-29 12:53:12', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to CASE'),
(49, '2025-11-29 12:53:14', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to DEPARTMENT'),
(50, '2025-11-29 12:53:14', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to LOGS'),
(51, '2025-11-29 12:53:18', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to CITIZEN'),
(52, '2025-11-29 12:54:42', 'INFO', 'admin_olivier', 'Theme Change', NULL, 'Switched to dark theme'),
(53, '2025-11-29 12:54:44', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to OFFICER'),
(54, '2025-11-29 12:54:46', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to USER_MGMT'),
(55, '2025-11-29 12:54:48', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to HOME'),
(56, '2025-11-29 12:54:49', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to LOGS'),
(57, '2025-11-29 12:54:50', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to CASE'),
(58, '2025-11-29 12:54:51', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to OFFICER'),
(59, '2025-11-29 12:54:53', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to SETTINGS'),
(60, '2025-11-29 12:54:53', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to LOGS'),
(61, '2025-11-29 12:54:54', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to DEPARTMENT'),
(62, '2025-11-29 12:54:56', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to CASE'),
(63, '2025-11-29 12:54:57', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to OFFICER'),
(64, '2025-11-29 12:54:58', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to USER_MGMT'),
(65, '2025-11-29 12:54:58', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to USER_MGMT'),
(66, '2025-11-29 12:54:59', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to HOME'),
(67, '2025-11-29 12:55:58', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to CITIZEN'),
(68, '2025-11-29 12:55:59', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to OFFICER'),
(69, '2025-11-29 12:57:03', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to CITIZEN'),
(70, '2025-11-29 12:57:03', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to OFFICER'),
(71, '2025-11-29 13:06:30', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to DEPARTMENT'),
(72, '2025-11-29 13:06:31', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to CASE'),
(73, '2025-11-29 13:06:31', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to OFFICER'),
(74, '2025-11-29 13:06:35', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to CITIZEN'),
(75, '2025-11-29 13:06:37', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to OFFICER'),
(76, '2025-11-29 13:06:38', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to USER_MGMT'),
(77, '2025-11-29 13:50:05', 'INFO', 'admin_olivier', 'Theme Change', NULL, 'Switched to dark theme'),
(78, '2025-11-29 13:50:06', 'INFO', 'admin_olivier', 'Theme Change', NULL, 'Switched to light theme'),
(79, '2025-11-29 13:50:07', 'INFO', 'admin_olivier', 'Theme Change', NULL, 'Switched to dark theme'),
(80, '2025-11-29 13:50:07', 'INFO', 'admin_olivier', 'Theme Change', NULL, 'Switched to light theme'),
(81, '2025-11-29 13:50:08', 'INFO', 'admin_olivier', 'Theme Change', NULL, 'Switched to dark theme'),
(82, '2025-11-29 13:50:15', 'INFO', 'admin_olivier', 'Theme Change', NULL, 'Switched to light theme'),
(83, '2025-11-29 13:50:24', 'INFO', 'admin_olivier', 'Theme Change', NULL, 'Switched to dark theme'),
(84, '2025-11-29 13:50:59', 'INFO', 'admin_olivier', 'Logout', NULL, 'User logged out of the system'),
(85, '2025-11-29 14:02:26', 'INFO', 'admin_olivier', 'Theme Change', NULL, 'Switched to dark theme'),
(86, '2025-11-29 14:02:26', 'INFO', 'admin_olivier', 'Theme Change', NULL, 'Switched to light theme'),
(87, '2025-11-29 14:02:54', 'INFO', 'admin_olivier', 'Theme Change', NULL, 'Switched to dark theme'),
(88, '2025-11-29 14:18:24', 'INFO', 'admin_olivier', 'Theme Change', NULL, 'Switched to dark theme'),
(89, '2025-11-29 14:22:22', 'INFO', 'admin_olivier', 'Theme Change', NULL, 'Switched to dark theme'),
(90, '2025-11-29 14:50:31', 'INFO', 'admin_olivier', 'Theme Change', NULL, 'Switched to dark theme'),
(91, '2025-11-29 14:54:05', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to CASE'),
(92, '2025-11-29 14:56:32', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to LOGS'),
(93, '2025-11-29 14:56:33', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to LOGS'),
(94, '2025-11-29 14:56:34', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to SETTINGS'),
(95, '2025-11-29 14:56:35', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to DEPARTMENT'),
(96, '2025-11-29 14:56:37', 'INFO', 'admin_olivier', 'Navigation', NULL, 'Navigated to DEPARTMENT'),
(97, '2025-11-29 14:56:39', 'INFO', 'admin_olivier', 'Theme Change', NULL, 'Switched to dark theme'),
(98, '2025-11-29 14:56:40', 'INFO', 'admin_olivier', 'Theme Change', NULL, 'Switched to light theme'),
(99, '2025-11-29 14:57:01', 'INFO', 'admin_olivier', 'Theme Change', NULL, 'Switched to dark theme'),
(100, '2025-11-29 15:08:05', 'INFO', 'admin_olivier', 'Logout', NULL, 'User logged out of the system'),
(101, '2025-11-29 15:53:23', 'INFO', 'admin_olivier', 'Logout', NULL, 'User logged out of the system'),
(102, '2025-11-29 16:02:12', 'INFO', 'admin_olivier', 'Logout', NULL, 'User logged out of the system');

-- --------------------------------------------------------

--
-- Table structure for table `useraccount`
--

DROP TABLE IF EXISTS `useraccount`;
CREATE TABLE IF NOT EXISTS `useraccount` (
  `UserID` bigint NOT NULL AUTO_INCREMENT,
  `Username` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `PasswordHash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Role` enum('Admin','Officer','Citizen') COLLATE utf8mb4_unicode_ci NOT NULL,
  `CitizenID` bigint DEFAULT NULL,
  `OfficerID` bigint DEFAULT NULL,
  `Email` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CreatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`UserID`),
  UNIQUE KEY `Username` (`Username`),
  KEY `fk_user_citizen` (`CitizenID`),
  KEY `fk_user_officer` (`OfficerID`),
  KEY `idx_user_username` (`Username`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `useraccount`
--

INSERT INTO `useraccount` (`UserID`, `Username`, `PasswordHash`, `Role`, `CitizenID`, `OfficerID`, `Email`, `CreatedAt`) VALUES
(1, 'admin_olivier', 'Admin@123', 'Admin', NULL, NULL, 'admin.olivier@govtech.gov.rw', '2025-10-16 10:19:23'),
(2, 'admin_jeanne', 'Admin@456', 'Admin', NULL, NULL, 'admin.jeanne@govtech.gov.rw', '2025-10-16 10:19:23'),
(3, 'officer_jean', 'Officer@123', 'Officer', NULL, 1, 'jean.mutware@govtech.gov.rw', '2025-10-16 10:19:23'),
(4, 'officer_alice', 'Officer@456', 'Officer', NULL, 2, 'alice.uwase@govtech.gov.rw', '2025-10-16 10:19:23'),
(5, 'citizen_bosco', 'Citizen@123', 'Citizen', 1, NULL, 'boscohabimana@gmail.com', '2025-10-16 10:19:23'),
(6, 'citizen_grace', 'Citizen@456', 'Citizen', 2, NULL, 'gracemukamana@gmail.com.com', '2025-10-16 10:19:23'),
(11, 'MANZI', 'MANZI1212', 'Citizen', NULL, NULL, 'manziolivier@gmail.com', '2025-10-22 10:38:37'),
(13, 'TUYIZERE Elie', 'Elie', 'Officer', NULL, NULL, 'TUYIZERE Elie@govtech.rw', '2025-10-24 14:56:22');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `document`
--
ALTER TABLE `document`
  ADD CONSTRAINT `fk_doc_case` FOREIGN KEY (`CaseID`) REFERENCES `casetable` (`CaseID`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_doc_citizen` FOREIGN KEY (`CitizenID`) REFERENCES `citizen` (`CitizenID`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_document_case` FOREIGN KEY (`CaseID`) REFERENCES `casetable` (`CaseID`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_document_citizen` FOREIGN KEY (`CitizenID`) REFERENCES `citizen` (`CitizenID`) ON DELETE SET NULL;

--
-- Constraints for table `officer`
--
ALTER TABLE `officer`
  ADD CONSTRAINT `fk_officer_department` FOREIGN KEY (`DepartmentID`) REFERENCES `department` (`DepartmentID`) ON DELETE CASCADE;

--
-- Constraints for table `officercase`
--
ALTER TABLE `officercase`
  ADD CONSTRAINT `fk_officercase_case` FOREIGN KEY (`CaseID`) REFERENCES `casetable` (`CaseID`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_officercase_officer` FOREIGN KEY (`OfficerID`) REFERENCES `officer` (`OfficerID`) ON DELETE CASCADE;

--
-- Constraints for table `servicerequest`
--
ALTER TABLE `servicerequest`
  ADD CONSTRAINT `fk_service_citizen` FOREIGN KEY (`CitizenID`) REFERENCES `citizen` (`CitizenID`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_service_department` FOREIGN KEY (`DepartmentID`) REFERENCES `department` (`DepartmentID`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_sr_citizen` FOREIGN KEY (`CitizenID`) REFERENCES `citizen` (`CitizenID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `useraccount`
--
ALTER TABLE `useraccount`
  ADD CONSTRAINT `fk_user_citizen` FOREIGN KEY (`CitizenID`) REFERENCES `citizen` (`CitizenID`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_user_officer` FOREIGN KEY (`OfficerID`) REFERENCES `officer` (`OfficerID`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
