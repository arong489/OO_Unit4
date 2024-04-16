$jarname = (Get-ChildItem -Filter "*.jar").Name
$tests = (Get-ChildItem -Path "ikun's test" -Filter "*stdin*").Name
if ($jarname -is [Array]) {
    $jarname = $jarname[0]
}
foreach ($test in $tests) {
    "test $test"
    Get-Content ".\ikun's test\$test" | java -jar $jarname | Out-File stdout.txt
    $stdans = $test.Replace("stdin", "out")
    $check = Compare-Object -ReferenceObject (Select-String -Path ".\stdout.txt" -Pattern "^\[" | Select-Object -Property Line) -DifferenceObject (Select-String -Path ".\ikun's test\$stdans" -Pattern "^\[" | Select-Object -Property Line)
    if ($null -ne $check) {
        break;
    }
}
if ($null -ne $check) {
    "/_\ something wrong happened /_\"
    code ".\ikun's test\$test"
    Write-Host "In the left, it is the sample, while in the right is your answer" -ForegroundColor:Red
    code -d ".\ikun's test\$stdans" ".\stdout.txt"
} else {
    "\^_^/ congratulation \^_^/"
}