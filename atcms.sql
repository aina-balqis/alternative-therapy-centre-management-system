-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 24, 2025 at 11:36 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `atcms`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `admin_id` int(11) NOT NULL,
  `admin_fullname` varchar(100) NOT NULL,
  `admin_email` varchar(100) NOT NULL,
  `admin_password` varchar(255) NOT NULL,
  `admin_dob` date DEFAULT NULL,
  `admin_phonenum` int(11) NOT NULL,
  `register_passcode` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`admin_id`, `admin_fullname`, `admin_email`, `admin_password`, `admin_dob`, `admin_phonenum`, `register_passcode`) VALUES
(1, 'manager', 'atcms@gmail.com', 'password123', '1985-06-15', 1234567890, 'passcode123');

-- --------------------------------------------------------

--
-- Table structure for table `appointment`
--

CREATE TABLE `appointment` (
  `appointment_id` int(11) NOT NULL,
  `client_id` int(11) DEFAULT NULL,
  `therapist_id` int(11) DEFAULT NULL,
  `package_id` int(11) DEFAULT NULL,
  `appointment_date` date DEFAULT NULL,
  `appointment_time` time DEFAULT NULL,
  `appointment_status` varchar(50) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `appointment`
--

INSERT INTO `appointment` (`appointment_id`, `client_id`, `therapist_id`, `package_id`, `appointment_date`, `appointment_time`, `appointment_status`, `notes`, `created_at`) VALUES
(11, 37, 8, 14, '2025-06-19', '16:00:00', 'Cancelled', '', '2025-06-26 01:12:48'),
(13, 37, 9, 14, '2025-07-01', '09:00:00', 'Completed', '', '2025-06-29 09:02:04'),
(14, 37, 6, 14, '2025-06-29', '16:00:00', 'Completed', '', '2025-06-26 01:12:48'),
(17, 37, 8, 14, '2025-08-08', '12:30:00', 'Confirmed', '', '2025-06-26 01:12:48'),
(18, 37, 7, 14, '2025-07-04', '09:30:00', 'Refund Completed', '\n\nTherapist Reschedule Request Reason: emergency\n\nTherapist Reschedule Request Reason: test\n\nClient rejected reschedule request and requested refund. You will be contacted within 3 working days for refund process.\n\n[Refund Completed Wed Jul 02 11:37:29 MYT 2025]\nMethod: Bank Transfer\nNotes: mm', '2025-07-02 11:34:19'),
(19, 37, 2, 14, '2025-07-03', '09:00:00', 'Refund Completed', '\r\n\n\nTherapist Reschedule Request Reason: test\r\n\n\nClient rejected reschedule request and requested refund. You will be contacted within 3 working days for refund process.\n\n[Refund Completed Sun Jun 29 03:07:41 MYT 2025]\nMethod: Bank Transfer\nNotes: admin refund berjaya', '2025-06-28 01:22:31'),
(35, 67, 2, 13, '2025-07-02', '10:00:00', 'Reschedule Approved', 'No special notes.\n\nTherapist Reschedule Request Reason: testt', '2025-07-01 12:23:48'),
(36, 68, 6, 14, '2025-07-05', '09:00:00', 'Refund Completed', 'Client requested focus on back pain.\n\nTherapist Reschedule Request Reason: Public Holiday\n\nTherapist Reschedule Request Reason: testing\n\nClient rejected reschedule request\n\nTherapist Reschedule Request Reason: testing2\n\nClient rejected reschedule request and requested refund. You will be contacted within 3 working days for refund process.\n\n[Refund Completed Thu Jul 03 23:23:17 MYT 2025]\nMethod: Bank Transfer\nNotes: done', '2025-07-03 23:17:38'),
(37, 69, 7, 15, '2025-07-04', '14:00:00', 'Cancelled (Unpaid)', 'First-time client.', '2025-07-01 12:23:48'),
(38, 69, 8, 16, '2025-07-05', '15:30:00', 'Confirmed', 'Prefers female therapist.', '2025-07-01 12:23:48'),
(39, 70, 9, 17, '2025-06-30', '09:30:00', 'Completed', 'Follow-up session.', '2025-07-01 12:23:48'),
(40, 71, 2, 18, '2025-07-07', '13:00:00', 'Confirmed', 'Focus on shoulders.', '2025-07-01 12:23:48'),
(41, 72, 6, 19, '2025-07-08', '16:00:00', 'Reschedule Approved', 'Needs extra relaxation.\n\nTherapist Reschedule Request Reason: try', '2025-07-01 12:23:48'),
(42, 73, 7, 20, '2025-07-05', '12:00:00', 'Confirmed', 'Bring medical report.', '2025-07-01 12:23:48'),
(43, 74, 8, 15, '2025-06-30', '10:30:00', 'Completed', 'Satisfied last session.', '2025-07-01 12:23:48'),
(44, 75, 9, 13, '2025-07-11', '11:30:00', 'Confirmed', 'Requested hot stone therapy.', '2025-07-01 12:23:48'),
(50, 37, 9, 20, '2025-07-10', '15:30:00', 'Cancelled (Unpaid)', '', '2025-07-01 18:05:37'),
(52, 37, 6, 14, '2025-07-03', '13:30:00', 'Cancelled (Unpaid)', '', '2025-07-01 18:36:12'),
(53, 37, 6, 15, '2025-07-03', '13:00:00', 'Cancelled (Unpaid)', '', '2025-07-01 18:36:12'),
(54, 37, 6, 13, '2025-07-05', '16:30:00', 'Cancelled (Unpaid)', '', '2025-07-01 18:38:47'),
(55, 64, 2, 13, '2025-07-03', '15:30:00', 'Cancelled (Unpaid)', 'muscle Injury', '2025-07-02 10:55:18'),
(56, 80, 6, 13, '2025-07-03', '13:00:00', 'Cancelled (Unpaid)', '', '2025-07-02 11:23:07'),
(57, 80, 6, 13, '2025-07-05', '14:00:00', 'Cancelled (Unpaid)', '', '2025-07-03 22:39:12'),
(58, 37, 6, 13, '2025-07-17', '13:00:00', 'Cancelled (Unpaid)', '', '2025-07-15 23:21:41'),
(59, 37, 6, 13, '2025-07-19', '12:00:00', 'Cancelled (Unpaid)', '', '2025-07-16 21:10:29'),
(60, 37, 6, 13, '2025-08-02', '15:30:00', 'Reschedule Approved', '.', '2025-07-17 21:00:47');

-- --------------------------------------------------------

--
-- Table structure for table `client`
--

CREATE TABLE `client` (
  `client_ID` int(11) NOT NULL,
  `client_fullname` varchar(100) NOT NULL,
  `client_email` varchar(100) NOT NULL,
  `client_password` varchar(64) NOT NULL,
  `client_dob` date DEFAULT NULL,
  `client_phonenum` varchar(64) NOT NULL,
  `client_address` text NOT NULL,
  `client_state` varchar(50) NOT NULL,
  `client_district` varchar(50) NOT NULL,
  `client_postcode` varchar(64) NOT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `email_verified` tinyint(1) DEFAULT 0,
  `reset_token` varchar(255) DEFAULT NULL,
  `reset_token_expiry` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `client`
--

INSERT INTO `client` (`client_ID`, `client_fullname`, `client_email`, `client_password`, `client_dob`, `client_phonenum`, `client_address`, `client_state`, `client_district`, `client_postcode`, `gender`, `email_verified`, `reset_token`, `reset_token_expiry`) VALUES
(37, 'Aina Norhisham ', 'ainabalqis17@gmail.com', 'password123', '2002-01-17', '0194991490', 'No 44, Jalan Meranti Bunga 10', 'Selangor', 'Klang', '41050', 'Female', 1, NULL, NULL),
(40, 'Najihah Lizal', 'najihahlizal@gmail.com', 'Fattah123.', '2000-11-12', '0123223232', 'NO 11, LOT 2354, JALAN KERETAPI LAMA, KG SG SEMBILANG', 'Selangor', 'Kota Bharu', '45800', 'Male', 1, NULL, NULL),
(64, 'Adam', 'ainamitst@gmail.com', 'Password123.', '2000-10-10', '0194991497', 'No 44, Jalan Meranti Bunga 9', 'Selangor', 'Klang', '41050', 'Male', 1, NULL, NULL),
(67, 'Ali Bin Ahmad', 'ali.ahmad@gmail.com', 'password123', '1990-05-12', '0123456789', '123 Jalan Mawar', 'Selangor', 'Shah Alam', '40000', 'Male', 1, NULL, NULL),
(68, 'Siti Binti Abu', 'siti.abu@gmail.com', 'password123', '1992-07-20', '0123456790', '45 Jalan Melati', 'Kuala Lumpur', 'Cheras', '56000', 'Female', 1, NULL, NULL),
(69, 'Ahmad Bin Bakar', 'ahmad.bakar@gmail.com', 'password123', '1988-03-15', '0123456791', '67 Jalan Kenanga', 'Johor', 'Johor Bahru', '80000', 'Male', 1, NULL, NULL),
(70, 'Nurul Aini', 'nurul.aini@gmail.com', 'password123', '1995-12-05', '0123456792', '89 Jalan Teratai', 'Penang', 'Georgetown', '10000', 'Female', 1, NULL, NULL),
(71, 'Faridah', 'faridah@gmail.com', 'password123', '1993-08-18', '0123456793', '12 Jalan Anggerik', 'Perak', 'Ipoh', '30000', 'Female', 1, NULL, NULL),
(72, 'Zulkifli', 'zulkifli@gmail.com', 'password123', '1985-11-22', '0123456794', '34 Jalan Kemboja', 'Negeri Sembilan', 'Seremban', '70000', 'Male', 1, NULL, NULL),
(73, 'Aisyah', 'aisyah@gmail.com', 'password123', '1997-02-28', '0123456795', '56 Jalan Dahlia', 'Sabah', 'Kota Kinabalu', '88000', 'Female', 1, NULL, NULL),
(74, 'Hafiz', 'hafiz@gmail.com', 'password123', '1989-09-10', '0123456796', '78 Jalan Ixora', 'Sarawak', 'Kuching', '93000', 'Male', 1, NULL, NULL),
(75, 'Azlan', 'azlan@gmail.com', 'password123', '1994-04-07', '0123456797', '90 Jalan Mawar Putih', 'Kelantan', 'Kota Bharu', '15000', 'Male', 1, NULL, NULL),
(76, 'Maryam', 'maryam@gmail.com', 'password123', '1996-06-25', '0123456798', '101 Jalan Cempaka', 'Terengganu', 'Kuala Terengganu', '20000', 'Female', 1, NULL, NULL),
(80, 'Fatin Arisha', 'ainabalqiss@gmail.com', 'password123', '2000-10-10', '0194991497', 'No 44, Jalan Meranti Bunga 9', 'Selangor', 'Klang', '41050', 'Female', 1, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `email_verification`
--

CREATE TABLE `email_verification` (
  `id` int(11) NOT NULL,
  `client_email` varchar(255) NOT NULL,
  `token` varchar(255) NOT NULL,
  `expiry` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `feedback`
--

CREATE TABLE `feedback` (
  `feedback_id` int(11) NOT NULL,
  `appointment_id` int(11) NOT NULL,
  `client_id` int(11) NOT NULL,
  `therapist_id` int(11) NOT NULL,
  `package_id` int(11) NOT NULL,
  `rating` int(11) NOT NULL CHECK (`rating` between 1 and 5),
  `comment` text DEFAULT NULL,
  `feedback_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `therapist_reply` text DEFAULT NULL,
  `reply_date` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `feedback`
--

INSERT INTO `feedback` (`feedback_id`, `appointment_id`, `client_id`, `therapist_id`, `package_id`, `rating`, `comment`, `feedback_date`, `therapist_reply`, `reply_date`) VALUES
(4, 39, 37, 7, 14, 5, 'I found the therapy package to be very comprehensive. It covered different techniques that helped me with my muscle stiffness and overall wellness', '2025-06-24 01:26:01', 'Thank you for sharing your experience. We look forward to seeing you again and helping you continue your wellness journey', '2025-06-24 20:57:34'),
(5, 39, 70, 9, 17, 5, 'The therapy package was excellent and worth the price. I felt noticeable improvement after just a few sessions, and the therapist was very attentive to my needs', '2025-07-01 04:54:45', 'We truly appreciate your kind words and are glad that the package was helpful for your condition. Your satisfaction is our priority!', '2025-07-01 05:01:13'),
(6, 43, 74, 8, 15, 5, 'I highly recommend the therapy packages offered here. They provide a good variety of treatments, and I appreciated the flexibility to choose according to my condition', '2025-07-01 04:58:34', NULL, NULL),
(9, 14, 37, 6, 14, 5, 'Best treatment from the best therapist centre!!', '2025-07-17 13:23:33', 'Glad to hear that, thank you for your feedback!', '2025-07-17 14:11:04');

-- --------------------------------------------------------

--
-- Table structure for table `payment`
--

CREATE TABLE `payment` (
  `payment_id` int(11) NOT NULL,
  `appointment_id` int(11) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `payment_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `transaction_id` varchar(100) DEFAULT NULL,
  `payment_method` varchar(50) DEFAULT NULL,
  `payment_status` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `payment`
--

INSERT INTO `payment` (`payment_id`, `appointment_id`, `amount`, `payment_date`, `transaction_id`, `payment_method`, `payment_status`) VALUES
(7, 11, 200.00, '2025-07-01 04:38:13', 'TP2506212624304975', 'ToyyibPay', 'Paid'),
(8, 13, 200.00, '2025-07-01 04:38:54', 'TP2506214849646045', 'ToyyibPay', 'Paid'),
(9, 17, 200.00, '2025-07-01 04:39:30', 'TP2506211189597666', 'ToyyibPay', 'Paid'),
(10, 18, 200.00, '2025-07-01 04:40:07', 'TP2506211514871316', 'ToyyibPay', 'Paid'),
(11, 19, 200.00, '2025-07-01 04:40:48', 'TP2506252076610011', 'ToyyibPay', 'Paid'),
(12, 35, 120.00, '2025-07-01 04:33:39', 'TXN001', 'Toyyibpay', 'Paid'),
(13, 36, 200.00, '2025-07-01 04:33:39', 'TXN002', 'Toyyibpay', 'Paid'),
(14, 38, 70.00, '2025-07-01 04:33:39', 'TXN003', 'Toyyibpay', 'Paid'),
(15, 39, 130.00, '2025-07-01 04:33:39', 'TXN004', 'Toyyibpay', 'Paid'),
(16, 40, 200.00, '2025-07-01 04:33:39', 'TXN005', 'Toyyibpay', 'Paid'),
(17, 41, 250.00, '2025-07-01 04:33:39', 'TXN006', 'Toyyibpay', 'Paid'),
(18, 42, 350.00, '2025-07-01 04:33:39', 'TXN007', 'Toyyibpay', 'Paid'),
(19, 43, 180.00, '2025-07-01 04:33:39', 'TXN008', 'Toyyibpay', 'Paid'),
(20, 44, 120.00, '2025-07-01 04:33:39', 'TXN008', 'Toyyibpay', 'Paid'),
(21, 60, 1.00, '2025-07-17 12:41:40', 'TP2507172414280649', 'ToyyibPay', 'Paid');

-- --------------------------------------------------------

--
-- Table structure for table `refund_logs`
--

CREATE TABLE `refund_logs` (
  `log_id` int(11) NOT NULL,
  `appointment_id` int(11) NOT NULL,
  `refund_amount` decimal(10,2) NOT NULL,
  `refund_method` varchar(50) NOT NULL,
  `processed_by` varchar(50) NOT NULL,
  `processed_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `therapist`
--

CREATE TABLE `therapist` (
  `therapist_ID` int(11) NOT NULL,
  `therapist_email` varchar(100) NOT NULL,
  `therapist_password` varchar(255) NOT NULL,
  `therapist_dob` date DEFAULT NULL,
  `therapist_IC` varchar(20) NOT NULL,
  `therapist_fullname` varchar(100) NOT NULL,
  `therapist_phonenum` varchar(20) DEFAULT NULL,
  `therapist_address` text NOT NULL,
  `therapist_state` varchar(50) NOT NULL,
  `therapist_district` varchar(50) NOT NULL,
  `therapist_postcode` varchar(10) DEFAULT NULL,
  `therapist_specialization` varchar(100) NOT NULL,
  `gender` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `therapist`
--

INSERT INTO `therapist` (`therapist_ID`, `therapist_email`, `therapist_password`, `therapist_dob`, `therapist_IC`, `therapist_fullname`, `therapist_phonenum`, `therapist_address`, `therapist_state`, `therapist_district`, `therapist_postcode`, `therapist_specialization`, `gender`) VALUES
(2, 'amir@gmail.com', 'password123', '1990-05-12', '900512-05-1234', 'Ameer Faizul', '0123456789', 'No 12, Jalan Orked', 'Selangor', 'Shah Alam', '40100', 'Personalized Treatment Plans', 'Male'),
(6, 'nadia@gmail.com', 'password123', '1991-07-14', '910714-09-7777', 'Nadia Binti Zulkifli', '133334444', '56, Jalan Melor', 'Perak', 'Ipoh', '31400', 'Women\'s Health & Wellness', 'Female'),
(7, 'aisyah@gmail.com', 'password123', '1992-06-15', '920615-14-5678', 'Aisyah Binti Ahmad', '0111234567', 'No. 10, Jalan Kenanga', 'Selangor', 'Shah Alam', '40000', 'Sports Injury & Rehabilitation', 'Female'),
(8, 'nizam@gmail.com', 'password123', '1990-09-21', '900921-08-1234', 'Shahrul Nizam', '0123456789', '23, Lorong Impian', 'Kuala Lumpur', 'Cheras', '56100', 'Chiropatric Therapy', 'Male'),
(9, 'hud@example.com', 'password123', '1994-01-30', '940130-05-6789', 'Mohd Hud Bin Mohd', '0138887777', '57, Jalan Mawar Berduri', 'Johor', 'Johor Bahru', '80000', 'Stress & Relaxation Therapy', 'Male');

-- --------------------------------------------------------

--
-- Table structure for table `therapist_schedules`
--

CREATE TABLE `therapist_schedules` (
  `scheduleId` int(11) NOT NULL,
  `therapistId` int(11) NOT NULL,
  `dayOfWeek` enum('Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday') NOT NULL,
  `startTime` time NOT NULL,
  `endTime` time NOT NULL,
  `isActive` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `therapist_schedules`
--

INSERT INTO `therapist_schedules` (`scheduleId`, `therapistId`, `dayOfWeek`, `startTime`, `endTime`, `isActive`) VALUES
(2, 6, 'Wednesday', '14:00:00', '21:00:00', 1),
(3, 8, 'Tuesday', '11:00:00', '14:00:00', 1),
(8, 7, 'Wednesday', '09:00:00', '17:00:00', 1),
(10, 2, 'Monday', '09:00:00', '17:00:00', 1),
(11, 2, 'Tuesday', '09:00:00', '17:00:00', 1),
(12, 2, 'Wednesday', '09:00:00', '17:00:00', 1),
(13, 2, 'Thursday', '09:00:00', '17:00:00', 1),
(14, 2, 'Friday', '09:00:00', '17:00:00', 1),
(16, 6, 'Wednesday', '13:00:00', '21:00:00', 1),
(17, 6, 'Thursday', '13:00:00', '21:00:00', 1),
(18, 6, 'Friday', '13:00:00', '21:00:00', 1),
(19, 6, 'Saturday', '09:00:00', '17:00:00', 1),
(20, 7, 'Monday', '08:00:00', '16:00:00', 1),
(21, 7, 'Tuesday', '08:00:00', '16:00:00', 1),
(22, 7, 'Wednesday', '08:00:00', '16:00:00', 1),
(23, 7, 'Friday', '08:00:00', '16:00:00', 1),
(24, 7, 'Saturday', '08:00:00', '12:00:00', 1),
(25, 8, 'Monday', '10:00:00', '18:00:00', 1),
(26, 8, 'Tuesday', '12:00:00', '20:00:00', 1),
(27, 8, 'Thursday', '10:00:00', '18:00:00', 1),
(28, 8, 'Friday', '12:00:00', '20:00:00', 1),
(29, 8, 'Saturday', '09:00:00', '13:00:00', 1),
(30, 9, 'Monday', '08:30:00', '17:30:00', 1),
(31, 9, 'Tuesday', '08:30:00', '17:30:00', 1),
(32, 9, 'Wednesday', '08:30:00', '17:30:00', 1),
(33, 9, 'Thursday', '08:30:00', '17:30:00', 1),
(34, 9, 'Friday', '08:30:00', '17:30:00', 1),
(35, 9, 'Saturday', '09:00:00', '13:00:00', 1),
(37, 8, 'Sunday', '21:00:00', '11:35:00', 1),
(38, 2, 'Saturday', '15:30:00', '21:20:00', 1);

-- --------------------------------------------------------

--
-- Table structure for table `therapy_package`
--

CREATE TABLE `therapy_package` (
  `package_ID` int(11) NOT NULL,
  `package_name` varchar(100) NOT NULL,
  `package_description` text DEFAULT NULL,
  `package_price` decimal(10,2) NOT NULL,
  `package_duration` int(11) NOT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `image_url` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `therapy_package`
--

INSERT INTO `therapy_package` (`package_ID`, `package_name`, `package_description`, `package_price`, `package_duration`, `is_active`, `image_url`) VALUES
(13, 'Sauna Therapy', 'Enjoy a 40-minute sauna session designed to deeply relax your muscles, flush out toxins, improve blood circulation, reduce stress, and leave your skin glowing. Perfect for recharging your body and mind..', 1.00, 40, 1, 'uploads/unnamed_74aa2d4469.png'),
(14, 'Reflexology + Sauna', 'Start with a relaxing reflexology massage to relieve tension and improve overall body function, then continue with a calming sauna session to sweat out toxins and leave you feeling light, recharged, and ready to take on the day!', 200.00, 90, 1, 'uploads/reflexology and sauna.webp'),
(15, 'Acupuncture Balancing Therapy', 'Experience a gentle acupuncture session that helps rebalance your energy, ease body aches, and calm your mind ÃÂÃÂ¢ÃÂÃÂÃÂÃÂ so you leave feeling lighter, happier, and more in tune with yourself', 180.00, 60, 1, 'uploads/acupuncture.jpg'),
(16, 'Foot Reflexology', 'A specialized foot massage focusing on key pressure points to improve blood circulation, relieve muscle tension, and support the healthy function of internal organs. Perfect for restoring overall balance and leaving you feeling light and rejuvenated', 70.00, 45, 1, 'uploads/foot-reflexology.webp'),
(17, 'Foot & Hand Reflexology', 'A unique combination of foot and hand reflexology techniques to deeply relax the body, improve circulation, and restore overall balance. This dual approach helps release tension, stimulate internal organs, and leave you feeling fully recharged from head to toe.', 130.00, 60, 1, 'uploads/foothand-refloxology.jpg'),
(18, 'Full Body Traditional Massage', 'Treat yourself to a full body traditional massage that melts away aches and tension, helps you sleep better, improves circulation, and leaves you feeling lighter and more energized from head to toe', 200.00, 90, 1, 'uploads/Traditional-Full-Body-1.jpg'),
(19, 'Cupping Therapy + Full Body Traditional Massage', 'Enjoy a healing combination of full body massage and cupping therapy that helps release stubborn muscle knots, improve breathing, boost circulation, and flush out toxins ÃÂÃÂ¢ÃÂÃÂÃÂÃÂ so you leave feeling totally refreshed, lighter, and more energized', 250.00, 120, 1, 'uploads/Cupping.jpg'),
(20, 'Complete Wellness Package', 'all-in-one premium treatment including cupping, full body massage, acupuncture, sauna session, and reflexology for total body rejuvenation', 350.00, 180, 1, 'uploads/spa.jpg'),
(23, ' Postpartum Recovery Package', 'A tailored treatment package for mothers, including full body massage, gentle abdominal binding, herbal compress, and sauna therapy to promote healing, improve circulation, reduce water retention, and restore hormonal balance after childbirth', 350.00, 120, 1, 'uploads/postpartum.webp'),
(24, 'Hot Stone Massage', 'A deeply relaxing massage using heated stones to melt away muscle tension, improve blood circulation, and promote a deep sense of calm and balance. Helps relieve chronic pain, reduce stress, improve sleep, and support detoxification', 220.00, 90, 1, 'uploads/Definition-Of-Hot-Stone-Massage.jpg'),
(25, 'Chiropractic Spinal Adjustment', 'A specialized manual adjustment therapy focused on realigning the spine to improve posture, relieve back and neck pain, and promote optimal nerve function. Ideal for clients with chronic back issues, poor posture, or tension-related headaches.', 180.00, 45, 1, 'uploads/tulang belakang 2.jpg'),
(29, 'Muscle Recovery Treatment', 'Perfect for muscle injury for certain part of body', 50.00, 60, 1, 'uploads/Muscle-recovery-.webp');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`admin_id`),
  ADD UNIQUE KEY `admin_email` (`admin_email`);

--
-- Indexes for table `appointment`
--
ALTER TABLE `appointment`
  ADD PRIMARY KEY (`appointment_id`),
  ADD KEY `client_id` (`client_id`),
  ADD KEY `therapist_id` (`therapist_id`),
  ADD KEY `package_id` (`package_id`);

--
-- Indexes for table `client`
--
ALTER TABLE `client`
  ADD PRIMARY KEY (`client_ID`),
  ADD UNIQUE KEY `client_email` (`client_email`);

--
-- Indexes for table `email_verification`
--
ALTER TABLE `email_verification`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `feedback`
--
ALTER TABLE `feedback`
  ADD PRIMARY KEY (`feedback_id`),
  ADD KEY `appointment_id` (`appointment_id`),
  ADD KEY `client_id` (`client_id`),
  ADD KEY `therapist_id` (`therapist_id`),
  ADD KEY `package_id` (`package_id`);

--
-- Indexes for table `payment`
--
ALTER TABLE `payment`
  ADD PRIMARY KEY (`payment_id`),
  ADD KEY `appointment_id` (`appointment_id`);

--
-- Indexes for table `refund_logs`
--
ALTER TABLE `refund_logs`
  ADD PRIMARY KEY (`log_id`),
  ADD KEY `appointment_id` (`appointment_id`);

--
-- Indexes for table `therapist`
--
ALTER TABLE `therapist`
  ADD PRIMARY KEY (`therapist_ID`),
  ADD UNIQUE KEY `therapist_email` (`therapist_email`),
  ADD UNIQUE KEY `therapist_IC` (`therapist_IC`);

--
-- Indexes for table `therapist_schedules`
--
ALTER TABLE `therapist_schedules`
  ADD PRIMARY KEY (`scheduleId`),
  ADD KEY `therapistId` (`therapistId`);

--
-- Indexes for table `therapy_package`
--
ALTER TABLE `therapy_package`
  ADD PRIMARY KEY (`package_ID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admin`
--
ALTER TABLE `admin`
  MODIFY `admin_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `appointment`
--
ALTER TABLE `appointment`
  MODIFY `appointment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=61;

--
-- AUTO_INCREMENT for table `client`
--
ALTER TABLE `client`
  MODIFY `client_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=82;

--
-- AUTO_INCREMENT for table `email_verification`
--
ALTER TABLE `email_verification`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `feedback`
--
ALTER TABLE `feedback`
  MODIFY `feedback_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `payment`
--
ALTER TABLE `payment`
  MODIFY `payment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT for table `refund_logs`
--
ALTER TABLE `refund_logs`
  MODIFY `log_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `therapist`
--
ALTER TABLE `therapist`
  MODIFY `therapist_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `therapist_schedules`
--
ALTER TABLE `therapist_schedules`
  MODIFY `scheduleId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=39;

--
-- AUTO_INCREMENT for table `therapy_package`
--
ALTER TABLE `therapy_package`
  MODIFY `package_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `appointment`
--
ALTER TABLE `appointment`
  ADD CONSTRAINT `appointment_ibfk_1` FOREIGN KEY (`client_id`) REFERENCES `client` (`client_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `appointment_ibfk_2` FOREIGN KEY (`therapist_id`) REFERENCES `therapist` (`therapist_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `appointment_ibfk_3` FOREIGN KEY (`package_id`) REFERENCES `therapy_package` (`package_ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `feedback`
--
ALTER TABLE `feedback`
  ADD CONSTRAINT `feedback_ibfk_1` FOREIGN KEY (`appointment_id`) REFERENCES `appointment` (`appointment_id`),
  ADD CONSTRAINT `feedback_ibfk_2` FOREIGN KEY (`client_id`) REFERENCES `client` (`client_ID`),
  ADD CONSTRAINT `feedback_ibfk_3` FOREIGN KEY (`therapist_id`) REFERENCES `therapist` (`therapist_ID`),
  ADD CONSTRAINT `feedback_ibfk_4` FOREIGN KEY (`package_id`) REFERENCES `therapy_package` (`package_ID`);

--
-- Constraints for table `payment`
--
ALTER TABLE `payment`
  ADD CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`appointment_id`) REFERENCES `appointment` (`appointment_id`);

--
-- Constraints for table `refund_logs`
--
ALTER TABLE `refund_logs`
  ADD CONSTRAINT `refund_logs_ibfk_1` FOREIGN KEY (`appointment_id`) REFERENCES `appointment` (`appointment_id`);

--
-- Constraints for table `therapist_schedules`
--
ALTER TABLE `therapist_schedules`
  ADD CONSTRAINT `therapist_schedules_ibfk_1` FOREIGN KEY (`therapistId`) REFERENCES `therapist` (`therapist_ID`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
