$baseUrl = "http://localhost:8081"
$sess = New-Object Microsoft.PowerShell.Commands.WebRequestSession

# Login as admin
$loginPage = Invoke-WebRequest -Uri "$baseUrl/login" -WebSession $sess -UseBasicParsing
$csrf = ""
if ($loginPage.Content -match 'name="_csrf"\s+value="([^"]+)"') { $csrf = $matches[1] }
$body = @{ username = "admin"; password = "admin123" }
if ($csrf) { $body["_csrf"] = $csrf }
Invoke-WebRequest -Uri "$baseUrl/login" -Method POST -Body $body -WebSession $sess -UseBasicParsing | Out-Null

$listPage = Invoke-WebRequest -Uri "$baseUrl/policies" -WebSession $sess -UseBasicParsing
$listPage.Content | Out-File -FilePath "policies_list.html" -Encoding utf8

# Find all policy links or table rows
$matches = [regex]::Matches($listPage.Content, '<tr[^>]*>[\s\S]*?<\/tr>')
Write-Host "Found $($matches.Count) table rows in policies list."
foreach ($m in $matches) {
    $row = $m.Value
    if ($row -match 'href="/policies/(\d+)"[^>]*>([^<]+)</a>') {
        $id = $Matches[1]
        $polNum = $Matches[2].Trim()
        Write-Host "Policy ID: $id | Number: $polNum"
        if ($row -match '5,200' -or $row -match '26 Oct' -or $row -match '07 Nov' -or $row -match 'POL-1122') {
            Write-Host "  -> MATCH: $row"
        }
    }
}

