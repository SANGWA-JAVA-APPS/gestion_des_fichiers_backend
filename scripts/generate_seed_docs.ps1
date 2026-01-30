$base = "d:\Apache\DEV\SPRING BOOT\gestion_des_fichiers_backend\src\main\resources\seed-files"
New-Item -ItemType Directory -Force -Path $base | Out-Null

$docTypes = @{
  norme_loi = "Norme_Loi"
  accord_concession = "Accord_Concession"
  comm_asset_land = "Comm_Asset_Land"
  permi_construction = "Permi_Construction"
  estate = "Estate"
  cert_licenses = "Cert_Licenses"
  cargo_damage = "Cargo_Damage"
  comm_third_party = "Comm_Third_Party"
}

$exts = @("pdf", "docx", "xlsx")

$pdfTemplate = "%PDF-1.4`n1 0 obj`n<< /Type /Catalog /Pages 2 0 R >>`nendobj`n2 0 obj`n<< /Type /Pages /Kids [3 0 R] /Count 1 >>`nendobj`n3 0 obj`n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 300 144] /Contents 4 0 R >>`nendobj`n4 0 obj`n<< /Length 60 >>`nstream`nBT`n/F1 12 Tf`n72 100 Td`n({0}) Tj`nET`nendstream`nendobj`n5 0 obj`n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Name /F1 >>`nendobj`nxref`n0 6`n0000000000 65535 f `n0000000010 00000 n `n0000000060 00000 n `n0000000115 00000 n `n0000000205 00000 n `n0000000300 00000 n `ntrailer`n<< /Size 6 /Root 1 0 R >>`nstartxref`n380`n%%EOF`n"

function ContentType($ext) {
  switch ($ext) {
    "pdf" { return "application/pdf" }
    "docx" { return "application/vnd.openxmlformats-officedocument.wordprocessingml.document" }
    "xlsx" { return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" }
  }
}

$bases = @{
  norme_loi = @(100001, 200001)
  accord_concession = @(110001, 210001)
  comm_asset_land = @(120001, 220001)
  permi_construction = @(130001, 230001)
  estate = @(140001, 240001)
  cert_licenses = @(150001, 250001)
  cargo_damage = @(160001, 260001)
  comm_third_party = @(170001, 270001)
}

$sql = New-Object System.Text.StringBuilder
[void]$sql.AppendLine("")
[void]$sql.AppendLine("-- Seed accounts/sections for document file data")
[void]$sql.AppendLine("INSERT INTO account_categories (id, name, description) VALUES (1, 'Seed Category', 'Seed data category');")
[void]$sql.AppendLine("INSERT INTO accounts (id, username, password, email, full_name, phone_number, gender, active, created_at, updated_at, account_category_id)")
[void]$sql.AppendLine("VALUES (1, 'seed_user', 'seed_password', 'seed@example.com', 'Seed User', '0000000000', 'N/A', 1, NOW(), NOW(), 1);")
[void]$sql.AppendLine("INSERT INTO section_category (id, name, description, code, active, created_at, updated_at)")
[void]$sql.AppendLine("VALUES (1, 'Default Section', 'Seed section category', 'DEFAULT', 1, NOW(), NOW());")

foreach ($folder in $docTypes.Keys) {
  $prefix = $docTypes[$folder]
  $folderPath = Join-Path $base $folder
  New-Item -ItemType Directory -Force -Path $folderPath | Out-Null
  $docIdBase = $bases[$folder][0]
  $entityIdBase = $bases[$folder][1]

  [void]$sql.AppendLine("")
  [void]$sql.AppendLine("-- Seed $folder documents")

  for ($i = 1; $i -le 50; $i++) {
    $ext = $exts[($i - 1) % $exts.Count]
    $filename = "{0}_{1:d3}.{2}" -f $folder, $i, $ext
    $filePath = Join-Path $folderPath $filename

    if ($ext -eq "pdf") {
      $content = [string]::Format($pdfTemplate, "Seed $folder file {0:d3}" -f $i)
    } else {
      $content = "Seed $folder file {0:d3} ($ext)`n" -f $i
    }

    Set-Content -Path $filePath -Value $content -Encoding UTF8
    $size = (Get-Item $filePath).Length

    $docId = $docIdBase + ($i - 1)
    $entityId = $entityIdBase + ($i - 1)
    $originalName = "{0}_{1:d3}.{2}" -f $prefix, $i, $ext
    $fileRelPath = "seed-files/$folder/$filename"

    [void]$sql.AppendLine("INSERT INTO files (id, file_name, original_file_name, content_type, file_size, file_path, owner_id, created_at, updated_at, expiration_date, status, expiry_date, expiry_alert_sent, version, active) VALUES ($docId, '$filename', '$originalName', '$(ContentType $ext)', $size, '$fileRelPath', 1, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 5 YEAR), 'ACTIVE', NULL, 0, '1.0', 1);")

    switch ($folder) {
      "norme_loi" {
        [void]$sql.AppendLine("INSERT INTO norme_loi (id, date_time, doneby, doc_id, statut_id, reference, description, date_vigueur, domaine_application, active, created_at, updated_at) VALUES ($entityId, NOW(), 1, $docId, (SELECT id FROM docstatus WHERE name='Valid' LIMIT 1), 'NL-{0:d3}', 'Seed norme loi {0:d3}', NOW(), 'General', 1, NOW(), NOW());" -f $i)
      }
      "accord_concession" {
        [void]$sql.AppendLine("INSERT INTO accord_concession (id, date_time, doneby, doc_id, statut_id, contrat_concession, numero_accord, objet_concession, concessionnaire, duree_annees, conditions_financieres, emplacement, coordonnees_gps, rapport_transfert_gestion, date_debut_concession, date_fin_concession, section_category_id, active, created_at, updated_at) VALUES ($entityId, NOW(), 1, $docId, (SELECT id FROM docstatus WHERE name='Valid' LIMIT 1), 'Contrat {0:d3}', 'ACC-{0:d3}', 'Objet {0:d3}', 'Concessionnaire {0:d3}', 5, 'Conditions {0:d3}', 'Emplacement {0:d3}', '0,0', 'Rapport {0:d3}', NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), (SELECT id FROM section_category WHERE code='DEFAULT' LIMIT 1), 1, NOW(), NOW());" -f $i)
      }
      "comm_asset_land" {
        [void]$sql.AppendLine("INSERT INTO comm_asset_land (id, date_time, doneby, doc_id, statut_id, description, reference, date_obtention, coordonnees_gps, emplacement, section_id, active, created_at, updated_at) VALUES ($entityId, NOW(), 1, $docId, (SELECT id FROM docstatus WHERE name='Valid' LIMIT 1), 'Description {0:d3}', 'CAL-{0:d3}', NOW(), '0,0', 'Emplacement {0:d3}', (SELECT id FROM section_category WHERE code='DEFAULT' LIMIT 1), 1, NOW(), NOW());" -f $i)
      }
      "permi_construction" {
        [void]$sql.AppendLine("INSERT INTO permi_construction (id, date_time, doneby, doc_id, statut_id, numero_permis, projet, localisation, date_delivrance, date_expiration, autorite_delivrance, section_category_id, reference_titre_foncier, refe_permis_construire, date_validation, date_estimee_travaux, active, created_at, updated_at) VALUES ($entityId, NOW(), 1, $docId, (SELECT id FROM docstatus WHERE name='Valid' LIMIT 1), 'PC-{0:d3}', 'Projet {0:d3}', 'Localisation {0:d3}', NOW(), DATE_ADD(NOW(), INTERVAL 2 YEAR), 'Autorite {0:d3}', (SELECT id FROM section_category WHERE code='DEFAULT' LIMIT 1), 'TF-{0:d3}', 'RPC-{0:d3}', NOW(), NOW(), 1, NOW(), NOW());" -f $i)
      }
      "estate" {
        [void]$sql.AppendLine("INSERT INTO estate (id, date_time, doneby, doc_id, statut_id, reference, estate_type, emplacement, coordonnees_gps, date_of_building, comments, active, created_at, updated_at) VALUES ($entityId, NOW(), 1, $docId, (SELECT id FROM docstatus WHERE name='Valid' LIMIT 1), 'EST-{0:d3}', 'Type {0:d3}', 'Emplacement {0:d3}', '0,0', NOW(), 'Comments {0:d3}', 1, NOW(), NOW());" -f $i)
      }
      "cert_licenses" {
        [void]$sql.AppendLine("INSERT INTO cert_licenses (id, date_time, doneby, doc_id, statut_id, description, agent_certifica, numero_agent, date_certificate, duree_certificat, active, created_at, updated_at) VALUES ($entityId, NOW(), 1, $docId, (SELECT id FROM docstatus WHERE name='Valid' LIMIT 1), 'Cert {0:d3}', 'Agent {0:d3}', 'AG-{0:d3}', NOW(), 12, 1, NOW(), NOW());" -f $i)
      }
      "cargo_damage" {
        [void]$sql.AppendLine("INSERT INTO cargo_damage (id, date_time, doneby, doc_id, statut_id, refe_request, description, quotation_contract_num, date_request, date_contract, active, created_at, updated_at) VALUES ($entityId, NOW(), 1, $docId, (SELECT id FROM docstatus WHERE name='Valid' LIMIT 1), 'CD-{0:d3}', 'Desc {0:d3}', 'QC-{0:d3}', NOW(), NOW(), 1, NOW(), NOW());" -f $i)
      }
      "comm_third_party" {
        [void]$sql.AppendLine("INSERT INTO comm_third_party (id, date_time, doneby, doc_id, statut_id, name, location, validity, activities, section_id, active, created_at, updated_at) VALUES ($entityId, NOW(), 1, $docId, (SELECT id FROM docstatus WHERE name='Valid' LIMIT 1), 'ThirdParty {0:d3}', 'Location {0:d3}', '2026-12-31', 'Activities {0:d3}', (SELECT id FROM section_category WHERE code='DEFAULT' LIMIT 1), 1, NOW(), NOW());" -f $i)
      }
    }
  }
}

$sqlPath = "d:\Apache\DEV\SPRING BOOT\gestion_des_fichiers_backend\src\main\resources\seed-documents.sql"
$sql.ToString() | Set-Content -Path $sqlPath -Encoding UTF8
Write-Host "Generated SQL: $sqlPath"
Write-Host "Files created under seed-files/"
