-- ============================================================================
-- VERIFICATION QUERIES FOR TRUCK DATE UPDATES
-- Database: mgrport
-- Purpose: Verify the updates before and after execution
-- ============================================================================

USE mgrport;

-- ============================================================================
-- PART 1: VERIFY truck_parking_invoice UPDATES
-- ============================================================================

-- Sample check: View current vs. expected values
SELECT 
    id,
    entry_time as current_entry_time,
    get_out_time as current_get_out_time,
    'Should be: 2025-09-23 11:30:00.0 | 2025-09-23 13:09:00' as expected_values
FROM truck_parking_invoice 
WHERE id = 1874;

-- Count total records to update
SELECT COUNT(*) as total_invoice_records_to_update 
FROM truck_parking_invoice 
WHERE id IN (
    1874,1875,1878,1879,1880,1882,1886,1887,1888,1889,
    1890,1891,1892,1893,1895,1894,1896,1897,1898,1899,
    1900,1901,1903,1902,1904,1905,1906,1907,1908,1909,
    1910,1911,1912,1913,1914,1915,1916,1917,1918,1919,
    1920,1922,1921,1923,1924,1925,1926,1928,1929,1931,
    1932,1933,1934,1935,1936,1938,1937,1939,1940,1941,
    1942,1943,1944,1945,1962,1963,1965,1968,1969,1883,
    1997,1996,1998
);

-- View sample of records that will be updated
SELECT 
    CAST(id AS CHAR) as id, 
    entry_time, 
    get_out_time 
FROM truck_parking_invoice 
WHERE id IN (1874, 1875, 1878, 1879, 1880)
ORDER BY id;

-- ============================================================================
-- PART 2: VERIFY truck_paymenr UPDATES
-- ============================================================================

-- Count total records to update
SELECT COUNT(*) as total_payment_records_to_update 
FROM truck_paymenr 
WHERE id IN (
    1479,1480,1592,1594,1589,1588,1587,1585,1591,1586,
    1580,1582,1579,1578,1577,1576,1575,1574,1573,1572,
    1570,1571,1569,1568,1567,1566,1565,1564,1563,1561,
    1560,1559,1558,1557,1556,1555,1554,1553,1552,1550,
    1551,1548,1483,1484,1485,1489,1490,1492,1493,1494,
    1495,1496,1497,1499,1498,1500,1501,1502,1503,1504,
    1505,1506,1523,1524,1525,1528,1530,1590,1602,1601,
    1603
);

-- View sample of records that will be updated
SELECT 
    id, 
    date_time,
    DATE(date_time) as current_date,
    TIME(date_time) as current_time
FROM truck_paymenr 
WHERE id IN (1479, 1480, 1592, 1594, 1589)
ORDER BY id;

-- ============================================================================
-- PART 3: VERIFY truck_exit UPDATES
-- ============================================================================

-- Count total records to update
SELECT COUNT(*) as total_exit_records_to_update 
FROM truck_exit 
WHERE id IN (
    1448,1449,1539,1542,1538,1537,1536,1531,1533,1530,
    1529,1528,1527,1526,1525,1524,1522,1521,1520,1519,
    1518,1516,1517,1514,1451,1452,1453,1454,1456,1458,
    1459,1460,1461,1462,1463,1465,1464,1466,1467,1468,
    1469,1470,1471,1472,1489,1490,1491,1494,1496,1541,
    1549,1548,1550
);

-- View sample of records that will be updated
SELECT 
    CAST(id AS CHAR) as id, 
    date_time 
FROM truck_exit 
WHERE id IN (1448, 1449, 1539, 1542, 1538)
ORDER BY id;

-- ============================================================================
-- PART 4: CROSS-REFERENCE CHECK
-- ============================================================================

-- Check which truck_parking_invoice IDs from CSV exist in database
SELECT 
    'truck_parking_invoice' as table_name,
    COUNT(*) as records_exist
FROM truck_parking_invoice 
WHERE id IN (
    1874,1875,1878,1879,1880,1882,1886,1887,1888,1889,
    1890,1891,1892,1893,1895,1894,1896,1897,1898,1899,
    1900,1901,1903,1902,1904,1905,1906,1907,1908,1909,
    1910,1911,1912,1913,1914,1915,1916,1917,1918,1919,
    1920,1922,1921,1923,1924,1925,1926,1928,1929,1931,
    1932,1933,1934,1935,1936,1938,1937,1939,1940,1941,
    1942,1943,1944,1945,1962,1963,1965,1968,1969,1883,
    1997,1996,1998
);

-- Check which truck_paymenr IDs from CSV exist in database
SELECT 
    'truck_paymenr' as table_name,
    COUNT(*) as records_exist
FROM truck_paymenr 
WHERE id IN (
    1479,1480,1592,1594,1589,1588,1587,1585,1591,1586,
    1580,1582,1579,1578,1577,1576,1575,1574,1573,1572,
    1570,1571,1569,1568,1567,1566,1565,1564,1563,1561,
    1560,1559,1558,1557,1556,1555,1554,1553,1552,1550,
    1551,1548,1483,1484,1485,1489,1490,1492,1493,1494,
    1495,1496,1497,1499,1498,1500,1501,1502,1503,1504,
    1505,1506,1523,1524,1525,1528,1530,1590,1602,1601,
    1603
);

-- Check which truck_exit IDs from CSV exist in database
SELECT 
    'truck_exit' as table_name,
    COUNT(*) as records_exist
FROM truck_exit 
WHERE id IN (
    1448,1449,1539,1542,1538,1537,1536,1531,1533,1530,
    1529,1528,1527,1526,1525,1524,1522,1521,1520,1519,
    1518,1516,1517,1514,1451,1452,1453,1454,1456,1458,
    1459,1460,1461,1462,1463,1465,1464,1466,1467,1468,
    1469,1470,1471,1472,1489,1490,1491,1494,1496,1541,
    1549,1548,1550
);

-- ============================================================================
-- PART 5: POST-UPDATE VERIFICATION
-- Run these queries AFTER executing UPDATE_TRUCK_DATES.sql
-- ============================================================================

-- Verify specific updates for truck_parking_invoice
SELECT 
    CAST(id AS CHAR) as id,
    entry_time,
    get_out_time,
    CASE 
        WHEN id = 1874 AND entry_time = '2025-09-23 11:30:00.0' AND get_out_time = '2025-09-23 13:09:00' THEN '✓ CORRECT'
        WHEN id = 1875 AND entry_time = '2025-09-05 13:13:00.0' AND get_out_time = '2025-09-06 16:19:00' THEN '✓ CORRECT'
        WHEN id = 1878 AND entry_time = '2025-09-07 07:21:00.0' AND get_out_time = '2025-09-07 14:00:00' THEN '✓ CORRECT'
        ELSE '✗ NEEDS CHECK'
    END as verification_status
FROM truck_parking_invoice 
WHERE id IN (1874, 1875, 1878);

-- Verify date updates for truck_paymenr (check if date changed but time preserved)
SELECT 
    id,
    date_time,
    DATE(date_time) as date_part,
    TIME(date_time) as time_part,
    CASE 
        WHEN id = 1479 AND DATE(date_time) = '2025-09-23' THEN '✓ DATE CORRECT'
        WHEN id = 1480 AND DATE(date_time) = '2025-09-06' THEN '✓ DATE CORRECT'
        ELSE '✗ NEEDS CHECK'
    END as verification_status
FROM truck_paymenr 
WHERE id IN (1479, 1480);

-- Verify truck_exit updates
SELECT 
    CAST(id AS CHAR) as id,
    date_time,
    CASE 
        WHEN id = 1448 AND date_time = '2025-09-23 13:09:00' THEN '✓ CORRECT'
        WHEN id = 1449 AND date_time = '2025-09-06 16:19:00' THEN '✓ CORRECT'
        ELSE '✗ NEEDS CHECK'
    END as verification_status
FROM truck_exit 
WHERE id IN (1448, 1449);

-- ============================================================================
-- PART 6: ROLLBACK QUERIES (IF NEEDED)
-- IMPORTANT: Only use these if you need to restore original data
-- Make sure to backup the data BEFORE running the update script!
-- ============================================================================

-- Example: To backup data before updates
/*
CREATE TABLE truck_parking_invoice_backup AS 
SELECT * FROM truck_parking_invoice 
WHERE id IN (1874,1875,1878...);

CREATE TABLE truck_paymenr_backup AS 
SELECT * FROM truck_paymenr 
WHERE id IN (1479,1480...);

CREATE TABLE truck_exit_backup AS 
SELECT * FROM truck_exit 
WHERE id IN (1448,1449...);
*/

-- Example: To restore from backup
/*
UPDATE truck_parking_invoice t1
JOIN truck_parking_invoice_backup t2 ON t1.id = t2.id
SET t1.entry_time = t2.entry_time, 
    t1.get_out_time = t2.get_out_time;
*/

-- ============================================================================
-- SUMMARY STATISTICS
-- ============================================================================

-- Get statistics on date ranges
SELECT 
    'truck_parking_invoice' as table_name,
    MIN(DATE(get_out_time)) as earliest_date,
    MAX(DATE(get_out_time)) as latest_date,
    COUNT(*) as total_records
FROM truck_parking_invoice 
WHERE id IN (1874,1875,1878,1879,1880,1882,1886,1887,1888,1889);

SELECT 
    'truck_paymenr' as table_name,
    MIN(DATE(date_time)) as earliest_date,
    MAX(DATE(date_time)) as latest_date,
    COUNT(*) as total_records
FROM truck_paymenr 
WHERE id IN (1479,1480,1592,1594,1589);

SELECT 
    'truck_exit' as table_name,
    MIN(DATE(date_time)) as earliest_date,
    MAX(DATE(date_time)) as latest_date,
    COUNT(*) as total_records
FROM truck_exit 
WHERE id IN (1448,1449,1539,1542,1538);
