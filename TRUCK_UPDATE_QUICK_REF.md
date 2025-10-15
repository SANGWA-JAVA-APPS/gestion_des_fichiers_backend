# TRUCK DATE UPDATE - QUICK REFERENCE

## 📊 Summary
- **Database:** mgrport
- **Tables:** 3 (truck_parking_invoice, truck_paymenr, truck_exit)
- **Total Updates:** 203 records
- **CSV Source:** Updates csv v2.csv

---

## 🎯 What Gets Updated

| Table | Field(s) | Records | Source in CSV |
|-------|----------|---------|---------------|
| truck_parking_invoice | entry_time, get_out_time | 76 | CORRECT ENTRY DATE, CORRECT EXIT DATE |
| truck_paymenr | date_time (date only) | 71 | CORRECT EXIT DATE (date) |
| truck_exit | date_time | 56 | CORRECT EXIT DATE (full) |

---

## 🚀 Quick Start (3 Steps)

### 1️⃣ BACKUP (MANDATORY!)
```sql
USE mgrport;

-- Backup all 3 tables
CREATE TABLE truck_parking_invoice_backup_20251014 AS 
SELECT * FROM truck_parking_invoice WHERE id BETWEEN 1800 AND 2000;

CREATE TABLE truck_paymenr_backup_20251014 AS 
SELECT * FROM truck_paymenr WHERE id BETWEEN 1400 AND 1700;

CREATE TABLE truck_exit_backup_20251014 AS 
SELECT * FROM truck_exit WHERE id BETWEEN 1400 AND 1600;
```

### 2️⃣ EXECUTE UPDATES
```bash
mysql -u root -p mgrport < UPDATE_TRUCK_DATES.sql
```

### 3️⃣ VERIFY
```sql
-- Check sample records
SELECT id, entry_time, get_out_time FROM truck_parking_invoice WHERE id = 1874;
-- Should show: 2025-09-23 11:30:00.0 | 2025-09-23 13:09:00

SELECT id, date_time FROM truck_paymenr WHERE id = 1479;
-- Should show: 2025-09-23 [original time]

SELECT id, date_time FROM truck_exit WHERE id = 1448;
-- Should show: 2025-09-23 13:09:00
```

---

## ⚠️ Important Notes

### truck_paymenr Special Handling
❗ **Only DATE is updated, TIME is preserved**
```sql
-- BEFORE: 2025-09-24 14:19:58
-- AFTER:  2025-09-23 14:19:58
--         ↑ new date  ↑ same time
```

### Skipped Records
- **18 exit records** with XX/XXX (no exit ID)
- **2 payment records** with empty receipt ID

---

## 🔄 Rollback (If Needed)

```sql
-- Restore from backup
UPDATE truck_parking_invoice t1
JOIN truck_parking_invoice_backup_20251014 t2 ON t1.id = t2.id
SET t1.entry_time = t2.entry_time, t1.get_out_time = t2.get_out_time;

UPDATE truck_paymenr t1
JOIN truck_paymenr_backup_20251014 t2 ON t1.id = t2.id
SET t1.date_time = t2.date_time;

UPDATE truck_exit t1
JOIN truck_exit_backup_20251014 t2 ON t1.id = t2.id
SET t1.date_time = t2.date_time;
```

---

## 📁 Files Reference

| File | Purpose |
|------|---------|
| UPDATE_TRUCK_DATES.sql | Main update script (run this!) |
| VERIFY_TRUCK_UPDATES.sql | Before/after verification queries |
| TRUCK_UPDATE_SUMMARY.md | Full documentation |
| TRUCK_UPDATE_QUICK_REF.md | This quick reference |

---

## ✅ Success Checklist

- [ ] Backup tables created
- [ ] UPDATE_TRUCK_DATES.sql executed successfully
- [ ] Verification queries show "✓ CORRECT"
- [ ] Sample records checked manually
- [ ] Application tested and working

---

## 📞 Quick Help

**Error: "Data truncated"**
→ Check datetime format in query

**Error: "Record not found"**  
→ ID may not exist in database

**Wrong time on truck_paymenr**
→ Check if CONCAT with TIME() was used

**Need to undo changes**
→ Use rollback queries with backup tables

---

## 🔍 Quick Verification Query

```sql
-- Run this after update to check 3 sample records
SELECT 'INVOICE' as type, CAST(id AS CHAR) as id, 
       entry_time as field1, get_out_time as field2 
FROM truck_parking_invoice WHERE id IN (1874,1875,1878)
UNION ALL
SELECT 'PAYMENT', id, date_time, '' 
FROM truck_paymenr WHERE id IN (1479,1480,1592)
UNION ALL
SELECT 'EXIT', CAST(id AS CHAR), date_time, '' 
FROM truck_exit WHERE id IN (1448,1449,1539);
```

Expected: Dates should match CSV "CORRECT ENTRY DATE" and "CORRECT EXIT DATE"

---

**Last Updated:** October 14, 2025  
**Database:** mgrport  
**Status:** ✅ Ready to execute
