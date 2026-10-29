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
    $editPage = Invoke-WebRequest -Uri "$baseUrl/policies/$id/edit" -WebSession $sess -UseBasicParsing
    Write-Host "=== Policy $id Form Values ==="
    $pattern = '<input[^>]*name="([^"]+)"[^>]*value="([^"]*)"'
    foreach ($m in [regex]::Matches($editPage.Content, $pattern)) {
        Write-Host "  $($m.Groups[1].Value) = '$($m.Groups[2].Value)'"
    }
    if ($editPage.Content -match '<select[^>]*name="policyType"[^>]*>([\s\S]*?)<\/select>') {
        $select = $matches[1]
        if ($select -match 'value="([^"]+)"[^>]*selected') {
            Write-Host "  policyType (selected) = '$($matches[1])'"
        }
    }
    if ($editPage.Content -match '<select[^>]*name="coverageDurationUnit"[^>]*>([\s\S]*?)<\/select>') {
        $select = $matches[1]
        if ($select -match 'value="([^"]+)"[^>]*selected') {
            Write-Host "  coverageDurationUnit (selected) = '$($matches[1])'"
        }
    }
}

