$baseUrl = "http://localhost:8081"
$sess = New-Object Microsoft.PowerShell.Commands.WebRequestSession

# Login as admin
$loginPage = Invoke-WebRequest -Uri "$baseUrl/login" -WebSession $sess -UseBasicParsing
$csrf = ""
if ($loginPage.Content -match 'name="_csrf"\s+value="([^"]+)"') { $csrf = $matches[1] }
$body = @{ username = "admin"; password = "admin123" }
if ($csrf) { $body["_csrf"] = $csrf }
Invoke-WebRequest -Uri "$baseUrl/login" -Method POST -Body $body -WebSession $sess -UseBasicParsing | Out-Null

$ids = @(1, 2, 3, 34)
foreach ($id in $ids) {
    try {
        $p = Invoke-WebRequest -Uri "$baseUrl/policies/$id" -WebSession $sess -UseBasicParsing
        $p.Content | Out-File -FilePath "policy_$id.html" -Encoding utf8
        Write-Host "Fetched Policy $($id) length: $($p.Content.Length) bytes"
    } catch {
        Write-Host "Error fetching Policy $($id)"
    }
}
