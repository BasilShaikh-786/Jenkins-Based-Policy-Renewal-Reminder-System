$baseUrl = "http://localhost:8081"
$sess = New-Object Microsoft.PowerShell.Commands.WebRequestSession

# Login as admin
$loginPage = Invoke-WebRequest -Uri "$baseUrl/login" -WebSession $sess -UseBasicParsing
$csrf = ""
if ($loginPage.Content -match 'name="_csrf"\s+value="([^"]+)"') { $csrf = $matches[1] }
$body = @{ username = "admin"; password = "admin123" }
if ($csrf) { $body["_csrf"] = $csrf }
Invoke-WebRequest -Uri "$baseUrl/login" -Method POST -Body $body -WebSession $sess -UseBasicParsing | Out-Null

$auditPage = Invoke-WebRequest -Uri "$baseUrl/audit" -WebSession $sess -UseBasicParsing
$auditPage.Content | Out-File -FilePath "audit_logs.html" -Encoding utf8
Select-String -Path "audit_logs.html" -Pattern "POL-232", "sd", "232", "RENEW", "POL-1122" -Context 1, 2
