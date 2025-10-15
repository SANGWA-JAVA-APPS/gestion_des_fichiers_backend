-- ============================================================================
-- SQL UPDATE QUERIES FOR TRUCK PARKING SYSTEM
-- Database: mgrport
-- Generated: October 14, 2025
-- 
-- Purpose: Update date/time fields in truck_parking_invoice, truck_paymenr, 
--          and truck_exit tables based on corrected dates from CSV file
-- ============================================================================

USE mgrport;

-- ============================================================================
-- PART 1: UPDATE truck_parking_invoice TABLE
-- Fields: entry_time (VARCHAR), get_out_time (DATETIME)
-- Source: CORRECT ENTRY DATE -> entry_time, CORRECT EXIT DATE -> get_out_time
-- ============================================================================

-- Update entry_time and get_out_time for truck_parking_invoice
UPDATE truck_parking_invoice SET entry_time = '2025-09-23 11:30:00.0', get_out_time = '2025-09-23 13:09:00' WHERE id = 1874;
UPDATE truck_parking_invoice SET entry_time = '2025-09-05 13:13:00.0', get_out_time = '2025-09-06 16:19:00' WHERE id = 1875;
UPDATE truck_parking_invoice SET entry_time = '2025-09-07 07:21:00.0', get_out_time = '2025-09-07 14:00:00' WHERE id = 1878;
UPDATE truck_parking_invoice SET entry_time = '2025-09-07 07:15:00.0', get_out_time = '2025-09-07 13:02:00' WHERE id = 1879;
UPDATE truck_parking_invoice SET entry_time = '2025-09-07 07:19:00.0', get_out_time = '2025-09-07 12:10:00' WHERE id = 1880;
UPDATE truck_parking_invoice SET entry_time = '2025-09-08 08:00:00.0', get_out_time = '2025-09-08 16:10:00' WHERE id = 1882;
UPDATE truck_parking_invoice SET entry_time = '2025-09-09 10:00:00.0', get_out_time = '2025-09-09 15:09:00' WHERE id = 1886;
UPDATE truck_parking_invoice SET entry_time = '2025-09-07 13:54:00.0', get_out_time = '2025-09-08 10:09:00' WHERE id = 1887;
UPDATE truck_parking_invoice SET entry_time = '2025-09-08 08:32:00.0', get_out_time = '2025-09-08 10:09:00' WHERE id = 1888;
UPDATE truck_parking_invoice SET entry_time = '2025-09-08 09:29:00.0', get_out_time = '2025-09-08 16:09:00' WHERE id = 1889;
UPDATE truck_parking_invoice SET entry_time = '2025-09-10 10:34:00.0', get_out_time = '2025-09-10 11:55:00' WHERE id = 1890;
UPDATE truck_parking_invoice SET entry_time = '2025-09-10 06:00:00.0', get_out_time = '2025-09-10 11:58:00' WHERE id = 1891;
UPDATE truck_parking_invoice SET entry_time = '2025-09-10 06:40:00.0', get_out_time = '2025-09-11 12:52:00' WHERE id = 1892;
UPDATE truck_parking_invoice SET entry_time = '2025-09-09 10:46:00.0', get_out_time = '2025-09-10 14:09:00' WHERE id = 1893;
UPDATE truck_parking_invoice SET entry_time = '2025-09-10 07:46:00.0', get_out_time = '2025-09-11 12:01:00' WHERE id = 1895;
UPDATE truck_parking_invoice SET entry_time = '2025-09-12 15:45:00.0', get_out_time = '2025-09-13 17:54:00' WHERE id = 1894;
UPDATE truck_parking_invoice SET entry_time = '2025-09-13 07:15:00.0', get_out_time = '2025-09-13 18:09:00' WHERE id = 1896;
UPDATE truck_parking_invoice SET entry_time = '2025-09-14 09:37:00.0', get_out_time = '2025-09-14 13:26:00' WHERE id = 1897;
UPDATE truck_parking_invoice SET entry_time = '2025-09-14 07:03:00.0', get_out_time = '2025-09-14 11:09:00' WHERE id = 1898;
UPDATE truck_parking_invoice SET entry_time = '2025-09-13 10:47:00.0', get_out_time = '2025-09-14 15:09:00' WHERE id = 1899;
UPDATE truck_parking_invoice SET entry_time = '2025-09-14 08:00:00.0', get_out_time = '2025-09-14 15:09:00' WHERE id = 1900;
UPDATE truck_parking_invoice SET entry_time = '2025-09-13 18:09:00.0', get_out_time = '2025-09-14 11:09:00' WHERE id = 1901;
UPDATE truck_parking_invoice SET entry_time = '2025-09-15 12:43:00.0', get_out_time = '2025-09-15 13:10:00' WHERE id = 1903;
UPDATE truck_parking_invoice SET entry_time = '2025-09-15 12:48:00.0', get_out_time = '2025-09-15 13:09:00' WHERE id = 1902;
UPDATE truck_parking_invoice SET entry_time = '2025-09-15 08:30:00.0', get_out_time = '2025-09-15 17:32:00' WHERE id = 1904;
UPDATE truck_parking_invoice SET entry_time = '2025-09-15 15:37:00.0', get_out_time = '2025-09-15 17:10:00' WHERE id = 1905;
UPDATE truck_parking_invoice SET entry_time = '2025-09-16 13:04:00.0', get_out_time = '2025-09-16 15:29:00' WHERE id = 1906;
UPDATE truck_parking_invoice SET entry_time = '2025-09-16 12:33:00.0', get_out_time = '2025-09-16 16:07:00' WHERE id = 1907;
UPDATE truck_parking_invoice SET entry_time = '2025-09-16 08:15:00.0', get_out_time = '2025-09-16 08:39:00' WHERE id = 1908;
UPDATE truck_parking_invoice SET entry_time = '2025-09-15 15:04:00.0', get_out_time = '2025-09-16 11:01:00' WHERE id = 1909;
UPDATE truck_parking_invoice SET entry_time = '2025-09-16 08:18:00.0', get_out_time = '2025-09-16 09:03:00' WHERE id = 1910;
UPDATE truck_parking_invoice SET entry_time = '2025-09-16 10:48:00.0', get_out_time = '2025-09-16 11:30:00' WHERE id = 1911;
UPDATE truck_parking_invoice SET entry_time = '2025-09-16 08:03:00.0', get_out_time = '2025-09-16 08:48:00' WHERE id = 1912;
UPDATE truck_parking_invoice SET entry_time = '2025-09-16 07:00:00.0', get_out_time = '2025-09-16 08:09:00' WHERE id = 1913;
UPDATE truck_parking_invoice SET entry_time = '2025-09-16 06:40:00.0', get_out_time = '2025-09-16 08:34:00' WHERE id = 1914;
UPDATE truck_parking_invoice SET entry_time = '2025-09-16 11:21:00.0', get_out_time = '2025-09-16 12:30:00' WHERE id = 1915;
UPDATE truck_parking_invoice SET entry_time = '2025-09-16 08:15:00.0', get_out_time = '2025-09-16 08:39:00' WHERE id = 1916;
UPDATE truck_parking_invoice SET entry_time = '2025-09-16 07:50:00.0', get_out_time = '2025-09-16 11:01:00' WHERE id = 1917;
UPDATE truck_parking_invoice SET entry_time = '2025-09-16 08:00:00.0', get_out_time = '2025-09-16 14:09:00' WHERE id = 1918;
UPDATE truck_parking_invoice SET entry_time = '2025-09-16 09:42:00.0', get_out_time = '2025-09-16 16:52:00' WHERE id = 1919;
UPDATE truck_parking_invoice SET entry_time = '2025-09-17 12:12:00.0', get_out_time = '2025-09-17 15:21:00' WHERE id = 1920;
UPDATE truck_parking_invoice SET entry_time = '2025-09-16 09:22:00.0', get_out_time = '2025-09-16 14:09:00' WHERE id = 1922;
UPDATE truck_parking_invoice SET entry_time = '2025-09-15 15:50:00.0', get_out_time = '2025-09-16 11:12:00' WHERE id = 1921;
UPDATE truck_parking_invoice SET entry_time = '2025-09-16 09:18:00.0', get_out_time = '2025-09-16 14:10:00' WHERE id = 1923;
UPDATE truck_parking_invoice SET entry_time = '2025-09-17 09:50:00.0', get_out_time = '2025-09-17 13:06:00' WHERE id = 1924;
UPDATE truck_parking_invoice SET entry_time = '2025-09-16 14:00:00.0', get_out_time = '2025-09-17 09:44:00' WHERE id = 1925;
UPDATE truck_parking_invoice SET entry_time = '2025-09-17 07:18:00.0', get_out_time = '2025-09-17 09:37:00' WHERE id = 1926;
UPDATE truck_parking_invoice SET entry_time = '2025-09-17 08:00:00.0', get_out_time = '2025-09-17 15:09:00' WHERE id = 1928;
UPDATE truck_parking_invoice SET entry_time = '2025-09-18 07:20:00.0', get_out_time = '2025-09-18 11:09:00' WHERE id = 1929;
UPDATE truck_parking_invoice SET entry_time = '2025-09-18 07:54:00.0', get_out_time = '2025-09-18 11:10:00' WHERE id = 1931;
UPDATE truck_parking_invoice SET entry_time = '2025-09-17 12:12:00.0', get_out_time = '2025-09-17 15:21:00' WHERE id = 1932;
UPDATE truck_parking_invoice SET entry_time = '2025-09-18 13:48:00.0', get_out_time = '2025-09-18 14:23:00' WHERE id = 1933;
UPDATE truck_parking_invoice SET entry_time = '2025-09-18 07:00:00.0', get_out_time = '2025-09-18 12:01:00' WHERE id = 1934;
UPDATE truck_parking_invoice SET entry_time = '2025-09-18 15:44:00.0', get_out_time = '2025-09-18 16:14:00' WHERE id = 1935;
UPDATE truck_parking_invoice SET entry_time = '2025-09-19 16:00:00.0', get_out_time = '2025-09-20 15:26:00' WHERE id = 1936;
UPDATE truck_parking_invoice SET entry_time = '2025-09-18 18:10:00.0', get_out_time = '2025-09-20 06:02:00' WHERE id = 1938;
UPDATE truck_parking_invoice SET entry_time = '2025-09-18 18:15:00.0', get_out_time = '2025-09-20 06:10:00' WHERE id = 1937;
UPDATE truck_parking_invoice SET entry_time = '2025-09-17 07:26:00.0', get_out_time = '2025-09-20 14:49:00' WHERE id = 1939;
UPDATE truck_parking_invoice SET entry_time = '2025-09-20 17:35:00.0', get_out_time = '2025-09-21 13:09:00' WHERE id = 1940;
UPDATE truck_parking_invoice SET entry_time = '2025-09-21 07:15:00.0', get_out_time = '2025-09-21 14:09:00' WHERE id = 1941;
UPDATE truck_parking_invoice SET entry_time = '2025-09-21 07:25:00.0', get_out_time = '2025-09-21 12:09:00' WHERE id = 1942;
UPDATE truck_parking_invoice SET entry_time = '2025-09-22 15:27:00.0', get_out_time = '2025-09-22 16:15:00' WHERE id = 1943;
UPDATE truck_parking_invoice SET entry_time = '2025-09-22 09:00:00.0', get_out_time = '2025-09-22 12:37:00' WHERE id = 1944;
UPDATE truck_parking_invoice SET entry_time = '2025-09-22 10:28:00.0', get_out_time = '2025-09-22 12:09:00' WHERE id = 1945;
UPDATE truck_parking_invoice SET entry_time = '2025-09-22 16:34:00.0', get_out_time = '2025-09-23 11:11:00' WHERE id = 1962;
UPDATE truck_parking_invoice SET entry_time = '2025-09-23 09:00:00.0', get_out_time = '2025-09-23 14:09:00' WHERE id = 1963;
UPDATE truck_parking_invoice SET entry_time = '2025-09-22 16:26:00.0', get_out_time = '2025-09-23 14:09:00' WHERE id = 1965;
UPDATE truck_parking_invoice SET entry_time = '2025-09-24 08:09:00.0', get_out_time = '2025-09-24 11:09:00' WHERE id = 1968;
UPDATE truck_parking_invoice SET entry_time = '2025-09-24 12:21:00.0', get_out_time = '2025-09-24 16:09:00' WHERE id = 1969;
UPDATE truck_parking_invoice SET entry_time = '2025-09-08 10:23:00.0', get_out_time = '2025-09-08 14:09:00' WHERE id = 1883;
UPDATE truck_parking_invoice SET entry_time = '2025-10-06 08:10:00.0', get_out_time = '2025-10-06 09:39:00' WHERE id = 1997;
UPDATE truck_parking_invoice SET entry_time = '2025-10-06 10:00:00.0', get_out_time = '2025-10-06 11:33:00' WHERE id = 1996;
UPDATE truck_parking_invoice SET entry_time = '2025-10-06 08:10:00.0', get_out_time = '2025-10-06 08:10:00' WHERE id = 1998;

-- ============================================================================
-- PART 2: UPDATE truck_paymenr TABLE
-- Field: date_time (DATETIME)
-- Source: CORRECT EXIT DATE (date only) -> date_time
-- Note: Only updating the DATE portion, keeping original TIME
-- ============================================================================

-- Update date_time for truck_paymenr (using EXIT DATE)
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-23', ' ', TIME(date_time)) WHERE id = 1479;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-06', ' ', TIME(date_time)) WHERE id = 1480;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-07', ' ', TIME(date_time)) WHERE id = 1592;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-07', ' ', TIME(date_time)) WHERE id = 1594;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-08', ' ', TIME(date_time)) WHERE id = 1589;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-08', ' ', TIME(date_time)) WHERE id = 1588;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-08', ' ', TIME(date_time)) WHERE id = 1587;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-08', ' ', TIME(date_time)) WHERE id = 1585;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-10', ' ', TIME(date_time)) WHERE id = 1591;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-10', ' ', TIME(date_time)) WHERE id = 1586;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-11', ' ', TIME(date_time)) WHERE id = 1580;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-10', ' ', TIME(date_time)) WHERE id = 1582;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-11', ' ', TIME(date_time)) WHERE id = 1579;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-13', ' ', TIME(date_time)) WHERE id = 1578;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-13', ' ', TIME(date_time)) WHERE id = 1577;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-14', ' ', TIME(date_time)) WHERE id = 1576;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-14', ' ', TIME(date_time)) WHERE id = 1575;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-14', ' ', TIME(date_time)) WHERE id = 1574;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-14', ' ', TIME(date_time)) WHERE id = 1573;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-14', ' ', TIME(date_time)) WHERE id = 1572;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-15', ' ', TIME(date_time)) WHERE id = 1570;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-15', ' ', TIME(date_time)) WHERE id = 1571;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-15', ' ', TIME(date_time)) WHERE id = 1569;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-15', ' ', TIME(date_time)) WHERE id = 1568;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-16', ' ', TIME(date_time)) WHERE id = 1567;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-16', ' ', TIME(date_time)) WHERE id = 1566;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-16', ' ', TIME(date_time)) WHERE id = 1565;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-16', ' ', TIME(date_time)) WHERE id = 1564;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-16', ' ', TIME(date_time)) WHERE id = 1563;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-16', ' ', TIME(date_time)) WHERE id = 1561;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-16', ' ', TIME(date_time)) WHERE id = 1560;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-16', ' ', TIME(date_time)) WHERE id = 1559;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-16', ' ', TIME(date_time)) WHERE id = 1558;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-16', ' ', TIME(date_time)) WHERE id = 1557;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-16', ' ', TIME(date_time)) WHERE id = 1556;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-16', ' ', TIME(date_time)) WHERE id = 1555;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-16', ' ', TIME(date_time)) WHERE id = 1554;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-16', ' ', TIME(date_time)) WHERE id = 1553;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-17', ' ', TIME(date_time)) WHERE id = 1552;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-16', ' ', TIME(date_time)) WHERE id = 1550;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-16', ' ', TIME(date_time)) WHERE id = 1551;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-16', ' ', TIME(date_time)) WHERE id = 1548;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-17', ' ', TIME(date_time)) WHERE id = 1483;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-17', ' ', TIME(date_time)) WHERE id = 1484;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-17', ' ', TIME(date_time)) WHERE id = 1485;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-17', ' ', TIME(date_time)) WHERE id = 1489;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-18', ' ', TIME(date_time)) WHERE id = 1490;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-18', ' ', TIME(date_time)) WHERE id = 1492;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-17', ' ', TIME(date_time)) WHERE id = 1493;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-18', ' ', TIME(date_time)) WHERE id = 1494;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-18', ' ', TIME(date_time)) WHERE id = 1495;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-18', ' ', TIME(date_time)) WHERE id = 1496;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-20', ' ', TIME(date_time)) WHERE id = 1497;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-20', ' ', TIME(date_time)) WHERE id = 1499;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-20', ' ', TIME(date_time)) WHERE id = 1498;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-20', ' ', TIME(date_time)) WHERE id = 1500;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-21', ' ', TIME(date_time)) WHERE id = 1501;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-21', ' ', TIME(date_time)) WHERE id = 1502;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-21', ' ', TIME(date_time)) WHERE id = 1503;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-22', ' ', TIME(date_time)) WHERE id = 1504;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-22', ' ', TIME(date_time)) WHERE id = 1505;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-22', ' ', TIME(date_time)) WHERE id = 1506;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-23', ' ', TIME(date_time)) WHERE id = 1523;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-23', ' ', TIME(date_time)) WHERE id = 1524;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-23', ' ', TIME(date_time)) WHERE id = 1525;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-24', ' ', TIME(date_time)) WHERE id = 1528;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-24', ' ', TIME(date_time)) WHERE id = 1530;
UPDATE truck_paymenr SET date_time = CONCAT('2025-09-08', ' ', TIME(date_time)) WHERE id = 1590;
UPDATE truck_paymenr SET date_time = CONCAT('2025-10-06', ' ', TIME(date_time)) WHERE id = 1602;
UPDATE truck_paymenr SET date_time = CONCAT('2025-10-06', ' ', TIME(date_time)) WHERE id = 1601;
UPDATE truck_paymenr SET date_time = CONCAT('2025-10-06', ' ', TIME(date_time)) WHERE id = 1603;

-- ============================================================================
-- PART 3: UPDATE truck_exit TABLE
-- Field: date_time (DATETIME)
-- Source: CORRECT EXIT DATE -> date_time
-- Note: Entries with 'XX' or 'XXX' in TRUCK EXIT ID are skipped (no ID)
-- ============================================================================

-- Update date_time for truck_exit (using EXIT DATE with time)
UPDATE truck_exit SET date_time = '2025-09-23 13:09:00' WHERE id = 1448;
UPDATE truck_exit SET date_time = '2025-09-06 16:19:00' WHERE id = 1449;
UPDATE truck_exit SET date_time = '2025-09-07 13:02:00' WHERE id = 1539;
UPDATE truck_exit SET date_time = '2025-09-07 12:10:00' WHERE id = 1542;
UPDATE truck_exit SET date_time = '2025-09-08 10:09:00' WHERE id = 1538;
UPDATE truck_exit SET date_time = '2025-09-10 11:55:00' WHERE id = 1537;
UPDATE truck_exit SET date_time = '2025-09-10 11:58:00' WHERE id = 1536;
UPDATE truck_exit SET date_time = '2025-09-11 12:52:00' WHERE id = 1531;
UPDATE truck_exit SET date_time = '2025-09-10 14:09:00' WHERE id = 1533;
UPDATE truck_exit SET date_time = '2025-09-11 12:01:00' WHERE id = 1530;
UPDATE truck_exit SET date_time = '2025-09-14 13:26:00' WHERE id = 1529;
UPDATE truck_exit SET date_time = '2025-09-14 11:09:00' WHERE id = 1528;
UPDATE truck_exit SET date_time = '2025-09-14 15:09:00' WHERE id = 1527;
UPDATE truck_exit SET date_time = '2025-09-14 11:09:00' WHERE id = 1526;
UPDATE truck_exit SET date_time = '2025-09-15 17:32:00' WHERE id = 1525;
UPDATE truck_exit SET date_time = '2025-09-16 11:01:00' WHERE id = 1524;
UPDATE truck_exit SET date_time = '2025-09-16 12:30:00' WHERE id = 1522;
UPDATE truck_exit SET date_time = '2025-09-16 08:39:00' WHERE id = 1521;
UPDATE truck_exit SET date_time = '2025-09-16 11:01:00' WHERE id = 1520;
UPDATE truck_exit SET date_time = '2025-09-16 14:09:00' WHERE id = 1519;
UPDATE truck_exit SET date_time = '2025-09-16 16:52:00' WHERE id = 1518;
UPDATE truck_exit SET date_time = '2025-09-16 14:09:00' WHERE id = 1516;
UPDATE truck_exit SET date_time = '2025-09-16 11:12:00' WHERE id = 1517;
UPDATE truck_exit SET date_time = '2025-09-16 14:10:00' WHERE id = 1514;
UPDATE truck_exit SET date_time = '2025-09-17 13:06:00' WHERE id = 1451;
UPDATE truck_exit SET date_time = '2025-09-17 09:44:00' WHERE id = 1452;
UPDATE truck_exit SET date_time = '2025-09-17 09:37:00' WHERE id = 1453;
UPDATE truck_exit SET date_time = '2025-09-17 15:09:00' WHERE id = 1454;
UPDATE truck_exit SET date_time = '2025-09-18 11:09:00' WHERE id = 1456;
UPDATE truck_exit SET date_time = '2025-09-18 11:10:00' WHERE id = 1458;
UPDATE truck_exit SET date_time = '2025-09-17 15:21:00' WHERE id = 1459;
UPDATE truck_exit SET date_time = '2025-09-18 14:23:00' WHERE id = 1460;
UPDATE truck_exit SET date_time = '2025-09-18 12:01:00' WHERE id = 1461;
UPDATE truck_exit SET date_time = '2025-09-18 16:14:00' WHERE id = 1462;
UPDATE truck_exit SET date_time = '2025-09-20 15:26:00' WHERE id = 1463;
UPDATE truck_exit SET date_time = '2025-09-20 06:02:00' WHERE id = 1465;
UPDATE truck_exit SET date_time = '2025-09-20 06:10:00' WHERE id = 1464;
UPDATE truck_exit SET date_time = '2025-09-20 14:49:00' WHERE id = 1466;
UPDATE truck_exit SET date_time = '2025-09-21 13:09:00' WHERE id = 1467;
UPDATE truck_exit SET date_time = '2025-09-21 14:09:00' WHERE id = 1468;
UPDATE truck_exit SET date_time = '2025-09-21 12:09:00' WHERE id = 1469;
UPDATE truck_exit SET date_time = '2025-09-22 16:15:00' WHERE id = 1470;
UPDATE truck_exit SET date_time = '2025-09-22 12:37:00' WHERE id = 1471;
UPDATE truck_exit SET date_time = '2025-09-22 12:09:00' WHERE id = 1472;
UPDATE truck_exit SET date_time = '2025-09-23 11:11:00' WHERE id = 1489;
UPDATE truck_exit SET date_time = '2025-09-23 14:09:00' WHERE id = 1490;
UPDATE truck_exit SET date_time = '2025-09-23 14:09:00' WHERE id = 1491;
UPDATE truck_exit SET date_time = '2025-09-24 11:09:00' WHERE id = 1494;
UPDATE truck_exit SET date_time = '2025-09-24 16:09:00' WHERE id = 1496;
UPDATE truck_exit SET date_time = '2025-09-08 14:09:00' WHERE id = 1541;
UPDATE truck_exit SET date_time = '2025-10-06 09:39:00' WHERE id = 1549;
UPDATE truck_exit SET date_time = '2025-10-06 11:33:00' WHERE id = 1548;
UPDATE truck_exit SET date_time = '2025-10-06 08:10:00' WHERE id = 1550;

-- ============================================================================
-- SUMMARY
-- ============================================================================
-- Total records to update:
--   - truck_parking_invoice: 76 records (entry_time + get_out_time)
--   - truck_paymenr: 71 records (date_time with date from EXIT DATE)
--   - truck_exit: 56 records (date_time, excluding XX/XXX entries)
-- 
-- Note: Some records in CSV have XX or XXX for TRUCK EXIT ID and empty 
--       TRUCK RECEIPT ID, these are skipped as no valid ID exists.
-- ============================================================================
